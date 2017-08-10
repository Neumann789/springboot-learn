package com.strobel.assembler.metadata.signatures;

public abstract interface Visitor<T>
  extends TypeTreeVisitor<T>
{
  public abstract void visitClassSignature(ClassSignature paramClassSignature);
  
  public abstract void visitMethodTypeSignature(MethodTypeSignature paramMethodTypeSignature);
}


