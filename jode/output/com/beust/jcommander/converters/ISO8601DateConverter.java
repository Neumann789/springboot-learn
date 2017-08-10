/* ISO8601DateConverter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander.converters;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.beust.jcommander.ParameterException;

public class ISO8601DateConverter extends BaseConverter
{
    private static final SimpleDateFormat DATE_FORMAT
	= new SimpleDateFormat("yyyy-MM-dd");
    
    public ISO8601DateConverter(String optionName) {
	super(optionName);
    }
    
    public Date convert(String value) {
	try {
	    return DATE_FORMAT.parse(value);
	} catch (java.text.ParseException PUSH) {
	    java.text.ParseException pe = POP;
	    throw new ParameterException
		      (getErrorString
		       (value, String.format("an ISO-8601 formatted date (%s)",
					     (new Object[]
					      { DATE_FORMAT.toPattern() }))));
	}
    }
}
