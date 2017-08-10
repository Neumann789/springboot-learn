/* TypePath - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm;

public class TypePath
{
    public static final int ARRAY_ELEMENT = 0;
    public static final int INNER_TYPE = 1;
    public static final int WILDCARD_BOUND = 2;
    public static final int TYPE_ARGUMENT = 3;
    byte[] a;
    int b;
    
    TypePath(byte[] is, int i) {
	a = is;
	b = i;
    }
    
    public int getLength() {
	return a[b];
    }
    
    public int getStep(int i) {
	return a[b + 2 * i + 1];
    }
    
    public int getStepArgument(int i) {
	return a[b + 2 * i + 2];
    }
    
    public static TypePath fromString(String string) {
	if (string != null && string.length() != 0) {
	    int i = string.length();
	    ByteVector bytevector = new ByteVector(i);
	    bytevector.putByte(0);
	    int i_0_ = 0;
	    for (;;) {
		if (i_0_ >= i) {
		    bytevector.a[0] = (byte) (bytevector.b / 2);
		    return new TypePath(bytevector.a, 0);
		}
		char c = string.charAt(i_0_++);
		if (c != 91) {
		    if (c != 46) {
			if (c != 42) {
			    if (c >= 48 && c <= 57) {
				int i_1_ = c - 48;
				for (;;) {
				    int i_2_;
				    if (i_0_ >= i
					|| (i_2_ = string.charAt(i_0_)) < 48
					|| i_2_ > 57) {
					bytevector.a(3, i_1_);
					break;
				    }
				    i_1_ = i_1_ * 10 + i_2_ - 48;
				    i_0_++;
				}
			    }
			} else
			    bytevector.a(2, 0);
		    } else
			bytevector.a(1, 0);
		} else
		    bytevector.a(0, 0);
		continue;
	    }
	}
	return null;
    }
    
    public String toString() {
	int i = getLength();
	StringBuffer stringbuffer = new StringBuffer(i * 2);
	int i_3_ = 0;
	for (;;) {
	    if (i_3_ >= i)
		return stringbuffer.toString();
	    switch (getStep(i_3_)) {
	    case 0:
		stringbuffer.append('[');
		break;
	    case 1:
		stringbuffer.append('.');
		break;
	    case 2:
		stringbuffer.append('*');
		break;
	    case 3:
		stringbuffer.append(getStepArgument(i_3_));
		break;
	    default:
		stringbuffer.append('_');
	    }
	    i_3_++;
	}
    }
}
