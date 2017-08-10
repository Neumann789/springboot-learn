/* BinaryOperatorExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;
import com.strobel.decompiler.patterns.Role;

public class BinaryOperatorExpression extends Expression
{
    public static final TokenRole BITWISE_AND_ROLE = new TokenRole("&", 2);
    public static final TokenRole BITWISE_OR_ROLE = new TokenRole("|", 2);
    public static final TokenRole LOGICAL_AND_ROLE = new TokenRole("&&", 2);
    public static final TokenRole LOGICAL_OR_ROLE = new TokenRole("||", 2);
    public static final TokenRole EXCLUSIVE_OR_ROLE = new TokenRole("^", 2);
    public static final TokenRole GREATER_THAN_ROLE = new TokenRole(">", 2);
    public static final TokenRole GREATER_THAN_OR_EQUAL_ROLE
	= new TokenRole(">=", 2);
    public static final TokenRole EQUALITY_ROLE = new TokenRole("==", 2);
    public static final TokenRole IN_EQUALITY_ROLE = new TokenRole("!=", 2);
    public static final TokenRole LESS_THAN_ROLE = new TokenRole("<", 2);
    public static final TokenRole LESS_THAN_OR_EQUAL_ROLE
	= new TokenRole("<=", 2);
    public static final TokenRole ADD_ROLE = new TokenRole("+", 2);
    public static final TokenRole SUBTRACT_ROLE = new TokenRole("-", 2);
    public static final TokenRole MULTIPLY_ROLE = new TokenRole("*", 2);
    public static final TokenRole DIVIDE_ROLE = new TokenRole("/", 2);
    public static final TokenRole MODULUS_ROLE = new TokenRole("%", 2);
    public static final TokenRole SHIFT_LEFT_ROLE = new TokenRole("<<", 2);
    public static final TokenRole SHIFT_RIGHT_ROLE = new TokenRole(">>", 2);
    public static final TokenRole UNSIGNED_SHIFT_RIGHT_ROLE
	= new TokenRole(">>>", 2);
    public static final TokenRole ANY_ROLE = new TokenRole("(op)", 2);
    public static final Role LEFT_ROLE
	= new Role("Left", Expression.class, Expression.NULL);
    public static final Role RIGHT_ROLE
	= new Role("Right", Expression.class, Expression.NULL);
    private BinaryOperatorType _operator;
    
    public BinaryOperatorExpression(Expression left,
				    BinaryOperatorType operator,
				    Expression right) {
	super(left.getOffset());
	setLeft(left);
	setOperator(operator);
	setRight(right);
    }
    
    public final BinaryOperatorType getOperator() {
	return _operator;
    }
    
    public final void setOperator(BinaryOperatorType operator) {
	verifyNotFrozen();
	_operator = operator;
    }
    
    public final JavaTokenNode getOperatorToken() {
	return (JavaTokenNode) getChildByRole(getOperatorRole(getOperator()));
    }
    
    public final Expression getLeft() {
	return (Expression) getChildByRole(LEFT_ROLE);
    }
    
    public final void setLeft(Expression value) {
	setChildByRole(LEFT_ROLE, value);
    }
    
    public final Expression getRight() {
	return (Expression) getChildByRole(RIGHT_ROLE);
    }
    
    public final void setRight(Expression value) {
	setChildByRole(RIGHT_ROLE, value);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitBinaryOperatorExpression(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof BinaryOperatorExpression))
	    return false;
    label_1590:
	{
	    BinaryOperatorExpression otherExpression
		= (BinaryOperatorExpression) other;
	    if (otherExpression.isNull()
		|| (otherExpression._operator != _operator
		    && _operator != BinaryOperatorType.ANY
		    && otherExpression._operator != BinaryOperatorType.ANY)
		|| !getLeft().matches(otherExpression.getLeft(), match)
		|| !getRight().matches(otherExpression.getRight(), match))
		PUSH false;
	    else
		PUSH true;
	    break label_1590;
	}
	return POP;
    }
    
    public static TokenRole getOperatorRole(BinaryOperatorType operator) {
	switch (ANONYMOUS CLASS com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression$1.$SwitchMap$com$strobel$decompiler$languages$java$ast$BinaryOperatorType[operator.ordinal()]) {
	case 1:
	    return BITWISE_AND_ROLE;
	case 2:
	    return BITWISE_OR_ROLE;
	case 3:
	    return LOGICAL_AND_ROLE;
	case 4:
	    return LOGICAL_OR_ROLE;
	case 5:
	    return EXCLUSIVE_OR_ROLE;
	case 6:
	    return GREATER_THAN_ROLE;
	case 7:
	    return GREATER_THAN_OR_EQUAL_ROLE;
	case 8:
	    return EQUALITY_ROLE;
	case 9:
	    return IN_EQUALITY_ROLE;
	case 10:
	    return LESS_THAN_ROLE;
	case 11:
	    return LESS_THAN_OR_EQUAL_ROLE;
	case 12:
	    return ADD_ROLE;
	case 13:
	    return SUBTRACT_ROLE;
	case 14:
	    return MULTIPLY_ROLE;
	case 15:
	    return DIVIDE_ROLE;
	case 16:
	    return MODULUS_ROLE;
	case 17:
	    return SHIFT_LEFT_ROLE;
	case 18:
	    return SHIFT_RIGHT_ROLE;
	case 19:
	    return UNSIGNED_SHIFT_RIGHT_ROLE;
	case 20:
	    return ANY_ROLE;
	default:
	    throw new IllegalArgumentException
		      ("Invalid value for BinaryOperatorType.");
	}
    }
}
