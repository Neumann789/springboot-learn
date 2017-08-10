package com.strobel.assembler.metadata;

public abstract interface MethodMetadataVisitor<P, R>
{
  public abstract R visitParameterizedMethod(MethodReference paramMethodReference, P paramP);
  
  public abstract R visitMethod(MethodReference paramMethodReference, P paramP);
}


