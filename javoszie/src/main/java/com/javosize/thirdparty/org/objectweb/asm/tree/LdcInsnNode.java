package com.javosize.thirdparty.org.objectweb.asm.tree;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import java.util.Map;

public class LdcInsnNode
  extends AbstractInsnNode
{
  public Object cst;
  
  public LdcInsnNode(Object paramObject)
  {
    super(18);
    this.cst = paramObject;
  }
  
  public int getType()
  {
    return 9;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitLdcInsn(this.cst);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map paramMap)
  {
    return new LdcInsnNode(this.cst).cloneAnnotations(this);
  }
}


