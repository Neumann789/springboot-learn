/* JumpInsnNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import java.util.Map;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;

public class JumpInsnNode extends AbstractInsnNode
{
    public LabelNode label;
    
    public JumpInsnNode(int i, LabelNode labelnode) {
	super(i);
	label = labelnode;
    }
    
    public void setOpcode(int i) {
	opcode = i;
    }
    
    public int getType() {
	return 7;
    }
    
    public void accept(MethodVisitor methodvisitor) {
	methodvisitor.visitJumpInsn(opcode, label.getLabel());
	acceptAnnotations(methodvisitor);
    }
    
    public AbstractInsnNode clone(Map map) {
	return new JumpInsnNode(opcode, clone(label, map))
		   .cloneAnnotations(this);
    }
}
