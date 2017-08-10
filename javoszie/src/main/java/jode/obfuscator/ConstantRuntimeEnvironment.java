package jode.obfuscator;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.MethodInfo;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;
import jode.jvm.Interpreter;
import jode.jvm.InterpreterException;
import jode.jvm.SimpleRuntimeEnvironment;

public class ConstantRuntimeEnvironment
  extends SimpleRuntimeEnvironment
{
  static Set whiteList = new HashSet();
  private Interpreter interpreter = new Interpreter(this);
  private Identifier currentFieldListener;
  
  static void addWhite(Reference paramReference)
  {
    whiteList.add(paramReference);
  }
  
  public static boolean isWhite(Reference paramReference)
  {
    return whiteList.contains(paramReference);
  }
  
  public static boolean isWhite(String paramString)
  {
    return (paramString.length() == 1) || (whiteList.contains(paramString));
  }
  
  public void setFieldListener(Identifier paramIdentifier)
  {
    this.currentFieldListener = paramIdentifier;
  }
  
  public static Object getDefaultValue(String paramString)
  {
    switch (paramString.charAt(0))
    {
    case 'B': 
    case 'C': 
    case 'I': 
    case 'S': 
    case 'Z': 
      return new Integer(0);
    case 'J': 
      return new Long(0L);
    case 'D': 
      return new Double(0.0D);
    case 'F': 
      return new Float(0.0F);
    }
    return null;
  }
  
  public Object getField(Reference paramReference, Object paramObject)
    throws InterpreterException
  {
    if (isWhite(paramReference)) {
      return super.getField(paramReference, paramObject);
    }
    FieldIdentifier localFieldIdentifier = (FieldIdentifier)Main.getClassBundle().getIdentifier(paramReference);
    if ((localFieldIdentifier != null) && (!localFieldIdentifier.isNotConstant()))
    {
      Object localObject = localFieldIdentifier.getConstant();
      if (this.currentFieldListener != null) {
        localFieldIdentifier.addFieldListener(this.currentFieldListener);
      }
      if (localObject == null) {
        localObject = getDefaultValue(paramReference.getType());
      }
      return localObject;
    }
    throw new InterpreterException("Field " + paramReference + " not constant");
  }
  
  public void putField(Reference paramReference, Object paramObject1, Object paramObject2)
    throws InterpreterException
  {
    throw new InterpreterException("Modifying Field " + paramReference + ".");
  }
  
  public Object invokeConstructor(Reference paramReference, Object[] paramArrayOfObject)
    throws InterpreterException, InvocationTargetException
  {
    if (isWhite(paramReference)) {
      return super.invokeConstructor(paramReference, paramArrayOfObject);
    }
    throw new InterpreterException("Creating new Object " + paramReference + ".");
  }
  
  public Object invokeMethod(Reference paramReference, boolean paramBoolean, Object paramObject, Object[] paramArrayOfObject)
    throws InterpreterException, InvocationTargetException
  {
    if (isWhite(paramReference)) {
      return super.invokeMethod(paramReference, paramBoolean, paramObject, paramArrayOfObject);
    }
    MethodIdentifier localMethodIdentifier = (MethodIdentifier)Main.getClassBundle().getIdentifier(paramReference);
    if (localMethodIdentifier != null)
    {
      BytecodeInfo localBytecodeInfo = localMethodIdentifier.info.getBytecode();
      if (localBytecodeInfo != null) {
        return this.interpreter.interpretMethod(localBytecodeInfo, paramObject, paramArrayOfObject);
      }
    }
    throw new InterpreterException("Invoking library method " + paramReference + ".");
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
    return (paramObject != null) && (localClass.isInstance(paramObject));
  }
  
  public Object newArray(String paramString, int[] paramArrayOfInt)
    throws InterpreterException, NegativeArraySizeException
  {
    if (paramString.length() == paramArrayOfInt.length + 1)
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
    throw new InterpreterException("Creating object array.");
  }
  
  static
  {
    addWhite(Reference.getReference("Ljava/lang/String;", "toCharArray", "()[C"));
    addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "<init>", "(Ljava/lang/String;)V"));
    addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "<init>", "()V"));
    addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "append", "(Ljava/lang/String;)Ljava/lang/StringBuffer;"));
    addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "append", "(C)Ljava/lang/StringBuffer;"));
    addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "append", "(B)Ljava/lang/StringBuffer;"));
    addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "append", "(S)Ljava/lang/StringBuffer;"));
    addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "append", "(Z)Ljava/lang/StringBuffer;"));
    addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "append", "(F)Ljava/lang/StringBuffer;"));
    addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "append", "(I)Ljava/lang/StringBuffer;"));
    addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "append", "(J)Ljava/lang/StringBuffer;"));
    addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "append", "(D)Ljava/lang/StringBuffer;"));
    addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "toString", "()Ljava/lang/String;"));
    addWhite(Reference.getReference("Ljava/lang/String;", "<init>", "()V"));
    addWhite(Reference.getReference("Ljava/lang/String;", "<init>", "([C)V"));
    addWhite(Reference.getReference("Ljava/lang/String;", "<init>", "([CII)V"));
    addWhite(Reference.getReference("Ljava/lang/String;", "<init>", "(Ljava/lang/String;)V"));
    addWhite(Reference.getReference("Ljava/lang/String;", "<init>", "(Ljava/lang/StringBuffer;)V"));
    addWhite(Reference.getReference("Ljava/lang/String;", "length", "()I"));
    addWhite(Reference.getReference("Ljava/lang/String;", "replace", "(CC)Ljava/lang/String;"));
    addWhite(Reference.getReference("Ljava/lang/String;", "valueOf", "(Z)Ljava/lang/String;"));
    addWhite(Reference.getReference("Ljava/lang/String;", "valueOf", "(B)Ljava/lang/String;"));
    addWhite(Reference.getReference("Ljava/lang/String;", "valueOf", "(S)Ljava/lang/String;"));
    addWhite(Reference.getReference("Ljava/lang/String;", "valueOf", "(C)Ljava/lang/String;"));
    addWhite(Reference.getReference("Ljava/lang/String;", "valueOf", "(D)Ljava/lang/String;"));
    addWhite(Reference.getReference("Ljava/lang/String;", "valueOf", "(F)Ljava/lang/String;"));
    addWhite(Reference.getReference("Ljava/lang/String;", "valueOf", "(I)Ljava/lang/String;"));
    addWhite(Reference.getReference("Ljava/lang/String;", "valueOf", "(J)Ljava/lang/String;"));
    addWhite(Reference.getReference("Ljava/lang/String;", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;"));
    addWhite(Reference.getReference("Ljava/lang/String;", "substring", "(I)Ljava/lang/String;"));
    addWhite(Reference.getReference("Ljava/lang/String;", "substring", "(II)Ljava/lang/String;"));
    addWhite(Reference.getReference("Ljava.lang/reflect/Modifier;", "toString", "(I)Ljava/lang/String;"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "abs", "(D)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "abs", "(F)F"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "abs", "(I)I"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "abs", "(J)J"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "acos", "(D)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "asin", "(D)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "atan", "(D)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "atan2", "(D)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "ceil", "(D)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "cos", "(D)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "exp", "(D)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "floor", "(D)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "IEEEremainder", "(DD)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "log", "(D)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "max", "(DD)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "max", "(FF)F"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "max", "(II)I"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "max", "(JJ)J"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "min", "(DD)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "min", "(FF)F"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "min", "(II)I"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "min", "(JJ)J"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "pow", "(DD)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "rint", "(D)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "round", "(D)J"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "round", "(F)I"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "sin", "(D)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "sqrt", "(D)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "tan", "(D)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "toDegrees", "(D)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "toRadians", "(D)D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "E", "D"));
    addWhite(Reference.getReference("Ljava/lang/Math;", "PI", "D"));
    whiteList.add("Ljava/lang/String;");
  }
}


