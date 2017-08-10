/* Wrapping - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java;

public final class Wrapping extends Enum
{
    public static final Wrapping DoNotWrap = new Wrapping("DoNotWrap", 0);
    public static final Wrapping WrapAlways = new Wrapping("WrapAlways", 1);
    public static final Wrapping WrapIfTooLong
	= new Wrapping("WrapIfTooLong", 2);
    /*synthetic*/ private static final Wrapping[] $VALUES
		      = { DoNotWrap, WrapAlways, WrapIfTooLong };
    
    public static Wrapping[] values() {
	return (Wrapping[]) $VALUES.clone();
    }
    
    public static Wrapping valueOf(String name) {
	return (Wrapping) Enum.valueOf(Wrapping.class, name);
    }
    
    private Wrapping(String string, int i) {
	super(string, i);
    }
}
