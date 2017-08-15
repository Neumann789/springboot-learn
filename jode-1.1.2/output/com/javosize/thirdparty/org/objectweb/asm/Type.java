/* Type - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Type
{
    public static final int VOID = 0;
    public static final int BOOLEAN = 1;
    public static final int CHAR = 2;
    public static final int BYTE = 3;
    public static final int SHORT = 4;
    public static final int INT = 5;
    public static final int FLOAT = 6;
    public static final int LONG = 7;
    public static final int DOUBLE = 8;
    public static final int ARRAY = 9;
    public static final int OBJECT = 10;
    public static final int METHOD = 11;
    public static final Type VOID_TYPE;
    public static final Type BOOLEAN_TYPE;
    public static final Type CHAR_TYPE;
    public static final Type BYTE_TYPE;
    public static final Type SHORT_TYPE;
    public static final Type INT_TYPE;
    public static final Type FLOAT_TYPE;
    public static final Type LONG_TYPE;
    public static final Type DOUBLE_TYPE;
    private final int a;
    private final char[] b;
    private final int c;
    private final int d;
    
    private Type(int i, char[] cs, int i_0_, int i_1_) {
	a = i;
	b = cs;
	c = i_0_;
	d = i_1_;
    }
    
    public static Type getType(String string) {
	return a(string.toCharArray(), 0);
    }
    
    public static Type getObjectType(String string) {
	char[] cs = string.toCharArray();
	return new Type(cs[0] == '[' ? 9 : 10, cs, 0, cs.length);
    }
    
    public static Type getMethodType(String string) {
	return a(string.toCharArray(), 0);
    }
    
    public static transient Type getMethodType(Type type, Type[] types) {
	return getType(getMethodDescriptor(type, types));
    }
    
    public static Type getType(Class var_class) {
	if (var_class.isPrimitive()) {
	    if (var_class == Integer.TYPE)
		return INT_TYPE;
	    if (var_class == Void.TYPE)
		return VOID_TYPE;
	    if (var_class == Boolean.TYPE)
		return BOOLEAN_TYPE;
	    if (var_class == Byte.TYPE)
		return BYTE_TYPE;
	    if (var_class == Character.TYPE)
		return CHAR_TYPE;
	    if (var_class == Short.TYPE)
		return SHORT_TYPE;
	    if (var_class == Double.TYPE)
		return DOUBLE_TYPE;
	    if (var_class == Float.TYPE)
		return FLOAT_TYPE;
	    return LONG_TYPE;
	}
	return getType(getDescriptor(var_class));
    }
    
    public static Type getType(Constructor constructor) {
	return getType(getConstructorDescriptor(constructor));
    }
    
    public static Type getType(Method method) {
	return getType(getMethodDescriptor(method));
    }
    
    public static Type[] getArgumentTypes(String string) {
	char[] cs = string.toCharArray();
	int i = 1;
	int i_2_ = 0;
	for (;;) {
	    char c = cs[i++];
	    if (c == ')')
		break;
	    if (c == 'L') {
		while (cs[i++] != ';') {
		    /* empty */
		}
		i_2_++;
	    } else if (c != '[')
		i_2_++;
	}
	Type[] types = new Type[i_2_];
	i = 1;
	i_2_ = 0;
	while (cs[i] != ')') {
	    types[i_2_] = a(cs, i);
	    i = i + (types[i_2_].d + (types[i_2_].a == 10 ? 2 : 0));
	    i_2_++;
	}
	return types;
    }
    
    public static Type[] getArgumentTypes(Method method) {
	Class[] var_classes = method.getParameterTypes();
	Type[] types = new Type[var_classes.length];
	for (int i = var_classes.length - 1; i >= 0; i--)
	    types[i] = getType(var_classes[i]);
	return types;
    }
    
    public static Type getReturnType(String string) {
	char[] cs = string.toCharArray();
	return a(cs, string.indexOf(')') + 1);
    }
    
    public static Type getReturnType(Method method) {
	return getType(method.getReturnType());
    }
    
    public static int getArgumentsAndReturnSizes(String string) {
	int i = 1;
	int i_3_ = 1;
	for (;;) {
	    char c = string.charAt(i_3_++);
	    if (c == ')') {
		c = string.charAt(i_3_);
		return i << 2 | (c == 'V' ? 0 : c == 'D' || c == 'J' ? 2 : 1);
	    }
	    if (c == 'L') {
		while (string.charAt(i_3_++) != ';') {
		    /* empty */
		}
		i++;
	    } else if (c == '[') {
		for (/**/; (c = string.charAt(i_3_)) == '['; i_3_++) {
		    /* empty */
		}
		if (c == 'D' || c == 'J')
		    i--;
	    } else if (c == 'D' || c == 'J')
		i += 2;
	    else
		i++;
	}
    }
    
    private static Type a(char[] cs, int i) {
	switch (cs[i]) {
	case 'V':
	    return VOID_TYPE;
	case 'Z':
	    return BOOLEAN_TYPE;
	case 'C':
	    return CHAR_TYPE;
	case 'B':
	    return BYTE_TYPE;
	case 'S':
	    return SHORT_TYPE;
	case 'I':
	    return INT_TYPE;
	case 'F':
	    return FLOAT_TYPE;
	case 'J':
	    return LONG_TYPE;
	case 'D':
	    return DOUBLE_TYPE;
	case '[': {
	    int i_4_;
	    for (i_4_ = 1; cs[i + i_4_] == '['; i_4_++) {
		/* empty */
	    }
	    if (cs[i + i_4_] == 'L') {
		for (i_4_++; cs[i + i_4_] != ';'; i_4_++) {
		    /* empty */
		}
	    }
	    return new Type(9, cs, i, i_4_ + 1);
	}
	case 'L': {
	    int i_5_;
	    for (i_5_ = 1; cs[i + i_5_] != ';'; i_5_++) {
		/* empty */
	    }
	    return new Type(10, cs, i + 1, i_5_ - 1);
	}
	default:
	    return new Type(11, cs, i, cs.length - i);
	}
    }
    
    public int getSort() {
	return a;
    }
    
    public int getDimensions() {
	int i;
	for (i = 1; b[c + i] == '['; i++) {
	    /* empty */
	}
	return i;
    }
    
    public Type getElementType() {
	return a(b, c + getDimensions());
    }
    
    public String getClassName() {
	switch (a) {
	case 0:
	    return "void";
	case 1:
	    return "boolean";
	case 2:
	    return "char";
	case 3:
	    return "byte";
	case 4:
	    return "short";
	case 5:
	    return "int";
	case 6:
	    return "float";
	case 7:
	    return "long";
	case 8:
	    return "double";
	case 9: {
	    StringBuffer stringbuffer
		= new StringBuffer(getElementType().getClassName());
	    for (int i = getDimensions(); i > 0; i--)
		stringbuffer.append("[]");
	    return stringbuffer.toString();
	}
	case 10:
	    return new String(b, c, d).replace('/', '.');
	default:
	    return null;
	}
    }
    
    public String getInternalName() {
	return new String(b, c, d);
    }
    
    public Type[] getArgumentTypes() {
	return getArgumentTypes(getDescriptor());
    }
    
    public Type getReturnType() {
	return getReturnType(getDescriptor());
    }
    
    public int getArgumentsAndReturnSizes() {
	return getArgumentsAndReturnSizes(getDescriptor());
    }
    
    public String getDescriptor() {
	StringBuffer stringbuffer = new StringBuffer();
	a(stringbuffer);
	return stringbuffer.toString();
    }
    
    public static transient String getMethodDescriptor(Type type,
						       Type[] types) {
	StringBuffer stringbuffer = new StringBuffer();
	stringbuffer.append('(');
	for (int i = 0; i < types.length; i++)
	    types[i].a(stringbuffer);
	stringbuffer.append(')');
	type.a(stringbuffer);
	return stringbuffer.toString();
    }
    
    private void a(StringBuffer stringbuffer) {
	if (b == null)
	    stringbuffer.append((char) ((c & ~0xffffff) >>> 24));
	else if (a == 10) {
	    stringbuffer.append('L');
	    stringbuffer.append(b, c, d);
	    stringbuffer.append(';');
	} else
	    stringbuffer.append(b, c, d);
    }
    
    public static String getInternalName(Class var_class) {
	return var_class.getName().replace('.', '/');
    }
    
    public static String getDescriptor(Class var_class) {
	StringBuffer stringbuffer = new StringBuffer();
	a(stringbuffer, var_class);
	return stringbuffer.toString();
    }
    
    public static String getConstructorDescriptor(Constructor constructor) {
	Class[] var_classes = constructor.getParameterTypes();
	StringBuffer stringbuffer = new StringBuffer();
	stringbuffer.append('(');
	for (int i = 0; i < var_classes.length; i++)
	    a(stringbuffer, var_classes[i]);
	return stringbuffer.append(")V").toString();
    }
    
    public static String getMethodDescriptor(Method method) {
	Class[] var_classes = method.getParameterTypes();
	StringBuffer stringbuffer = new StringBuffer();
	stringbuffer.append('(');
	for (int i = 0; i < var_classes.length; i++)
	    a(stringbuffer, var_classes[i]);
	stringbuffer.append(')');
	a(stringbuffer, method.getReturnType());
	return stringbuffer.toString();
    }
    
    private static void a(StringBuffer stringbuffer, Class var_class) {
	Class var_class_6_ = var_class;
	for (;;) {
	    if (var_class_6_.isPrimitive()) {
		char c;
		if (var_class_6_ == Integer.TYPE)
		    c = 'I';
		else if (var_class_6_ == Void.TYPE)
		    c = 'V';
		else if (var_class_6_ == Boolean.TYPE)
		    c = 'Z';
		else if (var_class_6_ == Byte.TYPE)
		    c = 'B';
		else if (var_class_6_ == Character.TYPE)
		    c = 'C';
		else if (var_class_6_ == Short.TYPE)
		    c = 'S';
		else if (var_class_6_ == Double.TYPE)
		    c = 'D';
		else if (var_class_6_ == Float.TYPE)
		    c = 'F';
		else
		    c = 'J';
		stringbuffer.append(c);
		return;
	    }
	    if (!var_class_6_.isArray())
		break;
	    stringbuffer.append('[');
	    var_class_6_ = var_class_6_.getComponentType();
	}
	stringbuffer.append('L');
	String string = var_class_6_.getName();
	int i = string.length();
	for (int i_7_ = 0; i_7_ < i; i_7_++) {
	    char c = string.charAt(i_7_);
	    stringbuffer.append(c == '.' ? '/' : c);
	}
	stringbuffer.append(';');
    }
    
    public int getSize() {
	return b == null ? c & 0xff : 1;
    }
    
    public int getOpcode(int i) {
	if (i == 46 || i == 79)
	    return i + (b == null ? (c & 0xff00) >> 8 : 4);
	return i + (b == null ? (c & 0xff0000) >> 16 : 4);
    }
    
    public boolean equals(Object object) {
	if (this == object)
	    return true;
	if (!(object instanceof Type))
	    return false;
	Type type_8_ = (Type) object;
	if (a != type_8_.a)
	    return false;
	if (a >= 9) {
	    if (d != type_8_.d)
		return false;
	    int i = c;
	    int i_9_ = type_8_.c;
	    int i_10_ = i + d;
	    while (i < i_10_) {
		if (b[i] != type_8_.b[i_9_])
		    return false;
		i++;
		i_9_++;
	    }
	}
	return true;
    }
    
    public int hashCode() {
	int i = 13 * a;
	if (a >= 9) {
	    int i_11_ = c;
	    for (int i_12_ = i_11_ + d; i_11_ < i_12_; i_11_++)
		i = 17 * (i + b[i_11_]);
	}
	return i;
    }
    
    public String toString() {
	return getDescriptor();
    }
    
    static {
	_clinit_();
	VOID_TYPE = new Type(0, null, 1443168256, 1);
	BOOLEAN_TYPE = new Type(1, null, 1509950721, 1);
	CHAR_TYPE = new Type(2, null, 1124075009, 1);
	BYTE_TYPE = new Type(3, null, 1107297537, 1);
	SHORT_TYPE = new Type(4, null, 1392510721, 1);
	INT_TYPE = new Type(5, null, 1224736769, 1);
	FLOAT_TYPE = new Type(6, null, 1174536705, 1);
	LONG_TYPE = new Type(7, null, 1241579778, 1);
	DOUBLE_TYPE = new Type(8, null, 1141048066, 1);
    }
    
    /*synthetic*/ static void _clinit_() {
	/* empty */
    }
}
