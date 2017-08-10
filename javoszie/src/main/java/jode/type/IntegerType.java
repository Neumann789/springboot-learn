package jode.type;

import java.io.PrintWriter;
import jode.AssertError;
import jode.GlobalOptions;

public class IntegerType
  extends Type
{
  public static final int IT_Z = 1;
  public static final int IT_I = 2;
  public static final int IT_C = 4;
  public static final int IT_S = 8;
  public static final int IT_B = 16;
  public static final int IT_cS = 32;
  public static final int IT_cB = 64;
  private static final int NUM_TYPES = 7;
  private static final int[] subTypes = { 1, 30, 4, 24, 16, 96, 64 };
  private static final int[] superTypes = { 1, 2, 6, 10, 26, 46, 126 };
  private static final Type[] simpleTypes = { new IntegerType(1), new IntegerType(2), new IntegerType(4), new IntegerType(8), new IntegerType(16), new IntegerType(32), new IntegerType(64) };
  private static final String[] typeNames = { "Z", "I", "C", "S", "B", "s", "b" };
  int possTypes;
  int hintTypes;
  
  public IntegerType(int paramInt)
  {
    this(paramInt, paramInt);
  }
  
  public IntegerType(int paramInt1, int paramInt2)
  {
    super(107);
    this.possTypes = paramInt1;
    this.hintTypes = paramInt2;
  }
  
  public Type getHint()
  {
    int i = this.possTypes & this.hintTypes;
    if (i == 0) {
      i = this.possTypes;
    }
    for (int j = 0; (i & 0x1) == 0; j++) {
      i >>= 1;
    }
    return simpleTypes[j];
  }
  
  public Type getCanonic()
  {
    int i = this.possTypes;
    for (int j = 0; i >>= 1 != 0; j++) {}
    return simpleTypes[j];
  }
  
  private static int getSubTypes(int paramInt)
  {
    int i = 0;
    for (int j = 0; j < 7; j++) {
      if ((1 << j & paramInt) != 0) {
        i |= subTypes[j];
      }
    }
    return i;
  }
  
  private static int getSuperTypes(int paramInt)
  {
    int i = 0;
    for (int j = 0; j < 7; j++) {
      if ((1 << j & paramInt) != 0) {
        i |= superTypes[j];
      }
    }
    return i;
  }
  
  public Type getSubType()
  {
    return new IntegerType(getSubTypes(this.possTypes), getSubTypes(this.hintTypes));
  }
  
  public Type getSuperType()
  {
    return new IntegerType(getSuperTypes(this.possTypes), this.hintTypes);
  }
  
  public boolean isValidType()
  {
    return true;
  }
  
  public boolean isOfType(Type paramType)
  {
    return (paramType.typecode == 107) && ((((IntegerType)paramType).possTypes & this.possTypes) != 0);
  }
  
  public String getDefaultName()
  {
    switch (((IntegerType)getHint()).possTypes)
    {
    case 1: 
      return "bool";
    case 4: 
      return "c";
    case 2: 
    case 8: 
    case 16: 
      return "i";
    }
    throw new AssertError("Local can't be of constant type!");
  }
  
  public Object getDefaultValue()
  {
    return new Integer(0);
  }
  
  public String getTypeSignature()
  {
    switch (((IntegerType)getHint()).possTypes)
    {
    case 1: 
      return "Z";
    case 4: 
      return "C";
    case 16: 
      return "B";
    case 8: 
      return "S";
    }
    return "I";
  }
  
  public Class getTypeClass()
  {
    switch (((IntegerType)getHint()).possTypes)
    {
    case 1: 
      return Boolean.TYPE;
    case 4: 
      return Character.TYPE;
    case 16: 
      return Byte.TYPE;
    case 8: 
      return Short.TYPE;
    }
    return Integer.TYPE;
  }
  
  public String toString()
  {
    if (this.possTypes == this.hintTypes) {
      switch (this.possTypes)
      {
      case 1: 
        return "boolean";
      case 4: 
        return "char";
      case 16: 
        return "byte";
      case 8: 
        return "short";
      case 2: 
        return "int";
      }
    }
    StringBuffer localStringBuffer = new StringBuffer("{");
    for (int i = 0; i < 7; i++) {
      if ((1 << i & this.possTypes) != 0) {
        localStringBuffer.append(typeNames[i]);
      }
    }
    if (this.possTypes != this.hintTypes)
    {
      localStringBuffer.append(":");
      for (i = 0; i < 7; i++) {
        if ((1 << i & this.hintTypes) != 0) {
          localStringBuffer.append(typeNames[i]);
        }
      }
    }
    localStringBuffer.append("}");
    return localStringBuffer.toString();
  }
  
  public Type intersection(Type paramType)
  {
    if (paramType == tError) {
      return paramType;
    }
    if (paramType == tUnknown) {
      return this;
    }
    int j = 0;
    int i;
    if (paramType.typecode != 107)
    {
      i = 0;
    }
    else
    {
      localIntegerType = (IntegerType)paramType;
      i = this.possTypes & localIntegerType.possTypes;
      j = this.hintTypes & localIntegerType.hintTypes;
      if ((i == this.possTypes) && (j == this.hintTypes)) {
        return this;
      }
      if ((i == localIntegerType.possTypes) && (j == localIntegerType.hintTypes)) {
        return localIntegerType;
      }
    }
    IntegerType localIntegerType = i == 0 ? tError : new IntegerType(i, j);
    if ((GlobalOptions.debuggingFlags & 0x4) != 0) {
      GlobalOptions.err.println("intersecting " + this + " and " + paramType + " to " + localIntegerType);
    }
    return localIntegerType;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof IntegerType))
    {
      IntegerType localIntegerType = (IntegerType)paramObject;
      return (localIntegerType.possTypes == this.possTypes) && (localIntegerType.hintTypes == this.hintTypes);
    }
    return false;
  }
}


