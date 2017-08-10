/* FlowControl - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir;

public final class FlowControl extends Enum
{
    public static final FlowControl Branch = new FlowControl("Branch", 0);
    public static final FlowControl Breakpoint
	= new FlowControl("Breakpoint", 1);
    public static final FlowControl Call = new FlowControl("Call", 2);
    public static final FlowControl ConditionalBranch
	= new FlowControl("ConditionalBranch", 3);
    public static final FlowControl Next = new FlowControl("Next", 4);
    public static final FlowControl Return = new FlowControl("Return", 5);
    public static final FlowControl Throw = new FlowControl("Throw", 6);
    /*synthetic*/ private static final FlowControl[] $VALUES
		      = { Branch, Breakpoint, Call, ConditionalBranch, Next,
			  Return, Throw };
    
    public static FlowControl[] values() {
	return (FlowControl[]) $VALUES.clone();
    }
    
    public static FlowControl valueOf(String name) {
	return (FlowControl) Enum.valueOf(FlowControl.class, name);
    }
    
    private FlowControl(String string, int i) {
	super(string, i);
    }
}
