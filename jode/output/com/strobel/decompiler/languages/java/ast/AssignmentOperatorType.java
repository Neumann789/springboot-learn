/* AssignmentOperatorType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;

public final class AssignmentOperatorType extends Enum
{
    public static final AssignmentOperatorType ASSIGN
	= new AssignmentOperatorType("ASSIGN", 0);
    public static final AssignmentOperatorType ADD
	= new AssignmentOperatorType("ADD", 1);
    public static final AssignmentOperatorType SUBTRACT
	= new AssignmentOperatorType("SUBTRACT", 2);
    public static final AssignmentOperatorType MULTIPLY
	= new AssignmentOperatorType("MULTIPLY", 3);
    public static final AssignmentOperatorType DIVIDE
	= new AssignmentOperatorType("DIVIDE", 4);
    public static final AssignmentOperatorType MODULUS
	= new AssignmentOperatorType("MODULUS", 5);
    public static final AssignmentOperatorType SHIFT_LEFT
	= new AssignmentOperatorType("SHIFT_LEFT", 6);
    public static final AssignmentOperatorType SHIFT_RIGHT
	= new AssignmentOperatorType("SHIFT_RIGHT", 7);
    public static final AssignmentOperatorType UNSIGNED_SHIFT_RIGHT
	= new AssignmentOperatorType("UNSIGNED_SHIFT_RIGHT", 8);
    public static final AssignmentOperatorType BITWISE_AND
	= new AssignmentOperatorType("BITWISE_AND", 9);
    public static final AssignmentOperatorType BITWISE_OR
	= new AssignmentOperatorType("BITWISE_OR", 10);
    public static final AssignmentOperatorType EXCLUSIVE_OR
	= new AssignmentOperatorType("EXCLUSIVE_OR", 11);
    public static final AssignmentOperatorType ANY
	= new AssignmentOperatorType("ANY", 12);
    /*synthetic*/ private static final AssignmentOperatorType[] $VALUES
		      = { ASSIGN, ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULUS,
			  SHIFT_LEFT, SHIFT_RIGHT, UNSIGNED_SHIFT_RIGHT,
			  BITWISE_AND, BITWISE_OR, EXCLUSIVE_OR, ANY };
    
    public static AssignmentOperatorType[] values() {
	return (AssignmentOperatorType[]) $VALUES.clone();
    }
    
    public static AssignmentOperatorType valueOf(String name) {
	return ((AssignmentOperatorType)
		Enum.valueOf(AssignmentOperatorType.class, name));
    }
    
    private AssignmentOperatorType(String string, int i) {
	super(string, i);
    }
    
    public final boolean isCompoundAssignment() {
	switch (ANONYMOUS CLASS com.strobel.decompiler.languages.java.ast.AssignmentOperatorType$1.$SwitchMap$com$strobel$decompiler$languages$java$ast$AssignmentOperatorType[ordinal()]) {
	case 1:
	case 2:
	    return false;
	default:
	    return true;
	}
    }
}
