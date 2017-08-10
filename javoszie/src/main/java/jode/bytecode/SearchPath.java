package jode.bytecode;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import jode.GlobalOptions;

public class SearchPath
{
  public static final char altPathSeparatorChar = ',';
  URL[] bases;
  byte[][] urlzips;
  File[] dirs;
  ZipFile[] zips;
  String[] zipDirs;
  Hashtable[] zipEntries;
  
  private static void addEntry(Hashtable paramHashtable, String paramString)
  {
    String str = "";
    int i = paramString.lastIndexOf("/");
    if (i != -1)
    {
      str = paramString.substring(0, i);
      paramString = paramString.substring(i + 1);
    }
    Vector localVector = (Vector)paramHashtable.get(str);
    if (localVector == null)
    {
      localVector = new Vector();
      paramHashtable.put(str, localVector);
      if (str != "") {
        addEntry(paramHashtable, str);
      }
    }
    localVector.addElement(paramString);
  }
  
  private void fillZipEntries(int paramInt)
  {
    Enumeration localEnumeration = this.zips[paramInt].entries();
    this.zipEntries[paramInt] = new Hashtable();
    while (localEnumeration.hasMoreElements())
    {
      ZipEntry localZipEntry = (ZipEntry)localEnumeration.nextElement();
      String str = localZipEntry.getName();
      if (this.zipDirs[paramInt] != null)
      {
        if (str.startsWith(this.zipDirs[paramInt])) {
          str = str.substring(this.zipDirs[paramInt].length());
        }
      }
      else if ((!localZipEntry.isDirectory()) && (str.endsWith(".class"))) {
        addEntry(this.zipEntries[paramInt], str);
      }
    }
  }
  
  private void readURLZip(int paramInt, URLConnection paramURLConnection)
  {
    int i = paramURLConnection.getContentLength();
    if (i <= 0) {
      i = 10240;
    } else {
      i++;
    }
    this.urlzips[paramInt] = new byte[i];
    Object localObject;
    try
    {
      InputStream localInputStream = paramURLConnection.getInputStream();
      int j = 0;
      for (;;)
      {
        int k = Math.max(localInputStream.available(), 1);
        if (j + localInputStream.available() > this.urlzips[paramInt].length)
        {
          byte[] arrayOfByte = new byte[Math.max(2 * this.urlzips[paramInt].length, j + localInputStream.available())];
          System.arraycopy(this.urlzips[paramInt], 0, arrayOfByte, 0, j);
          this.urlzips[paramInt] = arrayOfByte;
        }
        int m = localInputStream.read(this.urlzips[paramInt], j, this.urlzips[paramInt].length - j);
        if (m == -1) {
          break;
        }
        j += m;
      }
      if (j < this.urlzips[paramInt].length)
      {
        localObject = new byte[j];
        System.arraycopy(this.urlzips[paramInt], 0, localObject, 0, j);
        this.urlzips[paramInt] = localObject;
      }
    }
    catch (IOException localIOException1)
    {
      GlobalOptions.err.println("IOException while reading remote zip file " + this.bases[paramInt]);
      this.bases[paramInt] = null;
      this.urlzips[paramInt] = null;
      return;
    }
    try
    {
      ZipInputStream localZipInputStream = new ZipInputStream(new ByteArrayInputStream(this.urlzips[paramInt]));
      this.zipEntries[paramInt] = new Hashtable();
      ZipEntry localZipEntry;
      while ((localZipEntry = localZipInputStream.getNextEntry()) != null)
      {
        localObject = localZipEntry.getName();
        if (this.zipDirs[paramInt] != null)
        {
          if (((String)localObject).startsWith(this.zipDirs[paramInt])) {
            localObject = ((String)localObject).substring(this.zipDirs[paramInt].length());
          }
        }
        else
        {
          if ((!localZipEntry.isDirectory()) && (((String)localObject).endsWith(".class"))) {
            addEntry(this.zipEntries[paramInt], (String)localObject);
          }
          localZipInputStream.closeEntry();
        }
      }
      localZipInputStream.close();
    }
    catch (IOException localIOException2)
    {
      GlobalOptions.err.println("Remote zip file " + this.bases[paramInt] + " is corrupted.");
      this.bases[paramInt] = null;
      this.urlzips[paramInt] = null;
      this.zipEntries[paramInt] = null;
      return;
    }
  }
  
