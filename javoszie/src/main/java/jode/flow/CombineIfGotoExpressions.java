package jode.flow;

import jode.expr.BinaryOperator;
import jode.expr.CombineableOperator;
import jode.expr.Expression;
import jode.type.Type;

public class CombineIfGotoExpressions
{
  public static boolean transform(ConditionalBlock paramConditionalBlock, StructuredBlock paramStructuredBlock)
  {
    if ((paramConditionalBlock.jump == null) || (!(paramStructuredBlock.outer instanceof SequentialBlock))) {
      return false;
    }
    SequentialBlock localSequentialBlock = (SequentialBlock)paramConditionalBlock.outer;
    Expression localExpression2 = paramConditionalBlock.getInstruction();
    Object localObject1 = localExpression2;
    Object localObject2;
    Object localObject3;
    while ((localSequentialBlock.subBlocks[0] instanceof InstructionBlock))
    {
      localObject2 = (InstructionBlock)localSequentialBlock.subBlocks[0];
      if (!(localSequentialBlock.outer instanceof SequentialBlock)) {
        return false;
      }
      localObject3 = ((InstructionBlock)localObject2).getInstruction();
      if ((!(localObject3 instanceof CombineableOperator)) || (((Expression)localObject1).canCombine((CombineableOperator)localObject3) + localExpression2.canCombine((CombineableOperator)localObject3) <= 0)) {
        return false;
      }
      localObject1 = localObject3;
      localSequentialBlock = (SequentialBlock)localSequentialBlock.outer;
    }
    if ((localSequentialBlock.subBlocks[0] instanceof ConditionalBlock))
    {
      localObject2 = (ConditionalBlock)localSequentialBlock.subBlocks[0];
      localObject3 = ((ConditionalBlock)localObject2).trueBlock.jump;
      int i;
      Expression localExpression1;
      if (((Jump)localObject3).destination == paramConditionalBlock.jump.destination)
      {
        i = 32;
        localExpression1 = ((ConditionalBlock)localObject2).getInstruction().negate();
      }
      else if (((Jump)localObject3).destination == paramConditionalBlock.trueBlock.jump.destination)
      {
        i = 33;
        localExpression1 = ((ConditionalBlock)localObject2).getInstruction();
      }
      else
      {
        return false;
      }
      for (localSequentialBlock = (SequentialBlock)paramConditionalBlock.outer; (localSequentialBlock.subBlocks[0] instanceof InstructionBlock); localSequentialBlock = (SequentialBlock)localSequentialBlock.outer)
      {
        localObject4 = (InstructionBlock)localSequentialBlock.subBlocks[0];
        Expression localExpression3 = ((InstructionBlock)localObject4).getInstruction();
        localExpression2 = localExpression2.combine((CombineableOperator)localExpression3);
      }
      paramConditionalBlock.flowBlock.removeSuccessor((Jump)localObject3);
      ((Jump)localObject3).prev.removeJump();
      Object localObject4 = new BinaryOperator(Type.tBoolean, i).addOperand(localExpression2).addOperand(localExpression1);
      paramConditionalBlock.setInstruction((Expression)localObject4);
      paramConditionalBlock.moveDefinitions(localSequentialBlock, paramStructuredBlock);
      paramStructuredBlock.replace(localSequentialBlock);
      return true;
    }
    return false;
  }
}


