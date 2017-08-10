package jode.jvm;

import java.lang.reflect.InvocationTargetException;
import jode.bytecode.Reference;

public abstract interface RuntimeEnvironment
{
  public abstract Object getField(Reference paramReference, Object paramObject)
    throws InterpreterException;
  
  public abstract void putField(Reference paramReference, Object paramObject1, Object paramObject2)
    throws InterpreterException;
  
  public abstract Object invokeMethod(Reference paramReference, boolean paramBoolean, Object paramObject, Object[] paramArrayOfObject)
    throws InterpreterException, InvocationTargetException;
  
  public abstract Object invokeConstructor(Reference paramReference, Object[] paramArrayOfObject)
    throws InterpreterException, InvocationTargetException;
  
  public abstract boolean instanceOf(Object paramObject, String paramString)
    throws InterpreterException;
  
  public abstract Object newArray(String paramString, int[] paramArrayOfInt)
    throws InterpreterException;
  
  public abstract void enterMonitor(Object paramObject)
    throws InterpreterException;
  
  public abstract void exitMonitor(Object paramObject)
    throws InterpreterException;
}


