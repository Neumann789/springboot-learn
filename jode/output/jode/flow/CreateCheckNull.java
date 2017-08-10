/* CreateCheckNull - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import jode.decompiler.LocalInfo;
import jode.expr.CheckNullOperator;
import jode.expr.CompareUnaryOperator;
import jode.expr.InvokeOperator;
import jode.expr.Operator;
import jode.expr.PopOperator;
import jode.type.Type;

public class CreateCheckNull
{
    public static boolean transformJavac
	(InstructionContainer instructioncontainer,
	 StructuredBlock structuredblock) {
	if (structuredblock.outer instanceof SequentialBlock
	    && instructioncontainer.getInstruction() instanceof Operator
	    && (structuredblock.outer.getSubBlocks()[0]
		instanceof SpecialBlock)) {
	    SpecialBlock specialblock
		= (SpecialBlock) structuredblock.outer.getSubBlocks()[0];
	    if (specialblock.type == SpecialBlock.DUP
		&& specialblock.count == 1 && specialblock.depth == 0) {
		Operator operator
		    = (Operator) instructioncontainer.getInstruction();
		if (operator.getOperator() instanceof PopOperator
		    && (operator.getSubExpressions()[0]
			instanceof InvokeOperator)) {
		    InvokeOperator invokeoperator
			= (InvokeOperator) operator.getSubExpressions()[0];
		    if (invokeoperator.getMethodName().equals("getClass")
			&& invokeoperator.getMethodType().toString()
			       .equals("()Ljava/lang/Class;")) {
			LocalInfo localinfo = new LocalInfo();
			instructioncontainer.setInstruction
			    (new CheckNullOperator(Type.tUObject, localinfo));
			structuredblock.replace(structuredblock.outer);
			return true;
		    }
		    return false;
		}
		return false;
	    }
	    return false;
	}
	return false;
    }
    
    public static boolean transformJikes(IfThenElseBlock ifthenelseblock,
					 StructuredBlock structuredblock) {
    label_949:
	{
	    if (structuredblock.outer instanceof SequentialBlock
		&& (structuredblock.outer.getSubBlocks()[0]
		    instanceof SpecialBlock)
		&& ifthenelseblock.elseBlock == null
		&& ifthenelseblock.thenBlock instanceof ThrowBlock) {
		SpecialBlock specialblock
		    = (SpecialBlock) structuredblock.outer.getSubBlocks()[0];
		if (specialblock.type == SpecialBlock.DUP
		    && specialblock.count == 1 && specialblock.depth == 0) {
		    if (ifthenelseblock.cond instanceof CompareUnaryOperator) {
			CompareUnaryOperator compareunaryoperator
			    = (CompareUnaryOperator) ifthenelseblock.cond;
			if (compareunaryoperator.getOperatorIndex() == 26
			    && compareunaryoperator.getCompareType()
				   .isOfType(Type.tUObject)) {
			    LocalInfo localinfo = new LocalInfo();
			    InstructionBlock instructionblock
				= (new InstructionBlock
				   (new CheckNullOperator(Type.tUObject,
							  localinfo)));
			    ifthenelseblock.flowBlock.removeSuccessor
				(ifthenelseblock.thenBlock.jump);
			    instructionblock.moveJump(ifthenelseblock.jump);
			    if (structuredblock != ifthenelseblock) {
				instructionblock.replace(ifthenelseblock);
				structuredblock.replace(structuredblock.outer);
			    } else {
				instructionblock
				    .replace(structuredblock.outer);
				InstructionBlock instructionblock_0_
				    = instructionblock;
			    }
			} else
			    return false;
		    } else
			return false;
		} else
		    return false;
	    } else
		return false;
	}
	return true;
	break label_949;
    }
}
