package jode.decompiler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import jode.GlobalOptions;
import jode.bytecode.ClassInfo;
import jode.util.Option;
import jode.util.ProcessOptions;

public class Main
  extends Options
{
  private static int successCount = 0;
  private static Vector failedClasses;
  private static final int OPTION_START = 65536;
  private static final int OPTION_END = 131072;
  private static final Option[] longOptions = { new Option("cp", 1, 99), new Option("classpath", 1, 99), new Option("dest", 1, 100), new Option("help", 0, 104), new Option("version", 0, 86), new Option("verbose", 2, 118), new Option("debug", 2, 68), new Option("import", 1, 105), new Option("style", 1, 115), new Option("lvt", 2, 65536), new Option("inner", 2, 65537), new Option("anonymous", 2, 65538), new Option("push", 2, 65539), new Option("pretty", 2, 65540), new Option("decrypt", 2, 65541), new Option("onetime", 2, 65542), new Option("immediate", 2, 65543), new Option("verify", 2, 65544), new Option("contrafo", 2, 65545) };
  
  public static void usage()
  {
    PrintWriter localPrintWriter = GlobalOptions.err;
    localPrintWriter.println("Version: 1.1.2-pre1");
    localPrintWriter.println("Usage: java jode.decompiler.Main [OPTION]* {CLASS|JAR}*");
    localPrintWriter.println("Give a fully qualified CLASS name, e.g. jode.decompiler.Main, if you want to");
    localPrintWriter.println("decompile a single class, or a JAR file containing many classes.");
    localPrintWriter.println("OPTION is any of these:");
    localPrintWriter.println("  -h, --help           show this information.");
    localPrintWriter.println("  -V, --version        output version information and exit.");
    localPrintWriter.println("  -v, --verbose        be verbose (multiple times means more verbose).");
    localPrintWriter.println("  -c, --classpath <path> search for classes in specified classpath.");
    localPrintWriter.println("                       The directories should be separated by ','.");
    localPrintWriter.println("  -d, --dest <dir>     write decompiled files to disk into directory destdir.");
    localPrintWriter.println("  -s, --style {sun|gnu}  specify indentation style");
    localPrintWriter.println("  -i, --import <pkglimit>,<clslimit>");
    localPrintWriter.println("                       import classes used more than clslimit times");
    localPrintWriter.println("                       and packages with more then pkglimit used classes.");
    localPrintWriter.println("                       Limit 0 means never import. Default is 0,1.");
    localPrintWriter.println("  -D, --debug=...      use --debug=help for more information.");
    localPrintWriter.println("NOTE: The following options can be turned on or off with `yes' or `no'.");
    localPrintWriter.println("The options tagged with (default) are normally on.  Omitting the yes/no");
    localPrintWriter.println("argument will toggle the option, e.g. --verify is equivalent to --verify=no.");
    localPrintWriter.println("      --inner          decompile inner classes (default).");
    localPrintWriter.println("      --anonymous      decompile anonymous classes (default).");
    localPrintWriter.println("      --contrafo       transform constructors of inner classes (default).");
    localPrintWriter.println("      --lvt            use the local variable table (default).");
    localPrintWriter.println("      --pretty         use `pretty' names for local variables (default).");
    localPrintWriter.println("      --push           allow PUSH instructions in output.");
    localPrintWriter.println("      --decrypt        decrypt encrypted strings (default).");
    localPrintWriter.println("      --onetime        remove locals, that are used only one time.");
    localPrintWriter.println("      --immediate      output source immediately (may produce buggy code).");
    localPrintWriter.println("      --verify         verify code before decompiling it (default).");
  }
  
  public static boolean handleOption(int paramInt1, int paramInt2, String paramString)
  {
    if (paramString == null)
    {
      options ^= 1 << paramInt1;
    }
    else if (("yes".startsWith(paramString)) || (paramString.equals("on")))
    {
      options |= 1 << paramInt1;
    }
    else if (("no".startsWith(paramString)) || (paramString.equals("off")))
    {
      options &= (1 << paramInt1 ^ 0xFFFFFFFF);
    }
    else
    {
      GlobalOptions.err.println("jode.decompiler.Main: option --" + longOptions[paramInt2].getLongName() + " takes one of `yes', `no', `on', `off' as parameter");
      return false;
    }
    return true;
  }
  
  public static void decompileClass(String paramString1, ZipOutputStream paramZipOutputStream, String paramString2, TabbedPrintWriter paramTabbedPrintWriter, ImportHandler paramImportHandler)
  {
    try
    {
      ClassInfo localClassInfo;
      try
      {
        localClassInfo = ClassInfo.forName(paramString1);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        GlobalOptions.err.println("`" + paramString1 + "' is not a class name");
        return;
      }
      if (skipClass(localClassInfo)) {
        return;
      }
      String str = paramString1.replace('.', File.separatorChar) + ".java";
      if (paramZipOutputStream != null)
      {
        paramTabbedPrintWriter.flush();
        paramZipOutputStream.putNextEntry(new ZipEntry(str));
      }
      else if (paramString2 != null)
      {
        localObject = new File(paramString2, str);
        File localFile = new File(((File)localObject).getParent());
        if ((!localFile.exists()) && (!localFile.mkdirs())) {
          GlobalOptions.err.println("Could not create directory " + localFile.getPath() + ", check permissions.");
        }
        paramTabbedPrintWriter = new TabbedPrintWriter(new BufferedOutputStream(new FileOutputStream((File)localObject)), paramImportHandler, false);
      }
      GlobalOptions.err.println(paramString1);
      Object localObject = new ClassAnalyzer(localClassInfo, paramImportHandler);
      ((ClassAnalyzer)localObject).dumpJavaFile(paramTabbedPrintWriter);
      if (paramZipOutputStream != null)
      {
        paramTabbedPrintWriter.flush();
        paramZipOutputStream.closeEntry();
      }
      else if (paramString2 != null)
      {
        paramTabbedPrintWriter.close();
      }
      System.gc();
      successCount += 1;
    }
    catch (IOException localIOException)
    {
      failedClasses.addElement(paramString1);
      GlobalOptions.err.println("Can't write source of " + paramString1 + ".");
      GlobalOptions.err.println("Check the permissions.");
      localIOException.printStackTrace(GlobalOptions.err);
    }
    catch (Throwable localThrowable)
    {
      failedClasses.addElement(paramString1);
      GlobalOptions.err.println("Failed to decompile " + paramString1 + ".");
      localThrowable.printStackTrace(GlobalOptions.err);
    }
  }
  
  public static void main(String[] paramArrayOfString)
  {
    try
    {
      decompile(paramArrayOfString);
    }
    catch (ExceptionInInitializerError localExceptionInInitializerError)
    {
      localExceptionInInitializerError.getException().printStackTrace();
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    printSummary();
    System.exit(0);
  }
  
  private static void printSummary()
  {
    GlobalOptions.err.println();
    if (failedClasses.size() > 0)
    {
      GlobalOptions.err.println("Failed to decompile these classes:");
      Enumeration localEnumeration = failedClasses.elements();
      while (localEnumeration.hasMoreElements()) {
        GlobalOptions.err.println("\t" + localEnumeration.nextElement());
      }
      GlobalOptions.err.println("Failed to decompile " + failedClasses.size() + " classes.");
    }
    GlobalOptions.err.println("Decompiled " + successCount + " classes.");
  }
  
  public static void decompile(String[] paramArrayOfString)
  {
    if (paramArrayOfString.length == 0)
    {
      usage();
      return;
    }
    failedClasses = new Vector();
    String str1 = System.getProperty("java.class.path").replace(File.pathSeparatorChar, ',');
    String str2 = System.getProperty("sun.boot.class.path");
    if (str2 != null) {
      str1 = str1 + ',' + str2.replace(File.pathSeparatorChar, ',');
    }
    String str3 = null;
    int i = Integer.MAX_VALUE;
    int j = 1;
    GlobalOptions.err.println("Jode (c) 1998-2001 Jochen Hoenicke <jochen@gnu.org>");
    int k = 0;
    ProcessOptions localProcessOptions = new ProcessOptions(paramArrayOfString, longOptions);
    for (int m = localProcessOptions.getOption(); m != -1; m = localProcessOptions.getOption()) {
      switch (m)
      {
      case 0: 
        break;
      case 104: 
        usage();
        k = 1;
        break;
      case 86: 
        GlobalOptions.err.println("1.1.2-pre1");
        break;
      case 99: 
        str1 = localProcessOptions.getOptionArgument();
        break;
      case 100: 
        str3 = localProcessOptions.getOptionArgument();
        break;
      case 118: 
        localObject = localProcessOptions.getOptionArgument();
        if (localObject == null) {
          GlobalOptions.verboseLevel += 1;
        } else {
          try
          {
            GlobalOptions.verboseLevel = Integer.parseInt((String)localObject);
          }
          catch (NumberFormatException localNumberFormatException)
          {
            GlobalOptions.err.println("jode.decompiler.Main: Argument `" + (String)localObject + "' to --verbose must be numeric:");
            k = 1;
          }
        }
        break;
      case 68: 
        localObject = localProcessOptions.getOptionArgument();
        if (localObject == null) {
          localObject = "help";
        }
        k |= (!GlobalOptions.setDebugging((String)localObject) ? 1 : 0);
        break;
      case 115: 
        localObject = localProcessOptions.getOptionArgument();
        if ("sun".startsWith((String)localObject))
        {
          outputStyle = 20;
        }
        else if ("gnu".startsWith((String)localObject))
        {
          outputStyle = 66;
        }
        else if ("pascal".startsWith((String)localObject))
        {
          outputStyle = 36;
        }
        else
        {
          GlobalOptions.err.println("jode.decompiler.Main: Unknown style `" + (String)localObject + "'.");
          k = 1;
        }
        break;
      case 105: 
        localObject = localProcessOptions.getOptionArgument();
        int n = ((String)localObject).indexOf(',');
        try
        {
          int i1 = Integer.parseInt(((String)localObject).substring(0, n));
          if (i1 == 0) {
            i1 = Integer.MAX_VALUE;
          }
          if (i1 < 0) {
            throw new IllegalArgumentException();
          }
          int i3 = Integer.parseInt(((String)localObject).substring(n + 1));
          if (i3 == 0) {
            i3 = Integer.MAX_VALUE;
          }
          if (i3 < 0) {
            throw new IllegalArgumentException();
          }
          i = i1;
          j = i3;
        }
        catch (RuntimeException localRuntimeException)
        {
          GlobalOptions.err.println("jode.decompiler.Main: Invalid argument for -i option.");
          k = 1;
        }
      default: 
        if ((m >= 65536) && (m <= 131072)) {
          k |= (!handleOption(m - 65536, localProcessOptions.getPosition(), localProcessOptions.getOptionArgument()) ? 1 : 0);
        } else {
          k = 1;
        }
        break;
      }
    }
    if (k != 0) {
      return;
    }
    ClassInfo.setClassPath(str1);
    ImportHandler localImportHandler = new ImportHandler(i, j);
    Object localObject = null;
    TabbedPrintWriter localTabbedPrintWriter = null;
    if (str3 == null)
    {
      localTabbedPrintWriter = new TabbedPrintWriter(System.out, localImportHandler);
    }
    else if ((str3.toLowerCase().endsWith(".zip")) || (str3.toLowerCase().endsWith(".jar")))
    {
      try
      {
        localObject = new ZipOutputStream(new FileOutputStream(str3));
      }
      catch (IOException localIOException1)
      {
        GlobalOptions.err.println("Can't open zip file " + str3);
        localIOException1.printStackTrace(GlobalOptions.err);
        return;
      }
      localTabbedPrintWriter = new TabbedPrintWriter(new BufferedOutputStream((OutputStream)localObject), localImportHandler, false);
    }
    for (int i2 = localProcessOptions.getNoOptionPosition(); i2 < paramArrayOfString.length; i2++) {
      try
      {
        if (((paramArrayOfString[i2].endsWith(".jar")) || (paramArrayOfString[i2].endsWith(".zip"))) && (new File(paramArrayOfString[i2]).isFile()))
        {
          ClassInfo.setClassPath(paramArrayOfString[i2] + ',' + str1);
          Enumeration localEnumeration = new ZipFile(paramArrayOfString[i2]).entries();
          while (localEnumeration.hasMoreElements())
          {
            String str4 = ((ZipEntry)localEnumeration.nextElement()).getName();
            if (str4.endsWith(".class"))
            {
              str4 = str4.substring(0, str4.length() - 6).replace('/', '.');
              decompileClass(str4, (ZipOutputStream)localObject, str3, localTabbedPrintWriter, localImportHandler);
            }
          }
          ClassInfo.setClassPath(str1);
        }
        else
        {
          decompileClass(paramArrayOfString[i2], (ZipOutputStream)localObject, str3, localTabbedPrintWriter, localImportHandler);
        }
      }
      catch (IOException localIOException3)
      {
        GlobalOptions.err.println("Can't read zip file " + paramArrayOfString[i2] + ".");
        localIOException3.printStackTrace(GlobalOptions.err);
      }
    }
    if (localObject != null) {
      try
      {
        ((ZipOutputStream)localObject).close();
      }
      catch (IOException localIOException2)
      {
        GlobalOptions.err.println("Can't close Zipfile");
        localIOException2.printStackTrace(GlobalOptions.err);
      }
    }
  }
}


