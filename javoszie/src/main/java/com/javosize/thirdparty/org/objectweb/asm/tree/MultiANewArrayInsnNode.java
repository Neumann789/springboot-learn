package com.javosize.thirdparty.org.objectweb.asm.tree;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import java.util.Map;

public class MultiANewArrayInsnNode
  extends AbstractInsnNode
{
  public String desc;
  public int dims;
  
  public MultiANewArrayInsnNode(String paramString, int paramInt)
  {
    super(197);
    this.desc = paramString;
    this.dims = paramInt;
  }
  
  public int getType()
  {
    return 13;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitMultiANewArrayInsn(this.desc, this.dims);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map paramMap)
  {
    return new MultiANewArrayInsnNode(this.desc, this.dims).cloneAnnotations(this);
  }
}


