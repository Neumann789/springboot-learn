/* StringComparison - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.core;

public final class StringComparison extends Enum
{
    public static final StringComparison Ordinal
	= new StringComparison("Ordinal", 0);
    public static final StringComparison OrdinalIgnoreCase
	= new StringComparison("OrdinalIgnoreCase", 1);
    /*synthetic*/ private static final StringComparison[] $VALUES
		      = { Ordinal, OrdinalIgnoreCase };
    
    public static StringComparison[] values() {
	return (StringComparison[]) $VALUES.clone();
    }
    
    public static StringComparison valueOf(String name) {
	return (StringComparison) Enum.valueOf(StringComparison.class, name);
    }
    
    private StringComparison(String string, int i) {
	super(string, i);
    }
}
