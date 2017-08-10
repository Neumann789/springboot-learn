/* NameResolveMode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;

public final class NameResolveMode extends Enum
{
    public static final NameResolveMode TYPE = new NameResolveMode("TYPE", 0);
    public static final NameResolveMode EXPRESSION
	= new NameResolveMode("EXPRESSION", 1);
    /*synthetic*/ private static final NameResolveMode[] $VALUES
		      = { TYPE, EXPRESSION };
    
    public static NameResolveMode[] values() {
	return (NameResolveMode[]) $VALUES.clone();
    }
    
    public static NameResolveMode valueOf(String name) {
	return (NameResolveMode) Enum.valueOf(NameResolveMode.class, name);
    }
    
    private NameResolveMode(String string, int i) {
	super(string, i);
    }
}
