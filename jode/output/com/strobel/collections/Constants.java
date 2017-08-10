/* Constants - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.collections;

final class Constants
{
    public static final int DEFAULT_INT_NO_ENTRY_VALUE;
    
    static {
	int value;
    label_1371:
	{
	    String property
		= System.getProperty("gnu.trove.no_entry.int", "0");
	    if (!"MAX_VALUE".equalsIgnoreCase(property)) {
		if (!"MIN_VALUE".equalsIgnoreCase(property))
		    value = Integer.valueOf(property).intValue();
		else
		    value = -2147483648;
	    } else
		value = 2147483647;
	    break label_1371;
	}
	DEFAULT_INT_NO_ENTRY_VALUE = value;
    }
}
