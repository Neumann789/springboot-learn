/* SourceInterpreter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree.analysis;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.javosize.thirdparty.org.objectweb.asm.Opcodes;
import com.javosize.thirdparty.org.objectweb.asm.Type;
import com.javosize.thirdparty.org.objectweb.asm.tree.AbstractInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.FieldInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.InvokeDynamicInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.LdcInsnNode;
import com.javosize.thirdparty.org.objectweb.asm.tree.MethodInsnNode;

public class SourceInterpreter extends Interpreter implements Opcodes
{
    public SourceInterpreter() {
	super(327680);
    }
    
    protected SourceInterpreter(int i) {
	super(i);
    }
    
    public SourceValue newValue(Type type) {
    label_527:
	{
	    if (type != Type.VOID_TYPE) {
		PUSH new SourceValue;
		DUP
		if (type != null)
		    PUSH type.getSize();
		else
		    PUSH true;
	    } else
		return null;
	}
	((UNCONSTRUCTED)POP).SourceValue(POP);
	return POP;
	break label_527;
    }
    
    public SourceValue newOperation(AbstractInsnNode abstractinsnnode) {
	int i;
	switch (abstractinsnnode.getOpcode()) {
	case 9:
	case 10:
	case 14:
	case 15:
	    i = 2;
	    break;
	case 18:
	label_528:
	    {
		Object object = ((LdcInsnNode) abstractinsnnode).cst;
		if (!(object instanceof Long) && !(object instanceof Double))
		    PUSH true;
		else
		    PUSH 2;
		break label_528;
	    }
	    i = POP;
	    break;
	case 178:
	    i = Type.getType(((FieldInsnNode) abstractinsnnode).desc)
		    .getSize();
	    break;
	default:
	    i = 1;
	}
	return new SourceValue(i, abstractinsnnode);
    }
    
    public SourceValue copyOperation(AbstractInsnNode abstractinsnnode,
				     SourceValue sourcevalue) {
	return new SourceValue(sourcevalue.getSize(), abstractinsnnode);
    }
    
    public SourceValue unaryOperation(AbstractInsnNode abstractinsnnode,
				      SourceValue sourcevalue) {
	int i;
	switch (abstractinsnnode.getOpcode()) {
	case 117:
	case 119:
	case 133:
	case 135:
	case 138:
	case 140:
	case 141:
	case 143:
	    i = 2;
	    break;
	case 180:
	    i = Type.getType(((FieldInsnNode) abstractinsnnode).desc)
		    .getSize();
	    break;
	default:
	    i = 1;
	}
	return new SourceValue(i, abstractinsnnode);
    }
    
    public SourceValue binaryOperation(AbstractInsnNode abstractinsnnode,
				       SourceValue sourcevalue,
				       SourceValue sourcevalue_0_) {
	int i;
	switch (abstractinsnnode.getOpcode()) {
	case 47:
	case 49:
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
	case 121:
	case 123:
	case 125:
	case 127:
	case 129:
	case 131:
	    i = 2;
	    break;
	default:
	    i = 1;
	}
	return new SourceValue(i, abstractinsnnode);
    }
    
    public SourceValue ternaryOperation(AbstractInsnNode abstractinsnnode,
					SourceValue sourcevalue,
					SourceValue sourcevalue_1_,
					SourceValue sourcevalue_2_) {
	return new SourceValue(1, abstractinsnnode);
    }
    
    public SourceValue naryOperation(AbstractInsnNode abstractinsnnode,
				     List list) {
	int i = abstractinsnnode.getOpcode();
	int i_3_;
    label_530:
	{
	label_529:
	    {
		if (i != 197) {
		    if (i != 186)
			PUSH ((MethodInsnNode) abstractinsnnode).desc;
		    else
			PUSH ((InvokeDynamicInsnNode) abstractinsnnode).desc;
		} else {
		    i_3_ = 1;
		    break label_530;
		}
	    }
	    String string = POP;
	    i_3_ = Type.getReturnType(string).getSize();
	}
	return new SourceValue(i_3_, abstractinsnnode);
	break label_529;
    }
    
    public void returnOperation(AbstractInsnNode abstractinsnnode,
				SourceValue sourcevalue,
				SourceValue sourcevalue_4_) {
	/* empty */
    }
    
    public SourceValue merge(SourceValue sourcevalue,
			     SourceValue sourcevalue_5_) {
	if (!(sourcevalue.insns instanceof SmallSet)
	    || !(sourcevalue_5_.insns instanceof SmallSet)) {
	    if (sourcevalue.size == sourcevalue_5_.size
		&& sourcevalue.insns.containsAll(sourcevalue_5_.insns))
		return sourcevalue;
	    HashSet hashset = new HashSet();
	    hashset.addAll(sourcevalue.insns);
	    hashset.addAll(sourcevalue_5_.insns);
	    return new SourceValue(Math.min(sourcevalue.size,
					    sourcevalue_5_.size),
				   hashset);
	}
	Set set = ((SmallSet) sourcevalue.insns)
		      .union((SmallSet) sourcevalue_5_.insns);
	if (set != sourcevalue.insns
	    || sourcevalue.size != sourcevalue_5_.size)
	    return new SourceValue(Math.min(sourcevalue.size,
					    sourcevalue_5_.size),
				   set);
	return sourcevalue;
    }
}
