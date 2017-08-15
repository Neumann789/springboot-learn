/* FieldWriter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm;

final class FieldWriter extends FieldVisitor
{
    private final ClassWriter b;
    private final int c;
    private final int d;
    private final int e;
    private int f;
    private int g;
    private AnnotationWriter h;
    private AnnotationWriter i;
    private AnnotationWriter k;
    private AnnotationWriter l;
    private Attribute j;
    
    FieldWriter(ClassWriter classwriter, int i, String string,
		String string_0_, String string_1_, Object object) {
	super(327680);
	if (classwriter.B == null)
	    classwriter.B = this;
	else
	    classwriter.C.fv = this;
	classwriter.C = this;
	b = classwriter;
	c = i;
	d = classwriter.newUTF8(string);
	e = classwriter.newUTF8(string_0_);
	if (string_1_ != null)
	    f = classwriter.newUTF8(string_1_);
	if (object != null)
	    g = classwriter.a(object).a;
    }
    
    public AnnotationVisitor visitAnnotation(String string, boolean bool) {
	ByteVector bytevector = new ByteVector();
	bytevector.putShort(b.newUTF8(string)).putShort(0);
	AnnotationWriter annotationwriter
	    = new AnnotationWriter(b, true, bytevector, bytevector, 2);
	if (bool) {
	    annotationwriter.g = h;
	    h = annotationwriter;
	} else {
	    annotationwriter.g = i;
	    i = annotationwriter;
	}
	return annotationwriter;
    }
    
    public AnnotationVisitor visitTypeAnnotation(int i, TypePath typepath,
						 String string, boolean bool) {
	ByteVector bytevector = new ByteVector();
	AnnotationWriter.a(i, typepath, bytevector);
	bytevector.putShort(b.newUTF8(string)).putShort(0);
	AnnotationWriter annotationwriter
	    = new AnnotationWriter(b, true, bytevector, bytevector,
				   bytevector.b - 2);
	if (bool) {
	    annotationwriter.g = k;
	    k = annotationwriter;
	} else {
	    annotationwriter.g = l;
	    l = annotationwriter;
	}
	return annotationwriter;
    }
    
    public void visitAttribute(Attribute attribute) {
	attribute.a = j;
	j = attribute;
    }
    
    public void visitEnd() {
	/* empty */
    }
    
    int a() {
	int i = 8;
	if (g != 0) {
	    b.newUTF8("ConstantValue");
	    i += 8;
	}
	if ((c & 0x1000) != 0 && ((b.b & 0xffff) < 49 || (c & 0x40000) != 0)) {
	    b.newUTF8("Synthetic");
	    i += 6;
	}
	if ((c & 0x20000) != 0) {
	    b.newUTF8("Deprecated");
	    i += 6;
	}
	if (f != 0) {
	    b.newUTF8("Signature");
	    i += 8;
	}
	if (h != null) {
	    b.newUTF8("RuntimeVisibleAnnotations");
	    i += 8 + h.a();
	}
	if (this.i != null) {
	    b.newUTF8("RuntimeInvisibleAnnotations");
	    i += 8 + this.i.a();
	}
	if (k != null) {
	    b.newUTF8("RuntimeVisibleTypeAnnotations");
	    i += 8 + k.a();
	}
	if (l != null) {
	    b.newUTF8("RuntimeInvisibleTypeAnnotations");
	    i += 8 + l.a();
	}
	if (j != null)
	    i += j.a(b, null, 0, -1, -1);
	return i;
    }
    
    void a(ByteVector bytevector) {
	int i = 64;
	int i_2_ = 0x60000 | (c & 0x40000) / 64;
	bytevector.putShort(c & (i_2_ ^ 0xffffffff)).putShort(d).putShort(e);
	int i_3_ = 0;
	if (g != 0)
	    i_3_++;
	if ((c & 0x1000) != 0 && ((b.b & 0xffff) < 49 || (c & 0x40000) != 0))
	    i_3_++;
	if ((c & 0x20000) != 0)
	    i_3_++;
	if (f != 0)
	    i_3_++;
	if (h != null)
	    i_3_++;
	if (this.i != null)
	    i_3_++;
	if (k != null)
	    i_3_++;
	if (l != null)
	    i_3_++;
	if (j != null)
	    i_3_ += j.a();
	bytevector.putShort(i_3_);
	if (g != 0) {
	    bytevector.putShort(b.newUTF8("ConstantValue"));
	    bytevector.putInt(2).putShort(g);
	}
	if ((c & 0x1000) != 0 && ((b.b & 0xffff) < 49 || (c & 0x40000) != 0))
	    bytevector.putShort(b.newUTF8("Synthetic")).putInt(0);
	if ((c & 0x20000) != 0)
	    bytevector.putShort(b.newUTF8("Deprecated")).putInt(0);
	if (f != 0) {
	    bytevector.putShort(b.newUTF8("Signature"));
	    bytevector.putInt(2).putShort(f);
	}
	if (h != null) {
	    bytevector.putShort(b.newUTF8("RuntimeVisibleAnnotations"));
	    h.a(bytevector);
	}
	if (this.i != null) {
	    bytevector.putShort(b.newUTF8("RuntimeInvisibleAnnotations"));
	    this.i.a(bytevector);
	}
	if (k != null) {
	    bytevector.putShort(b.newUTF8("RuntimeVisibleTypeAnnotations"));
	    k.a(bytevector);
	}
	if (l != null) {
	    bytevector.putShort(b.newUTF8("RuntimeInvisibleTypeAnnotations"));
	    l.a(bytevector);
	}
	if (j != null)
	    j.a(b, null, 0, -1, -1, bytevector);
    }
}
