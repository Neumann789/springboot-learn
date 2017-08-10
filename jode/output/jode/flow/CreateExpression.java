/* CreateExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import jode.GlobalOptions;
import jode.expr.CombineableOperator;
import jode.expr.Expression;

public class CreateExpression
{
    public static boolean transform(InstructionContainer instructioncontainer,
				    StructuredBlock structuredblock) {
	int i = instructioncontainer.getInstruction().getFreeOperandCount();
	SequentialBlock sequentialblock;
	Expression expression;
    label_953:
	{
	    if (i != 0) {
		if (structuredblock.outer instanceof SequentialBlock) {
		    sequentialblock = (SequentialBlock) structuredblock.outer;
		    expression = instructioncontainer.getInstruction();
		    for (;;) {
			if (sequentialblock.subBlocks[0]
			    instanceof InstructionBlock) {
			    Expression expression_0_
				= ((InstructionBlock)
				   sequentialblock.subBlocks[0])
				      .getInstruction();
			    if (!expression_0_.isVoid()) {
				sequentialblock
				    = (SequentialBlock) structuredblock.outer;
				expression
				    = instructioncontainer.getInstruction();
				for (;;) {
				    expression_0_
					= ((InstructionBlock)
					   sequentialblock.subBlocks[0])
					      .getInstruction();
				    if (expression_0_.isVoid()) {
					expression = (expression.combine
						      ((CombineableOperator)
						       expression_0_));
					sequentialblock
					    = ((SequentialBlock)
					       sequentialblock.outer);
				    }
				    expression
					= expression.addOperand(expression_0_);
				    if (GlobalOptions.verboseLevel > 0
					&& (expression.getFreeOperandCount()
					    == 0))
					GlobalOptions.err.print('x');
				    break label_953;
				}
			    } else {
				if (expression_0_.getFreeOperandCount() <= 0
				    && (expression_0_
					instanceof CombineableOperator)
				    && ((expression.canCombine
					 ((CombineableOperator) expression_0_))
					> 0)) {
				    SequentialBlock sequentialblock_1_
					= sequentialblock;
				    for (;;) {
					if (sequentialblock_1_
					    == structuredblock.outer) {
					    if (sequentialblock.outer
						instanceof SequentialBlock)
						sequentialblock
						    = ((SequentialBlock)
						       sequentialblock.outer);
					    return false;
					}
					sequentialblock_1_
					    = ((SequentialBlock)
					       (sequentialblock_1_.subBlocks
						[1]));
					IF (!((InstructionBlock)
					      sequentialblock_1_.subBlocks[0])
						 .getInstruction
						 ().hasSideEffects
					     (expression_0_))
					    /* empty */
					return false;
				    }
				}
				return false;
			    }
			} else
			    return false;
			break label_953;
		    }
		} else
		    return false;
	    } else
		return false;
	}
	instructioncontainer.setInstruction(expression);
	instructioncontainer.moveDefinitions(sequentialblock, structuredblock);
	structuredblock.replace(sequentialblock);
	return true;
	break label_953;
    }
}
