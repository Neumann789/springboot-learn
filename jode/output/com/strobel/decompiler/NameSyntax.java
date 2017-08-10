/* NameSyntax - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler;

public final class NameSyntax extends Enum
{
    public static final NameSyntax SIGNATURE = new NameSyntax("SIGNATURE", 0);
    public static final NameSyntax ERASED_SIGNATURE
	= new NameSyntax("ERASED_SIGNATURE", 1);
    public static final NameSyntax DESCRIPTOR
	= new NameSyntax("DESCRIPTOR", 2);
    public static final NameSyntax TYPE_NAME = new NameSyntax("TYPE_NAME", 3);
    public static final NameSyntax SHORT_TYPE_NAME
	= new NameSyntax("SHORT_TYPE_NAME", 4);
    /*synthetic*/ private static final NameSyntax[] $VALUES
		      = { SIGNATURE, ERASED_SIGNATURE, DESCRIPTOR, TYPE_NAME,
			  SHORT_TYPE_NAME };
    
    public static NameSyntax[] values() {
	return (NameSyntax[]) $VALUES.clone();
    }
    
    public static NameSyntax valueOf(String name) {
	return (NameSyntax) Enum.valueOf(NameSyntax.class, name);
    }
    
    private NameSyntax(String string, int i) {
	super(string, i);
    }
}
