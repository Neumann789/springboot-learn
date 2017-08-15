/* AdviceAdapter - Decompiled by JODE
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

public abstract class AdviceAdapter extends GeneratorAdapter implements Opcodes
{
    private static final Object THIS;
    private static final Object OTHER;
    protected int methodAccess;
    protected String methodDesc;
    private boolean constructor;
    private boolean superInitialized;
    private List stackFrame;
    private Map branches;
    
    protected AdviceAdapter(int i, MethodVisitor methodvisitor, int i_0_,
			    String string, String string_1_) {
	super(i, methodvisitor, i_0_, string, string_1_);
	methodAccess = i_0_;
	methodDesc = string_1_;
	constructor = "<init>".equals(string);
    }
    
    public void visitCode() {
	mv.visitCode();
	if (constructor) {
	    stackFrame = new ArrayList();
	    branches = new HashMap();
	} else {
	    superInitialized = true;
	    onMethodEnter();
	}
    }
    
    public void visitLabel(Label label) {
	mv.visitLabel(label);
	if (constructor && branches != null) {
	    List list = (List) branches.get(label);
	    if (list != null) {
		stackFrame = list;
		branches.remove(label);
	    }
	}
    }
    
    public void visitInsn(int i) {
	if (constructor) {
	    switch (i) {
	    case 177:
		onMethodExit(i);
		break;
	    case 172:
	    case 174:
	    case 176:
	    case 191:
		popValue();
		onMethodExit(i);
		break;
	    case 173:
	    case 175:
		popValue();
		popValue();
		onMethodExit(i);
		break;
	    case 0:
	    case 47:
	    case 49:
	    case 116:
	    case 117:
	    case 118:
	    case 119:
	    case 134:
	    case 138:
	    case 139:
	    case 143:
	    case 145:
	    case 146:
	    case 147:
	    case 190:
		break;
	    case 1:
	    case 2:
	    case 3:
	    case 4:
	    case 5:
	    case 6:
	    case 7:
	    case 8:
	    case 11:
	    case 12:
	    case 13:
	    case 133:
	    case 135:
	    case 140:
	    case 141:
		pushValue(OTHER);
		break;
	    case 9:
	    case 10:
	    case 14:
	    case 15:
		pushValue(OTHER);
		pushValue(OTHER);
		break;
	    case 46:
	    case 48:
	    case 50:
	    case 51:
	    case 52:
	    case 53:
	    case 87:
	    case 96:
	    case 98:
	    case 100:
	    case 102:
	    case 104:
	    case 106:
	    case 108:
	    case 110:
	    case 112:
	    case 114:
	    case 120:
	    case 121:
	    case 122:
	    case 123:
	    case 124:
	    case 125:
	    case 126:
	    case 128:
	    case 130:
	    case 136:
	    case 137:
	    case 142:
	    case 144:
	    case 149:
	    case 150:
	    case 194:
	    case 195:
		popValue();
		break;
	    case 88:
	    case 97:
	    case 99:
	    case 101:
	    case 103:
	    case 105:
	    case 107:
	    case 109:
	    case 111:
	    case 113:
	    case 115:
	    case 127:
	    case 129:
	    case 131:
		popValue();
		popValue();
		break;
	    case 79:
	    case 81:
	    case 83:
	    case 84:
	    case 85:
	    case 86:
	    case 148:
	    case 151:
	    case 152:
		popValue();
		popValue();
		popValue();
		break;
	    case 80:
	    case 82:
		popValue();
		popValue();
		popValue();
		popValue();
		break;
	    case 89:
		pushValue(peekValue());
		break;
	    case 90: {
		int i_2_ = stackFrame.size();
		stackFrame.add(i_2_ - 2, stackFrame.get(i_2_ - 1));
		break;
	    }
	    case 91: {
		int i_3_ = stackFrame.size();
		stackFrame.add(i_3_ - 3, stackFrame.get(i_3_ - 1));
		break;
	    }
	    case 92: {
		int i_4_ = stackFrame.size();
		stackFrame.add(i_4_ - 2, stackFrame.get(i_4_ - 1));
		stackFrame.add(i_4_ - 2, stackFrame.get(i_4_ - 1));
		break;
	    }
	    case 93: {
		int i_5_ = stackFrame.size();
		stackFrame.add(i_5_ - 3, stackFrame.get(i_5_ - 1));
		stackFrame.add(i_5_ - 3, stackFrame.get(i_5_ - 1));
		break;
	    }
	    case 94: {
		int i_6_ = stackFrame.size();
		stackFrame.add(i_6_ - 4, stackFrame.get(i_6_ - 1));
		stackFrame.add(i_6_ - 4, stackFrame.get(i_6_ - 1));
		break;
	    }
	    case 95: {
		int i_7_ = stackFrame.size();
		stackFrame.add(i_7_ - 2, stackFrame.get(i_7_ - 1));
		stackFrame.remove(i_7_);
		break;
	    }
	    }
	} else {
	    switch (i) {
	    case 172:
	    case 173:
	    case 174:
	    case 175:
	    case 176:
	    case 177:
	    case 191:
		onMethodExit(i);
		break;
	    }
	}
	mv.visitInsn(i);
    }
    
    public void visitVarInsn(int i, int i_8_) {
	super.visitVarInsn(i, i_8_);
	if (constructor) {
	    switch (i) {
	    case 21:
	    case 23:
		pushValue(OTHER);
		break;
	    case 22:
	    case 24:
		pushValue(OTHER);
		pushValue(OTHER);
		break;
	    case 25:
		pushValue(i_8_ == 0 ? THIS : OTHER);
		break;
	    case 54:
	    case 56:
	    case 58:
		popValue();
		break;
	    case 55:
	    case 57:
		popValue();
		popValue();
		break;
	    }
	}
    }
    
    public void visitFieldInsn(int i, String string, String string_9_,
			       String string_10_) {
	mv.visitFieldInsn(i, string, string_9_, string_10_);
	if (constructor) {
	    char c = string_10_.charAt(0);
	    boolean bool = c == 'J' || c == 'D';
	    switch (i) {
	    case 178:
		pushValue(OTHER);
		if (bool)
		    pushValue(OTHER);
		break;
	    case 179:
		popValue();
		if (bool)
		    popValue();
		break;
	    case 181:
		popValue();
		if (bool) {
		    popValue();
		    popValue();
		}
		break;
	    default:
		if (bool)
		    pushValue(OTHER);
	    }
	}
    }
    
    public void visitIntInsn(int i, int i_11_) {
	mv.visitIntInsn(i, i_11_);
	if (constructor && i != 188)
	    pushValue(OTHER);
    }
    
    public void visitLdcInsn(Object object) {
	mv.visitLdcInsn(object);
	if (constructor) {
	    pushValue(OTHER);
	    if (object instanceof Double || object instanceof Long)
		pushValue(OTHER);
	}
    }
    
    public void visitMultiANewArrayInsn(String string, int i) {
	mv.visitMultiANewArrayInsn(string, i);
	if (constructor) {
	    for (int i_12_ = 0; i_12_ < i; i_12_++)
		popValue();
	    pushValue(OTHER);
	}
    }
    
    public void visitTypeInsn(int i, String string) {
	mv.visitTypeInsn(i, string);
	if (constructor && i == 187)
	    pushValue(OTHER);
    }
    
    /**
     * @deprecated
     */
    public void visitMethodInsn(int i, String string, String string_13_,
				String string_14_) {
	if (api >= 327680)
	    super.visitMethodInsn(i, string, string_13_, string_14_);
	else
	    doVisitMethodInsn(i, string, string_13_, string_14_, i == 185);
    }
    
    public void visitMethodInsn(int i, String string, String string_15_,
				String string_16_, boolean bool) {
	if (api < 327680)
	    super.visitMethodInsn(i, string, string_15_, string_16_, bool);
	else
	    doVisitMethodInsn(i, string, string_15_, string_16_, bool);
    }
    
    private void doVisitMethodInsn(int i, String string, String string_17_,
				   String string_18_, boolean bool) {
	mv.visitMethodInsn(i, string, string_17_, string_18_, bool);
	if (constructor) {
	    Type[] types = Type.getArgumentTypes(string_18_);
	    for (int i_19_ = 0; i_19_ < types.length; i_19_++) {
		popValue();
		if (types[i_19_].getSize() == 2)
		    popValue();
	    }
	    switch (i) {
	    case 182:
	    case 185:
		popValue();
		break;
	    case 183: {
		Object object = popValue();
		if (object == THIS && !superInitialized) {
		    onMethodEnter();
		    superInitialized = true;
		    constructor = false;
		}
		break;
	    }
	    }
	    Type type = Type.getReturnType(string_18_);
	    if (type != Type.VOID_TYPE) {
		pushValue(OTHER);
		if (type.getSize() == 2)
		    pushValue(OTHER);
	    }
	}
    }
    
    public transient void visitInvokeDynamicInsn
	(String string, String string_20_, Handle handle, Object[] objects) {
	mv.visitInvokeDynamicInsn(string, string_20_, handle, objects);
	if (constructor) {
	    Type[] types = Type.getArgumentTypes(string_20_);
	    for (int i = 0; i < types.length; i++) {
		popValue();
		if (types[i].getSize() == 2)
		    popValue();
	    }
	    Type type = Type.getReturnType(string_20_);
	    if (type != Type.VOID_TYPE) {
		pushValue(OTHER);
		if (type.getSize() == 2)
		    pushValue(OTHER);
	    }
	}
    }
    
    public void visitJumpInsn(int i, Label label) {
	mv.visitJumpInsn(i, label);
	if (constructor) {
	    switch (i) {
	    case 153:
	    case 154:
	    case 155:
	    case 156:
	    case 157:
	    case 158:
	    case 198:
	    case 199:
		popValue();
		break;
	    case 159:
	    case 160:
	    case 161:
	    case 162:
	    case 163:
	    case 164:
	    case 165:
	    case 166:
		popValue();
		popValue();
		break;
	    case 168:
		pushValue(OTHER);
		break;
	    }
	    addBranch(label);
	}
    }
    
    public void visitLookupSwitchInsn(Label label, int[] is, Label[] labels) {
	mv.visitLookupSwitchInsn(label, is, labels);
	if (constructor) {
	    popValue();
	    addBranches(label, labels);
	}
    }
    
    public transient void visitTableSwitchInsn(int i, int i_21_, Label label,
					       Label[] labels) {
	mv.visitTableSwitchInsn(i, i_21_, label, labels);
	if (constructor) {
	    popValue();
	    addBranches(label, labels);
	}
    }
    
    public void visitTryCatchBlock(Label label, Label label_22_,
				   Label label_23_, String string) {
	super.visitTryCatchBlock(label, label_22_, label_23_, string);
	if (constructor && !branches.containsKey(label_23_)) {
	    ArrayList arraylist = new ArrayList();
	    arraylist.add(OTHER);
	    branches.put(label_23_, arraylist);
	}
    }
    
    private void addBranches(Label label, Label[] labels) {
	addBranch(label);
	for (int i = 0; i < labels.length; i++)
	    addBranch(labels[i]);
    }
    
    private void addBranch(Label label) {
	if (!branches.containsKey(label))
	    branches.put(label, new ArrayList(stackFrame));
    }
    
    private Object popValue() {
	return stackFrame.remove(stackFrame.size() - 1);
    }
    
    private Object peekValue() {
	return stackFrame.get(stackFrame.size() - 1);
    }
    
    private void pushValue(Object object) {
	stackFrame.add(object);
    }
    
    protected void onMethodEnter() {
	/* empty */
    }
    
    protected void onMethodExit(int i) {
	/* empty */
    }
    
    static {
	_clinit_();
	THIS = new Object();
	OTHER = new Object();
    }
    
    /*synthetic*/ static void _clinit_() {
	/* empty */
    }
}
