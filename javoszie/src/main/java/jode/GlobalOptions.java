package jode;

import java.io.PrintWriter;
import java.util.StringTokenizer;

public class GlobalOptions
{
  public static final String version = "1.1.2-pre1";
  public static final String email = "jochen@gnu.org";
  public static final String copyright = "Jode (c) 1998-2001 Jochen Hoenicke <jochen@gnu.org>";
  public static final String URL = "http://jode.sourceforge.net/";
  public static PrintWriter err = new PrintWriter(System.err, true);
  public static int verboseLevel = 0;
  public static int debuggingFlags = 0;
  public static final int DEBUG_BYTECODE = 1;
  public static final int DEBUG_VERIFIER = 2;
  public static final int DEBUG_TYPES = 4;
  public static final int DEBUG_FLOW = 8;
  public static final int DEBUG_INOUT = 16;
  public static final int DEBUG_ANALYZE = 32;
  public static final int DEBUG_LVT = 64;
  public static final int DEBUG_CHECK = 128;
  public static final int DEBUG_LOCALS = 256;
  public static final int DEBUG_CONSTRS = 512;
  public static final int DEBUG_INTERPRT = 1024;
  public static final String[] debuggingNames = { "bytecode", "verifier", "types", "flow", "inout", "analyze", "lvt", "check", "locals", "constructors", "interpreter" };
  
  public static void usageDebugging()
  {
    err.println("Debugging option: --debug=flag1,flag2,...");
    err.println("possible flags:");
    err.println("  bytecode     show bytecode, as it is read from class file.");
    err.println("  verifier     show result of bytecode verification.");
    err.println("  types        show type intersections");
    err.println("  flow         show flow block merging.");
    err.println("  analyze      show T1/T2 analyzation of flow blocks.");
    err.println("  inout        show in/out set analysis.");
    err.println("  lvt          dump LocalVariableTable.");
    err.println("  check        do time consuming sanity checks.");
    err.println("  locals       dump local merging information.");
    err.println("  constructors dump constructor simplification.");
    err.println("  interpreter  debug execution of interpreter.");
    System.exit(0);
  }
  
  public static boolean setDebugging(String paramString)
  {
    if ((paramString.length() == 0) || (paramString.equals("help")))
    {
      usageDebugging();
      return false;
    }
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
    if (localStringTokenizer.hasMoreTokens())
    {
      String str = localStringTokenizer.nextToken().intern();
      for (int i = 0;; i++)
      {
        if (i >= debuggingNames.length) {
          break label85;
        }
        if (str == debuggingNames[i])
        {
          debuggingFlags |= 1 << i;
          break;
        }
      }
      label85:
      err.println("Illegal debugging flag: " + str);
      return false;
    }
    return true;
  }
}


