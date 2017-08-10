/* CreateAssignExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import jode.expr.BinaryOperator;
import jode.expr.ConvertOperator;
import jode.expr.Expression;
import jode.expr.LocalLoadOperator;
import jode.expr.LocalStoreOperator;
import jode.expr.Operator;
import jode.expr.StoreInstruction;
import jode.expr.StringAddOperator;
import jode.type.Type;

public class CreateAssignExpression
{
    public static boolean transform(InstructionContainer instructioncontainer,
				    StructuredBlock structuredblock) {
    label_938:
	{
	    if (structuredblock.outer instanceof SequentialBlock
		&& (instructioncontainer.getInstruction()
		    instanceof StoreInstruction)
		&& instructioncontainer.getInstruction().isVoid()) {
		if (!createAssignOp(instructioncontainer, structuredblock)
		    && !createAssignExpression(instructioncontainer,
					       structuredblock))
		    PUSH false;
		else
		    PUSH true;
	    } else
		return false;
	}
	return POP;
	break label_938;
    }
    
    public static boolean createAssignOp
	(InstructionContainer instructioncontainer,
	 StructuredBlock structuredblock) {
	SequentialBlock sequentialblock
	    = (SequentialBlock) structuredblock.outer;
	StoreInstruction storeinstruction
	    = (StoreInstruction) instructioncontainer.getInstruction();
	Expression expression;
	int i;
	boolean bool;
    label_939:
	{
	    if (storeinstruction.isFreeOperator()
		&& !storeinstruction.isOpAssign()) {
		expression = storeinstruction.getSubExpressions()[0];
		i = expression.getFreeOperandCount();
		bool = false;
		if (sequentialblock.subBlocks[0] instanceof SpecialBlock) {
		    SpecialBlock specialblock
			= (SpecialBlock) sequentialblock.subBlocks[0];
		    if (specialblock.type == SpecialBlock.DUP
			&& specialblock.depth == i
			&& (specialblock.count
			    == expression.getType().stackSize())
			&& sequentialblock.outer instanceof SequentialBlock) {
			sequentialblock
			    = (SequentialBlock) sequentialblock.outer;
			bool = true;
		    } else
			return false;
		}
	    } else
		return false;
	}
	InstructionBlock instructionblock;
	Operator operator;
	Type type;
	SpecialBlock specialblock;
    label_941:
	{
	label_940:
	    {
		if (sequentialblock.subBlocks[0] instanceof InstructionBlock) {
		    instructionblock
			= (InstructionBlock) sequentialblock.subBlocks[0];
		    if (instructionblock.getInstruction()
			instanceof Operator) {
			operator
			    = (Operator) instructionblock.getInstruction();
			if (operator.getFreeOperandCount() == i) {
			    type = operator.getType();
			    specialblock = null;
			    if (i > 0) {
				if ((sequentialblock.outer
				     instanceof SequentialBlock)
				    && (sequentialblock.outer.getSubBlocks()[0]
					instanceof SpecialBlock)) {
				    SequentialBlock sequentialblock_0_
					= ((SequentialBlock)
					   sequentialblock.outer);
				    specialblock
					= ((SpecialBlock)
					   sequentialblock_0_.subBlocks[0]);
				    if (specialblock.type != SpecialBlock.DUP
					|| specialblock.depth != 0
					|| specialblock.count != i)
					return false;
				} else
				    return false;
			    }
			} else
			    return false;
		    } else
			return false;
		} else
		    return false;
	    }
	    if (operator instanceof ConvertOperator
		&& operator.getSubExpressions()[0] instanceof Operator
		&& operator.getType().isOfType(expression.getType())) {
		for (operator = (Operator) operator.getSubExpressions()[0];
		     (operator instanceof ConvertOperator
		      && operator.getSubExpressions()[0] instanceof Operator);
		     operator = (Operator) operator.getSubExpressions()[0]) {
		    /* empty */
		}
	    }
	    break label_941;
	}
	Expression expression_1_;
	int i_2_;
    label_947:
	{
	label_946:
	    {
	    label_945:
		{
		    Operator operator_3_;
		    Operator operator_4_;
		label_944:
		    {
			if (!(operator instanceof BinaryOperator)) {
			    Expression expression_5_
				= operator.simplifyString();
			    expression_1_ = expression_5_;
			    operator_3_ = null;
			    operator_4_ = null;
			    for (;;) {
				if (!(expression_5_
				      instanceof StringAddOperator)) {
				    if (operator_3_ != null
					&& expression_5_ instanceof Operator
					&& (storeinstruction.lvalueMatches
					    ((Operator) expression_5_))
					&& ((Operator) expression_5_)
					       .isFreeOperator(i)) {
					if (expression
					    instanceof LocalStoreOperator)
					    ((LocalLoadOperator) expression_5_)
						.getLocalInfo
						().combineWith
						(((LocalStoreOperator)
						  expression)
						     .getLocalInfo());
				    } else
					return false;
				    break label_944;
				}
				operator_4_ = operator_3_;
				operator_3_ = (Operator) expression_5_;
				expression_5_
				    = operator_3_.getSubExpressions()[0];
			    }
			} else {
			    i_2_ = operator.getOperatorIndex();
			    PUSH i_2_;
			label_942:
			    {
				if (operator != null) {
				    /* empty */
				}
				if (POP >= 1) {
				    PUSH i_2_;
				    if (operator != null) {
					/* empty */
				    }
				    if (POP < 12) {
				    label_943:
					{
					    if (operator.getSubExpressions()[0]
						instanceof Operator) {
						Operator operator_6_
						    = ((Operator)
						       (operator
							    .getSubExpressions
							()[0]));
						for (;;) {
						    if (!(operator_6_
							  instanceof ConvertOperator)
							|| !((operator_6_
								  .getSubExpressions
							      ()[0])
							     instanceof Operator)) {
							if ((storeinstruction
								 .lvalueMatches
							     (operator_6_))
							    && (operator_6_
								    .isFreeOperator
								(i))) {
							    if (expression
								instanceof LocalStoreOperator)
								((LocalLoadOperator)
								 operator_6_)
								    .getLocalInfo
								    ()
								    .combineWith
								    (((LocalStoreOperator)
								      expression)
									 .getLocalInfo
								     ());
							} else
							    return false;
							break label_943;
						    }
						    operator_6_
							= ((Operator)
							   (operator_6_
								.getSubExpressions
							    ()[0]));
						}
					    } else
						return false;
					}
					expression_1_
					    = operator.getSubExpressions()[1];
					break label_946;
					break label_943;
				    }
				}
				break label_942;
			    }
			    return false;
			}
		    }
		    if (operator_4_ == null)
			expression_1_ = operator_3_.getSubExpressions()[1];
		    else
			operator_4_.setSubExpressions(0,
						      (operator_3_
							   .getSubExpressions
						       ()[1]));
		    break label_945;
		}
		i_2_ = 1;
	    }
	    if (specialblock != null)
		specialblock.removeBlock();
	    break label_947;
	}
	instructionblock.setInstruction(expression_1_);
	expression.setType(type);
	PUSH storeinstruction;
	if (storeinstruction != null) {
	    /* empty */
	}
    label_948:
	{
	    ((StoreInstruction) POP).makeOpAssign(12 + i_2_);
	    if (bool)
		storeinstruction.makeNonVoid();
	    break label_948;
	}
	structuredblock.replace(sequentialblock.subBlocks[1]);
	return true;
	break label_944;
	break label_940;
	break label_939;
    }
    
    public static boolean createAssignExpression
	(InstructionContainer instructioncontainer,
	 StructuredBlock structuredblock) {
	SequentialBlock sequentialblock
	    = (SequentialBlock) structuredblock.outer;
	StoreInstruction storeinstruction
	    = (StoreInstruction) instructioncontainer.getInstruction();
	if (!(sequentialblock.subBlocks[0] instanceof SpecialBlock)
	    || !storeinstruction.isFreeOperator())
	    return false;
	Expression expression = storeinstruction.getSubExpressions()[0];
	SpecialBlock specialblock
	    = (SpecialBlock) sequentialblock.subBlocks[0];
	if (specialblock.type == SpecialBlock.DUP
	    && specialblock.depth == expression.getFreeOperandCount()
	    && specialblock.count == expression.getType().stackSize()) {
	    specialblock.removeBlock();
	    storeinstruction.makeNonVoid();
	    return true;
	}
	return false;
    }
}
