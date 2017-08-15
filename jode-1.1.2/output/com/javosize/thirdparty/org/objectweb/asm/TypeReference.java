/* TypeReference - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm;

public class TypeReference
{
    public static final int CLASS_TYPE_PARAMETER = 0;
    public static final int METHOD_TYPE_PARAMETER = 1;
    public static final int CLASS_EXTENDS = 16;
    public static final int CLASS_TYPE_PARAMETER_BOUND = 17;
    public static final int METHOD_TYPE_PARAMETER_BOUND = 18;
    public static final int FIELD = 19;
    public static final int METHOD_RETURN = 20;
    public static final int METHOD_RECEIVER = 21;
    public static final int METHOD_FORMAL_PARAMETER = 22;
    public static final int THROWS = 23;
    public static final int LOCAL_VARIABLE = 64;
    public static final int RESOURCE_VARIABLE = 65;
    public static final int EXCEPTION_PARAMETER = 66;
    public static final int INSTANCEOF = 67;
    public static final int NEW = 68;
    public static final int CONSTRUCTOR_REFERENCE = 69;
    public static final int METHOD_REFERENCE = 70;
    public static final int CAST = 71;
    public static final int CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT = 72;
    public static final int METHOD_INVOCATION_TYPE_ARGUMENT = 73;
    public static final int CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT = 74;
    public static final int METHOD_REFERENCE_TYPE_ARGUMENT = 75;
    private int a;
    
    public TypeReference(int i) {
	a = i;
    }
    
    public static TypeReference newTypeReference(int i) {
	return new TypeReference(i << 24);
    }
    
    public static TypeReference newTypeParameterReference(int i, int i_0_) {
	return new TypeReference(i << 24 | i_0_ << 16);
    }
    
    public static TypeReference newTypeParameterBoundReference(int i, int i_1_,
							       int i_2_) {
	return new TypeReference(i << 24 | i_1_ << 16 | i_2_ << 8);
    }
    
    public static TypeReference newSuperTypeReference(int i) {
	i &= 0xffff;
	return new TypeReference(0x10000000 | i << 8);
    }
    
    public static TypeReference newFormalParameterReference(int i) {
	return new TypeReference(0x16000000 | i << 16);
    }
    
    public static TypeReference newExceptionReference(int i) {
	return new TypeReference(0x17000000 | i << 8);
    }
    
    public static TypeReference newTryCatchReference(int i) {
	return new TypeReference(0x42000000 | i << 8);
    }
    
    public static TypeReference newTypeArgumentReference(int i, int i_3_) {
	return new TypeReference(i << 24 | i_3_);
    }
    
    public int getSort() {
	return a >>> 24;
    }
    
    public int getTypeParameterIndex() {
	return (a & 0xff0000) >> 16;
    }
    
    public int getTypeParameterBoundIndex() {
	return (a & 0xff00) >> 8;
    }
    
    public int getSuperTypeIndex() {
	return (short) ((a & 0xffff00) >> 8);
    }
    
    public int getFormalParameterIndex() {
	return (a & 0xff0000) >> 16;
    }
    
    public int getExceptionIndex() {
	return (a & 0xffff00) >> 8;
    }
    
    public int getTryCatchBlockIndex() {
	return (a & 0xffff00) >> 8;
    }
    
    public int getTypeArgumentIndex() {
	return a & 0xff;
    }
    
    public int getValue() {
	return a;
    }
}
