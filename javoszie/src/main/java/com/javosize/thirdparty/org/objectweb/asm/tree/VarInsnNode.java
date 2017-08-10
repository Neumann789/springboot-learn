package com.javosize.thirdparty.org.objectweb.asm.tree;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import java.util.Map;

public class VarInsnNode
  extends AbstractInsnNode
{
  public int var;
  
  public VarInsnNode(int paramInt1, int paramInt2)
  {
    super(paramInt1);
    this.var = paramInt2;
  }
  
  public void setOpcode(int paramInt)
  {
    this.opcode = paramInt;
  }
  
  public int getType()
  {
    return 2;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitVarInsn(this.opcode, this.var);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map paramMap)
  {
    return new VarInsnNode(this.opcode, this.var).cloneAnnotations(this);
  }
}


