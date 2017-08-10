package jode.flow;

import java.io.PrintWriter;
import jode.GlobalOptions;
import jode.expr.CompareUnaryOperator;
import jode.expr.ConstOperator;
import jode.expr.Expression;
import jode.expr.IfThenElseOperator;
import jode.type.Type;

public class CreateIfThenElseOperator
{
  private static boolean createFunnyHelper(FlowBlock paramFlowBlock1, FlowBlock paramFlowBlock2, StructuredBlock paramStructuredBlock)
  {
    if (((paramStructuredBlock instanceof InstructionBlock)) && (!((InstructionBlock)paramStructuredBlock).getInstruction().isVoid())) {
      return true;
    }
    Object localObject;
    Expression localExpression;
    if ((paramStructuredBlock instanceof IfThenElseBlock))
    {
      localObject = (IfThenElseBlock)paramStructuredBlock;
      if (((IfThenElseBlock)localObject).elseBlock == null) {
        return false;
      }
      if (((!createFunnyHelper(paramFlowBlock1, paramFlowBlock2, ((IfThenElseBlock)localObject).thenBlock) ? 1 : 0) | (!createFunnyHelper(paramFlowBlock1, paramFlowBlock2, ((IfThenElseBlock)localObject).elseBlock) ? 1 : 0)) != 0) {
        return false;
      }
      if (GlobalOptions.verboseLevel > 0) {
        GlobalOptions.err.print('?');
      }
      localExpression = new IfThenElseOperator(Type.tBoolean).addOperand(((InstructionBlock)((IfThenElseBlock)localObject).elseBlock).getInstruction()).addOperand(((InstructionBlock)((IfThenElseBlock)localObject).thenBlock).getInstruction()).addOperand(((IfThenElseBlock)localObject).cond);
      ((InstructionBlock)((IfThenElseBlock)localObject).thenBlock).setInstruction(localExpression);
      ((IfThenElseBlock)localObject).thenBlock.moveDefinitions((StructuredBlock)localObject, null);
      ((IfThenElseBlock)localObject).thenBlock.replace((StructuredBlock)localObject);
      return true;
    }
    if (((paramStructuredBlock instanceof SequentialBlock)) && ((paramStructuredBlock.getSubBlocks()[0] instanceof ConditionalBlock)) && ((paramStructuredBlock.getSubBlocks()[1] instanceof InstructionBlock)))
    {
      localObject = (ConditionalBlock)paramStructuredBlock.getSubBlocks()[0];
      InstructionBlock localInstructionBlock = (InstructionBlock)paramStructuredBlock.getSubBlocks()[1];
      if (!(localInstructionBlock.getInstruction() instanceof ConstOperator)) {
        return false;
      }
      ConstOperator localConstOperator = (ConstOperator)localInstructionBlock.getInstruction();
      if ((((ConditionalBlock)localObject).trueBlock.jump.destination == paramFlowBlock1) && (localConstOperator.getValue().equals(new Integer(0))))
      {
        localExpression = ((ConditionalBlock)localObject).getInstruction();
        ((ConditionalBlock)localObject).flowBlock.removeSuccessor(((ConditionalBlock)localObject).trueBlock.jump);
        ((ConditionalBlock)localObject).trueBlock.removeJump();
        localInstructionBlock.setInstruction(localExpression);
        localInstructionBlock.moveDefinitions(paramStructuredBlock, null);
        localInstructionBlock.replace(paramStructuredBlock);
        return true;
      }
    }
    return false;
  }
  
  public static boolean createFunny(ConditionalBlock paramConditionalBlock, StructuredBlock paramStructuredBlock)
  {
    if ((paramConditionalBlock.jump == null) || (!(paramConditionalBlock.getInstruction() instanceof CompareUnaryOperator)) || (!(paramStructuredBlock.outer instanceof SequentialBlock)) || (!(paramStructuredBlock.outer.getSubBlocks()[0] instanceof IfThenElseBlock))) {
      return false;
    }
    CompareUnaryOperator localCompareUnaryOperator = (CompareUnaryOperator)paramConditionalBlock.getInstruction();
    FlowBlock localFlowBlock1;
    FlowBlock localFlowBlock2;
    if (localCompareUnaryOperator.getOperatorIndex() == 26)
    {
      localFlowBlock1 = paramConditionalBlock.jump.destination;
      localFlowBlock2 = paramConditionalBlock.trueBlock.jump.destination;
    }
    else if (localCompareUnaryOperator.getOperatorIndex() == 27)
    {
      localFlowBlock2 = paramConditionalBlock.jump.destination;
      localFlowBlock1 = paramConditionalBlock.trueBlock.jump.destination;
    }
    else
    {
      return false;
    }
    Expression[] arrayOfExpression = new Expression[3];
    SequentialBlock localSequentialBlock = (SequentialBlock)paramStructuredBlock.outer;
    return createFunnyHelper(localFlowBlock1, localFlowBlock2, localSequentialBlock.subBlocks[0]);
  }
  
  public static boolean create(InstructionContainer paramInstructionContainer, StructuredBlock paramStructuredBlock)
  {
    if ((paramInstructionContainer.jump == null) || (!(paramStructuredBlock.outer instanceof SequentialBlock))) {
      return false;
    }
    SequentialBlock localSequentialBlock = (SequentialBlock)paramStructuredBlock.outer;
    if (!(localSequentialBlock.subBlocks[0] instanceof IfThenElseBlock)) {
      return false;
    }
    IfThenElseBlock localIfThenElseBlock = (IfThenElseBlock)localSequentialBlock.subBlocks[0];
    if ((!(localIfThenElseBlock.thenBlock instanceof InstructionBlock)) || (localIfThenElseBlock.thenBlock.jump == null) || (localIfThenElseBlock.thenBlock.jump.destination != paramInstructionContainer.jump.destination) || (localIfThenElseBlock.elseBlock != null)) {
      return false;
    }
    InstructionBlock localInstructionBlock = (InstructionBlock)localIfThenElseBlock.thenBlock;
    Expression localExpression2 = localInstructionBlock.getInstruction();
    if ((localExpression2.isVoid()) || (localExpression2.getFreeOperandCount() > 0)) {
      return false;
    }
    Expression localExpression3 = paramInstructionContainer.getInstruction();
    if ((localExpression3.isVoid()) || (localExpression3.getFreeOperandCount() > 0)) {
      return false;
    }
    Expression localExpression1 = localIfThenElseBlock.cond;
    if (GlobalOptions.verboseLevel > 0) {
      GlobalOptions.err.print('?');
    }
    localInstructionBlock.flowBlock.removeSuccessor(localInstructionBlock.jump);
    localInstructionBlock.removeJump();
    IfThenElseOperator localIfThenElseOperator = new IfThenElseOperator(Type.tSuperType(localExpression2.getType()).intersection(Type.tSuperType(localExpression3.getType())));
    localIfThenElseOperator.addOperand(localExpression3);
    localIfThenElseOperator.addOperand(localExpression2);
    localIfThenElseOperator.addOperand(localExpression1);
    paramInstructionContainer.setInstruction(localIfThenElseOperator);
    paramInstructionContainer.moveDefinitions(paramStructuredBlock.outer, paramStructuredBlock);
    paramStructuredBlock.replace(paramStructuredBlock.outer);
    return true;
  }
}


