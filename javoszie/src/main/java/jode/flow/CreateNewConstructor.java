package jode.flow;

import jode.bytecode.Reference;
import jode.decompiler.MethodAnalyzer;
import jode.expr.Expression;
import jode.expr.InvokeOperator;
import jode.expr.NewOperator;
import jode.expr.NopOperator;
import jode.type.MethodType;
import jode.type.Type;

public class CreateNewConstructor
{
  public static boolean transform(InstructionContainer paramInstructionContainer, StructuredBlock paramStructuredBlock)
  {
    return (transformNormal(paramInstructionContainer, paramStructuredBlock)) || (transformJikesString(paramInstructionContainer, paramStructuredBlock));
  }
  
  static boolean transformJikesString(InstructionContainer paramInstructionContainer, StructuredBlock paramStructuredBlock)
  {
    if ((!(paramStructuredBlock.outer instanceof SequentialBlock)) || (!(paramInstructionContainer.getInstruction() instanceof InvokeOperator))) {
      return false;
    }
    InvokeOperator localInvokeOperator1 = (InvokeOperator)paramInstructionContainer.getInstruction();
    if ((!localInvokeOperator1.getClassType().equals(Type.tStringBuffer)) || (!localInvokeOperator1.isFreeOperator(2)) || (localInvokeOperator1.isStatic()) || (!localInvokeOperator1.getMethodName().equals("append")) || (localInvokeOperator1.getMethodType().getParameterTypes().length != 1)) {
      return false;
    }
    SequentialBlock localSequentialBlock = (SequentialBlock)paramStructuredBlock.outer;
    if ((!(localSequentialBlock.outer instanceof SequentialBlock)) || (!(localSequentialBlock.subBlocks[0] instanceof SpecialBlock))) {
      return false;
    }
    SpecialBlock localSpecialBlock = (SpecialBlock)localSequentialBlock.subBlocks[0];
    localSequentialBlock = (SequentialBlock)localSequentialBlock.outer;
    if ((localSpecialBlock.type != SpecialBlock.SWAP) || (!(localSequentialBlock.subBlocks[0] instanceof InstructionBlock)) || (!(localSequentialBlock.outer instanceof SequentialBlock))) {
      return false;
    }
    InstructionBlock localInstructionBlock = (InstructionBlock)localSequentialBlock.subBlocks[0];
    localSequentialBlock = (SequentialBlock)localSequentialBlock.outer;
    if ((!(localInstructionBlock.getInstruction() instanceof InvokeOperator)) || (!(localSequentialBlock.subBlocks[0] instanceof InstructionBlock))) {
      return false;
    }
    InvokeOperator localInvokeOperator2 = (InvokeOperator)localInstructionBlock.getInstruction();
    localInstructionBlock = (InstructionBlock)localSequentialBlock.subBlocks[0];
    if ((!localInvokeOperator2.isConstructor()) || (!localInvokeOperator2.getClassType().equals(Type.tStringBuffer)) || (localInvokeOperator2.isVoid()) || (localInvokeOperator2.getMethodType().getParameterTypes().length != 0)) {
      return false;
    }
    MethodAnalyzer localMethodAnalyzer = localInstructionBlock.flowBlock.method;
    Expression localExpression = localInstructionBlock.getInstruction();
    Type localType = localInvokeOperator1.getMethodType().getParameterTypes()[0];
    if (!localType.equals(Type.tString))
    {
      localInvokeOperator3 = new InvokeOperator(localMethodAnalyzer, 2, Reference.getReference("Ljava/lang/String;", "valueOf", "(" + localType.getTypeSignature() + ")Ljava/lang/String;"));
      localExpression = localInvokeOperator3.addOperand(localExpression);
    }
    InvokeOperator localInvokeOperator3 = new InvokeOperator(localMethodAnalyzer, 3, Reference.getReference("Ljava/lang/StringBuffer;", "<init>", "(Ljava/lang/String;)V"));
    localInvokeOperator3.makeNonVoid();
    localInvokeOperator3.setSubExpressions(0, localInvokeOperator2.getSubExpressions()[0]);
    localInvokeOperator3.setSubExpressions(1, localExpression);
    paramInstructionContainer.setInstruction(localInvokeOperator3);
    paramStructuredBlock.replace(localSequentialBlock);
    return true;
  }
  
