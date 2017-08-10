/* FieldFlag - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.fusesource.hawtjni.runtime;

public final class FieldFlag extends Enum
{
    public static final FieldFlag FIELD_SKIP = new FieldFlag("FIELD_SKIP", 0);
    public static final FieldFlag CONSTANT = new FieldFlag("CONSTANT", 1);
    public static final FieldFlag POINTER_FIELD
	= new FieldFlag("POINTER_FIELD", 2);
    /*synthetic*/ private static final FieldFlag[] $VALUES
		      = { FIELD_SKIP, CONSTANT, POINTER_FIELD };
    
    public static FieldFlag[] values() {
	return (FieldFlag[]) $VALUES.clone();
    }
    
    public static FieldFlag valueOf(String name) {
	return (FieldFlag) Enum.valueOf(FieldFlag.class, name);
    }
    
    private FieldFlag(String string, int i) {
	super(string, i);
    }
}
