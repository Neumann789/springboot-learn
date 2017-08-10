/* ITextOutput - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler;

public interface ITextOutput
{
    public int getRow();
    
    public int getColumn();
    
    public void indent();
    
    public void unindent();
    
    public void write(char c);
    
    public void write(String string);
    
    public void writeError(String string);
    
    public void writeLabel(String string);
    
    public void writeLiteral(Object object);
    
    public void writeTextLiteral(Object object);
    
    public void writeComment(String string);
    
    public transient void writeComment(String string, Object[] objects);
    
    public transient void write(String string, Object[] objects);
    
    public void writeLine(String string);
    
    public transient void writeLine(String string, Object[] objects);
    
    public void writeLine();
    
    public void writeDelimiter(String string);
    
    public void writeOperator(String string);
    
    public void writeKeyword(String string);
    
    public void writeAttribute(String string);
    
    public void writeDefinition(String string, Object object);
    
    public void writeDefinition(String string, Object object, boolean bool);
    
    public void writeReference(String string, Object object);
    
    public void writeReference(String string, Object object, boolean bool);
    
    public boolean isFoldingSupported();
    
    public void markFoldStart(String string, boolean bool);
    
    public void markFoldEnd();
    
    public String getIndentToken();
    
    public void setIndentToken(String string);
}
