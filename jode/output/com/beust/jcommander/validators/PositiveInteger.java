/* PositiveInteger - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander.validators;
import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class PositiveInteger implements IParameterValidator
{
    public void validate(String name, String value) throws ParameterException {
	int n = Integer.parseInt(value);
	IF (n >= 0)
	    /* empty */
	throw new ParameterException("Parameter " + name
				     + " should be positive (found " + value
				     + ")");
    }
}
