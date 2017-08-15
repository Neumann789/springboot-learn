/* AnnotationWriter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm;

final class AnnotationWriter extends AnnotationVisitor
{
    private final ClassWriter a;
    private int b;
    private final boolean c;
    private final ByteVector d;
    private final ByteVector e;
    private final int f;
    AnnotationWriter g;
    AnnotationWriter h;
    
    AnnotationWriter(ClassWriter classwriter, boolean bool,
		     ByteVector bytevector, ByteVector bytevector_0_, int i) {
	super(327680);
	a = classwriter;
	c = bool;
	d = bytevector;
	e = bytevector_0_;
	f = i;
    }
    
    public void visit(String string, Object object) {
	b++;
	if (c)
	    d.putShort(a.newUTF8(string));
	if (object instanceof String)
	    d.b(115, a.newUTF8((String) object));
	else if (object instanceof Byte)
	    d.b(66, a.a(((Byte) object).byteValue()).a);
	else if (object instanceof Boolean) {
	    int i = ((Boolean) object).booleanValue() ? 1 : 0;
	    d.b(90, a.a(i).a);
	} else if (object instanceof Character)
	    d.b(67, a.a(((Character) object).charValue()).a);
	else if (object instanceof Short)
	    d.b(83, a.a(((Short) object).shortValue()).a);
	else if (object instanceof Type)
	    d.b(99, a.newUTF8(((Type) object).getDescriptor()));
	else if (object instanceof byte[]) {
	    byte[] is = (byte[]) object;
	    d.b(91, is.length);
	    for (int i = 0; i < is.length; i++)
		d.b(66, a.a(is[i]).a);
	} else if (object instanceof boolean[]) {
	    boolean[] bools = (boolean[]) object;
	    d.b(91, bools.length);
	    for (int i = 0; i < bools.length; i++)
		d.b(90, a.a(bools[i] ? 1 : 0).a);
	} else if (object instanceof short[]) {
	    short[] is = (short[]) object;
	    d.b(91, is.length);
	    for (int i = 0; i < is.length; i++)
		d.b(83, a.a(is[i]).a);
	} else if (object instanceof char[]) {
	    char[] cs = (char[]) object;
	    d.b(91, cs.length);
	    for (int i = 0; i < cs.length; i++)
		d.b(67, a.a(cs[i]).a);
	} else if (object instanceof int[]) {
	    int[] is = (int[]) object;
	    d.b(91, is.length);
	    for (int i = 0; i < is.length; i++)
		d.b(73, a.a(is[i]).a);
	} else if (object instanceof long[]) {
	    long[] ls = (long[]) object;
	    d.b(91, ls.length);
	    for (int i = 0; i < ls.length; i++)
		d.b(74, a.a(ls[i]).a);
	} else if (object instanceof float[]) {
	    float[] fs = (float[]) object;
	    d.b(91, fs.length);
	    for (int i = 0; i < fs.length; i++)
		d.b(70, a.a(fs[i]).a);
	} else if (object instanceof double[]) {
	    double[] ds = (double[]) object;
	    d.b(91, ds.length);
	    for (int i = 0; i < ds.length; i++)
		d.b(68, a.a(ds[i]).a);
	} else {
	    Item item = a.a(object);
	    d.b(".s.IFJDCS".charAt(item.b), item.a);
	}
    }
    
    public void visitEnum(String string, String string_1_, String string_2_) {
	b++;
	if (c)
	    d.putShort(a.newUTF8(string));
	d.b(101, a.newUTF8(string_1_)).putShort(a.newUTF8(string_2_));
    }
    
    public AnnotationVisitor visitAnnotation(String string, String string_3_) {
	b++;
	if (c)
	    d.putShort(a.newUTF8(string));
	d.b(64, a.newUTF8(string_3_)).putShort(0);
	return new AnnotationWriter(a, true, d, d, d.b - 2);
    }
    
    public AnnotationVisitor visitArray(String string) {
	b++;
	if (c)
	    d.putShort(a.newUTF8(string));
	d.b(91, 0);
	return new AnnotationWriter(a, false, d, d, d.b - 2);
    }
    
    public void visitEnd() {
	if (e != null) {
	    byte[] is = e.a;
	    is[f] = (byte) (b >>> 8);
	    is[f + 1] = (byte) b;
	}
    }
    
    int a() {
	int i = 0;
	for (AnnotationWriter annotationwriter_4_ = this;
	     annotationwriter_4_ != null;
	     annotationwriter_4_ = annotationwriter_4_.g)
	    i += annotationwriter_4_.d.b;
	return i;
    }
    
    void a(ByteVector bytevector) {
	int i = 0;
	int i_5_ = 2;
	AnnotationWriter annotationwriter_6_ = this;
	AnnotationWriter annotationwriter_7_ = null;
	for (/**/; annotationwriter_6_ != null;
	     annotationwriter_6_ = annotationwriter_6_.g) {
	    i++;
	    i_5_ += annotationwriter_6_.d.b;
	    annotationwriter_6_.visitEnd();
	    annotationwriter_6_.h = annotationwriter_7_;
	    annotationwriter_7_ = annotationwriter_6_;
	}
	bytevector.putInt(i_5_);
	bytevector.putShort(i);
	for (annotationwriter_6_ = annotationwriter_7_;
	     annotationwriter_6_ != null;
	     annotationwriter_6_ = annotationwriter_6_.h)
	    bytevector.putByteArray(annotationwriter_6_.d.a, 0,
				    annotationwriter_6_.d.b);
    }
    
    static void a(AnnotationWriter[] annotationwriters, int i,
		  ByteVector bytevector) {
	int i_8_ = 1 + 2 * (annotationwriters.length - i);
	for (int i_9_ = i; i_9_ < annotationwriters.length; i_9_++)
	    i_8_ = i_8_ + (annotationwriters[i_9_] == null ? 0
			   : annotationwriters[i_9_].a());
	bytevector.putInt(i_8_).putByte(annotationwriters.length - i);
	for (int i_10_ = i; i_10_ < annotationwriters.length; i_10_++) {
	    AnnotationWriter annotationwriter = annotationwriters[i_10_];
	    AnnotationWriter annotationwriter_11_ = null;
	    int i_12_ = 0;
	    for (/**/; annotationwriter != null;
		 annotationwriter = annotationwriter.g) {
		i_12_++;
		annotationwriter.visitEnd();
		annotationwriter.h = annotationwriter_11_;
		annotationwriter_11_ = annotationwriter;
	    }
	    bytevector.putShort(i_12_);
	    for (annotationwriter = annotationwriter_11_;
		 annotationwriter != null;
		 annotationwriter = annotationwriter.h)
		bytevector.putByteArray(annotationwriter.d.a, 0,
					annotationwriter.d.b);
	}
    }
    
    static void a(int i, TypePath typepath, ByteVector bytevector) {
	switch (i >>> 24) {
	case 0:
	case 1:
	case 22:
	    bytevector.putShort(i >>> 16);
	    break;
	case 19:
	case 20:
	case 21:
	    bytevector.putByte(i >>> 24);
	    break;
	case 71:
	case 72:
	case 73:
	case 74:
	case 75:
	    bytevector.putInt(i);
	    break;
	default:
	    bytevector.b(i >>> 24, (i & 0xffff00) >> 8);
	}
	if (typepath == null)
	    bytevector.putByte(0);
	else {
	    int i_13_ = typepath.a[typepath.b] * 2 + 1;
	    bytevector.putByteArray(typepath.a, typepath.b, i_13_);
	}
    }
}
