package jode.type;

public class NullType
  extends ReferenceType
{
  public NullType()
  {
    super(8);
  }
  
  public Type getSubType()
  {
    return this;
  }
  
  public Type createRangeType(ReferenceType paramReferenceType)
  {
    return tRange(paramReferenceType, this);
  }
  
  public Type getGeneralizedType(Type paramType)
  {
    if (paramType.typecode == 103) {
      paramType = ((RangeType)paramType).getTop();
    }
    return paramType;
  }
  
  public Type getSpecializedType(Type paramType)
  {
    if (paramType.typecode == 103) {
      paramType = ((RangeType)paramType).getBottom();
    }
    return paramType;
  }
  
  public String toString()
  {
    return "tNull";
  }
}


