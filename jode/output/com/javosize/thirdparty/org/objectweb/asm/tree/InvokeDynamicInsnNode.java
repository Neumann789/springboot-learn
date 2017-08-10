/* InvokeDynamicInsnNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import java.util.Map;

import com.javosize.thirdparty.org.objectweb.asm.Handle;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;

public class InvokeDynamicInsnNode extends AbstractInsnNode
{
    public String name;
    public String desc;
    public Handle bsm;
    public Object[] bsmArgs;
    
    public transient InvokeDynamicInsnNode(String string, String string_0_,
					   Handle handle, Object[] objects) {
	super(186);
	name = string;
	desc = string_0_;
	bsm = handle;
	bsmArgs = objects;
    }
    
    public int getType() {
	return 6;
    }
    
    public void accept(MethodVisitor methodvisitor) {
	methodvisitor.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
	acceptAnnotations(methodvisitor);
    }
    
    public AbstractInsnNode clone(Map map) {
	return new InvokeDynamicInsnNode(name, desc, bsm, bsmArgs)
		   .cloneAnnotations(this);
    }
}
