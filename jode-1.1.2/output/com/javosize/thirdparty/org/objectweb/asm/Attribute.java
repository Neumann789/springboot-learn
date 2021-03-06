/* Attribute - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm;

public class Attribute
{
    public final String type;
    byte[] b;
    Attribute a;
    
    protected Attribute(String string) {
	type = string;
    }
    
    public boolean isUnknown() {
	return true;
    }
    
    public boolean isCodeAttribute() {
	return false;
    }
    
    protected Label[] getLabels() {
	return null;
    }
    
    protected Attribute read(ClassReader classreader, int i, int i_0_,
			     char[] cs, int i_1_, Label[] labels) {
	Attribute attribute_2_ = new Attribute(type);
	attribute_2_.b = new byte[i_0_];
	System.arraycopy(classreader.b, i, attribute_2_.b, 0, i_0_);
	return attribute_2_;
    }
    
    protected ByteVector write(ClassWriter classwriter, byte[] is, int i,
			       int i_3_, int i_4_) {
	ByteVector bytevector = new ByteVector();
	bytevector.a = b;
	bytevector.b = b.length;
	return bytevector;
    }
    
    final int a() {
	int i = 0;
	for (Attribute attribute_5_ = this; attribute_5_ != null;
	     attribute_5_ = attribute_5_.a)
	    i++;
	return i;
    }
    
    final int a(ClassWriter classwriter, byte[] is, int i, int i_6_,
		int i_7_) {
	Attribute attribute_8_ = this;
	int i_9_ = 0;
	for (/**/; attribute_8_ != null; attribute_8_ = attribute_8_.a) {
	    classwriter.newUTF8(attribute_8_.type);
	    i_9_ += attribute_8_.write(classwriter, is, i, i_6_, i_7_).b + 6;
	}
	return i_9_;
    }
    
    final void a(ClassWriter classwriter, byte[] is, int i, int i_10_,
		 int i_11_, ByteVector bytevector) {
	for (Attribute attribute_12_ = this; attribute_12_ != null;
	     attribute_12_ = attribute_12_.a) {
	    ByteVector bytevector_13_
		= attribute_12_.write(classwriter, is, i, i_10_, i_11_);
	    bytevector.putShort(classwriter.newUTF8(attribute_12_.type))
		.putInt(bytevector_13_.b);
	    bytevector.putByteArray(bytevector_13_.a, 0, bytevector_13_.b);
	}
    }
}
