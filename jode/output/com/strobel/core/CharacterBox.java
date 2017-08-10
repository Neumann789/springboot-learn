/* CharacterBox - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.core;

public final class CharacterBox implements IStrongBox
{
    public char value;
    
    public CharacterBox() {
	/* empty */
    }
    
    public CharacterBox(char value) {
	this.value = value;
    }
    
    public Character get() {
	return Character.valueOf(value);
    }
    
    public void set(Object value) {
	this.value = ((Character) value).charValue();
    }
}
