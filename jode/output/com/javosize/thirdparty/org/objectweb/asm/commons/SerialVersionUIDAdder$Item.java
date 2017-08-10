/* SerialVersionUIDAdder$Item - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;

class SerialVersionUIDAdder$Item implements Comparable
{
    final String name;
    final int access;
    final String desc;
    
    SerialVersionUIDAdder$Item(String string, int i, String string_0_) {
	name = string;
	access = i;
	desc = string_0_;
    }
    
    public int compareTo(SerialVersionUIDAdder$Item item_1_) {
	int i;
    label_438:
	{
	    i = name.compareTo(item_1_.name);
	    if (i == 0)
		i = desc.compareTo(item_1_.desc);
	    break label_438;
	}
	return i;
    }
    
    public boolean equals(Object object) {
    label_439:
	{
	    if (!(object instanceof SerialVersionUIDAdder$Item))
		return false;
	    if (compareTo((SerialVersionUIDAdder$Item) object) != 0)
		PUSH false;
	    else
		PUSH true;
	    break label_439;
	}
	return POP;
    }
    
    public int hashCode() {
	return (name + desc).hashCode();
    }
}