  public SearchPath(String paramString)
  {
    int i = 1;
    int j = paramString.indexOf(File.pathSeparatorChar);
    while (j != -1)
    {
      j = paramString.indexOf(File.pathSeparatorChar, j + 1);
      i++;
    }
    if (File.pathSeparatorChar != ',')
    {
      j = paramString.indexOf(',');
      while (j != -1)
      {
        j = paramString.indexOf(',', j + 1);
        i++;
      }
    }
    this.bases = new URL[i];
    this.urlzips = new byte[i][];
    this.dirs = new File[i];
    this.zips = new ZipFile[i];
    this.zipEntries = new Hashtable[i];
    this.zipDirs = new String[i];
    j = 0;
    int k = 0;
    while (k < paramString.length())
    {
      for (int m = k; (m < paramString.length()) && (paramString.charAt(m) != File.pathSeparatorChar) && (paramString.charAt(m) != ','); m++) {}
      int n = k;
      while ((m > k) && (m < paramString.length()) && (paramString.charAt(m) == ':'))
      {
        while (n < m)
        {
          int i1 = paramString.charAt(n);
          if (((i1 < 65) || (i1 > 90)) && ((i1 < 97) || (i1 > 122)) && ((i1 < 48) || (i1 > 57)) && ("+-".indexOf(i1) == -1)) {
            break label328;
          }
          n++;
        }
        m++;
        n++;
        while ((m < paramString.length()) && (paramString.charAt(m) != File.pathSeparatorChar) && (paramString.charAt(m) != ',')) {
          m++;
        }
      }
      label328:
      String str = paramString.substring(k, m);
      k = m;
      int i2 = 0;
      if (str.startsWith("jar:"))
      {
        n = 0;
        do
        {
          n = str.indexOf('!', n);
        } while ((n != -1) && (n != str.length() - 1) && (str.charAt(n + 1) != '/'));
        if ((n == -1) || (n == str.length() - 1))
        {
          GlobalOptions.err.println("Warning: Illegal jar url " + str + ".");
        }
        else
        {
          this.zipDirs[j] = str.substring(n + 2);
          if (!this.zipDirs[j].endsWith("/")) {
            this.zipDirs[j] = (this.zipDirs[j] + "/");
          }
          str = str.substring(4, n);
          i2 = 1;
        }
      }
      else
      {
        n = str.indexOf(':');
        if ((n != -1) && (n < str.length() - 2) && (str.charAt(n + 1) == '/') && (str.charAt(n + 2) == '/')) {
          try
          {
            this.bases[j] = new URL(str);
            try
            {
              URLConnection localURLConnection = this.bases[j].openConnection();
              if ((i2 != 0) || (str.endsWith(".zip")) || (str.endsWith(".jar")) || (localURLConnection.getContentType().endsWith("/zip"))) {
                readURLZip(j, localURLConnection);
              }
            }
            catch (IOException localIOException1) {}catch (SecurityException localSecurityException1)
            {
              GlobalOptions.err.println("Warning: Security exception while accessing " + this.bases[j] + ".");
            }
          }
          catch (MalformedURLException localMalformedURLException)
          {
            this.bases[j] = null;
            this.dirs[j] = null;
          }
        } else {
          try
          {
            this.dirs[j] = new File(str);
            if ((i2 != 0) || (!this.dirs[j].isDirectory())) {
              try
              {
                this.zips[j] = new ZipFile(this.dirs[j]);
              }
              catch (IOException localIOException2)
              {
                this.dirs[j] = null;
              }
            }
          }
          catch (SecurityException localSecurityException2)
          {
            GlobalOptions.err.println("Warning: SecurityException while accessing " + str + ".");
            this.dirs[j] = null;
          }
        }
      }
      k++;
      j++;
    }
  }
  
