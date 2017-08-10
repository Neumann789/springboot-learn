/* ClassFlag - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.fusesource.hawtjni.runtime;

public final class ClassFlag extends Enum
{
    public static final ClassFlag CLASS_SKIP = new ClassFlag("CLASS_SKIP", 0);
    public static final ClassFlag CPP = new ClassFlag("CPP", 1);
    public static final ClassFlag STRUCT = new ClassFlag("STRUCT", 2);
    public static final ClassFlag TYPEDEF = new ClassFlag("TYPEDEF", 3);
    public static final ClassFlag ZERO_OUT = new ClassFlag("ZERO_OUT", 4);
    /*synthetic*/ private static final ClassFlag[] $VALUES
		      = { CLASS_SKIP, CPP, STRUCT, TYPEDEF, ZERO_OUT };
    
    public static ClassFlag[] values() {
	return (ClassFlag[]) $VALUES.clone();
    }
    
    public static ClassFlag valueOf(String name) {
	return (ClassFlag) Enum.valueOf(ClassFlag.class, name);
    }
    
    private ClassFlag(String string, int i) {
	super(string, i);
    }
}
