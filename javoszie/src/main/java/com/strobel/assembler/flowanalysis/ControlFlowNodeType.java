package com.strobel.assembler.flowanalysis;

public enum ControlFlowNodeType
{
  Normal,  EntryPoint,  RegularExit,  ExceptionalExit,  CatchHandler,  FinallyHandler,  EndFinally;
  
  private ControlFlowNodeType() {}
}


