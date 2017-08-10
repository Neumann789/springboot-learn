package com.strobel.assembler.metadata.signatures;

public abstract interface TypeTreeVisitor<T>
{
  public abstract T getResult();
  
  public abstract void visitFormalTypeParameter(FormalTypeParameter paramFormalTypeParameter);
  
  public abstract void visitClassTypeSignature(ClassTypeSignature paramClassTypeSignature);
  
  public abstract void visitArrayTypeSignature(ArrayTypeSignature paramArrayTypeSignature);
  
  public abstract void visitTypeVariableSignature(TypeVariableSignature paramTypeVariableSignature);
  
  public abstract void visitWildcard(Wildcard paramWildcard);
  
  public abstract void visitSimpleClassTypeSignature(SimpleClassTypeSignature paramSimpleClassTypeSignature);
  
  public abstract void visitBottomSignature(BottomSignature paramBottomSignature);
  
  public abstract void visitByteSignature(ByteSignature paramByteSignature);
  
  public abstract void visitBooleanSignature(BooleanSignature paramBooleanSignature);
  
  public abstract void visitShortSignature(ShortSignature paramShortSignature);
  
  public abstract void visitCharSignature(CharSignature paramCharSignature);
  
  public abstract void visitIntSignature(IntSignature paramIntSignature);
  
  public abstract void visitLongSignature(LongSignature paramLongSignature);
  
  public abstract void visitFloatSignature(FloatSignature paramFloatSignature);
  
  public abstract void visitDoubleSignature(DoubleSignature paramDoubleSignature);
  
  public abstract void visitVoidSignature(VoidSignature paramVoidSignature);
}


