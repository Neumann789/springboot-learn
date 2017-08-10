/* CompleteSynchronized - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import jode.GlobalOptions;
import jode.expr.LocalLoadOperator;
import jode.expr.LocalStoreOperator;
import jode.expr.MonitorEnterOperator;
import jode.expr.StoreInstruction;

public class CompleteSynchronized
{
    public static boolean enter(SynchronizedBlock synchronizedblock,
				StructuredBlock structuredblock) {
    label_934:
	{
	    if (structuredblock.outer instanceof SequentialBlock) {
		SequentialBlock sequentialblock
		    = (SequentialBlock) synchronizedblock.outer;
		if (sequentialblock.subBlocks[0] instanceof InstructionBlock) {
		    jode.expr.Expression expression
			= ((InstructionBlock) sequentialblock.subBlocks[0])
			      .getInstruction();
		    if (expression instanceof MonitorEnterOperator) {
			jode.expr.Expression expression_0_
			    = (((MonitorEnterOperator) expression)
				   .getSubExpressions
			       ()[0]);
			if (expression_0_ instanceof LocalLoadOperator
			    && (((LocalLoadOperator) expression_0_)
				    .getLocalInfo()
				== synchronizedblock.local.getLocalInfo())) {
			    if (GlobalOptions.verboseLevel > 0)
				GlobalOptions.err.print('s');
			} else
			    return false;
		    } else
			return false;
		} else
		    return false;
	    } else
		return false;
	}
	synchronizedblock.isEntered = true;
	synchronizedblock.moveDefinitions(structuredblock.outer,
					  structuredblock);
	structuredblock.replace(structuredblock.outer);
	return true;
	break label_934;
    }
    
    public static boolean combineObject(SynchronizedBlock synchronizedblock,
					StructuredBlock structuredblock) {
	if (structuredblock.outer instanceof SequentialBlock) {
	    SequentialBlock sequentialblock
		= (SequentialBlock) structuredblock.outer;
	    if (sequentialblock.subBlocks[0] instanceof InstructionBlock) {
		InstructionBlock instructionblock
		    = (InstructionBlock) sequentialblock.subBlocks[0];
		if (instructionblock.getInstruction()
		    instanceof StoreInstruction) {
		    StoreInstruction storeinstruction
			= (StoreInstruction) instructionblock.getInstruction();
		    if (storeinstruction.getLValue()
			instanceof LocalStoreOperator) {
			LocalStoreOperator localstoreoperator
			    = ((LocalStoreOperator)
			       storeinstruction.getLValue());
			if ((localstoreoperator.getLocalInfo()
			     == synchronizedblock.local.getLocalInfo())
			    && (storeinstruction.getSubExpressions()[1]
				!= null)) {
			    synchronizedblock.object
				= storeinstruction.getSubExpressions()[1];
			    synchronizedblock.moveDefinitions((structuredblock
							       .outer),
							      structuredblock);
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
	return false;
    }
}
