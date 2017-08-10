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
    label_332:
	{
	    super(327680);
	    if (classwriter.B != null)
		classwriter.C.fv = this;
	    else
		classwriter.B = this;
	    break label_332;
	}
	classwriter.C = this;
	b = classwriter;
	c = i;
	d = classwriter.newUTF8(string);
    label_333:
	{
	    e = classwriter.newUTF8(string_0_);
	    if (string_1_ != null)
		f = classwriter.newUTF8(string_1_);
	    break label_333;
	}
	if (object != null)
	    g = classwriter.a(object).a;
	return;
    }
    
    public AnnotationVisitor visitAnnotation(String string, boolean bool) {
	ByteVector bytevector = new ByteVector();
	bytevector.putShort(b.newUTF8(string)).putShort(0);
	AnnotationWriter annotationwriter;
    label_334:
	{
	    annotationwriter
		= new AnnotationWriter(b, true, bytevector, bytevector, 2);
	    if (!bool) {
		annotationwriter.g = i;
		i = annotationwriter;
	    } else {
		annotationwriter.g = h;
		h = annotationwriter;
	    }
	    break label_334;
	}
	return annotationwriter;
    }
    
    public AnnotationVisitor visitTypeAnnotation(int i, TypePath typepath,
						 String string, boolean bool) {
	ByteVector bytevector = new ByteVector();
	AnnotationWriter.a(i, typepath, bytevector);
	bytevector.putShort(b.newUTF8(string)).putShort(0);
	AnnotationWriter annotationwriter;
    label_335:
	{
	    annotationwriter
		= new AnnotationWriter(b, true, bytevector, bytevector,
				       bytevector.b - 2);
	    if (!bool) {
		annotationwriter.g = l;
		l = annotationwriter;
	    } else {
		annotationwriter.g = k;
		k = annotationwriter;
	    }
	    break label_335;
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
	int i;
    label_344:
	{
	label_343:
	    {
	    label_342:
		{
		label_341:
		    {
		    label_340:
			{
			label_339:
			    {
			    label_338:
				{
				label_337:
				    {
				    label_336:
					{
					    i = 8;
					    if (g != 0) {
						b.newUTF8("ConstantValue");
						i += 8;
					    }
					    break label_336;
					}
					if ((c & 0x1000) != 0
					    && ((b.b & 0xffff) < 49
						|| (c & 0x40000) != 0)) {
					    b.newUTF8("Synthetic");
					    i += 6;
					}
					break label_337;
				    }
				    if ((c & 0x20000) != 0) {
					b.newUTF8("Deprecated");
					i += 6;
				    }
				    break label_338;
				}
				if (f != 0) {
				    b.newUTF8("Signature");
				    i += 8;
				}
				break label_339;
			    }
			    if (h != null) {
				b.newUTF8("RuntimeVisibleAnnotations");
				i += 8 + h.a();
			    }
			    break label_340;
			}
			if (this.i != null) {
			    b.newUTF8("RuntimeInvisibleAnnotations");
			    i += 8 + this.i.a();
			}
			break label_341;
		    }
		    if (k != null) {
			b.newUTF8("RuntimeVisibleTypeAnnotations");
			i += 8 + k.a();
		    }
		    break label_342;
		}
		if (l != null) {
		    b.newUTF8("RuntimeInvisibleTypeAnnotations");
		    i += 8 + l.a();
		}
		break label_343;
	    }
	    if (j != null)
		i += j.a(b, null, 0, -1, -1);
	    break label_344;
	}
	return i;
    }
    
    void a(ByteVector bytevector) {
	int i = 64;
	int i_2_ = 0x60000 | (c & 0x40000) / 64;
	bytevector.putShort(c & (i_2_ ^ 0xffffffff)).putShort(d).putShort(e);
	int i_3_;
    label_353:
	{
	label_352:
	    {
	    label_351:
		{
		label_350:
		    {
		    label_349:
			{
			label_348:
			    {
			    label_347:
				{
				label_346:
				    {
				    label_345:
					{
					    i_3_ = 0;
					    if (g != 0)
						i_3_++;
					    break label_345;
					}
					if ((c & 0x1000) != 0
					    && ((b.b & 0xffff) < 49
						|| (c & 0x40000) != 0))
					    i_3_++;
					break label_346;
				    }
				    if ((c & 0x20000) != 0)
					i_3_++;
				    break label_347;
				}
				if (f != 0)
				    i_3_++;
				break label_348;
			    }
			    if (h != null)
				i_3_++;
			    break label_349;
			}
			if (this.i != null)
			    i_3_++;
			break label_350;
		    }
		    if (k != null)
			i_3_++;
		    break label_351;
		}
		if (l != null)
		    i_3_++;
		break label_352;
	    }
	    if (j != null)
		i_3_ += j.a();
	    break label_353;
	}
    label_361:
	{
	label_360:
	    {
	    label_359:
		{
		label_358:
		    {
		    label_357:
			{
			label_356:
			    {
			    label_355:
				{
				label_354:
				    {
					bytevector.putShort(i_3_);
					if (g != 0) {
					    bytevector.putShort
						(b.newUTF8("ConstantValue"));
					    bytevector.putInt(2).putShort(g);
					}
					break label_354;
				    }
				    if ((c & 0x1000) != 0
					&& ((b.b & 0xffff) < 49
					    || (c & 0x40000) != 0))
					bytevector.putShort
					    (b.newUTF8("Synthetic")).putInt(0);
				    break label_355;
				}
				if ((c & 0x20000) != 0)
				    bytevector.putShort
					(b.newUTF8("Deprecated")).putInt(0);
				break label_356;
			    }
			    if (f != 0) {
				bytevector.putShort(b.newUTF8("Signature"));
				bytevector.putInt(2).putShort(f);
			    }
			    break label_357;
			}
			if (h != null) {
			    bytevector.putShort
				(b.newUTF8("RuntimeVisibleAnnotations"));
			    h.a(bytevector);
			}
			break label_358;
		    }
		    if (this.i != null) {
			bytevector.putShort
			    (b.newUTF8("RuntimeInvisibleAnnotations"));
			this.i.a(bytevector);
		    }
		    break label_359;
		}
		if (k != null) {
		    bytevector
			.putShort(b.newUTF8("RuntimeVisibleTypeAnnotations"));
		    k.a(bytevector);
		}
		break label_360;
	    }
	    if (l != null) {
		bytevector
		    .putShort(b.newUTF8("RuntimeInvisibleTypeAnnotations"));
		l.a(bytevector);
	    }
	    break label_361;
	}
	if (j != null)
	    j.a(b, null, 0, -1, -1, bytevector);
	return;
    }
}
