/* ClassWriter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm;

public class ClassWriter extends ClassVisitor
{
    public static final int COMPUTE_MAXS = 1;
    public static final int COMPUTE_FRAMES = 2;
    static final byte[] a;
    ClassReader M;
    int b;
    int c = 1;
    final ByteVector d = new ByteVector();
    Item[] e = new Item[256];
    int f = (int) (0.75 * (double) e.length);
    final Item g = new Item();
    final Item h = new Item();
    final Item i = new Item();
    final Item j = new Item();
    Item[] H;
    private short G;
    private int k;
    private int l;
    String I;
    private int m;
    private int n;
    private int o;
    private int[] p;
    private int q;
    private ByteVector r;
    private int s;
    private int t;
    private AnnotationWriter u;
    private AnnotationWriter v;
    private AnnotationWriter N;
    private AnnotationWriter O;
    private Attribute w;
    private int x;
    private ByteVector y;
    int z;
    ByteVector A;
    FieldWriter B;
    FieldWriter C;
    MethodWriter D;
    MethodWriter E;
    private boolean K;
    private boolean J;
    boolean L;
    
    public ClassWriter(int i) {
	super(327680);
	K = (i & 0x1) != 0;
	J = (i & 0x2) != 0;
    }
    
    public ClassWriter(ClassReader classreader, int i) {
	this(i);
	classreader.a(this);
	M = classreader;
    }
    
    public final void visit(int i, int i_0_, String string, String string_1_,
			    String string_2_, String[] strings) {
	b = i;
	k = i_0_;
	l = newClass(string);
	I = string;
	if (string_1_ != null)
	    m = newUTF8(string_1_);
	n = string_2_ == null ? 0 : newClass(string_2_);
	if (strings != null && strings.length > 0) {
	    o = strings.length;
	    p = new int[o];
	    for (int i_3_ = 0; i_3_ < o; i_3_++)
		p[i_3_] = newClass(strings[i_3_]);
	}
    }
    
    public final void visitSource(String string, String string_4_) {
	if (string != null)
	    q = newUTF8(string);
	if (string_4_ != null)
	    r = new ByteVector().encodeUTF8(string_4_, 0, 2147483647);
    }
    
    public final void visitOuterClass(String string, String string_5_,
				      String string_6_) {
	s = newClass(string);
	if (string_5_ != null && string_6_ != null)
	    t = newNameType(string_5_, string_6_);
    }
    
    public final AnnotationVisitor visitAnnotation(String string,
						   boolean bool) {
	ByteVector bytevector = new ByteVector();
	bytevector.putShort(newUTF8(string)).putShort(0);
	AnnotationWriter annotationwriter
	    = new AnnotationWriter(this, true, bytevector, bytevector, 2);
	if (bool) {
	    annotationwriter.g = u;
	    u = annotationwriter;
	} else {
	    annotationwriter.g = v;
	    v = annotationwriter;
	}
	return annotationwriter;
    }
    
    public final AnnotationVisitor visitTypeAnnotation
	(int i, TypePath typepath, String string, boolean bool) {
	ByteVector bytevector = new ByteVector();
	AnnotationWriter.a(i, typepath, bytevector);
	bytevector.putShort(newUTF8(string)).putShort(0);
	AnnotationWriter annotationwriter
	    = new AnnotationWriter(this, true, bytevector, bytevector,
				   bytevector.b - 2);
	if (bool) {
	    annotationwriter.g = N;
	    N = annotationwriter;
	} else {
	    annotationwriter.g = O;
	    O = annotationwriter;
	}
	return annotationwriter;
    }
    
    public final void visitAttribute(Attribute attribute) {
	attribute.a = w;
	w = attribute;
    }
    
    public final void visitInnerClass(String string, String string_7_,
				      String string_8_, int i) {
	if (y == null)
	    y = new ByteVector();
	Item item = a(string);
	if (item.c == 0) {
	    x++;
	    y.putShort(item.a);
	    y.putShort(string_7_ == null ? 0 : newClass(string_7_));
	    y.putShort(string_8_ == null ? 0 : newUTF8(string_8_));
	    y.putShort(i);
	    item.c = x;
	}
    }
    
    public final FieldVisitor visitField(int i, String string,
					 String string_9_, String string_10_,
					 Object object) {
	return new FieldWriter(this, i, string, string_9_, string_10_, object);
    }
    
    public final MethodVisitor visitMethod
	(int i, String string, String string_11_, String string_12_,
	 String[] strings) {
	return new MethodWriter(this, i, string, string_11_, string_12_,
				strings, K, J);
    }
    
    public final void visitEnd() {
	/* empty */
    }
    
    public byte[] toByteArray() {
	if (c > 65535)
	    throw new RuntimeException("Class file too large!");
	int i = 24 + 2 * o;
	int i_13_ = 0;
	for (FieldWriter fieldwriter = B; fieldwriter != null;
	     fieldwriter = (FieldWriter) fieldwriter.fv) {
	    i_13_++;
	    i += fieldwriter.a();
	}
	int i_14_ = 0;
	for (MethodWriter methodwriter = D; methodwriter != null;
	     methodwriter = (MethodWriter) methodwriter.mv) {
	    i_14_++;
	    i += methodwriter.a();
	}
	int i_15_ = 0;
	if (A != null) {
	    i_15_++;
	    i += 8 + A.b;
	    newUTF8("BootstrapMethods");
	}
	if (m != 0) {
	    i_15_++;
	    i += 8;
	    newUTF8("Signature");
	}
	if (q != 0) {
	    i_15_++;
	    i += 8;
	    newUTF8("SourceFile");
	}
	if (r != null) {
	    i_15_++;
	    i += r.b + 6;
	    newUTF8("SourceDebugExtension");
	}
	if (s != 0) {
	    i_15_++;
	    i += 10;
	    newUTF8("EnclosingMethod");
	}
	if ((k & 0x20000) != 0) {
	    i_15_++;
	    i += 6;
	    newUTF8("Deprecated");
	}
	if ((k & 0x1000) != 0 && ((b & 0xffff) < 49 || (k & 0x40000) != 0)) {
	    i_15_++;
	    i += 6;
	    newUTF8("Synthetic");
	}
	if (y != null) {
	    i_15_++;
	    i += 8 + y.b;
	    newUTF8("InnerClasses");
	}
	if (u != null) {
	    i_15_++;
	    i += 8 + u.a();
	    newUTF8("RuntimeVisibleAnnotations");
	}
	if (v != null) {
	    i_15_++;
	    i += 8 + v.a();
	    newUTF8("RuntimeInvisibleAnnotations");
	}
	if (N != null) {
	    i_15_++;
	    i += 8 + N.a();
	    newUTF8("RuntimeVisibleTypeAnnotations");
	}
	if (O != null) {
	    i_15_++;
	    i += 8 + O.a();
	    newUTF8("RuntimeInvisibleTypeAnnotations");
	}
	if (w != null) {
	    i_15_ += w.a();
	    i += w.a(this, null, 0, -1, -1);
	}
	i += d.b;
	ByteVector bytevector = new ByteVector(i);
	bytevector.putInt(-889275714).putInt(b);
	bytevector.putShort(c).putByteArray(d.a, 0, d.b);
	int i_16_ = 0x60000 | (k & 0x40000) / 64;
	bytevector.putShort(k & (i_16_ ^ 0xffffffff)).putShort(l).putShort(n);
	bytevector.putShort(o);
	for (int i_17_ = 0; i_17_ < o; i_17_++)
	    bytevector.putShort(p[i_17_]);
	bytevector.putShort(i_13_);
	for (FieldWriter fieldwriter = B; fieldwriter != null;
	     fieldwriter = (FieldWriter) fieldwriter.fv)
	    fieldwriter.a(bytevector);
	bytevector.putShort(i_14_);
	for (MethodWriter methodwriter = D; methodwriter != null;
	     methodwriter = (MethodWriter) methodwriter.mv)
	    methodwriter.a(bytevector);
	bytevector.putShort(i_15_);
	if (A != null) {
	    bytevector.putShort(newUTF8("BootstrapMethods"));
	    bytevector.putInt(A.b + 2).putShort(z);
	    bytevector.putByteArray(A.a, 0, A.b);
	}
	if (m != 0)
	    bytevector.putShort(newUTF8("Signature")).putInt(2).putShort(m);
	if (q != 0)
	    bytevector.putShort(newUTF8("SourceFile")).putInt(2).putShort(q);
	if (r != null) {
	    int i_18_ = r.b;
	    bytevector.putShort(newUTF8("SourceDebugExtension")).putInt(i_18_);
	    bytevector.putByteArray(r.a, 0, i_18_);
	}
	if (s != 0) {
	    bytevector.putShort(newUTF8("EnclosingMethod")).putInt(4);
	    bytevector.putShort(s).putShort(t);
	}
	if ((k & 0x20000) != 0)
	    bytevector.putShort(newUTF8("Deprecated")).putInt(0);
	if ((k & 0x1000) != 0 && ((b & 0xffff) < 49 || (k & 0x40000) != 0))
	    bytevector.putShort(newUTF8("Synthetic")).putInt(0);
	if (y != null) {
	    bytevector.putShort(newUTF8("InnerClasses"));
	    bytevector.putInt(y.b + 2).putShort(x);
	    bytevector.putByteArray(y.a, 0, y.b);
	}
	if (u != null) {
	    bytevector.putShort(newUTF8("RuntimeVisibleAnnotations"));
	    u.a(bytevector);
	}
	if (v != null) {
	    bytevector.putShort(newUTF8("RuntimeInvisibleAnnotations"));
	    v.a(bytevector);
	}
	if (N != null) {
	    bytevector.putShort(newUTF8("RuntimeVisibleTypeAnnotations"));
	    N.a(bytevector);
	}
	if (O != null) {
	    bytevector.putShort(newUTF8("RuntimeInvisibleTypeAnnotations"));
	    O.a(bytevector);
	}
	if (w != null)
	    w.a(this, null, 0, -1, -1, bytevector);
	if (L) {
	    u = null;
	    v = null;
	    w = null;
	    x = 0;
	    y = null;
	    z = 0;
	    A = null;
	    B = null;
	    C = null;
	    D = null;
	    E = null;
	    K = false;
	    J = true;
	    L = false;
	    new ClassReader(bytevector.a).accept(this, 4);
	    return toByteArray();
	}
	return bytevector.a;
    }
    
    Item a(Object object) {
	if (object instanceof Integer) {
	    int i = ((Integer) object).intValue();
	    return a(i);
	}
	if (object instanceof Byte) {
	    int i = ((Byte) object).intValue();
	    return a(i);
	}
	if (object instanceof Character) {
	    char c = ((Character) object).charValue();
	    return a(c);
	}
	if (object instanceof Short) {
	    int i = ((Short) object).intValue();
	    return a(i);
	}
	if (object instanceof Boolean) {
	    int i = ((Boolean) object).booleanValue() ? 1 : 0;
	    return a(i);
	}
	if (object instanceof Float) {
	    float f = ((Float) object).floatValue();
	    return a(f);
	}
	if (object instanceof Long) {
	    long l = ((Long) object).longValue();
	    return a(l);
	}
	if (object instanceof Double) {
	    double d = ((Double) object).doubleValue();
	    return a(d);
	}
	if (object instanceof String)
	    return b((String) object);
	if (object instanceof Type) {
	    Type type = (Type) object;
	    int i = type.getSort();
	    if (i == 10)
		return a(type.getInternalName());
	    if (i == 11)
		return c(type.getDescriptor());
	    return a(type.getDescriptor());
	}
	if (object instanceof Handle) {
	    Handle handle = (Handle) object;
	    return a(handle.a, handle.b, handle.c, handle.d);
	}
	throw new IllegalArgumentException("value " + object);
    }
    
    public int newConst(Object object) {
	return a(object).a;
    }
    
    public int newUTF8(String string) {
	g.a(1, string, null, null);
	Item item = a(g);
	if (item == null) {
	    d.putByte(1).putUTF8(string);
	    item = new Item(c++, g);
	    b(item);
	}
	return item.a;
    }
    
    Item a(String string) {
	h.a(7, string, null, null);
	Item item = a(h);
	if (item == null) {
	    d.b(7, newUTF8(string));
	    item = new Item(c++, h);
	    b(item);
	}
	return item;
    }
    
    public int newClass(String string) {
	return a(string).a;
    }
    
    Item c(String string) {
	h.a(16, string, null, null);
	Item item = a(h);
	if (item == null) {
	    d.b(16, newUTF8(string));
	    item = new Item(c++, h);
	    b(item);
	}
	return item;
    }
    
    public int newMethodType(String string) {
	return c(string).a;
    }
    
    Item a(int i, String string, String string_19_, String string_20_) {
	j.a(20 + i, string, string_19_, string_20_);
	Item item = a(j);
	if (item == null) {
	    if (i <= 4)
		b(15, i, newField(string, string_19_, string_20_));
	    else
		b(15, i, newMethod(string, string_19_, string_20_, i == 9));
	    item = new Item(c++, j);
	    b(item);
	}
	return item;
    }
    
    public int newHandle(int i, String string, String string_21_,
			 String string_22_) {
	return a(i, string, string_21_, string_22_).a;
    }
    
    transient Item a(String string, String string_23_, Handle handle,
		     Object[] objects) {
	ByteVector bytevector = A;
	if (bytevector == null)
	    bytevector = A = new ByteVector();
	int i = bytevector.b;
	int i_24_ = handle.hashCode();
	bytevector.putShort(newHandle(handle.a, handle.b, handle.c, handle.d));
	int i_25_ = objects.length;
	bytevector.putShort(i_25_);
	for (int i_26_ = 0; i_26_ < i_25_; i_26_++) {
	    Object object = objects[i_26_];
	    i_24_ ^= object.hashCode();
	    bytevector.putShort(newConst(object));
	}
	byte[] is = bytevector.a;
	int i_27_ = 2 + i_25_ << 1;
	i_24_ &= 0x7fffffff;
	Item item = e[i_24_ % e.length];
    while_23_:
	while (item != null) {
	    if (item.b != 33 || item.j != i_24_)
		item = item.k;
	    else {
		int i_28_ = item.c;
		int i_29_ = 0;
		for (;;) {
		    if (i_29_ >= i_27_)
			break while_23_;
		    if (is[i + i_29_] != is[i_28_ + i_29_]) {
			item = item.k;
			break;
		    }
		    i_29_++;
		}
	    }
	}
	int i_30_;
	if (item != null) {
	    i_30_ = item.a;
	    bytevector.b = i;
	} else {
	    i_30_ = z++;
	    item = new Item(i_30_);
	    item.a(i, i_24_);
	    b(item);
	}
	this.i.a(string, string_23_, i_30_);
	item = a(this.i);
	if (item == null) {
	    a(18, i_30_, newNameType(string, string_23_));
	    item = new Item(c++, this.i);
	    b(item);
	}
	return item;
    }
    
    public transient int newInvokeDynamic(String string, String string_31_,
					  Handle handle, Object[] objects) {
	return a(string, string_31_, handle, objects).a;
    }
    
    Item a(String string, String string_32_, String string_33_) {
	i.a(9, string, string_32_, string_33_);
	Item item = a(i);
	if (item == null) {
	    a(9, newClass(string), newNameType(string_32_, string_33_));
	    item = new Item(c++, i);
	    b(item);
	}
	return item;
    }
    
    public int newField(String string, String string_34_, String string_35_) {
	return a(string, string_34_, string_35_).a;
    }
    
    Item a(String string, String string_36_, String string_37_, boolean bool) {
	int i = bool ? 11 : 10;
	this.i.a(i, string, string_36_, string_37_);
	Item item = a(this.i);
	if (item == null) {
	    a(i, newClass(string), newNameType(string_36_, string_37_));
	    item = new Item(c++, this.i);
	    b(item);
	}
	return item;
    }
    
    public int newMethod(String string, String string_38_, String string_39_,
			 boolean bool) {
	return a(string, string_38_, string_39_, bool).a;
    }
    
    Item a(int i) {
	g.a(i);
	Item item = a(g);
	if (item == null) {
	    d.putByte(3).putInt(i);
	    item = new Item(c++, g);
	    b(item);
	}
	return item;
    }
    
    Item a(float f) {
	g.a(f);
	Item item = a(g);
	if (item == null) {
	    d.putByte(4).putInt(g.c);
	    item = new Item(c++, g);
	    b(item);
	}
	return item;
    }
    
    Item a(long l) {
	g.a(l);
	Item item = a(g);
	if (item == null) {
	    d.putByte(5).putLong(l);
	    item = new Item(c, g);
	    c += 2;
	    b(item);
	}
	return item;
    }
    
    Item a(double d) {
	g.a(d);
	Item item = a(g);
	if (item == null) {
	    this.d.putByte(6).putLong(g.d);
	    item = new Item(c, g);
	    c += 2;
	    b(item);
	}
	return item;
    }
    
    private Item b(String string) {
	h.a(8, string, null, null);
	Item item = a(h);
	if (item == null) {
	    d.b(8, newUTF8(string));
	    item = new Item(c++, h);
	    b(item);
	}
	return item;
    }
    
    public int newNameType(String string, String string_40_) {
	return a(string, string_40_).a;
    }
    
    Item a(String string, String string_41_) {
	h.a(12, string, string_41_, null);
	Item item = a(h);
	if (item == null) {
	    a(12, newUTF8(string), newUTF8(string_41_));
	    item = new Item(c++, h);
	    b(item);
	}
	return item;
    }
    
    int c(String string) {
	g.a(30, string, null, null);
	Item item = a(g);
	if (item == null)
	    item = c(g);
	return item.a;
    }
    
    int a(String string, int i) {
	g.b = 31;
	g.c = i;
	g.g = string;
	g.j = 0x7fffffff & 31 + string.hashCode() + i;
	Item item = a(g);
	if (item == null)
	    item = c(g);
	return item.a;
    }
    
    private Item c(Item item) {
	G++;
	Item item_42_ = new Item(G, g);
	b(item_42_);
	if (H == null)
	    H = new Item[16];
	if (G == H.length) {
	    Item[] items = new Item[2 * H.length];
	    System.arraycopy(H, 0, items, 0, H.length);
	    H = items;
	}
	H[G] = item_42_;
	return item_42_;
    }
    
    int a(int i, int i_43_) {
	h.b = 32;
	h.d = (long) i | (long) i_43_ << 32;
	h.j = 0x7fffffff & 32 + i + i_43_;
	Item item = a(h);
	if (item == null) {
	    String string = H[i].g;
	    String string_44_ = H[i_43_].g;
	    h.c = c(getCommonSuperClass(string, string_44_));
	    item = new Item(0, h);
	    b(item);
	}
	return item.c;
    }
    
    protected String getCommonSuperClass(String string, String string_45_) {
	ClassLoader classloader = this.getClass().getClassLoader();
	Class var_class;
	Class var_class_46_;
	try {
	    var_class
		= Class.forName(string.replace('/', '.'), false, classloader);
	    var_class_46_ = Class.forName(string_45_.replace('/', '.'), false,
					  classloader);
	} catch (Exception exception) {
	    throw new RuntimeException(exception.toString());
	}
	if (var_class.isAssignableFrom(var_class_46_))
	    return string;
	if (var_class_46_.isAssignableFrom(var_class))
	    return string_45_;
	if (var_class.isInterface() || var_class_46_.isInterface())
	    return "java/lang/Object";
	do
	    var_class = var_class.getSuperclass();
	while (!var_class.isAssignableFrom(var_class_46_));
	return var_class.getName().replace('.', '/');
    }
    
    private Item a(Item item) {
	Item item_47_;
	for (item_47_ = e[item.j % e.length];
	     item_47_ != null && (item_47_.b != item.b || !item.a(item_47_));
	     item_47_ = item_47_.k) {
	    /* empty */
	}
	return item_47_;
    }
    
    private void b(Item item) {
	if (c + G > f) {
	    int i = e.length;
	    int i_48_ = i * 2 + 1;
	    Item[] items = new Item[i_48_];
	    for (int i_49_ = i - 1; i_49_ >= 0; i_49_--) {
		Item item_51_;
		for (Item item_50_ = e[i_49_]; item_50_ != null;
		     item_50_ = item_51_) {
		    int i_52_ = item_50_.j % items.length;
		    item_51_ = item_50_.k;
		    item_50_.k = items[i_52_];
		    items[i_52_] = item_50_;
		}
	    }
	    e = items;
	    f = (int) ((double) i_48_ * 0.75);
	}
	int i = item.j % e.length;
	item.k = e[i];
	e[i] = item;
    }
    
    private void a(int i, int i_53_, int i_54_) {
	d.b(i, i_53_).putShort(i_54_);
    }
    
    private void b(int i, int i_55_, int i_56_) {
	d.a(i, i_55_).putShort(i_56_);
    }
    
    static {
	_clinit_();
	byte[] is = new byte[220];
	String string
	    = "AAAAAAAAAAAAAAAABCLMMDDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAADDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAANAAAAAAAAAAAAAAAAAAAAJJJJJJJJJJJJJJJJDOPAAAAAAGGGGGGGHIFBFAAFFAARQJJKKJJJJJJJJJJJJJJJJJJ";
	for (int i = 0; i < is.length; i++)
	    is[i] = (byte) (string.charAt(i) - 'A');
	a = is;
    }
    
    /*synthetic*/ static void _clinit_() {
	/* empty */
    }
}
