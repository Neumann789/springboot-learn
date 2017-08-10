/* GenericParameterType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;

public final class GenericParameterType extends Enum
{
    public static final GenericParameterType Type
	= new GenericParameterType("Type", 0);
    public static final GenericParameterType Method
	= new GenericParameterType("Method", 1);
    /*synthetic*/ private static final GenericParameterType[] $VALUES
		      = { Type, Method };
    
    public static GenericParameterType[] values() {
	return (GenericParameterType[]) $VALUES.clone();
    }
    
    public static GenericParameterType valueOf(String name) {
	return ((GenericParameterType)
		Enum.valueOf(GenericParameterType.class, name));
    }
    
    private GenericParameterType(String string, int i) {
	super(string, i);
    }
}
