package com.strobel.decompiler.languages.java.ast;

public enum NodeType
{
  UNKNOWN,  TYPE_REFERENCE,  TYPE_DECLARATION,  MEMBER,  STATEMENT,  EXPRESSION,  TOKEN,  WHITESPACE,  PATTERN;
  
  private NodeType() {}
}


