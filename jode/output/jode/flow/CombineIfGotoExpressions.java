/* CombineIfGotoExpressions - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import jode.expr.BinaryOperator;
import jode.expr.CombineableOperator;
import jode.expr.Expression;
import jode.type.Type;

public class CombineIfGotoExpressions
{
    public static boolean transform(ConditionalBlock conditionalblock,
				    StructuredBlock structuredblock) {
	Expression expression;
	Jump jump;
	int i;
	Expression expression_0_;
    label_933:
	{
	    if (conditionalblock.jump != null
		&& structuredblock.outer instanceof SequentialBlock) {
		SequentialBlock sequentialblock
		    = (SequentialBlock) conditionalblock.outer;
		expression = conditionalblock.getInstruction();
		Expression expression_1_ = expression;
		for (;;) {
		    if (!(sequentialblock.subBlocks[0]
			  instanceof InstructionBlock)) {
			if (!(sequentialblock.subBlocks[0]
			      instanceof ConditionalBlock))
			    return false;
			ConditionalBlock conditionalblock_2_
			    = (ConditionalBlock) sequentialblock.subBlocks[0];
			jump = conditionalblock_2_.trueBlock.jump;
			if (jump.destination
			    != conditionalblock.jump.destination) {
			    if (jump.destination
				!= conditionalblock.trueBlock.jump.destination)
				return false;
			    i = 33;
			    expression_0_
				= conditionalblock_2_.getInstruction();
			} else {
			    i = 32;
			    expression_0_ = conditionalblock_2_.getInstruction
						().negate();
			}
		    } else {
			InstructionBlock instructionblock
			    = (InstructionBlock) sequentialblock.subBlocks[0];
			if (sequentialblock.outer instanceof SequentialBlock) {
			    Expression expression_3_
				= instructionblock.getInstruction();
			    if (expression_3_ instanceof CombineableOperator
				&& (((expression_1_.canCombine
				      ((CombineableOperator) expression_3_))
				     + (expression.canCombine
					((CombineableOperator) expression_3_)))
				    > 0)) {
				expression_1_ = expression_3_;
				sequentialblock
				    = (SequentialBlock) sequentialblock.outer;
			    }
			    return false;
			}
			return false;
		    }
		    break label_933;
		}
	    } else
		return false;
	}
	SequentialBlock sequentialblock
	    = (SequentialBlock) conditionalblock.outer;
	for (;;) {
	    if (!(sequentialblock.subBlocks[0] instanceof InstructionBlock)) {
		conditionalblock.flowBlock.removeSuccessor(jump);
		jump.prev.removeJump();
		Expression expression_4_
		    = new BinaryOperator(Type.tBoolean, i).addOperand
			  (expression).addOperand(expression_0_);
		conditionalblock.setInstruction(expression_4_);
		conditionalblock.moveDefinitions(sequentialblock,
						 structuredblock);
		structuredblock.replace(sequentialblock);
		return true;
	    }
	    InstructionBlock instructionblock
		= (InstructionBlock) sequentialblock.subBlocks[0];
	    Expression expression_5_ = instructionblock.getInstruction();
	    expression
		= expression.combine((CombineableOperator) expression_5_);
	    sequentialblock = (SequentialBlock) sequentialblock.outer;
	}
	break label_933;
    }
}
