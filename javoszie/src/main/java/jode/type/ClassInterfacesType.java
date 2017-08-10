package jode.type;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import jode.AssertError;
import jode.bytecode.ClassInfo;

public class ClassInterfacesType
  extends ReferenceType
{
  ClassInfo clazz;
  ClassInfo[] ifaces;
  private static final Hashtable keywords = new Hashtable();
  
  public ClassInfo getClazz()
  {
    return this.clazz != null ? this.clazz : ClassInfo.javaLangObject;
  }
  
  public ClassInterfacesType(String paramString)
  {
    super(10);
    ClassInfo localClassInfo = ClassInfo.forName(paramString);
    if (localClassInfo.isInterface())
    {
      this.clazz = null;
      this.ifaces = new ClassInfo[] { localClassInfo };
    }
    else
    {
      this.clazz = (localClassInfo == ClassInfo.javaLangObject ? null : localClassInfo);
      this.ifaces = new ClassInfo[0];
    }
  }
  
  public ClassInterfacesType(ClassInfo paramClassInfo)
  {
    super(10);
    if (paramClassInfo.isInterface())
    {
      this.clazz = null;
      this.ifaces = new ClassInfo[] { paramClassInfo };
    }
    else
    {
      this.clazz = (paramClassInfo == ClassInfo.javaLangObject ? null : paramClassInfo);
      this.ifaces = new ClassInfo[0];
    }
  }
  
  public ClassInterfacesType(ClassInfo paramClassInfo, ClassInfo[] paramArrayOfClassInfo)
  {
    super(10);
    this.clazz = paramClassInfo;
    this.ifaces = paramArrayOfClassInfo;
  }
  
  static ClassInterfacesType create(ClassInfo paramClassInfo, ClassInfo[] paramArrayOfClassInfo)
  {
    if ((paramArrayOfClassInfo.length == 0) && (paramClassInfo == null)) {
      return tObject;
    }
    if (paramArrayOfClassInfo.length == 0) {
      return tClass(paramClassInfo);
    }
    if ((paramArrayOfClassInfo.length == 1) && (paramClassInfo == null)) {
      return tClass(paramArrayOfClassInfo[0]);
    }
    return new ClassInterfacesType(paramClassInfo, paramArrayOfClassInfo);
  }
  
  public Type getSubType()
  {
    if (((this.clazz == null) && (this.ifaces.length == 1)) || (this.ifaces.length == 0)) {
      return tRange(this, tNull);
    }
    throw new AssertError("getSubType called on set of classes and interfaces!");
  }
  
  public Type getHint()
  {
    if ((this.ifaces.length == 0) || ((this.clazz == null) && (this.ifaces.length == 1))) {
      return this;
    }
    if (this.clazz != null) {
      return Type.tClass(this.clazz.getName());
    }
    return Type.tClass(this.ifaces[0].getName());
  }
  
  public Type getCanonic()
  {
    if ((this.ifaces.length == 0) || ((this.clazz == null) && (this.ifaces.length == 1))) {
      return this;
    }
    if (this.clazz != null) {
      return Type.tClass(this.clazz.getName());
    }
    return Type.tClass(this.ifaces[0].getName());
  }
  
  public Type createRangeType(ReferenceType paramReferenceType)
  {
    if (paramReferenceType.typecode != 10) {
      return tError;
    }
    ClassInterfacesType localClassInterfacesType = (ClassInterfacesType)paramReferenceType;
    if (paramReferenceType == tObject) {
      return this == tObject ? tObject : tRange(tObject, this);
    }
    if (localClassInterfacesType.clazz != null)
    {
      if (!localClassInterfacesType.clazz.superClassOf(this.clazz)) {
        return tError;
      }
      for (int i = 0; i < localClassInterfacesType.ifaces.length; i++) {
        if (!localClassInterfacesType.ifaces[i].implementedBy(this.clazz)) {
          return tError;
        }
      }
      if ((localClassInterfacesType.clazz == this.clazz) && (localClassInterfacesType.ifaces.length == 0)) {
        return localClassInterfacesType;
      }
      if (this.ifaces.length != 0) {
        return tRange(localClassInterfacesType, create(this.clazz, new ClassInfo[0]));
      }
      return tRange(localClassInterfacesType, this);
    }
    ClassInfo localClassInfo = this.clazz;
    int j;
    if (localClassInfo != null) {
      for (j = 0; j < localClassInterfacesType.ifaces.length; j++) {
        if (!localClassInterfacesType.ifaces[j].implementedBy(localClassInfo))
        {
          localClassInfo = null;
          break;
        }
      }
    }
    if ((localClassInfo == null) && (localClassInterfacesType.ifaces.length == 1)) {
      for (j = 0; j < this.ifaces.length; j++) {
        if (this.ifaces[j] == localClassInterfacesType.ifaces[0]) {
          return localClassInterfacesType;
        }
      }
    }
    Object localObject = new ClassInfo[this.ifaces.length];
    int k = 0;
    label339:
    for (int m = 0; m < this.ifaces.length; m++)
    {
      for (int n = 0; n < localClassInterfacesType.ifaces.length; n++) {
        if (!localClassInterfacesType.ifaces[n].implementedBy(this.ifaces[m])) {
          break label339;
        }
      }
      localObject[(k++)] = this.ifaces[m];
    }
    if ((localClassInfo == null) && (k == 0)) {
      return tError;
    }
    if (k < localObject.length)
    {
      ClassInfo[] arrayOfClassInfo = new ClassInfo[k];
      System.arraycopy(localObject, 0, arrayOfClassInfo, 0, k);
      localObject = arrayOfClassInfo;
    }
    else if (localClassInfo == this.clazz)
    {
      return tRange(localClassInterfacesType, this);
    }
    return tRange(localClassInterfacesType, create(localClassInfo, (ClassInfo[])localObject));
  }
  
  public Type getSpecializedType(Type paramType)
  {
    int i = paramType.typecode;
    if (i == 103)
    {
      paramType = ((RangeType)paramType).getBottom();
      i = paramType.typecode;
    }
    if (i == 8) {
      return this;
    }
    if (i == 9) {
      return ((ArrayType)paramType).getSpecializedType(this);
    }
    if (i != 10) {
      return tError;
    }
    ClassInterfacesType localClassInterfacesType = (ClassInterfacesType)paramType;
    ClassInfo localClassInfo1;
    if (this.clazz == null) {
      localClassInfo1 = localClassInterfacesType.clazz;
    } else if (localClassInterfacesType.clazz == null) {
      localClassInfo1 = this.clazz;
    } else if (this.clazz.superClassOf(localClassInterfacesType.clazz)) {
      localClassInfo1 = localClassInterfacesType.clazz;
    } else if (localClassInterfacesType.clazz.superClassOf(this.clazz)) {
      localClassInfo1 = this.clazz;
    } else {
      return tError;
    }
    if ((localClassInfo1 == this.clazz) && (implementsAllIfaces(this.clazz, this.ifaces, localClassInterfacesType.ifaces))) {
      return this;
    }
    if ((localClassInfo1 == localClassInterfacesType.clazz) && (implementsAllIfaces(localClassInterfacesType.clazz, localClassInterfacesType.ifaces, this.ifaces))) {
      return localClassInterfacesType;
    }
    Vector localVector = new Vector();
    ClassInfo localClassInfo2;
    int k;
    label295:
    for (int j = 0; j < this.ifaces.length; j++)
    {
      localClassInfo2 = this.ifaces[j];
      if ((localClassInfo1 == null) || (!localClassInfo2.implementedBy(localClassInfo1)))
      {
        for (k = 0; k < localClassInterfacesType.ifaces.length; k++) {
          if (localClassInfo2.implementedBy(localClassInterfacesType.ifaces[k])) {
            break label295;
          }
        }
        localVector.addElement(localClassInfo2);
      }
    }
    label388:
    for (j = 0; j < localClassInterfacesType.ifaces.length; j++)
    {
      localClassInfo2 = localClassInterfacesType.ifaces[j];
      if ((localClassInfo1 == null) || (!localClassInfo2.implementedBy(localClassInfo1)))
      {
        for (k = 0; k < localVector.size(); k++) {
          if (localClassInfo2.implementedBy((ClassInfo)localVector.elementAt(k))) {
            break label388;
          }
        }
        localVector.addElement(localClassInfo2);
      }
    }
    ClassInfo[] arrayOfClassInfo = new ClassInfo[localVector.size()];
    localVector.copyInto(arrayOfClassInfo);
    return create(localClassInfo1, arrayOfClassInfo);
  }
  
  public Type getGeneralizedType(Type paramType)
  {
    int i = paramType.typecode;
    if (i == 103)
    {
      paramType = ((RangeType)paramType).getTop();
      i = paramType.typecode;
    }
    if (i == 8) {
      return this;
    }
    if (i == 9) {
      return ((ArrayType)paramType).getGeneralizedType(this);
    }
    if (i != 10) {
      return tError;
    }
    ClassInterfacesType localClassInterfacesType = (ClassInterfacesType)paramType;
    ClassInfo localClassInfo;
    if ((this.clazz == null) || (localClassInterfacesType.clazz == null))
    {
      localClassInfo = null;
    }
    else
    {
      for (localClassInfo = this.clazz; (localClassInfo != null) && (!localClassInfo.superClassOf(localClassInterfacesType.clazz)); localClassInfo = localClassInfo.getSuperclass()) {}
      if (localClassInfo == ClassInfo.javaLangObject) {
        localClassInfo = null;
      }
    }
    if ((localClassInfo == this.clazz) && (implementsAllIfaces(localClassInterfacesType.clazz, localClassInterfacesType.ifaces, this.ifaces))) {
      return this;
    }
    if ((localClassInfo == localClassInterfacesType.clazz) && (implementsAllIfaces(this.clazz, this.ifaces, localClassInterfacesType.ifaces))) {
      return localClassInterfacesType;
    }
    Stack localStack = new Stack();
    int k;
    if (this.clazz != null) {
      for (localObject1 = this.clazz; localClassInfo != localObject1; localObject1 = ((ClassInfo)localObject1).getSuperclass())
      {
        ClassInfo[] arrayOfClassInfo1 = ((ClassInfo)localObject1).getInterfaces();
        for (k = 0; k < arrayOfClassInfo1.length; k++) {
          localStack.push(arrayOfClassInfo1[k]);
        }
      }
    }
    Object localObject1 = new Vector();
    for (int j = 0; j < this.ifaces.length; j++) {
      localStack.push(this.ifaces[j]);
    }
    while (!localStack.isEmpty())
    {
      localObject2 = (ClassInfo)localStack.pop();
      if (((localClassInfo == null) || (!((ClassInfo)localObject2).implementedBy(localClassInfo))) && (!((Vector)localObject1).contains(localObject2))) {
        if ((localClassInterfacesType.clazz != null) && (((ClassInfo)localObject2).implementedBy(localClassInterfacesType.clazz)))
        {
          ((Vector)localObject1).addElement(localObject2);
        }
        else
        {
          for (k = 0;; k++)
          {
            if (k >= localClassInterfacesType.ifaces.length) {
              break label421;
            }
            if (((ClassInfo)localObject2).implementedBy(localClassInterfacesType.ifaces[k]))
            {
              ((Vector)localObject1).addElement(localObject2);
              break;
            }
          }
          label421:
          ClassInfo[] arrayOfClassInfo2 = ((ClassInfo)localObject2).getInterfaces();
          for (int m = 0; m < arrayOfClassInfo2.length; m++) {
            localStack.push(arrayOfClassInfo2[m]);
          }
        }
      }
    }
    Object localObject2 = new ClassInfo[((Vector)localObject1).size()];
    ((Vector)localObject1).copyInto((Object[])localObject2);
    return create(localClassInfo, (ClassInfo[])localObject2);
  }
  
  public String getTypeSignature()
  {
    if (this.clazz != null) {
      return "L" + this.clazz.getName().replace('.', '/') + ";";
    }
    if (this.ifaces.length > 0) {
      return "L" + this.ifaces[0].getName().replace('.', '/') + ";";
    }
    return "Ljava/lang/Object;";
  }
  
  public Class getTypeClass()
    throws ClassNotFoundException
  {
    if (this.clazz != null) {
      return Class.forName(this.clazz.getName());
    }
    if (this.ifaces.length > 0) {
      return Class.forName(this.ifaces[0].getName());
    }
    return Class.forName("java.lang.Object");
  }
  
  public ClassInfo getClassInfo()
  {
    if (this.clazz != null) {
      return this.clazz;
    }
    if (this.ifaces.length > 0) {
      return this.ifaces[0];
    }
    return ClassInfo.javaLangObject;
  }
  
  public String toString()
  {
    if (this == tObject) {
      return "java.lang.Object";
    }
    if (this.ifaces.length == 0) {
      return this.clazz.getName();
    }
    if ((this.clazz == null) && (this.ifaces.length == 1)) {
      return this.ifaces[0].getName();
    }
    StringBuffer localStringBuffer = new StringBuffer("{");
    String str = "";
    if (this.clazz != null)
    {
      localStringBuffer = localStringBuffer.append(this.clazz.getName());
      str = ", ";
    }
    for (int i = 0; i < this.ifaces.length; i++)
    {
      localStringBuffer.append(str).append(this.ifaces[i].getName());
      str = ", ";
    }
    return "}";
  }
  
  public Type getCastHelper(Type paramType)
  {
    Type localType = paramType.getHint();
    switch (localType.getTypeCode())
    {
    case 9: 
      if ((this.clazz == null) && (implementsAllIfaces(null, ArrayType.arrayIfaces, this.ifaces))) {
        return null;
      }
      return tObject;
    case 10: 
      ClassInterfacesType localClassInterfacesType = (ClassInterfacesType)localType;
      if ((localClassInterfacesType.clazz == null) || (this.clazz == null) || (this.clazz.superClassOf(localClassInterfacesType.clazz)) || (localClassInterfacesType.clazz.superClassOf(this.clazz))) {
        return null;
      }
      for (ClassInfo localClassInfo = this.clazz.getSuperclass(); (localClassInfo != null) && (!localClassInfo.superClassOf(localClassInterfacesType.clazz)); localClassInfo = localClassInfo.getSuperclass()) {}
      return tClass(localClassInfo.getName());
    case 101: 
      return null;
    }
    return tObject;
  }
  
  public boolean isValidType()
  {
    return (this.ifaces.length == 0) || ((this.clazz == null) && (this.ifaces.length == 1));
  }
  
  public boolean isClassType()
  {
    return true;
  }
  
  public String getDefaultName()
  {
    ClassInfo localClassInfo;
    if (this.clazz != null) {
      localClassInfo = this.clazz;
    } else if (this.ifaces.length > 0) {
      localClassInfo = this.ifaces[0];
    } else {
      localClassInfo = ClassInfo.javaLangObject;
    }
    String str = localClassInfo.getName();
    int i = Math.max(str.lastIndexOf('.'), str.lastIndexOf('$'));
    if (i >= 0) {
      str = str.substring(i + 1);
    }
    if (Character.isUpperCase(str.charAt(0)))
    {
      str = str.toLowerCase();
      if (keywords.get(str) != null) {
        return "var_" + str;
      }
      return str;
    }
    return "var_" + str;
  }
  
  public int hashCode()
  {
    int i = this.clazz == null ? 0 : this.clazz.hashCode();
    for (int j = 0; j < this.ifaces.length; j++) {
      i ^= this.ifaces[j].hashCode();
    }
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (((paramObject instanceof Type)) && (((Type)paramObject).typecode == 10))
    {
      ClassInterfacesType localClassInterfacesType = (ClassInterfacesType)paramObject;
      if ((localClassInterfacesType.clazz == this.clazz) && (localClassInterfacesType.ifaces.length == this.ifaces.length))
      {
        label106:
        for (int i = 0; i < localClassInterfacesType.ifaces.length; i++)
        {
          for (int j = 0; j < this.ifaces.length; j++) {
            if (localClassInterfacesType.ifaces[i] == this.ifaces[j]) {
              break label106;
            }
          }
          return false;
        }
        return true;
      }
    }
    return false;
  }
  
  static
  {
    keywords.put("abstract", Boolean.TRUE);
    keywords.put("default", Boolean.TRUE);
    keywords.put("if", Boolean.TRUE);
    keywords.put("private", Boolean.TRUE);
    keywords.put("throw", Boolean.TRUE);
    keywords.put("boolean", Boolean.TRUE);
    keywords.put("do", Boolean.TRUE);
    keywords.put("implements", Boolean.TRUE);
    keywords.put("protected", Boolean.TRUE);
    keywords.put("throws", Boolean.TRUE);
    keywords.put("break", Boolean.TRUE);
    keywords.put("double", Boolean.TRUE);
    keywords.put("import", Boolean.TRUE);
    keywords.put("public", Boolean.TRUE);
    keywords.put("transient", Boolean.TRUE);
    keywords.put("byte", Boolean.TRUE);
    keywords.put("else", Boolean.TRUE);
    keywords.put("instanceof", Boolean.TRUE);
    keywords.put("return", Boolean.TRUE);
    keywords.put("try", Boolean.TRUE);
    keywords.put("case", Boolean.TRUE);
    keywords.put("extends", Boolean.TRUE);
    keywords.put("int", Boolean.TRUE);
    keywords.put("short", Boolean.TRUE);
    keywords.put("void", Boolean.TRUE);
    keywords.put("catch", Boolean.TRUE);
    keywords.put("final", Boolean.TRUE);
    keywords.put("interface", Boolean.TRUE);
    keywords.put("static", Boolean.TRUE);
    keywords.put("volatile", Boolean.TRUE);
    keywords.put("char", Boolean.TRUE);
    keywords.put("finally", Boolean.TRUE);
    keywords.put("long", Boolean.TRUE);
    keywords.put("super", Boolean.TRUE);
    keywords.put("while", Boolean.TRUE);
    keywords.put("class", Boolean.TRUE);
    keywords.put("float", Boolean.TRUE);
    keywords.put("native", Boolean.TRUE);
    keywords.put("switch", Boolean.TRUE);
    keywords.put("const", Boolean.TRUE);
    keywords.put("for", Boolean.TRUE);
    keywords.put("new", Boolean.TRUE);
    keywords.put("synchronized", Boolean.TRUE);
    keywords.put("continue", Boolean.TRUE);
    keywords.put("goto", Boolean.TRUE);
    keywords.put("package", Boolean.TRUE);
    keywords.put("this", Boolean.TRUE);
    keywords.put("strictfp", Boolean.TRUE);
    keywords.put("null", Boolean.TRUE);
    keywords.put("true", Boolean.TRUE);
    keywords.put("false", Boolean.TRUE);
  }
}


