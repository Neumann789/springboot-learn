/* ConditionalExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;
import com.strobel.decompiler.patterns.Role;

public class ConditionalExpression extends Expression
{
    public static final Role CONDITION_ROLE = Roles.CONDITION;
    public static final TokenRole QUESTION_MARK_ROLE = new TokenRole("?", 2);
    public static final Role TRUE_ROLE
	= new Role("True", Expression.class, Expression.NULL);
    public static final TokenRole COLON_ROLE = new TokenRole(":", 2);
    public static final Role FALSE_ROLE
	= new Role("False", Expression.class, Expression.NULL);
    
    public ConditionalExpression(Expression condition,
				 Expression trueExpression,
				 Expression falseExpression) {
	super(condition.getOffset());
	addChild(condition, CONDITION_ROLE);
	addChild(trueExpression, TRUE_ROLE);
	addChild(falseExpression, FALSE_ROLE);
    }
    
    public final JavaTokenNode getQuestionMark() {
	return (JavaTokenNode) getChildByRole(QUESTION_MARK_ROLE);
    }
    
    public final JavaTokenNode getColonToken() {
	return (JavaTokenNode) getChildByRole(COLON_ROLE);
    }
    
    public final Expression getCondition() {
	return (Expression) getChildByRole(CONDITION_ROLE);
    }
    
    public final void setCondition(Expression value) {
	setChildByRole(CONDITION_ROLE, value);
    }
    
    public final Expression getTrueExpression() {
	return (Expression) getChildByRole(TRUE_ROLE);
    }
    
    public final void setTrueExpression(Expression value) {
	setChildByRole(TRUE_ROLE, value);
    }
    
    public final Expression getFalseExpression() {
	return (Expression) getChildByRole(FALSE_ROLE);
    }
    
    public final void setFalseExpression(Expression value) {
	setChildByRole(FALSE_ROLE, value);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitConditionalExpression(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof ConditionalExpression))
	    return false;
    label_1595:
	{
	    ConditionalExpression otherCondition
		= (ConditionalExpression) other;
	    if (other.isNull()
		|| !getCondition().matches(otherCondition.getCondition(),
					   match)
		|| !getTrueExpression()
			.matches(otherCondition.getTrueExpression(), match)
		|| !getFalseExpression()
			.matches(otherCondition.getFalseExpression(), match))
		PUSH false;
	    else
		PUSH true;
	    break label_1595;
	}
	return POP;
    }
}
