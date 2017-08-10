/* ClassType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;

public final class ClassType extends Enum
{
    public static final ClassType CLASS = new ClassType("CLASS", 0);
    public static final ClassType INTERFACE = new ClassType("INTERFACE", 1);
    public static final ClassType ANNOTATION = new ClassType("ANNOTATION", 2);
    public static final ClassType ENUM = new ClassType("ENUM", 3);
    /*synthetic*/ private static final ClassType[] $VALUES
		      = { CLASS, INTERFACE, ANNOTATION, ENUM };
    
    public static ClassType[] values() {
	return (ClassType[]) $VALUES.clone();
    }
    
    public static ClassType valueOf(String name) {
	return (ClassType) Enum.valueOf(ClassType.class, name);
    }
    
    private ClassType(String string, int i) {
	super(string, i);
    }
}
