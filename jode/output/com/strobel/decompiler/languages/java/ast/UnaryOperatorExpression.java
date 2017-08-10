/* UnaryOperatorExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class UnaryOperatorExpression extends Expression
{
    public static final TokenRole NOT_ROLE = new TokenRole("!");
    public static final TokenRole BITWISE_NOT_ROLE = new TokenRole("~");
    public static final TokenRole MINUS_ROLE = new TokenRole("-");
    public static final TokenRole PLUS_ROLE = new TokenRole("+");
    public static final TokenRole INCREMENT_ROLE = new TokenRole("++");
    public static final TokenRole DECREMENT_ROLE = new TokenRole("--");
    public static final TokenRole DEREFERENCE_ROLE = new TokenRole("*");
    public static final TokenRole ADDRESS_OF_ROLE = new TokenRole("&");
    private UnaryOperatorType _operator;
    
    public UnaryOperatorExpression(UnaryOperatorType operator,
				   Expression expression) {
	super(expression.getOffset());
	setOperator(operator);
	setExpression(expression);
    }
    
    public final UnaryOperatorType getOperator() {
	return _operator;
    }
    
    public final void setOperator(UnaryOperatorType operator) {
	verifyNotFrozen();
	_operator = operator;
    }
    
    public final JavaTokenNode getOperatorToken() {
	return (JavaTokenNode) getChildByRole(getOperatorRole(getOperator()));
    }
    
    public final Expression getExpression() {
	return (Expression) getChildByRole(Roles.EXPRESSION);
    }
    
    public final void setExpression(Expression value) {
	setChildByRole(Roles.EXPRESSION, value);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitUnaryOperatorExpression(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof UnaryOperatorExpression))
	    return false;
    label_1682:
	{
	    UnaryOperatorExpression otherOperator
		= (UnaryOperatorExpression) other;
	    if (otherOperator.isNull()
		|| (otherOperator._operator != _operator
		    && _operator != UnaryOperatorType.ANY
		    && otherOperator._operator != UnaryOperatorType.ANY)
		|| !getExpression().matches(otherOperator.getExpression(),
					    match))
		PUSH false;
	    else
		PUSH true;
	    break label_1682;
	}
	return POP;
    }
    
    public static TokenRole getOperatorRole(UnaryOperatorType operator) {
	switch (ANONYMOUS CLASS com.strobel.decompiler.languages.java.ast.UnaryOperatorExpression$1.$SwitchMap$com$strobel$decompiler$languages$java$ast$UnaryOperatorType[operator.ordinal()]) {
	case 1:
	    return NOT_ROLE;
	case 2:
	    return BITWISE_NOT_ROLE;
	case 3:
	    return MINUS_ROLE;
	case 4:
	    return PLUS_ROLE;
	case 5:
	    return INCREMENT_ROLE;
	case 6:
	    return DECREMENT_ROLE;
	case 7:
	    return INCREMENT_ROLE;
	case 8:
	    return DECREMENT_ROLE;
	default:
	    throw new IllegalArgumentException
		      ("Invalid value for UnaryOperatorType.");
	}
    }
}
