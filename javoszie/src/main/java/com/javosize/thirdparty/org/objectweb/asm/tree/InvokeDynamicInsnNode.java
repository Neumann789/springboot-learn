package com.javosize.thirdparty.org.objectweb.asm.tree;

import com.javosize.thirdparty.org.objectweb.asm.Handle;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import java.util.Map;

public class InvokeDynamicInsnNode
  extends AbstractInsnNode
{
  public String name;
  public String desc;
  public Handle bsm;
  public Object[] bsmArgs;
  
  public InvokeDynamicInsnNode(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs)
  {
    super(186);
    this.name = paramString1;
    this.desc = paramString2;
    this.bsm = paramHandle;
    this.bsmArgs = paramVarArgs;
  }
  
  public int getType()
  {
    return 6;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitInvokeDynamicInsn(this.name, this.desc, this.bsm, this.bsmArgs);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map paramMap)
  {
    return new InvokeDynamicInsnNode(this.name, this.desc, this.bsm, this.bsmArgs).cloneAnnotations(this);
  }
}


