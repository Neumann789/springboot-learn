package jode.flow;

import java.io.PrintWriter;
import jode.GlobalOptions;
import jode.expr.CombineableOperator;
import jode.expr.Expression;

public class CreateExpression
{
  public static boolean transform(InstructionContainer paramInstructionContainer, StructuredBlock paramStructuredBlock)
  {
    int i = paramInstructionContainer.getInstruction().getFreeOperandCount();
    if (i == 0) {
      return false;
    }
    if (!(paramStructuredBlock.outer instanceof SequentialBlock)) {
      return false;
    }
    SequentialBlock localSequentialBlock1 = (SequentialBlock)paramStructuredBlock.outer;
    Expression localExpression1 = paramInstructionContainer.getInstruction();
    Expression localExpression2;
    for (;;)
    {
      if (!(localSequentialBlock1.subBlocks[0] instanceof InstructionBlock)) {
        return false;
      }
      localExpression2 = ((InstructionBlock)localSequentialBlock1.subBlocks[0]).getInstruction();
      if (!localExpression2.isVoid()) {
        break;
      }
      if ((localExpression2.getFreeOperandCount() > 0) || (!(localExpression2 instanceof CombineableOperator)) || (localExpression1.canCombine((CombineableOperator)localExpression2) <= 0)) {
        return false;
      }
      SequentialBlock localSequentialBlock2 = localSequentialBlock1;
      while (localSequentialBlock2 != paramStructuredBlock.outer)
      {
        localSequentialBlock2 = (SequentialBlock)localSequentialBlock2.subBlocks[1];
        if (((InstructionBlock)localSequentialBlock2.subBlocks[0]).getInstruction().hasSideEffects(localExpression2)) {
          return false;
        }
      }
      if (!(localSequentialBlock1.outer instanceof SequentialBlock)) {
        return false;
      }
      localSequentialBlock1 = (SequentialBlock)localSequentialBlock1.outer;
    }
    localSequentialBlock1 = (SequentialBlock)paramStructuredBlock.outer;
    localExpression1 = paramInstructionContainer.getInstruction();
    for (;;)
    {
      localExpression2 = ((InstructionBlock)localSequentialBlock1.subBlocks[0]).getInstruction();
      if (!localExpression2.isVoid())
      {
        localExpression1 = localExpression1.addOperand(localExpression2);
        break;
      }
      localExpression1 = localExpression1.combine((CombineableOperator)localExpression2);
      localSequentialBlock1 = (SequentialBlock)localSequentialBlock1.outer;
    }
    if ((GlobalOptions.verboseLevel > 0) && (localExpression1.getFreeOperandCount() == 0)) {
      GlobalOptions.err.print('x');
    }
    paramInstructionContainer.setInstruction(localExpression1);
    paramInstructionContainer.moveDefinitions(localSequentialBlock1, paramStructuredBlock);
    paramStructuredBlock.replace(localSequentialBlock1);
    return true;
  }
}


