/* JumpType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.flowanalysis;

public final class JumpType extends Enum
{
    public static final JumpType Normal = new JumpType("Normal", 0);
    public static final JumpType JumpToExceptionHandler
	= new JumpType("JumpToExceptionHandler", 1);
    public static final JumpType LeaveTry = new JumpType("LeaveTry", 2);
    public static final JumpType EndFinally = new JumpType("EndFinally", 3);
    /*synthetic*/ private static final JumpType[] $VALUES
		      = { Normal, JumpToExceptionHandler, LeaveTry,
			  EndFinally };
    
    public static JumpType[] values() {
	return (JumpType[]) $VALUES.clone();
    }
    
    public static JumpType valueOf(String name) {
	return (JumpType) Enum.valueOf(JumpType.class, name);
    }
    
    private JumpType(String string, int i) {
	super(string, i);
    }
}
