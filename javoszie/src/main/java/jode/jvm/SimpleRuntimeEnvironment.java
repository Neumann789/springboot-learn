package jode.jvm;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;

public class SimpleRuntimeEnvironment
  implements RuntimeEnvironment
{
  public static Object fromReflectType(String paramString, Object paramObject)
  {
    switch (paramString.charAt(0))
    {
    case 'Z': 
      return new Integer(((Boolean)paramObject).booleanValue() ? 1 : 0);
    case 'B': 
    case 'S': 
      return new Integer(((Number)paramObject).intValue());
    case 'C': 
      return new Integer(((Character)paramObject).charValue());
    }
    return paramObject;
  }
  
  public static Object toReflectType(String paramString, Object paramObject)
  {
    switch (paramString.charAt(0))
    {
    case 'Z': 
      return new Boolean(((Integer)paramObject).intValue() != 0);
    case 'B': 
      return new Byte(((Integer)paramObject).byteValue());
    case 'S': 
      return new Short(((Integer)paramObject).shortValue());
    case 'C': 
      return new Character((char)((Integer)paramObject).intValue());
    }
    return paramObject;
  }
  
  public Object getField(Reference paramReference, Object paramObject)
    throws InterpreterException
  {
    Field localField;
    try
    {
      Class localClass = TypeSignature.getClass(paramReference.getClazz());
      try
      {
        localField = localClass.getField(paramReference.getName());
      }
      catch (NoSuchFieldException localNoSuchFieldException2)
      {
        localField = localClass.getDeclaredField(paramReference.getName());
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new InterpreterException(paramReference + ": Class not found");
    }
    catch (NoSuchFieldException localNoSuchFieldException1)
    {
      throw new InterpreterException("Constructor " + paramReference + " not found");
    }
    catch (SecurityException localSecurityException)
    {
      throw new InterpreterException(paramReference + ": Security exception");
    }
    try
    {
      return fromReflectType(paramReference.getType(), localField.get(paramObject));
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new InterpreterException("Field " + paramReference + " not accessible");
    }
  }
  
  public void putField(Reference paramReference, Object paramObject1, Object paramObject2)
    throws InterpreterException
  {
    Field localField;
    try
    {
      Class localClass = TypeSignature.getClass(paramReference.getClazz());
      try
      {
        localField = localClass.getField(paramReference.getName());
      }
      catch (NoSuchFieldException localNoSuchFieldException2)
      {
        localField = localClass.getDeclaredField(paramReference.getName());
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new InterpreterException(paramReference + ": Class not found");
    }
    catch (NoSuchFieldException localNoSuchFieldException1)
    {
      throw new InterpreterException("Constructor " + paramReference + " not found");
    }
    catch (SecurityException localSecurityException)
    {
      throw new InterpreterException(paramReference + ": Security exception");
    }
    try
    {
      localField.set(paramObject1, toReflectType(paramReference.getType(), paramObject2));
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new InterpreterException("Field " + paramReference + " not accessible");
    }
  }
  
  public Object invokeConstructor(Reference paramReference, Object[] paramArrayOfObject)
    throws InterpreterException, InvocationTargetException
  {
    Constructor localConstructor;
    try
    {
      String[] arrayOfString = TypeSignature.getParameterTypes(paramReference.getType());
      Class localClass = TypeSignature.getClass(paramReference.getClazz());
      Class[] arrayOfClass = new Class[arrayOfString.length];
      for (int i = 0; i < arrayOfString.length; i++)
      {
        paramArrayOfObject[i] = toReflectType(arrayOfString[i], paramArrayOfObject[i]);
        arrayOfClass[i] = TypeSignature.getClass(arrayOfString[i]);
      }
      try
      {
        localConstructor = localClass.getConstructor(arrayOfClass);
      }
      catch (NoSuchMethodException localNoSuchMethodException2)
      {
        localConstructor = localClass.getDeclaredConstructor(arrayOfClass);
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new InterpreterException(paramReference + ": Class not found");
    }
    catch (NoSuchMethodException localNoSuchMethodException1)
    {
      throw new InterpreterException("Constructor " + paramReference + " not found");
    }
    catch (SecurityException localSecurityException)
    {
      throw new InterpreterException(paramReference + ": Security exception");
    }
    try
    {
      return localConstructor.newInstance(paramArrayOfObject);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new InterpreterException("Constructor " + paramReference + " not accessible");
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new InterpreterException("InstantiationException in " + paramReference + ".");
    }
  }
  
  public Object invokeMethod(Reference paramReference, boolean paramBoolean, Object paramObject, Object[] paramArrayOfObject)
    throws InterpreterException, InvocationTargetException
  {
    if ((!paramBoolean) && (paramObject != null)) {
      throw new InterpreterException("Can't invoke nonvirtual Method " + paramReference + ".");
    }
    Method localMethod;
    try
    {
      String[] arrayOfString = TypeSignature.getParameterTypes(paramReference.getType());
      Class localClass = TypeSignature.getClass(paramReference.getClazz());
      Class[] arrayOfClass = new Class[arrayOfString.length];
      for (int i = 0; i < arrayOfString.length; i++)
      {
        paramArrayOfObject[i] = toReflectType(arrayOfString[i], paramArrayOfObject[i]);
        arrayOfClass[i] = TypeSignature.getClass(arrayOfString[i]);
      }
      try
      {
        localMethod = localClass.getMethod(paramReference.getName(), arrayOfClass);
      }
      catch (NoSuchMethodException localNoSuchMethodException2)
      {
        localMethod = localClass.getDeclaredMethod(paramReference.getName(), arrayOfClass);
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new InterpreterException(paramReference + ": Class not found");
    }
    catch (NoSuchMethodException localNoSuchMethodException1)
    {
      throw new InterpreterException("Method " + paramReference + " not found");
    }
    catch (SecurityException localSecurityException)
    {
      throw new InterpreterException(paramReference + ": Security exception");
    }
    String str = TypeSignature.getReturnType(paramReference.getType());
    try
    {
      return fromReflectType(str, localMethod.invoke(paramObject, paramArrayOfObject));
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new InterpreterException("Method " + paramReference + " not accessible");
    }
  }
  
  public boolean instanceOf(Object paramObject, String paramString)
    throws InterpreterException
  {
    Class localClass;
    try
    {
      localClass = Class.forName(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new InterpreterException("Class " + localClassNotFoundException.getMessage() + " not found");
    }
    return (paramObject != null) && (!localClass.isInstance(paramObject));
  }
  
  public Object newArray(String paramString, int[] paramArrayOfInt)
    throws InterpreterException, NegativeArraySizeException
  {
    Class localClass;
    try
    {
      localClass = TypeSignature.getClass(paramString.substring(paramArrayOfInt.length));
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new InterpreterException("Class " + localClassNotFoundException.getMessage() + " not found");
    }
    return Array.newInstance(localClass, paramArrayOfInt);
  }
  
  public void enterMonitor(Object paramObject)
    throws InterpreterException
  {
    throw new InterpreterException("monitor not implemented");
  }
  
  public void exitMonitor(Object paramObject)
    throws InterpreterException
  {
    throw new InterpreterException("monitor not implemented");
  }
}


