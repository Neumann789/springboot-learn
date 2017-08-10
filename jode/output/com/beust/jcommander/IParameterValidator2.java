/* IParameterValidator2 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander;

public interface IParameterValidator2 extends IParameterValidator
{
    public void validate(String string, String string_0_,
			 ParameterDescription parameterdescription)
	throws ParameterException;
}
