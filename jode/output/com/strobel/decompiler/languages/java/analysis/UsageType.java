/* UsageType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.analysis;

public final class UsageType extends Enum
{
    public static final UsageType Read = new UsageType("Read", 0);
    public static final UsageType Write = new UsageType("Write", 1);
    public static final UsageType ReadWrite = new UsageType("ReadWrite", 2);
    /*synthetic*/ private static final UsageType[] $VALUES
		      = { Read, Write, ReadWrite };
    
    public static UsageType[] values() {
	return (UsageType[]) $VALUES.clone();
    }
    
    public static UsageType valueOf(String name) {
	return (UsageType) Enum.valueOf(UsageType.class, name);
    }
    
    private UsageType(String string, int i) {
	super(string, i);
    }
}
