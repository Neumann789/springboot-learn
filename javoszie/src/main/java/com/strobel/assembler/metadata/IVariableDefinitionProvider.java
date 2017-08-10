package com.strobel.assembler.metadata;

import java.util.List;

public abstract interface IVariableDefinitionProvider
{
  public abstract boolean hasVariables();
  
  public abstract List<VariableDefinition> getVariables();
}


