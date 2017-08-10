/* OpCodeType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir;

public final class OpCodeType extends Enum
{
    public static final OpCodeType Annotation
	= new OpCodeType("Annotation", 0);
    public static final OpCodeType Macro = new OpCodeType("Macro", 1);
    public static final OpCodeType Internal = new OpCodeType("Internal", 2);
    public static final OpCodeType ObjectModel
	= new OpCodeType("ObjectModel", 3);
    public static final OpCodeType Prefix = new OpCodeType("Prefix", 4);
    public static final OpCodeType Primitive = new OpCodeType("Primitive", 5);
    /*synthetic*/ private static final OpCodeType[] $VALUES
		      = { Annotation, Macro, Internal, ObjectModel, Prefix,
			  Primitive };
    
    public static OpCodeType[] values() {
	return (OpCodeType[]) $VALUES.clone();
    }
    
    public static OpCodeType valueOf(String name) {
	return (OpCodeType) Enum.valueOf(OpCodeType.class, name);
    }
    
    private OpCodeType(String string, int i) {
	super(string, i);
    }
}
