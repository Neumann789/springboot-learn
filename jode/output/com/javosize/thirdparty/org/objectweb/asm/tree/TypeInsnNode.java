/* TypeInsnNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import java.util.Map;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;

public class TypeInsnNode extends AbstractInsnNode
{
    public String desc;
    
    public TypeInsnNode(int i, String string) {
	super(i);
	desc = string;
    }
    
    public void setOpcode(int i) {
	opcode = i;
    }
    
    public int getType() {
	return 3;
    }
    
    public void accept(MethodVisitor methodvisitor) {
	methodvisitor.visitTypeInsn(opcode, desc);
	acceptAnnotations(methodvisitor);
    }
    
    public AbstractInsnNode clone(Map map) {
	return new TypeInsnNode(opcode, desc).cloneAnnotations(this);
    }
}
