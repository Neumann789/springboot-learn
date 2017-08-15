/* Label - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm;

public class Label
{
    public Object info;
    int a;
    int b;
    int c;
    private int d;
    private int[] e;
    int f;
    int g;
    Frame h;
    Label i;
    Edge j;
    Label k;
    
    public int getOffset() {
	if ((a & 0x2) == 0)
	    throw new IllegalStateException
		      ("Label offset position has not been resolved yet");
	return c;
    }
    
    void a(MethodWriter methodwriter, ByteVector bytevector, int i,
	   boolean bool) {
	if ((a & 0x2) == 0) {
	    if (bool) {
		a(-1 - i, bytevector.b);
		bytevector.putInt(-1);
	    } else {
		a(i, bytevector.b);
		bytevector.putShort(-1);
	    }
	} else if (bool)
	    bytevector.putInt(c - i);
	else
	    bytevector.putShort(c - i);
    }
    
    private void a(int i, int i_0_) {
	if (e == null)
	    e = new int[6];
	if (d >= e.length) {
	    int[] is = new int[e.length + 6];
	    System.arraycopy(e, 0, is, 0, e.length);
	    e = is;
	}
	e[d++] = i;
	e[d++] = i_0_;
    }
    
    boolean a(MethodWriter methodwriter, int i, byte[] is) {
	boolean bool = false;
	a |= 0x2;
	c = i;
	int i_1_ = 0;
	while (i_1_ < d) {
	    int i_2_ = e[i_1_++];
	    int i_3_ = e[i_1_++];
	    if (i_2_ >= 0) {
		int i_4_ = i - i_2_;
		if (i_4_ < -32768 || i_4_ > 32767) {
		    int i_5_ = is[i_3_ - 1] & 0xff;
		    if (i_5_ <= 168)
			is[i_3_ - 1] = (byte) (i_5_ + 49);
		    else
			is[i_3_ - 1] = (byte) (i_5_ + 20);
		    bool = true;
		}
		is[i_3_++] = (byte) (i_4_ >>> 8);
		is[i_3_] = (byte) i_4_;
	    } else {
		int i_6_ = i + i_2_ + 1;
		is[i_3_++] = (byte) (i_6_ >>> 24);
		is[i_3_++] = (byte) (i_6_ >>> 16);
		is[i_3_++] = (byte) (i_6_ >>> 8);
		is[i_3_] = (byte) i_6_;
	    }
	}
	return bool;
    }
    
    Label a() {
	return h == null ? this : h.b;
    }
    
    boolean a(long l) {
	if ((a & 0x400) != 0)
	    return (e[(int) (l >>> 32)] & (int) l) != 0;
	return false;
    }
    
    boolean a(Label label_7_) {
	if ((a & 0x400) == 0 || (label_7_.a & 0x400) == 0)
	    return false;
	for (int i = 0; i < e.length; i++) {
	    if ((e[i] & label_7_.e[i]) != 0)
		return true;
	}
	return false;
    }
    
    void a(long l, int i) {
	if ((a & 0x400) == 0) {
	    a |= 0x400;
	    e = new int[i / 32 + 1];
	}
	e[(int) (l >>> 32)] |= (int) l;
    }
    
    void b(Label label_8_, long l, int i) {
	Label label_9_ = this;
	while (label_9_ != null) {
	    Label label_10_ = label_9_;
	    label_9_ = label_10_.k;
	    label_10_.k = null;
	    if (label_8_ != null) {
		if ((label_10_.a & 0x800) != 0)
		    continue;
		label_10_.a |= 0x800;
		if ((label_10_.a & 0x100) != 0 && !label_10_.a(label_8_)) {
		    Edge edge = new Edge();
		    edge.a = label_10_.f;
		    edge.b = label_8_.j.b;
		    edge.c = label_10_.j;
		    label_10_.j = edge;
		}
	    } else {
		if (label_10_.a(l))
		    continue;
		label_10_.a(l, i);
	    }
	    for (Edge edge = label_10_.j; edge != null; edge = edge.c) {
		if (((label_10_.a & 0x80) == 0 || edge != label_10_.j.c)
		    && edge.b.k == null) {
		    edge.b.k = label_9_;
		    label_9_ = edge.b;
		}
	    }
	}
    }
    
    public String toString() {
	return "L" + System.identityHashCode(this);
    }
}
