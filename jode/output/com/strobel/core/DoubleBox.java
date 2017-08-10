/* DoubleBox - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.core;

public final class DoubleBox implements IStrongBox
{
    public double value;
    
    public DoubleBox() {
	/* empty */
    }
    
    public DoubleBox(double value) {
	this.value = value;
    }
    
    public Double get() {
	return Double.valueOf(value);
    }
    
    public void set(Object value) {
	this.value = ((Double) value).doubleValue();
    }
}
