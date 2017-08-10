/* IincInsnNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import java.util.Map;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;

public class IincInsnNode extends AbstractInsnNode
{
    public int var;
    public int incr;
    
    public IincInsnNode(int i, int i_0_) {
	super(132);
	var = i;
	incr = i_0_;
    }
    
    public int getType() {
	return 10;
    }
    
    public void accept(MethodVisitor methodvisitor) {
	methodvisitor.visitIincInsn(var, incr);
	acceptAnnotations(methodvisitor);
    }
    
    public AbstractInsnNode clone(Map map) {
	return new IincInsnNode(var, incr).cloneAnnotations(this);
    }
}
