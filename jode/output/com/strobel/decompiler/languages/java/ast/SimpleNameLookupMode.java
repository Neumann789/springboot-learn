/* SimpleNameLookupMode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;

public final class SimpleNameLookupMode extends Enum
{
    public static final SimpleNameLookupMode EXPRESSION
	= new SimpleNameLookupMode("EXPRESSION", 0);
    public static final SimpleNameLookupMode INVOCATION_TARGET
	= new SimpleNameLookupMode("INVOCATION_TARGET", 1);
    public static final SimpleNameLookupMode TYPE
	= new SimpleNameLookupMode("TYPE", 2);
    public static final SimpleNameLookupMode TYPE_IN_IMPORT_DECLARATION
	= new SimpleNameLookupMode("TYPE_IN_IMPORT_DECLARATION", 3);
    public static final SimpleNameLookupMode BASE_TYPE_REFERENCE
	= new SimpleNameLookupMode("BASE_TYPE_REFERENCE", 4);
    /*synthetic*/ private static final SimpleNameLookupMode[] $VALUES
		      = { EXPRESSION, INVOCATION_TARGET, TYPE,
			  TYPE_IN_IMPORT_DECLARATION, BASE_TYPE_REFERENCE };
    
    public static SimpleNameLookupMode[] values() {
	return (SimpleNameLookupMode[]) $VALUES.clone();
    }
    
    public static SimpleNameLookupMode valueOf(String name) {
	return ((SimpleNameLookupMode)
		Enum.valueOf(SimpleNameLookupMode.class, name));
    }
    
    private SimpleNameLookupMode(String string, int i) {
	super(string, i);
    }
}
