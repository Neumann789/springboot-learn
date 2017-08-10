package com.strobel.decompiler.languages.java.ast;

public enum DefiniteAssignmentStatus
{
  DEFINITELY_NOT_ASSIGNED,  POTENTIALLY_ASSIGNED,  DEFINITELY_ASSIGNED,  ASSIGNED_AFTER_TRUE_EXPRESSION,  ASSIGNED_AFTER_FALSE_EXPRESSION,  CODE_UNREACHABLE;
  
  private DefiniteAssignmentStatus() {}
}


