/* Interpreter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree.analysis;
import java.util.List;

import com.javosize.thirdparty.org.objectweb.asm.Type;
import com.javosize.thirdparty.org.objectweb.asm.tree.AbstractInsnNode;

public abstract class Interpreter
{
    protected final int api;
    
    protected Interpreter(int i) {
	api = i;
    }
    
    public abstract Value newValue(Type type);
    
    public abstract Value newOperation(AbstractInsnNode abstractinsnnode)
	throws AnalyzerException;
    
    public abstract Value copyOperation(AbstractInsnNode abstractinsnnode,
					Value value)
	throws AnalyzerException;
    
    public abstract Value unaryOperation(AbstractInsnNode abstractinsnnode,
					 Value value)
	throws AnalyzerException;
    
    public abstract Value binaryOperation(AbstractInsnNode abstractinsnnode,
					  Value value, Value value_0_)
	throws AnalyzerException;
    
    public abstract Value ternaryOperation(AbstractInsnNode abstractinsnnode,
					   Value value, Value value_1_,
					   Value value_2_)
	throws AnalyzerException;
    
    public abstract Value naryOperation(AbstractInsnNode abstractinsnnode,
					List list)
	throws AnalyzerException;
    
    public abstract void returnOperation(AbstractInsnNode abstractinsnnode,
					 Value value, Value value_3_)
	throws AnalyzerException;
    
    public abstract Value merge(Value value, Value value_4_);
}