  static boolean transformNormal(InstructionContainer paramInstructionContainer, StructuredBlock paramStructuredBlock)
  {
    if (!(paramStructuredBlock.outer instanceof SequentialBlock)) {
      return false;
    }
    if (!(paramInstructionContainer.getInstruction() instanceof InvokeOperator)) {
      return false;
    }
    InvokeOperator localInvokeOperator = (InvokeOperator)paramInstructionContainer.getInstruction();
    if ((!localInvokeOperator.isConstructor()) || (!localInvokeOperator.isVoid())) {
      return false;
    }
    SpecialBlock localSpecialBlock = null;
    SequentialBlock localSequentialBlock = (SequentialBlock)paramStructuredBlock.outer;
    Expression[] arrayOfExpression = localInvokeOperator.getSubExpressions();
    int i = localInvokeOperator.getFreeOperandCount();
    if (arrayOfExpression != null)
    {
      if (!(arrayOfExpression[0] instanceof NopOperator)) {
        return false;
      }
      if (localInvokeOperator.getFreeOperandCount() > 1)
      {
        if ((!(localSequentialBlock.outer instanceof SequentialBlock)) || (!(localSequentialBlock.subBlocks[0] instanceof SpecialBlock))) {
          return false;
        }
        localSpecialBlock = (SpecialBlock)localSequentialBlock.subBlocks[0];
        localSequentialBlock = (SequentialBlock)localSequentialBlock.outer;
        if ((localSpecialBlock.type != SpecialBlock.DUP) || (localSpecialBlock.depth == 0)) {
          return false;
        }
        int j = localSpecialBlock.count;
        do
        {
          if ((!(localSequentialBlock.outer instanceof SequentialBlock)) || (!(localSequentialBlock.subBlocks[0] instanceof InstructionBlock))) {
            return false;
          }
          localObject2 = ((InstructionBlock)localSequentialBlock.subBlocks[0]).getInstruction();
          localSequentialBlock = (SequentialBlock)localSequentialBlock.outer;
          if (!((Expression)localObject2).isVoid())
          {
            j -= ((Expression)localObject2).getType().stackSize();
            i--;
          }
        } while ((j > 0) && (i > 1));
        if (j != 0) {
          return false;
        }
      }
    }
    if (i != 1) {
      return false;
    }
    while (((localSequentialBlock.subBlocks[0] instanceof InstructionBlock)) && ((localSequentialBlock.outer instanceof SequentialBlock)))
    {
      localObject1 = ((InstructionBlock)localSequentialBlock.subBlocks[0]).getInstruction();
      if ((!((Expression)localObject1).isVoid()) || (((Expression)localObject1).getFreeOperandCount() > 0)) {
        break;
      }
      localSequentialBlock = (SequentialBlock)localSequentialBlock.outer;
    }
    Object localObject1 = null;
    if (((localSequentialBlock.outer instanceof SequentialBlock)) && ((localSequentialBlock.subBlocks[0] instanceof SpecialBlock)))
    {
      localObject1 = (SpecialBlock)localSequentialBlock.subBlocks[0];
      if ((((SpecialBlock)localObject1).type != SpecialBlock.DUP) || (((SpecialBlock)localObject1).count != 1) || (((SpecialBlock)localObject1).depth != 0)) {
        return false;
      }
      localSequentialBlock = (SequentialBlock)localSequentialBlock.outer;
      if ((localSpecialBlock != null) && (localSpecialBlock.depth != 2)) {
        return false;
      }
    }
    else if ((localSpecialBlock != null) && (localSpecialBlock.depth != 1))
    {
      return false;
    }
    if (!(localSequentialBlock.subBlocks[0] instanceof InstructionBlock)) {
      return false;
    }
    Object localObject2 = (InstructionBlock)localSequentialBlock.subBlocks[0];
    if (!(((InstructionBlock)localObject2).getInstruction() instanceof NewOperator)) {
      return false;
    }
    NewOperator localNewOperator = (NewOperator)((InstructionBlock)localObject2).getInstruction();
    if (localInvokeOperator.getClassType() != localNewOperator.getType()) {
      return false;
    }
    ((InstructionBlock)localObject2).removeBlock();
    if (localObject1 != null) {
      ((SpecialBlock)localObject1).removeBlock();
    }
    if (localSpecialBlock != null) {
      localSpecialBlock.depth = 0;
    }
    localInvokeOperator.setSubExpressions(0, localNewOperator);
    if (localObject1 != null) {
      localInvokeOperator.makeNonVoid();
    }
    return true;
  }
}


