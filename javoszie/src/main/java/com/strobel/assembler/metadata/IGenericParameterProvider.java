package com.strobel.assembler.metadata;

import java.util.List;

public abstract interface IGenericParameterProvider
{
  public abstract boolean hasGenericParameters();
  
  public abstract boolean isGenericDefinition();
  
  public abstract List<GenericParameter> getGenericParameters();
}


