/* ConstructorInitializerType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;

public final class ConstructorInitializerType extends Enum
{
    public static final ConstructorInitializerType SUPER
	= new ConstructorInitializerType("SUPER", 0);
    public static final ConstructorInitializerType THIS
	= new ConstructorInitializerType("THIS", 1);
    /*synthetic*/ private static final ConstructorInitializerType[] $VALUES
		      = { SUPER, THIS };
    
    public static ConstructorInitializerType[] values() {
	return (ConstructorInitializerType[]) $VALUES.clone();
    }
    
    public static ConstructorInitializerType valueOf(String name) {
	return ((ConstructorInitializerType)
		Enum.valueOf(ConstructorInitializerType.class, name));
    }
    
    private ConstructorInitializerType(String string, int i) {
	super(string, i);
    }
}
