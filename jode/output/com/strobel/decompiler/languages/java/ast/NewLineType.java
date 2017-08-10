/* NewLineType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;

public final class NewLineType extends Enum
{
    public static final NewLineType UNIX = new NewLineType("UNIX", 0);
    public static final NewLineType WINDOWS = new NewLineType("WINDOWS", 1);
    public static final NewLineType MAC = new NewLineType("MAC", 2);
    /*synthetic*/ private static final NewLineType[] $VALUES
		      = { UNIX, WINDOWS, MAC };
    
    public static NewLineType[] values() {
	return (NewLineType[]) $VALUES.clone();
    }
    
    public static NewLineType valueOf(String name) {
	return (NewLineType) Enum.valueOf(NewLineType.class, name);
    }
    
    private NewLineType(String string, int i) {
	super(string, i);
    }
}
