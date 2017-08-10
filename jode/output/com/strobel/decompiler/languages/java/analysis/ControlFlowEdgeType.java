/* ControlFlowEdgeType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.analysis;

public final class ControlFlowEdgeType extends Enum
{
    public static final ControlFlowEdgeType Normal
	= new ControlFlowEdgeType("Normal", 0);
    public static final ControlFlowEdgeType ConditionTrue
	= new ControlFlowEdgeType("ConditionTrue", 1);
    public static final ControlFlowEdgeType ConditionFalse
	= new ControlFlowEdgeType("ConditionFalse", 2);
    public static final ControlFlowEdgeType Jump
	= new ControlFlowEdgeType("Jump", 3);
    /*synthetic*/ private static final ControlFlowEdgeType[] $VALUES
		      = { Normal, ConditionTrue, ConditionFalse, Jump };
    
    public static ControlFlowEdgeType[] values() {
	return (ControlFlowEdgeType[]) $VALUES.clone();
    }
    
    public static ControlFlowEdgeType valueOf(String name) {
	return ((ControlFlowEdgeType)
		Enum.valueOf(ControlFlowEdgeType.class, name));
    }
    
    private ControlFlowEdgeType(String string, int i) {
	super(string, i);
    }
}
