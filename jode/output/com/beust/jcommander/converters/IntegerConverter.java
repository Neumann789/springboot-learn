/* IntegerConverter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander.converters;
import com.beust.jcommander.ParameterException;

public class IntegerConverter extends BaseConverter
{
    public IntegerConverter(String optionName) {
	super(optionName);
    }
    
    public Integer convert(String value) {
	try {
	    return Integer.valueOf(Integer.parseInt(value));
	} catch (NumberFormatException PUSH) {
	    NumberFormatException ex = POP;
	    throw new ParameterException(getErrorString(value, "an integer"));
	}
    }
}
