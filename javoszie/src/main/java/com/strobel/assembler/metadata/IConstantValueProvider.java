package com.strobel.assembler.metadata;

public abstract interface IConstantValueProvider
{
  public abstract boolean hasConstantValue();
  
  public abstract Object getConstantValue();
}


