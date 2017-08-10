package jode.decompiler;

public abstract interface Scope
{
  public static final int PACKAGENAME = 0;
  public static final int CLASSNAME = 1;
  public static final int METHODNAME = 2;
  public static final int FIELDNAME = 3;
  public static final int AMBIGUOUSNAME = 4;
  public static final int LOCALNAME = 5;
  public static final int NOSUPERMETHODNAME = 12;
  public static final int NOSUPERFIELDNAME = 13;
  public static final int CLASSSCOPE = 1;
  public static final int METHODSCOPE = 2;
  
  public abstract boolean isScopeOf(Object paramObject, int paramInt);
  
  public abstract boolean conflicts(String paramString, int paramInt);
}


