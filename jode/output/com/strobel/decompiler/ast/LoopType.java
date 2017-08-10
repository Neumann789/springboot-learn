/* LoopType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.ast;

public final class LoopType extends Enum
{
    public static final LoopType PreCondition
	= new LoopType("PreCondition", 0);
    public static final LoopType PostCondition
	= new LoopType("PostCondition", 1);
    /*synthetic*/ private static final LoopType[] $VALUES
		      = { PreCondition, PostCondition };
    
    public static LoopType[] values() {
	return (LoopType[]) $VALUES.clone();
    }
    
    public static LoopType valueOf(String name) {
	return (LoopType) Enum.valueOf(LoopType.class, name);
    }
    
    private LoopType(String string, int i) {
	super(string, i);
    }
}
