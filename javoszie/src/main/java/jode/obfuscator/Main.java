package jode.obfuscator;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Random;
import jode.GlobalOptions;
import jode.util.Option;
import jode.util.ProcessOptions;

public class Main
{
  public static boolean swapOrder = false;
  public static final int OPTION_STRONGOVERLOAD = 1;
  public static final int OPTION_PRESERVESERIAL = 2;
  public static int options = 2;
  private static final Option[] longOptions = { new Option("cp", 1, 99), new Option("classpath", 1, 99), new Option("destpath", 1, 100), new Option("help", 0, 104), new Option("version", 0, 86), new Option("verbose", 2, 118), new Option("debug", 2, 68) };
  public static final String[] stripNames = { "unreach", "inner", "lvt", "lnt", "source" };
  public static final int STRIP_UNREACH = 1;
  public static final int STRIP_INNERINFO = 2;
  public static final int STRIP_LVT = 4;
  public static final int STRIP_LNT = 8;
  public static final int STRIP_SOURCE = 16;
  public static int stripping = 0;
  public static Random rand = new Random(123456L);
  private static ClassBundle bundle;
  
  public static void usage()
  {
    PrintWriter localPrintWriter = GlobalOptions.err;
    localPrintWriter.println("usage: jode.Obfuscator flags* script");
    localPrintWriter.println("  -h, --help           show this information.");
    localPrintWriter.println("  -V, --version        output version information and exit.");
    localPrintWriter.println("  -v, --verbose        be verbose (multiple times means more verbose).");
    localPrintWriter.println("  -c, --classpath <path> search for classes in specified classpath.");
    localPrintWriter.println("                       The directories should be separated by ','.");
    localPrintWriter.println("  -d, --dest <dir>     write decompiled files to disk into directory destdir.");
    localPrintWriter.println("  -D, --debug=...      use --debug=help for more information.");
  }
  
  public static ClassBundle getClassBundle()
  {
    return bundle;
  }
  
  public static void main(String[] paramArrayOfString)
  {
    if (paramArrayOfString.length == 0)
    {
      usage();
      return;
    }
    String str1 = null;
    String str2 = null;
    GlobalOptions.err.println("Jode (c) 1998-2001 Jochen Hoenicke <jochen@gnu.org>");
    bundle = new ClassBundle();
    int i = 0;
    ProcessOptions localProcessOptions = new ProcessOptions(paramArrayOfString, longOptions);
    Object localObject;
    for (int j = localProcessOptions.getOption(); j != -1; j = localProcessOptions.getOption()) {
      switch (j)
      {
      case 0: 
        break;
      case 104: 
        usage();
        i = 1;
        break;
      case 86: 
        GlobalOptions.err.println("1.1.2-pre1");
        break;
      case 99: 
        str1 = localProcessOptions.getOptionArgument();
        break;
      case 100: 
        str2 = localProcessOptions.getOptionArgument();
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
            GlobalOptions.err.println("jode.obfuscator.Main: Argument `" + (String)localObject + "' to --verbose must be numeric:");
            i = 1;
          }
        }
        break;
      case 68: 
        localObject = localProcessOptions.getOptionArgument();
        if (localObject == null) {
          localObject = "help";
        }
        i |= (!GlobalOptions.setDebugging((String)localObject) ? 1 : 0);
        break;
      default: 
        i = 1;
      }
    }
    if (i != 0) {
      return;
    }
    if (localProcessOptions.getPosition() != paramArrayOfString.length - 1)
    {
      GlobalOptions.err.println("You must specify exactly one script.");
      return;
    }
    try
    {
      String str3 = paramArrayOfString[localProcessOptions.getPosition()];
      localObject = new ScriptParser(str3.equals("-") ? new InputStreamReader(System.in) : new FileReader(str3));
      ((ScriptParser)localObject).parseOptions(bundle);
    }
    catch (IOException localIOException)
    {
      GlobalOptions.err.println("IOException while reading script file.");
      localIOException.printStackTrace(GlobalOptions.err);
      return;
    }
    catch (ParseException localParseException)
    {
      GlobalOptions.err.println("Syntax error in script file: ");
      GlobalOptions.err.println(localParseException.getMessage());
      if (GlobalOptions.verboseLevel > 5) {
        localParseException.printStackTrace(GlobalOptions.err);
      }
      return;
    }
    if (str1 != null) {
      bundle.setOption("classpath", Collections.singleton(str1));
    }
    if (str2 != null) {
      bundle.setOption("dest", Collections.singleton(str2));
    }
    bundle.run();
  }
}


