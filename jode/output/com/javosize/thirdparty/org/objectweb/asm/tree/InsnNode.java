/* InsnNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import java.util.Map;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;

public class InsnNode extends AbstractInsnNode
{
    public InsnNode(int i) {
	super(i);
    }
    
    public int getType() {
	return 0;
    }
    
    public void accept(MethodVisitor methodvisitor) {
	methodvisitor.visitInsn(opcode);
	acceptAnnotations(methodvisitor);
    }
    
    public AbstractInsnNode clone(Map map) {
	return new InsnNode(opcode).cloneAnnotations(this);
    }
}
