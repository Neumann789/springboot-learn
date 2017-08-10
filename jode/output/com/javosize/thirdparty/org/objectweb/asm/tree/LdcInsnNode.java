/* LdcInsnNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import java.util.Map;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;

public class LdcInsnNode extends AbstractInsnNode
{
    public Object cst;
    
    public LdcInsnNode(Object object) {
	super(18);
	cst = object;
    }
    
    public int getType() {
	return 9;
    }
    
    public void accept(MethodVisitor methodvisitor) {
	methodvisitor.visitLdcInsn(cst);
	acceptAnnotations(methodvisitor);
    }
    
    public AbstractInsnNode clone(Map map) {
	return new LdcInsnNode(cst).cloneAnnotations(this);
    }
}
