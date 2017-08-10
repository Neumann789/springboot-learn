package com.strobel.assembler.metadata;

import java.util.List;

public abstract interface IClassSignature
  extends IGenericParameterProvider
{
  public abstract TypeReference getBaseType();
  
  public abstract List<TypeReference> getExplicitInterfaces();
  
  public abstract boolean hasGenericParameters();
  
  public abstract List<GenericParameter> getGenericParameters();
}


