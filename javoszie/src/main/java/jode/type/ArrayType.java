package jode.type;

import java.util.Vector;
import jode.bytecode.ClassInfo;

public class ArrayType
  extends ReferenceType
{
  static final ClassInfo[] arrayIfaces = { ClassInfo.forName("java.lang.Cloneable"), ClassInfo.forName("java.io.Serializable") };
  Type elementType;
  
  ArrayType(Type paramType)
  {
    super(9);
    this.elementType = paramType;
  }
  
  public Type getElementType()
  {
    return this.elementType;
  }
  
  public Type getSuperType()
  {
    if ((this.elementType instanceof IntegerType)) {
      return tRange(tObject, this);
    }
    return tRange(tObject, (ReferenceType)tArray(this.elementType.getSuperType()));
  }
  
  public Type getSubType()
  {
    if ((this.elementType instanceof IntegerType)) {
      return this;
    }
    return tArray(this.elementType.getSubType());
  }
  
  public Type getHint()
  {
    return tArray(this.elementType.getHint());
  }
  
  public Type getCanonic()
  {
    return tArray(this.elementType.getCanonic());
  }
  
  public Type createRangeType(ReferenceType paramReferenceType)
  {
    if (paramReferenceType.getTypeCode() == 9) {
      return tArray(this.elementType.intersection(((ArrayType)paramReferenceType).elementType));
    }
    if (paramReferenceType.getTypeCode() == 10)
    {
      ClassInterfacesType localClassInterfacesType = (ClassInterfacesType)paramReferenceType;
      if ((localClassInterfacesType.clazz == null) && (implementsAllIfaces(null, arrayIfaces, localClassInterfacesType.ifaces))) {
        return tRange(localClassInterfacesType, this);
      }
    }
    return tError;
  }
  
  public Type getSpecializedType(Type paramType)
  {
    if (paramType.getTypeCode() == 103) {
      paramType = ((RangeType)paramType).getBottom();
    }
    if (paramType == tNull) {
      return this;
    }
    Object localObject;
    if (paramType.getTypeCode() == 9)
    {
      localObject = this.elementType.intersection(((ArrayType)paramType).elementType);
      return localObject != tError ? tArray((Type)localObject) : tError;
    }
    if (paramType.getTypeCode() == 10)
    {
      localObject = (ClassInterfacesType)paramType;
      if ((((ClassInterfacesType)localObject).clazz == null) && (implementsAllIfaces(null, arrayIfaces, ((ClassInterfacesType)localObject).ifaces))) {
        return this;
      }
    }
    return tError;
  }
  
  public Type getGeneralizedType(Type paramType)
  {
    if (paramType.getTypeCode() == 103) {
      paramType = ((RangeType)paramType).getTop();
    }
    if (paramType == tNull) {
      return this;
    }
    Object localObject;
    if (paramType.getTypeCode() == 9)
    {
      localObject = this.elementType.intersection(((ArrayType)paramType).elementType);
      if (localObject != tError) {
        return tArray((Type)localObject);
      }
      return ClassInterfacesType.create(null, arrayIfaces);
    }
    if (paramType.getTypeCode() == 10)
    {
      localObject = (ClassInterfacesType)paramType;
      if (implementsAllIfaces(((ClassInterfacesType)localObject).clazz, ((ClassInterfacesType)localObject).ifaces, arrayIfaces)) {
        return ClassInterfacesType.create(null, arrayIfaces);
      }
      if ((((ClassInterfacesType)localObject).clazz == null) && (implementsAllIfaces(null, arrayIfaces, ((ClassInterfacesType)localObject).ifaces))) {
        return (Type)localObject;
      }
      Vector localVector = new Vector();
      for (int i = 0; i < arrayIfaces.length; i++) {
        if ((((ClassInterfacesType)localObject).clazz != null) && (arrayIfaces[i].implementedBy(((ClassInterfacesType)localObject).clazz))) {
          localVector.addElement(arrayIfaces[i]);
        } else {
          for (int j = 0; j < ((ClassInterfacesType)localObject).ifaces.length; j++) {
            if (arrayIfaces[i].implementedBy(localObject.ifaces[j]))
            {
              localVector.addElement(arrayIfaces[i]);
              break;
            }
          }
        }
      }
      ClassInfo[] arrayOfClassInfo = new ClassInfo[localVector.size()];
      localVector.copyInto(arrayOfClassInfo);
      return ClassInterfacesType.create(null, arrayOfClassInfo);
    }
    return tError;
  }
  
  public Type getCastHelper(Type paramType)
  {
    Type localType1 = paramType.getHint();
    switch (localType1.getTypeCode())
    {
    case 9: 
      if ((!this.elementType.isClassType()) || (!((ArrayType)localType1).elementType.isClassType())) {
        return tObject;
      }
      Type localType2 = this.elementType.getCastHelper(((ArrayType)localType1).elementType);
      if (localType2 != null) {
        return tArray(localType2);
      }
      return null;
    case 10: 
      ClassInterfacesType localClassInterfacesType = (ClassInterfacesType)localType1;
      if ((localClassInterfacesType.clazz == null) && (implementsAllIfaces(null, arrayIfaces, localClassInterfacesType.ifaces))) {
        return null;
      }
      return tObject;
    case 101: 
      return null;
    }
    return tObject;
  }
  
  public boolean isValidType()
  {
    return this.elementType.isValidType();
  }
  
  public boolean isClassType()
  {
    return true;
  }
  
  public String getTypeSignature()
  {
    return "[" + this.elementType.getTypeSignature();
  }
  
  public Class getTypeClass()
    throws ClassNotFoundException
  {
    return Class.forName("[" + this.elementType.getTypeSignature());
  }
  
  public String toString()
  {
    return this.elementType.toString() + "[]";
  }
  
  private static String pluralize(String paramString)
  {
    return paramString + ((paramString.endsWith("s")) || (paramString.endsWith("x")) || (paramString.endsWith("sh")) || (paramString.endsWith("ch")) ? "es" : "s");
  }
  
  public String getDefaultName()
  {
    if ((this.elementType instanceof ArrayType)) {
      return this.elementType.getDefaultName();
    }
    return pluralize(this.elementType.getDefaultName());
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof ArrayType))
    {
      ArrayType localArrayType = (ArrayType)paramObject;
      return localArrayType.elementType.equals(this.elementType);
    }
    return false;
  }
}


