/* MethodFlag - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.fusesource.hawtjni.runtime;

public final class MethodFlag extends Enum
{
    public static final MethodFlag METHOD_SKIP
	= new MethodFlag("METHOD_SKIP", 0);
    public static final MethodFlag DYNAMIC = new MethodFlag("DYNAMIC", 1);
    public static final MethodFlag CONSTANT_GETTER
	= new MethodFlag("CONSTANT_GETTER", 2);
    public static final MethodFlag CAST = new MethodFlag("CAST", 3);
    public static final MethodFlag JNI = new MethodFlag("JNI", 4);
    public static final MethodFlag ADDRESS = new MethodFlag("ADDRESS", 5);
    public static final MethodFlag CPP_METHOD
	= new MethodFlag("CPP_METHOD", 6);
    public static final MethodFlag CPP_NEW = new MethodFlag("CPP_NEW", 7);
    public static final MethodFlag CPP_DELETE
	= new MethodFlag("CPP_DELETE", 8);
    public static final MethodFlag CS_NEW = new MethodFlag("CS_NEW", 9);
    public static final MethodFlag CS_OBJECT = new MethodFlag("CS_OBJECT", 10);
    public static final MethodFlag SETTER = new MethodFlag("SETTER", 11);
    public static final MethodFlag GETTER = new MethodFlag("GETTER", 12);
    public static final MethodFlag ADDER = new MethodFlag("ADDER", 13);
    public static final MethodFlag POINTER_RETURN
	= new MethodFlag("POINTER_RETURN", 14);
    public static final MethodFlag CONSTANT_INITIALIZER
	= new MethodFlag("CONSTANT_INITIALIZER", 15);
    /*synthetic*/ private static final MethodFlag[] $VALUES
		      = { METHOD_SKIP, DYNAMIC, CONSTANT_GETTER, CAST, JNI,
			  ADDRESS, CPP_METHOD, CPP_NEW, CPP_DELETE, CS_NEW,
			  CS_OBJECT, SETTER, GETTER, ADDER, POINTER_RETURN,
			  CONSTANT_INITIALIZER };
    
    public static MethodFlag[] values() {
	return (MethodFlag[]) $VALUES.clone();
    }
    
    public static MethodFlag valueOf(String name) {
	return (MethodFlag) Enum.valueOf(MethodFlag.class, name);
    }
    
    private MethodFlag(String string, int i) {
	super(string, i);
    }
}
