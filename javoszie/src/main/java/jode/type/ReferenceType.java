package jode.type;

import java.io.PrintWriter;
import jode.GlobalOptions;
import jode.bytecode.ClassInfo;

public abstract class ReferenceType
  extends Type
{
  public ReferenceType(int paramInt)
  {
    super(paramInt);
  }
  
  public abstract Type getSpecializedType(Type paramType);
  
  public abstract Type getGeneralizedType(Type paramType);
  
  public abstract Type createRangeType(ReferenceType paramReferenceType);
  
  protected static boolean implementsAllIfaces(ClassInfo paramClassInfo, ClassInfo[] paramArrayOfClassInfo1, ClassInfo[] paramArrayOfClassInfo2)
  {
    label62:
    for (int i = 0; i < paramArrayOfClassInfo2.length; i++)
    {
      ClassInfo localClassInfo = paramArrayOfClassInfo2[i];
      if ((paramClassInfo == null) || (!localClassInfo.implementedBy(paramClassInfo)))
      {
        for (int j = 0; j < paramArrayOfClassInfo1.length; j++) {
          if (localClassInfo.implementedBy(paramArrayOfClassInfo1[j])) {
            break label62;
          }
        }
        return false;
      }
    }
    return true;
  }
  
  public Type getSuperType()
  {
    return this == tObject ? tObject : tRange(tObject, this);
  }
  
  public abstract Type getSubType();
  
  public Type intersection(Type paramType)
  {
    if (paramType == tError) {
      return paramType;
    }
    if (paramType == Type.tUnknown) {
      return this;
    }
    Type localType1 = getSpecializedType(paramType);
    Type localType2 = getGeneralizedType(paramType);
    Type localType3;
    if (localType2.equals(localType1)) {
      localType3 = localType2;
    } else if (((localType2 instanceof ReferenceType)) && ((localType1 instanceof ReferenceType))) {
      localType3 = ((ReferenceType)localType2).createRangeType((ReferenceType)localType1);
    } else {
      localType3 = tError;
    }
    if ((GlobalOptions.debuggingFlags & 0x4) != 0) {
      GlobalOptions.err.println("intersecting " + this + " and " + paramType + " to " + localType3);
    }
    return localType3;
  }
}


