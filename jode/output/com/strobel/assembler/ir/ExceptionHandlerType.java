/* ExceptionHandlerType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir;

public final class ExceptionHandlerType extends Enum
{
    public static final ExceptionHandlerType Catch
	= new ExceptionHandlerType("Catch", 0);
    public static final ExceptionHandlerType Finally
	= new ExceptionHandlerType("Finally", 1);
    /*synthetic*/ private static final ExceptionHandlerType[] $VALUES
		      = { Catch, Finally };
    
    public static ExceptionHandlerType[] values() {
	return (ExceptionHandlerType[]) $VALUES.clone();
    }
    
    public static ExceptionHandlerType valueOf(String name) {
	return ((ExceptionHandlerType)
		Enum.valueOf(ExceptionHandlerType.class, name));
    }
    
    private ExceptionHandlerType(String string, int i) {
	super(string, i);
    }
}
