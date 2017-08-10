package jode.flow;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.CompareUnaryOperator;
import jode.expr.Expression;
import jode.expr.InvokeOperator;
import jode.expr.PopOperator;
import jode.expr.StoreInstruction;
import jode.type.Type;

public class SpecialBlock
  extends StructuredBlock
{
  public static int DUP = 0;
  public static int SWAP = 1;
  public static int POP = 2;
  private static String[] output = { "DUP", "SWAP", "POP" };
  int type;
  int count;
  int depth;
  
  public SpecialBlock(int paramInt1, int paramInt2, int paramInt3, Jump paramJump)
  {
    this.type = paramInt1;
    this.count = paramInt2;
    this.depth = paramInt3;
    setJump(paramJump);
  }
  
  public VariableStack mapStackToLocal(VariableStack paramVariableStack)
  {
    VariableStack localVariableStack = paramVariableStack.executeSpecial(this);
    return super.mapStackToLocal(localVariableStack);
  }
  
  public void removePush()
  {
    removeBlock();
  }
  
  public void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.println(output[this.type] + (this.count == 1 ? "" : "2") + (this.depth == 0 ? "" : new StringBuilder().append("_X").append(this.depth).toString()));
  }
  
  public boolean doTransformations()
  {
    return ((this.type == SWAP) && (removeSwap(this.flowBlock.lastModified))) || ((this.type == POP) && (removePop(this.flowBlock.lastModified)));
  }
  
  public boolean removeSwap(StructuredBlock paramStructuredBlock)
  {
    if (((paramStructuredBlock.outer instanceof SequentialBlock)) && ((paramStructuredBlock.outer.outer instanceof SequentialBlock)) && ((paramStructuredBlock.outer.getSubBlocks()[0] instanceof InstructionBlock)) && ((paramStructuredBlock.outer.outer.getSubBlocks()[0] instanceof InstructionBlock)))
    {
      InstructionBlock localInstructionBlock1 = (InstructionBlock)paramStructuredBlock.outer.outer.getSubBlocks()[0];
      InstructionBlock localInstructionBlock2 = (InstructionBlock)paramStructuredBlock.outer.getSubBlocks()[0];
      Expression localExpression1 = localInstructionBlock1.getInstruction();
      Expression localExpression2 = localInstructionBlock2.getInstruction();
      if ((localExpression1.isVoid()) || (localExpression2.isVoid()) || (localExpression1.getFreeOperandCount() != 0) || (localExpression2.getFreeOperandCount() != 0) || (localExpression1.hasSideEffects(localExpression2)) || (localExpression2.hasSideEffects(localExpression1))) {
        return false;
      }
      paramStructuredBlock.outer.replace(localInstructionBlock1.outer);
      localInstructionBlock1.replace(this);
      localInstructionBlock1.moveJump(this.jump);
      localInstructionBlock1.flowBlock.lastModified = localInstructionBlock1;
      return true;
    }
    return false;
  }
  
  public boolean removePop(StructuredBlock paramStructuredBlock)
  {
    if (((paramStructuredBlock.outer instanceof SequentialBlock)) && ((paramStructuredBlock.outer.getSubBlocks()[0] instanceof InstructionBlock)))
    {
      if ((this.jump != null) && (this.jump.destination == null)) {
        return false;
      }
      InstructionBlock localInstructionBlock = (InstructionBlock)paramStructuredBlock.outer.getSubBlocks()[0];
      Expression localExpression1 = localInstructionBlock.getInstruction();
      if (localExpression1.getType().stackSize() == this.count)
      {
        Expression localExpression2;
        Object localObject;
        if (((localExpression1 instanceof InvokeOperator)) || ((localExpression1 instanceof StoreInstruction)))
        {
          localExpression2 = new PopOperator(localExpression1.getType()).addOperand(localExpression1);
          localInstructionBlock.setInstruction(localExpression2);
          localObject = localInstructionBlock;
        }
        else
        {
          localExpression2 = new CompareUnaryOperator(localExpression1.getType(), 27).addOperand(localExpression1);
          IfThenElseBlock localIfThenElseBlock = new IfThenElseBlock(localExpression2);
          localIfThenElseBlock.setThenBlock(new EmptyBlock());
          localObject = localIfThenElseBlock;
        }
        ((StructuredBlock)localObject).moveDefinitions(paramStructuredBlock.outer, paramStructuredBlock);
        ((StructuredBlock)localObject).moveJump(this.jump);
        if (this == paramStructuredBlock)
        {
          ((StructuredBlock)localObject).replace(paramStructuredBlock.outer);
          this.flowBlock.lastModified = ((StructuredBlock)localObject);
        }
        else
        {
          ((StructuredBlock)localObject).replace(this);
          paramStructuredBlock.replace(paramStructuredBlock.outer);
        }
        return true;
      }
    }
    return false;
  }
}


