/* FloatBox - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.core;

public final class FloatBox implements IStrongBox
{
    public float value;
    
    public FloatBox() {
	/* empty */
    }
    
    public FloatBox(float value) {
	this.value = value;
    }
    
    public Float get() {
	return Float.valueOf(value);
    }
    
    public void set(Object value) {
	this.value = ((Float) value).floatValue();
    }
}
