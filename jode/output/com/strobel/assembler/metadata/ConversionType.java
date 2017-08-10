/* ConversionType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;

public final class ConversionType extends Enum
{
    public static final ConversionType IDENTITY
	= new ConversionType("IDENTITY", 0);
    public static final ConversionType IMPLICIT
	= new ConversionType("IMPLICIT", 1);
    public static final ConversionType EXPLICIT
	= new ConversionType("EXPLICIT", 2);
    public static final ConversionType EXPLICIT_TO_UNBOXED
	= new ConversionType("EXPLICIT_TO_UNBOXED", 3);
    public static final ConversionType NONE = new ConversionType("NONE", 4);
    /*synthetic*/ private static final ConversionType[] $VALUES
		      = { IDENTITY, IMPLICIT, EXPLICIT, EXPLICIT_TO_UNBOXED,
			  NONE };
    
    public static ConversionType[] values() {
	return (ConversionType[]) $VALUES.clone();
    }
    
    public static ConversionType valueOf(String name) {
	return (ConversionType) Enum.valueOf(ConversionType.class, name);
    }
    
    private ConversionType(String string, int i) {
	super(string, i);
    }
}
