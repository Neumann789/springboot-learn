package com.strobel.assembler.metadata;

import java.util.List;

public abstract interface IGenericInstance
{
  public abstract boolean hasTypeArguments();
  
  public abstract List<TypeReference> getTypeArguments();
  
  public abstract IGenericParameterProvider getGenericDefinition();
}


