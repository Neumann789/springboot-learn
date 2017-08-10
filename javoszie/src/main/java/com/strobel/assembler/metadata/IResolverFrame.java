package com.strobel.assembler.metadata;

public abstract interface IResolverFrame
  extends IGenericContext
{
  public abstract TypeReference findType(String paramString);
}


