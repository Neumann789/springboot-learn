/* StringKey - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander;

public class StringKey implements FuzzyMap.IKey
{
    private String m_name;
    
    public StringKey(String name) {
	m_name = name;
    }
    
    public String getName() {
	return m_name;
    }
    
    public String toString() {
	return m_name;
    }
    
    public int hashCode() {
	int prime = 31;
	int result = 1;
    label_1129:
	{
	    PUSH 31 * result;
	    if (m_name != null)
		PUSH m_name.hashCode();
	    else
		PUSH false;
	    break label_1129;
	}
	result = POP + POP;
	return result;
    }
    
    public boolean equals(Object obj) {
    label_1130:
	{
	    if (this != obj) {
		if (obj != null) {
		    if (this.getClass() == obj.getClass()) {
			StringKey other = (StringKey) obj;
			if (m_name != null) {
			    if (!m_name.equals(other.m_name))
				return false;
			} else if (other.m_name != null)
			    return false;
		    } else
			return false;
		} else
		    return false;
	    } else
		return true;
	}
	return true;
	break label_1130;
    }
}
