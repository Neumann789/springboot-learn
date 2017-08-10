/* DoubleConverter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander.converters;
import com.beust.jcommander.ParameterException;

public class DoubleConverter extends BaseConverter
{
    public DoubleConverter(String optionName) {
	super(optionName);
    }
    
    public Double convert(String value) {
	try {
	    return Double.valueOf(Double.parseDouble(value));
	} catch (NumberFormatException PUSH) {
	    NumberFormatException ex = POP;
	    throw new ParameterException(getErrorString(value, "a double"));
	}
    }
}
