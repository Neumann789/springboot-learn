package com.strobel.assembler.flowanalysis;

public enum JumpType
{
  Normal,  JumpToExceptionHandler,  LeaveTry,  EndFinally;
  
  private JumpType() {}
}


