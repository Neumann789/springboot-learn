package com.strobel.decompiler.languages.java.analysis;

public enum ControlFlowNodeType
{
  None,  StartNode,  BetweenStatements,  EndNode,  LoopCondition;
  
  private ControlFlowNodeType() {}
}


