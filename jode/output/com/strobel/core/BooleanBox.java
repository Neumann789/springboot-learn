/* BooleanBox - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.core;

public final class BooleanBox implements IStrongBox
{
    public boolean value;
    
    public BooleanBox() {
	/* empty */
    }
    
    public BooleanBox(boolean value) {
	this.value = value;
    }
    
    public Boolean get() {
	return Boolean.valueOf(value);
    }
    
    public void set(Object value) {
	this.value = ((Boolean) value).booleanValue();
    }
}
