package jode.flow;

import java.io.PrintWriter;
import jode.GlobalOptions;
import jode.expr.CombineableOperator;
import jode.expr.Expression;

public class CreateForInitializer
{
  public static boolean transform(LoopBlock paramLoopBlock, StructuredBlock paramStructuredBlock)
  {
    if (!(paramStructuredBlock.outer instanceof SequentialBlock)) {
      return false;
    }
    SequentialBlock localSequentialBlock = (SequentialBlock)paramStructuredBlock.outer;
    if (!(localSequentialBlock.subBlocks[0] instanceof InstructionBlock)) {
      return false;
    }
    InstructionBlock localInstructionBlock = (InstructionBlock)localSequentialBlock.subBlocks[0];
    if ((!localInstructionBlock.getInstruction().isVoid()) || (!(localInstructionBlock.getInstruction() instanceof CombineableOperator)) || (!paramLoopBlock.conditionMatches((CombineableOperator)localInstructionBlock.getInstruction()))) {
      return false;
    }
    if (GlobalOptions.verboseLevel > 0) {
      GlobalOptions.err.print('f');
    }
    paramLoopBlock.setInit((InstructionBlock)localSequentialBlock.subBlocks[0]);
    return true;
  }
}


