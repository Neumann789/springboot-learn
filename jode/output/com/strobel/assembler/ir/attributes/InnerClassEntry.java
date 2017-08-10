/* InnerClassEntry - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir.attributes;
import com.strobel.assembler.metadata.Flags;

public final class InnerClassEntry
{
    private final String _innerClassName;
    private final String _outerClassName;
    private final String _shortName;
    private final int _accessFlags;
    
    public InnerClassEntry(String innerClassName, String outerClassName,
			   String shortName, int accessFlags) {
	_innerClassName = innerClassName;
	_outerClassName = outerClassName;
	_shortName = shortName;
	_accessFlags = accessFlags;
    }
    
    public String getInnerClassName() {
	return _innerClassName;
    }
    
    public String getOuterClassName() {
	return _outerClassName;
    }
    
    public String getShortName() {
	return _shortName;
    }
    
    public int getAccessFlags() {
	return _accessFlags;
    }
    
    public String toString() {
	return ("InnerClassEntry{InnerClassName='" + _innerClassName + '\''
		+ ", OuterClassName='" + _outerClassName + '\''
		+ ", ShortName='" + _shortName + '\'' + ", AccessFlags=["
		+ Flags.toString((long) _accessFlags) + "]}");
    }
}
