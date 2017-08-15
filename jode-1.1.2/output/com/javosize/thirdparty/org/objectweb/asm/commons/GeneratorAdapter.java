/* GeneratorAdapter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.javosize.thirdparty.org.objectweb.asm.ClassVisitor;
import com.javosize.thirdparty.org.objectweb.asm.Handle;
import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.Type;

public class GeneratorAdapter extends LocalVariablesSorter
{
    private static final String CLDESC = "Ljava/lang/Class;";
    private static final Type BYTE_TYPE;
    private static final Type BOOLEAN_TYPE;
    private static final Type SHORT_TYPE;
    private static final Type CHARACTER_TYPE;
    private static final Type INTEGER_TYPE;
    private static final Type FLOAT_TYPE;
    private static final Type LONG_TYPE;
    private static final Type DOUBLE_TYPE;
    private static final Type NUMBER_TYPE;
    private static final Type OBJECT_TYPE;
    private static final Method BOOLEAN_VALUE;
    private static final Method CHAR_VALUE;
    private static final Method INT_VALUE;
    private static final Method FLOAT_VALUE;
    private static final Method LONG_VALUE;
    private static final Method DOUBLE_VALUE;
    public static final int ADD = 96;
    public static final int SUB = 100;
    public static final int MUL = 104;
    public static final int DIV = 108;
    public static final int REM = 112;
    public static final int NEG = 116;
    public static final int SHL = 120;
    public static final int SHR = 122;
    public static final int USHR = 124;
    public static final int AND = 126;
    public static final int OR = 128;
    public static final int XOR = 130;
    public static final int EQ = 153;
    public static final int NE = 154;
    public static final int LT = 155;
    public static final int GE = 156;
    public static final int GT = 157;
    public static final int LE = 158;
    private final int access;
    private final Type returnType;
    private final Type[] argumentTypes;
    private final List localTypes = new ArrayList();
    /*synthetic*/ static Class class$org$objectweb$asm$commons$GeneratorAdapter;
    
    public GeneratorAdapter(MethodVisitor methodvisitor, int i, String string,
			    String string_0_) {
	this(327680, methodvisitor, i, string, string_0_);
	if (this.getClass()
	    != class$org$objectweb$asm$commons$GeneratorAdapter)
	    throw new IllegalStateException();
    }
    
    protected GeneratorAdapter(int i, MethodVisitor methodvisitor, int i_1_,
			       String string, String string_2_) {
	super(i, i_1_, string_2_, methodvisitor);
	access = i_1_;
	returnType = Type.getReturnType(string_2_);
	argumentTypes = Type.getArgumentTypes(string_2_);
    }
    
    public GeneratorAdapter(int i, Method method,
			    MethodVisitor methodvisitor) {
	this(methodvisitor, i, null, method.getDescriptor());
    }
    
    public GeneratorAdapter(int i, Method method, String string, Type[] types,
			    ClassVisitor classvisitor) {
	this(i, method,
	     classvisitor.visitMethod(i, method.getName(),
				      method.getDescriptor(), string,
				      getInternalNames(types)));
    }
    
    private static String[] getInternalNames(Type[] types) {
	if (types == null)
	    return null;
	String[] strings = new String[types.length];
	for (int i = 0; i < strings.length; i++)
	    strings[i] = types[i].getInternalName();
	return strings;
    }
    
    public void push(boolean bool) {
	push(bool ? 1 : 0);
    }
    
    public void push(int i) {
	if (i >= -1 && i <= 5)
	    mv.visitInsn(3 + i);
	else if (i >= -128 && i <= 127)
	    mv.visitIntInsn(16, i);
	else if (i >= -32768 && i <= 32767)
	    mv.visitIntInsn(17, i);
	else
	    mv.visitLdcInsn(new Integer(i));
    }
    
    public void push(long l) {
	if (l == 0L || l == 1L)
	    mv.visitInsn(9 + (int) l);
	else
	    mv.visitLdcInsn(new Long(l));
    }
    
    public void push(float f) {
	int i = Float.floatToIntBits(f);
	if ((long) i == 0L || i == 1065353216 || i == 1073741824)
	    mv.visitInsn(11 + (int) f);
	else
	    mv.visitLdcInsn(new Float(f));
    }
    
    public void push(double d) {
	long l = Double.doubleToLongBits(d);
	if (l == 0L || l == 4607182418800017408L)
	    mv.visitInsn(14 + (int) d);
	else
	    mv.visitLdcInsn(new Double(d));
    }
    
    public void push(String string) {
	if (string == null)
	    mv.visitInsn(1);
	else
	    mv.visitLdcInsn(string);
    }
    
    public void push(Type type) {
	if (type == null)
	    mv.visitInsn(1);
	else {
	    switch (type.getSort()) {
	    case 1:
		mv.visitFieldInsn(178, "java/lang/Boolean", "TYPE",
				  "Ljava/lang/Class;");
		break;
	    case 2:
		mv.visitFieldInsn(178, "java/lang/Character", "TYPE",
				  "Ljava/lang/Class;");
		break;
	    case 3:
		mv.visitFieldInsn(178, "java/lang/Byte", "TYPE",
				  "Ljava/lang/Class;");
		break;
	    case 4:
		mv.visitFieldInsn(178, "java/lang/Short", "TYPE",
				  "Ljava/lang/Class;");
		break;
	    case 5:
		mv.visitFieldInsn(178, "java/lang/Integer", "TYPE",
				  "Ljava/lang/Class;");
		break;
	    case 6:
		mv.visitFieldInsn(178, "java/lang/Float", "TYPE",
				  "Ljava/lang/Class;");
		break;
	    case 7:
		mv.visitFieldInsn(178, "java/lang/Long", "TYPE",
				  "Ljava/lang/Class;");
		break;
	    case 8:
		mv.visitFieldInsn(178, "java/lang/Double", "TYPE",
				  "Ljava/lang/Class;");
		break;
	    default:
		mv.visitLdcInsn(type);
	    }
	}
    }
    
    public void push(Handle handle) {
	mv.visitLdcInsn(handle);
    }
    
    private int getArgIndex(int i) {
	int i_3_ = (access & 0x8) == 0 ? 1 : 0;
	for (int i_4_ = 0; i_4_ < i; i_4_++)
	    i_3_ += argumentTypes[i_4_].getSize();
	return i_3_;
    }
    
    private void loadInsn(Type type, int i) {
	mv.visitVarInsn(type.getOpcode(21), i);
    }
    
    private void storeInsn(Type type, int i) {
	mv.visitVarInsn(type.getOpcode(54), i);
    }
    
    public void loadThis() {
	if ((access & 0x8) != 0)
	    throw new IllegalStateException
		      ("no 'this' pointer within static method");
	mv.visitVarInsn(25, 0);
    }
    
    public void loadArg(int i) {
	loadInsn(argumentTypes[i], getArgIndex(i));
    }
    
    public void loadArgs(int i, int i_5_) {
	int i_6_ = getArgIndex(i);
	for (int i_7_ = 0; i_7_ < i_5_; i_7_++) {
	    Type type = argumentTypes[i + i_7_];
	    loadInsn(type, i_6_);
	    i_6_ += type.getSize();
	}
    }
    
    public void loadArgs() {
	loadArgs(0, argumentTypes.length);
    }
    
    public void loadArgArray() {
	push(argumentTypes.length);
	newArray(OBJECT_TYPE);
	for (int i = 0; i < argumentTypes.length; i++) {
	    dup();
	    push(i);
	    loadArg(i);
	    box(argumentTypes[i]);
	    arrayStore(OBJECT_TYPE);
	}
    }
    
    public void storeArg(int i) {
	storeInsn(argumentTypes[i], getArgIndex(i));
    }
    
    public Type getLocalType(int i) {
	return (Type) localTypes.get(i - firstLocal);
    }
    
    protected void setLocalType(int i, Type type) {
	int i_8_ = i - firstLocal;
	while (localTypes.size() < i_8_ + 1)
	    localTypes.add(null);
	localTypes.set(i_8_, type);
    }
    
    public void loadLocal(int i) {
	loadInsn(getLocalType(i), i);
    }
    
    public void loadLocal(int i, Type type) {
	setLocalType(i, type);
	loadInsn(type, i);
    }
    
    public void storeLocal(int i) {
	storeInsn(getLocalType(i), i);
    }
    
    public void storeLocal(int i, Type type) {
	setLocalType(i, type);
	storeInsn(type, i);
    }
    
    public void arrayLoad(Type type) {
	mv.visitInsn(type.getOpcode(46));
    }
    
    public void arrayStore(Type type) {
	mv.visitInsn(type.getOpcode(79));
    }
    
    public void pop() {
	mv.visitInsn(87);
    }
    
    public void pop2() {
	mv.visitInsn(88);
    }
    
    public void dup() {
	mv.visitInsn(89);
    }
    
    public void dup2() {
	mv.visitInsn(92);
    }
    
    public void dupX1() {
	mv.visitInsn(90);
    }
    
    public void dupX2() {
	mv.visitInsn(91);
    }
    
    public void dup2X1() {
	mv.visitInsn(93);
    }
    
    public void dup2X2() {
	mv.visitInsn(94);
    }
    
    public void swap() {
	mv.visitInsn(95);
    }
    
    public void swap(Type type, Type type_9_) {
	if (type_9_.getSize() == 1) {
	    if (type.getSize() == 1)
		swap();
	    else {
		dupX2();
		pop();
	    }
	} else if (type.getSize() == 1) {
	    dup2X1();
	    pop2();
	} else {
	    dup2X2();
	    pop2();
	}
    }
    
    public void math(int i, Type type) {
	mv.visitInsn(type.getOpcode(i));
    }
    
    public void not() {
	mv.visitInsn(4);
	mv.visitInsn(130);
    }
    
    public void iinc(int i, int i_10_) {
	mv.visitIincInsn(i, i_10_);
    }
    
    public void cast(Type type, Type type_11_) {
	if (type != type_11_) {
	    if (type == Type.DOUBLE_TYPE) {
		if (type_11_ == Type.FLOAT_TYPE)
		    mv.visitInsn(144);
		else if (type_11_ == Type.LONG_TYPE)
		    mv.visitInsn(143);
		else {
		    mv.visitInsn(142);
		    cast(Type.INT_TYPE, type_11_);
		}
	    } else if (type == Type.FLOAT_TYPE) {
		if (type_11_ == Type.DOUBLE_TYPE)
		    mv.visitInsn(141);
		else if (type_11_ == Type.LONG_TYPE)
		    mv.visitInsn(140);
		else {
		    mv.visitInsn(139);
		    cast(Type.INT_TYPE, type_11_);
		}
	    } else if (type == Type.LONG_TYPE) {
		if (type_11_ == Type.DOUBLE_TYPE)
		    mv.visitInsn(138);
		else if (type_11_ == Type.FLOAT_TYPE)
		    mv.visitInsn(137);
		else {
		    mv.visitInsn(136);
		    cast(Type.INT_TYPE, type_11_);
		}
	    } else if (type_11_ == Type.BYTE_TYPE)
		mv.visitInsn(145);
	    else if (type_11_ == Type.CHAR_TYPE)
		mv.visitInsn(146);
	    else if (type_11_ == Type.DOUBLE_TYPE)
		mv.visitInsn(135);
	    else if (type_11_ == Type.FLOAT_TYPE)
		mv.visitInsn(134);
	    else if (type_11_ == Type.LONG_TYPE)
		mv.visitInsn(133);
	    else if (type_11_ == Type.SHORT_TYPE)
		mv.visitInsn(147);
	}
    }
    
    private static Type getBoxedType(Type type) {
	switch (type.getSort()) {
	case 3:
	    return BYTE_TYPE;
	case 1:
	    return BOOLEAN_TYPE;
	case 4:
	    return SHORT_TYPE;
	case 2:
	    return CHARACTER_TYPE;
	case 5:
	    return INTEGER_TYPE;
	case 6:
	    return FLOAT_TYPE;
	case 7:
	    return LONG_TYPE;
	case 8:
	    return DOUBLE_TYPE;
	default:
	    return type;
	}
    }
    
    public void box(Type type) {
	if (type.getSort() != 10 && type.getSort() != 9) {
	    if (type == Type.VOID_TYPE)
		push((String) null);
	    else {
		Type type_12_ = getBoxedType(type);
		newInstance(type_12_);
		if (type.getSize() == 2) {
		    dupX2();
		    dupX2();
		    pop();
		} else {
		    dupX1();
		    swap();
		}
		invokeConstructor(type_12_,
				  new Method("<init>", Type.VOID_TYPE,
					     new Type[] { type }));
	    }
	}
    }
    
    public void valueOf(Type type) {
	if (type.getSort() != 10 && type.getSort() != 9) {
	    if (type == Type.VOID_TYPE)
		push((String) null);
	    else {
		Type type_13_ = getBoxedType(type);
		invokeStatic(type_13_, new Method("valueOf", type_13_,
						  new Type[] { type }));
	    }
	}
    }
    
    public void unbox(Type type) {
	Type type_14_ = NUMBER_TYPE;
	Method method = null;
	switch (type.getSort()) {
	case 0:
	    return;
	case 2:
	    type_14_ = CHARACTER_TYPE;
	    method = CHAR_VALUE;
	    break;
	case 1:
	    type_14_ = BOOLEAN_TYPE;
	    method = BOOLEAN_VALUE;
	    break;
	case 8:
	    method = DOUBLE_VALUE;
	    break;
	case 6:
	    method = FLOAT_VALUE;
	    break;
	case 7:
	    method = LONG_VALUE;
	    break;
	case 3:
	case 4:
	case 5:
	    method = INT_VALUE;
	    break;
	}
	if (method == null)
	    checkCast(type);
	else {
	    checkCast(type_14_);
	    invokeVirtual(type_14_, method);
	}
    }
    
    public Label newLabel() {
	return new Label();
    }
    
    public void mark(Label label) {
	mv.visitLabel(label);
    }
    
    public Label mark() {
	Label label = new Label();
	mv.visitLabel(label);
	return label;
    }
    
    public void ifCmp(Type type, int i, Label label) {
	switch (type.getSort()) {
	case 7:
	    mv.visitInsn(148);
	    break;
	case 8:
	    mv.visitInsn(i == 156 || i == 157 ? 151 : 152);
	    break;
	case 6:
	    mv.visitInsn(i == 156 || i == 157 ? 149 : 150);
	    break;
	case 9:
	case 10:
	    switch (i) {
	    case 153:
		mv.visitJumpInsn(165, label);
		return;
	    case 154:
		mv.visitJumpInsn(166, label);
		return;
	    default:
		throw new IllegalArgumentException("Bad comparison for type "
						   + type);
	    }
	default: {
	    int i_15_ = -1;
	    switch (i) {
	    case 153:
		i_15_ = 159;
		break;
	    case 154:
		i_15_ = 160;
		break;
	    case 156:
		i_15_ = 162;
		break;
	    case 155:
		i_15_ = 161;
		break;
	    case 158:
		i_15_ = 164;
		break;
	    case 157:
		i_15_ = 163;
		break;
	    }
	    mv.visitJumpInsn(i_15_, label);
	    return;
	}
	}
	mv.visitJumpInsn(i, label);
    }
    
    public void ifICmp(int i, Label label) {
	ifCmp(Type.INT_TYPE, i, label);
    }
    
    public void ifZCmp(int i, Label label) {
	mv.visitJumpInsn(i, label);
    }
    
    public void ifNull(Label label) {
	mv.visitJumpInsn(198, label);
    }
    
    public void ifNonNull(Label label) {
	mv.visitJumpInsn(199, label);
    }
    
    public void goTo(Label label) {
	mv.visitJumpInsn(167, label);
    }
    
    public void ret(int i) {
	mv.visitVarInsn(169, i);
    }
    
    public void tableSwitch(int[] is,
			    TableSwitchGenerator tableswitchgenerator) {
	float f;
	if (is.length == 0)
	    f = 0.0F;
	else
	    f = (float) is.length / (float) (is[is.length - 1] - is[0] + 1);
	tableSwitch(is, tableswitchgenerator, f >= 0.5F);
    }
    
    public void tableSwitch
	(int[] is, TableSwitchGenerator tableswitchgenerator, boolean bool) {
	for (int i = 1; i < is.length; i++) {
	    if (is[i] < is[i - 1])
		throw new IllegalArgumentException
			  ("keys must be sorted ascending");
	}
	Label label = newLabel();
	Label label_16_ = newLabel();
	if (is.length > 0) {
	    int i = is.length;
	    int i_17_ = is[0];
	    int i_18_ = is[i - 1];
	    int i_19_ = i_18_ - i_17_ + 1;
	    if (bool) {
		Label[] labels = new Label[i_19_];
		Arrays.fill(labels, label);
		for (int i_20_ = 0; i_20_ < i; i_20_++)
		    labels[is[i_20_] - i_17_] = newLabel();
		mv.visitTableSwitchInsn(i_17_, i_18_, label, labels);
		for (int i_21_ = 0; i_21_ < i_19_; i_21_++) {
		    Label label_22_ = labels[i_21_];
		    if (label_22_ != label) {
			mark(label_22_);
			tableswitchgenerator.generateCase(i_21_ + i_17_,
							  label_16_);
		    }
		}
	    } else {
		Label[] labels = new Label[i];
		for (int i_23_ = 0; i_23_ < i; i_23_++)
		    labels[i_23_] = newLabel();
		mv.visitLookupSwitchInsn(label, is, labels);
		for (int i_24_ = 0; i_24_ < i; i_24_++) {
		    mark(labels[i_24_]);
		    tableswitchgenerator.generateCase(is[i_24_], label_16_);
		}
	    }
	}
	mark(label);
	tableswitchgenerator.generateDefault();
	mark(label_16_);
    }
    
    public void returnValue() {
	mv.visitInsn(returnType.getOpcode(172));
    }
    
    private void fieldInsn(int i, Type type, String string, Type type_25_) {
	mv.visitFieldInsn(i, type.getInternalName(), string,
			  type_25_.getDescriptor());
    }
    
    public void getStatic(Type type, String string, Type type_26_) {
	fieldInsn(178, type, string, type_26_);
    }
    
    public void putStatic(Type type, String string, Type type_27_) {
	fieldInsn(179, type, string, type_27_);
    }
    
    public void getField(Type type, String string, Type type_28_) {
	fieldInsn(180, type, string, type_28_);
    }
    
    public void putField(Type type, String string, Type type_29_) {
	fieldInsn(181, type, string, type_29_);
    }
    
    private void invokeInsn(int i, Type type, Method method, boolean bool) {
	String string = (type.getSort() == 9 ? type.getDescriptor()
			 : type.getInternalName());
	mv.visitMethodInsn(i, string, method.getName(), method.getDescriptor(),
			   bool);
    }
    
    public void invokeVirtual(Type type, Method method) {
	invokeInsn(182, type, method, false);
    }
    
    public void invokeConstructor(Type type, Method method) {
	invokeInsn(183, type, method, false);
    }
    
    public void invokeStatic(Type type, Method method) {
	invokeInsn(184, type, method, false);
    }
    
    public void invokeInterface(Type type, Method method) {
	invokeInsn(185, type, method, true);
    }
    
    public transient void invokeDynamic(String string, String string_30_,
					Handle handle, Object[] objects) {
	mv.visitInvokeDynamicInsn(string, string_30_, handle, objects);
    }
    
    private void typeInsn(int i, Type type) {
	mv.visitTypeInsn(i, type.getInternalName());
    }
    
    public void newInstance(Type type) {
	typeInsn(187, type);
    }
    
    public void newArray(Type type) {
	int i;
	switch (type.getSort()) {
	case 1:
	    i = 4;
	    break;
	case 2:
	    i = 5;
	    break;
	case 3:
	    i = 8;
	    break;
	case 4:
	    i = 9;
	    break;
	case 5:
	    i = 10;
	    break;
	case 6:
	    i = 6;
	    break;
	case 7:
	    i = 11;
	    break;
	case 8:
	    i = 7;
	    break;
	default:
	    typeInsn(189, type);
	    return;
	}
	mv.visitIntInsn(188, i);
    }
    
    public void arrayLength() {
	mv.visitInsn(190);
    }
    
    public void throwException() {
	mv.visitInsn(191);
    }
    
    public void throwException(Type type, String string) {
	newInstance(type);
	dup();
	push(string);
	invokeConstructor(type, Method.getMethod("void <init> (String)"));
	throwException();
    }
    
    public void checkCast(Type type) {
	if (!type.equals(OBJECT_TYPE))
	    typeInsn(192, type);
    }
    
    public void instanceOf(Type type) {
	typeInsn(193, type);
    }
    
    public void monitorEnter() {
	mv.visitInsn(194);
    }
    
    public void monitorExit() {
	mv.visitInsn(195);
    }
    
    public void endMethod() {
	if ((access & 0x400) == 0)
	    mv.visitMaxs(0, 0);
	mv.visitEnd();
    }
    
    public void catchException(Label label, Label label_31_, Type type) {
	if (type == null)
	    mv.visitTryCatchBlock(label, label_31_, mark(), null);
	else
	    mv.visitTryCatchBlock(label, label_31_, mark(),
				  type.getInternalName());
    }
    
    static {
	_clinit_();
	BYTE_TYPE = Type.getObjectType("java/lang/Byte");
	BOOLEAN_TYPE = Type.getObjectType("java/lang/Boolean");
	SHORT_TYPE = Type.getObjectType("java/lang/Short");
	CHARACTER_TYPE = Type.getObjectType("java/lang/Character");
	INTEGER_TYPE = Type.getObjectType("java/lang/Integer");
	FLOAT_TYPE = Type.getObjectType("java/lang/Float");
	LONG_TYPE = Type.getObjectType("java/lang/Long");
	DOUBLE_TYPE = Type.getObjectType("java/lang/Double");
	NUMBER_TYPE = Type.getObjectType("java/lang/Number");
	OBJECT_TYPE = Type.getObjectType("java/lang/Object");
	BOOLEAN_VALUE = Method.getMethod("boolean booleanValue()");
	CHAR_VALUE = Method.getMethod("char charValue()");
	INT_VALUE = Method.getMethod("int intValue()");
	FLOAT_VALUE = Method.getMethod("float floatValue()");
	LONG_VALUE = Method.getMethod("long longValue()");
	DOUBLE_VALUE = Method.getMethod("double doubleValue()");
    }
    
    /*synthetic*/ static Class class$(String string) {
	Class var_class;
	try {
	    var_class = Class.forName(string);
	} catch (ClassNotFoundException classnotfoundexception) {
	    String string_32_ = classnotfoundexception.getMessage();
	    throw new NoClassDefFoundError(string_32_);
	}
	return var_class;
    }
    
    private static void _clinit_() {
	class$org$objectweb$asm$commons$GeneratorAdapter
	    = (class$
	       ("com.javosize.thirdparty.org.objectweb.asm.commons.GeneratorAdapter"));
    }
}
