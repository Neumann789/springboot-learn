/* SimpleRuntimeEnvironment - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.jvm;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;

public class SimpleRuntimeEnvironment implements RuntimeEnvironment
{
    public static Object fromReflectType(String string, Object object) {
	switch (string.charAt(0)) {
	case 'Z':
	    PUSH new Integer;
	label_990:
	    {
		DUP
		if (!((Boolean) object).booleanValue())
		    PUSH false;
		else
		    PUSH true;
		break label_990;
	    }
	    ((UNCONSTRUCTED)POP).Integer(POP);
	    return POP;
	case 'B':
	case 'S':
	    return new Integer(((Number) object).intValue());
	case 'C':
	    return new Integer(((Character) object).charValue());
	default:
	    return object;
	}
    }
    
    public static Object toReflectType(String string, Object object) {
	switch (string.charAt(0)) {
	case 'Z':
	    PUSH new Boolean;
	label_991:
	    {
		DUP
		if (((Integer) object).intValue() == 0)
		    PUSH false;
		else
		    PUSH true;
		break label_991;
	    }
	    ((UNCONSTRUCTED)POP).Boolean(POP);
	    return POP;
	case 'B':
	    return new Byte(((Integer) object).byteValue());
	case 'S':
	    return new Short(((Integer) object).shortValue());
	case 'C':
	    return new Character((char) ((Integer) object).intValue());
	default:
	    return object;
	}
    }
    
    public Object getField(Reference reference, Object object)
	throws InterpreterException {
	Field field;
	try {
	    Class var_class = TypeSignature.getClass(reference.getClazz());
	    try {
		field = var_class.getField(reference.getName());
	    } catch (NoSuchFieldException PUSH) {
		Object object_0_ = POP;
		field = var_class.getDeclaredField(reference.getName());
	    }
	} catch (ClassNotFoundException PUSH) {
	    Object object_1_ = POP;
	    throw new InterpreterException(reference + ": Class not found");
	} catch (NoSuchFieldException PUSH) {
	    Object object_2_ = POP;
	    throw new InterpreterException("Constructor " + reference
					   + " not found");
	} catch (SecurityException PUSH) {
	    Object object_3_ = POP;
	    throw new InterpreterException(reference + ": Security exception");
	}
	try {
	    return fromReflectType(reference.getType(), field.get(object));
	} catch (IllegalAccessException PUSH) {
	    Object object_4_ = POP;
	    throw new InterpreterException("Field " + reference
					   + " not accessible");
	}
    }
    
    public void putField(Reference reference, Object object, Object object_5_)
	throws InterpreterException {
	Field field;
	try {
	    Class var_class = TypeSignature.getClass(reference.getClazz());
	    try {
		field = var_class.getField(reference.getName());
	    } catch (NoSuchFieldException PUSH) {
		Object object_6_ = POP;
		field = var_class.getDeclaredField(reference.getName());
	    }
	} catch (ClassNotFoundException PUSH) {
	    Object object_7_ = POP;
	    throw new InterpreterException(reference + ": Class not found");
	} catch (NoSuchFieldException PUSH) {
	    Object object_8_ = POP;
	    throw new InterpreterException("Constructor " + reference
					   + " not found");
	} catch (SecurityException PUSH) {
	    Object object_9_ = POP;
	    throw new InterpreterException(reference + ": Security exception");
	}
	try {
	    field.set(object, toReflectType(reference.getType(), object_5_));
	} catch (IllegalAccessException PUSH) {
	    Object object_10_ = POP;
	    throw new InterpreterException("Field " + reference
					   + " not accessible");
	}
    }
    
    public Object invokeConstructor(Reference reference, Object[] objects)
	throws InterpreterException, InvocationTargetException {
	String[] strings
	    = TypeSignature.getParameterTypes(reference.getType());
	Class var_class = TypeSignature.getClass(reference.getClazz());
	Class[] var_classes = new Class[strings.length];
	int i = 0;
	GOTO flow_1_69_
    flow_1_69_:
	int i;
	String[] strings;
	Class[] var_classes;
	if (i >= strings.length) {
	    Constructor constructor;
	    Class var_class;
	    try {
		constructor = var_class.getConstructor(var_classes);
		GOTO flow_8_70_
	    } catch (NoSuchMethodException PUSH) {
		Object object = POP;
		constructor = var_class.getDeclaredConstructor(var_classes);
		GOTO flow_8_70_
	    }
	}
	objects[i] = toReflectType(strings[i], objects[i]);
	var_classes[i] = TypeSignature.getClass(strings[i]);
	i++;
	GOTO flow_1_69_
    flow_5_71_:
	Object object = POP;
	throw new InterpreterException(reference + ": Class not found");
    flow_6_72_:
	Object object = POP;
	throw new InterpreterException("Constructor " + reference
				       + " not found");
    flow_7_73_:
	Object object = POP;
	throw new InterpreterException(reference + ": Security exception");
    flow_8_70_:
	Constructor constructor;
	return constructor.newInstance(objects);
	GOTO END_OF_METHOD
    flow_9_74_:
	Object object = POP;
	throw new InterpreterException("Constructor " + reference
				       + " not accessible");
    flow_10_75_:
	Object object = POP;
	throw new InterpreterException("InstantiationException in " + reference
				       + ".");
    }
    
    public Object invokeMethod(Reference reference, boolean bool,
			       Object object, Object[] objects)
	throws InterpreterException, InvocationTargetException {
	if (bool || object == null) {
	    try {
		String[] strings
		    = TypeSignature.getParameterTypes(reference.getType());
		Class var_class = TypeSignature.getClass(reference.getClazz());
		Class[] var_classes = new Class[strings.length];
		int i = 0;
		GOTO flow_4_76_
	    } catch (ClassNotFoundException PUSH) {
		GOTO flow_8_77_
	    } catch (NoSuchMethodException PUSH) {
		GOTO flow_9_78_
	    } catch (SecurityException PUSH) {
		GOTO flow_10_79_
	    }
	}
	throw new InterpreterException("Can't invoke nonvirtual Method "
				       + reference + ".");
    flow_4_76_:
	int i;
	String[] strings;
	Class[] var_classes;
	if (i >= strings.length) {
	    Method method;
	    Class var_class;
	    try {
		method = var_class.getMethod(reference.getName(), var_classes);
		GOTO flow_11_80_
	    } catch (NoSuchMethodException PUSH) {
		Object object_11_ = POP;
		method = var_class.getDeclaredMethod(reference.getName(),
						     var_classes);
		GOTO flow_11_80_
	    }
	}
	objects[i] = toReflectType(strings[i], objects[i]);
	var_classes[i] = TypeSignature.getClass(strings[i]);
	i++;
	GOTO flow_4_76_
    flow_8_77_:
	Object object_12_ = POP;
	throw new InterpreterException(reference + ": Class not found");
    flow_9_78_:
	Object object_13_ = POP;
	throw new InterpreterException("Method " + reference + " not found");
    flow_10_79_:
	Object object_14_ = POP;
	throw new InterpreterException(reference + ": Security exception");
    flow_11_80_:
	String string = TypeSignature.getReturnType(reference.getType());
	GOTO flow_12_81_
    flow_12_81_:
	String string;
	Method method;
	return fromReflectType(string, method.invoke(object, objects));
	GOTO END_OF_METHOD
    flow_13_82_:
	Object object_15_ = POP;
	throw new InterpreterException("Method " + reference
				       + " not accessible");
    }
    
    public boolean instanceOf(Object object, String string)
	throws InterpreterException {
    label_993:
	{
	label_992:
	    {
		Class var_class;
		try {
		    var_class = Class.forName(string);
		    if (object == null)
			break label_992;
		} catch (ClassNotFoundException PUSH) {
		    ClassNotFoundException classnotfoundexception = POP;
		    throw new InterpreterException("Class "
						   + classnotfoundexception
							 .getMessage()
						   + " not found");
		}
		if (!var_class.isInstance(object)) {
		    PUSH true;
		    break label_993;
		}
	    }
	    PUSH false;
	}
	return POP;
    }
    
    public Object newArray(String string, int[] is)
	throws InterpreterException, NegativeArraySizeException {
	try {
	    Class var_class
		= TypeSignature.getClass(string.substring(is.length));
	    return Array.newInstance(var_class, is);
	} catch (ClassNotFoundException PUSH) {
	    ClassNotFoundException classnotfoundexception = POP;
	    throw new InterpreterException("Class "
					   + classnotfoundexception
						 .getMessage()
					   + " not found");
	}
    }
    
    public void enterMonitor(Object object) throws InterpreterException {
	throw new InterpreterException("monitor not implemented");
    }
    
    public void exitMonitor(Object object) throws InterpreterException {
	throw new InterpreterException("monitor not implemented");
    }
}
