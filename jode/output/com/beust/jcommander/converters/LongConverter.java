/* LongConverter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander.converters;
import com.beust.jcommander.ParameterException;

public class LongConverter extends BaseConverter
{
    public LongConverter(String optionName) {
	super(optionName);
    }
    
    public Long convert(String value) {
	try {
	    return Long.valueOf(Long.parseLong(value));
	} catch (NumberFormatException PUSH) {
	    NumberFormatException ex = POP;
	    throw new ParameterException(getErrorString(value, "a long"));
	}
    }
}
