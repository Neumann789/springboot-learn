package jode.flow;

import jode.decompiler.FieldAnalyzer;
import jode.expr.ClassFieldOperator;
import jode.expr.CompareUnaryOperator;
import jode.expr.ConstOperator;
import jode.expr.Expression;
import jode.expr.GetFieldOperator;
import jode.expr.InvokeOperator;
import jode.expr.Operator;
import jode.expr.PutFieldOperator;
import jode.expr.StoreInstruction;
import jode.type.Type;

public class CreateClassField
{
  public static boolean transform(IfThenElseBlock paramIfThenElseBlock, StructuredBlock paramStructuredBlock)
  {
    if ((!(paramIfThenElseBlock.cond instanceof CompareUnaryOperator)) || (((Operator)paramIfThenElseBlock.cond).getOperatorIndex() != 26) || (!(paramIfThenElseBlock.thenBlock instanceof InstructionBlock)) || (paramIfThenElseBlock.elseBlock != null)) {
      return false;
    }
    if ((paramIfThenElseBlock.thenBlock.jump != null) && ((paramIfThenElseBlock.jump == null) || (paramIfThenElseBlock.jump.destination != paramIfThenElseBlock.thenBlock.jump.destination))) {
      return false;
    }
    CompareUnaryOperator localCompareUnaryOperator = (CompareUnaryOperator)paramIfThenElseBlock.cond;
    Expression localExpression1 = ((InstructionBlock)paramIfThenElseBlock.thenBlock).getInstruction();
    if ((!(localCompareUnaryOperator.getSubExpressions()[0] instanceof GetFieldOperator)) || (!(localExpression1 instanceof StoreInstruction))) {
      return false;
    }
    StoreInstruction localStoreInstruction = (StoreInstruction)localExpression1;
    if (!(localStoreInstruction.getLValue() instanceof PutFieldOperator)) {
      return false;
    }
    PutFieldOperator localPutFieldOperator = (PutFieldOperator)localStoreInstruction.getLValue();
    if ((localPutFieldOperator.getField() == null) || (!localPutFieldOperator.matches((GetFieldOperator)localCompareUnaryOperator.getSubExpressions()[0])) || (!(localStoreInstruction.getSubExpressions()[1] instanceof InvokeOperator))) {
      return false;
    }
    InvokeOperator localInvokeOperator = (InvokeOperator)localStoreInstruction.getSubExpressions()[1];
    if (!localInvokeOperator.isGetClass()) {
      return false;
    }
    Expression localExpression2 = localInvokeOperator.getSubExpressions()[0];
    if (((localExpression2 instanceof ConstOperator)) && ((((ConstOperator)localExpression2).getValue() instanceof String)))
    {
      String str = (String)((ConstOperator)localExpression2).getValue();
      if (localPutFieldOperator.getField().setClassConstant(str))
      {
        localCompareUnaryOperator.setSubExpressions(0, new ClassFieldOperator(str.charAt(0) == '[' ? Type.tType(str) : Type.tClass(str)));
        EmptyBlock localEmptyBlock = new EmptyBlock();
        localEmptyBlock.moveJump(paramIfThenElseBlock.thenBlock.jump);
        paramIfThenElseBlock.setThenBlock(localEmptyBlock);
        return true;
      }
    }
    return false;
  }
}


