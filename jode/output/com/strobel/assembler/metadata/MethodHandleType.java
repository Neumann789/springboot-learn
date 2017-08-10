/* MethodHandleType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;

public final class MethodHandleType extends Enum
{
    public static final MethodHandleType GetField
	= new MethodHandleType("GetField", 0);
    public static final MethodHandleType GetStatic
	= new MethodHandleType("GetStatic", 1);
    public static final MethodHandleType PutField
	= new MethodHandleType("PutField", 2);
    public static final MethodHandleType PutStatic
	= new MethodHandleType("PutStatic", 3);
    public static final MethodHandleType InvokeVirtual
	= new MethodHandleType("InvokeVirtual", 4);
    public static final MethodHandleType InvokeStatic
	= new MethodHandleType("InvokeStatic", 5);
    public static final MethodHandleType InvokeSpecial
	= new MethodHandleType("InvokeSpecial", 6);
    public static final MethodHandleType NewInvokeSpecial
	= new MethodHandleType("NewInvokeSpecial", 7);
    public static final MethodHandleType InvokeInterface
	= new MethodHandleType("InvokeInterface", 8);
    /*synthetic*/ private static final MethodHandleType[] $VALUES
		      = { GetField, GetStatic, PutField, PutStatic,
			  InvokeVirtual, InvokeStatic, InvokeSpecial,
			  NewInvokeSpecial, InvokeInterface };
    
    public static MethodHandleType[] values() {
	return (MethodHandleType[]) $VALUES.clone();
    }
    
    public static MethodHandleType valueOf(String name) {
	return (MethodHandleType) Enum.valueOf(MethodHandleType.class, name);
    }
    
    private MethodHandleType(String string, int i) {
	super(string, i);
    }
}
