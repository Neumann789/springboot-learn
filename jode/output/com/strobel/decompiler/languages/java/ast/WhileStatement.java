/* WhileStatement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class WhileStatement extends Statement
{
    public static final TokenRole WHILE_KEYWORD_ROLE
	= new TokenRole("while", 1);
    
    public WhileStatement(int offset) {
	super(offset);
    }
    
    public WhileStatement(Expression condition) {
	super(condition.getOffset());
	setCondition(condition);
    }
    
    public final Statement getEmbeddedStatement() {
	return (Statement) getChildByRole(Roles.EMBEDDED_STATEMENT);
    }
    
    public final void setEmbeddedStatement(Statement value) {
	setChildByRole(Roles.EMBEDDED_STATEMENT, value);
    }
    
    public final Expression getCondition() {
	return (Expression) getChildByRole(Roles.CONDITION);
    }
    
    public final void setCondition(Expression value) {
	setChildByRole(Roles.CONDITION, value);
    }
    
    public final JavaTokenNode getWhileToken() {
	return (JavaTokenNode) getChildByRole(WHILE_KEYWORD_ROLE);
    }
    
    public final JavaTokenNode getLeftParenthesisToken() {
	return (JavaTokenNode) getChildByRole(Roles.LEFT_PARENTHESIS);
    }
    
    public final JavaTokenNode getRightParenthesisToken() {
	return (JavaTokenNode) getChildByRole(Roles.RIGHT_PARENTHESIS);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitWhileStatement(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof WhileStatement))
	    return false;
    label_1686:
	{
	    WhileStatement otherStatement = (WhileStatement) other;
	    if (other.isNull()
		|| !getCondition().matches(otherStatement.getCondition(),
					   match)
		|| !getEmbeddedStatement()
			.matches(otherStatement.getEmbeddedStatement(), match))
		PUSH false;
	    else
		PUSH true;
	    break label_1686;
	}
	return POP;
    }
}
