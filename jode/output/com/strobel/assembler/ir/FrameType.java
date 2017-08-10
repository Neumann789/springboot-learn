/* FrameType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir;

public final class FrameType extends Enum
{
    public static final FrameType Append = new FrameType("Append", 0);
    public static final FrameType Chop = new FrameType("Chop", 1);
    public static final FrameType Full = new FrameType("Full", 2);
    public static final FrameType New = new FrameType("New", 3);
    public static final FrameType Same = new FrameType("Same", 4);
    public static final FrameType Same1 = new FrameType("Same1", 5);
    /*synthetic*/ private static final FrameType[] $VALUES
		      = { Append, Chop, Full, New, Same, Same1 };
    
    public static FrameType[] values() {
	return (FrameType[]) $VALUES.clone();
    }
    
    public static FrameType valueOf(String name) {
	return (FrameType) Enum.valueOf(FrameType.class, name);
    }
    
    private FrameType(String string, int i) {
	super(string, i);
    }
}
