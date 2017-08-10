package jode.bytecode;

import jode.AssertError;

public class TypeSignature
{
  private static final StringBuffer appendSignature(StringBuffer paramStringBuffer, Class paramClass)
  {
    if (paramClass.isPrimitive())
    {
      if (paramClass == Boolean.TYPE) {
        return paramStringBuffer.append('Z');
      }
      if (paramClass == Byte.TYPE) {
        return paramStringBuffer.append('B');
      }
      if (paramClass == Character.TYPE) {
        return paramStringBuffer.append('C');
      }
      if (paramClass == Short.TYPE) {
        return paramStringBuffer.append('S');
      }
      if (paramClass == Integer.TYPE) {
        return paramStringBuffer.append('I');
      }
      if (paramClass == Long.TYPE) {
        return paramStringBuffer.append('J');
      }
      if (paramClass == Float.TYPE) {
        return paramStringBuffer.append('F');
      }
      if (paramClass == Double.TYPE) {
        return paramStringBuffer.append('D');
      }
      if (paramClass == Void.TYPE) {
        return paramStringBuffer.append('V');
      }
      throw new AssertError("Unknown primitive type: " + paramClass);
    }
    if (paramClass.isArray()) {
      return appendSignature(paramStringBuffer.append('['), paramClass.getComponentType());
    }
    return paramStringBuffer.append('L').append(paramClass.getName().replace('.', '/')).append(';');
  }
  
  public static String getSignature(Class paramClass)
  {
    return appendSignature(new StringBuffer(), paramClass).toString();
  }
  
  public static String getSignature(Class[] paramArrayOfClass, Class paramClass)
  {
    StringBuffer localStringBuffer = new StringBuffer("(");
    for (int i = 0; i < paramArrayOfClass.length; i++) {
      appendSignature(localStringBuffer, paramArrayOfClass[i]);
    }
    return appendSignature(localStringBuffer.append(')'), paramClass).toString();
  }
  
  public static Class getClass(String paramString)
    throws ClassNotFoundException
  {
    switch (paramString.charAt(0))
    {
    case 'Z': 
      return Boolean.TYPE;
    case 'B': 
      return Byte.TYPE;
    case 'C': 
      return Character.TYPE;
    case 'S': 
      return Short.TYPE;
    case 'I': 
      return Integer.TYPE;
    case 'F': 
      return Float.TYPE;
    case 'J': 
      return Long.TYPE;
    case 'D': 
      return Double.TYPE;
    case 'V': 
      return Void.TYPE;
    case 'L': 
      paramString = paramString.substring(1, paramString.length() - 1).replace('/', '.');
    case '[': 
      return Class.forName(paramString);
    }
    throw new IllegalArgumentException(paramString);
  }
  
  private static boolean usingTwoSlots(char paramChar)
  {
    return "JD".indexOf(paramChar) >= 0;
  }
  
  public static int getTypeSize(String paramString)
  {
    return usingTwoSlots(paramString.charAt(0)) ? 2 : 1;
  }
  
  public static String getElementType(String paramString)
  {
    if (paramString.charAt(0) != '[') {
      throw new IllegalArgumentException();
    }
    return paramString.substring(1);
  }
  
  public static ClassInfo getClassInfo(String paramString)
  {
    if (paramString.charAt(0) != 'L') {
      throw new IllegalArgumentException();
    }
    return ClassInfo.forName(paramString.substring(1, paramString.length() - 1).replace('/', '.'));
  }
  
  public static int skipType(String paramString, int paramInt)
  {
    for (int i = paramString.charAt(paramInt++); i == 91; i = paramString.charAt(paramInt++)) {}
    if (i == 76) {
      return paramString.indexOf(';', paramInt) + 1;
    }
    return paramInt;
  }
  
  public static int getArgumentSize(String paramString)
  {
    int i = 0;
    int j = 1;
    for (;;)
    {
      char c = paramString.charAt(j);
      if (c == ')') {
        return i;
      }
      j = skipType(paramString, j);
      if (usingTwoSlots(c)) {
        i += 2;
      } else {
        i++;
      }
    }
  }
  
  public static int getReturnSize(String paramString)
  {
    int i = paramString.length();
    if (paramString.charAt(i - 2) == ')')
    {
      char c = paramString.charAt(i - 1);
      return usingTwoSlots(c) ? 2 : c == 'V' ? 0 : 1;
    }
    return 1;
  }
  
  public static String[] getParameterTypes(String paramString)
  {
    int i = 1;
    for (int j = 0; paramString.charAt(i) != ')'; j++) {
      i = skipType(paramString, i);
    }
    String[] arrayOfString = new String[j];
    i = 1;
    for (int k = 0; k < j; k++)
    {
      int m = i;
      i = skipType(paramString, i);
      arrayOfString[k] = paramString.substring(m, i);
    }
    return arrayOfString;
  }
  
  public static String getReturnType(String paramString)
  {
    return paramString.substring(paramString.lastIndexOf(')') + 1);
  }
  
  private static int checkClassName(String paramString, int paramInt)
    throws IllegalArgumentException, StringIndexOutOfBoundsException
  {
    for (;;)
    {
      char c = paramString.charAt(paramInt++);
      if (c == ';') {
        return paramInt;
      }
      if ((c != '/') && (!Character.isJavaIdentifierPart(c))) {
        throw new IllegalArgumentException("Illegal java class name: " + paramString);
      }
    }
  }
  
  private static int checkTypeSig(String paramString, int paramInt)
  {
    for (int i = paramString.charAt(paramInt++); i == 91; i = paramString.charAt(paramInt++)) {}
    if (i == 76) {
      paramInt = checkClassName(paramString, paramInt);
    } else if ("ZBSCIJFD".indexOf(i) == -1) {
      throw new IllegalArgumentException("Type sig error: " + paramString);
    }
    return paramInt;
  }
  
  public static void checkTypeSig(String paramString)
    throws IllegalArgumentException
  {
    try
    {
      if (checkTypeSig(paramString, 0) != paramString.length()) {
        throw new IllegalArgumentException("Type sig too long: " + paramString);
      }
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
    {
      throw new IllegalArgumentException("Incomplete type sig: " + paramString);
    }
  }
  
  public static void checkMethodTypeSig(String paramString)
    throws IllegalArgumentException
  {
    try
    {
      if (paramString.charAt(0) != '(') {
        throw new IllegalArgumentException("No method signature: " + paramString);
      }
      for (int i = 1; paramString.charAt(i) != ')'; i = checkTypeSig(paramString, i)) {}
      i++;
      if (paramString.charAt(i) == 'V') {
        i++;
      } else {
        i = checkTypeSig(paramString, i);
      }
      if (i != paramString.length()) {
        throw new IllegalArgumentException("Type sig too long: " + paramString);
      }
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
    {
      throw new IllegalArgumentException("Incomplete type sig: " + paramString);
    }
  }
}


