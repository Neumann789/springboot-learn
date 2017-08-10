/* ControlFlowNodeType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.analysis;

public final class ControlFlowNodeType extends Enum
{
    public static final ControlFlowNodeType None
	= new ControlFlowNodeType("None", 0);
    public static final ControlFlowNodeType StartNode
	= new ControlFlowNodeType("StartNode", 1);
    public static final ControlFlowNodeType BetweenStatements
	= new ControlFlowNodeType("BetweenStatements", 2);
    public static final ControlFlowNodeType EndNode
	= new ControlFlowNodeType("EndNode", 3);
    public static final ControlFlowNodeType LoopCondition
	= new ControlFlowNodeType("LoopCondition", 4);
    /*synthetic*/ private static final ControlFlowNodeType[] $VALUES
		      = { None, StartNode, BetweenStatements, EndNode,
			  LoopCondition };
    
    public static ControlFlowNodeType[] values() {
	return (ControlFlowNodeType[]) $VALUES.clone();
    }
    
    public static ControlFlowNodeType valueOf(String name) {
	return ((ControlFlowNodeType)
		Enum.valueOf(ControlFlowNodeType.class, name));
    }
    
    private ControlFlowNodeType(String string, int i) {
	super(string, i);
    }
}
