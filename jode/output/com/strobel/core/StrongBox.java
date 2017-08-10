/* StrongBox - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.core;
import com.strobel.functions.Block;

public final class StrongBox implements IStrongBox, Block
{
    public Object value;
    
    public StrongBox() {
	/* empty */
    }
    
    public StrongBox(Object value) {
	this.value = value;
    }
    
    public Object get() {
	return value;
    }
    
    public void set(Object value) {
	this.value = value;
    }
    
    public void accept(Object input) {
	value = input;
    }
    
    public String toString() {
	return "StrongBox{value=" + value + '}';
    }
}
