/* IntInsnNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import java.util.Map;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;

public class IntInsnNode extends AbstractInsnNode
{
    public int operand;
    
    public IntInsnNode(int i, int i_0_) {
	super(i);
	operand = i_0_;
    }
    
    public void setOpcode(int i) {
	opcode = i;
    }
    
    public int getType() {
	return 1;
    }
    
    public void accept(MethodVisitor methodvisitor) {
	methodvisitor.visitIntInsn(opcode, operand);
	acceptAnnotations(methodvisitor);
    }
    
    public AbstractInsnNode clone(Map map) {
	return new IntInsnNode(opcode, operand).cloneAnnotations(this);
    }
}
