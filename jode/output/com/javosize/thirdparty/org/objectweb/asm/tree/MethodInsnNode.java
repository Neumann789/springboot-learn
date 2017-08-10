/* MethodInsnNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import java.util.Map;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;

public class MethodInsnNode extends AbstractInsnNode
{
    public String owner;
    public String name;
    public String desc;
    public boolean itf;
    
    /**
     * @deprecated
     */
    public MethodInsnNode(int i, String string, String string_0_,
			  String string_1_) {
	PUSH this;
	PUSH i;
	PUSH string;
	PUSH string_0_;
    label_474:
	{
	    PUSH string_1_;
	    if (i != 185)
		PUSH false;
	    else
		PUSH true;
	    break label_474;
	}
	((UNCONSTRUCTED)POP).MethodInsnNode(POP, POP, POP, POP, POP);
    }
    
    public MethodInsnNode(int i, String string, String string_2_,
			  String string_3_, boolean bool) {
	super(i);
	owner = string;
	name = string_2_;
	desc = string_3_;
	itf = bool;
    }
    
    public void setOpcode(int i) {
	opcode = i;
    }
    
    public int getType() {
	return 5;
    }
    
    public void accept(MethodVisitor methodvisitor) {
	methodvisitor.visitMethodInsn(opcode, owner, name, desc, itf);
    }
    
    public AbstractInsnNode clone(Map map) {
	return new MethodInsnNode(opcode, owner, name, desc, itf);
    }
}
