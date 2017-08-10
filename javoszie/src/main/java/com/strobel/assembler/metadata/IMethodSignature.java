package com.strobel.assembler.metadata;

import java.util.List;

public abstract interface IMethodSignature
  extends IGenericParameterProvider, IGenericContext
{
  public abstract boolean hasParameters();
  
  public abstract List<ParameterDefinition> getParameters();
  
  public abstract TypeReference getReturnType();
  
  public abstract List<TypeReference> getThrownTypes();
}


