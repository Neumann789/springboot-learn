/* LocalVariableNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;

public class LocalVariableNode
{
    public String name;
    public String desc;
    public String signature;
    public LabelNode start;
    public LabelNode end;
    public int index;
    
    public LocalVariableNode(String string, String string_0_, String string_1_,
			     LabelNode labelnode, LabelNode labelnode_2_,
			     int i) {
	name = string;
	desc = string_0_;
	signature = string_1_;
	start = labelnode;
	end = labelnode_2_;
	index = i;
    }
    
    public void accept(MethodVisitor methodvisitor) {
	methodvisitor.visitLocalVariable(name, desc, signature,
					 start.getLabel(), end.getLabel(),
					 index);
    }
}
