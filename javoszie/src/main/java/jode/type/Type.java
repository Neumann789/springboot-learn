package jode.type;

import java.io.PrintWriter;
import java.util.Iterator;
import jode.AssertError;
import jode.GlobalOptions;
import jode.bytecode.ClassInfo;
import jode.util.UnifyHash;

public class Type
{
  public static final int TC_BOOLEAN = 0;
  public static final int TC_BYTE = 1;
  public static final int TC_CHAR = 2;
  public static final int TC_SHORT = 3;
  public static final int TC_INT = 4;
  public static final int TC_LONG = 5;
  public static final int TC_FLOAT = 6;
  public static final int TC_DOUBLE = 7;
  public static final int TC_NULL = 8;
  public static final int TC_ARRAY = 9;
  public static final int TC_CLASS = 10;
  public static final int TC_VOID = 11;
  public static final int TC_METHOD = 12;
  public static final int TC_ERROR = 13;
  public static final int TC_UNKNOWN = 101;
  public static final int TC_RANGE = 103;
  public static final int TC_INTEGER = 107;
  private static final UnifyHash classHash = new UnifyHash();
  private static final UnifyHash arrayHash = new UnifyHash();
  private static final UnifyHash methodHash = new UnifyHash();
  public static final Type tBoolean = new IntegerType(1);
  public static final Type tByte = new IntegerType(16);
  public static final Type tChar = new IntegerType(4);
  public static final Type tShort = new IntegerType(8);
  public static final Type tInt = new IntegerType(2);
  public static final Type tLong = new Type(5);
  public static final Type tFloat = new Type(6);
  public static final Type tDouble = new Type(7);
  public static final Type tVoid = new Type(11);
  public static final Type tError = new Type(13);
  public static final Type tUnknown = new Type(101);
  public static final Type tUInt = new IntegerType(30);
  public static final Type tBoolInt = new IntegerType(3);
  public static final Type tBoolUInt = new IntegerType(31);
  public static final Type tBoolByte = new IntegerType(17);
  public static final ClassInterfacesType tObject = tClass("java.lang.Object");
  public static final ReferenceType tNull = new NullType();
  public static final Type tUObject = tRange(tObject, tNull);
  public static final Type tString = tClass("java.lang.String");
  public static final Type tStringBuffer = tClass("java.lang.StringBuffer");
  public static final Type tJavaLangClass = tClass("java.lang.Class");
  final int typecode;
  
