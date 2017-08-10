/* IntegerBox - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.core;

public final class IntegerBox implements IStrongBox
{
    public int value;
    
    public IntegerBox() {
	/* empty */
    }
    
    public IntegerBox(int value) {
	this.value = value;
    }
    
    public Integer get() {
	return Integer.valueOf(value);
    }
    
    public void set(Object value) {
	this.value = ((Integer) value).intValue();
    }
}
