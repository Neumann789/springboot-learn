package com.javosize.thirdparty.org.objectweb.asm.tree;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import java.util.Map;

public class InsnNode
  extends AbstractInsnNode
{
  public InsnNode(int paramInt)
  {
    super(paramInt);
  }
  
  public int getType()
  {
    return 0;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitInsn(this.opcode);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map paramMap)
  {
    return new InsnNode(this.opcode).cloneAnnotations(this);
  }
}


