package jode.flow;

import jode.decompiler.LocalInfo;
import jode.expr.CheckNullOperator;
import jode.expr.CompareUnaryOperator;
import jode.expr.InvokeOperator;
import jode.expr.Operator;
import jode.expr.PopOperator;
import jode.type.MethodType;
import jode.type.Type;

public class CreateCheckNull
{
  public static boolean transformJavac(InstructionContainer paramInstructionContainer, StructuredBlock paramStructuredBlock)
  {
    if ((!(paramStructuredBlock.outer instanceof SequentialBlock)) || (!(paramInstructionContainer.getInstruction() instanceof Operator)) || (!(paramStructuredBlock.outer.getSubBlocks()[0] instanceof SpecialBlock))) {
      return false;
    }
    SpecialBlock localSpecialBlock = (SpecialBlock)paramStructuredBlock.outer.getSubBlocks()[0];
    if ((localSpecialBlock.type != SpecialBlock.DUP) || (localSpecialBlock.count != 1) || (localSpecialBlock.depth != 0)) {
      return false;
    }
    Operator localOperator = (Operator)paramInstructionContainer.getInstruction();
    if ((!(localOperator.getOperator() instanceof PopOperator)) || (!(localOperator.getSubExpressions()[0] instanceof InvokeOperator))) {
      return false;
    }
    InvokeOperator localInvokeOperator = (InvokeOperator)localOperator.getSubExpressions()[0];
    if ((!localInvokeOperator.getMethodName().equals("getClass")) || (!localInvokeOperator.getMethodType().toString().equals("()Ljava/lang/Class;"))) {
      return false;
    }
    LocalInfo localLocalInfo = new LocalInfo();
    paramInstructionContainer.setInstruction(new CheckNullOperator(Type.tUObject, localLocalInfo));
    paramStructuredBlock.replace(paramStructuredBlock.outer);
    return true;
  }
  
  public static boolean transformJikes(IfThenElseBlock paramIfThenElseBlock, StructuredBlock paramStructuredBlock)
  {
    if ((!(paramStructuredBlock.outer instanceof SequentialBlock)) || (!(paramStructuredBlock.outer.getSubBlocks()[0] instanceof SpecialBlock)) || (paramIfThenElseBlock.elseBlock != null) || (!(paramIfThenElseBlock.thenBlock instanceof ThrowBlock))) {
      return false;
    }
    SpecialBlock localSpecialBlock = (SpecialBlock)paramStructuredBlock.outer.getSubBlocks()[0];
    if ((localSpecialBlock.type != SpecialBlock.DUP) || (localSpecialBlock.count != 1) || (localSpecialBlock.depth != 0)) {
      return false;
    }
    if (!(paramIfThenElseBlock.cond instanceof CompareUnaryOperator)) {
      return false;
    }
    CompareUnaryOperator localCompareUnaryOperator = (CompareUnaryOperator)paramIfThenElseBlock.cond;
    if ((localCompareUnaryOperator.getOperatorIndex() != 26) || (!localCompareUnaryOperator.getCompareType().isOfType(Type.tUObject))) {
      return false;
    }
    LocalInfo localLocalInfo = new LocalInfo();
    InstructionBlock localInstructionBlock = new InstructionBlock(new CheckNullOperator(Type.tUObject, localLocalInfo));
    paramIfThenElseBlock.flowBlock.removeSuccessor(paramIfThenElseBlock.thenBlock.jump);
    localInstructionBlock.moveJump(paramIfThenElseBlock.jump);
    if (paramStructuredBlock == paramIfThenElseBlock)
    {
      localInstructionBlock.replace(paramStructuredBlock.outer);
      paramStructuredBlock = localInstructionBlock;
    }
    else
    {
      localInstructionBlock.replace(paramIfThenElseBlock);
      paramStructuredBlock.replace(paramStructuredBlock.outer);
    }
    return true;
  }
}


