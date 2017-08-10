package com.javosize.thirdparty.org.objectweb.asm.tree.analysis;

import com.javosize.thirdparty.org.objectweb.asm.tree.AbstractInsnNode;
import java.util.Set;

public class SourceValue
  implements Value
{
  public final int size;
  public final Set insns;
  
  public SourceValue(int paramInt)
  {
    this(paramInt, SmallSet.emptySet());
  }
  
  public SourceValue(int paramInt, AbstractInsnNode paramAbstractInsnNode)
  {
    this.size = paramInt;
    this.insns = new SmallSet(paramAbstractInsnNode, null);
  }
  
  public SourceValue(int paramInt, Set paramSet)
  {
    this.size = paramInt;
    this.insns = paramSet;
  }
  
  public int getSize()
  {
    return this.size;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof SourceValue)) {
      return false;
    }
    SourceValue localSourceValue = (SourceValue)paramObject;
    return (this.size == localSourceValue.size) && (this.insns.equals(localSourceValue.insns));
  }
  
  public int hashCode()
  {
    return this.insns.hashCode();
  }
}


