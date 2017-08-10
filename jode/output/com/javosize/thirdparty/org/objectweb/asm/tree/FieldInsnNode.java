/* FieldInsnNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import java.util.Map;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;

public class FieldInsnNode extends AbstractInsnNode
{
    public String owner;
    public String name;
    public String desc;
    
    public FieldInsnNode(int i, String string, String string_0_,
			 String string_1_) {
	super(i);
	owner = string;
	name = string_0_;
	desc = string_1_;
    }
    
    public void setOpcode(int i) {
	opcode = i;
    }
    
    public int getType() {
	return 4;
    }
    
    public void accept(MethodVisitor methodvisitor) {
	methodvisitor.visitFieldInsn(opcode, owner, name, desc);
	acceptAnnotations(methodvisitor);
    }
    
    public AbstractInsnNode clone(Map map) {
	return new FieldInsnNode(opcode, owner, name, desc)
		   .cloneAnnotations(this);
    }
}
