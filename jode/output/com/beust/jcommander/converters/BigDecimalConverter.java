/* BigDecimalConverter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander.converters;
import java.math.BigDecimal;

import com.beust.jcommander.ParameterException;

public class BigDecimalConverter extends BaseConverter
{
    public BigDecimalConverter(String optionName) {
	super(optionName);
    }
    
    public BigDecimal convert(String value) {
	try {
	    return new BigDecimal(value);
	} catch (NumberFormatException PUSH) {
	    NumberFormatException nfe = POP;
	    throw new ParameterException(getErrorString(value,
							"a BigDecimal"));
	}
    }
}
