/* ForStatement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;
import com.strobel.decompiler.patterns.Role;

public class ForStatement extends Statement
{
    public static final TokenRole FOR_KEYWORD_ROLE = new TokenRole("for", 1);
    public static final Role INITIALIZER_ROLE
	= new Role("Initializer", Statement.class, Statement.NULL);
    public static final Role ITERATOR_ROLE
	= new Role("Iterator", Statement.class, Statement.NULL);
    
    public ForStatement(int offset) {
	super(offset);
    }
    
    public final JavaTokenNode getForToken() {
	return (JavaTokenNode) getChildByRole(FOR_KEYWORD_ROLE);
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
    
    public final JavaTokenNode getLeftParenthesisToken() {
	return (JavaTokenNode) getChildByRole(Roles.LEFT_PARENTHESIS);
    }
    
    public final JavaTokenNode getRightParenthesisToken() {
	return (JavaTokenNode) getChildByRole(Roles.RIGHT_PARENTHESIS);
    }
    
    public final AstNodeCollection getInitializers() {
	return getChildrenByRole(INITIALIZER_ROLE);
    }
    
    public final AstNodeCollection getIterators() {
	return getChildrenByRole(ITERATOR_ROLE);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitForStatement(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof ForStatement))
	    return false;
    label_1607:
	{
	    ForStatement otherStatement = (ForStatement) other;
	    if (other.isNull()
		|| !getInitializers().matches(otherStatement.getInitializers(),
					      match)
		|| !getCondition().matches(otherStatement.getCondition(),
					   match)
		|| !getIterators().matches(otherStatement.getIterators(),
					   match)
		|| !getEmbeddedStatement()
			.matches(otherStatement.getEmbeddedStatement(), match))
		PUSH false;
	    else
		PUSH true;
	    break label_1607;
	}
	return POP;
    }
}
