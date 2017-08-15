/* ByteVector - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm;

public class ByteVector
{
    byte[] a;
    int b;
    
    public ByteVector() {
	a = new byte[64];
    }
    
    public ByteVector(int i) {
	a = new byte[i];
    }
    
    public ByteVector putByte(int i) {
	int i_0_ = b;
	if (i_0_ + 1 > a.length)
	    a(1);
	a[i_0_++] = (byte) i;
	b = i_0_;
	return this;
    }
    
    ByteVector a(int i, int i_1_) {
	int i_2_ = b;
	if (i_2_ + 2 > a.length)
	    a(2);
	byte[] is = a;
	is[i_2_++] = (byte) i;
	is[i_2_++] = (byte) i_1_;
	b = i_2_;
	return this;
    }
    
    public ByteVector putShort(int i) {
	int i_3_ = b;
	if (i_3_ + 2 > a.length)
	    a(2);
	byte[] is = a;
	is[i_3_++] = (byte) (i >>> 8);
	is[i_3_++] = (byte) i;
	b = i_3_;
	return this;
    }
    
    ByteVector b(int i, int i_4_) {
	int i_5_ = b;
	if (i_5_ + 3 > a.length)
	    a(3);
	byte[] is = a;
	is[i_5_++] = (byte) i;
	is[i_5_++] = (byte) (i_4_ >>> 8);
	is[i_5_++] = (byte) i_4_;
	b = i_5_;
	return this;
    }
    
    public ByteVector putInt(int i) {
	int i_6_ = b;
	if (i_6_ + 4 > a.length)
	    a(4);
	byte[] is = a;
	is[i_6_++] = (byte) (i >>> 24);
	is[i_6_++] = (byte) (i >>> 16);
	is[i_6_++] = (byte) (i >>> 8);
	is[i_6_++] = (byte) i;
	b = i_6_;
	return this;
    }
    
    public ByteVector putLong(long l) {
	int i = b;
	if (i + 8 > a.length)
	    a(8);
	byte[] is = a;
	int i_7_ = (int) (l >>> 32);
	is[i++] = (byte) (i_7_ >>> 24);
	is[i++] = (byte) (i_7_ >>> 16);
	is[i++] = (byte) (i_7_ >>> 8);
	is[i++] = (byte) i_7_;
	i_7_ = (int) l;
	is[i++] = (byte) (i_7_ >>> 24);
	is[i++] = (byte) (i_7_ >>> 16);
	is[i++] = (byte) (i_7_ >>> 8);
	is[i++] = (byte) i_7_;
	b = i;
	return this;
    }
    
    public ByteVector putUTF8(String string) {
	int i = string.length();
	if (i > 65535)
	    throw new IllegalArgumentException();
	int i_8_ = b;
	if (i_8_ + 2 + i > a.length)
	    a(2 + i);
	byte[] is = a;
	is[i_8_++] = (byte) (i >>> 8);
	is[i_8_++] = (byte) i;
	for (int i_9_ = 0; i_9_ < i; i_9_++) {
	    char c = string.charAt(i_9_);
	    if (c >= '\001' && c <= '\u007f')
		is[i_8_++] = (byte) c;
	    else {
		b = i_8_;
		return encodeUTF8(string, i_9_, 65535);
	    }
	}
	b = i_8_;
	return this;
    }
    
    ByteVector encodeUTF8(String string, int i, int i_10_) {
	int i_11_ = string.length();
	int i_12_ = i;
	for (int i_13_ = i; i_13_ < i_11_; i_13_++) {
	    char c = string.charAt(i_13_);
	    if (c >= '\001' && c <= '\u007f')
		i_12_++;
	    else if (c > '\u07ff')
		i_12_ += 3;
	    else
		i_12_ += 2;
	}
	if (i_12_ > i_10_)
	    throw new IllegalArgumentException();
	int i_14_ = b - i - 2;
	if (i_14_ >= 0) {
	    a[i_14_] = (byte) (i_12_ >>> 8);
	    a[i_14_ + 1] = (byte) i_12_;
	}
	if (b + i_12_ - i > a.length)
	    a(i_12_ - i);
	int i_15_ = b;
	for (int i_16_ = i; i_16_ < i_11_; i_16_++) {
	    int i_17_ = string.charAt(i_16_);
	    if (i_17_ >= 1 && i_17_ <= 127)
		a[i_15_++] = (byte) i_17_;
	    else if (i_17_ > 2047) {
		a[i_15_++] = (byte) (0xe0 | i_17_ >> 12 & 0xf);
		a[i_15_++] = (byte) (0x80 | i_17_ >> 6 & 0x3f);
		a[i_15_++] = (byte) (0x80 | i_17_ & 0x3f);
	    } else {
		a[i_15_++] = (byte) (0xc0 | i_17_ >> 6 & 0x1f);
		a[i_15_++] = (byte) (0x80 | i_17_ & 0x3f);
	    }
	}
	b = i_15_;
	return this;
    }
    
    public ByteVector putByteArray(byte[] is, int i, int i_18_) {
	if (b + i_18_ > a.length)
	    a(i_18_);
	if (is != null)
	    System.arraycopy(is, i, a, b, i_18_);
	b += i_18_;
	return this;
    }
    
    private void a(int i) {
	int i_19_ = 2 * a.length;
	int i_20_ = b + i;
	byte[] is = new byte[i_19_ > i_20_ ? i_19_ : i_20_];
	System.arraycopy(a, 0, is, 0, b);
	a = is;
    }
}
