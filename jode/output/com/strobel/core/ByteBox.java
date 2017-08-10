/* ByteBox - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.core;

public final class ByteBox implements IStrongBox
{
    public byte value;
    
    public ByteBox() {
	/* empty */
    }
    
    public ByteBox(byte value) {
	this.value = value;
    }
    
    public Byte get() {
	return Byte.valueOf(value);
    }
    
    public void set(Object value) {
	this.value = ((Byte) value).byteValue();
    }
}
