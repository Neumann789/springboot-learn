/* VarInsnNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import java.util.Map;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;

public class VarInsnNode extends AbstractInsnNode
{
    public int var;
    
    public VarInsnNode(int i, int i_0_) {
	super(i);
	var = i_0_;
    }
    
    public void setOpcode(int i) {
	opcode = i;
    }
    
    public int getType() {
	return 2;
    }
    
    public void accept(MethodVisitor methodvisitor) {
	methodvisitor.visitVarInsn(opcode, var);
	acceptAnnotations(methodvisitor);
    }
    
    public AbstractInsnNode clone(Map map) {
	return new VarInsnNode(opcode, var).cloneAnnotations(this);
    }
}
