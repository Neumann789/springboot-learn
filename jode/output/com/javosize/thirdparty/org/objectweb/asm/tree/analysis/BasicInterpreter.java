/* BasicInterpreter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree.analysis;
import java.util.List;

import com.javosize.thirdparty.org.objectweb.asm.Handle;
import com.javosize.thirdparty.org.objectweb.asm.Opcodes;
import com.javosize.thirdparty.org.objectweb.asm.Type;
import com.javosize.thirdparty.org.objectweb.asm.tree.AbstractInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.FieldInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.IntInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.InvokeDynamicInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.LdcInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.MethodInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.MultiANewArrayInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.TypeInsnNode;

public class BasicInterpreter extends Interpreter implements Opcodes
{
    public BasicInterpreter() {
	super(327680);
    }
    
    protected BasicInterpreter(int i) {
	super(i);
    }
    
    public BasicValue newValue(Type type) {
	if (type != null) {
	    switch (type.getSort()) {
	    case 0:
		return null;
	    case 1:
	    case 2:
	    case 3:
	    case 4:
	    case 5:
		return BasicValue.INT_VALUE;
	    case 6:
		return BasicValue.FLOAT_VALUE;
	    case 7:
		return BasicValue.LONG_VALUE;
	    case 8:
		return BasicValue.DOUBLE_VALUE;
	    case 9:
	    case 10:
		return BasicValue.REFERENCE_VALUE;
	    default:
		throw new Error("Internal error");
	    }
	}
	return BasicValue.UNINITIALIZED_VALUE;
    }
    
    public BasicValue newOperation(AbstractInsnNode abstractinsnnode)
	throws AnalyzerException {
	switch (abstractinsnnode.getOpcode()) {
	case 1:
	    return newValue(Type.getObjectType("null"));
	case 2:
	case 3:
	case 4:
	case 5:
	case 6:
	case 7:
	case 8:
	    return BasicValue.INT_VALUE;
	case 9:
	case 10:
	    return BasicValue.LONG_VALUE;
	case 11:
	case 12:
	case 13:
	    return BasicValue.FLOAT_VALUE;
	case 14:
	case 15:
	    return BasicValue.DOUBLE_VALUE;
	case 16:
	case 17:
	    return BasicValue.INT_VALUE;
	case 18: {
	    Object object = ((LdcInsnNode) abstractinsnnode).cst;
	    if (!(object instanceof Integer)) {
		if (!(object instanceof Float)) {
		    if (!(object instanceof Long)) {
			if (!(object instanceof Double)) {
			    if (!(object instanceof String)) {
				if (!(object instanceof Type)) {
				    if (!(object instanceof Handle))
					throw new IllegalArgumentException
						  ("Illegal LDC constant "
						   + object);
				    return (newValue
					    (Type.getObjectType
					     ("java/lang/invoke/MethodHandle")));
				}
				int i = ((Type) object).getSort();
				if (i != 10 && i != 9) {
				    if (i != 11)
					throw new IllegalArgumentException
						  ("Illegal LDC constant "
						   + object);
				    return (newValue
					    (Type.getObjectType
					     ("java/lang/invoke/MethodType")));
				}
				return newValue(Type.getObjectType
						("java/lang/Class"));
			    }
			    return (newValue
				    (Type.getObjectType("java/lang/String")));
			}
			return BasicValue.DOUBLE_VALUE;
		    }
		    return BasicValue.LONG_VALUE;
		}
		return BasicValue.FLOAT_VALUE;
	    }
	    return BasicValue.INT_VALUE;
	}
	case 168:
	    return BasicValue.RETURNADDRESS_VALUE;
	case 178:
	    return newValue(Type.getType(((FieldInsnNode) abstractinsnnode)
					 .desc));
	case 187:
	    return newValue(Type.getObjectType(((TypeInsnNode)
						abstractinsnnode).desc));
	default:
	    throw new Error("Internal error.");
	}
    }
    
    public BasicValue copyOperation(AbstractInsnNode abstractinsnnode,
				    BasicValue basicvalue)
	throws AnalyzerException {
	return basicvalue;
    }
    
    public BasicValue unaryOperation(AbstractInsnNode abstractinsnnode,
				     BasicValue basicvalue)
	throws AnalyzerException {
	switch (abstractinsnnode.getOpcode()) {
	case 116:
	case 132:
	case 136:
	case 139:
	case 142:
	case 145:
	case 146:
	case 147:
	    return BasicValue.INT_VALUE;
	case 118:
	case 134:
	case 137:
	case 144:
	    return BasicValue.FLOAT_VALUE;
	case 117:
	case 133:
	case 140:
	case 143:
	    return BasicValue.LONG_VALUE;
	case 119:
	case 135:
	case 138:
	case 141:
	    return BasicValue.DOUBLE_VALUE;
	case 153:
	case 154:
	case 155:
	case 156:
	case 157:
	case 158:
	case 170:
	case 171:
	case 172:
	case 173:
	case 174:
	case 175:
	case 176:
	case 179:
	    return null;
	case 180:
	    return newValue(Type.getType(((FieldInsnNode) abstractinsnnode)
					 .desc));
	case 188:
	    switch (((IntInsnNode) abstractinsnnode).operand) {
	    case 4:
		return newValue(Type.getType("[Z"));
	    case 5:
		return newValue(Type.getType("[C"));
	    case 8:
		return newValue(Type.getType("[B"));
	    case 9:
		return newValue(Type.getType("[S"));
	    case 10:
		return newValue(Type.getType("[I"));
	    case 6:
		return newValue(Type.getType("[F"));
	    case 7:
		return newValue(Type.getType("[D"));
	    case 11:
		return newValue(Type.getType("[J"));
	    default:
		throw new AnalyzerException(abstractinsnnode,
					    "Invalid array type");
	    }
	case 189: {
	    String string = ((TypeInsnNode) abstractinsnnode).desc;
	    return newValue(Type.getType("[" + Type.getObjectType(string)));
	}
	case 190:
	    return BasicValue.INT_VALUE;
	case 191:
	    return null;
	case 192: {
	    String string = ((TypeInsnNode) abstractinsnnode).desc;
	    return newValue(Type.getObjectType(string));
	}
	case 193:
	    return BasicValue.INT_VALUE;
	case 194:
	case 195:
	case 198:
	case 199:
	    return null;
	default:
	    throw new Error("Internal error.");
	}
    }
    
    public BasicValue binaryOperation(AbstractInsnNode abstractinsnnode,
				      BasicValue basicvalue,
				      BasicValue basicvalue_0_)
	throws AnalyzerException {
	switch (abstractinsnnode.getOpcode()) {
	case 46:
	case 51:
	case 52:
	case 53:
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
	    return BasicValue.INT_VALUE;
	case 48:
	case 98:
	case 102:
	case 106:
	case 110:
	case 114:
	    return BasicValue.FLOAT_VALUE;
	case 47:
	case 97:
	case 101:
	case 105:
	case 109:
	case 113:
	case 121:
	case 123:
	case 125:
	case 127:
	case 129:
	case 131:
	    return BasicValue.LONG_VALUE;
	case 49:
	case 99:
	case 103:
	case 107:
	case 111:
	case 115:
	    return BasicValue.DOUBLE_VALUE;
	case 50:
	    return BasicValue.REFERENCE_VALUE;
	case 148:
	case 149:
	case 150:
	case 151:
	case 152:
	    return BasicValue.INT_VALUE;
	case 159:
	case 160:
	case 161:
	case 162:
	case 163:
	case 164:
	case 165:
	case 166:
	case 181:
	    return null;
	default:
	    throw new Error("Internal error.");
	}
    }
    
    public BasicValue ternaryOperation(AbstractInsnNode abstractinsnnode,
				       BasicValue basicvalue,
				       BasicValue basicvalue_1_,
				       BasicValue basicvalue_2_)
	throws AnalyzerException {
	return null;
    }
    
    public BasicValue naryOperation(AbstractInsnNode abstractinsnnode,
				    List list)
	throws AnalyzerException {
	int i = abstractinsnnode.getOpcode();
	if (i != 197) {
	    if (i != 186)
		return newValue(Type.getReturnType(((MethodInsnNode)
						    abstractinsnnode).desc));
	    return newValue(Type.getReturnType(((InvokeDynamicInsnNode)
						abstractinsnnode).desc));
	}
	return newValue(Type.getType(((MultiANewArrayInsnNode)
				      abstractinsnnode).desc));
    }
    
    public void returnOperation(AbstractInsnNode abstractinsnnode,
				BasicValue basicvalue,
				BasicValue basicvalue_3_)
	throws AnalyzerException {
	/* empty */
    }
    
    public BasicValue merge(BasicValue basicvalue, BasicValue basicvalue_4_) {
	if (basicvalue.equals(basicvalue_4_))
	    return basicvalue;
	return BasicValue.UNINITIALIZED_VALUE;
    }
}
