package com.strobel.decompiler.languages.java.ast.transforms;

import com.strobel.decompiler.languages.java.ast.AstNode;

public abstract interface IAstTransform
{
  public abstract void run(AstNode paramAstNode);
}


