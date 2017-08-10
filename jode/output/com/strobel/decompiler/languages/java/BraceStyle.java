/* BraceStyle - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java;

public final class BraceStyle extends Enum
{
    public static final BraceStyle DoNotChange
	= new BraceStyle("DoNotChange", 0);
    public static final BraceStyle EndOfLine = new BraceStyle("EndOfLine", 1);
    public static final BraceStyle EndOfLineWithoutSpace
	= new BraceStyle("EndOfLineWithoutSpace", 2);
    public static final BraceStyle NextLine = new BraceStyle("NextLine", 3);
    public static final BraceStyle NextLineShifted
	= new BraceStyle("NextLineShifted", 4);
    public static final BraceStyle NextLineShifted2
	= new BraceStyle("NextLineShifted2", 5);
    public static final BraceStyle BannerStyle
	= new BraceStyle("BannerStyle", 6);
    /*synthetic*/ private static final BraceStyle[] $VALUES
		      = { DoNotChange, EndOfLine, EndOfLineWithoutSpace,
			  NextLine, NextLineShifted, NextLineShifted2,
			  BannerStyle };
    
    public static BraceStyle[] values() {
	return (BraceStyle[]) $VALUES.clone();
    }
    
    public static BraceStyle valueOf(String name) {
	return (BraceStyle) Enum.valueOf(BraceStyle.class, name);
    }
    
    private BraceStyle(String string, int i) {
	super(string, i);
    }
}
