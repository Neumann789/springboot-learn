package jode.flow;

import jode.expr.BinaryOperator;
import jode.expr.ConstOperator;
import jode.expr.Expression;
import jode.expr.IIncOperator;
import jode.expr.LocalLoadOperator;
import jode.expr.NopOperator;
import jode.expr.Operator;
import jode.expr.PrePostFixOperator;
import jode.expr.StoreInstruction;
import jode.type.Type;

public class CreatePrePostIncExpression
{
  public static boolean transform(InstructionContainer paramInstructionContainer, StructuredBlock paramStructuredBlock)
  {
    return (createLocalPrePostInc(paramInstructionContainer, paramStructuredBlock)) || (createPostInc(paramInstructionContainer, paramStructuredBlock));
  }
  
  public static boolean createLocalPrePostInc(InstructionContainer paramInstructionContainer, StructuredBlock paramStructuredBlock)
  {
    if ((!(paramStructuredBlock.outer instanceof SequentialBlock)) || (!(paramStructuredBlock.outer.getSubBlocks()[0] instanceof InstructionBlock))) {
      return false;
    }
    Expression localExpression1 = ((InstructionBlock)paramStructuredBlock.outer.getSubBlocks()[0]).getInstruction();
    Expression localExpression2 = paramInstructionContainer.getInstruction();
    IIncOperator localIIncOperator;
    LocalLoadOperator localLocalLoadOperator;
    boolean bool;
    if (((localExpression1 instanceof IIncOperator)) && ((localExpression2 instanceof LocalLoadOperator)))
    {
      localIIncOperator = (IIncOperator)localExpression1;
      localLocalLoadOperator = (LocalLoadOperator)localExpression2;
      bool = false;
    }
    else if (((localExpression1 instanceof LocalLoadOperator)) && ((localExpression2 instanceof IIncOperator)))
    {
      localLocalLoadOperator = (LocalLoadOperator)localExpression1;
      localIIncOperator = (IIncOperator)localExpression2;
      bool = true;
    }
    else
    {
      return false;
    }
    int i;
    if (localIIncOperator.getOperatorIndex() == 1 + 12) {
      i = 24;
    } else if (localIIncOperator.getOperatorIndex() == 2 + 12) {
      i = 25;
    } else {
      return false;
    }
    if (localIIncOperator.getValue() == -1) {
      i ^= 0x1;
    } else if (localIIncOperator.getValue() != 1) {
      return false;
    }
    if (!localIIncOperator.lvalueMatches(localLocalLoadOperator)) {
      return false;
    }
    Type localType = localLocalLoadOperator.getType().intersection(Type.tUInt);
    localIIncOperator.makeNonVoid();
    PrePostFixOperator localPrePostFixOperator = new PrePostFixOperator(localType, i, localIIncOperator.getLValue(), bool);
    paramInstructionContainer.setInstruction(localPrePostFixOperator);
    paramInstructionContainer.moveDefinitions(paramStructuredBlock.outer, paramStructuredBlock);
    paramStructuredBlock.replace(paramStructuredBlock.outer);
    return true;
  }
  
  public static boolean createPostInc(InstructionContainer paramInstructionContainer, StructuredBlock paramStructuredBlock)
  {
    if (!(paramInstructionContainer.getInstruction() instanceof StoreInstruction)) {
      return false;
    }
    StoreInstruction localStoreInstruction = (StoreInstruction)paramInstructionContainer.getInstruction();
    Expression localExpression = localStoreInstruction.getSubExpressions()[0];
    int i = localExpression.getFreeOperandCount();
    if ((!((Operator)localExpression).isFreeOperator()) || (!localStoreInstruction.isVoid()) || (!(localStoreInstruction.getSubExpressions()[1] instanceof BinaryOperator))) {
      return false;
    }
    BinaryOperator localBinaryOperator = (BinaryOperator)localStoreInstruction.getSubExpressions()[1];
    if ((localBinaryOperator.getSubExpressions() == null) || (!(localBinaryOperator.getSubExpressions()[0] instanceof NopOperator)) || (!(localBinaryOperator.getSubExpressions()[1] instanceof ConstOperator))) {
      return false;
    }
    ConstOperator localConstOperator = (ConstOperator)localBinaryOperator.getSubExpressions()[1];
    int j;
    if (localBinaryOperator.getOperatorIndex() == 1) {
      j = 24;
    } else if (localBinaryOperator.getOperatorIndex() == 2) {
      j = 25;
    } else {
      return false;
    }
    if (!localConstOperator.isOne(localExpression.getType())) {
      return false;
    }
    if (!(paramStructuredBlock.outer instanceof SequentialBlock)) {
      return false;
    }
    SequentialBlock localSequentialBlock = (SequentialBlock)paramStructuredBlock.outer;
    if (!(localSequentialBlock.subBlocks[0] instanceof SpecialBlock)) {
      return false;
    }
    SpecialBlock localSpecialBlock1 = (SpecialBlock)localSequentialBlock.subBlocks[0];
    if ((localSpecialBlock1.type != SpecialBlock.DUP) || (localSpecialBlock1.count != localExpression.getType().stackSize()) || (localSpecialBlock1.depth != i)) {
      return false;
    }
    if (!(localSequentialBlock.outer instanceof SequentialBlock)) {
      return false;
    }
    localSequentialBlock = (SequentialBlock)localSequentialBlock.outer;
    if (!(localSequentialBlock.subBlocks[0] instanceof InstructionBlock)) {
      return false;
    }
    InstructionBlock localInstructionBlock = (InstructionBlock)localSequentialBlock.subBlocks[0];
    if ((!(localInstructionBlock.getInstruction() instanceof Operator)) || (!localStoreInstruction.lvalueMatches((Operator)localInstructionBlock.getInstruction()))) {
      return false;
    }
    if (i > 0)
    {
      if (!(localSequentialBlock.outer instanceof SequentialBlock)) {
        return false;
      }
      localSequentialBlock = (SequentialBlock)localSequentialBlock.outer;
      if (!(localSequentialBlock.subBlocks[0] instanceof SpecialBlock)) {
        return false;
      }
      SpecialBlock localSpecialBlock2 = (SpecialBlock)localSequentialBlock.subBlocks[0];
      if ((localSpecialBlock2.type != SpecialBlock.DUP) || (localSpecialBlock2.count != i) || (localSpecialBlock2.depth != 0)) {
        return false;
      }
    }
    paramInstructionContainer.setInstruction(new PrePostFixOperator(localExpression.getType(), j, localStoreInstruction.getLValue(), true));
    paramInstructionContainer.moveDefinitions(localSequentialBlock, paramStructuredBlock);
    paramStructuredBlock.replace(localSequentialBlock);
    return true;
  }
}


