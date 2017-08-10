package com.strobel.assembler.metadata.signatures;

public abstract interface Signature
  extends Tree
{
  public abstract FormalTypeParameter[] getFormalTypeParameters();
}


