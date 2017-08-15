/* AnalyzerAdapter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.javosize.thirdparty.org.objectweb.asm.Handle;
import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.Opcodes;
import com.javosize.thirdparty.org.objectweb.asm.Type;

public class AnalyzerAdapter extends MethodVisitor
{
    public List locals;
    public List stack;
    private List labels;
    public Map uninitializedTypes;
    private int maxStack;
    private int maxLocals;
    private String owner;
    /*synthetic*/ static Class class$org$objectweb$asm$commons$AnalyzerAdapter
		      = (class$
			 ("com.javosize.thirdparty.org.objectweb.asm.commons.AnalyzerAdapter"));
    
    public AnalyzerAdapter(String string, int i, String string_0_,
			   String string_1_, MethodVisitor methodvisitor) {
	this(327680, string, i, string_0_, string_1_, methodvisitor);
	if (this.getClass() != class$org$objectweb$asm$commons$AnalyzerAdapter)
	    throw new IllegalStateException();
    }
    
    protected AnalyzerAdapter(int i, String string, int i_2_, String string_3_,
			      String string_4_, MethodVisitor methodvisitor) {
	super(i, methodvisitor);
	owner = string;
	locals = new ArrayList();
	stack = new ArrayList();
	uninitializedTypes = new HashMap();
	if ((i_2_ & 0x8) == 0) {
	    if ("<init>".equals(string_3_))
		locals.add(Opcodes.UNINITIALIZED_THIS);
	    else
		locals.add(string);
	}
	Type[] types = Type.getArgumentTypes(string_4_);
	for (int i_5_ = 0; i_5_ < types.length; i_5_++) {
	    Type type = types[i_5_];
	    switch (type.getSort()) {
	    case 1:
	    case 2:
	    case 3:
	    case 4:
	    case 5:
		locals.add(Opcodes.INTEGER);
		break;
	    case 6:
		locals.add(Opcodes.FLOAT);
		break;
	    case 7:
		locals.add(Opcodes.LONG);
		locals.add(Opcodes.TOP);
		break;
	    case 8:
		locals.add(Opcodes.DOUBLE);
		locals.add(Opcodes.TOP);
		break;
	    case 9:
		locals.add(types[i_5_].getDescriptor());
		break;
	    default:
		locals.add(types[i_5_].getInternalName());
	    }
	}
	maxLocals = locals.size();
    }
    
    public void visitFrame(int i, int i_6_, Object[] objects, int i_7_,
			   Object[] objects_8_) {
	if (i != -1)
	    throw new IllegalStateException
		      ("ClassReader.accept() should be called with EXPAND_FRAMES flag");
	if (mv != null)
	    mv.visitFrame(i, i_6_, objects, i_7_, objects_8_);
	if (locals != null) {
	    locals.clear();
	    stack.clear();
	} else {
	    locals = new ArrayList();
	    stack = new ArrayList();
	}
	visitFrameTypes(i_6_, objects, locals);
	visitFrameTypes(i_7_, objects_8_, stack);
	maxStack = Math.max(maxStack, stack.size());
    }
    
    private static void visitFrameTypes(int i, Object[] objects, List list) {
	for (int i_9_ = 0; i_9_ < i; i_9_++) {
	    Object object = objects[i_9_];
	    list.add(object);
	    if (object == Opcodes.LONG || object == Opcodes.DOUBLE)
		list.add(Opcodes.TOP);
	}
    }
    
    public void visitInsn(int i) {
	if (mv != null)
	    mv.visitInsn(i);
	execute(i, 0, null);
	if (i >= 172 && i <= 177 || i == 191) {
	    locals = null;
	    stack = null;
	}
    }
    
    public void visitIntInsn(int i, int i_10_) {
	if (mv != null)
	    mv.visitIntInsn(i, i_10_);
	execute(i, i_10_, null);
    }
    
    public void visitVarInsn(int i, int i_11_) {
	if (mv != null)
	    mv.visitVarInsn(i, i_11_);
	execute(i, i_11_, null);
    }
    
    public void visitTypeInsn(int i, String string) {
	if (i == 187) {
	    if (labels == null) {
		Label label = new Label();
		labels = new ArrayList(3);
		labels.add(label);
		if (mv != null)
		    mv.visitLabel(label);
	    }
	    for (int i_12_ = 0; i_12_ < labels.size(); i_12_++)
		uninitializedTypes.put(labels.get(i_12_), string);
	}
	if (mv != null)
	    mv.visitTypeInsn(i, string);
	execute(i, 0, string);
    }
    
    public void visitFieldInsn(int i, String string, String string_13_,
			       String string_14_) {
	if (mv != null)
	    mv.visitFieldInsn(i, string, string_13_, string_14_);
	execute(i, 0, string_14_);
    }
    
    /**
     * @deprecated
     */
    public void visitMethodInsn(int i, String string, String string_15_,
				String string_16_) {
	if (api >= 327680)
	    super.visitMethodInsn(i, string, string_15_, string_16_);
	else
	    doVisitMethodInsn(i, string, string_15_, string_16_, i == 185);
    }
    
    public void visitMethodInsn(int i, String string, String string_17_,
				String string_18_, boolean bool) {
	if (api < 327680)
	    super.visitMethodInsn(i, string, string_17_, string_18_, bool);
	else
	    doVisitMethodInsn(i, string, string_17_, string_18_, bool);
    }
    
    private void doVisitMethodInsn(int i, String string, String string_19_,
				   String string_20_, boolean bool) {
	if (mv != null)
	    mv.visitMethodInsn(i, string, string_19_, string_20_, bool);
	if (locals == null)
	    labels = null;
	else {
	    pop(string_20_);
	    if (i != 184) {
		Object object = pop();
		if (i == 183 && string_19_.charAt(0) == '<') {
		    Object object_21_;
		    if (object == Opcodes.UNINITIALIZED_THIS)
			object_21_ = owner;
		    else
			object_21_ = uninitializedTypes.get(object);
		    for (int i_22_ = 0; i_22_ < locals.size(); i_22_++) {
			if (locals.get(i_22_) == object)
			    locals.set(i_22_, object_21_);
		    }
		    for (int i_23_ = 0; i_23_ < stack.size(); i_23_++) {
			if (stack.get(i_23_) == object)
			    stack.set(i_23_, object_21_);
		    }
		}
	    }
	    pushDesc(string_20_);
	    labels = null;
	}
    }
    
    public transient void visitInvokeDynamicInsn
	(String string, String string_24_, Handle handle, Object[] objects) {
	if (mv != null)
	    mv.visitInvokeDynamicInsn(string, string_24_, handle, objects);
	if (locals == null)
	    labels = null;
	else {
	    pop(string_24_);
	    pushDesc(string_24_);
	    labels = null;
	}
    }
    
    public void visitJumpInsn(int i, Label label) {
	if (mv != null)
	    mv.visitJumpInsn(i, label);
	execute(i, 0, null);
	if (i == 167) {
	    locals = null;
	    stack = null;
	}
    }
    
    public void visitLabel(Label label) {
	if (mv != null)
	    mv.visitLabel(label);
	if (labels == null)
	    labels = new ArrayList(3);
	labels.add(label);
    }
    
    public void visitLdcInsn(Object object) {
	if (mv != null)
	    mv.visitLdcInsn(object);
	if (locals == null)
	    labels = null;
	else {
	    if (object instanceof Integer)
		push(Opcodes.INTEGER);
	    else if (object instanceof Long) {
		push(Opcodes.LONG);
		push(Opcodes.TOP);
	    } else if (object instanceof Float)
		push(Opcodes.FLOAT);
	    else if (object instanceof Double) {
		push(Opcodes.DOUBLE);
		push(Opcodes.TOP);
	    } else if (object instanceof String)
		push("java/lang/String");
	    else if (object instanceof Type) {
		int i = ((Type) object).getSort();
		if (i == 10 || i == 9)
		    push("java/lang/Class");
		else if (i == 11)
		    push("java/lang/invoke/MethodType");
		else
		    throw new IllegalArgumentException();
	    } else if (object instanceof Handle)
		push("java/lang/invoke/MethodHandle");
	    else
		throw new IllegalArgumentException();
	    labels = null;
	}
    }
    
    public void visitIincInsn(int i, int i_25_) {
	if (mv != null)
	    mv.visitIincInsn(i, i_25_);
	execute(132, i, null);
    }
    
    public transient void visitTableSwitchInsn(int i, int i_26_, Label label,
					       Label[] labels) {
	if (mv != null)
	    mv.visitTableSwitchInsn(i, i_26_, label, labels);
	execute(170, 0, null);
	locals = null;
	stack = null;
    }
    
    public void visitLookupSwitchInsn(Label label, int[] is, Label[] labels) {
	if (mv != null)
	    mv.visitLookupSwitchInsn(label, is, labels);
	execute(171, 0, null);
	locals = null;
	stack = null;
    }
    
    public void visitMultiANewArrayInsn(String string, int i) {
	if (mv != null)
	    mv.visitMultiANewArrayInsn(string, i);
	execute(197, i, string);
    }
    
    public void visitMaxs(int i, int i_27_) {
	if (mv != null) {
	    maxStack = Math.max(maxStack, i);
	    maxLocals = Math.max(maxLocals, i_27_);
	    mv.visitMaxs(maxStack, maxLocals);
	}
    }
    
    private Object get(int i) {
	maxLocals = Math.max(maxLocals, i + 1);
	return i < locals.size() ? (Object) locals.get(i) : Opcodes.TOP;
    }
    
    private void set(int i, Object object) {
	maxLocals = Math.max(maxLocals, i + 1);
	while (i >= locals.size())
	    locals.add(Opcodes.TOP);
	locals.set(i, object);
    }
    
    private void push(Object object) {
	stack.add(object);
	maxStack = Math.max(maxStack, stack.size());
    }
    
    private void pushDesc(String string) {
	int i = string.charAt(0) == '(' ? string.indexOf(')') + 1 : 0;
	switch (string.charAt(i)) {
	case 'V':
	    break;
	case 'B':
	case 'C':
	case 'I':
	case 'S':
	case 'Z':
	    push(Opcodes.INTEGER);
	    break;
	case 'F':
	    push(Opcodes.FLOAT);
	    break;
	case 'J':
	    push(Opcodes.LONG);
	    push(Opcodes.TOP);
	    break;
	case 'D':
	    push(Opcodes.DOUBLE);
	    push(Opcodes.TOP);
	    break;
	case '[':
	    if (i == 0)
		push(string);
	    else
		push(string.substring(i, string.length()));
	    break;
	default:
	    if (i == 0)
		push(string.substring(1, string.length() - 1));
	    else
		push(string.substring(i + 1, string.length() - 1));
	}
    }
    
    private Object pop() {
	return stack.remove(stack.size() - 1);
    }
    
    private void pop(int i) {
	int i_28_ = stack.size();
	int i_29_ = i_28_ - i;
	for (int i_30_ = i_28_ - 1; i_30_ >= i_29_; i_30_--)
	    stack.remove(i_30_);
    }
    
    private void pop(String string) {
	char c = string.charAt(0);
	if (c == '(') {
	    int i = 0;
	    Type[] types = Type.getArgumentTypes(string);
	    for (int i_31_ = 0; i_31_ < types.length; i_31_++)
		i += types[i_31_].getSize();
	    pop(i);
	} else if (c == 'J' || c == 'D')
	    pop(2);
	else
	    pop(1);
    }
    
    private void execute(int i, int i_32_, String string) {
	if (locals == null)
	    labels = null;
	else {
	    switch (i) {
	    case 0:
	    case 116:
	    case 117:
	    case 118:
	    case 119:
	    case 145:
	    case 146:
	    case 147:
	    case 167:
	    case 177:
		break;
	    case 1:
		push(Opcodes.NULL);
		break;
	    case 2:
	    case 3:
	    case 4:
	    case 5:
	    case 6:
	    case 7:
	    case 8:
	    case 16:
	    case 17:
		push(Opcodes.INTEGER);
		break;
	    case 9:
	    case 10:
		push(Opcodes.LONG);
		push(Opcodes.TOP);
		break;
	    case 11:
	    case 12:
	    case 13:
		push(Opcodes.FLOAT);
		break;
	    case 14:
	    case 15:
		push(Opcodes.DOUBLE);
		push(Opcodes.TOP);
		break;
	    case 21:
	    case 23:
	    case 25:
		push(get(i_32_));
		break;
	    case 22:
	    case 24:
		push(get(i_32_));
		push(Opcodes.TOP);
		break;
	    case 46:
	    case 51:
	    case 52:
	    case 53:
		pop(2);
		push(Opcodes.INTEGER);
		break;
	    case 47:
	    case 143:
		pop(2);
		push(Opcodes.LONG);
		push(Opcodes.TOP);
		break;
	    case 48:
		pop(2);
		push(Opcodes.FLOAT);
		break;
	    case 49:
	    case 138:
		pop(2);
		push(Opcodes.DOUBLE);
		push(Opcodes.TOP);
		break;
	    case 50: {
		pop(1);
		Object object = pop();
		if (object instanceof String)
		    pushDesc(((String) object).substring(1));
		else
		    push("java/lang/Object");
		break;
	    }
	    case 54:
	    case 56:
	    case 58: {
		Object object = pop();
		set(i_32_, object);
		if (i_32_ > 0) {
		    Object object_33_ = get(i_32_ - 1);
		    if (object_33_ == Opcodes.LONG
			|| object_33_ == Opcodes.DOUBLE)
			set(i_32_ - 1, Opcodes.TOP);
		}
		break;
	    }
	    case 55:
	    case 57: {
		pop(1);
		Object object = pop();
		set(i_32_, object);
		set(i_32_ + 1, Opcodes.TOP);
		if (i_32_ > 0) {
		    Object object_34_ = get(i_32_ - 1);
		    if (object_34_ == Opcodes.LONG
			|| object_34_ == Opcodes.DOUBLE)
			set(i_32_ - 1, Opcodes.TOP);
		}
		break;
	    }
	    case 79:
	    case 81:
	    case 83:
	    case 84:
	    case 85:
	    case 86:
		pop(3);
		break;
	    case 80:
	    case 82:
		pop(4);
		break;
	    case 87:
	    case 153:
	    case 154:
	    case 155:
	    case 156:
	    case 157:
	    case 158:
	    case 170:
	    case 171:
	    case 172:
	    case 174:
	    case 176:
	    case 191:
	    case 194:
	    case 195:
	    case 198:
	    case 199:
		pop(1);
		break;
	    case 88:
	    case 159:
	    case 160:
	    case 161:
	    case 162:
	    case 163:
	    case 164:
	    case 165:
	    case 166:
	    case 173:
	    case 175:
		pop(2);
		break;
	    case 89: {
		Object object = pop();
		push(object);
		push(object);
		break;
	    }
	    case 90: {
		Object object = pop();
		Object object_35_ = pop();
		push(object);
		push(object_35_);
		push(object);
		break;
	    }
	    case 91: {
		Object object = pop();
		Object object_36_ = pop();
		Object object_37_ = pop();
		push(object);
		push(object_37_);
		push(object_36_);
		push(object);
		break;
	    }
	    case 92: {
		Object object = pop();
		Object object_38_ = pop();
		push(object_38_);
		push(object);
		push(object_38_);
		push(object);
		break;
	    }
	    case 93: {
		Object object = pop();
		Object object_39_ = pop();
		Object object_40_ = pop();
		push(object_39_);
		push(object);
		push(object_40_);
		push(object_39_);
		push(object);
		break;
	    }
	    case 94: {
		Object object = pop();
		Object object_41_ = pop();
		Object object_42_ = pop();
		Object object_43_ = pop();
		push(object_41_);
		push(object);
		push(object_43_);
		push(object_42_);
		push(object_41_);
		push(object);
		break;
	    }
	    case 95: {
		Object object = pop();
		Object object_44_ = pop();
		push(object);
		push(object_44_);
		break;
	    }
	    case 96:
	    case 100:
	    case 104:
	    case 108:
	    case 112:
	    case 120:
	    case 122:
	    case 124:
	    case 126:
	    case 128:
	    case 130:
	    case 136:
	    case 142:
	    case 149:
	    case 150:
		pop(2);
		push(Opcodes.INTEGER);
		break;
	    case 97:
	    case 101:
	    case 105:
	    case 109:
	    case 113:
	    case 127:
	    case 129:
	    case 131:
		pop(4);
		push(Opcodes.LONG);
		push(Opcodes.TOP);
		break;
	    case 98:
	    case 102:
	    case 106:
	    case 110:
	    case 114:
	    case 137:
	    case 144:
		pop(2);
		push(Opcodes.FLOAT);
		break;
	    case 99:
	    case 103:
	    case 107:
	    case 111:
	    case 115:
		pop(4);
		push(Opcodes.DOUBLE);
		push(Opcodes.TOP);
		break;
	    case 121:
	    case 123:
	    case 125:
		pop(3);
		push(Opcodes.LONG);
		push(Opcodes.TOP);
		break;
	    case 132:
		set(i_32_, Opcodes.INTEGER);
		break;
	    case 133:
	    case 140:
		pop(1);
		push(Opcodes.LONG);
		push(Opcodes.TOP);
		break;
	    case 134:
		pop(1);
		push(Opcodes.FLOAT);
		break;
	    case 135:
	    case 141:
		pop(1);
		push(Opcodes.DOUBLE);
		push(Opcodes.TOP);
		break;
	    case 139:
	    case 190:
	    case 193:
		pop(1);
		push(Opcodes.INTEGER);
		break;
	    case 148:
	    case 151:
	    case 152:
		pop(4);
		push(Opcodes.INTEGER);
		break;
	    case 168:
	    case 169:
		throw new RuntimeException("JSR/RET are not supported");
	    case 178:
		pushDesc(string);
		break;
	    case 179:
		pop(string);
		break;
	    case 180:
		pop(1);
		pushDesc(string);
		break;
	    case 181:
		pop(string);
		pop();
		break;
	    case 187:
		push(labels.get(0));
		break;
	    case 188:
		pop();
		switch (i_32_) {
		case 4:
		    pushDesc("[Z");
		    break;
		case 5:
		    pushDesc("[C");
		    break;
		case 8:
		    pushDesc("[B");
		    break;
		case 9:
		    pushDesc("[S");
		    break;
		case 10:
		    pushDesc("[I");
		    break;
		case 6:
		    pushDesc("[F");
		    break;
		case 7:
		    pushDesc("[D");
		    break;
		default:
		    pushDesc("[J");
		}
		break;
	    case 189:
		pop();
		pushDesc("[" + Type.getObjectType(string));
		break;
	    case 192:
		pop();
		pushDesc(Type.getObjectType(string).getDescriptor());
		break;
	    default:
		pop(i_32_);
		pushDesc(string);
	    }
	    labels = null;
	}
    }
    
    /*synthetic*/ static Class class$(String string) {
	Class var_class;
	try {
	    var_class = Class.forName(string);
	} catch (ClassNotFoundException classnotfoundexception) {
	    String string_45_ = classnotfoundexception.getMessage();
	    throw new NoClassDefFoundError(string_45_);
	}
	return var_class;
    }
}
