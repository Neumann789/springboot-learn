/* CreateClassField - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import jode.expr.ClassFieldOperator;
import jode.expr.CompareUnaryOperator;
import jode.expr.ConstOperator;
import jode.expr.GetFieldOperator;
import jode.expr.InvokeOperator;
import jode.expr.Operator;
import jode.expr.PutFieldOperator;
import jode.expr.StoreInstruction;
import jode.type.Type;

public class CreateClassField
{
    public static boolean transform(IfThenElseBlock ifthenelseblock,
				    StructuredBlock structuredblock) {
    label_950:
	{
	    if (ifthenelseblock.cond instanceof CompareUnaryOperator
		&& ((Operator) ifthenelseblock.cond).getOperatorIndex() == 26
		&& ifthenelseblock.thenBlock instanceof InstructionBlock
		&& ifthenelseblock.elseBlock == null) {
		if (ifthenelseblock.thenBlock.jump == null
		    || (ifthenelseblock.jump != null
			&& (ifthenelseblock.jump.destination
			    == ifthenelseblock.thenBlock.jump.destination))) {
		    CompareUnaryOperator compareunaryoperator
			= (CompareUnaryOperator) ifthenelseblock.cond;
		    jode.expr.Expression expression
			= ((InstructionBlock) ifthenelseblock.thenBlock)
			      .getInstruction();
		    if ((compareunaryoperator.getSubExpressions()[0]
			 instanceof GetFieldOperator)
			&& expression instanceof StoreInstruction) {
			StoreInstruction storeinstruction
			    = (StoreInstruction) expression;
			if (storeinstruction.getLValue()
			    instanceof PutFieldOperator) {
			    PutFieldOperator putfieldoperator
				= ((PutFieldOperator)
				   storeinstruction.getLValue());
			    if (putfieldoperator.getField() != null
				&& (putfieldoperator.matches
				    ((GetFieldOperator) (compareunaryoperator
							     .getSubExpressions
							 ()[0])))
				&& (storeinstruction.getSubExpressions()[1]
				    instanceof InvokeOperator)) {
				InvokeOperator invokeoperator
				    = ((InvokeOperator)
				       (storeinstruction.getSubExpressions()
					[1]));
				if (invokeoperator.isGetClass()) {
				label_951:
				    {
					jode.expr.Expression expression_0_
					    = (invokeoperator.getSubExpressions
					       ()[0]);
					if ((expression_0_
					     instanceof ConstOperator)
					    && (((ConstOperator) expression_0_)
						    .getValue()
						instanceof String)) {
					    String string
						= (String) ((ConstOperator)
							    expression_0_)
							       .getValue();
					    if (putfieldoperator.getField()
						    .setClassConstant
						(string)) {
						PUSH compareunaryoperator;
						PUSH false;
						PUSH new ClassFieldOperator;
						DUP
						if (string.charAt(0) != '[')
						    PUSH Type.tClass(string);
						else
						    PUSH Type.tType(string);
					    }
					}
					break label_951;
				    }
				    return false;
				} else
				    return false;
			    } else
				return false;
			} else
			    return false;
		    } else
			return false;
		} else
		    return false;
	    } else
		return false;
	}
	((UNCONSTRUCTED)POP).ClassFieldOperator(POP);
	((CompareUnaryOperator) POP).setSubExpressions(POP, POP);
	EmptyBlock emptyblock = new EmptyBlock();
	emptyblock.moveJump(ifthenelseblock.thenBlock.jump);
	ifthenelseblock.setThenBlock(emptyblock);
	return true;
	break label_950;
    }
}
