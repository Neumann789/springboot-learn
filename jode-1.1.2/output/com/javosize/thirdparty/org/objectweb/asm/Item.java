/* Item - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm;

final class Item
{
    int a;
    int b;
    int c;
    long d;
    String g;
    String h;
    String i;
    int j;
    Item k;
    
    Item() {
	/* empty */
    }
    
    Item(int i) {
	a = i;
    }
    
    Item(int i, Item item_0_) {
	a = i;
	b = item_0_.b;
	c = item_0_.c;
	d = item_0_.d;
	g = item_0_.g;
	h = item_0_.h;
	this.i = item_0_.i;
	j = item_0_.j;
    }
    
    void a(int i) {
	b = 3;
	c = i;
	j = 0x7fffffff & b + i;
    }
    
    void a(long l) {
	b = 5;
	d = l;
	j = 0x7fffffff & b + (int) l;
    }
    
    void a(float f) {
	b = 4;
	c = Float.floatToRawIntBits(f);
	j = 0x7fffffff & b + (int) f;
    }
    
    void a(double d) {
	b = 6;
	this.d = Double.doubleToRawLongBits(d);
	j = 0x7fffffff & b + (int) d;
    }
    
    void a(int i, String string, String string_1_, String string_2_) {
	b = i;
	g = string;
	h = string_1_;
	this.i = string_2_;
	switch (i) {
	case 7:
	    c = 0;
	    /* fall through */
	case 1:
	case 8:
	case 16:
	case 30:
	    j = 0x7fffffff & i + string.hashCode();
	    break;
	case 12:
	    j = 0x7fffffff & i + string.hashCode() * string_1_.hashCode();
	    break;
	default:
	    j = 0x7fffffff & i + (string.hashCode() * string_1_.hashCode()
				  * string_2_.hashCode());
	}
    }
    
    void a(String string, String string_3_, int i) {
	b = 18;
	d = (long) i;
	g = string;
	h = string_3_;
	j = 0x7fffffff & 18 + i * g.hashCode() * h.hashCode();
    }
    
    void a(int i, int i_4_) {
	b = 33;
	c = i;
	j = i_4_;
    }
    
    boolean a(Item item_5_) {
	switch (b) {
	case 1:
	case 7:
	case 8:
	case 16:
	case 30:
	    return item_5_.g.equals(g);
	case 5:
	case 6:
	case 32:
	    return item_5_.d == d;
	case 3:
	case 4:
	    return item_5_.c == c;
	case 31:
	    return item_5_.c == c && item_5_.g.equals(g);
	case 12:
	    return item_5_.g.equals(g) && item_5_.h.equals(h);
	case 18:
	    return (item_5_.d == d && item_5_.g.equals(g)
		    && item_5_.h.equals(h));
	default:
	    return (item_5_.g.equals(g) && item_5_.h.equals(h)
		    && item_5_.i.equals(i));
	}
    }
}
