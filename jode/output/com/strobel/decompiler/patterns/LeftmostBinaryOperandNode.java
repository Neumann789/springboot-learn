/* LeftmostBinaryOperandNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.patterns;
import com.strobel.core.VerifyArgument;
import com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression;
import com.strobel.decompiler.languages.java.ast.BinaryOperatorType;

public class LeftmostBinaryOperandNode extends Pattern
{
    private final boolean _matchWithoutOperator;
    private final BinaryOperatorType _operatorType;
    private final INode _operandPattern;
    
    public LeftmostBinaryOperandNode(INode pattern) {
	this(pattern, BinaryOperatorType.ANY, false);
    }
    
    public LeftmostBinaryOperandNode(INode pattern, BinaryOperatorType type,
				     boolean matchWithoutOperator) {
	_matchWithoutOperator = matchWithoutOperator;
	_operatorType
	    = (BinaryOperatorType) VerifyArgument.notNull(type, "type");
	_operandPattern = (INode) VerifyArgument.notNull(pattern, "pattern");
    }
    
    public final INode getOperandPattern() {
	return _operandPattern;
    }
    
    public boolean matches(INode other, Match match) {
	if (!_matchWithoutOperator
	    && !(other instanceof BinaryOperatorExpression))
	    return false;
	INode current = other;
    label_1860:
	{
	    for (;;) {
		if (!(current instanceof BinaryOperatorExpression)
		    || (_operatorType != BinaryOperatorType.ANY
			&& (((BinaryOperatorExpression) current).getOperator()
			    != _operatorType))) {
		    if (current == null
			|| !_operandPattern.matches(current, match))
			PUSH false;
		    else
			PUSH true;
		    break label_1860;
		}
		current = ((BinaryOperatorExpression) current).getLeft();
	    }
	}
	return POP;
	break label_1860;
    }
}
