package com.strobel.assembler.metadata;

public abstract interface FieldMetadataVisitor<P, R>
{
  public abstract R visitField(FieldReference paramFieldReference, P paramP);
}


