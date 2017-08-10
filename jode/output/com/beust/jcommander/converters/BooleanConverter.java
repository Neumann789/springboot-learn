/* BooleanConverter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander.converters;
import com.beust.jcommander.ParameterException;

public class BooleanConverter extends BaseConverter
{
    public BooleanConverter(String optionName) {
	super(optionName);
    }
    
    public Boolean convert(String value) {
	if (!"false".equalsIgnoreCase(value)
	    && !"true".equalsIgnoreCase(value))
	    throw new ParameterException(getErrorString(value, "a boolean"));
	return Boolean.valueOf(Boolean.parseBoolean(value));
    }
}
