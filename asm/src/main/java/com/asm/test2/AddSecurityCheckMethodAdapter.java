package com.asm.test2;

import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class AddSecurityCheckMethodAdapter extends MethodAdapter { 
    public AddSecurityCheckMethodAdapter(MethodVisitor mv) { 
        super(mv); 
    } 
 
    public void visitCode() { 
        visitMethodInsn(Opcodes.INVOKESTATIC, "com/asm/test2/SecurityChecker", 
           "checkSecurity", "()V"); 
    } 
}
