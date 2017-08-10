package com.strobel.decompiler.languages.java.ast;

public enum SimpleNameLookupMode
{
  EXPRESSION,  INVOCATION_TARGET,  TYPE,  TYPE_IN_IMPORT_DECLARATION,  BASE_TYPE_REFERENCE;
  
  private SimpleNameLookupMode() {}
}