  public static final Type tType(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return tError;
    }
    switch (paramString.charAt(0))
    {
    case 'Z': 
      return tBoolean;
    case 'B': 
      return tByte;
    case 'C': 
      return tChar;
    case 'S': 
      return tShort;
    case 'I': 
      return tInt;
    case 'F': 
      return tFloat;
    case 'J': 
      return tLong;
    case 'D': 
      return tDouble;
    case 'V': 
      return tVoid;
    case '[': 
      return tArray(tType(paramString.substring(1)));
    case 'L': 
      int i = paramString.indexOf(';');
      if (i != paramString.length() - 1) {
        return tError;
      }
      return tClass(paramString.substring(1, i));
    case '(': 
      return tMethod(paramString);
    }
    throw new AssertError("Unknown type signature: " + paramString);
  }
  
  public static final ClassInterfacesType tClass(String paramString)
  {
    return tClass(ClassInfo.forName(paramString.replace('/', '.')));
  }
  
  public static final ClassInterfacesType tClass(ClassInfo paramClassInfo)
  {
    int i = paramClassInfo.hashCode();
    Iterator localIterator = classHash.iterateHashCode(i);
    while (localIterator.hasNext())
    {
      localClassInterfacesType = (ClassInterfacesType)localIterator.next();
      if (localClassInterfacesType.getClassInfo() == paramClassInfo) {
        return localClassInterfacesType;
      }
    }
    ClassInterfacesType localClassInterfacesType = new ClassInterfacesType(paramClassInfo);
    classHash.put(i, localClassInterfacesType);
    return localClassInterfacesType;
  }
  
  public static final Type tArray(Type paramType)
  {
    if (paramType == tError) {
      return paramType;
    }
    int i = paramType.hashCode();
    Iterator localIterator = arrayHash.iterateHashCode(i);
    while (localIterator.hasNext())
    {
      localArrayType = (ArrayType)localIterator.next();
      if (localArrayType.getElementType().equals(paramType)) {
        return localArrayType;
      }
    }
    ArrayType localArrayType = new ArrayType(paramType);
    arrayHash.put(i, localArrayType);
    return localArrayType;
  }
  
  public static MethodType tMethod(String paramString)
  {
    int i = paramString.hashCode();
    Iterator localIterator = methodHash.iterateHashCode(i);
    while (localIterator.hasNext())
    {
      localMethodType = (MethodType)localIterator.next();
      if (localMethodType.getTypeSignature().equals(paramString)) {
        return localMethodType;
      }
    }
    MethodType localMethodType = new MethodType(paramString);
    methodHash.put(i, localMethodType);
    return localMethodType;
  }
  
  public static final Type tRange(ReferenceType paramReferenceType1, ReferenceType paramReferenceType2)
  {
    return new RangeType(paramReferenceType1, paramReferenceType2);
  }
  
  public static Type tSuperType(Type paramType)
  {
    return paramType.getSuperType();
  }
  
  public static Type tSubType(Type paramType)
  {
    return paramType.getSubType();
  }
  
  protected Type(int paramInt)
  {
    this.typecode = paramInt;
  }
  
  public Type getSubType()
  {
    return this;
  }
  
  public Type getSuperType()
  {
    return this;
  }
  
  public Type getHint()
  {
    return getCanonic();
  }
  
  public Type getCanonic()
  {
    return this;
  }
  
  public final int getTypeCode()
  {
    return this.typecode;
  }
  
  public int stackSize()
  {
    switch (this.typecode)
    {
    case 11: 
      return 0;
    case 6: 
    case 8: 
    case 9: 
    case 10: 
    case 12: 
    case 13: 
    default: 
      return 1;
    }
    return 2;
  }
  
  public Type intersection(Type paramType)
  {
    if ((this == tError) || (paramType == tError)) {
      return tError;
    }
    if (this == tUnknown) {
      return paramType;
    }
    if ((paramType == tUnknown) || (this == paramType)) {
      return this;
    }
    if ((GlobalOptions.debuggingFlags & 0x4) != 0) {
      GlobalOptions.err.println("intersecting " + this + " and " + paramType + " to <error>");
    }
    return tError;
  }
  
  public Type getCastHelper(Type paramType)
  {
    return null;
  }
  
  public boolean isValidType()
  {
    return this.typecode <= 7;
  }
  
  public boolean isClassType()
  {
    return false;
  }
  
  public boolean isOfType(Type paramType)
  {
    return intersection(paramType) != tError;
  }
  
  public String getDefaultName()
  {
    switch (this.typecode)
    {
    case 5: 
      return "l";
    case 6: 
      return "f";
    case 7: 
      return "d";
    }
    return "local";
  }
  
  public Object getDefaultValue()
  {
    switch (this.typecode)
    {
    case 5: 
      return new Long(0L);
    case 6: 
      return new Float(0.0F);
    case 7: 
      return new Double(0.0D);
    }
    return null;
  }
  
  public String getTypeSignature()
  {
    switch (this.typecode)
    {
    case 5: 
      return "J";
    case 6: 
      return "F";
    case 7: 
      return "D";
    }
    return "?";
  }
  
  public Class getTypeClass()
    throws ClassNotFoundException
  {
    switch (this.typecode)
    {
    case 5: 
      return Long.TYPE;
    case 6: 
      return Float.TYPE;
    case 7: 
      return Double.TYPE;
    }
    throw new AssertError("getTypeClass() called on illegal type");
  }
  
  public String toString()
  {
    switch (this.typecode)
    {
    case 5: 
      return "long";
    case 6: 
      return "float";
    case 7: 
      return "double";
    case 8: 
      return "null";
    case 11: 
      return "void";
    case 101: 
      return "<unknown>";
    }
    return "<error>";
  }
}


