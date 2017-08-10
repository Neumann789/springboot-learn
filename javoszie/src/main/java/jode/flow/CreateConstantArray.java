package jode.flow;

import java.io.PrintWriter;
import jode.GlobalOptions;
import jode.expr.ArrayStoreOperator;
import jode.expr.ConstOperator;
import jode.expr.ConstantArrayOperator;
import jode.expr.Expression;
import jode.expr.NewArrayOperator;
import jode.expr.NopOperator;
import jode.expr.StoreInstruction;

public class CreateConstantArray
{
  public static boolean transform(InstructionContainer paramInstructionContainer, StructuredBlock paramStructuredBlock)
  {
    if ((paramStructuredBlock.outer instanceof SequentialBlock))
    {
      SequentialBlock localSequentialBlock = (SequentialBlock)paramStructuredBlock.outer;
      if ((!(paramInstructionContainer.getInstruction() instanceof StoreInstruction)) || (paramInstructionContainer.getInstruction().getFreeOperandCount() != 1) || (!(localSequentialBlock.subBlocks[0] instanceof SpecialBlock)) || (!(localSequentialBlock.outer instanceof SequentialBlock))) {
        return false;
      }
      StoreInstruction localStoreInstruction = (StoreInstruction)paramInstructionContainer.getInstruction();
      if (!(localStoreInstruction.getLValue() instanceof ArrayStoreOperator)) {
        return false;
      }
      ArrayStoreOperator localArrayStoreOperator = (ArrayStoreOperator)localStoreInstruction.getLValue();
      if ((!(localArrayStoreOperator.getSubExpressions()[0] instanceof NopOperator)) || (!(localArrayStoreOperator.getSubExpressions()[1] instanceof ConstOperator))) {
        return false;
      }
      Expression localExpression = localStoreInstruction.getSubExpressions()[1];
      ConstOperator localConstOperator1 = (ConstOperator)localArrayStoreOperator.getSubExpressions()[1];
      SpecialBlock localSpecialBlock = (SpecialBlock)localSequentialBlock.subBlocks[0];
      localSequentialBlock = (SequentialBlock)localSequentialBlock.outer;
      if ((localSpecialBlock.type != SpecialBlock.DUP) || (localSpecialBlock.depth != 0) || (localSpecialBlock.count != 1) || (!(localConstOperator1.getValue() instanceof Integer)) || (!(localSequentialBlock.subBlocks[0] instanceof InstructionBlock))) {
        return false;
      }
      int i = ((Integer)localConstOperator1.getValue()).intValue();
      InstructionBlock localInstructionBlock = (InstructionBlock)localSequentialBlock.subBlocks[0];
      Object localObject;
      if ((localInstructionBlock.getInstruction() instanceof NewArrayOperator))
      {
        localObject = (NewArrayOperator)localInstructionBlock.getInstruction();
        if ((((NewArrayOperator)localObject).getDimensions() != 1) || (!(localObject.getSubExpressions()[0] instanceof ConstOperator))) {
          return false;
        }
        ConstOperator localConstOperator2 = (ConstOperator)localObject.getSubExpressions()[0];
        if (!(localConstOperator2.getValue() instanceof Integer)) {
          return false;
        }
        int j = ((Integer)localConstOperator2.getValue()).intValue();
        if (j <= i) {
          return false;
        }
        if (GlobalOptions.verboseLevel > 0) {
          GlobalOptions.err.print('a');
        }
        ConstantArrayOperator localConstantArrayOperator = new ConstantArrayOperator(((NewArrayOperator)localObject).getType(), j);
        localConstantArrayOperator.setValue(i, localExpression);
        paramInstructionContainer.setInstruction(localConstantArrayOperator);
        paramInstructionContainer.moveDefinitions(localSequentialBlock, paramStructuredBlock);
        paramStructuredBlock.replace(localSequentialBlock);
        return true;
      }
      if ((localInstructionBlock.getInstruction() instanceof ConstantArrayOperator))
      {
        localObject = (ConstantArrayOperator)localInstructionBlock.getInstruction();
        if (((ConstantArrayOperator)localObject).setValue(i, localExpression))
        {
          paramInstructionContainer.setInstruction((Expression)localObject);
          paramInstructionContainer.moveDefinitions(localSequentialBlock, paramStructuredBlock);
          paramStructuredBlock.replace(localSequentialBlock);
          return true;
        }
      }
    }
    return false;
  }
}


