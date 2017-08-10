/* FloatConverter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander.converters;
import com.beust.jcommander.ParameterException;

public class FloatConverter extends BaseConverter
{
    public FloatConverter(String optionName) {
	super(optionName);
    }
    
    public Float convert(String value) {
	try {
	    return Float.valueOf(Float.parseFloat(value));
	} catch (NumberFormatException PUSH) {
	    NumberFormatException ex = POP;
	    throw new ParameterException(getErrorString(value, "a float"));
	}
    }
}
