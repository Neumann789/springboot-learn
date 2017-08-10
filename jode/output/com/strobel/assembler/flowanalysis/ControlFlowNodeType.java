/* ControlFlowNodeType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.flowanalysis;

public final class ControlFlowNodeType extends Enum
{
    public static final ControlFlowNodeType Normal
	= new ControlFlowNodeType("Normal", 0);
    public static final ControlFlowNodeType EntryPoint
	= new ControlFlowNodeType("EntryPoint", 1);
    public static final ControlFlowNodeType RegularExit
	= new ControlFlowNodeType("RegularExit", 2);
    public static final ControlFlowNodeType ExceptionalExit
	= new ControlFlowNodeType("ExceptionalExit", 3);
    public static final ControlFlowNodeType CatchHandler
	= new ControlFlowNodeType("CatchHandler", 4);
    public static final ControlFlowNodeType FinallyHandler
	= new ControlFlowNodeType("FinallyHandler", 5);
    public static final ControlFlowNodeType EndFinally
	= new ControlFlowNodeType("EndFinally", 6);
    /*synthetic*/ private static final ControlFlowNodeType[] $VALUES
		      = { Normal, EntryPoint, RegularExit, ExceptionalExit,
			  CatchHandler, FinallyHandler, EndFinally };
    
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