  public boolean exists(String paramString)
  {
    String str = File.separatorChar != '/' ? paramString.replace('/', File.separatorChar) : paramString;
    for (int i = 0; i < this.dirs.length; i++)
    {
      Object localObject1;
      Object localObject3;
      if (this.zipEntries[i] != null)
      {
        if (this.zipEntries[i].get(paramString) != null) {
          return true;
        }
        localObject1 = "";
        localObject3 = paramString;
        int j = paramString.lastIndexOf('/');
        if (j >= 0)
        {
          localObject1 = paramString.substring(0, j);
          localObject3 = paramString.substring(j + 1);
        }
        Vector localVector = (Vector)this.zipEntries[i].get(localObject1);
        if ((localVector != null) && (localVector.contains(localObject3))) {
          return true;
        }
      }
      else if (this.bases[i] != null)
      {
        try
        {
          localObject1 = new URL(this.bases[i], paramString);
          localObject3 = ((URL)localObject1).openConnection();
          ((URLConnection)localObject3).connect();
          ((URLConnection)localObject3).getInputStream().close();
          return true;
        }
        catch (IOException localIOException) {}
      }
      else if (this.dirs[i] != null)
      {
        Object localObject2;
        if (this.zips[i] != null)
        {
          localObject2 = this.zipDirs[i] != null ? this.zipDirs[i] + paramString : paramString;
          localObject3 = this.zips[i].getEntry((String)localObject2);
          if (localObject3 != null) {
            return true;
          }
        }
        else
        {
          try
          {
            localObject2 = new File(this.dirs[i], str);
            if (((File)localObject2).exists()) {
              return true;
            }
          }
          catch (SecurityException localSecurityException) {}
        }
      }
    }
    return false;
  }
  
  public InputStream getFile(String paramString)
    throws IOException
  {
    String str1 = File.separatorChar != '/' ? paramString.replace('/', File.separatorChar) : paramString;
    for (int i = 0; i < this.dirs.length; i++)
    {
      Object localObject1;
      Object localObject3;
      if (this.urlzips[i] != null)
      {
        localObject1 = new ZipInputStream(new ByteArrayInputStream(this.urlzips[i]));
        String str2 = this.zipDirs[i] != null ? this.zipDirs[i] + paramString : paramString;
        while ((localObject3 = ((ZipInputStream)localObject1).getNextEntry()) != null)
        {
          if (((ZipEntry)localObject3).getName().equals(str2)) {
            return (InputStream)localObject1;
          }
          ((ZipInputStream)localObject1).closeEntry();
        }
      }
      if (this.bases[i] != null)
      {
        try
        {
          localObject1 = new URL(this.bases[i], paramString);
          localObject3 = ((URL)localObject1).openConnection();
          ((URLConnection)localObject3).setAllowUserInteraction(true);
          return ((URLConnection)localObject3).getInputStream();
        }
        catch (SecurityException localSecurityException1)
        {
          GlobalOptions.err.println("Warning: SecurityException while accessing " + this.bases[i] + paramString);
          localSecurityException1.printStackTrace(GlobalOptions.err);
        }
        catch (FileNotFoundException localFileNotFoundException) {}
      }
      else if (this.dirs[i] != null)
      {
        Object localObject2;
        if (this.zips[i] != null)
        {
          localObject2 = this.zipDirs[i] != null ? this.zipDirs[i] + paramString : paramString;
          localObject3 = this.zips[i].getEntry((String)localObject2);
          if (localObject3 != null) {
            return this.zips[i].getInputStream((ZipEntry)localObject3);
          }
        }
        else
        {
          try
          {
            localObject2 = new File(this.dirs[i], str1);
            if (((File)localObject2).exists()) {
              return new FileInputStream((File)localObject2);
            }
          }
          catch (SecurityException localSecurityException2)
          {
            GlobalOptions.err.println("Warning: SecurityException while accessing " + this.dirs[i] + str1);
          }
        }
      }
    }
    throw new FileNotFoundException(paramString);
  }
  
