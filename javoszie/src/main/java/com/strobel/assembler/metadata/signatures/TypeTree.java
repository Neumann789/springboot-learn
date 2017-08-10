package com.strobel.assembler.metadata.signatures;

public abstract interface TypeTree
  extends Tree
{
  public abstract void accept(TypeTreeVisitor<?> paramTypeTreeVisitor);
}


