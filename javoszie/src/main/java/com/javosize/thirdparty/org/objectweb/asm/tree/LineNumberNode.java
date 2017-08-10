package com.javosize.thirdparty.org.objectweb.asm.tree;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import java.util.Map;

public class LineNumberNode
  extends AbstractInsnNode
{
  public int line;
  public LabelNode start;
  
  public LineNumberNode(int paramInt, LabelNode paramLabelNode)
  {
    super(-1);
    this.line = paramInt;
    this.start = paramLabelNode;
  }
  
  public int getType()
  {
    return 15;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitLineNumber(this.line, this.start.getLabel());
  }
  
  public AbstractInsnNode clone(Map paramMap)
  {
    return new LineNumberNode(this.line, clone(this.start, paramMap));
  }
}


