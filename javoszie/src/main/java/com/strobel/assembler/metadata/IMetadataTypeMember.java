package com.strobel.assembler.metadata;

public abstract interface IMetadataTypeMember
{
  public abstract String getName();
  
  public abstract TypeReference getDeclaringType();
}


