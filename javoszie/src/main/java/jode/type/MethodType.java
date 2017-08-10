package jode.type;

public class MethodType
  extends Type
{
  final String signature;
  final Type[] parameterTypes;
  final Type returnType;
  
  public MethodType(String paramString)
  {
    super(12);
    this.signature = paramString;
    int i = 1;
    int j = 0;
    while (paramString.charAt(i) != ')')
    {
      j++;
      while (paramString.charAt(i) == '[') {
        i++;
      }
      if (paramString.charAt(i) == 'L') {
        i = paramString.indexOf(';', i);
      }
      i++;
    }
    this.parameterTypes = new Type[j];
    i = 1;
    j = 0;
    while (paramString.charAt(i) != ')')
    {
      int k = i;
      while (paramString.charAt(i) == '[') {
        i++;
      }
      if (paramString.charAt(i) == 'L') {
        i = paramString.indexOf(';', i);
      }
      i++;
      this.parameterTypes[(j++)] = Type.tType(paramString.substring(k, i));
    }
    this.returnType = Type.tType(paramString.substring(i + 1));
  }
  
  public final int stackSize()
  {
    int i = this.returnType.stackSize();
    for (int j = 0; j < this.parameterTypes.length; j++) {
      i -= this.parameterTypes[j].stackSize();
    }
    return i;
  }
  
  public Type[] getParameterTypes()
  {
    return this.parameterTypes;
  }
  
  public Class[] getParameterClasses()
    throws ClassNotFoundException
  {
    Class[] arrayOfClass = new Class[this.parameterTypes.length];
    int i = arrayOfClass.length;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      arrayOfClass[i] = this.parameterTypes[i].getTypeClass();
    }
    return arrayOfClass;
  }
  
  public Type getReturnType()
  {
    return this.returnType;
  }
  
  public Class getReturnClass()
    throws ClassNotFoundException
  {
    return this.returnType.getTypeClass();
  }
  
  public String getTypeSignature()
  {
    return this.signature;
  }
  
  public String toString()
  {
    return this.signature;
  }
  
  public boolean equals(Object paramObject)
  {
    MethodType localMethodType;
    return ((paramObject instanceof MethodType)) && (this.signature.equals((localMethodType = (MethodType)paramObject).signature));
  }
}


