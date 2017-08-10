/* Handle - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm;

public final class Handle
{
    final int a;
    final String b;
    final String c;
    final String d;
    
    public Handle(int i, String string, String string_0_, String string_1_) {
	a = i;
	b = string;
	c = string_0_;
	d = string_1_;
    }
    
    public int getTag() {
	return a;
    }
    
    public String getOwner() {
	return b;
    }
    
    public String getName() {
	return c;
    }
    
    public String getDesc() {
	return d;
    }
    
    public boolean equals(Object object) {
    label_363:
	{
	    if (object != this) {
		if (object instanceof Handle) {
		    Handle handle_2_ = (Handle) object;
		    if (a != handle_2_.a || !b.equals(handle_2_.b)
			|| !c.equals(handle_2_.c) || !d.equals(handle_2_.d))
			PUSH false;
		    else
			PUSH true;
		} else
		    return false;
	    } else
		return true;
	}
	return POP;
	break label_363;
    }
    
    public int hashCode() {
	return a + b.hashCode() * c.hashCode() * d.hashCode();
    }
    
    public String toString() {
	return b + '.' + c + d + " (" + a + ')';
    }
}
