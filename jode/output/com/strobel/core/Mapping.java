/* Mapping - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.core;

public abstract class Mapping
{
    private final String _name;
    
    protected Mapping() {
	this(null);
    }
    
    protected Mapping(String name) {
	_name = name;
    }
    
    public abstract Object apply(Object object);
    
    public String toString() {
	if (_name == null)
	    return super.toString();
	return _name;
    }
}
