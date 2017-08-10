/* ShortBox - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.core;

public final class ShortBox implements IStrongBox
{
    public short value;
    
    public ShortBox() {
	/* empty */
    }
    
    public ShortBox(short value) {
	this.value = value;
    }
    
    public Short get() {
	return Short.valueOf(value);
    }
    
    public void set(Object value) {
	this.value = ((Short) value).shortValue();
    }
}
