package com.strobel.decompiler.languages.java.analysis;

public enum ControlFlowEdgeType
{
  Normal,  ConditionTrue,  ConditionFalse,  Jump;
  
  private ControlFlowEdgeType() {}
}


