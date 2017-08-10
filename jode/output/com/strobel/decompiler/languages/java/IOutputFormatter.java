/* IOutputFormatter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java;
import com.strobel.decompiler.languages.java.ast.AstNode;
import com.strobel.decompiler.languages.java.ast.CommentType;

public interface IOutputFormatter
{
    public void startNode(AstNode astnode);
    
    public void endNode(AstNode astnode);
    
    public void writeLabel(String string);
    
    public void writeIdentifier(String string);
    
    public void writeKeyword(String string);
    
    public void writeOperator(String string);
    
    public void writeDelimiter(String string);
    
    public void writeToken(String string);
    
    public void writeLiteral(String string);
    
    public void writeTextLiteral(String string);
    
    public void space();
    
    public void openBrace(BraceStyle bracestyle);
    
    public void closeBrace(BraceStyle bracestyle);
    
    public void indent();
    
    public void unindent();
    
    public void newLine();
    
    public void writeComment(CommentType commenttype, String string);
    
    public void resetLineNumberOffsets
	(OffsetToLineNumberConverter offsettolinenumberconverter);
}
