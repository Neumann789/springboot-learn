/* ArraySpecifier - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class ArraySpecifier extends AstNode
{
    public final JavaTokenNode getLeftBracketToken() {
	return (JavaTokenNode) getChildByRole(Roles.LEFT_BRACKET);
    }
    
    public final JavaTokenNode getRightBracketToken() {
	return (JavaTokenNode) getChildByRole(Roles.RIGHT_BRACKET);
    }
    
    public NodeType getNodeType() {
	return NodeType.UNKNOWN;
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitArraySpecifier(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	return other instanceof ArraySpecifier;
    }
    
    public String toString() {
	return "[]";
    }
}
