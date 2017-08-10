/* CastExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class CastExpression extends Expression
{
    public CastExpression(AstType castToType, Expression expression) {
	super(expression.getOffset());
	setType(castToType);
	setExpression(expression);
    }
    
    public final AstType getType() {
	return (AstType) getChildByRole(Roles.TYPE);
    }
    
    public final void setType(AstType type) {
	setChildByRole(Roles.TYPE, type);
    }
    
    public final JavaTokenNode getLeftParenthesisToken() {
	return (JavaTokenNode) getChildByRole(Roles.LEFT_PARENTHESIS);
    }
    
    public final JavaTokenNode getRightParenthesisToken() {
	return (JavaTokenNode) getChildByRole(Roles.RIGHT_PARENTHESIS);
    }
    
    public final Expression getExpression() {
	return (Expression) getChildByRole(Roles.EXPRESSION);
    }
    
    public final void setExpression(Expression value) {
	setChildByRole(Roles.EXPRESSION, value);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitCastExpression(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof CastExpression))
	    return false;
    label_1591:
	{
	    CastExpression otherCast = (CastExpression) other;
	    if (otherCast.isNull()
		|| !getType().matches(otherCast.getType(), match)
		|| !getExpression().matches(otherCast.getExpression(), match))
		PUSH false;
	    else
		PUSH true;
	    break label_1591;
	}
	return POP;
    }
}
