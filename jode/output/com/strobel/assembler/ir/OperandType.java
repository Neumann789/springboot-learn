/* OperandType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir;

public final class OperandType extends Enum
{
    public static final OperandType None = new OperandType("None", 0, 0);
    public static final OperandType PrimitiveTypeCode
	= new OperandType("PrimitiveTypeCode", 1, 1);
    public static final OperandType TypeReference
	= new OperandType("TypeReference", 2, 2);
    public static final OperandType TypeReferenceU1
	= new OperandType("TypeReferenceU1", 3, 3);
    public static final OperandType DynamicCallSite
	= new OperandType("DynamicCallSite", 4, 4);
    public static final OperandType MethodReference
	= new OperandType("MethodReference", 5, 2);
    public static final OperandType FieldReference
	= new OperandType("FieldReference", 6, 2);
    public static final OperandType BranchTarget
	= new OperandType("BranchTarget", 7, 2);
    public static final OperandType BranchTargetWide
	= new OperandType("BranchTargetWide", 8, 4);
    public static final OperandType I1 = new OperandType("I1", 9, 1);
    public static final OperandType I2 = new OperandType("I2", 10, 2);
    public static final OperandType I8 = new OperandType("I8", 11, 8);
    public static final OperandType Constant
	= new OperandType("Constant", 12, 1);
    public static final OperandType WideConstant
	= new OperandType("WideConstant", 13, 2);
    public static final OperandType Switch = new OperandType("Switch", 14, -1);
    public static final OperandType Local = new OperandType("Local", 15, 1);
    public static final OperandType LocalI1
	= new OperandType("LocalI1", 16, 2);
    public static final OperandType LocalI2
	= new OperandType("LocalI2", 17, 4);
    private final int size;
    /*synthetic*/ private static final OperandType[] $VALUES
		      = { None, PrimitiveTypeCode, TypeReference,
			  TypeReferenceU1, DynamicCallSite, MethodReference,
			  FieldReference, BranchTarget, BranchTargetWide, I1,
			  I2, I8, Constant, WideConstant, Switch, Local,
			  LocalI1, LocalI2 };
    
    public static OperandType[] values() {
	return (OperandType[]) $VALUES.clone();
    }
    
    public static OperandType valueOf(String name) {
	return (OperandType) Enum.valueOf(OperandType.class, name);
    }
    
    private OperandType(String string, int i, int size) {
	super(string, i);
	this.size = size;
    }
    
    public final int getBaseSize() {
	return size;
    }
}
