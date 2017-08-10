/* UsageClassifier - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.analysis;
import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
import com.strobel.decompiler.languages.java.ast.AssignmentOperatorType;
import com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression;
import com.strobel.decompiler.languages.java.ast.Expression;
import com.strobel.decompiler.languages.java.ast.UnaryOperatorExpression;

public final class UsageClassifier
{
    public static UsageType getUsageType(Expression expression) {
	com.strobel.decompiler.languages.java.ast.AstNode parent
	    = expression.getParent();
    label_1582:
	{
	    if (!(parent instanceof BinaryOperatorExpression)) {
		if (!(parent instanceof AssignmentExpression)) {
		    if (parent instanceof UnaryOperatorExpression) {
			UnaryOperatorExpression unary
			    = (UnaryOperatorExpression) parent;
			switch (ANONYMOUS CLASS com.strobel.decompiler.languages.java.analysis.UsageClassifier$1.$SwitchMap$com$strobel$decompiler$languages$java$ast$UnaryOperatorType[unary.getOperator().ordinal()]) {
			case 1:
			    return UsageType.ReadWrite;
			case 2:
			case 3:
			case 4:
			case 5:
			    return UsageType.Read;
			case 6:
			case 7:
			case 8:
			case 9:
			    return UsageType.ReadWrite;
			}
		    }
		} else {
		    if (!expression.matches(((AssignmentExpression) parent)
						.getLeft()))
			return UsageType.Read;
		    AssignmentOperatorType operator
			= ((AssignmentExpression) parent).getOperator();
		    if (operator != AssignmentOperatorType.ANY
			&& operator != AssignmentOperatorType.ASSIGN)
			return UsageType.ReadWrite;
		    return UsageType.Write;
		}
	    } else
		return UsageType.Read;
	}
	return UsageType.Read;
	break label_1582;
    }
}
