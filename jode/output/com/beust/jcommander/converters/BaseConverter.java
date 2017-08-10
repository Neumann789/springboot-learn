/* BaseConverter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander.converters;
import com.beust.jcommander.IStringConverter;

public abstract class BaseConverter implements IStringConverter
{
    private String m_optionName;
    
    public BaseConverter(String optionName) {
	m_optionName = optionName;
    }
    
    public String getOptionName() {
	return m_optionName;
    }
    
    protected String getErrorString(String value, String to) {
	return ("\"" + getOptionName() + "\": couldn't convert \"" + value
		+ "\" to " + to);
    }
}
