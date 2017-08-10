/* Frame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree.analysis;
import java.util.ArrayList;

import com.javosize.thirdparty.org.objectweb.asm.Type;
import com.javosize.thirdparty.org.objectweb.asm.tree.AbstractInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.IincInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.InvokeDynamicInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.MethodInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.MultiANewArrayInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.VarInsnNode;

public class Frame
{
    private Value returnValue;
    private Value[] values;
    private int locals;
    private int top;
    
    public Frame(int i, int i_0_) {
	values = new Value[i + i_0_];
	locals = i;
    }
    
    public Frame(Frame frame_1_) {
	this(frame_1_.locals, frame_1_.values.length - frame_1_.locals);
	init(frame_1_);
    }
    
    public Frame init(Frame frame_2_) {
	returnValue = frame_2_.returnValue;
	System.arraycopy(frame_2_.values, 0, values, 0, values.length);
	top = frame_2_.top;
	return this;
    }
    
    public void setReturn(Value value) {
	returnValue = value;
    }
    
    public int getLocals() {
	return locals;
    }
    
    public int getMaxStackSize() {
	return values.length - locals;
    }
    
    public Value getLocal(int i) throws IndexOutOfBoundsException {
	if (i < locals)
	    return values[i];
	throw new IndexOutOfBoundsException
		  ("Trying to access an inexistant local variable");
    }
    
    public void setLocal(int i, Value value) throws IndexOutOfBoundsException {
	if (i < locals)
	    values[i] = value;
	throw new IndexOutOfBoundsException
		  ("Trying to access an inexistant local variable " + i);
    }
    
    public int getStackSize() {
	return top;
    }
    
    public Value getStack(int i) throws IndexOutOfBoundsException {
	return values[i + locals];
    }
    
    public void clearStack() {
	top = 0;
    }
    
    public Value pop() throws IndexOutOfBoundsException {
	if (top != 0)
	    return values[--top + locals];
	throw new IndexOutOfBoundsException
		  ("Cannot pop operand off an empty stack.");
    }
    
    public void push(Value value) throws IndexOutOfBoundsException {
	if (top + locals < values.length)
	    values[top++ + locals] = value;
	throw new IndexOutOfBoundsException
		  ("Insufficient maximum stack size.");
    }
    
    public void execute(AbstractInsnNode abstractinsnnode,
			Interpreter interpreter)
	throws AnalyzerException {
	switch (abstractinsnnode.getOpcode()) {
	case 1:
	case 2:
	case 3:
	case 4:
	case 5:
	case 6:
	case 7:
	case 8:
	case 9:
	case 10:
	case 11:
	case 12:
	case 13:
	case 14:
	case 15:
	case 16:
	case 17:
	case 18:
	    push(interpreter.newOperation(abstractinsnnode));
	    break;
	case 21:
	case 22:
	case 23:
	case 24:
	case 25:
	    push(interpreter.copyOperation(abstractinsnnode,
					   getLocal(((VarInsnNode)
						     abstractinsnnode).var)));
	    break;
	case 46:
	case 47:
	case 48:
	case 49:
	case 50:
	case 51:
	case 52:
	case 53: {
	    Value value = pop();
	    Value value_3_ = pop();
	    push(interpreter.binaryOperation(abstractinsnnode, value_3_,
					     value));
	    break;
	}
	case 54:
	case 55:
	case 56:
	case 57:
	case 58: {
	    Value value = interpreter.copyOperation(abstractinsnnode, pop());
	    int i = ((VarInsnNode) abstractinsnnode).var;
	label_511:
	    {
		setLocal(i, value);
		if (value.getSize() == 2)
		    setLocal(i + 1, interpreter.newValue(null));
		break label_511;
	    }
	    if (i > 0) {
		Value value_4_ = getLocal(i - 1);
		if (value_4_ != null && value_4_.getSize() == 2)
		    setLocal(i - 1, interpreter.newValue(null));
	    }
	    break;
	}
	case 79:
	case 80:
	case 81:
	case 82:
	case 83:
	case 84:
	case 85:
	case 86: {
	    Value value = pop();
	    Value value_5_ = pop();
	    Value value_6_ = pop();
	    interpreter.ternaryOperation(abstractinsnnode, value_6_, value_5_,
					 value);
	    break;
	}
	case 87:
	    if (pop().getSize() == 2)
		throw new AnalyzerException(abstractinsnnode,
					    "Illegal use of POP");
	    break;
	case 88:
	    if (pop().getSize() == 1 && pop().getSize() != 1)
		throw new AnalyzerException(abstractinsnnode,
					    "Illegal use of POP2");
	    break;
	case 89: {
	    Value value = pop();
	    if (value.getSize() == 1) {
		push(value);
		push(interpreter.copyOperation(abstractinsnnode, value));
	    } else
		throw new AnalyzerException(abstractinsnnode,
					    "Illegal use of DUP");
	    break;
	}
	case 90: {
	    Value value = pop();
	    Value value_7_ = pop();
	    if (value.getSize() == 1 && value_7_.getSize() == 1) {
		push(interpreter.copyOperation(abstractinsnnode, value));
		push(value_7_);
		push(value);
	    } else
		throw new AnalyzerException(abstractinsnnode,
					    "Illegal use of DUP_X1");
	    break;
	}
	case 91:
	label_512:
	    {
		Value value = pop();
		if (value.getSize() == 1) {
		    Value value_8_ = pop();
		    if (value_8_.getSize() != 1) {
			push(interpreter.copyOperation(abstractinsnnode,
						       value));
			push(value_8_);
			push(value);
			return;
		    }
		    Value value_9_ = pop();
		    if (value_9_.getSize() == 1) {
			push(interpreter.copyOperation(abstractinsnnode,
						       value));
			push(value_9_);
			push(value_8_);
			push(value);
			return;
		    }
		}
		break label_512;
	    }
	    throw new AnalyzerException(abstractinsnnode,
					"Illegal use of DUP_X2");
	case 92: {
	    Value value = pop();
	    if (value.getSize() != 1) {
		push(value);
		push(interpreter.copyOperation(abstractinsnnode, value));
	    } else {
		Value value_10_ = pop();
		if (value_10_.getSize() != 1)
		    throw new AnalyzerException(abstractinsnnode,
						"Illegal use of DUP2");
		push(value_10_);
		push(value);
		push(interpreter.copyOperation(abstractinsnnode, value_10_));
		push(interpreter.copyOperation(abstractinsnnode, value));
	    }
	    break;
	}
	case 93:
	label_513:
	    {
		Value value = pop();
		if (value.getSize() != 1) {
		    Value value_11_ = pop();
		    if (value_11_.getSize() == 1) {
			push(interpreter.copyOperation(abstractinsnnode,
						       value));
			push(value_11_);
			push(value);
			return;
		    }
		} else {
		    Value value_12_ = pop();
		    if (value_12_.getSize() == 1) {
			Value value_13_ = pop();
			if (value_13_.getSize() == 1) {
			    push(interpreter.copyOperation(abstractinsnnode,
							   value_12_));
			    push(interpreter.copyOperation(abstractinsnnode,
							   value));
			    push(value_13_);
			    push(value_12_);
			    push(value);
			    return;
			}
		    }
		}
		break label_513;
	    }
	    throw new AnalyzerException(abstractinsnnode,
					"Illegal use of DUP2_X1");
	case 94:
	label_514:
	    {
		Value value = pop();
		if (value.getSize() != 1) {
		    Value value_14_ = pop();
		    if (value_14_.getSize() != 1) {
			push(interpreter.copyOperation(abstractinsnnode,
						       value));
			push(value_14_);
			push(value);
			return;
		    }
		    Value value_15_ = pop();
		    if (value_15_.getSize() == 1) {
			push(interpreter.copyOperation(abstractinsnnode,
						       value));
			push(value_15_);
			push(value_14_);
			push(value);
			return;
		    }
		} else {
		    Value value_16_ = pop();
		    if (value_16_.getSize() == 1) {
			Value value_17_ = pop();
			if (value_17_.getSize() != 1) {
			    push(interpreter.copyOperation(abstractinsnnode,
							   value_16_));
			    push(interpreter.copyOperation(abstractinsnnode,
							   value));
			    push(value_17_);
			    push(value_16_);
			    push(value);
			    return;
			}
			Value value_18_ = pop();
			if (value_18_.getSize() == 1) {
			    push(interpreter.copyOperation(abstractinsnnode,
							   value_16_));
			    push(interpreter.copyOperation(abstractinsnnode,
							   value));
			    push(value_18_);
			    push(value_17_);
			    push(value_16_);
			    push(value);
			    return;
			}
		    }
		}
		break label_514;
	    }
	    throw new AnalyzerException(abstractinsnnode,
					"Illegal use of DUP2_X2");
	case 95: {
	    Value value = pop();
	    Value value_19_ = pop();
	    if (value_19_.getSize() == 1 && value.getSize() == 1) {
		push(interpreter.copyOperation(abstractinsnnode, value));
		push(interpreter.copyOperation(abstractinsnnode, value_19_));
	    } else
		throw new AnalyzerException(abstractinsnnode,
					    "Illegal use of SWAP");
	    break;
	}
	case 96:
	case 97:
	case 98:
	case 99:
	case 100:
	case 101:
	case 102:
	case 103:
	case 104:
	case 105:
	case 106:
	case 107:
	case 108:
	case 109:
	case 110:
	case 111:
	case 112:
	case 113:
	case 114:
	case 115: {
	    Value value = pop();
	    Value value_20_ = pop();
	    push(interpreter.binaryOperation(abstractinsnnode, value_20_,
					     value));
	    break;
	}
	case 116:
	case 117:
	case 118:
	case 119:
	    push(interpreter.unaryOperation(abstractinsnnode, pop()));
	    break;
	case 120:
	case 121:
	case 122:
	case 123:
	case 124:
	case 125:
	case 126:
	case 127:
	case 128:
	case 129:
	case 130:
	case 131: {
	    Value value = pop();
	    Value value_21_ = pop();
	    push(interpreter.binaryOperation(abstractinsnnode, value_21_,
					     value));
	    break;
	}
	case 132: {
	    int i = ((IincInsnNode) abstractinsnnode).var;
	    setLocal(i, interpreter.unaryOperation(abstractinsnnode,
						   getLocal(i)));
	    break;
	}
	case 133:
	case 134:
	case 135:
	case 136:
	case 137:
	case 138:
	case 139:
	case 140:
	case 141:
	case 142:
	case 143:
	case 144:
	case 145:
	case 146:
	case 147:
	    push(interpreter.unaryOperation(abstractinsnnode, pop()));
	    break;
	case 148:
	case 149:
	case 150:
	case 151:
	case 152: {
	    Value value = pop();
	    Value value_22_ = pop();
	    push(interpreter.binaryOperation(abstractinsnnode, value_22_,
					     value));
	    break;
	}
	case 153:
	case 154:
	case 155:
	case 156:
	case 157:
	case 158:
	    interpreter.unaryOperation(abstractinsnnode, pop());
	    break;
	case 159:
	case 160:
	case 161:
	case 162:
	case 163:
	case 164:
	case 165:
	case 166: {
	    Value value = pop();
	    Value value_23_ = pop();
	    interpreter.binaryOperation(abstractinsnnode, value_23_, value);
	    break;
	}
	case 168:
	    push(interpreter.newOperation(abstractinsnnode));
	    break;
	case 170:
	case 171:
	    interpreter.unaryOperation(abstractinsnnode, pop());
	    break;
	case 172:
	case 173:
	case 174:
	case 175:
	case 176: {
	    Value value = pop();
	    interpreter.unaryOperation(abstractinsnnode, value);
	    interpreter.returnOperation(abstractinsnnode, value, returnValue);
	    break;
	}
	case 177:
	    if (returnValue != null)
		throw new AnalyzerException(abstractinsnnode,
					    "Incompatible return type");
	    break;
	case 178:
	    push(interpreter.newOperation(abstractinsnnode));
	    break;
	case 179:
	    interpreter.unaryOperation(abstractinsnnode, pop());
	    break;
	case 180:
	    push(interpreter.unaryOperation(abstractinsnnode, pop()));
	    break;
	case 181: {
	    Value value = pop();
	    Value value_24_ = pop();
	    interpreter.binaryOperation(abstractinsnnode, value_24_, value);
	    break;
	}
	case 182:
	case 183:
	case 184:
	case 185: {
	    ArrayList arraylist = new ArrayList();
	    String string = ((MethodInsnNode) abstractinsnnode).desc;
	    int i = Type.getArgumentTypes(string).length;
	label_515:
	    {
		for (;;) {
		    if (i <= 0) {
			if (abstractinsnnode.getOpcode() != 184)
			    arraylist.add(0, pop());
			break label_515;
		    }
		    arraylist.add(0, pop());
		    i--;
		}
	    }
	    if (Type.getReturnType(string) != Type.VOID_TYPE)
		push(interpreter.naryOperation(abstractinsnnode, arraylist));
	    else
		interpreter.naryOperation(abstractinsnnode, arraylist);
	    break;
	    break label_515;
	}
	case 186: {
	    ArrayList arraylist = new ArrayList();
	    String string = ((InvokeDynamicInsnNode) abstractinsnnode).desc;
	    int i = Type.getArgumentTypes(string).length;
	    for (;;) {
		if (i <= 0) {
		    if (Type.getReturnType(string) != Type.VOID_TYPE)
			push(interpreter.naryOperation(abstractinsnnode,
						       arraylist));
		    else
			interpreter.naryOperation(abstractinsnnode, arraylist);
		    break;
		}
		arraylist.add(0, pop());
		i--;
	    }
	    break;
	}
	case 187:
	    push(interpreter.newOperation(abstractinsnnode));
	    break;
	case 188:
	case 189:
	case 190:
	    push(interpreter.unaryOperation(abstractinsnnode, pop()));
	    break;
	case 191:
	    interpreter.unaryOperation(abstractinsnnode, pop());
	    break;
	case 192:
	case 193:
	    push(interpreter.unaryOperation(abstractinsnnode, pop()));
	    break;
	case 194:
	case 195:
	    interpreter.unaryOperation(abstractinsnnode, pop());
	    break;
	case 197: {
	    ArrayList arraylist = new ArrayList();
	    int i = ((MultiANewArrayInsnNode) abstractinsnnode).dims;
	    for (;;) {
		if (i <= 0) {
		    push(interpreter.naryOperation(abstractinsnnode,
						   arraylist));
		    break;
		}
		arraylist.add(0, pop());
		i--;
	    }
	    break;
	}
	case 198:
	case 199:
	    interpreter.unaryOperation(abstractinsnnode, pop());
	    break;
	default:
	    throw new RuntimeException("Illegal opcode "
				       + abstractinsnnode.getOpcode());
	case 0:
	case 167:
	case 169:
	}
    }
    
    public boolean merge(Frame frame_25_, Interpreter interpreter)
	throws AnalyzerException {
	if (top == frame_25_.top) {
	    boolean bool = false;
	    int i = 0;
	    for (;;) {
		if (i >= locals + top)
		    return bool;
	    label_516:
		{
		    Value value
			= interpreter.merge(values[i], frame_25_.values[i]);
		    if (!value.equals(values[i])) {
			values[i] = value;
			bool = true;
		    }
		    break label_516;
		}
		i++;
	    }
	}
	throw new AnalyzerException(null, "Incompatible stack heights");
    }
    
    public boolean merge(Frame frame_26_, boolean[] bools) {
	boolean bool = false;
	int i = 0;
	for (;;) {
	label_517:
	    {
		if (i >= locals)
		    return bool;
		if (!bools[i] && !values[i].equals(frame_26_.values[i])) {
		    values[i] = frame_26_.values[i];
		    bool = true;
		}
		break label_517;
	    }
	    i++;
	}
    }
    
    public String toString() {
	StringBuffer stringbuffer = new StringBuffer();
	int i = 0;
	for (;;) {
	    if (i >= getLocals()) {
		stringbuffer.append(' ');
		i = 0;
		for (;;) {
		    if (i >= getStackSize())
			return stringbuffer.toString();
		    stringbuffer.append(getStack(i).toString());
		    i++;
		}
	    }
	    stringbuffer.append(getLocal(i));
	    i++;
	}
    }
}
