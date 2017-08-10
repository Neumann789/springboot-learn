/* IValueValidator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander;

public interface IValueValidator
{
    public void validate(String string, Object object)
	throws ParameterException;
}
