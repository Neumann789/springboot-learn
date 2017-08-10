/* InstanceOfExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class InstanceOfExpression extends Expression
{
    public static final TokenRole INSTANCE_OF_KEYWORD_ROLE
	= new TokenRole("instanceof", 3);
    
    public InstanceOfExpression(int offset, Expression expression,
				AstType type) {
	super(offset);
	setExpression(expression);
	setType(type);
    }
    
    public final AstType getType() {
	return (AstType) getChildByRole(Roles.TYPE);
    }
    
    public final void setType(AstType type) {
	setChildByRole(Roles.TYPE, type);
    }
    
    public final JavaTokenNode getInstanceOfToken() {
	return (JavaTokenNode) getChildByRole(INSTANCE_OF_KEYWORD_ROLE);
    }
    
    public final Expression getExpression() {
	return (Expression) getChildByRole(Roles.EXPRESSION);
    }
    
    public final void setExpression(Expression value) {
	setChildByRole(Roles.EXPRESSION, value);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitInstanceOfExpression(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof InstanceOfExpression))
	    return false;
    label_1616:
	{
	    InstanceOfExpression otherExpression
		= (InstanceOfExpression) other;
	    if (otherExpression.isNull()
		|| !getExpression().matches(otherExpression.getExpression(),
					    match)
		|| !getType().matches(otherExpression.getType(), match))
		PUSH false;
	    else
		PUSH true;
	    break label_1616;
	}
	return POP;
    }
}
