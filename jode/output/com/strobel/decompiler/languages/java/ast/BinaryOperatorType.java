/* BinaryOperatorType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;

public final class BinaryOperatorType extends Enum
{
    public static final BinaryOperatorType ANY
	= new BinaryOperatorType("ANY", 0);
    public static final BinaryOperatorType BITWISE_AND
	= new BinaryOperatorType("BITWISE_AND", 1);
    public static final BinaryOperatorType BITWISE_OR
	= new BinaryOperatorType("BITWISE_OR", 2);
    public static final BinaryOperatorType EXCLUSIVE_OR
	= new BinaryOperatorType("EXCLUSIVE_OR", 3);
    public static final BinaryOperatorType LOGICAL_AND
	= new BinaryOperatorType("LOGICAL_AND", 4);
    public static final BinaryOperatorType LOGICAL_OR
	= new BinaryOperatorType("LOGICAL_OR", 5);
    public static final BinaryOperatorType GREATER_THAN
	= new BinaryOperatorType("GREATER_THAN", 6);
    public static final BinaryOperatorType GREATER_THAN_OR_EQUAL
	= new BinaryOperatorType("GREATER_THAN_OR_EQUAL", 7);
    public static final BinaryOperatorType LESS_THAN
	= new BinaryOperatorType("LESS_THAN", 8);
    public static final BinaryOperatorType LESS_THAN_OR_EQUAL
	= new BinaryOperatorType("LESS_THAN_OR_EQUAL", 9);
    public static final BinaryOperatorType EQUALITY
	= new BinaryOperatorType("EQUALITY", 10);
    public static final BinaryOperatorType INEQUALITY
	= new BinaryOperatorType("INEQUALITY", 11);
    public static final BinaryOperatorType ADD
	= new BinaryOperatorType("ADD", 12);
    public static final BinaryOperatorType SUBTRACT
	= new BinaryOperatorType("SUBTRACT", 13);
    public static final BinaryOperatorType MULTIPLY
	= new BinaryOperatorType("MULTIPLY", 14);
    public static final BinaryOperatorType DIVIDE
	= new BinaryOperatorType("DIVIDE", 15);
    public static final BinaryOperatorType MODULUS
	= new BinaryOperatorType("MODULUS", 16);
    public static final BinaryOperatorType SHIFT_LEFT
	= new BinaryOperatorType("SHIFT_LEFT", 17);
    public static final BinaryOperatorType SHIFT_RIGHT
	= new BinaryOperatorType("SHIFT_RIGHT", 18);
    public static final BinaryOperatorType UNSIGNED_SHIFT_RIGHT
	= new BinaryOperatorType("UNSIGNED_SHIFT_RIGHT", 19);
    /*synthetic*/ private static final BinaryOperatorType[] $VALUES
		      = { ANY, BITWISE_AND, BITWISE_OR, EXCLUSIVE_OR,
			  LOGICAL_AND, LOGICAL_OR, GREATER_THAN,
			  GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL,
			  EQUALITY, INEQUALITY, ADD, SUBTRACT, MULTIPLY,
			  DIVIDE, MODULUS, SHIFT_LEFT, SHIFT_RIGHT,
			  UNSIGNED_SHIFT_RIGHT };
    
    public static BinaryOperatorType[] values() {
	return (BinaryOperatorType[]) $VALUES.clone();
    }
    
    public static BinaryOperatorType valueOf(String name) {
	return ((BinaryOperatorType)
		Enum.valueOf(BinaryOperatorType.class, name));
    }
    
    private BinaryOperatorType(String string, int i) {
	super(string, i);
    }
    
    public final boolean isCommutative() {
	switch (ANONYMOUS CLASS com.strobel.decompiler.languages.java.ast.BinaryOperatorType$1.$SwitchMap$com$strobel$decompiler$languages$java$ast$BinaryOperatorType[ordinal()]) {
	case 1:
	case 2:
	case 3:
	case 4:
	case 5:
	case 6:
	case 7:
	    return true;
	default:
	    return false;
	}
    }
}
