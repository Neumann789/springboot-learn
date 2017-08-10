/* InstructionAdapter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;
import com.javosize.thirdparty.org.objectweb.asm.Handle;
import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.Type;

public class InstructionAdapter extends MethodVisitor
{
    public static final Type OBJECT_TYPE;
    /*synthetic*/ static Class class$org$objectweb$asm$commons$InstructionAdapter;
    
    public InstructionAdapter(MethodVisitor methodvisitor) {
	this(327680, methodvisitor);
	IF (this.getClass()
	    == class$org$objectweb$asm$commons$InstructionAdapter)
	    /* empty */
	throw new IllegalStateException();
    }
    
    protected InstructionAdapter(int i, MethodVisitor methodvisitor) {
	super(i, methodvisitor);
    }
    
    public void visitInsn(int i) {
	switch (i) {
	case 0:
	    nop();
	    break;
	case 1:
	    aconst(null);
	    break;
	case 2:
	case 3:
	case 4:
	case 5:
	case 6:
	case 7:
	case 8:
	    iconst(i - 3);
	    break;
	case 9:
	case 10:
	    lconst((long) (i - 9));
	    break;
	case 11:
	case 12:
	case 13:
	    fconst((float) (i - 11));
	    break;
	case 14:
	case 15:
	    dconst((double) (i - 14));
	    break;
	case 46:
	    aload(Type.INT_TYPE);
	    break;
	case 47:
	    aload(Type.LONG_TYPE);
	    break;
	case 48:
	    aload(Type.FLOAT_TYPE);
	    break;
	case 49:
	    aload(Type.DOUBLE_TYPE);
	    break;
	case 50:
	    aload(OBJECT_TYPE);
	    break;
	case 51:
	    aload(Type.BYTE_TYPE);
	    break;
	case 52:
	    aload(Type.CHAR_TYPE);
	    break;
	case 53:
	    aload(Type.SHORT_TYPE);
	    break;
	case 79:
	    astore(Type.INT_TYPE);
	    break;
	case 80:
	    astore(Type.LONG_TYPE);
	    break;
	case 81:
	    astore(Type.FLOAT_TYPE);
	    break;
	case 82:
	    astore(Type.DOUBLE_TYPE);
	    break;
	case 83:
	    astore(OBJECT_TYPE);
	    break;
	case 84:
	    astore(Type.BYTE_TYPE);
	    break;
	case 85:
	    astore(Type.CHAR_TYPE);
	    break;
	case 86:
	    astore(Type.SHORT_TYPE);
	    break;
	case 87:
	    pop();
	    break;
	case 88:
	    pop2();
	    break;
	case 89:
	    dup();
	    break;
	case 90:
	    dupX1();
	    break;
	case 91:
	    dupX2();
	    break;
	case 92:
	    dup2();
	    break;
	case 93:
	    dup2X1();
	    break;
	case 94:
	    dup2X2();
	    break;
	case 95:
	    swap();
	    break;
	case 96:
	    add(Type.INT_TYPE);
	    break;
	case 97:
	    add(Type.LONG_TYPE);
	    break;
	case 98:
	    add(Type.FLOAT_TYPE);
	    break;
	case 99:
	    add(Type.DOUBLE_TYPE);
	    break;
	case 100:
	    sub(Type.INT_TYPE);
	    break;
	case 101:
	    sub(Type.LONG_TYPE);
	    break;
	case 102:
	    sub(Type.FLOAT_TYPE);
	    break;
	case 103:
	    sub(Type.DOUBLE_TYPE);
	    break;
	case 104:
	    mul(Type.INT_TYPE);
	    break;
	case 105:
	    mul(Type.LONG_TYPE);
	    break;
	case 106:
	    mul(Type.FLOAT_TYPE);
	    break;
	case 107:
	    mul(Type.DOUBLE_TYPE);
	    break;
	case 108:
	    div(Type.INT_TYPE);
	    break;
	case 109:
	    div(Type.LONG_TYPE);
	    break;
	case 110:
	    div(Type.FLOAT_TYPE);
	    break;
	case 111:
	    div(Type.DOUBLE_TYPE);
	    break;
	case 112:
	    rem(Type.INT_TYPE);
	    break;
	case 113:
	    rem(Type.LONG_TYPE);
	    break;
	case 114:
	    rem(Type.FLOAT_TYPE);
	    break;
	case 115:
	    rem(Type.DOUBLE_TYPE);
	    break;
	case 116:
	    neg(Type.INT_TYPE);
	    break;
	case 117:
	    neg(Type.LONG_TYPE);
	    break;
	case 118:
	    neg(Type.FLOAT_TYPE);
	    break;
	case 119:
	    neg(Type.DOUBLE_TYPE);
	    break;
	case 120:
	    shl(Type.INT_TYPE);
	    break;
	case 121:
	    shl(Type.LONG_TYPE);
	    break;
	case 122:
	    shr(Type.INT_TYPE);
	    break;
	case 123:
	    shr(Type.LONG_TYPE);
	    break;
	case 124:
	    ushr(Type.INT_TYPE);
	    break;
	case 125:
	    ushr(Type.LONG_TYPE);
	    break;
	case 126:
	    and(Type.INT_TYPE);
	    break;
	case 127:
	    and(Type.LONG_TYPE);
	    break;
	case 128:
	    or(Type.INT_TYPE);
	    break;
	case 129:
	    or(Type.LONG_TYPE);
	    break;
	case 130:
	    xor(Type.INT_TYPE);
	    break;
	case 131:
	    xor(Type.LONG_TYPE);
	    break;
	case 133:
	    cast(Type.INT_TYPE, Type.LONG_TYPE);
	    break;
	case 134:
	    cast(Type.INT_TYPE, Type.FLOAT_TYPE);
	    break;
	case 135:
	    cast(Type.INT_TYPE, Type.DOUBLE_TYPE);
	    break;
	case 136:
	    cast(Type.LONG_TYPE, Type.INT_TYPE);
	    break;
	case 137:
	    cast(Type.LONG_TYPE, Type.FLOAT_TYPE);
	    break;
	case 138:
	    cast(Type.LONG_TYPE, Type.DOUBLE_TYPE);
	    break;
	case 139:
	    cast(Type.FLOAT_TYPE, Type.INT_TYPE);
	    break;
	case 140:
	    cast(Type.FLOAT_TYPE, Type.LONG_TYPE);
	    break;
	case 141:
	    cast(Type.FLOAT_TYPE, Type.DOUBLE_TYPE);
	    break;
	case 142:
	    cast(Type.DOUBLE_TYPE, Type.INT_TYPE);
	    break;
	case 143:
	    cast(Type.DOUBLE_TYPE, Type.LONG_TYPE);
	    break;
	case 144:
	    cast(Type.DOUBLE_TYPE, Type.FLOAT_TYPE);
	    break;
	case 145:
	    cast(Type.INT_TYPE, Type.BYTE_TYPE);
	    break;
	case 146:
	    cast(Type.INT_TYPE, Type.CHAR_TYPE);
	    break;
	case 147:
	    cast(Type.INT_TYPE, Type.SHORT_TYPE);
	    break;
	case 148:
	    lcmp();
	    break;
	case 149:
	    cmpl(Type.FLOAT_TYPE);
	    break;
	case 150:
	    cmpg(Type.FLOAT_TYPE);
	    break;
	case 151:
	    cmpl(Type.DOUBLE_TYPE);
	    break;
	case 152:
	    cmpg(Type.DOUBLE_TYPE);
	    break;
	case 172:
	    areturn(Type.INT_TYPE);
	    break;
	case 173:
	    areturn(Type.LONG_TYPE);
	    break;
	case 174:
	    areturn(Type.FLOAT_TYPE);
	    break;
	case 175:
	    areturn(Type.DOUBLE_TYPE);
	    break;
	case 176:
	    areturn(OBJECT_TYPE);
	    break;
	case 177:
	    areturn(Type.VOID_TYPE);
	    break;
	case 190:
	    arraylength();
	    break;
	case 191:
	    athrow();
	    break;
	case 194:
	    monitorenter();
	    break;
	case 195:
	    monitorexit();
	    break;
	default:
	    throw new IllegalArgumentException();
	}
    }
    
    public void visitIntInsn(int i, int i_0_) {
	switch (i) {
	case 16:
	    iconst(i_0_);
	    break;
	case 17:
	    iconst(i_0_);
	    break;
	case 188:
	    switch (i_0_) {
	    case 4:
		newarray(Type.BOOLEAN_TYPE);
		break;
	    case 5:
		newarray(Type.CHAR_TYPE);
		break;
	    case 8:
		newarray(Type.BYTE_TYPE);
		break;
	    case 9:
		newarray(Type.SHORT_TYPE);
		break;
	    case 10:
		newarray(Type.INT_TYPE);
		break;
	    case 6:
		newarray(Type.FLOAT_TYPE);
		break;
	    case 11:
		newarray(Type.LONG_TYPE);
		break;
	    case 7:
		newarray(Type.DOUBLE_TYPE);
		break;
	    default:
		throw new IllegalArgumentException();
	    }
	    break;
	default:
	    throw new IllegalArgumentException();
	}
    }
    
    public void visitVarInsn(int i, int i_1_) {
	switch (i) {
	case 21:
	    load(i_1_, Type.INT_TYPE);
	    break;
	case 22:
	    load(i_1_, Type.LONG_TYPE);
	    break;
	case 23:
	    load(i_1_, Type.FLOAT_TYPE);
	    break;
	case 24:
	    load(i_1_, Type.DOUBLE_TYPE);
	    break;
	case 25:
	    load(i_1_, OBJECT_TYPE);
	    break;
	case 54:
	    store(i_1_, Type.INT_TYPE);
	    break;
	case 55:
	    store(i_1_, Type.LONG_TYPE);
	    break;
	case 56:
	    store(i_1_, Type.FLOAT_TYPE);
	    break;
	case 57:
	    store(i_1_, Type.DOUBLE_TYPE);
	    break;
	case 58:
	    store(i_1_, OBJECT_TYPE);
	    break;
	case 169:
	    ret(i_1_);
	    break;
	default:
	    throw new IllegalArgumentException();
	}
    }
    
    public void visitTypeInsn(int i, String string) {
	Type type = Type.getObjectType(string);
	switch (i) {
	case 187:
	    anew(type);
	    break;
	case 189:
	    newarray(type);
	    break;
	case 192:
	    checkcast(type);
	    break;
	case 193:
	    instanceOf(type);
	    break;
	default:
	    throw new IllegalArgumentException();
	}
    }
    
    public void visitFieldInsn(int i, String string, String string_2_,
			       String string_3_) {
	switch (i) {
	case 178:
	    getstatic(string, string_2_, string_3_);
	    break;
	case 179:
	    putstatic(string, string_2_, string_3_);
	    break;
	case 180:
	    getfield(string, string_2_, string_3_);
	    break;
	case 181:
	    putfield(string, string_2_, string_3_);
	    break;
	default:
	    throw new IllegalArgumentException();
	}
    }
    
    /**
     * @deprecated
     */
    public void visitMethodInsn(int i, String string, String string_4_,
				String string_5_) {
    label_384:
	{
	    if (api < 327680) {
		PUSH this;
		PUSH i;
		PUSH string;
		PUSH string_4_;
		PUSH string_5_;
		if (i != 185)
		    PUSH false;
		else
		    PUSH true;
	    } else {
		super.visitMethodInsn(i, string, string_4_, string_5_);
		return;
	    }
	}
	((InstructionAdapter) POP).doVisitMethodInsn(POP, POP, POP, POP, POP);
	break label_384;
    }
    
    public void visitMethodInsn(int i, String string, String string_6_,
				String string_7_, boolean bool) {
	if (api >= 327680)
	    doVisitMethodInsn(i, string, string_6_, string_7_, bool);
	else
	    super.visitMethodInsn(i, string, string_6_, string_7_, bool);
	return;
    }
    
    private void doVisitMethodInsn(int i, String string, String string_8_,
				   String string_9_, boolean bool) {
	switch (i) {
	case 183:
	    invokespecial(string, string_8_, string_9_, bool);
	    break;
	case 182:
	    invokevirtual(string, string_8_, string_9_, bool);
	    break;
	case 184:
	    invokestatic(string, string_8_, string_9_, bool);
	    break;
	case 185:
	    invokeinterface(string, string_8_, string_9_);
	    break;
	default:
	    throw new IllegalArgumentException();
	}
    }
    
    public transient void visitInvokeDynamicInsn(String string,
						 String string_10_,
						 Handle handle,
						 Object[] objects) {
	invokedynamic(string, string_10_, handle, objects);
    }
    
    public void visitJumpInsn(int i, Label label) {
	switch (i) {
	case 153:
	    ifeq(label);
	    break;
	case 154:
	    ifne(label);
	    break;
	case 155:
	    iflt(label);
	    break;
	case 156:
	    ifge(label);
	    break;
	case 157:
	    ifgt(label);
	    break;
	case 158:
	    ifle(label);
	    break;
	case 159:
	    ificmpeq(label);
	    break;
	case 160:
	    ificmpne(label);
	    break;
	case 161:
	    ificmplt(label);
	    break;
	case 162:
	    ificmpge(label);
	    break;
	case 163:
	    ificmpgt(label);
	    break;
	case 164:
	    ificmple(label);
	    break;
	case 165:
	    ifacmpeq(label);
	    break;
	case 166:
	    ifacmpne(label);
	    break;
	case 167:
	    goTo(label);
	    break;
	case 168:
	    jsr(label);
	    break;
	case 198:
	    ifnull(label);
	    break;
	case 199:
	    ifnonnull(label);
	    break;
	default:
	    throw new IllegalArgumentException();
	}
    }
    
    public void visitLabel(Label label) {
	mark(label);
    }
    
    public void visitLdcInsn(Object object) {
    label_385:
	{
	    if (!(object instanceof Integer)) {
		if (!(object instanceof Byte)) {
		    if (!(object instanceof Character)) {
			if (!(object instanceof Short)) {
			    if (!(object instanceof Boolean)) {
				if (!(object instanceof Float)) {
				    if (!(object instanceof Long)) {
					if (!(object instanceof Double)) {
					    if (!(object instanceof String)) {
						if (!(object
						      instanceof Type)) {
						    if (!(object
							  instanceof Handle))
							throw new IllegalArgumentException
								  ();
						    hconst((Handle) object);
						} else
						    tconst((Type) object);
					    } else
						aconst(object);
					} else {
					    double d = ((Double) object)
							   .doubleValue();
					    dconst(d);
					}
				    } else {
					long l = ((Long) object).longValue();
					lconst(l);
				    }
				} else {
				    float f = ((Float) object).floatValue();
				    fconst(f);
				}
				return;
			    }
			    if (!((Boolean) object).booleanValue())
				PUSH false;
			    else
				PUSH true;
			} else {
			    int i = ((Short) object).intValue();
			    iconst(i);
			    return;
			}
		    } else {
			char c = ((Character) object).charValue();
			iconst(c);
			return;
		    }
		} else {
		    int i = ((Byte) object).intValue();
		    iconst(i);
		    return;
		}
	    } else {
		int i = ((Integer) object).intValue();
		iconst(i);
		return;
	    }
	}
	int i = POP;
	iconst(i);
	break label_385;
    }
    
    public void visitIincInsn(int i, int i_11_) {
	iinc(i, i_11_);
    }
    
    public transient void visitTableSwitchInsn(int i, int i_12_, Label label,
					       Label[] labels) {
	tableswitch(i, i_12_, label, labels);
    }
    
    public void visitLookupSwitchInsn(Label label, int[] is, Label[] labels) {
	lookupswitch(label, is, labels);
    }
    
    public void visitMultiANewArrayInsn(String string, int i) {
	multianewarray(string, i);
    }
    
    public void nop() {
	mv.visitInsn(0);
    }
    
    public void aconst(Object object) {
	if (object != null)
	    mv.visitLdcInsn(object);
	else
	    mv.visitInsn(1);
	return;
    }
    
    public void iconst(int i) {
	if (i < -1 || i > 5) {
	    if (i < -128 || i > 127) {
		if (i < -32768 || i > 32767)
		    mv.visitLdcInsn(new Integer(i));
		else
		    mv.visitIntInsn(17, i);
	    } else
		mv.visitIntInsn(16, i);
	} else
	    mv.visitInsn(3 + i);
	return;
    }
    
    public void lconst(long l) {
	if (l != 0L && l != 1L)
	    mv.visitLdcInsn(new Long(l));
	else
	    mv.visitInsn(9 + (int) l);
	return;
    }
    
    public void fconst(float f) {
	int i = Float.floatToIntBits(f);
	if ((long) i != 0L && i != 1065353216 && i != 1073741824)
	    mv.visitLdcInsn(new Float(f));
	else
	    mv.visitInsn(11 + (int) f);
	return;
    }
    
    public void dconst(double d) {
	long l = Double.doubleToLongBits(d);
	if (l != 0L && l != 4607182418800017408L)
	    mv.visitLdcInsn(new Double(d));
	else
	    mv.visitInsn(14 + (int) d);
	return;
    }
    
    public void tconst(Type type) {
	mv.visitLdcInsn(type);
    }
    
    public void hconst(Handle handle) {
	mv.visitLdcInsn(handle);
    }
    
    public void load(int i, Type type) {
	mv.visitVarInsn(type.getOpcode(21), i);
    }
    
    public void aload(Type type) {
	mv.visitInsn(type.getOpcode(46));
    }
    
    public void store(int i, Type type) {
	mv.visitVarInsn(type.getOpcode(54), i);
    }
    
    public void astore(Type type) {
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
    
    public void add(Type type) {
	mv.visitInsn(type.getOpcode(96));
    }
    
    public void sub(Type type) {
	mv.visitInsn(type.getOpcode(100));
    }
    
    public void mul(Type type) {
	mv.visitInsn(type.getOpcode(104));
    }
    
    public void div(Type type) {
	mv.visitInsn(type.getOpcode(108));
    }
    
    public void rem(Type type) {
	mv.visitInsn(type.getOpcode(112));
    }
    
    public void neg(Type type) {
	mv.visitInsn(type.getOpcode(116));
    }
    
    public void shl(Type type) {
	mv.visitInsn(type.getOpcode(120));
    }
    
    public void shr(Type type) {
	mv.visitInsn(type.getOpcode(122));
    }
    
    public void ushr(Type type) {
	mv.visitInsn(type.getOpcode(124));
    }
    
    public void and(Type type) {
	mv.visitInsn(type.getOpcode(126));
    }
    
    public void or(Type type) {
	mv.visitInsn(type.getOpcode(128));
    }
    
    public void xor(Type type) {
	mv.visitInsn(type.getOpcode(130));
    }
    
    public void iinc(int i, int i_13_) {
	mv.visitIincInsn(i, i_13_);
    }
    
    public void cast(Type type, Type type_14_) {
	if (type != type_14_) {
	    if (type != Type.DOUBLE_TYPE) {
		if (type != Type.FLOAT_TYPE) {
		    if (type != Type.LONG_TYPE) {
			if (type_14_ != Type.BYTE_TYPE) {
			    if (type_14_ != Type.CHAR_TYPE) {
				if (type_14_ != Type.DOUBLE_TYPE) {
				    if (type_14_ != Type.FLOAT_TYPE) {
					if (type_14_ != Type.LONG_TYPE) {
					    if (type_14_ == Type.SHORT_TYPE)
						mv.visitInsn(147);
					} else
					    mv.visitInsn(133);
				    } else
					mv.visitInsn(134);
				} else
				    mv.visitInsn(135);
			    } else
				mv.visitInsn(146);
			} else
			    mv.visitInsn(145);
		    } else if (type_14_ != Type.DOUBLE_TYPE) {
			if (type_14_ != Type.FLOAT_TYPE) {
			    mv.visitInsn(136);
			    cast(Type.INT_TYPE, type_14_);
			} else
			    mv.visitInsn(137);
		    } else
			mv.visitInsn(138);
		} else if (type_14_ != Type.DOUBLE_TYPE) {
		    if (type_14_ != Type.LONG_TYPE) {
			mv.visitInsn(139);
			cast(Type.INT_TYPE, type_14_);
		    } else
			mv.visitInsn(140);
		} else
		    mv.visitInsn(141);
	    } else if (type_14_ != Type.FLOAT_TYPE) {
		if (type_14_ != Type.LONG_TYPE) {
		    mv.visitInsn(142);
		    cast(Type.INT_TYPE, type_14_);
		} else
		    mv.visitInsn(143);
	    } else
		mv.visitInsn(144);
	}
	return;
    }
    
    public void lcmp() {
	mv.visitInsn(148);
    }
    
    public void cmpl(Type type) {
    label_386:
	{
	    PUSH mv;
	    if (type != Type.FLOAT_TYPE)
		PUSH 151;
	    else
		PUSH 149;
	    break label_386;
	}
	((MethodVisitor) POP).visitInsn(POP);
    }
    
    public void cmpg(Type type) {
    label_387:
	{
	    PUSH mv;
	    if (type != Type.FLOAT_TYPE)
		PUSH 152;
	    else
		PUSH 150;
	    break label_387;
	}
	((MethodVisitor) POP).visitInsn(POP);
    }
    
    public void ifeq(Label label) {
	mv.visitJumpInsn(153, label);
    }
    
    public void ifne(Label label) {
	mv.visitJumpInsn(154, label);
    }
    
    public void iflt(Label label) {
	mv.visitJumpInsn(155, label);
    }
    
    public void ifge(Label label) {
	mv.visitJumpInsn(156, label);
    }
    
    public void ifgt(Label label) {
	mv.visitJumpInsn(157, label);
    }
    
    public void ifle(Label label) {
	mv.visitJumpInsn(158, label);
    }
    
    public void ificmpeq(Label label) {
	mv.visitJumpInsn(159, label);
    }
    
    public void ificmpne(Label label) {
	mv.visitJumpInsn(160, label);
    }
    
    public void ificmplt(Label label) {
	mv.visitJumpInsn(161, label);
    }
    
    public void ificmpge(Label label) {
	mv.visitJumpInsn(162, label);
    }
    
    public void ificmpgt(Label label) {
	mv.visitJumpInsn(163, label);
    }
    
    public void ificmple(Label label) {
	mv.visitJumpInsn(164, label);
    }
    
    public void ifacmpeq(Label label) {
	mv.visitJumpInsn(165, label);
    }
    
    public void ifacmpne(Label label) {
	mv.visitJumpInsn(166, label);
    }
    
    public void goTo(Label label) {
	mv.visitJumpInsn(167, label);
    }
    
    public void jsr(Label label) {
	mv.visitJumpInsn(168, label);
    }
    
    public void ret(int i) {
	mv.visitVarInsn(169, i);
    }
    
    public transient void tableswitch(int i, int i_15_, Label label,
				      Label[] labels) {
	mv.visitTableSwitchInsn(i, i_15_, label, labels);
    }
    
    public void lookupswitch(Label label, int[] is, Label[] labels) {
	mv.visitLookupSwitchInsn(label, is, labels);
    }
    
    public void areturn(Type type) {
	mv.visitInsn(type.getOpcode(172));
    }
    
    public void getstatic(String string, String string_16_,
			  String string_17_) {
	mv.visitFieldInsn(178, string, string_16_, string_17_);
    }
    
    public void putstatic(String string, String string_18_,
			  String string_19_) {
	mv.visitFieldInsn(179, string, string_18_, string_19_);
    }
    
    public void getfield(String string, String string_20_, String string_21_) {
	mv.visitFieldInsn(180, string, string_20_, string_21_);
    }
    
    public void putfield(String string, String string_22_, String string_23_) {
	mv.visitFieldInsn(181, string, string_22_, string_23_);
    }
    
    /**
     * @deprecated
     */
    public void invokevirtual(String string, String string_24_,
			      String string_25_) {
	if (api < 327680)
	    mv.visitMethodInsn(182, string, string_24_, string_25_);
	else
	    invokevirtual(string, string_24_, string_25_, false);
	return;
    }
    
    public void invokevirtual(String string, String string_26_,
			      String string_27_, boolean bool) {
	if (api >= 327680)
	    mv.visitMethodInsn(182, string, string_26_, string_27_, bool);
	else if (!bool)
	    invokevirtual(string, string_26_, string_27_);
	else
	    throw new IllegalArgumentException
		      ("INVOKEVIRTUAL on interfaces require ASM 5");
	return;
    }
    
    /**
     * @deprecated
     */
    public void invokespecial(String string, String string_28_,
			      String string_29_) {
	if (api < 327680)
	    mv.visitMethodInsn(183, string, string_28_, string_29_, false);
	else
	    invokespecial(string, string_28_, string_29_, false);
	return;
    }
    
    public void invokespecial(String string, String string_30_,
			      String string_31_, boolean bool) {
	if (api >= 327680)
	    mv.visitMethodInsn(183, string, string_30_, string_31_, bool);
	else if (!bool)
	    invokespecial(string, string_30_, string_31_);
	else
	    throw new IllegalArgumentException
		      ("INVOKESPECIAL on interfaces require ASM 5");
	return;
    }
    
    /**
     * @deprecated
     */
    public void invokestatic(String string, String string_32_,
			     String string_33_) {
	if (api < 327680)
	    mv.visitMethodInsn(184, string, string_32_, string_33_, false);
	else
	    invokestatic(string, string_32_, string_33_, false);
	return;
    }
    
    public void invokestatic(String string, String string_34_,
			     String string_35_, boolean bool) {
	if (api >= 327680)
	    mv.visitMethodInsn(184, string, string_34_, string_35_, bool);
	else if (!bool)
	    invokestatic(string, string_34_, string_35_);
	else
	    throw new IllegalArgumentException
		      ("INVOKESTATIC on interfaces require ASM 5");
	return;
    }
    
    public void invokeinterface(String string, String string_36_,
				String string_37_) {
	mv.visitMethodInsn(185, string, string_36_, string_37_, true);
    }
    
    public void invokedynamic(String string, String string_38_, Handle handle,
			      Object[] objects) {
	mv.visitInvokeDynamicInsn(string, string_38_, handle, objects);
    }
    
    public void anew(Type type) {
	mv.visitTypeInsn(187, type.getInternalName());
    }
    
    public void newarray(Type type) {
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
	    mv.visitTypeInsn(189, type.getInternalName());
	    return;
	}
	mv.visitIntInsn(188, i);
    }
    
    public void arraylength() {
	mv.visitInsn(190);
    }
    
    public void athrow() {
	mv.visitInsn(191);
    }
    
    public void checkcast(Type type) {
	mv.visitTypeInsn(192, type.getInternalName());
    }
    
    public void instanceOf(Type type) {
	mv.visitTypeInsn(193, type.getInternalName());
    }
    
    public void monitorenter() {
	mv.visitInsn(194);
    }
    
    public void monitorexit() {
	mv.visitInsn(195);
    }
    
    public void multianewarray(String string, int i) {
	mv.visitMultiANewArrayInsn(string, i);
    }
    
    public void ifnull(Label label) {
	mv.visitJumpInsn(198, label);
    }
    
    public void ifnonnull(Label label) {
	mv.visitJumpInsn(199, label);
    }
    
    public void mark(Label label) {
	mv.visitLabel(label);
    }
    
    static {
	_clinit_();
	OBJECT_TYPE = Type.getType("Ljava/lang/Object;");
    }
    
    /*synthetic*/ static Class class$(String string) {
	try {
	    return Class.forName(string);
	} catch (ClassNotFoundException PUSH) {
	    String string_39_ = ((ClassNotFoundException) POP).getMessage();
	    throw new NoClassDefFoundError(string_39_);
	}
    }
    
    private static void _clinit_() {
	class$org$objectweb$asm$commons$InstructionAdapter
	    = (class$
	       ("com.javosize.thirdparty.org.objectweb.asm.commons.InstructionAdapter"));
    }
}
