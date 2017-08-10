/* FlattenElseIfStatementsTransform - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
import com.strobel.decompiler.languages.java.ast.BlockStatement;
import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
import com.strobel.decompiler.languages.java.ast.Expression;
import com.strobel.decompiler.languages.java.ast.IfElseStatement;
import com.strobel.decompiler.languages.java.ast.Statement;
import com.strobel.decompiler.languages.java.ast.UnaryOperatorExpression;
import com.strobel.decompiler.languages.java.ast.UnaryOperatorType;

public class FlattenElseIfStatementsTransform extends ContextTrackingVisitor
{
    public FlattenElseIfStatementsTransform(DecompilerContext context) {
	super(context);
    }
    
    public Void visitIfElseStatement(IfElseStatement node, Void data) {
	super.visitIfElseStatement(node, data);
	Statement trueStatement = node.getTrueStatement();
	Statement falseStatement = node.getFalseStatement();
    label_1721:
	{
	    if (!(trueStatement instanceof BlockStatement)
		|| !(falseStatement instanceof BlockStatement)
		|| !((BlockStatement) trueStatement).getStatements().isEmpty()
		|| ((BlockStatement) falseStatement).getStatements()
		       .isEmpty()) {
		if (falseStatement instanceof BlockStatement) {
		    BlockStatement falseBlock
			= (BlockStatement) falseStatement;
		    AstNodeCollection falseStatements
			= falseBlock.getStatements();
		    if (falseStatements.hasSingleElement()
			&& (falseStatements.firstOrNullObject()
			    instanceof IfElseStatement)) {
			Statement elseIf
			    = (Statement) falseStatements.firstOrNullObject();
			elseIf.remove();
			falseStatement.replaceWith(elseIf);
			return null;
		    }
		}
	    } else {
		Expression condition = node.getCondition();
		condition.remove();
		node.setCondition
		    (new UnaryOperatorExpression(UnaryOperatorType.NOT,
						 condition));
		falseStatement.remove();
		node.setTrueStatement(falseStatement);
		node.setFalseStatement(null);
		return null;
	    }
	}
	return null;
	break label_1721;
    }
}
