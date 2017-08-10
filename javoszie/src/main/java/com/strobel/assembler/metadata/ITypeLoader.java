package com.strobel.assembler.metadata;

public abstract interface ITypeLoader
{
  public abstract boolean tryLoadType(String paramString, Buffer paramBuffer);
}


