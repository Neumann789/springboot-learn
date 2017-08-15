/* MethodWriter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm;

class MethodWriter extends MethodVisitor
{
    final ClassWriter b;
    private int c;
    private final int d;
    private final int e;
    private final String f;
    String g;
    int h;
    int i;
    int j;
    int[] k;
    private ByteVector l;
    private AnnotationWriter m;
    private AnnotationWriter n;
    private AnnotationWriter U;
    private AnnotationWriter V;
    private AnnotationWriter[] o;
    private AnnotationWriter[] p;
    private int S;
    private Attribute q;
    private ByteVector r = new ByteVector();
    private int s;
    private int t;
    private int T;
    private int u;
    private ByteVector v;
    private int w;
    private int[] x;
    private int[] z;
    private int A;
    private Handler B;
    private Handler C;
    private int Z;
    private ByteVector $;
    private int D;
    private ByteVector E;
    private int F;
    private ByteVector G;
    private int H;
    private ByteVector I;
    private int Y;
    private AnnotationWriter W;
    private AnnotationWriter X;
    private Attribute J;
    private boolean K;
    private int L;
    private final int M;
    private Label N;
    private Label O;
    private Label P;
    private int Q;
    private int R;
    
    MethodWriter(ClassWriter classwriter, int i, String string,
		 String string_0_, String string_1_, String[] strings,
		 boolean bool, boolean bool_2_) {
	super(327680);
	if (classwriter.D == null)
	    classwriter.D = this;
	else
	    classwriter.E.mv = this;
	classwriter.E = this;
	b = classwriter;
	c = i;
	if ("<init>".equals(string))
	    c |= 0x80000;
	d = classwriter.newUTF8(string);
	e = classwriter.newUTF8(string_0_);
	f = string_0_;
	g = string_1_;
	if (strings != null && strings.length > 0) {
	    j = strings.length;
	    k = new int[j];
	    for (int i_3_ = 0; i_3_ < j; i_3_++)
		k[i_3_] = classwriter.newClass(strings[i_3_]);
	}
	M = bool_2_ ? 0 : bool ? 1 : 2;
	if (bool || bool_2_) {
	    int i_4_ = Type.getArgumentsAndReturnSizes(f) >> 2;
	    if ((i & 0x8) != 0)
		i_4_--;
	    t = i_4_;
	    T = i_4_;
	    N = new Label();
	    N.a |= 0x8;
	    visitLabel(N);
	}
    }
    
    public void visitParameter(String string, int i) {
	if ($ == null)
	    $ = new ByteVector();
	Z++;
	$.putShort(string == null ? 0 : b.newUTF8(string)).putShort(i);
    }
    
    public AnnotationVisitor visitAnnotationDefault() {
	l = new ByteVector();
	return new AnnotationWriter(b, false, l, null, 0);
    }
    
    public AnnotationVisitor visitAnnotation(String string, boolean bool) {
	ByteVector bytevector = new ByteVector();
	bytevector.putShort(b.newUTF8(string)).putShort(0);
	AnnotationWriter annotationwriter
	    = new AnnotationWriter(b, true, bytevector, bytevector, 2);
	if (bool) {
	    annotationwriter.g = m;
	    m = annotationwriter;
	} else {
	    annotationwriter.g = n;
	    n = annotationwriter;
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
	    annotationwriter.g = U;
	    U = annotationwriter;
	} else {
	    annotationwriter.g = V;
	    V = annotationwriter;
	}
	return annotationwriter;
    }
    
    public AnnotationVisitor visitParameterAnnotation(int i, String string,
						      boolean bool) {
	ByteVector bytevector = new ByteVector();
	if ("Ljava/lang/Synthetic;".equals(string)) {
	    S = Math.max(S, i + 1);
	    return new AnnotationWriter(b, false, bytevector, null, 0);
	}
	bytevector.putShort(b.newUTF8(string)).putShort(0);
	AnnotationWriter annotationwriter
	    = new AnnotationWriter(b, true, bytevector, bytevector, 2);
	if (bool) {
	    if (o == null)
		o = new AnnotationWriter[Type.getArgumentTypes(f).length];
	    annotationwriter.g = o[i];
	    o[i] = annotationwriter;
	} else {
	    if (p == null)
		p = new AnnotationWriter[Type.getArgumentTypes(f).length];
	    annotationwriter.g = p[i];
	    p[i] = annotationwriter;
	}
	return annotationwriter;
    }
    
    public void visitAttribute(Attribute attribute) {
	if (attribute.isCodeAttribute()) {
	    attribute.a = J;
	    J = attribute;
	} else {
	    attribute.a = q;
	    q = attribute;
	}
    }
    
    public void visitCode() {
	/* empty */
    }
    
    public void visitFrame(int i, int i_5_, Object[] objects, int i_6_,
			   Object[] objects_7_) {
	if (M != 0) {
	    if (i == -1) {
		if (x == null)
		    f();
		T = i_5_;
		int i_8_ = a(r.b, i_5_, i_6_);
		for (int i_9_ = 0; i_9_ < i_5_; i_9_++) {
		    if (objects[i_9_] instanceof String)
			z[i_8_++] = 0x1700000 | b.c((String) objects[i_9_]);
		    else if (objects[i_9_] instanceof Integer)
			z[i_8_++] = ((Integer) objects[i_9_]).intValue();
		    else
			z[i_8_++]
			    = 0x1800000 | b.a("", ((Label) objects[i_9_]).c);
		}
		for (int i_10_ = 0; i_10_ < i_6_; i_10_++) {
		    if (objects_7_[i_10_] instanceof String)
			z[i_8_++]
			    = 0x1700000 | b.c((String) objects_7_[i_10_]);
		    else if (objects_7_[i_10_] instanceof Integer)
			z[i_8_++] = ((Integer) objects_7_[i_10_]).intValue();
		    else
			z[i_8_++]
			    = 0x1800000 | b.a("",
					      ((Label) objects_7_[i_10_]).c);
		}
		b();
	    } else {
		int i_11_;
		if (v == null) {
		    v = new ByteVector();
		    i_11_ = r.b;
		} else {
		    i_11_ = r.b - w - 1;
		    if (i_11_ < 0) {
			if (i == 3)
			    return;
			throw new IllegalStateException();
		    }
		}
		switch (i) {
		case 0:
		    T = i_5_;
		    v.putByte(255).putShort(i_11_).putShort(i_5_);
		    for (int i_12_ = 0; i_12_ < i_5_; i_12_++)
			a(objects[i_12_]);
		    v.putShort(i_6_);
		    for (int i_13_ = 0; i_13_ < i_6_; i_13_++)
			a(objects_7_[i_13_]);
		    break;
		case 1:
		    T += i_5_;
		    v.putByte(251 + i_5_).putShort(i_11_);
		    for (int i_14_ = 0; i_14_ < i_5_; i_14_++)
			a(objects[i_14_]);
		    break;
		case 2:
		    T -= i_5_;
		    v.putByte(251 - i_5_).putShort(i_11_);
		    break;
		case 3:
		    if (i_11_ < 64)
			v.putByte(i_11_);
		    else
			v.putByte(251).putShort(i_11_);
		    break;
		case 4:
		    if (i_11_ < 64)
			v.putByte(64 + i_11_);
		    else
			v.putByte(247).putShort(i_11_);
		    a(objects_7_[0]);
		    break;
		}
		w = r.b;
		u++;
	    }
	    s = Math.max(s, i_6_);
	    t = Math.max(t, T);
	}
    }
    
    public void visitInsn(int i) {
	Y = r.b;
	r.putByte(i);
	if (P != null) {
	    if (M == 0)
		P.h.a(i, 0, null, null);
	    else {
		int i_15_ = Q + Frame.a[i];
		if (i_15_ > R)
		    R = i_15_;
		Q = i_15_;
	    }
	    if (i >= 172 && i <= 177 || i == 191)
		e();
	}
    }
    
    public void visitIntInsn(int i, int i_16_) {
	Y = r.b;
	if (P != null) {
	    if (M == 0)
		P.h.a(i, i_16_, null, null);
	    else if (i != 188) {
		int i_17_ = Q + 1;
		if (i_17_ > R)
		    R = i_17_;
		Q = i_17_;
	    }
	}
	if (i == 17)
	    r.b(i, i_16_);
	else
	    r.a(i, i_16_);
    }
    
    public void visitVarInsn(int i, int i_18_) {
	Y = r.b;
	if (P != null) {
	    if (M == 0)
		P.h.a(i, i_18_, null, null);
	    else if (i == 169) {
		P.a |= 0x100;
		P.f = Q;
		e();
	    } else {
		int i_19_ = Q + Frame.a[i];
		if (i_19_ > R)
		    R = i_19_;
		Q = i_19_;
	    }
	}
	if (M != 2) {
	    int i_20_;
	    if (i == 22 || i == 24 || i == 55 || i == 57)
		i_20_ = i_18_ + 2;
	    else
		i_20_ = i_18_ + 1;
	    if (i_20_ > t)
		t = i_20_;
	}
	if (i_18_ < 4 && i != 169) {
	    int i_21_;
	    if (i < 54)
		i_21_ = 26 + (i - 21 << 2) + i_18_;
	    else
		i_21_ = 59 + (i - 54 << 2) + i_18_;
	    r.putByte(i_21_);
	} else if (i_18_ >= 256)
	    r.putByte(196).b(i, i_18_);
	else
	    r.a(i, i_18_);
	if (i >= 54 && M == 0 && A > 0)
	    visitLabel(new Label());
    }
    
    public void visitTypeInsn(int i, String string) {
	Y = r.b;
	Item item = b.a(string);
	if (P != null) {
	    if (M == 0)
		P.h.a(i, r.b, b, item);
	    else if (i == 187) {
		int i_22_ = Q + 1;
		if (i_22_ > R)
		    R = i_22_;
		Q = i_22_;
	    }
	}
	r.b(i, item.a);
    }
    
    public void visitFieldInsn(int i, String string, String string_23_,
			       String string_24_) {
	Y = r.b;
	Item item = b.a(string, string_23_, string_24_);
	if (P != null) {
	    if (M == 0)
		P.h.a(i, 0, b, item);
	    else {
		char c = string_24_.charAt(0);
		int i_25_;
		switch (i) {
		case 178:
		    i_25_ = Q + (c == 'D' || c == 'J' ? 2 : 1);
		    break;
		case 179:
		    i_25_ = Q + (c == 'D' || c == 'J' ? -2 : -1);
		    break;
		case 180:
		    i_25_ = Q + (c == 'D' || c == 'J' ? 1 : 0);
		    break;
		default:
		    i_25_ = Q + (c == 'D' || c == 'J' ? -3 : -2);
		}
		if (i_25_ > R)
		    R = i_25_;
		Q = i_25_;
	    }
	}
	r.b(i, item.a);
    }
    
    public void visitMethodInsn(int i, String string, String string_26_,
				String string_27_, boolean bool) {
	Y = r.b;
	Item item = b.a(string, string_26_, string_27_, bool);
	int i_28_ = item.c;
	if (P != null) {
	    if (M == 0)
		P.h.a(i, 0, b, item);
	    else {
		if (i_28_ == 0) {
		    i_28_ = Type.getArgumentsAndReturnSizes(string_27_);
		    item.c = i_28_;
		}
		int i_29_;
		if (i == 184)
		    i_29_ = Q - (i_28_ >> 2) + (i_28_ & 0x3) + 1;
		else
		    i_29_ = Q - (i_28_ >> 2) + (i_28_ & 0x3);
		if (i_29_ > R)
		    R = i_29_;
		Q = i_29_;
	    }
	}
	if (i == 185) {
	    if (i_28_ == 0) {
		i_28_ = Type.getArgumentsAndReturnSizes(string_27_);
		item.c = i_28_;
	    }
	    r.b(185, item.a).a(i_28_ >> 2, 0);
	} else
	    r.b(i, item.a);
    }
    
    public transient void visitInvokeDynamicInsn
	(String string, String string_30_, Handle handle, Object[] objects) {
	Y = r.b;
	Item item = b.a(string, string_30_, handle, objects);
	int i = item.c;
	if (P != null) {
	    if (M == 0)
		P.h.a(186, 0, b, item);
	    else {
		if (i == 0) {
		    i = Type.getArgumentsAndReturnSizes(string_30_);
		    item.c = i;
		}
		int i_31_ = Q - (i >> 2) + (i & 0x3) + 1;
		if (i_31_ > R)
		    R = i_31_;
		Q = i_31_;
	    }
	}
	r.b(186, item.a);
	r.putShort(0);
    }
    
    public void visitJumpInsn(int i, Label label) {
	Y = r.b;
	Label label_32_ = null;
	if (P != null) {
	    if (M == 0) {
		P.h.a(i, 0, null, null);
		label.a().a |= 0x10;
		a(0, label);
		if (i != 167)
		    label_32_ = new Label();
	    } else if (i == 168) {
		if ((label.a & 0x200) == 0) {
		    label.a |= 0x200;
		    L++;
		}
		P.a |= 0x80;
		a(Q + 1, label);
		label_32_ = new Label();
	    } else {
		Q += Frame.a[i];
		a(Q, label);
	    }
	}
	if ((label.a & 0x2) != 0 && label.c - r.b < -32768) {
	    if (i == 167)
		r.putByte(200);
	    else if (i == 168)
		r.putByte(201);
	    else {
		if (label_32_ != null)
		    label_32_.a |= 0x10;
		r.putByte(i <= 166 ? (i + 1 ^ 0x1) - 1 : i ^ 0x1);
		r.putShort(8);
		r.putByte(200);
	    }
	    label.a(this, r, r.b - 1, true);
	} else {
	    r.putByte(i);
	    label.a(this, r, r.b - 1, false);
	}
	if (P != null) {
	    if (label_32_ != null)
		visitLabel(label_32_);
	    if (i == 167)
		e();
	}
    }
    
    public void visitLabel(Label label) {
	K |= label.a(this, r.b, r.a);
	if ((label.a & 0x1) == 0) {
	    if (M == 0) {
		if (P != null) {
		    if (label.c == P.c) {
			P.a |= label.a & 0x10;
			label.h = P.h;
			return;
		    }
		    a(0, label);
		}
		P = label;
		if (label.h == null) {
		    label.h = new Frame();
		    label.h.b = label;
		}
		if (O != null) {
		    if (label.c == O.c) {
			O.a |= label.a & 0x10;
			label.h = O.h;
			P = O;
			return;
		    }
		    O.i = label;
		}
		O = label;
	    } else if (M == 1) {
		if (P != null) {
		    P.g = R;
		    a(Q, label);
		}
		P = label;
		Q = 0;
		R = 0;
		if (O != null)
		    O.i = label;
		O = label;
	    }
	}
    }
    
    public void visitLdcInsn(Object object) {
	Y = r.b;
	Item item = b.a(object);
	if (P != null) {
	    if (M == 0)
		P.h.a(18, 0, b, item);
	    else {
		int i;
		if (item.b == 5 || item.b == 6)
		    i = Q + 2;
		else
		    i = Q + 1;
		if (i > R)
		    R = i;
		Q = i;
	    }
	}
	int i = item.a;
	if (item.b == 5 || item.b == 6)
	    r.b(20, i);
	else if (i >= 256)
	    r.b(19, i);
	else
	    r.a(18, i);
    }
    
    public void visitIincInsn(int i, int i_33_) {
	Y = r.b;
	if (P != null && M == 0)
	    P.h.a(132, i, null, null);
	if (M != 2) {
	    int i_34_ = i + 1;
	    if (i_34_ > t)
		t = i_34_;
	}
	if (i > 255 || i_33_ > 127 || i_33_ < -128)
	    r.putByte(196).b(132, i).putShort(i_33_);
	else
	    r.putByte(132).a(i, i_33_);
    }
    
    public transient void visitTableSwitchInsn(int i, int i_35_, Label label,
					       Label[] labels) {
	Y = r.b;
	int i_36_ = r.b;
	r.putByte(170);
	r.putByteArray(null, 0, (4 - r.b % 4) % 4);
	label.a(this, r, i_36_, true);
	r.putInt(i).putInt(i_35_);
	for (int i_37_ = 0; i_37_ < labels.length; i_37_++)
	    labels[i_37_].a(this, r, i_36_, true);
	a(label, labels);
    }
    
    public void visitLookupSwitchInsn(Label label, int[] is, Label[] labels) {
	Y = r.b;
	int i = r.b;
	r.putByte(171);
	r.putByteArray(null, 0, (4 - r.b % 4) % 4);
	label.a(this, r, i, true);
	r.putInt(labels.length);
	for (int i_38_ = 0; i_38_ < labels.length; i_38_++) {
	    r.putInt(is[i_38_]);
	    labels[i_38_].a(this, r, i, true);
	}
	a(label, labels);
    }
    
    private void a(Label label, Label[] labels) {
	if (P != null) {
	    if (M == 0) {
		P.h.a(171, 0, null, null);
		a(0, label);
		label.a().a |= 0x10;
		for (int i = 0; i < labels.length; i++) {
		    a(0, labels[i]);
		    labels[i].a().a |= 0x10;
		}
	    } else {
		Q--;
		a(Q, label);
		for (int i = 0; i < labels.length; i++)
		    a(Q, labels[i]);
	    }
	    e();
	}
    }
    
    public void visitMultiANewArrayInsn(String string, int i) {
	Y = r.b;
	Item item = b.a(string);
	if (P != null) {
	    if (M == 0)
		P.h.a(197, i, b, item);
	    else
		Q += 1 - i;
	}
	r.b(197, item.a).putByte(i);
    }
    
    public AnnotationVisitor visitInsnAnnotation(int i, TypePath typepath,
						 String string, boolean bool) {
	ByteVector bytevector = new ByteVector();
	i = i & ~0xffff00 | Y << 8;
	AnnotationWriter.a(i, typepath, bytevector);
	bytevector.putShort(b.newUTF8(string)).putShort(0);
	AnnotationWriter annotationwriter
	    = new AnnotationWriter(b, true, bytevector, bytevector,
				   bytevector.b - 2);
	if (bool) {
	    annotationwriter.g = W;
	    W = annotationwriter;
	} else {
	    annotationwriter.g = X;
	    X = annotationwriter;
	}
	return annotationwriter;
    }
    
    public void visitTryCatchBlock(Label label, Label label_39_,
				   Label label_40_, String string) {
	A++;
	Handler handler = new Handler();
	handler.a = label;
	handler.b = label_39_;
	handler.c = label_40_;
	handler.d = string;
	handler.e = string != null ? b.newClass(string) : 0;
	if (C == null)
	    B = handler;
	else
	    C.f = handler;
	C = handler;
    }
    
    public AnnotationVisitor visitTryCatchAnnotation
	(int i, TypePath typepath, String string, boolean bool) {
	ByteVector bytevector = new ByteVector();
	AnnotationWriter.a(i, typepath, bytevector);
	bytevector.putShort(b.newUTF8(string)).putShort(0);
	AnnotationWriter annotationwriter
	    = new AnnotationWriter(b, true, bytevector, bytevector,
				   bytevector.b - 2);
	if (bool) {
	    annotationwriter.g = W;
	    W = annotationwriter;
	} else {
	    annotationwriter.g = X;
	    X = annotationwriter;
	}
	return annotationwriter;
    }
    
    public void visitLocalVariable(String string, String string_41_,
				   String string_42_, Label label,
				   Label label_43_, int i) {
	if (string_42_ != null) {
	    if (G == null)
		G = new ByteVector();
	    F++;
	    G.putShort(label.c).putShort(label_43_.c - label.c).putShort
		(b.newUTF8(string)).putShort
		(b.newUTF8(string_42_)).putShort(i);
	}
	if (E == null)
	    E = new ByteVector();
	D++;
	E.putShort(label.c).putShort(label_43_.c - label.c).putShort
	    (b.newUTF8(string)).putShort
	    (b.newUTF8(string_41_)).putShort(i);
	if (M != 2) {
	    char c = string_41_.charAt(0);
	    int i_44_ = i + (c == 'J' || c == 'D' ? 2 : 1);
	    if (i_44_ > t)
		t = i_44_;
	}
    }
    
    public AnnotationVisitor visitLocalVariableAnnotation
	(int i, TypePath typepath, Label[] labels, Label[] labels_45_,
	 int[] is, String string, boolean bool) {
	ByteVector bytevector = new ByteVector();
	bytevector.putByte(i >>> 24).putShort(labels.length);
	for (int i_46_ = 0; i_46_ < labels.length; i_46_++)
	    bytevector.putShort(labels[i_46_].c).putShort
		(labels_45_[i_46_].c - labels[i_46_].c).putShort(is[i_46_]);
	if (typepath == null)
	    bytevector.putByte(0);
	else {
	    int i_47_ = typepath.a[typepath.b] * 2 + 1;
	    bytevector.putByteArray(typepath.a, typepath.b, i_47_);
	}
	bytevector.putShort(b.newUTF8(string)).putShort(0);
	AnnotationWriter annotationwriter
	    = new AnnotationWriter(b, true, bytevector, bytevector,
				   bytevector.b - 2);
	if (bool) {
	    annotationwriter.g = W;
	    W = annotationwriter;
	} else {
	    annotationwriter.g = X;
	    X = annotationwriter;
	}
	return annotationwriter;
    }
    
    public void visitLineNumber(int i, Label label) {
	if (I == null)
	    I = new ByteVector();
	H++;
	I.putShort(label.c);
	I.putShort(i);
    }
    
    public void visitMaxs(int i, int i_48_) {
	if (K)
	    d();
	if (M == 0) {
	    for (Handler handler = B; handler != null; handler = handler.f) {
		Label label = handler.a.a();
		Label label_49_ = handler.c.a();
		Label label_50_ = handler.b.a();
		String string
		    = handler.d == null ? "java/lang/Throwable" : handler.d;
		int i_51_ = 0x1700000 | b.c(string);
		label_49_.a |= 0x10;
		for (/**/; label != label_50_; label = label.i) {
		    Edge edge = new Edge();
		    edge.a = i_51_;
		    edge.b = label_49_;
		    edge.c = label.j;
		    label.j = edge;
		}
	    }
	    Frame frame = N.h;
	    Type[] types = Type.getArgumentTypes(f);
	    frame.a(b, c, types, t);
	    b(frame);
	    int i_52_ = 0;
	    Label label = N;
	    while (label != null) {
		Label label_53_ = label;
		label = label.k;
		label_53_.k = null;
		frame = label_53_.h;
		if ((label_53_.a & 0x10) != 0)
		    label_53_.a |= 0x20;
		label_53_.a |= 0x40;
		int i_54_ = frame.d.length + label_53_.g;
		if (i_54_ > i_52_)
		    i_52_ = i_54_;
		for (Edge edge = label_53_.j; edge != null; edge = edge.c) {
		    Label label_55_ = edge.b.a();
		    boolean bool = frame.a(b, label_55_.h, edge.a);
		    if (bool && label_55_.k == null) {
			label_55_.k = label;
			label = label_55_;
		    }
		}
	    }
	    for (Label label_56_ = N; label_56_ != null;
		 label_56_ = label_56_.i) {
		frame = label_56_.h;
		if ((label_56_.a & 0x20) != 0)
		    b(frame);
		if ((label_56_.a & 0x40) == 0) {
		    Label label_57_ = label_56_.i;
		    int i_58_ = label_56_.c;
		    int i_59_ = (label_57_ == null ? r.b : label_57_.c) - 1;
		    if (i_59_ >= i_58_) {
			i_52_ = Math.max(i_52_, 1);
			for (int i_60_ = i_58_; i_60_ < i_59_; i_60_++)
			    r.a[i_60_] = (byte) 0;
			r.a[i_59_] = (byte) -65;
			int i_61_ = a(i_58_, 0, 1);
			z[i_61_] = 0x1700000 | b.c("java/lang/Throwable");
			b();
			B = Handler.a(B, label_56_, label_57_);
		    }
		}
	    }
	    Handler handler = B;
	    A = 0;
	    for (/**/; handler != null; handler = handler.f)
		A++;
	    s = i_52_;
	} else if (M == 1) {
	    for (Handler handler = B; handler != null; handler = handler.f) {
		Label label = handler.a;
		Label label_62_ = handler.c;
		for (Label label_63_ = handler.b; label != label_63_;
		     label = label.i) {
		    Edge edge = new Edge();
		    edge.a = 2147483647;
		    edge.b = label_62_;
		    if ((label.a & 0x80) == 0) {
			edge.c = label.j;
			label.j = edge;
		    } else {
			edge.c = label.j.c.c;
			label.j.c.c = edge;
		    }
		}
	    }
	    if (L > 0) {
		int i_64_ = 0;
		N.b(null, 1L, L);
		for (Label label = N; label != null; label = label.i) {
		    if ((label.a & 0x80) != 0) {
			Label label_65_ = label.j.c.b;
			if ((label_65_.a & 0x400) == 0) {
			    i_64_++;
			    label_65_.b(null, ((long) i_64_ / 32L << 32
					       | 1L << i_64_ % 32), L);
			}
		    }
		}
		for (Label label = N; label != null; label = label.i) {
		    if ((label.a & 0x80) != 0) {
			for (Label label_66_ = N; label_66_ != null;
			     label_66_ = label_66_.i)
			    label_66_.a &= ~0x800;
			Label label_67_ = label.j.c.b;
			label_67_.b(label, 0L, L);
		    }
		}
	    }
	    int i_68_ = 0;
	    Label label = N;
	    while (label != null) {
		Label label_69_ = label;
		label = label.k;
		int i_70_ = label_69_.f;
		int i_71_ = i_70_ + label_69_.g;
		if (i_71_ > i_68_)
		    i_68_ = i_71_;
		Edge edge = label_69_.j;
		if ((label_69_.a & 0x80) != 0)
		    edge = edge.c;
		for (/**/; edge != null; edge = edge.c) {
		    label_69_ = edge.b;
		    if ((label_69_.a & 0x8) == 0) {
			label_69_.f
			    = edge.a == 2147483647 ? 1 : i_70_ + edge.a;
			label_69_.a |= 0x8;
			label_69_.k = label;
			label = label_69_;
		    }
		}
	    }
	    s = Math.max(i, i_68_);
	} else {
	    s = i;
	    t = i_48_;
	}
    }
    
    public void visitEnd() {
	/* empty */
    }
    
    private void a(int i, Label label) {
	Edge edge = new Edge();
	edge.a = i;
	edge.b = label;
	edge.c = P.j;
	P.j = edge;
    }
    
    private void e() {
	if (M == 0) {
	    Label label = new Label();
	    label.h = new Frame();
	    label.h.b = label;
	    label.a(this, r.b, r.a);
	    O.i = label;
	    O = label;
	} else
	    P.g = R;
	P = null;
    }
    
    private void b(Frame frame) {
	int i = 0;
	int i_72_ = 0;
	int i_73_ = 0;
	int[] is = frame.c;
	int[] is_74_ = frame.d;
	for (int i_75_ = 0; i_75_ < is.length; i_75_++) {
	    int i_76_ = is[i_75_];
	    if (i_76_ == 16777216)
		i++;
	    else {
		i_72_ += i + 1;
		i = 0;
	    }
	    if (i_76_ == 16777220 || i_76_ == 16777219)
		i_75_++;
	}
	for (int i_77_ = 0; i_77_ < is_74_.length; i_77_++) {
	    int i_78_ = is_74_[i_77_];
	    i_73_++;
	    if (i_78_ == 16777220 || i_78_ == 16777219)
		i_77_++;
	}
	int i_79_ = a(frame.b.c, i_72_, i_73_);
	int i_80_ = 0;
	for (/**/; i_72_ > 0; i_72_--) {
	    int i_81_ = is[i_80_];
	    z[i_79_++] = i_81_;
	    if (i_81_ == 16777220 || i_81_ == 16777219)
		i_80_++;
	    i_80_++;
	}
	for (i_80_ = 0; i_80_ < is_74_.length; i_80_++) {
	    int i_82_ = is_74_[i_80_];
	    z[i_79_++] = i_82_;
	    if (i_82_ == 16777220 || i_82_ == 16777219)
		i_80_++;
	}
	b();
    }
    
    private void f() {
	int i = a(0, f.length() + 1, 0);
	if ((c & 0x8) == 0) {
	    if ((c & 0x80000) == 0)
		z[i++] = 0x1700000 | b.c(b.I);
	    else
		z[i++] = 6;
	}
	int i_83_ = 1;
	for (;;) {
	    int i_84_ = i_83_;
	    switch (f.charAt(i_83_++)) {
	    case 'B':
	    case 'C':
	    case 'I':
	    case 'S':
	    case 'Z':
		z[i++] = 1;
		break;
	    case 'F':
		z[i++] = 2;
		break;
	    case 'J':
		z[i++] = 4;
		break;
	    case 'D':
		z[i++] = 3;
		break;
	    case '[':
		for (/**/; f.charAt(i_83_) == '['; i_83_++) {
		    /* empty */
		}
		if (f.charAt(i_83_) == 'L') {
		    for (i_83_++; f.charAt(i_83_) != ';'; i_83_++) {
			/* empty */
		    }
		}
		z[i++] = 0x1700000 | b.c(f.substring(i_84_, ++i_83_));
		break;
	    case 'L':
		for (/**/; f.charAt(i_83_) != ';'; i_83_++) {
		    /* empty */
		}
		z[i++] = 0x1700000 | b.c(f.substring(i_84_ + 1, i_83_++));
		break;
	    default:
		z[1] = i - 3;
		b();
		return;
	    }
	}
    }
    
    private int a(int i, int i_85_, int i_86_) {
	int i_87_ = 3 + i_85_ + i_86_;
	if (z == null || z.length < i_87_)
	    z = new int[i_87_];
	z[0] = i;
	z[1] = i_85_;
	z[2] = i_86_;
	return 3;
    }
    
    private void b() {
	if (x != null) {
	    if (v == null)
		v = new ByteVector();
	    c();
	    u++;
	}
	x = z;
	z = null;
    }
    
    private void c() {
	int i = z[1];
	int i_88_ = z[2];
	if ((b.b & 0xffff) < 50) {
	    v.putShort(z[0]).putShort(i);
	    a(3, 3 + i);
	    v.putShort(i_88_);
	    a(3 + i, 3 + i + i_88_);
	} else {
	    int i_89_ = x[1];
	    int i_90_ = 255;
	    int i_91_ = 0;
	    int i_92_;
	    if (u == 0)
		i_92_ = z[0];
	    else
		i_92_ = z[0] - x[0] - 1;
	    if (i_88_ == 0) {
		i_91_ = i - i_89_;
		switch (i_91_) {
		case -3:
		case -2:
		case -1:
		    i_90_ = 248;
		    i_89_ = i;
		    break;
		case 0:
		    i_90_ = i_92_ < 64 ? 0 : 251;
		    break;
		case 1:
		case 2:
		case 3:
		    i_90_ = 252;
		    break;
		}
	    } else if (i == i_89_ && i_88_ == 1)
		i_90_ = i_92_ < 63 ? 64 : 247;
	    if (i_90_ != 255) {
		int i_93_ = 3;
		for (int i_94_ = 0; i_94_ < i_89_; i_94_++) {
		    if (z[i_93_] != x[i_93_]) {
			i_90_ = 255;
			break;
		    }
		    i_93_++;
		}
	    }
	    switch (i_90_) {
	    case 0:
		v.putByte(i_92_);
		break;
	    case 64:
		v.putByte(64 + i_92_);
		a(3 + i, 4 + i);
		break;
	    case 247:
		v.putByte(247).putShort(i_92_);
		a(3 + i, 4 + i);
		break;
	    case 251:
		v.putByte(251).putShort(i_92_);
		break;
	    case 248:
		v.putByte(251 + i_91_).putShort(i_92_);
		break;
	    case 252:
		v.putByte(251 + i_91_).putShort(i_92_);
		a(3 + i_89_, 3 + i);
		break;
	    default:
		v.putByte(255).putShort(i_92_).putShort(i);
		a(3, 3 + i);
		v.putShort(i_88_);
		a(3 + i, 3 + i + i_88_);
	    }
	}
    }
    
    private void a(int i, int i_95_) {
	for (int i_96_ = i; i_96_ < i_95_; i_96_++) {
	    int i_97_ = z[i_96_];
	    int i_98_ = i_97_ & ~0xfffffff;
	    if (i_98_ == 0) {
		int i_99_ = i_97_ & 0xfffff;
		switch (i_97_ & 0xff00000) {
		case 24117248:
		    v.putByte(7).putShort(b.newClass(b.H[i_99_].g));
		    break;
		case 25165824:
		    v.putByte(8).putShort(b.H[i_99_].c);
		    break;
		default:
		    v.putByte(i_99_);
		}
	    } else {
		StringBuffer stringbuffer = new StringBuffer();
		i_98_ >>= 28;
		while (i_98_-- > 0)
		    stringbuffer.append('[');
		if ((i_97_ & 0xff00000) == 24117248) {
		    stringbuffer.append('L');
		    stringbuffer.append(b.H[i_97_ & 0xfffff].g);
		    stringbuffer.append(';');
		} else {
		    switch (i_97_ & 0xf) {
		    case 1:
			stringbuffer.append('I');
			break;
		    case 2:
			stringbuffer.append('F');
			break;
		    case 3:
			stringbuffer.append('D');
			break;
		    case 9:
			stringbuffer.append('Z');
			break;
		    case 10:
			stringbuffer.append('B');
			break;
		    case 11:
			stringbuffer.append('C');
			break;
		    case 12:
			stringbuffer.append('S');
			break;
		    default:
			stringbuffer.append('J');
		    }
		}
		v.putByte(7).putShort(b.newClass(stringbuffer.toString()));
	    }
	}
    }
    
    private void a(Object object) {
	if (object instanceof String)
	    v.putByte(7).putShort(b.newClass((String) object));
	else if (object instanceof Integer)
	    v.putByte(((Integer) object).intValue());
	else
	    v.putByte(8).putShort(((Label) object).c);
    }
    
    final int a() {
	if (h != 0)
	    return 6 + this.i;
	int i = 8;
	if (r.b > 0) {
	    if (r.b > 65536)
		throw new RuntimeException("Method code too large!");
	    b.newUTF8("Code");
	    i += 18 + r.b + 8 * A;
	    if (E != null) {
		b.newUTF8("LocalVariableTable");
		i += 8 + E.b;
	    }
	    if (G != null) {
		b.newUTF8("LocalVariableTypeTable");
		i += 8 + G.b;
	    }
	    if (I != null) {
		b.newUTF8("LineNumberTable");
		i += 8 + I.b;
	    }
	    if (v != null) {
		boolean bool = (b.b & 0xffff) >= 50;
		b.newUTF8(bool ? "StackMapTable" : "StackMap");
		i += 8 + v.b;
	    }
	    if (W != null) {
		b.newUTF8("RuntimeVisibleTypeAnnotations");
		i += 8 + W.a();
	    }
	    if (X != null) {
		b.newUTF8("RuntimeInvisibleTypeAnnotations");
		i += 8 + X.a();
	    }
	    if (J != null)
		i += J.a(b, r.a, r.b, s, t);
	}
	if (j > 0) {
	    b.newUTF8("Exceptions");
	    i += 8 + 2 * j;
	}
	if ((c & 0x1000) != 0 && ((b.b & 0xffff) < 49 || (c & 0x40000) != 0)) {
	    b.newUTF8("Synthetic");
	    i += 6;
	}
	if ((c & 0x20000) != 0) {
	    b.newUTF8("Deprecated");
	    i += 6;
	}
	if (g != null) {
	    b.newUTF8("Signature");
	    b.newUTF8(g);
	    i += 8;
	}
	if ($ != null) {
	    b.newUTF8("MethodParameters");
	    i += 7 + $.b;
	}
	if (l != null) {
	    b.newUTF8("AnnotationDefault");
	    i += 6 + l.b;
	}
	if (m != null) {
	    b.newUTF8("RuntimeVisibleAnnotations");
	    i += 8 + m.a();
	}
	if (n != null) {
	    b.newUTF8("RuntimeInvisibleAnnotations");
	    i += 8 + n.a();
	}
	if (U != null) {
	    b.newUTF8("RuntimeVisibleTypeAnnotations");
	    i += 8 + U.a();
	}
	if (V != null) {
	    b.newUTF8("RuntimeInvisibleTypeAnnotations");
	    i += 8 + V.a();
	}
	if (o != null) {
	    b.newUTF8("RuntimeVisibleParameterAnnotations");
	    i += 7 + 2 * (o.length - S);
	    for (int i_100_ = o.length - 1; i_100_ >= S; i_100_--)
		i = i + (o[i_100_] == null ? 0 : o[i_100_].a());
	}
	if (p != null) {
	    b.newUTF8("RuntimeInvisibleParameterAnnotations");
	    i += 7 + 2 * (p.length - S);
	    for (int i_101_ = p.length - 1; i_101_ >= S; i_101_--)
		i = i + (p[i_101_] == null ? 0 : p[i_101_].a());
	}
	if (q != null)
	    i += q.a(b, null, 0, -1, -1);
	return i;
    }
    
    final void a(ByteVector bytevector) {
	int i = 64;
	int i_102_ = 0xe0000 | (c & 0x40000) / 64;
	bytevector.putShort(c & (i_102_ ^ 0xffffffff)).putShort(d).putShort(e);
	if (h != 0)
	    bytevector.putByteArray(b.M.b, h, this.i);
	else {
	    int i_103_ = 0;
	    if (r.b > 0)
		i_103_++;
	    if (j > 0)
		i_103_++;
	    if ((c & 0x1000) != 0
		&& ((b.b & 0xffff) < 49 || (c & 0x40000) != 0))
		i_103_++;
	    if ((c & 0x20000) != 0)
		i_103_++;
	    if (g != null)
		i_103_++;
	    if ($ != null)
		i_103_++;
	    if (l != null)
		i_103_++;
	    if (m != null)
		i_103_++;
	    if (n != null)
		i_103_++;
	    if (U != null)
		i_103_++;
	    if (V != null)
		i_103_++;
	    if (o != null)
		i_103_++;
	    if (p != null)
		i_103_++;
	    if (q != null)
		i_103_ += q.a();
	    bytevector.putShort(i_103_);
	    if (r.b > 0) {
		int i_104_ = 12 + r.b + 8 * A;
		if (E != null)
		    i_104_ += 8 + E.b;
		if (G != null)
		    i_104_ += 8 + G.b;
		if (I != null)
		    i_104_ += 8 + I.b;
		if (v != null)
		    i_104_ += 8 + v.b;
		if (W != null)
		    i_104_ += 8 + W.a();
		if (X != null)
		    i_104_ += 8 + X.a();
		if (J != null)
		    i_104_ += J.a(b, r.a, r.b, s, t);
		bytevector.putShort(b.newUTF8("Code")).putInt(i_104_);
		bytevector.putShort(s).putShort(t);
		bytevector.putInt(r.b).putByteArray(r.a, 0, r.b);
		bytevector.putShort(A);
		if (A > 0) {
		    for (Handler handler = B; handler != null;
			 handler = handler.f)
			bytevector.putShort(handler.a.c).putShort
			    (handler.b.c).putShort
			    (handler.c.c).putShort(handler.e);
		}
		i_103_ = 0;
		if (E != null)
		    i_103_++;
		if (G != null)
		    i_103_++;
		if (I != null)
		    i_103_++;
		if (v != null)
		    i_103_++;
		if (W != null)
		    i_103_++;
		if (X != null)
		    i_103_++;
		if (J != null)
		    i_103_ += J.a();
		bytevector.putShort(i_103_);
		if (E != null) {
		    bytevector.putShort(b.newUTF8("LocalVariableTable"));
		    bytevector.putInt(E.b + 2).putShort(D);
		    bytevector.putByteArray(E.a, 0, E.b);
		}
		if (G != null) {
		    bytevector.putShort(b.newUTF8("LocalVariableTypeTable"));
		    bytevector.putInt(G.b + 2).putShort(F);
		    bytevector.putByteArray(G.a, 0, G.b);
		}
		if (I != null) {
		    bytevector.putShort(b.newUTF8("LineNumberTable"));
		    bytevector.putInt(I.b + 2).putShort(H);
		    bytevector.putByteArray(I.a, 0, I.b);
		}
		if (v != null) {
		    boolean bool = (b.b & 0xffff) >= 50;
		    bytevector.putShort(b.newUTF8(bool ? "StackMapTable"
						  : "StackMap"));
		    bytevector.putInt(v.b + 2).putShort(u);
		    bytevector.putByteArray(v.a, 0, v.b);
		}
		if (W != null) {
		    bytevector
			.putShort(b.newUTF8("RuntimeVisibleTypeAnnotations"));
		    W.a(bytevector);
		}
		if (X != null) {
		    bytevector.putShort
			(b.newUTF8("RuntimeInvisibleTypeAnnotations"));
		    X.a(bytevector);
		}
		if (J != null)
		    J.a(b, r.a, r.b, t, s, bytevector);
	    }
	    if (j > 0) {
		bytevector.putShort(b.newUTF8("Exceptions")).putInt(2 * j + 2);
		bytevector.putShort(j);
		for (int i_105_ = 0; i_105_ < j; i_105_++)
		    bytevector.putShort(k[i_105_]);
	    }
	    if ((c & 0x1000) != 0
		&& ((b.b & 0xffff) < 49 || (c & 0x40000) != 0))
		bytevector.putShort(b.newUTF8("Synthetic")).putInt(0);
	    if ((c & 0x20000) != 0)
		bytevector.putShort(b.newUTF8("Deprecated")).putInt(0);
	    if (g != null)
		bytevector.putShort(b.newUTF8("Signature")).putInt(2)
		    .putShort(b.newUTF8(g));
	    if ($ != null) {
		bytevector.putShort(b.newUTF8("MethodParameters"));
		bytevector.putInt($.b + 1).putByte(Z);
		bytevector.putByteArray($.a, 0, $.b);
	    }
	    if (l != null) {
		bytevector.putShort(b.newUTF8("AnnotationDefault"));
		bytevector.putInt(l.b);
		bytevector.putByteArray(l.a, 0, l.b);
	    }
	    if (m != null) {
		bytevector.putShort(b.newUTF8("RuntimeVisibleAnnotations"));
		m.a(bytevector);
	    }
	    if (n != null) {
		bytevector.putShort(b.newUTF8("RuntimeInvisibleAnnotations"));
		n.a(bytevector);
	    }
	    if (U != null) {
		bytevector
		    .putShort(b.newUTF8("RuntimeVisibleTypeAnnotations"));
		U.a(bytevector);
	    }
	    if (V != null) {
		bytevector
		    .putShort(b.newUTF8("RuntimeInvisibleTypeAnnotations"));
		V.a(bytevector);
	    }
	    if (o != null) {
		bytevector
		    .putShort(b.newUTF8("RuntimeVisibleParameterAnnotations"));
		AnnotationWriter.a(o, S, bytevector);
	    }
	    if (p != null) {
		bytevector.putShort
		    (b.newUTF8("RuntimeInvisibleParameterAnnotations"));
		AnnotationWriter.a(p, S, bytevector);
	    }
	    if (q != null)
		q.a(b, null, 0, -1, -1, bytevector);
	}
    }
    
    private void d() {
	byte[] is = r.a;
	int[] is_106_ = new int[0];
	int[] is_107_ = new int[0];
	boolean[] bools = new boolean[r.b];
	int i = 3;
	do {
	    if (i == 3)
		i = 2;
	    int i_108_ = 0;
	    while (i_108_ < is.length) {
		int i_109_ = is[i_108_] & 0xff;
		int i_110_ = 0;
		switch (ClassWriter.a[i_109_]) {
		case 0:
		case 4:
		    i_108_++;
		    break;
		case 9: {
		    int i_111_;
		    if (i_109_ > 201) {
			i_109_ = i_109_ < 218 ? i_109_ - 49 : i_109_ - 20;
			i_111_ = i_108_ + c(is, i_108_ + 1);
		    } else
			i_111_ = i_108_ + b(is, i_108_ + 1);
		    int i_112_ = a(is_106_, is_107_, i_108_, i_111_);
		    if ((i_112_ < -32768 || i_112_ > 32767)
			&& !bools[i_108_]) {
			if (i_109_ == 167 || i_109_ == 168)
			    i_110_ = 2;
			else
			    i_110_ = 5;
			bools[i_108_] = true;
		    }
		    i_108_ += 3;
		    break;
		}
		case 10:
		    i_108_ += 5;
		    break;
		case 14:
		    if (i == 1) {
			int i_113_ = a(is_106_, is_107_, 0, i_108_);
			i_110_ = -(i_113_ & 0x3);
		    } else if (!bools[i_108_]) {
			i_110_ = i_108_ & 0x3;
			bools[i_108_] = true;
		    }
		    i_108_ = i_108_ + 4 - (i_108_ & 0x3);
		    i_108_ += (4 * (a(is, i_108_ + 8) - a(is, i_108_ + 4) + 1)
			       + 12);
		    break;
		case 15:
		    if (i == 1) {
			int i_114_ = a(is_106_, is_107_, 0, i_108_);
			i_110_ = -(i_114_ & 0x3);
		    } else if (!bools[i_108_]) {
			i_110_ = i_108_ & 0x3;
			bools[i_108_] = true;
		    }
		    i_108_ = i_108_ + 4 - (i_108_ & 0x3);
		    i_108_ += 8 * a(is, i_108_ + 4) + 8;
		    break;
		case 17:
		    i_109_ = is[i_108_ + 1] & 0xff;
		    if (i_109_ == 132)
			i_108_ += 6;
		    else
			i_108_ += 4;
		    break;
		case 1:
		case 3:
		case 11:
		    i_108_ += 2;
		    break;
		case 2:
		case 5:
		case 6:
		case 12:
		case 13:
		    i_108_ += 3;
		    break;
		case 7:
		case 8:
		    i_108_ += 5;
		    break;
		default:
		    i_108_ += 4;
		}
		if (i_110_ != 0) {
		    int[] is_115_ = new int[is_106_.length + 1];
		    int[] is_116_ = new int[is_107_.length + 1];
		    System.arraycopy(is_106_, 0, is_115_, 0, is_106_.length);
		    System.arraycopy(is_107_, 0, is_116_, 0, is_107_.length);
		    is_115_[is_106_.length] = i_108_;
		    is_116_[is_107_.length] = i_110_;
		    is_106_ = is_115_;
		    is_107_ = is_116_;
		    if (i_110_ > 0)
			i = 3;
		}
	    }
	    if (i < 3)
		i--;
	} while (i != 0);
	ByteVector bytevector = new ByteVector(r.b);
	int i_117_ = 0;
	while (i_117_ < r.b) {
	    int i_118_ = is[i_117_] & 0xff;
	    switch (ClassWriter.a[i_118_]) {
	    case 0:
	    case 4:
		bytevector.putByte(i_118_);
		i_117_++;
		break;
	    case 9: {
		int i_119_;
		if (i_118_ > 201) {
		    i_118_ = i_118_ < 218 ? i_118_ - 49 : i_118_ - 20;
		    i_119_ = i_117_ + c(is, i_117_ + 1);
		} else
		    i_119_ = i_117_ + b(is, i_117_ + 1);
		int i_120_ = a(is_106_, is_107_, i_117_, i_119_);
		if (bools[i_117_]) {
		    if (i_118_ == 167)
			bytevector.putByte(200);
		    else if (i_118_ == 168)
			bytevector.putByte(201);
		    else {
			bytevector.putByte(i_118_ <= 166
					   ? (i_118_ + 1 ^ 0x1) - 1
					   : i_118_ ^ 0x1);
			bytevector.putShort(8);
			bytevector.putByte(200);
			i_120_ -= 3;
		    }
		    bytevector.putInt(i_120_);
		} else {
		    bytevector.putByte(i_118_);
		    bytevector.putShort(i_120_);
		}
		i_117_ += 3;
		break;
	    }
	    case 10: {
		int i_121_ = i_117_ + a(is, i_117_ + 1);
		int i_122_ = a(is_106_, is_107_, i_117_, i_121_);
		bytevector.putByte(i_118_);
		bytevector.putInt(i_122_);
		i_117_ += 5;
		break;
	    }
	    case 14: {
		int i_123_ = i_117_;
		i_117_ = i_117_ + 4 - (i_123_ & 0x3);
		bytevector.putByte(170);
		bytevector.putByteArray(null, 0, (4 - bytevector.b % 4) % 4);
		int i_124_ = i_123_ + a(is, i_117_);
		i_117_ += 4;
		int i_125_ = a(is_106_, is_107_, i_123_, i_124_);
		bytevector.putInt(i_125_);
		int i_126_ = a(is, i_117_);
		i_117_ += 4;
		bytevector.putInt(i_126_);
		i_126_ = a(is, i_117_) - i_126_ + 1;
		i_117_ += 4;
		bytevector.putInt(a(is, i_117_ - 4));
		for (/**/; i_126_ > 0; i_126_--) {
		    i_124_ = i_123_ + a(is, i_117_);
		    i_117_ += 4;
		    i_125_ = a(is_106_, is_107_, i_123_, i_124_);
		    bytevector.putInt(i_125_);
		}
		break;
	    }
	    case 15: {
		int i_127_ = i_117_;
		i_117_ = i_117_ + 4 - (i_127_ & 0x3);
		bytevector.putByte(171);
		bytevector.putByteArray(null, 0, (4 - bytevector.b % 4) % 4);
		int i_128_ = i_127_ + a(is, i_117_);
		i_117_ += 4;
		int i_129_ = a(is_106_, is_107_, i_127_, i_128_);
		bytevector.putInt(i_129_);
		int i_130_ = a(is, i_117_);
		i_117_ += 4;
		bytevector.putInt(i_130_);
		for (/**/; i_130_ > 0; i_130_--) {
		    bytevector.putInt(a(is, i_117_));
		    i_117_ += 4;
		    i_128_ = i_127_ + a(is, i_117_);
		    i_117_ += 4;
		    i_129_ = a(is_106_, is_107_, i_127_, i_128_);
		    bytevector.putInt(i_129_);
		}
		break;
	    }
	    case 17:
		i_118_ = is[i_117_ + 1] & 0xff;
		if (i_118_ == 132) {
		    bytevector.putByteArray(is, i_117_, 6);
		    i_117_ += 6;
		} else {
		    bytevector.putByteArray(is, i_117_, 4);
		    i_117_ += 4;
		}
		break;
	    case 1:
	    case 3:
	    case 11:
		bytevector.putByteArray(is, i_117_, 2);
		i_117_ += 2;
		break;
	    case 2:
	    case 5:
	    case 6:
	    case 12:
	    case 13:
		bytevector.putByteArray(is, i_117_, 3);
		i_117_ += 3;
		break;
	    case 7:
	    case 8:
		bytevector.putByteArray(is, i_117_, 5);
		i_117_ += 5;
		break;
	    default:
		bytevector.putByteArray(is, i_117_, 4);
		i_117_ += 4;
	    }
	}
	if (M == 0) {
	    for (Label label = N; label != null; label = label.i) {
		i_117_ = label.c - 3;
		if (i_117_ >= 0 && bools[i_117_])
		    label.a |= 0x10;
		a(is_106_, is_107_, label);
	    }
	    for (int i_131_ = 0; i_131_ < b.H.length; i_131_++) {
		Item item = b.H[i_131_];
		if (item != null && item.b == 31)
		    item.c = a(is_106_, is_107_, 0, item.c);
	    }
	} else if (u > 0)
	    b.L = true;
	for (Handler handler = B; handler != null; handler = handler.f) {
	    a(is_106_, is_107_, handler.a);
	    a(is_106_, is_107_, handler.b);
	    a(is_106_, is_107_, handler.c);
	}
	for (int i_132_ = 0; i_132_ < 2; i_132_++) {
	    ByteVector bytevector_133_ = i_132_ == 0 ? E : G;
	    if (bytevector_133_ != null) {
		is = bytevector_133_.a;
		for (i_117_ = 0; i_117_ < bytevector_133_.b; i_117_ += 10) {
		    int i_134_ = c(is, i_117_);
		    int i_135_ = a(is_106_, is_107_, 0, i_134_);
		    a(is, i_117_, i_135_);
		    i_134_ += c(is, i_117_ + 2);
		    i_135_ = a(is_106_, is_107_, 0, i_134_) - i_135_;
		    a(is, i_117_ + 2, i_135_);
		}
	    }
	}
	if (I != null) {
	    is = I.a;
	    for (i_117_ = 0; i_117_ < I.b; i_117_ += 4)
		a(is, i_117_, a(is_106_, is_107_, 0, c(is, i_117_)));
	}
	for (Attribute attribute = J; attribute != null;
	     attribute = attribute.a) {
	    Label[] labels = attribute.getLabels();
	    if (labels != null) {
		for (int i_136_ = labels.length - 1; i_136_ >= 0; i_136_--)
		    a(is_106_, is_107_, labels[i_136_]);
	    }
	}
	r = bytevector;
    }
    
    static int c(byte[] is, int i) {
	return (is[i] & 0xff) << 8 | is[i + 1] & 0xff;
    }
    
    static short b(byte[] is, int i) {
	return (short) ((is[i] & 0xff) << 8 | is[i + 1] & 0xff);
    }
    
    static int a(byte[] is, int i) {
	return ((is[i] & 0xff) << 24 | (is[i + 1] & 0xff) << 16
		| (is[i + 2] & 0xff) << 8 | is[i + 3] & 0xff);
    }
    
    static void a(byte[] is, int i, int i_137_) {
	is[i] = (byte) (i_137_ >>> 8);
	is[i + 1] = (byte) i_137_;
    }
    
    static int a(int[] is, int[] is_138_, int i, int i_139_) {
	int i_140_ = i_139_ - i;
	for (int i_141_ = 0; i_141_ < is.length; i_141_++) {
	    if (i < is[i_141_] && is[i_141_] <= i_139_)
		i_140_ += is_138_[i_141_];
	    else if (i_139_ < is[i_141_] && is[i_141_] <= i)
		i_140_ -= is_138_[i_141_];
	}
	return i_140_;
    }
    
    static void a(int[] is, int[] is_142_, Label label) {
	if ((label.a & 0x4) == 0) {
	    label.c = a(is, is_142_, 0, label.c);
	    label.a |= 0x4;
	}
    }
}
