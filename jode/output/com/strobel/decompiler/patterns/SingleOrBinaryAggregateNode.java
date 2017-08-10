/* SingleOrBinaryAggregateNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.patterns;
import com.strobel.core.VerifyArgument;
import com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression;
import com.strobel.decompiler.languages.java.ast.BinaryOperatorType;

public final class SingleOrBinaryAggregateNode extends Pattern
{
    private final INode _pattern;
    private final BinaryOperatorType _operator;
    
    public SingleOrBinaryAggregateNode(BinaryOperatorType operator,
				       INode pattern) {
	_pattern = (INode) VerifyArgument.notNull(pattern, "pattern");
	_operator = (BinaryOperatorType) VerifyArgument.notNull(operator,
								"operator");
    }
    
    public boolean matches(INode other, Match match) {
    label_1862:
	{
	    if (!_pattern.matches(other, match)) {
		if (other instanceof BinaryOperatorExpression) {
		    BinaryOperatorExpression binary
			= (BinaryOperatorExpression) other;
		    if (_operator == BinaryOperatorType.ANY
			|| binary.getOperator() == _operator) {
			int checkPoint = match.getCheckPoint();
			if (!matches(binary.getLeft(), match)
			    || !matches(binary.getRight(), match))
			    match.restoreCheckPoint(checkPoint);
			else
			    return true;
		    } else
			return false;
		}
	    } else
		return true;
	}
	return false;
	break label_1862;
    }
}