  public boolean isDirectory(String paramString)
  {
    String str = File.separatorChar != '/' ? paramString.replace('/', File.separatorChar) : paramString;
    for (int i = 0; i < this.dirs.length; i++) {
      if (this.dirs[i] != null)
      {
        if ((this.zips[i] != null) && (this.zipEntries[i] == null)) {
          fillZipEntries(i);
        }
        if (this.zipEntries[i] != null)
        {
          if (this.zipEntries[i].containsKey(paramString)) {
            return true;
          }
        }
        else {
          try
          {
            File localFile = new File(this.dirs[i], str);
            if (localFile.exists()) {
              return localFile.isDirectory();
            }
          }
          catch (SecurityException localSecurityException)
          {
            GlobalOptions.err.println("Warning: SecurityException while accessing " + this.dirs[i] + str);
          }
        }
      }
    }
    return false;
  }
  
  public Enumeration listFiles(final String paramString)
  {
    new Enumeration()
    {
      int pathNr;
      Enumeration zipEnum;
      int fileNr;
      String localDirName = File.separatorChar != '/' ? paramString.replace('/', File.separatorChar) : paramString;
      File currentDir;
      String[] files;
      String nextName;
      
      public String findNextFile()
      {
        for (;;)
        {
          if (this.zipEnum != null)
          {
            if (this.zipEnum.hasMoreElements()) {
              return (String)this.zipEnum.nextElement();
            }
            this.zipEnum = null;
          }
          Object localObject;
          if (this.files != null)
          {
            while (this.fileNr < this.files.length)
            {
              localObject = this.files[(this.fileNr++)];
              if (((String)localObject).endsWith(".class")) {
                return (String)localObject;
              }
              if (((String)localObject).indexOf(".") == -1)
              {
                File localFile = new File(this.currentDir, (String)localObject);
                if ((localFile.exists()) && (localFile.isDirectory())) {
                  return (String)localObject;
                }
              }
            }
            this.files = null;
          }
          if (this.pathNr == SearchPath.this.dirs.length) {
            return null;
          }
          if ((SearchPath.this.zips[this.pathNr] != null) && (SearchPath.this.zipEntries[this.pathNr] == null)) {
            SearchPath.this.fillZipEntries(this.pathNr);
          }
          if (SearchPath.this.zipEntries[this.pathNr] != null)
          {
            localObject = (Vector)SearchPath.this.zipEntries[this.pathNr].get(paramString);
            if (localObject != null) {
              this.zipEnum = ((Vector)localObject).elements();
            }
          }
          else if (SearchPath.this.dirs[this.pathNr] != null)
          {
            try
            {
              localObject = new File(SearchPath.this.dirs[this.pathNr], this.localDirName);
              if ((((File)localObject).exists()) && (((File)localObject).isDirectory()))
              {
                this.currentDir = ((File)localObject);
                this.files = ((File)localObject).list();
                this.fileNr = 0;
              }
            }
            catch (SecurityException localSecurityException)
            {
              GlobalOptions.err.println("Warning: SecurityException while accessing " + SearchPath.this.dirs[this.pathNr] + this.localDirName);
            }
          }
          this.pathNr += 1;
        }
      }
      
      public boolean hasMoreElements()
      {
        return (this.nextName != null) || ((this.nextName = findNextFile()) != null);
      }
      
      public Object nextElement()
      {
        if (this.nextName == null) {
          return findNextFile();
        }
        String str = this.nextName;
        this.nextName = null;
        return str;
      }
    };
  }
}


