package com.strobel.decompiler;

public abstract interface ITextOutput
{
  public abstract int getRow();
  
  public abstract int getColumn();
  
  public abstract void indent();
  
  public abstract void unindent();
  
  public abstract void write(char paramChar);
  
  public abstract void write(String paramString);
  
  public abstract void writeError(String paramString);
  
  public abstract void writeLabel(String paramString);
  
  public abstract void writeLiteral(Object paramObject);
  
  public abstract void writeTextLiteral(Object paramObject);
  
  public abstract void writeComment(String paramString);
  
  public abstract void writeComment(String paramString, Object... paramVarArgs);
  
  public abstract void write(String paramString, Object... paramVarArgs);
  
  public abstract void writeLine(String paramString);
  
  public abstract void writeLine(String paramString, Object... paramVarArgs);
  
  public abstract void writeLine();
  
  public abstract void writeDelimiter(String paramString);
  
  public abstract void writeOperator(String paramString);
  
  public abstract void writeKeyword(String paramString);
  
  public abstract void writeAttribute(String paramString);
  
  public abstract void writeDefinition(String paramString, Object paramObject);
  
  public abstract void writeDefinition(String paramString, Object paramObject, boolean paramBoolean);
  
  public abstract void writeReference(String paramString, Object paramObject);
  
  public abstract void writeReference(String paramString, Object paramObject, boolean paramBoolean);
  
  public abstract boolean isFoldingSupported();
  
  public abstract void markFoldStart(String paramString, boolean paramBoolean);
  
  public abstract void markFoldEnd();
  
  public abstract String getIndentToken();
  
  public abstract void setIndentToken(String paramString);
}


