/* MultiANewArrayInsnNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import java.util.Map;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;

public class MultiANewArrayInsnNode extends AbstractInsnNode
{
    public String desc;
    public int dims;
    
    public MultiANewArrayInsnNode(String string, int i) {
	super(197);
	desc = string;
	dims = i;
    }
    
    public int getType() {
	return 13;
    }
    
    public void accept(MethodVisitor methodvisitor) {
	methodvisitor.visitMultiANewArrayInsn(desc, dims);
	acceptAnnotations(methodvisitor);
    }
    
    public AbstractInsnNode clone(Map map) {
	return new MultiANewArrayInsnNode(desc, dims).cloneAnnotations(this);
    }
}
