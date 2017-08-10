package jode.type;

import java.io.PrintWriter;
import jode.AssertError;
import jode.GlobalOptions;

public class RangeType
  extends Type
{
  final ReferenceType bottomType;
  final ReferenceType topType;
  
  public RangeType(ReferenceType paramReferenceType1, ReferenceType paramReferenceType2)
  {
    super(103);
    if (paramReferenceType1 == tNull) {
      throw new AssertError("bottom is NULL");
    }
    this.bottomType = paramReferenceType1;
    this.topType = paramReferenceType2;
  }
  
  public ReferenceType getBottom()
  {
    return this.bottomType;
  }
  
  public ReferenceType getTop()
  {
    return this.topType;
  }
  
  public Type getHint()
  {
    Type localType1 = this.bottomType.getHint();
    Type localType2 = this.topType.getHint();
    if ((this.topType == tNull) && (this.bottomType.equals(localType1))) {
      return localType1;
    }
    return localType2;
  }
  
  public Type getCanonic()
  {
    return this.topType.getCanonic();
  }
  
  public Type getSuperType()
  {
    return this.topType.getSuperType();
  }
  
  public Type getSubType()
  {
    return tRange(this.bottomType, tNull);
  }
  
  public Type getCastHelper(Type paramType)
  {
    return this.topType.getCastHelper(paramType);
  }
  
  public String getTypeSignature()
  {
    if ((this.topType.isClassType()) || (!this.bottomType.isValidType())) {
      return this.topType.getTypeSignature();
    }
    return this.bottomType.getTypeSignature();
  }
  
  public Class getTypeClass()
    throws ClassNotFoundException
  {
    if ((this.topType.isClassType()) || (!this.bottomType.isValidType())) {
      return this.topType.getTypeClass();
    }
    return this.bottomType.getTypeClass();
  }
  
  public String toString()
  {
    return "<" + this.bottomType + "-" + this.topType + ">";
  }
  
  public String getDefaultName()
  {
    throw new AssertError("getDefaultName() called on range");
  }
  
  public int hashCode()
  {
    int i = this.topType.hashCode();
    return (i << 16 | i >>> 16) ^ this.bottomType.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof RangeType))
    {
      RangeType localRangeType = (RangeType)paramObject;
      return (this.topType.equals(localRangeType.topType)) && (this.bottomType.equals(localRangeType.bottomType));
    }
    return false;
  }
  
  public Type intersection(Type paramType)
  {
    if (paramType == tError) {
      return paramType;
    }
    if (paramType == Type.tUnknown) {
      return this;
    }
    Type localType2 = this.bottomType.getSpecializedType(paramType);
    Type localType1 = this.topType.getGeneralizedType(paramType);
    Type localType3;
    if (localType1.equals(localType2)) {
      localType3 = localType1;
    } else if (((localType1 instanceof ReferenceType)) && ((localType2 instanceof ReferenceType))) {
      localType3 = ((ReferenceType)localType1).createRangeType((ReferenceType)localType2);
    } else {
      localType3 = tError;
    }
    if ((GlobalOptions.debuggingFlags & 0x4) != 0) {
      GlobalOptions.err.println("intersecting " + this + " and " + paramType + " to " + localType3);
    }
    return localType3;
  }
}


