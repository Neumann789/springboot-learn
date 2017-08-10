/* CreateForInitializer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import jode.GlobalOptions;
import jode.expr.CombineableOperator;

public class CreateForInitializer
{
    public static boolean transform(LoopBlock loopblock,
				    StructuredBlock structuredblock) {
	SequentialBlock sequentialblock;
    label_954:
	{
	    if (structuredblock.outer instanceof SequentialBlock) {
		sequentialblock = (SequentialBlock) structuredblock.outer;
		if (sequentialblock.subBlocks[0] instanceof InstructionBlock) {
		    InstructionBlock instructionblock
			= (InstructionBlock) sequentialblock.subBlocks[0];
		    if (instructionblock.getInstruction().isVoid()
			&& (instructionblock.getInstruction()
			    instanceof CombineableOperator)
			&& loopblock.conditionMatches((CombineableOperator)
						      instructionblock
							  .getInstruction())) {
			if (GlobalOptions.verboseLevel > 0)
			    GlobalOptions.err.print('f');
		    } else
			return false;
		} else
		    return false;
	    } else
		return false;
	}
	loopblock.setInit((InstructionBlock) sequentialblock.subBlocks[0]);
	return true;
	break label_954;
    }
}
