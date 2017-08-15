/* ClassReader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm;
import java.io.IOException;
import java.io.InputStream;

public class ClassReader
{
    public static final int SKIP_CODE = 1;
    public static final int SKIP_DEBUG = 2;
    public static final int SKIP_FRAMES = 4;
    public static final int EXPAND_FRAMES = 8;
    public final byte[] b;
    private final int[] a;
    private final String[] c;
    private final int d;
    public final int header;
    
    public ClassReader(byte[] is) {
	this(is, 0, is.length);
    }
    
    public ClassReader(byte[] is, int i, int i_0_) {
	b = is;
	if (readShort(i + 6) > 52)
	    throw new IllegalArgumentException();
	a = new int[readUnsignedShort(i + 8)];
	int i_1_ = a.length;
	c = new String[i_1_];
	int i_2_ = 0;
	int i_3_ = i + 10;
	for (int i_4_ = 1; i_4_ < i_1_; i_4_++) {
	    a[i_4_] = i_3_ + 1;
	    int i_5_;
	    switch (is[i_3_]) {
	    case 3:
	    case 4:
	    case 9:
	    case 10:
	    case 11:
	    case 12:
	    case 18:
		i_5_ = 5;
		break;
	    case 5:
	    case 6:
		i_5_ = 9;
		i_4_++;
		break;
	    case 1:
		i_5_ = 3 + readUnsignedShort(i_3_ + 1);
		if (i_5_ > i_2_)
		    i_2_ = i_5_;
		break;
	    case 15:
		i_5_ = 4;
		break;
	    default:
		i_5_ = 3;
	    }
	    i_3_ += i_5_;
	}
	d = i_2_;
	header = i_3_;
    }
    
    public int getAccess() {
	return readUnsignedShort(header);
    }
    
    public String getClassName() {
	return readClass(header + 2, new char[d]);
    }
    
    public String getSuperName() {
	return readClass(header + 4, new char[d]);
    }
    
    public String[] getInterfaces() {
	int i = header + 6;
	int i_6_ = readUnsignedShort(i);
	String[] strings = new String[i_6_];
	if (i_6_ > 0) {
	    char[] cs = new char[d];
	    for (int i_7_ = 0; i_7_ < i_6_; i_7_++) {
		i += 2;
		strings[i_7_] = readClass(i, cs);
	    }
	}
	return strings;
    }
    
    void a(ClassWriter classwriter) {
	char[] cs = new char[d];
	int i = a.length;
	Item[] items = new Item[i];
	for (int i_8_ = 1; i_8_ < i; i_8_++) {
	    int i_9_ = a[i_8_];
	    byte i_10_ = b[i_9_ - 1];
	    Item item = new Item(i_8_);
	    switch (i_10_) {
	    case 9:
	    case 10:
	    case 11: {
		int i_11_ = a[readUnsignedShort(i_9_ + 2)];
		item.a(i_10_, readClass(i_9_, cs), readUTF8(i_11_, cs),
		       readUTF8(i_11_ + 2, cs));
		break;
	    }
	    case 3:
		item.a(readInt(i_9_));
		break;
	    case 4:
		item.a(Float.intBitsToFloat(readInt(i_9_)));
		break;
	    case 12:
		item.a(i_10_, readUTF8(i_9_, cs), readUTF8(i_9_ + 2, cs),
		       null);
		break;
	    case 5:
		item.a(readLong(i_9_));
		i_8_++;
		break;
	    case 6:
		item.a(Double.longBitsToDouble(readLong(i_9_)));
		i_8_++;
		break;
	    case 1: {
		String string = c[i_8_];
		if (string == null) {
		    i_9_ = a[i_8_];
		    string = c[i_8_]
			= a(i_9_ + 2, readUnsignedShort(i_9_), cs);
		}
		item.a(i_10_, string, null, null);
		break;
	    }
	    case 15: {
		int i_12_ = a[readUnsignedShort(i_9_ + 1)];
		int i_13_ = a[readUnsignedShort(i_12_ + 2)];
		item.a(20 + readByte(i_9_), readClass(i_12_, cs),
		       readUTF8(i_13_, cs), readUTF8(i_13_ + 2, cs));
		break;
	    }
	    case 18: {
		if (classwriter.A == null)
		    a(classwriter, items, cs);
		int i_14_ = a[readUnsignedShort(i_9_ + 2)];
		item.a(readUTF8(i_14_, cs), readUTF8(i_14_ + 2, cs),
		       readUnsignedShort(i_9_));
		break;
	    }
	    default:
		item.a(i_10_, readUTF8(i_9_, cs), null, null);
	    }
	    int i_15_ = item.j % items.length;
	    item.k = items[i_15_];
	    items[i_15_] = item;
	}
	int i_16_ = a[1] - 1;
	classwriter.d.putByteArray(b, i_16_, header - i_16_);
	classwriter.e = items;
	classwriter.f = (int) (0.75 * (double) i);
	classwriter.c = i;
    }
    
    private void a(ClassWriter classwriter, Item[] items, char[] cs) {
	int i = a();
	boolean bool = false;
	for (int i_17_ = readUnsignedShort(i); i_17_ > 0; i_17_--) {
	    String string = readUTF8(i + 2, cs);
	    if ("BootstrapMethods".equals(string)) {
		bool = true;
		break;
	    }
	    i += 6 + readInt(i + 4);
	}
	if (bool) {
	    int i_18_ = readUnsignedShort(i + 8);
	    int i_19_ = 0;
	    int i_20_ = i + 10;
	    for (/**/; i_19_ < i_18_; i_19_++) {
		int i_21_ = i_20_ - i - 10;
		int i_22_ = readConst(readUnsignedShort(i_20_), cs).hashCode();
		for (int i_23_ = readUnsignedShort(i_20_ + 2); i_23_ > 0;
		     i_23_--) {
		    i_22_ ^= readConst(readUnsignedShort(i_20_ + 4), cs)
				 .hashCode();
		    i_20_ += 2;
		}
		i_20_ += 4;
		Item item = new Item(i_19_);
		item.a(i_21_, i_22_ & 0x7fffffff);
		int i_24_ = item.j % items.length;
		item.k = items[i_24_];
		items[i_24_] = item;
	    }
	    i_19_ = readInt(i + 4);
	    ByteVector bytevector = new ByteVector(i_19_ + 62);
	    bytevector.putByteArray(b, i + 10, i_19_ - 2);
	    classwriter.z = i_18_;
	    classwriter.A = bytevector;
	}
    }
    
    public ClassReader(InputStream inputstream) throws IOException {
	this(a(inputstream, false));
    }
    
    public ClassReader(String string) throws IOException {
	this(a(ClassLoader.getSystemResourceAsStream(string.replace('.', '/')
						     + ".class"),
	       true));
    }
    
    private static byte[] a(InputStream inputstream, boolean bool)
	throws IOException {
	object = object_33_;
	break while_22_;
    }
    
    public void accept(ClassVisitor classvisitor, int i) {
	accept(classvisitor, new Attribute[0], i);
    }
    
    public void accept(ClassVisitor classvisitor, Attribute[] attributes,
		       int i) {
	int i_35_ = header;
	char[] cs = new char[d];
	Context context = new Context();
	context.a = attributes;
	context.b = i;
	context.c = cs;
	int i_36_ = readUnsignedShort(i_35_);
	String string = readClass(i_35_ + 2, cs);
	String string_37_ = readClass(i_35_ + 4, cs);
	String[] strings = new String[readUnsignedShort(i_35_ + 6)];
	i_35_ += 8;
	for (int i_38_ = 0; i_38_ < strings.length; i_38_++) {
	    strings[i_38_] = readClass(i_35_, cs);
	    i_35_ += 2;
	}
	String string_39_ = null;
	String string_40_ = null;
	String string_41_ = null;
	String string_42_ = null;
	String string_43_ = null;
	String string_44_ = null;
	int i_45_ = 0;
	int i_46_ = 0;
	int i_47_ = 0;
	int i_48_ = 0;
	int i_49_ = 0;
	Attribute attribute = null;
	i_35_ = a();
	for (int i_50_ = readUnsignedShort(i_35_); i_50_ > 0; i_50_--) {
	    String string_51_ = readUTF8(i_35_ + 2, cs);
	    if ("SourceFile".equals(string_51_))
		string_40_ = readUTF8(i_35_ + 8, cs);
	    else if ("InnerClasses".equals(string_51_))
		i_49_ = i_35_ + 8;
	    else if ("EnclosingMethod".equals(string_51_)) {
		string_42_ = readClass(i_35_ + 8, cs);
		int i_52_ = readUnsignedShort(i_35_ + 10);
		if (i_52_ != 0) {
		    string_43_ = readUTF8(a[i_52_], cs);
		    string_44_ = readUTF8(a[i_52_] + 2, cs);
		}
	    } else if ("Signature".equals(string_51_))
		string_39_ = readUTF8(i_35_ + 8, cs);
	    else if ("RuntimeVisibleAnnotations".equals(string_51_))
		i_45_ = i_35_ + 8;
	    else if ("RuntimeVisibleTypeAnnotations".equals(string_51_))
		i_47_ = i_35_ + 8;
	    else if ("Deprecated".equals(string_51_))
		i_36_ |= 0x20000;
	    else if ("Synthetic".equals(string_51_))
		i_36_ |= 0x41000;
	    else if ("SourceDebugExtension".equals(string_51_)) {
		int i_53_ = readInt(i_35_ + 4);
		string_41_ = a(i_35_ + 8, i_53_, new char[i_53_]);
	    } else if ("RuntimeInvisibleAnnotations".equals(string_51_))
		i_46_ = i_35_ + 8;
	    else if ("RuntimeInvisibleTypeAnnotations".equals(string_51_))
		i_48_ = i_35_ + 8;
	    else if ("BootstrapMethods".equals(string_51_)) {
		int[] is = new int[readUnsignedShort(i_35_ + 8)];
		int i_54_ = 0;
		int i_55_ = i_35_ + 10;
		for (/**/; i_54_ < is.length; i_54_++) {
		    is[i_54_] = i_55_;
		    i_55_ += 2 + readUnsignedShort(i_55_ + 2) << 1;
		}
		context.d = is;
	    } else {
		Attribute attribute_56_ = a(attributes, string_51_, i_35_ + 8,
					    readInt(i_35_ + 4), cs, -1, null);
		if (attribute_56_ != null) {
		    attribute_56_.a = attribute;
		    attribute = attribute_56_;
		}
	    }
	    i_35_ += 6 + readInt(i_35_ + 4);
	}
	classvisitor.visit(readInt(a[1] - 7), i_36_, string, string_39_,
			   string_37_, strings);
	if ((i & 0x2) == 0 && (string_40_ != null || string_41_ != null))
	    classvisitor.visitSource(string_40_, string_41_);
	if (string_42_ != null)
	    classvisitor.visitOuterClass(string_42_, string_43_, string_44_);
	if (i_45_ != 0) {
	    int i_57_ = readUnsignedShort(i_45_);
	    int i_58_ = i_45_ + 2;
	    for (/**/; i_57_ > 0; i_57_--)
		i_58_ = a(i_58_ + 2, cs, true,
			  classvisitor.visitAnnotation(readUTF8(i_58_, cs),
						       true));
	}
	if (i_46_ != 0) {
	    int i_59_ = readUnsignedShort(i_46_);
	    int i_60_ = i_46_ + 2;
	    for (/**/; i_59_ > 0; i_59_--)
		i_60_ = a(i_60_ + 2, cs, true,
			  classvisitor.visitAnnotation(readUTF8(i_60_, cs),
						       false));
	}
	if (i_47_ != 0) {
	    int i_61_ = readUnsignedShort(i_47_);
	    int i_62_ = i_47_ + 2;
	    for (/**/; i_61_ > 0; i_61_--) {
		i_62_ = a(context, i_62_);
		i_62_
		    = a(i_62_ + 2, cs, true,
			classvisitor.visitTypeAnnotation(context.i, context.j,
							 readUTF8(i_62_, cs),
							 true));
	    }
	}
	if (i_48_ != 0) {
	    int i_63_ = readUnsignedShort(i_48_);
	    int i_64_ = i_48_ + 2;
	    for (/**/; i_63_ > 0; i_63_--) {
		i_64_ = a(context, i_64_);
		i_64_
		    = a(i_64_ + 2, cs, true,
			classvisitor.visitTypeAnnotation(context.i, context.j,
							 readUTF8(i_64_, cs),
							 false));
	    }
	}
	Attribute attribute_65_;
	for (/**/; attribute != null; attribute = attribute_65_) {
	    attribute_65_ = attribute.a;
	    attribute.a = null;
	    classvisitor.visitAttribute(attribute);
	}
	if (i_49_ != 0) {
	    int i_66_ = i_49_ + 2;
	    for (int i_67_ = readUnsignedShort(i_49_); i_67_ > 0; i_67_--) {
		classvisitor.visitInnerClass(readClass(i_66_, cs),
					     readClass(i_66_ + 2, cs),
					     readUTF8(i_66_ + 4, cs),
					     readUnsignedShort(i_66_ + 6));
		i_66_ += 8;
	    }
	}
	i_35_ = header + 10 + 2 * strings.length;
	for (int i_68_ = readUnsignedShort(i_35_ - 2); i_68_ > 0; i_68_--)
	    i_35_ = a(classvisitor, context, i_35_);
	i_35_ += 2;
	for (int i_69_ = readUnsignedShort(i_35_ - 2); i_69_ > 0; i_69_--)
	    i_35_ = b(classvisitor, context, i_35_);
	classvisitor.visitEnd();
    }
    
    private int a(ClassVisitor classvisitor, Context context, int i) {
	char[] cs = context.c;
	int i_70_ = readUnsignedShort(i);
	String string = readUTF8(i + 2, cs);
	String string_71_ = readUTF8(i + 4, cs);
	i += 6;
	String string_72_ = null;
	int i_73_ = 0;
	int i_74_ = 0;
	int i_75_ = 0;
	int i_76_ = 0;
	Object object = null;
	Attribute attribute = null;
	for (int i_77_ = readUnsignedShort(i); i_77_ > 0; i_77_--) {
	    String string_78_ = readUTF8(i + 2, cs);
	    if ("ConstantValue".equals(string_78_)) {
		int i_79_ = readUnsignedShort(i + 8);
		object = i_79_ == 0 ? null : readConst(i_79_, cs);
	    } else if ("Signature".equals(string_78_))
		string_72_ = readUTF8(i + 8, cs);
	    else if ("Deprecated".equals(string_78_))
		i_70_ |= 0x20000;
	    else if ("Synthetic".equals(string_78_))
		i_70_ |= 0x41000;
	    else if ("RuntimeVisibleAnnotations".equals(string_78_))
		i_73_ = i + 8;
	    else if ("RuntimeVisibleTypeAnnotations".equals(string_78_))
		i_75_ = i + 8;
	    else if ("RuntimeInvisibleAnnotations".equals(string_78_))
		i_74_ = i + 8;
	    else if ("RuntimeInvisibleTypeAnnotations".equals(string_78_))
		i_76_ = i + 8;
	    else {
		Attribute attribute_80_ = a(context.a, string_78_, i + 8,
					    readInt(i + 4), cs, -1, null);
		if (attribute_80_ != null) {
		    attribute_80_.a = attribute;
		    attribute = attribute_80_;
		}
	    }
	    i += 6 + readInt(i + 4);
	}
	i += 2;
	FieldVisitor fieldvisitor
	    = classvisitor.visitField(i_70_, string, string_71_, string_72_,
				      object);
	if (fieldvisitor == null)
	    return i;
	if (i_73_ != 0) {
	    int i_81_ = readUnsignedShort(i_73_);
	    int i_82_ = i_73_ + 2;
	    for (/**/; i_81_ > 0; i_81_--)
		i_82_ = a(i_82_ + 2, cs, true,
			  fieldvisitor.visitAnnotation(readUTF8(i_82_, cs),
						       true));
	}
	if (i_74_ != 0) {
	    int i_83_ = readUnsignedShort(i_74_);
	    int i_84_ = i_74_ + 2;
	    for (/**/; i_83_ > 0; i_83_--)
		i_84_ = a(i_84_ + 2, cs, true,
			  fieldvisitor.visitAnnotation(readUTF8(i_84_, cs),
						       false));
	}
	if (i_75_ != 0) {
	    int i_85_ = readUnsignedShort(i_75_);
	    int i_86_ = i_75_ + 2;
	    for (/**/; i_85_ > 0; i_85_--) {
		i_86_ = a(context, i_86_);
		i_86_
		    = a(i_86_ + 2, cs, true,
			fieldvisitor.visitTypeAnnotation(context.i, context.j,
							 readUTF8(i_86_, cs),
							 true));
	    }
	}
	if (i_76_ != 0) {
	    int i_87_ = readUnsignedShort(i_76_);
	    int i_88_ = i_76_ + 2;
	    for (/**/; i_87_ > 0; i_87_--) {
		i_88_ = a(context, i_88_);
		i_88_
		    = a(i_88_ + 2, cs, true,
			fieldvisitor.visitTypeAnnotation(context.i, context.j,
							 readUTF8(i_88_, cs),
							 false));
	    }
	}
	Attribute attribute_89_;
	for (/**/; attribute != null; attribute = attribute_89_) {
	    attribute_89_ = attribute.a;
	    attribute.a = null;
	    fieldvisitor.visitAttribute(attribute);
	}
	fieldvisitor.visitEnd();
	return i;
    }
    
    private int b(ClassVisitor classvisitor, Context context, int i) {
	char[] cs = context.c;
	context.e = readUnsignedShort(i);
	context.f = readUTF8(i + 2, cs);
	context.g = readUTF8(i + 4, cs);
	i += 6;
	int i_90_ = 0;
	int i_91_ = 0;
	String[] strings = null;
	String string = null;
	int i_92_ = 0;
	int i_93_ = 0;
	int i_94_ = 0;
	int i_95_ = 0;
	int i_96_ = 0;
	int i_97_ = 0;
	int i_98_ = 0;
	int i_99_ = 0;
	int i_100_ = i;
	Attribute attribute = null;
	for (int i_101_ = readUnsignedShort(i); i_101_ > 0; i_101_--) {
	    String string_102_ = readUTF8(i + 2, cs);
	    if ("Code".equals(string_102_)) {
		if ((context.b & 0x1) == 0)
		    i_90_ = i + 8;
	    } else if ("Exceptions".equals(string_102_)) {
		strings = new String[readUnsignedShort(i + 8)];
		i_91_ = i + 10;
		for (int i_103_ = 0; i_103_ < strings.length; i_103_++) {
		    strings[i_103_] = readClass(i_91_, cs);
		    i_91_ += 2;
		}
	    } else if ("Signature".equals(string_102_))
		string = readUTF8(i + 8, cs);
	    else if ("Deprecated".equals(string_102_))
		context.e |= 0x20000;
	    else if ("RuntimeVisibleAnnotations".equals(string_102_))
		i_93_ = i + 8;
	    else if ("RuntimeVisibleTypeAnnotations".equals(string_102_))
		i_95_ = i + 8;
	    else if ("AnnotationDefault".equals(string_102_))
		i_97_ = i + 8;
	    else if ("Synthetic".equals(string_102_))
		context.e |= 0x41000;
	    else if ("RuntimeInvisibleAnnotations".equals(string_102_))
		i_94_ = i + 8;
	    else if ("RuntimeInvisibleTypeAnnotations".equals(string_102_))
		i_96_ = i + 8;
	    else if ("RuntimeVisibleParameterAnnotations".equals(string_102_))
		i_98_ = i + 8;
	    else if ("RuntimeInvisibleParameterAnnotations"
			 .equals(string_102_))
		i_99_ = i + 8;
	    else if ("MethodParameters".equals(string_102_))
		i_92_ = i + 8;
	    else {
		Attribute attribute_104_ = a(context.a, string_102_, i + 8,
					     readInt(i + 4), cs, -1, null);
		if (attribute_104_ != null) {
		    attribute_104_.a = attribute;
		    attribute = attribute_104_;
		}
	    }
	    i += 6 + readInt(i + 4);
	}
	i += 2;
	MethodVisitor methodvisitor
	    = classvisitor.visitMethod(context.e, context.f, context.g, string,
				       strings);
	if (methodvisitor == null)
	    return i;
	if (methodvisitor instanceof MethodWriter) {
	    MethodWriter methodwriter = (MethodWriter) methodvisitor;
	    if (methodwriter.b.M == this && string == methodwriter.g) {
		boolean bool = false;
		if (strings == null)
		    bool = methodwriter.j == 0;
		else if (strings.length == methodwriter.j) {
		    bool = true;
		    for (int i_105_ = strings.length - 1; i_105_ >= 0;
			 i_105_--) {
			i_91_ -= 2;
			if (methodwriter.k[i_105_]
			    != readUnsignedShort(i_91_)) {
			    bool = false;
			    break;
			}
		    }
		}
		if (bool) {
		    methodwriter.h = i_100_;
		    methodwriter.i = i - i_100_;
		    return i;
		}
	    }
	}
	if (i_92_ != 0) {
	    int i_106_ = b[i_92_] & 0xff;
	    int i_107_ = i_92_ + 1;
	    while (i_106_ > 0) {
		methodvisitor.visitParameter(readUTF8(i_107_, cs),
					     readUnsignedShort(i_107_ + 2));
		i_106_--;
		i_107_ += 4;
	    }
	}
	if (i_97_ != 0) {
	    AnnotationVisitor annotationvisitor
		= methodvisitor.visitAnnotationDefault();
	    a(i_97_, cs, (String) null, annotationvisitor);
	    if (annotationvisitor != null)
		annotationvisitor.visitEnd();
	}
	if (i_93_ != 0) {
	    int i_108_ = readUnsignedShort(i_93_);
	    int i_109_ = i_93_ + 2;
	    for (/**/; i_108_ > 0; i_108_--)
		i_109_ = a(i_109_ + 2, cs, true,
			   methodvisitor.visitAnnotation(readUTF8(i_109_, cs),
							 true));
	}
	if (i_94_ != 0) {
	    int i_110_ = readUnsignedShort(i_94_);
	    int i_111_ = i_94_ + 2;
	    for (/**/; i_110_ > 0; i_110_--)
		i_111_ = a(i_111_ + 2, cs, true,
			   methodvisitor.visitAnnotation(readUTF8(i_111_, cs),
							 false));
	}
	if (i_95_ != 0) {
	    int i_112_ = readUnsignedShort(i_95_);
	    int i_113_ = i_95_ + 2;
	    for (/**/; i_112_ > 0; i_112_--) {
		i_113_ = a(context, i_113_);
		i_113_
		    = a(i_113_ + 2, cs, true,
			methodvisitor.visitTypeAnnotation(context.i, context.j,
							  readUTF8(i_113_, cs),
							  true));
	    }
	}
	if (i_96_ != 0) {
	    int i_114_ = readUnsignedShort(i_96_);
	    int i_115_ = i_96_ + 2;
	    for (/**/; i_114_ > 0; i_114_--) {
		i_115_ = a(context, i_115_);
		i_115_
		    = a(i_115_ + 2, cs, true,
			methodvisitor.visitTypeAnnotation(context.i, context.j,
							  readUTF8(i_115_, cs),
							  false));
	    }
	}
	if (i_98_ != 0)
	    b(methodvisitor, context, i_98_, true);
	if (i_99_ != 0)
	    b(methodvisitor, context, i_99_, false);
	Attribute attribute_116_;
	for (/**/; attribute != null; attribute = attribute_116_) {
	    attribute_116_ = attribute.a;
	    attribute.a = null;
	    methodvisitor.visitAttribute(attribute);
	}
	if (i_90_ != 0) {
	    methodvisitor.visitCode();
	    a(methodvisitor, context, i_90_);
	}
	methodvisitor.visitEnd();
	return i;
    }
    
    private void a(MethodVisitor methodvisitor, Context context, int i) {
	byte[] is = b;
	char[] cs = context.c;
	int i_117_ = readUnsignedShort(i);
	int i_118_ = readUnsignedShort(i + 2);
	int i_119_ = readInt(i + 4);
	i += 8;
	int i_120_ = i;
	int i_121_ = i + i_119_;
	Label[] labels = context.h = new Label[i_119_ + 2];
	readLabel(i_119_ + 1, labels);
	while (i < i_121_) {
	    int i_122_ = i - i_120_;
	    int i_123_ = is[i] & 0xff;
	    switch (ClassWriter.a[i_123_]) {
	    case 0:
	    case 4:
		i++;
		break;
	    case 9:
		readLabel(i_122_ + readShort(i + 1), labels);
		i += 3;
		break;
	    case 10:
		readLabel(i_122_ + readInt(i + 1), labels);
		i += 5;
		break;
	    case 17:
		i_123_ = is[i + 1] & 0xff;
		if (i_123_ == 132)
		    i += 6;
		else
		    i += 4;
		break;
	    case 14:
		i = i + 4 - (i_122_ & 0x3);
		readLabel(i_122_ + readInt(i), labels);
		for (int i_124_ = readInt(i + 8) - readInt(i + 4) + 1;
		     i_124_ > 0; i_124_--) {
		    readLabel(i_122_ + readInt(i + 12), labels);
		    i += 4;
		}
		i += 12;
		break;
	    case 15:
		i = i + 4 - (i_122_ & 0x3);
		readLabel(i_122_ + readInt(i), labels);
		for (int i_125_ = readInt(i + 4); i_125_ > 0; i_125_--) {
		    readLabel(i_122_ + readInt(i + 12), labels);
		    i += 8;
		}
		i += 8;
		break;
	    case 1:
	    case 3:
	    case 11:
		i += 2;
		break;
	    case 2:
	    case 5:
	    case 6:
	    case 12:
	    case 13:
		i += 3;
		break;
	    case 7:
	    case 8:
		i += 5;
		break;
	    default:
		i += 4;
	    }
	}
	for (int i_126_ = readUnsignedShort(i); i_126_ > 0; i_126_--) {
	    Label label = readLabel(readUnsignedShort(i + 2), labels);
	    Label label_127_ = readLabel(readUnsignedShort(i + 4), labels);
	    Label label_128_ = readLabel(readUnsignedShort(i + 6), labels);
	    String string = readUTF8(a[readUnsignedShort(i + 8)], cs);
	    methodvisitor.visitTryCatchBlock(label, label_127_, label_128_,
					     string);
	    i += 8;
	}
	i += 2;
	int[] is_129_ = null;
	int[] is_130_ = null;
	int i_131_ = 0;
	int i_132_ = 0;
	int i_133_ = -1;
	int i_134_ = -1;
	int i_135_ = 0;
	int i_136_ = 0;
	boolean bool = true;
	boolean bool_137_ = (context.b & 0x8) != 0;
	int i_138_ = 0;
	int i_139_ = 0;
	int i_140_ = 0;
	Context context_141_ = null;
	Attribute attribute = null;
	for (int i_142_ = readUnsignedShort(i); i_142_ > 0; i_142_--) {
	    String string = readUTF8(i + 2, cs);
	    if ("LocalVariableTable".equals(string)) {
		if ((context.b & 0x2) == 0) {
		    i_135_ = i + 8;
		    int i_143_ = readUnsignedShort(i + 8);
		    int i_144_ = i;
		    for (/**/; i_143_ > 0; i_143_--) {
			int i_145_ = readUnsignedShort(i_144_ + 10);
			if (labels[i_145_] == null)
			    readLabel(i_145_, labels).a |= 0x1;
			i_145_ += readUnsignedShort(i_144_ + 12);
			if (labels[i_145_] == null)
			    readLabel(i_145_, labels).a |= 0x1;
			i_144_ += 10;
		    }
		}
	    } else if ("LocalVariableTypeTable".equals(string))
		i_136_ = i + 8;
	    else if ("LineNumberTable".equals(string)) {
		if ((context.b & 0x2) == 0) {
		    int i_146_ = readUnsignedShort(i + 8);
		    int i_147_ = i;
		    for (/**/; i_146_ > 0; i_146_--) {
			int i_148_ = readUnsignedShort(i_147_ + 10);
			if (labels[i_148_] == null)
			    readLabel(i_148_, labels).a |= 0x1;
			labels[i_148_].b = readUnsignedShort(i_147_ + 12);
			i_147_ += 4;
		    }
		}
	    } else if ("RuntimeVisibleTypeAnnotations".equals(string)) {
		is_129_ = a(methodvisitor, context, i + 8, true);
		i_133_ = (is_129_.length == 0 || readByte(is_129_[0]) < 67 ? -1
			  : readUnsignedShort(is_129_[0] + 1));
	    } else if ("RuntimeInvisibleTypeAnnotations".equals(string)) {
		is_130_ = a(methodvisitor, context, i + 8, false);
		i_134_ = (is_130_.length == 0 || readByte(is_130_[0]) < 67 ? -1
			  : readUnsignedShort(is_130_[0] + 1));
	    } else if ("StackMapTable".equals(string)) {
		if ((context.b & 0x4) == 0) {
		    i_138_ = i + 10;
		    i_139_ = readInt(i + 4);
		    i_140_ = readUnsignedShort(i + 8);
		}
	    } else if ("StackMap".equals(string)) {
		if ((context.b & 0x4) == 0) {
		    bool = false;
		    i_138_ = i + 10;
		    i_139_ = readInt(i + 4);
		    i_140_ = readUnsignedShort(i + 8);
		}
	    } else {
		for (int i_149_ = 0; i_149_ < context.a.length; i_149_++) {
		    if (context.a[i_149_].type.equals(string)) {
			Attribute attribute_150_
			    = context.a[i_149_].read(this, i + 8,
						     readInt(i + 4), cs,
						     i_120_ - 8, labels);
			if (attribute_150_ != null) {
			    attribute_150_.a = attribute;
			    attribute = attribute_150_;
			}
		    }
		}
	    }
	    i += 6 + readInt(i + 4);
	}
	i += 2;
	if (i_138_ != 0) {
	    context_141_ = context;
	    context_141_.o = -1;
	    context_141_.p = 0;
	    context_141_.q = 0;
	    context_141_.r = 0;
	    context_141_.t = 0;
	    context_141_.s = new Object[i_118_];
	    context_141_.u = new Object[i_117_];
	    if (bool_137_)
		a(context);
	    for (int i_151_ = i_138_; i_151_ < i_138_ + i_139_ - 2; i_151_++) {
		if (is[i_151_] == 8) {
		    int i_152_ = readUnsignedShort(i_151_ + 1);
		    if (i_152_ >= 0 && i_152_ < i_119_
			&& (is[i_120_ + i_152_] & 0xff) == 187)
			readLabel(i_152_, labels);
		}
	    }
	}
	i = i_120_;
	while (i < i_121_) {
	    int i_153_ = i - i_120_;
	    Label label = labels[i_153_];
	    if (label != null) {
		methodvisitor.visitLabel(label);
		if ((context.b & 0x2) == 0 && label.b > 0)
		    methodvisitor.visitLineNumber(label.b, label);
	    }
	    while (context_141_ != null
		   && (context_141_.o == i_153_ || context_141_.o == -1)) {
		if (context_141_.o != -1) {
		    if (!bool || bool_137_)
			methodvisitor.visitFrame(-1, context_141_.q,
						 context_141_.s,
						 context_141_.t,
						 context_141_.u);
		    else
			methodvisitor.visitFrame(context_141_.p,
						 context_141_.r,
						 context_141_.s,
						 context_141_.t,
						 context_141_.u);
		}
		if (i_140_ > 0) {
		    i_138_ = a(i_138_, bool, bool_137_, context_141_);
		    i_140_--;
		} else
		    context_141_ = null;
	    }
	    int i_154_ = is[i] & 0xff;
	    switch (ClassWriter.a[i_154_]) {
	    case 0:
		methodvisitor.visitInsn(i_154_);
		i++;
		break;
	    case 4:
		if (i_154_ > 54) {
		    i_154_ -= 59;
		    methodvisitor.visitVarInsn(54 + (i_154_ >> 2),
					       i_154_ & 0x3);
		} else {
		    i_154_ -= 26;
		    methodvisitor.visitVarInsn(21 + (i_154_ >> 2),
					       i_154_ & 0x3);
		}
		i++;
		break;
	    case 9:
		methodvisitor.visitJumpInsn(i_154_,
					    labels[i_153_ + readShort(i + 1)]);
		i += 3;
		break;
	    case 10:
		methodvisitor.visitJumpInsn(i_154_ - 33,
					    labels[i_153_ + readInt(i + 1)]);
		i += 5;
		break;
	    case 17:
		i_154_ = is[i + 1] & 0xff;
		if (i_154_ == 132) {
		    methodvisitor.visitIincInsn(readUnsignedShort(i + 2),
						readShort(i + 4));
		    i += 6;
		} else {
		    methodvisitor.visitVarInsn(i_154_,
					       readUnsignedShort(i + 2));
		    i += 4;
		}
		break;
	    case 14: {
		i = i + 4 - (i_153_ & 0x3);
		int i_155_ = i_153_ + readInt(i);
		int i_156_ = readInt(i + 4);
		int i_157_ = readInt(i + 8);
		Label[] labels_158_ = new Label[i_157_ - i_156_ + 1];
		i += 12;
		for (int i_159_ = 0; i_159_ < labels_158_.length; i_159_++) {
		    labels_158_[i_159_] = labels[i_153_ + readInt(i)];
		    i += 4;
		}
		methodvisitor.visitTableSwitchInsn(i_156_, i_157_,
						   labels[i_155_],
						   labels_158_);
		break;
	    }
	    case 15: {
		i = i + 4 - (i_153_ & 0x3);
		int i_160_ = i_153_ + readInt(i);
		int i_161_ = readInt(i + 4);
		int[] is_162_ = new int[i_161_];
		Label[] labels_163_ = new Label[i_161_];
		i += 8;
		for (int i_164_ = 0; i_164_ < i_161_; i_164_++) {
		    is_162_[i_164_] = readInt(i);
		    labels_163_[i_164_] = labels[i_153_ + readInt(i + 4)];
		    i += 8;
		}
		methodvisitor.visitLookupSwitchInsn(labels[i_160_], is_162_,
						    labels_163_);
		break;
	    }
	    case 3:
		methodvisitor.visitVarInsn(i_154_, is[i + 1] & 0xff);
		i += 2;
		break;
	    case 1:
		methodvisitor.visitIntInsn(i_154_, is[i + 1]);
		i += 2;
		break;
	    case 2:
		methodvisitor.visitIntInsn(i_154_, readShort(i + 1));
		i += 3;
		break;
	    case 11:
		methodvisitor.visitLdcInsn(readConst(is[i + 1] & 0xff, cs));
		i += 2;
		break;
	    case 12:
		methodvisitor.visitLdcInsn(readConst(readUnsignedShort(i + 1),
						     cs));
		i += 3;
		break;
	    case 6:
	    case 7: {
		int i_165_ = a[readUnsignedShort(i + 1)];
		boolean bool_166_ = is[i_165_ - 1] == 11;
		String string = readClass(i_165_, cs);
		i_165_ = a[readUnsignedShort(i_165_ + 2)];
		String string_167_ = readUTF8(i_165_, cs);
		String string_168_ = readUTF8(i_165_ + 2, cs);
		if (i_154_ < 182)
		    methodvisitor.visitFieldInsn(i_154_, string, string_167_,
						 string_168_);
		else
		    methodvisitor.visitMethodInsn(i_154_, string, string_167_,
						  string_168_, bool_166_);
		if (i_154_ == 185)
		    i += 5;
		else
		    i += 3;
		break;
	    }
	    case 8: {
		int i_169_ = a[readUnsignedShort(i + 1)];
		int i_170_ = context.d[readUnsignedShort(i_169_)];
		Handle handle
		    = (Handle) readConst(readUnsignedShort(i_170_), cs);
		int i_171_ = readUnsignedShort(i_170_ + 2);
		Object[] objects = new Object[i_171_];
		i_170_ += 4;
		for (int i_172_ = 0; i_172_ < i_171_; i_172_++) {
		    objects[i_172_] = readConst(readUnsignedShort(i_170_), cs);
		    i_170_ += 2;
		}
		i_169_ = a[readUnsignedShort(i_169_ + 2)];
		String string = readUTF8(i_169_, cs);
		String string_173_ = readUTF8(i_169_ + 2, cs);
		methodvisitor.visitInvokeDynamicInsn(string, string_173_,
						     handle, objects);
		i += 5;
		break;
	    }
	    case 5:
		methodvisitor.visitTypeInsn(i_154_, readClass(i + 1, cs));
		i += 3;
		break;
	    case 13:
		methodvisitor.visitIincInsn(is[i + 1] & 0xff, is[i + 2]);
		i += 3;
		break;
	    default:
		methodvisitor.visitMultiANewArrayInsn(readClass(i + 1, cs),
						      is[i + 3] & 0xff);
		i += 4;
	    }
	    for (;;) {
		if (is_129_ != null && i_131_ < is_129_.length
		    && i_133_ <= i_153_) {
		    if (i_133_ == i_153_) {
			int i_174_ = a(context, is_129_[i_131_]);
			a(i_174_ + 2, cs, true,
			  methodvisitor.visitInsnAnnotation(context.i,
							    context.j,
							    readUTF8(i_174_,
								     cs),
							    true));
		    }
		    i_133_ = ((++i_131_ >= is_129_.length
			       || readByte(is_129_[i_131_]) < 67)
			      ? -1 : readUnsignedShort(is_129_[i_131_] + 1));
		} else {
		    for (/**/;
			 (is_130_ != null && i_132_ < is_130_.length
			  && i_134_ <= i_153_);
			 i_134_ = ((++i_132_ >= is_130_.length
				    || readByte(is_130_[i_132_]) < 67)
				   ? -1
				   : readUnsignedShort(is_130_[i_132_] + 1))) {
			if (i_134_ == i_153_) {
			    int i_175_ = a(context, is_130_[i_132_]);
			    a(i_175_ + 2, cs, true,
			      (methodvisitor.visitInsnAnnotation
			       (context.i, context.j, readUTF8(i_175_, cs),
				false)));
			}
		    }
		    break;
		}
	    }
	}
	if (labels[i_119_] != null)
	    methodvisitor.visitLabel(labels[i_119_]);
	if ((context.b & 0x2) == 0 && i_135_ != 0) {
	    int[] is_176_ = null;
	    if (i_136_ != 0) {
		i = i_136_ + 2;
		is_176_ = new int[readUnsignedShort(i_136_) * 3];
		int i_177_ = is_176_.length;
		while (i_177_ > 0) {
		    is_176_[--i_177_] = i + 6;
		    is_176_[--i_177_] = readUnsignedShort(i + 8);
		    is_176_[--i_177_] = readUnsignedShort(i);
		    i += 10;
		}
	    }
	    i = i_135_ + 2;
	    for (int i_178_ = readUnsignedShort(i_135_); i_178_ > 0;
		 i_178_--) {
		int i_179_ = readUnsignedShort(i);
		int i_180_ = readUnsignedShort(i + 2);
		int i_181_ = readUnsignedShort(i + 8);
		String string = null;
		if (is_176_ != null) {
		    for (int i_182_ = 0; i_182_ < is_176_.length;
			 i_182_ += 3) {
			if (is_176_[i_182_] == i_179_
			    && is_176_[i_182_ + 1] == i_181_) {
			    string = readUTF8(is_176_[i_182_ + 2], cs);
			    break;
			}
		    }
		}
		methodvisitor.visitLocalVariable(readUTF8(i + 4, cs),
						 readUTF8(i + 6, cs), string,
						 labels[i_179_],
						 labels[i_179_ + i_180_],
						 i_181_);
		i += 10;
	    }
	}
	if (is_129_ != null) {
	    for (int i_183_ = 0; i_183_ < is_129_.length; i_183_++) {
		if (readByte(is_129_[i_183_]) >> 1 == 32) {
		    int i_184_ = a(context, is_129_[i_183_]);
		    i_184_ = a(i_184_ + 2, cs, true,
			       (methodvisitor.visitLocalVariableAnnotation
				(context.i, context.j, context.l, context.m,
				 context.n, readUTF8(i_184_, cs), true)));
		}
	    }
	}
	if (is_130_ != null) {
	    for (int i_185_ = 0; i_185_ < is_130_.length; i_185_++) {
		if (readByte(is_130_[i_185_]) >> 1 == 32) {
		    int i_186_ = a(context, is_130_[i_185_]);
		    i_186_ = a(i_186_ + 2, cs, true,
			       (methodvisitor.visitLocalVariableAnnotation
				(context.i, context.j, context.l, context.m,
				 context.n, readUTF8(i_186_, cs), false)));
		}
	    }
	}
	Attribute attribute_187_;
	for (/**/; attribute != null; attribute = attribute_187_) {
	    attribute_187_ = attribute.a;
	    attribute.a = null;
	    methodvisitor.visitAttribute(attribute);
	}
	methodvisitor.visitMaxs(i_117_, i_118_);
    }
    
    private int[] a(MethodVisitor methodvisitor, Context context, int i,
		    boolean bool) {
	char[] cs = context.c;
	int[] is = new int[readUnsignedShort(i)];
	i += 2;
	for (int i_188_ = 0; i_188_ < is.length; i_188_++) {
	    is[i_188_] = i;
	    int i_189_ = readInt(i);
	    switch (i_189_ >>> 24) {
	    case 0:
	    case 1:
	    case 22:
		i += 2;
		break;
	    case 19:
	    case 20:
	    case 21:
		i++;
		break;
	    case 64:
	    case 65:
		for (int i_190_ = readUnsignedShort(i + 1); i_190_ > 0;
		     i_190_--) {
		    int i_191_ = readUnsignedShort(i + 3);
		    int i_192_ = readUnsignedShort(i + 5);
		    readLabel(i_191_, context.h);
		    readLabel(i_191_ + i_192_, context.h);
		    i += 6;
		}
		i += 3;
		break;
	    case 71:
	    case 72:
	    case 73:
	    case 74:
	    case 75:
		i += 4;
		break;
	    default:
		i += 3;
	    }
	    int i_193_ = readByte(i);
	    if (i_189_ >>> 24 == 66) {
		TypePath typepath = i_193_ == 0 ? null : new TypePath(b, i);
		i += 1 + 2 * i_193_;
		i = a(i + 2, cs, true,
		      methodvisitor.visitTryCatchAnnotation(i_189_, typepath,
							    readUTF8(i, cs),
							    bool));
	    } else
		i = a(i + 3 + 2 * i_193_, cs, true, null);
	}
	return is;
    }
    
    private int a(Context context, int i) {
	int i_194_ = readInt(i);
	switch (i_194_ >>> 24) {
	case 0:
	case 1:
	case 22:
	    i_194_ &= ~0xffff;
	    i += 2;
	    break;
	case 19:
	case 20:
	case 21:
	    i_194_ &= ~0xffffff;
	    i++;
	    break;
	case 64:
	case 65: {
	    i_194_ &= ~0xffffff;
	    int i_195_ = readUnsignedShort(i + 1);
	    context.l = new Label[i_195_];
	    context.m = new Label[i_195_];
	    context.n = new int[i_195_];
	    i += 3;
	    for (int i_196_ = 0; i_196_ < i_195_; i_196_++) {
		int i_197_ = readUnsignedShort(i);
		int i_198_ = readUnsignedShort(i + 2);
		context.l[i_196_] = readLabel(i_197_, context.h);
		context.m[i_196_] = readLabel(i_197_ + i_198_, context.h);
		context.n[i_196_] = readUnsignedShort(i + 4);
		i += 6;
	    }
	    break;
	}
	case 71:
	case 72:
	case 73:
	case 74:
	case 75:
	    i_194_ &= ~0xffff00;
	    i += 4;
	    break;
	default:
	    i_194_ = i_194_ & (i_194_ >>> 24 < 67 ? -256 : -16777216);
	    i += 3;
	}
	int i_199_ = readByte(i);
	context.i = i_194_;
	context.j = i_199_ == 0 ? null : new TypePath(b, i);
	return i + 1 + 2 * i_199_;
    }
    
    private void b(MethodVisitor methodvisitor, Context context, int i,
		   boolean bool) {
	int i_200_ = b[i++] & 0xff;
	int i_201_ = Type.getArgumentTypes(context.g).length - i_200_;
	int i_202_;
	for (i_202_ = 0; i_202_ < i_201_; i_202_++) {
	    AnnotationVisitor annotationvisitor
		= (methodvisitor.visitParameterAnnotation
		   (i_202_, "Ljava/lang/Synthetic;", false));
	    if (annotationvisitor != null)
		annotationvisitor.visitEnd();
	}
	char[] cs = context.c;
	for (/**/; i_202_ < i_200_ + i_201_; i_202_++) {
	    int i_203_ = readUnsignedShort(i);
	    i += 2;
	    for (/**/; i_203_ > 0; i_203_--) {
		AnnotationVisitor annotationvisitor
		    = methodvisitor.visitParameterAnnotation(i_202_,
							     readUTF8(i, cs),
							     bool);
		i = a(i + 2, cs, true, annotationvisitor);
	    }
	}
    }
    
    private int a(int i, char[] cs, boolean bool,
		  AnnotationVisitor annotationvisitor) {
	int i_204_ = readUnsignedShort(i);
	i += 2;
	if (bool) {
	    for (/**/; i_204_ > 0; i_204_--)
		i = a(i + 2, cs, readUTF8(i, cs), annotationvisitor);
	} else {
	    for (/**/; i_204_ > 0; i_204_--)
		i = a(i, cs, (String) null, annotationvisitor);
	}
	if (annotationvisitor != null)
	    annotationvisitor.visitEnd();
	return i;
    }
    
    private int a(int i, char[] cs, String string,
		  AnnotationVisitor annotationvisitor) {
	if (annotationvisitor == null) {
	    switch (b[i] & 0xff) {
	    case 101:
		return i + 5;
	    case 64:
		return a(i + 3, cs, true, null);
	    case 91:
		return a(i + 1, cs, false, null);
	    default:
		return i + 3;
	    }
	}
	switch (b[i++] & 0xff) {
	case 68:
	case 70:
	case 73:
	case 74:
	    annotationvisitor.visit(string,
				    readConst(readUnsignedShort(i), cs));
	    i += 2;
	    break;
	case 66:
	    annotationvisitor.visit
		(string, new Byte((byte) readInt(a[readUnsignedShort(i)])));
	    i += 2;
	    break;
	case 90:
	    annotationvisitor.visit(string,
				    (readInt(a[readUnsignedShort(i)]) == 0
				     ? Boolean.FALSE : Boolean.TRUE));
	    i += 2;
	    break;
	case 83:
	    annotationvisitor.visit
		(string, new Short((short) readInt(a[readUnsignedShort(i)])));
	    i += 2;
	    break;
	case 67:
	    annotationvisitor.visit
		(string,
		 new Character((char) readInt(a[readUnsignedShort(i)])));
	    i += 2;
	    break;
	case 115:
	    annotationvisitor.visit(string, readUTF8(i, cs));
	    i += 2;
	    break;
	case 101:
	    annotationvisitor.visitEnum(string, readUTF8(i, cs),
					readUTF8(i + 2, cs));
	    i += 4;
	    break;
	case 99:
	    annotationvisitor.visit(string, Type.getType(readUTF8(i, cs)));
	    i += 2;
	    break;
	case 64:
	    i = a(i + 2, cs, true,
		  annotationvisitor.visitAnnotation(string, readUTF8(i, cs)));
	    break;
	case 91: {
	    int i_205_ = readUnsignedShort(i);
	    i += 2;
	    if (i_205_ == 0)
		return a(i - 2, cs, false,
			 annotationvisitor.visitArray(string));
	    switch (b[i++] & 0xff) {
	    case 66: {
		byte[] is = new byte[i_205_];
		for (int i_206_ = 0; i_206_ < i_205_; i_206_++) {
		    is[i_206_] = (byte) readInt(a[readUnsignedShort(i)]);
		    i += 3;
		}
		annotationvisitor.visit(string, is);
		i--;
		break;
	    }
	    case 90: {
		boolean[] bools = new boolean[i_205_];
		for (int i_207_ = 0; i_207_ < i_205_; i_207_++) {
		    bools[i_207_] = readInt(a[readUnsignedShort(i)]) != 0;
		    i += 3;
		}
		annotationvisitor.visit(string, bools);
		i--;
		break;
	    }
	    case 83: {
		short[] is = new short[i_205_];
		for (int i_208_ = 0; i_208_ < i_205_; i_208_++) {
		    is[i_208_] = (short) readInt(a[readUnsignedShort(i)]);
		    i += 3;
		}
		annotationvisitor.visit(string, is);
		i--;
		break;
	    }
	    case 67: {
		char[] cs_209_ = new char[i_205_];
		for (int i_210_ = 0; i_210_ < i_205_; i_210_++) {
		    cs_209_[i_210_] = (char) readInt(a[readUnsignedShort(i)]);
		    i += 3;
		}
		annotationvisitor.visit(string, cs_209_);
		i--;
		break;
	    }
	    case 73: {
		int[] is = new int[i_205_];
		for (int i_211_ = 0; i_211_ < i_205_; i_211_++) {
		    is[i_211_] = readInt(a[readUnsignedShort(i)]);
		    i += 3;
		}
		annotationvisitor.visit(string, is);
		i--;
		break;
	    }
	    case 74: {
		long[] ls = new long[i_205_];
		for (int i_212_ = 0; i_212_ < i_205_; i_212_++) {
		    ls[i_212_] = readLong(a[readUnsignedShort(i)]);
		    i += 3;
		}
		annotationvisitor.visit(string, ls);
		i--;
		break;
	    }
	    case 70: {
		float[] fs = new float[i_205_];
		for (int i_213_ = 0; i_213_ < i_205_; i_213_++) {
		    fs[i_213_] = (Float.intBitsToFloat
				  (readInt(a[readUnsignedShort(i)])));
		    i += 3;
		}
		annotationvisitor.visit(string, fs);
		i--;
		break;
	    }
	    case 68: {
		double[] ds = new double[i_205_];
		for (int i_214_ = 0; i_214_ < i_205_; i_214_++) {
		    ds[i_214_] = (Double.longBitsToDouble
				  (readLong(a[readUnsignedShort(i)])));
		    i += 3;
		}
		annotationvisitor.visit(string, ds);
		i--;
		break;
	    }
	    default:
		i = a(i - 3, cs, false, annotationvisitor.visitArray(string));
	    }
	    break;
	}
	}
	return i;
    }
    
    private void a(Context context) {
	String string = context.g;
	Object[] objects = context.s;
	int i = 0;
	if ((context.e & 0x8) == 0) {
	    if ("<init>".equals(context.f))
		objects[i++] = Opcodes.UNINITIALIZED_THIS;
	    else
		objects[i++] = readClass(header + 2, context.c);
	}
	int i_215_ = 1;
	for (;;) {
	    int i_216_ = i_215_;
	    switch (string.charAt(i_215_++)) {
	    case 'B':
	    case 'C':
	    case 'I':
	    case 'S':
	    case 'Z':
		objects[i++] = Opcodes.INTEGER;
		break;
	    case 'F':
		objects[i++] = Opcodes.FLOAT;
		break;
	    case 'J':
		objects[i++] = Opcodes.LONG;
		break;
	    case 'D':
		objects[i++] = Opcodes.DOUBLE;
		break;
	    case '[':
		for (/**/; string.charAt(i_215_) == '['; i_215_++) {
		    /* empty */
		}
		if (string.charAt(i_215_) == 'L') {
		    for (i_215_++; string.charAt(i_215_) != ';'; i_215_++) {
			/* empty */
		    }
		}
		objects[i++] = string.substring(i_216_, ++i_215_);
		break;
	    case 'L':
		for (/**/; string.charAt(i_215_) != ';'; i_215_++) {
		    /* empty */
		}
		objects[i++] = string.substring(i_216_ + 1, i_215_++);
		break;
	    default:
		context.q = i;
		return;
	    }
	}
    }
    
    private int a(int i, boolean bool, boolean bool_217_, Context context) {
	char[] cs = context.c;
	Label[] labels = context.h;
	int i_218_;
	if (bool)
	    i_218_ = b[i++] & 0xff;
	else {
	    i_218_ = 255;
	    context.o = -1;
	}
	context.r = 0;
	int i_219_;
	if (i_218_ < 64) {
	    i_219_ = i_218_;
	    context.p = 3;
	    context.t = 0;
	} else if (i_218_ < 128) {
	    i_219_ = i_218_ - 64;
	    i = a(context.u, 0, i, cs, labels);
	    context.p = 4;
	    context.t = 1;
	} else {
	    i_219_ = readUnsignedShort(i);
	    i += 2;
	    if (i_218_ == 247) {
		i = a(context.u, 0, i, cs, labels);
		context.p = 4;
		context.t = 1;
	    } else if (i_218_ >= 248 && i_218_ < 251) {
		context.p = 2;
		context.r = 251 - i_218_;
		context.q -= context.r;
		context.t = 0;
	    } else if (i_218_ == 251) {
		context.p = 3;
		context.t = 0;
	    } else if (i_218_ < 255) {
		int i_220_ = bool_217_ ? context.q : 0;
		for (int i_221_ = i_218_ - 251; i_221_ > 0; i_221_--)
		    i = a(context.s, i_220_++, i, cs, labels);
		context.p = 1;
		context.r = i_218_ - 251;
		context.q += context.r;
		context.t = 0;
	    } else {
		context.p = 0;
		int i_222_ = readUnsignedShort(i);
		i += 2;
		context.r = i_222_;
		context.q = i_222_;
		int i_223_ = 0;
		for (/**/; i_222_ > 0; i_222_--)
		    i = a(context.s, i_223_++, i, cs, labels);
		i_222_ = readUnsignedShort(i);
		i += 2;
		context.t = i_222_;
		i_223_ = 0;
		for (/**/; i_222_ > 0; i_222_--)
		    i = a(context.u, i_223_++, i, cs, labels);
	    }
	}
	context.o += i_219_ + 1;
	readLabel(context.o, labels);
	return i;
    }
    
    private int a(Object[] objects, int i, int i_224_, char[] cs,
		  Label[] labels) {
	int i_225_ = b[i_224_++] & 0xff;
	switch (i_225_) {
	case 0:
	    objects[i] = Opcodes.TOP;
	    break;
	case 1:
	    objects[i] = Opcodes.INTEGER;
	    break;
	case 2:
	    objects[i] = Opcodes.FLOAT;
	    break;
	case 3:
	    objects[i] = Opcodes.DOUBLE;
	    break;
	case 4:
	    objects[i] = Opcodes.LONG;
	    break;
	case 5:
	    objects[i] = Opcodes.NULL;
	    break;
	case 6:
	    objects[i] = Opcodes.UNINITIALIZED_THIS;
	    break;
	case 7:
	    objects[i] = readClass(i_224_, cs);
	    i_224_ += 2;
	    break;
	default:
	    objects[i] = readLabel(readUnsignedShort(i_224_), labels);
	    i_224_ += 2;
	}
	return i_224_;
    }
    
    protected Label readLabel(int i, Label[] labels) {
	if (labels[i] == null)
	    labels[i] = new Label();
	return labels[i];
    }
    
    private int a() {
	int i = header + 8 + readUnsignedShort(header + 6) * 2;
	for (int i_226_ = readUnsignedShort(i); i_226_ > 0; i_226_--) {
	    for (int i_227_ = readUnsignedShort(i + 8); i_227_ > 0; i_227_--)
		i += 6 + readInt(i + 12);
	    i += 8;
	}
	i += 2;
	for (int i_228_ = readUnsignedShort(i); i_228_ > 0; i_228_--) {
	    for (int i_229_ = readUnsignedShort(i + 8); i_229_ > 0; i_229_--)
		i += 6 + readInt(i + 12);
	    i += 8;
	}
	return i + 2;
    }
    
    private Attribute a(Attribute[] attributes, String string, int i,
			int i_230_, char[] cs, int i_231_, Label[] labels) {
	for (int i_232_ = 0; i_232_ < attributes.length; i_232_++) {
	    if (attributes[i_232_].type.equals(string))
		return attributes[i_232_].read(this, i, i_230_, cs, i_231_,
					       labels);
	}
	return new Attribute(string).read(this, i, i_230_, null, -1, null);
    }
    
    public int getItemCount() {
	return a.length;
    }
    
    public int getItem(int i) {
	return a[i];
    }
    
    public int getMaxStringLength() {
	return d;
    }
    
    public int readByte(int i) {
	return b[i] & 0xff;
    }
    
    public int readUnsignedShort(int i) {
	byte[] is = b;
	return (is[i] & 0xff) << 8 | is[i + 1] & 0xff;
    }
    
    public short readShort(int i) {
	byte[] is = b;
	return (short) ((is[i] & 0xff) << 8 | is[i + 1] & 0xff);
    }
    
    public int readInt(int i) {
	byte[] is = b;
	return ((is[i] & 0xff) << 24 | (is[i + 1] & 0xff) << 16
		| (is[i + 2] & 0xff) << 8 | is[i + 3] & 0xff);
    }
    
    public long readLong(int i) {
	long l = (long) readInt(i);
	long l_233_ = (long) readInt(i + 4) & 0xffffffffL;
	return l << 32 | l_233_;
    }
    
    public String readUTF8(int i, char[] cs) {
	int i_234_ = readUnsignedShort(i);
	if (i == 0 || i_234_ == 0)
	    return null;
	String string = c[i_234_];
	if (string != null)
	    return string;
	i = a[i_234_];
	return c[i_234_] = a(i + 2, readUnsignedShort(i), cs);
    }
    
    private String a(int i, int i_235_, char[] cs) {
	int i_236_ = i + i_235_;
	byte[] is = b;
	int i_237_ = 0;
	int i_238_ = 0;
	int i_239_ = 0;
	while (i < i_236_) {
	    int i_240_ = is[i++];
	    switch (i_238_) {
	    case 0:
		i_240_ &= 0xff;
		if (i_240_ < 128)
		    cs[i_237_++] = (char) i_240_;
		else if (i_240_ < 224 && i_240_ > 191) {
		    i_239_ = (char) (i_240_ & 0x1f);
		    i_238_ = 1;
		} else {
		    i_239_ = (char) (i_240_ & 0xf);
		    i_238_ = 2;
		}
		break;
	    case 1:
		cs[i_237_++] = (char) (i_239_ << 6 | i_240_ & 0x3f);
		i_238_ = 0;
		break;
	    case 2:
		i_239_ = (char) (i_239_ << 6 | i_240_ & 0x3f);
		i_238_ = 1;
		break;
	    }
	}
	return new String(cs, 0, i_237_);
    }
    
    public String readClass(int i, char[] cs) {
	return readUTF8(a[readUnsignedShort(i)], cs);
    }
    
    public Object readConst(int i, char[] cs) {
	int i_241_ = a[i];
	switch (b[i_241_ - 1]) {
	case 3:
	    return new Integer(readInt(i_241_));
	case 4:
	    return new Float(Float.intBitsToFloat(readInt(i_241_)));
	case 5:
	    return new Long(readLong(i_241_));
	case 6:
	    return new Double(Double.longBitsToDouble(readLong(i_241_)));
	case 7:
	    return Type.getObjectType(readUTF8(i_241_, cs));
	case 8:
	    return readUTF8(i_241_, cs);
	case 16:
	    return Type.getMethodType(readUTF8(i_241_, cs));
	default: {
	    int i_242_ = readByte(i_241_);
	    int[] is = a;
	    int i_243_ = is[readUnsignedShort(i_241_ + 1)];
	    String string = readClass(i_243_, cs);
	    i_243_ = is[readUnsignedShort(i_243_ + 2)];
	    String string_244_ = readUTF8(i_243_, cs);
	    String string_245_ = readUTF8(i_243_ + 2, cs);
	    return new Handle(i_242_, string, string_244_, string_245_);
	}
	}
    }
}
