package com.strobel.decompiler.languages.java;

import com.strobel.decompiler.languages.java.ast.AstNode;
import com.strobel.decompiler.languages.java.ast.CommentType;

public abstract interface IOutputFormatter
{
  public abstract void startNode(AstNode paramAstNode);
  
  public abstract void endNode(AstNode paramAstNode);
  
  public abstract void writeLabel(String paramString);
  
  public abstract void writeIdentifier(String paramString);
  
  public abstract void writeKeyword(String paramString);
  
  public abstract void writeOperator(String paramString);
  
  public abstract void writeDelimiter(String paramString);
  
  public abstract void writeToken(String paramString);
  
  public abstract void writeLiteral(String paramString);
  
  public abstract void writeTextLiteral(String paramString);
  
  public abstract void space();
  
  public abstract void openBrace(BraceStyle paramBraceStyle);
  
  public abstract void closeBrace(BraceStyle paramBraceStyle);
  
  public abstract void indent();
  
  public abstract void unindent();
  
  public abstract void newLine();
  
  public abstract void writeComment(CommentType paramCommentType, String paramString);
  
  public abstract void resetLineNumberOffsets(OffsetToLineNumberConverter paramOffsetToLineNumberConverter);
}


