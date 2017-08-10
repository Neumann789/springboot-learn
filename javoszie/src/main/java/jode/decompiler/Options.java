package jode.decompiler;

import jode.bytecode.ClassInfo;
import jode.bytecode.InnerClassInfo;

public class Options
{
  public static final int TAB_SIZE_MASK = 15;
  public static final int BRACE_AT_EOL = 16;
  public static final int BRACE_FLUSH_LEFT = 32;
  public static final int GNU_SPACING = 64;
  public static final int SUN_STYLE = 20;
  public static final int GNU_STYLE = 66;
  public static final int PASCAL_STYLE = 36;
  public static final int OPTION_LVT = 1;
  public static final int OPTION_INNER = 2;
  public static final int OPTION_ANON = 4;
  public static final int OPTION_PUSH = 8;
  public static final int OPTION_PRETTY = 16;
  public static final int OPTION_DECRYPT = 32;
  public static final int OPTION_ONETIME = 64;
  public static final int OPTION_IMMEDIATE = 128;
  public static final int OPTION_VERIFY = 256;
  public static final int OPTION_CONTRAFO = 512;
  public static int options = 823;
  public static int outputStyle = 20;
  
  public static final boolean doAnonymous()
  {
    return (options & 0x4) != 0;
  }
  
  public static final boolean doInner()
  {
    return (options & 0x2) != 0;
  }
  
  public static boolean skipClass(ClassInfo paramClassInfo)
  {
    InnerClassInfo[] arrayOfInnerClassInfo = paramClassInfo.getOuterClasses();
    if (arrayOfInnerClassInfo != null)
    {
      if (arrayOfInnerClassInfo[0].outer == null) {
        return doAnonymous();
      }
      return doInner();
    }
    return false;
  }
}


