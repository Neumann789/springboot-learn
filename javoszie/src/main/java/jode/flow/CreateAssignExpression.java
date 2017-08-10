package jode.flow;

import jode.decompiler.LocalInfo;
import jode.expr.BinaryOperator;
import jode.expr.ConvertOperator;
import jode.expr.Expression;
import jode.expr.LocalLoadOperator;
import jode.expr.LocalStoreOperator;
import jode.expr.Operator;
import jode.expr.StoreInstruction;
import jode.expr.StringAddOperator;
import jode.type.Type;

public class CreateAssignExpression
{
  public static boolean transform(InstructionContainer paramInstructionContainer, StructuredBlock paramStructuredBlock)
  {
    if ((!(paramStructuredBlock.outer instanceof SequentialBlock)) || (!(paramInstructionContainer.getInstruction() instanceof StoreInstruction)) || (!paramInstructionContainer.getInstruction().isVoid())) {
      return false;
    }
    return (createAssignOp(paramInstructionContainer, paramStructuredBlock)) || (createAssignExpression(paramInstructionContainer, paramStructuredBlock));
  }
  
  public static boolean createAssignOp(InstructionContainer paramInstructionContainer, StructuredBlock paramStructuredBlock)
  {
    SequentialBlock localSequentialBlock1 = (SequentialBlock)paramStructuredBlock.outer;
    StoreInstruction localStoreInstruction = (StoreInstruction)paramInstructionContainer.getInstruction();
    if ((!localStoreInstruction.isFreeOperator()) || (localStoreInstruction.isOpAssign())) {
      return false;
    }
    Expression localExpression = localStoreInstruction.getSubExpressions()[0];
    int i = localExpression.getFreeOperandCount();
    int j = 0;
    if ((localSequentialBlock1.subBlocks[0] instanceof SpecialBlock))
    {
      localObject1 = (SpecialBlock)localSequentialBlock1.subBlocks[0];
      if ((((SpecialBlock)localObject1).type != SpecialBlock.DUP) || (((SpecialBlock)localObject1).depth != i) || (((SpecialBlock)localObject1).count != localExpression.getType().stackSize()) || (!(localSequentialBlock1.outer instanceof SequentialBlock))) {
        return false;
      }
      localSequentialBlock1 = (SequentialBlock)localSequentialBlock1.outer;
      j = 1;
    }
    if (!(localSequentialBlock1.subBlocks[0] instanceof InstructionBlock)) {
      return false;
    }
    Object localObject1 = (InstructionBlock)localSequentialBlock1.subBlocks[0];
    if (!(((InstructionBlock)localObject1).getInstruction() instanceof Operator)) {
      return false;
    }
    Operator localOperator1 = (Operator)((InstructionBlock)localObject1).getInstruction();
    if (localOperator1.getFreeOperandCount() != i) {
      return false;
    }
    Type localType = localOperator1.getType();
    SpecialBlock localSpecialBlock = null;
    if (i > 0)
    {
      if ((!(localSequentialBlock1.outer instanceof SequentialBlock)) || (!(localSequentialBlock1.outer.getSubBlocks()[0] instanceof SpecialBlock))) {
        return false;
      }
      SequentialBlock localSequentialBlock2 = (SequentialBlock)localSequentialBlock1.outer;
      localSpecialBlock = (SpecialBlock)localSequentialBlock2.subBlocks[0];
      if ((localSpecialBlock.type != SpecialBlock.DUP) || (localSpecialBlock.depth != 0) || (localSpecialBlock.count != i)) {
        return false;
      }
    }
    if (((localOperator1 instanceof ConvertOperator)) && ((localOperator1.getSubExpressions()[0] instanceof Operator)) && (localOperator1.getType().isOfType(localExpression.getType()))) {
      for (localOperator1 = (Operator)localOperator1.getSubExpressions()[0]; ((localOperator1 instanceof ConvertOperator)) && ((localOperator1.getSubExpressions()[0] instanceof Operator)); localOperator1 = (Operator)localOperator1.getSubExpressions()[0]) {}
    }
    int k;
    Object localObject3;
    Object localObject2;
    if ((localOperator1 instanceof BinaryOperator))
    {
      k = localOperator1.getOperatorIndex();
      if ((k < 1) || (k >= 12)) {
        return false;
      }
      if (!(localOperator1.getSubExpressions()[0] instanceof Operator)) {
        return false;
      }
      for (localObject3 = (Operator)localOperator1.getSubExpressions()[0]; ((localObject3 instanceof ConvertOperator)) && ((localObject3.getSubExpressions()[0] instanceof Operator)); localObject3 = (Operator)localObject3.getSubExpressions()[0]) {}
      if ((!localStoreInstruction.lvalueMatches((Operator)localObject3)) || (!((Operator)localObject3).isFreeOperator(i))) {
        return false;
      }
      if ((localExpression instanceof LocalStoreOperator)) {
        ((LocalLoadOperator)localObject3).getLocalInfo().combineWith(((LocalStoreOperator)localExpression).getLocalInfo());
      }
      localObject2 = localOperator1.getSubExpressions()[1];
    }
    else
    {
      localObject3 = localOperator1.simplifyString();
      localObject2 = localObject3;
      Operator localOperator2 = null;
      Operator localOperator3 = null;
      while ((localObject3 instanceof StringAddOperator))
      {
        localOperator3 = localOperator2;
        localOperator2 = (Operator)localObject3;
        localObject3 = localOperator2.getSubExpressions()[0];
      }
      if ((localOperator2 == null) || (!(localObject3 instanceof Operator)) || (!localStoreInstruction.lvalueMatches((Operator)localObject3)) || (!((Operator)localObject3).isFreeOperator(i))) {
        return false;
      }
      if ((localExpression instanceof LocalStoreOperator)) {
        ((LocalLoadOperator)localObject3).getLocalInfo().combineWith(((LocalStoreOperator)localExpression).getLocalInfo());
      }
      if (localOperator3 != null) {
        localOperator3.setSubExpressions(0, localOperator2.getSubExpressions()[1]);
      } else {
        localObject2 = localOperator2.getSubExpressions()[1];
      }
      k = 1;
    }
    if (localSpecialBlock != null) {
      localSpecialBlock.removeBlock();
    }
    ((InstructionBlock)localObject1).setInstruction((Expression)localObject2);
    localExpression.setType(localType);
    localStoreInstruction.makeOpAssign(12 + k);
    if (j != 0) {
      localStoreInstruction.makeNonVoid();
    }
    paramStructuredBlock.replace(localSequentialBlock1.subBlocks[1]);
    return true;
  }
  
  public static boolean createAssignExpression(InstructionContainer paramInstructionContainer, StructuredBlock paramStructuredBlock)
  {
    SequentialBlock localSequentialBlock = (SequentialBlock)paramStructuredBlock.outer;
    StoreInstruction localStoreInstruction = (StoreInstruction)paramInstructionContainer.getInstruction();
    if (((localSequentialBlock.subBlocks[0] instanceof SpecialBlock)) && (localStoreInstruction.isFreeOperator()))
    {
      Expression localExpression = localStoreInstruction.getSubExpressions()[0];
      SpecialBlock localSpecialBlock = (SpecialBlock)localSequentialBlock.subBlocks[0];
      if ((localSpecialBlock.type != SpecialBlock.DUP) || (localSpecialBlock.depth != localExpression.getFreeOperandCount()) || (localSpecialBlock.count != localExpression.getType().stackSize())) {
        return false;
      }
      localSpecialBlock.removeBlock();
      localStoreInstruction.makeNonVoid();
      return true;
    }
    return false;
  }
}


