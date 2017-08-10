/* AssignmentExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;
import com.strobel.decompiler.patterns.Role;

public class AssignmentExpression extends Expression
{
    public static final Role LEFT_ROLE = BinaryOperatorExpression.LEFT_ROLE;
    public static final Role RIGHT_ROLE = BinaryOperatorExpression.RIGHT_ROLE;
    public static final TokenRole ASSIGN_ROLE = new TokenRole("=", 2);
    public static final TokenRole ADD_ROLE = new TokenRole("+=", 2);
    public static final TokenRole SUBTRACT_ROLE = new TokenRole("-=", 2);
    public static final TokenRole MULTIPLY_ROLE = new TokenRole("*=", 2);
    public static final TokenRole DIVIDE_ROLE = new TokenRole("/=", 2);
    public static final TokenRole MODULUS_ROLE = new TokenRole("%=", 2);
    public static final TokenRole SHIFT_LEFT_ROLE = new TokenRole("<<=", 2);
    public static final TokenRole SHIFT_RIGHT_ROLE = new TokenRole(">>=", 2);
    public static final TokenRole UNSIGNED_SHIFT_RIGHT_ROLE
	= new TokenRole(">>>=", 2);
    public static final TokenRole BITWISE_AND_ROLE = new TokenRole("&=", 2);
    public static final TokenRole BITWISE_OR_ROLE = new TokenRole("|=", 2);
    public static final TokenRole EXCLUSIVE_OR_ROLE = new TokenRole("^=", 2);
    public static final TokenRole ANY_ROLE = new TokenRole("(assign)", 2);
    private AssignmentOperatorType _operator;
    
    public AssignmentExpression(Expression left, Expression right) {
	super(left.getOffset());
	setLeft(left);
	setOperator(AssignmentOperatorType.ASSIGN);
	setRight(right);
    }
    
    public AssignmentExpression(Expression left,
				AssignmentOperatorType operator,
				Expression right) {
	super(left.getOffset());
	setLeft(left);
	setOperator(operator);
	setRight(right);
    }
    
    public final AssignmentOperatorType getOperator() {
	return _operator;
    }
    
    public final void setOperator(AssignmentOperatorType operator) {
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
	return visitor.visitAssignmentExpression(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof AssignmentExpression))
	    return false;
    label_1588:
	{
	    AssignmentExpression otherExpression
		= (AssignmentExpression) other;
	    if (otherExpression.isNull()
		|| (otherExpression._operator != _operator
		    && _operator != AssignmentOperatorType.ANY
		    && otherExpression._operator != AssignmentOperatorType.ANY)
		|| !getLeft().matches(otherExpression.getLeft(), match)
		|| !getRight().matches(otherExpression.getRight(), match))
		PUSH false;
	    else
		PUSH true;
	    break label_1588;
	}
	return POP;
    }
    
    public static TokenRole getOperatorRole(AssignmentOperatorType operator) {
	switch (ANONYMOUS CLASS com.strobel.decompiler.languages.java.ast.AssignmentExpression$1.$SwitchMap$com$strobel$decompiler$languages$java$ast$AssignmentOperatorType[operator.ordinal()]) {
	case 1:
	    return ASSIGN_ROLE;
	case 2:
	    return ADD_ROLE;
	case 3:
	    return SUBTRACT_ROLE;
	case 4:
	    return MULTIPLY_ROLE;
	case 5:
	    return DIVIDE_ROLE;
	case 6:
	    return MODULUS_ROLE;
	case 7:
	    return SHIFT_LEFT_ROLE;
	case 8:
	    return SHIFT_RIGHT_ROLE;
	case 9:
	    return UNSIGNED_SHIFT_RIGHT_ROLE;
	case 10:
	    return BITWISE_AND_ROLE;
	case 11:
	    return BITWISE_OR_ROLE;
	case 12:
	    return EXCLUSIVE_OR_ROLE;
	case 13:
	    return ANY_ROLE;
	default:
	    throw new IllegalArgumentException
		      ("Invalid value for AssignmentOperatorType");
	}
    }
    
    public static BinaryOperatorType getCorrespondingBinaryOperator
	(AssignmentOperatorType operator) {
	switch (ANONYMOUS CLASS com.strobel.decompiler.languages.java.ast.AssignmentExpression$1.$SwitchMap$com$strobel$decompiler$languages$java$ast$AssignmentOperatorType[operator.ordinal()]) {
	case 1:
	    return null;
	case 2:
	    return BinaryOperatorType.ADD;
	case 3:
	    return BinaryOperatorType.SUBTRACT;
	case 4:
	    return BinaryOperatorType.MULTIPLY;
	case 5:
	    return BinaryOperatorType.DIVIDE;
	case 6:
	    return BinaryOperatorType.MODULUS;
	case 7:
	    return BinaryOperatorType.SHIFT_LEFT;
	case 8:
	    return BinaryOperatorType.SHIFT_RIGHT;
	case 9:
	    return BinaryOperatorType.UNSIGNED_SHIFT_RIGHT;
	case 10:
	    return BinaryOperatorType.BITWISE_AND;
	case 11:
	    return BinaryOperatorType.BITWISE_OR;
	case 12:
	    return BinaryOperatorType.EXCLUSIVE_OR;
	case 13:
	    return BinaryOperatorType.ANY;
	default:
	    return null;
	}
    }
    
    public static AssignmentOperatorType getCorrespondingAssignmentOperator
	(BinaryOperatorType operator) {
	switch (ANONYMOUS CLASS com.strobel.decompiler.languages.java.ast.AssignmentExpression$1.$SwitchMap$com$strobel$decompiler$languages$java$ast$BinaryOperatorType[operator.ordinal()]) {
	case 1:
	    return AssignmentOperatorType.ADD;
	case 2:
	    return AssignmentOperatorType.SUBTRACT;
	case 3:
	    return AssignmentOperatorType.MULTIPLY;
	case 4:
	    return AssignmentOperatorType.DIVIDE;
	case 5:
	    return AssignmentOperatorType.MODULUS;
	case 6:
	    return AssignmentOperatorType.SHIFT_LEFT;
	case 7:
	    return AssignmentOperatorType.SHIFT_RIGHT;
	case 8:
	    return AssignmentOperatorType.UNSIGNED_SHIFT_RIGHT;
	case 9:
	    return AssignmentOperatorType.BITWISE_AND;
	case 10:
	    return AssignmentOperatorType.BITWISE_OR;
	case 11:
	    return AssignmentOperatorType.EXCLUSIVE_OR;
	case 12:
	    return AssignmentOperatorType.ANY;
	default:
	    return null;
	}
    }
}
