/* RuntimeEnvironment - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.jvm;
import java.lang.reflect.InvocationTargetException;

import jode.bytecode.Reference;

public interface RuntimeEnvironment
{
    public Object getField(Reference reference, Object object)
	throws InterpreterException;
    
    public void putField(Reference reference, Object object, Object object_0_)
	throws InterpreterException;
    
    public Object invokeMethod(Reference reference, boolean bool,
			       Object object, Object[] objects)
	throws InterpreterException, InvocationTargetException;
    
    public Object invokeConstructor(Reference reference, Object[] objects)
	throws InterpreterException, InvocationTargetException;
    
    public boolean instanceOf(Object object, String string)
	throws InterpreterException;
    
    public Object newArray(String string, int[] is)
	throws InterpreterException;
    
    public void enterMonitor(Object object) throws InterpreterException;
    
    public void exitMonitor(Object object) throws InterpreterException;
}
