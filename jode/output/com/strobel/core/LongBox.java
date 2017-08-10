/* LongBox - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.core;

public final class LongBox implements IStrongBox
{
    public long value;
    
    public LongBox() {
	/* empty */
    }
    
    public LongBox(long value) {
	this.value = value;
    }
    
    public Long get() {
	return Long.valueOf(value);
    }
    
    public void set(Object value) {
	this.value = ((Long) value).longValue();
    }
}
