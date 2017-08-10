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
    label_308:
	{
	    b++;
	    if (c)
		d.putShort(a.newUTF8(string));
	    break label_308;
	}
    label_309:
	{
	    if (!(object instanceof String)) {
		if (!(object instanceof Byte)) {
		    if (!(object instanceof Boolean)) {
			if (!(object instanceof Character)) {
			    if (!(object instanceof Short)) {
				if (!(object instanceof Type)) {
				    if (!(object instanceof byte[])) {
					if (!(object instanceof boolean[])) {
					    if (!(object instanceof short[])) {
						if (!(object
						      instanceof char[])) {
						    if (!(object
							  instanceof int[])) {
							if (!(object
							      instanceof long[])) {
							    if (!(object
								  instanceof float[])) {
								if (!(object
								      instanceof double[])) {
								    Item item
									= (a.a
									   (object));
								    d.b((".s.IFJDCS"
									     .charAt
									 (item
									  .b)),
									(item
									 .a));
								} else {
								    double[] ds
									= ((double[])
									   object);
								    d.b(91,
									ds.length);
								    for (int i
									     = 0;
									 i < ds.length;
									 i++)
									d.b(68,
									    (a.a
									     (ds
									      [i])
									     .a));
								}
							    } else {
								float[] fs
								    = ((float[])
								       object);
								d.b(91,
								    fs.length);
								for (int i = 0;
								     i < fs.length;
								     i++)
								    d.b(70,
									(a.a
									 (fs
									  [i])
									 .a));
							    }
							} else {
							    long[] ls
								= ((long[])
								   object);
							    d.b(91, ls.length);
							    for (int i = 0;
								 i < ls.length;
								 i++)
								d.b(74,
								    (a.a(ls[i])
								     .a));
							}
						    } else {
							int[] is
							    = (int[]) object;
							d.b(91, is.length);
							for (int i = 0;
							     i < is.length;
							     i++)
							    d.b(73,
								a.a(is[i]).a);
						    }
						} else {
						    char[] cs
							= (char[]) object;
						    d.b(91, cs.length);
						    for (int i = 0;
							 i < cs.length; i++)
							d.b(67, a.a(cs[i]).a);
						}
					    } else {
						short[] is = (short[]) object;
						d.b(91, is.length);
						for (int i = 0; i < is.length;
						     i++)
						    d.b(83, a.a(is[i]).a);
					    }
					} else {
					    boolean[] bools
						= (boolean[]) object;
					    d.b(91, bools.length);
					    for (int i = 0; i < bools.length;
						 i++) {
						PUSH d;
						PUSH 90;
					    label_310:
						{
						    PUSH a;
						    if (!bools[i])
							PUSH false;
						    else
							PUSH true;
						    break label_310;
						}
						((ByteVector) POP).b
						    (POP, ((ClassWriter) POP)
							      .a(POP).a);
					    }
					}
				    } else {
					byte[] is = (byte[]) object;
					d.b(91, is.length);
					for (int i = 0; i < is.length; i++)
					    d.b(66, a.a(is[i]).a);
				    }
				} else
				    d.b(99, a.newUTF8(((Type) object)
							  .getDescriptor()));
			    } else
				d.b(83, a.a(((Short) object).shortValue()).a);
			} else
			    d.b(67, a.a(((Character) object).charValue()).a);
			return;
		    }
		    if (!((Boolean) object).booleanValue())
			PUSH false;
		    else
			PUSH true;
		} else {
		    d.b(66, a.a(((Byte) object).byteValue()).a);
		    return;
		}
	    } else {
		d.b(115, a.newUTF8((String) object));
		return;
	    }
	}
	int i = POP;
	d.b(90, a.a(i).a);
	break label_309;
    }
    
    public void visitEnum(String string, String string_1_, String string_2_) {
    label_311:
	{
	    b++;
	    if (c)
		d.putShort(a.newUTF8(string));
	    break label_311;
	}
	d.b(101, a.newUTF8(string_1_)).putShort(a.newUTF8(string_2_));
    }
    
    public AnnotationVisitor visitAnnotation(String string, String string_3_) {
    label_312:
	{
	    b++;
	    if (c)
		d.putShort(a.newUTF8(string));
	    break label_312;
	}
	d.b(64, a.newUTF8(string_3_)).putShort(0);
	return new AnnotationWriter(a, true, d, d, d.b - 2);
    }
    
    public AnnotationVisitor visitArray(String string) {
    label_313:
	{
	    b++;
	    if (c)
		d.putShort(a.newUTF8(string));
	    break label_313;
	}
	d.b(91, 0);
	return new AnnotationWriter(a, false, d, d, d.b - 2);
    }
    
    public void visitEnd() {
	if (e != null) {
	    byte[] is = e.a;
	    is[f] = (byte) (b >>> 8);
	    is[f + 1] = (byte) b;
	}
	return;
    }
    
    int a() {
	int i = 0;
	AnnotationWriter annotationwriter_4_ = this;
	for (;;) {
	    if (annotationwriter_4_ == null)
		return i;
	    i += annotationwriter_4_.d.b;
	    annotationwriter_4_ = annotationwriter_4_.g;
	}
    }
    
    void a(ByteVector bytevector) {
	int i = 0;
	int i_5_ = 2;
	AnnotationWriter annotationwriter_6_ = this;
	AnnotationWriter annotationwriter_7_ = null;
	for (;;) {
	    if (annotationwriter_6_ == null) {
		bytevector.putInt(i_5_);
		bytevector.putShort(i);
		annotationwriter_6_ = annotationwriter_7_;
		for (;;) {
		    IF (annotationwriter_6_ == null)
			/* empty */
		    bytevector.putByteArray(annotationwriter_6_.d.a, 0,
					    annotationwriter_6_.d.b);
		    annotationwriter_6_ = annotationwriter_6_.h;
		}
	    }
	    i++;
	    i_5_ += annotationwriter_6_.d.b;
	    annotationwriter_6_.visitEnd();
	    annotationwriter_6_.h = annotationwriter_7_;
	    annotationwriter_7_ = annotationwriter_6_;
	    annotationwriter_6_ = annotationwriter_6_.g;
	}
    }
    
    static void a(AnnotationWriter[] annotationwriters, int i,
		  ByteVector bytevector) {
	int i_8_ = 1 + 2 * (annotationwriters.length - i);
	int i_9_ = i;
	for (;;) {
	    if (i_9_ >= annotationwriters.length) {
		bytevector.putInt(i_8_).putByte(annotationwriters.length - i);
		i_9_ = i;
		for (;;) {
		    IF (i_9_ >= annotationwriters.length)
			/* empty */
		    AnnotationWriter annotationwriter
			= annotationwriters[i_9_];
		    AnnotationWriter annotationwriter_10_ = null;
		    int i_11_ = 0;
		    for (;;) {
			if (annotationwriter == null) {
			    bytevector.putShort(i_11_);
			    annotationwriter = annotationwriter_10_;
			    for (;;) {
				if (annotationwriter == null)
				    i_9_++;
				bytevector.putByteArray(annotationwriter.d.a,
							0,
							annotationwriter.d.b);
				annotationwriter = annotationwriter.h;
			    }
			}
			i_11_++;
			annotationwriter.visitEnd();
			annotationwriter.h = annotationwriter_10_;
			annotationwriter_10_ = annotationwriter;
			annotationwriter = annotationwriter.g;
		    }
		}
	    }
	label_314:
	    {
		PUSH i_8_;
		if (annotationwriters[i_9_] != null)
		    PUSH annotationwriters[i_9_].a();
		else
		    PUSH false;
		break label_314;
	    }
	    i_8_ = POP + POP;
	    i_9_++;
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
	if (typepath != null) {
	    int i_12_ = typepath.a[typepath.b] * 2 + 1;
	    bytevector.putByteArray(typepath.a, typepath.b, i_12_);
	} else
	    bytevector.putByte(0);
	return;
    }
}
