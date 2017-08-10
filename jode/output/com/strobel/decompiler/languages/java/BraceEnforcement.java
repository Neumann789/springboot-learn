/* BraceEnforcement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java;

public final class BraceEnforcement extends Enum
{
    public static final BraceEnforcement DoNotChange
	= new BraceEnforcement("DoNotChange", 0);
    public static final BraceEnforcement RemoveBraces
	= new BraceEnforcement("RemoveBraces", 1);
    public static final BraceEnforcement AddBraces
	= new BraceEnforcement("AddBraces", 2);
    /*synthetic*/ private static final BraceEnforcement[] $VALUES
		      = { DoNotChange, RemoveBraces, AddBraces };
    
    public static BraceEnforcement[] values() {
	return (BraceEnforcement[]) $VALUES.clone();
    }
    
    public static BraceEnforcement valueOf(String name) {
	return (BraceEnforcement) Enum.valueOf(BraceEnforcement.class, name);
    }
    
    private BraceEnforcement(String string, int i) {
	super(string, i);
    }
}
