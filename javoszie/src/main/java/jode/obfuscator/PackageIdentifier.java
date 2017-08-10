package jode.obfuscator;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import jode.GlobalOptions;
import jode.bytecode.ClassInfo;

public class PackageIdentifier
  extends Identifier
{
  ClassBundle bundle;
  PackageIdentifier parent;
  String name;
  String fullName;
  boolean loadOnDemand;
  Map loadedClasses;
  List swappedClasses;
  
  public PackageIdentifier(ClassBundle paramClassBundle, PackageIdentifier paramPackageIdentifier, String paramString1, String paramString2)
  {
    super(paramString2);
    this.bundle = paramClassBundle;
    this.parent = paramPackageIdentifier;
    this.fullName = paramString1;
    this.name = paramString2;
    this.loadedClasses = new HashMap();
  }
  
  protected void setSinglePreserved()
  {
    if (this.parent != null) {
      this.parent.setPreserved();
    }
  }
  
  public void setLoadOnDemand()
  {
    if (this.loadOnDemand) {
      return;
    }
    this.loadOnDemand = true;
    if ((Main.stripping & 0x1) == 0)
    {
      String str1 = this.fullName.length() > 0 ? this.fullName + "." : "";
      Enumeration localEnumeration = ClassInfo.getClassesAndPackages(getFullName());
      while (localEnumeration.hasMoreElements())
      {
        String str2 = ((String)localEnumeration.nextElement()).intern();
        if (!this.loadedClasses.containsKey(str2))
        {
          String str3 = (str1 + str2).intern();
          Object localObject;
          if (ClassInfo.isPackage(str3))
          {
            localObject = new PackageIdentifier(this.bundle, this, str3, str2);
            this.loadedClasses.put(str2, localObject);
            this.swappedClasses = null;
            ((PackageIdentifier)localObject).setLoadOnDemand();
          }
          else
          {
            localObject = new ClassIdentifier(this, str3, str2, ClassInfo.forName(str3));
            if (GlobalOptions.verboseLevel > 1) {
              GlobalOptions.err.println("preloading Class " + str3);
            }
            this.loadedClasses.put(str2, localObject);
            this.swappedClasses = null;
            this.bundle.addClassIdentifier((Identifier)localObject);
            ((ClassIdentifier)localObject).initClass();
          }
        }
      }
      this.loadOnDemand = false;
    }
  }
  
  public Identifier getIdentifier(String paramString)
  {
    if (this.loadOnDemand)
    {
      Identifier localIdentifier = loadClass(paramString);
      return localIdentifier;
    }
    int i = paramString.indexOf('.');
    if (i == -1) {
      return (Identifier)this.loadedClasses.get(paramString);
    }
    PackageIdentifier localPackageIdentifier = (PackageIdentifier)this.loadedClasses.get(paramString.substring(0, i));
    if (localPackageIdentifier != null) {
      return localPackageIdentifier.getIdentifier(paramString.substring(i + 1));
    }
    return null;
  }
  
  public Identifier loadClass(String paramString)
  {
    int i = paramString.indexOf('.');
    Object localObject3;
    if (i == -1)
    {
      localObject1 = (Identifier)this.loadedClasses.get(paramString);
      if (localObject1 == null)
      {
        localObject2 = this.fullName.length() > 0 ? this.fullName + "." + paramString : paramString;
        localObject2 = ((String)localObject2).intern();
        if (ClassInfo.isPackage((String)localObject2))
        {
          localObject3 = new PackageIdentifier(this.bundle, this, (String)localObject2, paramString);
          this.loadedClasses.put(paramString, localObject3);
          this.swappedClasses = null;
          ((PackageIdentifier)localObject3).setLoadOnDemand();
          localObject1 = localObject3;
        }
        else if (!ClassInfo.exists((String)localObject2))
        {
          GlobalOptions.err.println("Warning: Can't find class " + (String)localObject2);
          Thread.dumpStack();
        }
        else
        {
          localObject1 = new ClassIdentifier(this, (String)localObject2, paramString, ClassInfo.forName((String)localObject2));
          this.loadedClasses.put(paramString, localObject1);
          this.swappedClasses = null;
          this.bundle.addClassIdentifier((Identifier)localObject1);
          ((ClassIdentifier)localObject1).initClass();
        }
      }
      return (Identifier)localObject1;
    }
    Object localObject1 = paramString.substring(0, i);
    Object localObject2 = (PackageIdentifier)this.loadedClasses.get(localObject1);
    if (localObject2 == null)
    {
      localObject3 = this.fullName.length() > 0 ? this.fullName + "." + (String)localObject1 : localObject1;
      localObject3 = ((String)localObject3).intern();
      if (ClassInfo.isPackage((String)localObject3))
      {
        localObject2 = new PackageIdentifier(this.bundle, this, (String)localObject3, (String)localObject1);
        this.loadedClasses.put(localObject1, localObject2);
        this.swappedClasses = null;
        if (this.loadOnDemand) {
          ((PackageIdentifier)localObject2).setLoadOnDemand();
        }
      }
    }
    if (localObject2 != null) {
      return ((PackageIdentifier)localObject2).loadClass(paramString.substring(i + 1));
    }
    return null;
  }
  
  public void loadMatchingClasses(IdentifierMatcher paramIdentifierMatcher)
  {
    String str = paramIdentifierMatcher.getNextComponent(this);
    Object localObject1;
    Object localObject2;
    if (str != null)
    {
      localObject1 = (Identifier)this.loadedClasses.get(str);
      if (localObject1 == null)
      {
        str = str.intern();
        localObject2 = this.fullName.length() > 0 ? this.fullName + "." + str : str;
        localObject2 = ((String)localObject2).intern();
        if (ClassInfo.isPackage((String)localObject2))
        {
          localObject1 = new PackageIdentifier(this.bundle, this, (String)localObject2, str);
          this.loadedClasses.put(str, localObject1);
          this.swappedClasses = null;
          if (this.loadOnDemand) {
            ((PackageIdentifier)localObject1).setLoadOnDemand();
          }
        }
        else if (ClassInfo.exists((String)localObject2))
        {
          if (GlobalOptions.verboseLevel > 1) {
            GlobalOptions.err.println("loading Class " + (String)localObject2);
          }
          localObject1 = new ClassIdentifier(this, (String)localObject2, str, ClassInfo.forName((String)localObject2));
          if ((this.loadOnDemand) || (paramIdentifierMatcher.matches((Identifier)localObject1)))
          {
            this.loadedClasses.put(str, localObject1);
            this.swappedClasses = null;
            this.bundle.addClassIdentifier((Identifier)localObject1);
            ((ClassIdentifier)localObject1).initClass();
          }
        }
        else
        {
          GlobalOptions.err.println("Warning: Can't find class/package " + (String)localObject2);
        }
      }
      if ((localObject1 instanceof PackageIdentifier))
      {
        if (paramIdentifierMatcher.matches((Identifier)localObject1))
        {
          if (GlobalOptions.verboseLevel > 0) {
            GlobalOptions.err.println("loading Package " + ((Identifier)localObject1).getFullName());
          }
          ((PackageIdentifier)localObject1).setLoadOnDemand();
        }
        if (paramIdentifierMatcher.matchesSub((Identifier)localObject1, null)) {
          ((PackageIdentifier)localObject1).loadMatchingClasses(paramIdentifierMatcher);
        }
      }
    }
    else
    {
      localObject1 = this.fullName.length() > 0 ? this.fullName + "." : "";
      localObject2 = ClassInfo.getClassesAndPackages(getFullName());
      Object localObject5;
      while (((Enumeration)localObject2).hasMoreElements())
      {
        localObject3 = ((String)((Enumeration)localObject2).nextElement()).intern();
        if (!this.loadedClasses.containsKey(localObject3))
        {
          localObject4 = ((String)localObject1 + (String)localObject3).intern();
          if (paramIdentifierMatcher.matchesSub(this, (String)localObject3)) {
            if (ClassInfo.isPackage((String)localObject4))
            {
              if (GlobalOptions.verboseLevel > 0) {
                GlobalOptions.err.println("loading Package " + (String)localObject4);
              }
              localObject5 = new PackageIdentifier(this.bundle, this, (String)localObject4, (String)localObject3);
              this.loadedClasses.put(localObject3, localObject5);
              this.swappedClasses = null;
              if ((this.loadOnDemand) || (paramIdentifierMatcher.matches((Identifier)localObject5))) {
                ((PackageIdentifier)localObject5).setLoadOnDemand();
              }
            }
            else
            {
              localObject5 = new ClassIdentifier(this, (String)localObject4, (String)localObject3, ClassInfo.forName((String)localObject4));
              if ((this.loadOnDemand) || (paramIdentifierMatcher.matches((Identifier)localObject5)))
              {
                if (GlobalOptions.verboseLevel > 1) {
                  GlobalOptions.err.println("loading Class " + (String)localObject4);
                }
                this.loadedClasses.put(localObject3, localObject5);
                this.swappedClasses = null;
                this.bundle.addClassIdentifier((Identifier)localObject5);
                ((ClassIdentifier)localObject5).initClass();
              }
            }
          }
        }
      }
      Object localObject3 = new ArrayList();
      ((List)localObject3).addAll(this.loadedClasses.values());
      Object localObject4 = ((List)localObject3).iterator();
      while (((Iterator)localObject4).hasNext())
      {
        localObject5 = (Identifier)((Iterator)localObject4).next();
        if ((localObject5 instanceof PackageIdentifier))
        {
          if (paramIdentifierMatcher.matches((Identifier)localObject5)) {
            ((PackageIdentifier)localObject5).setLoadOnDemand();
          }
          if (paramIdentifierMatcher.matchesSub((Identifier)localObject5, null)) {
            ((PackageIdentifier)localObject5).loadMatchingClasses(paramIdentifierMatcher);
          }
        }
      }
    }
  }
  
  public void applyPreserveRule(IdentifierMatcher paramIdentifierMatcher)
  {
    if (this.loadOnDemand) {
      loadMatchingClasses(paramIdentifierMatcher);
    }
    super.applyPreserveRule(paramIdentifierMatcher);
  }
  
  public String getFullName()
  {
    return this.fullName;
  }
  
  public String getFullAlias()
  {
    if (this.parent != null)
    {
      String str1 = this.parent.getFullAlias();
      String str2 = getAlias();
      if (str2.length() == 0) {
        return str1;
      }
      if (str1.length() == 0) {
        return str2;
      }
      return str1 + "." + str2;
    }
    return "";
  }
  
  public void buildTable(Renamer paramRenamer)
  {
    this.loadOnDemand = false;
    super.buildTable(paramRenamer);
  }
  
  public void doTransformations()
  {
    Iterator localIterator = getChilds();
    while (localIterator.hasNext())
    {
      Identifier localIdentifier = (Identifier)localIterator.next();
      if ((localIdentifier instanceof ClassIdentifier)) {
        ((ClassIdentifier)localIdentifier).doTransformations();
      } else {
        ((PackageIdentifier)localIdentifier).doTransformations();
      }
    }
  }
  
  public void readTable(Map paramMap)
  {
    if (this.parent != null) {
      setAlias((String)paramMap.get(getFullName()));
    }
    Iterator localIterator = this.loadedClasses.values().iterator();
    while (localIterator.hasNext())
    {
      Identifier localIdentifier = (Identifier)localIterator.next();
      if (((Main.stripping & 0x1) == 0) || (localIdentifier.isReachable())) {
        localIdentifier.readTable(paramMap);
      }
    }
  }
  
  public Identifier getParent()
  {
    return this.parent;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getType()
  {
    return "package";
  }
  
  public Iterator getChilds()
  {
    if (this.swappedClasses == null)
    {
      this.swappedClasses = Arrays.asList(this.loadedClasses.values().toArray());
      Collections.shuffle(this.swappedClasses, Main.rand);
    }
    return this.swappedClasses.iterator();
  }
  
  public void storeClasses(ZipOutputStream paramZipOutputStream)
  {
    Iterator localIterator = getChilds();
    while (localIterator.hasNext())
    {
      Identifier localIdentifier = (Identifier)localIterator.next();
      if (((Main.stripping & 0x1) != 0) && (!localIdentifier.isReachable()))
      {
        if (GlobalOptions.verboseLevel > 4) {
          GlobalOptions.err.println("Class/Package " + localIdentifier.getFullName() + " is not reachable");
        }
      }
      else if ((localIdentifier instanceof PackageIdentifier)) {
        ((PackageIdentifier)localIdentifier).storeClasses(paramZipOutputStream);
      } else {
        try
        {
          String str = localIdentifier.getFullAlias().replace('.', '/') + ".class";
          paramZipOutputStream.putNextEntry(new ZipEntry(str));
          DataOutputStream localDataOutputStream = new DataOutputStream(new BufferedOutputStream(paramZipOutputStream));
          ((ClassIdentifier)localIdentifier).storeClass(localDataOutputStream);
          localDataOutputStream.flush();
          paramZipOutputStream.closeEntry();
        }
        catch (IOException localIOException)
        {
          GlobalOptions.err.println("Can't write Class " + localIdentifier.getName());
          localIOException.printStackTrace(GlobalOptions.err);
        }
      }
    }
  }
  
  public void storeClasses(File paramFile)
  {
    File localFile1 = this.parent == null ? paramFile : new File(paramFile, getAlias());
    if ((!localFile1.exists()) && (!localFile1.mkdir())) {
      GlobalOptions.err.println("Could not create directory " + localFile1.getPath() + ", check permissions.");
    }
    Iterator localIterator = getChilds();
    while (localIterator.hasNext())
    {
      Identifier localIdentifier = (Identifier)localIterator.next();
      if (((Main.stripping & 0x1) != 0) && (!localIdentifier.isReachable()))
      {
        if (GlobalOptions.verboseLevel > 4) {
          GlobalOptions.err.println("Class/Package " + localIdentifier.getFullName() + " is not reachable");
        }
      }
      else if ((localIdentifier instanceof PackageIdentifier)) {
        ((PackageIdentifier)localIdentifier).storeClasses(localFile1);
      } else {
        try
        {
          File localFile2 = new File(localFile1, localIdentifier.getAlias() + ".class");
          DataOutputStream localDataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(localFile2)));
          ((ClassIdentifier)localIdentifier).storeClass(localDataOutputStream);
          localDataOutputStream.close();
        }
        catch (IOException localIOException)
        {
          GlobalOptions.err.println("Can't write Class " + localIdentifier.getName());
          localIOException.printStackTrace(GlobalOptions.err);
        }
      }
    }
  }
  
  public String toString()
  {
    return this.parent == null ? "base package" : getFullName();
  }
  
  public boolean contains(String paramString, Identifier paramIdentifier)
  {
    Iterator localIterator = this.loadedClasses.values().iterator();
    while (localIterator.hasNext())
    {
      Identifier localIdentifier = (Identifier)localIterator.next();
      if (localIdentifier != paramIdentifier)
      {
        if ((((Main.stripping & 0x1) == 0) || (localIdentifier.isReachable())) && (localIdentifier.getAlias().equalsIgnoreCase(paramString))) {
          return true;
        }
        if (((localIdentifier instanceof PackageIdentifier)) && (localIdentifier.getAlias().length() == 0) && (((PackageIdentifier)localIdentifier).contains(paramString, this))) {
          return true;
        }
      }
    }
    return (getAlias().length() == 0) && (this.parent != null) && (this.parent != paramIdentifier) && (this.parent.contains(paramString, this));
  }
  
  public boolean conflicting(String paramString)
  {
    return this.parent.contains(paramString, this);
  }
}


