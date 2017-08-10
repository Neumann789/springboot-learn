package jode.flow;

import java.io.IOException;
import jode.AssertError;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.Expression;

public class ConditionalBlock
  extends InstructionContainer
{
  VariableStack stack;
  EmptyBlock trueBlock;
  
  public void checkConsistent()
  {
    super.checkConsistent();
    if ((this.trueBlock.jump == null) || (!(this.trueBlock instanceof EmptyBlock))) {
      throw new AssertError("Inconsistency");
    }
  }
  
  public ConditionalBlock(Expression paramExpression, Jump paramJump1, Jump paramJump2)
  {
    super(paramExpression, paramJump2);
    this.trueBlock = new EmptyBlock(paramJump1);
    this.trueBlock.outer = this;
  }
  
  public ConditionalBlock(Expression paramExpression)
  {
    super(paramExpression);
    this.trueBlock = new EmptyBlock();
    this.trueBlock.outer = this;
  }
  
  public StructuredBlock[] getSubBlocks()
  {
    return new StructuredBlock[] { this.trueBlock };
  }
  
  public boolean replaceSubBlock(StructuredBlock paramStructuredBlock1, StructuredBlock paramStructuredBlock2)
  {
    throw new AssertError("replaceSubBlock on ConditionalBlock");
  }
  
  public VariableStack mapStackToLocal(VariableStack paramVariableStack)
  {
    int i = this.instr.getFreeOperandCount();
    VariableStack localVariableStack;
    if (i > 0)
    {
      this.stack = paramVariableStack.peek(i);
      localVariableStack = paramVariableStack.pop(i);
    }
    else
    {
      localVariableStack = paramVariableStack;
    }
    if (this.trueBlock.jump != null) {
      this.trueBlock.jump.stackMap = localVariableStack;
    }
    if (this.jump != null)
    {
      this.jump.stackMap = localVariableStack;
      return null;
    }
    return localVariableStack;
  }
  
  public void removePush()
  {
    if (this.stack != null) {
      this.instr = this.stack.mergeIntoExpression(this.instr);
    }
    this.trueBlock.removePush();
  }
  
  public void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.print("IF (");
    this.instr.dumpExpression(0, paramTabbedPrintWriter);
    paramTabbedPrintWriter.println(")");
    paramTabbedPrintWriter.tab();
    this.trueBlock.dumpSource(paramTabbedPrintWriter);
    paramTabbedPrintWriter.untab();
  }
  
  public boolean doTransformations()
  {
    StructuredBlock localStructuredBlock = this.flowBlock.lastModified;
    return (super.doTransformations()) || (CombineIfGotoExpressions.transform(this, localStructuredBlock)) || (CreateIfThenElseOperator.createFunny(this, localStructuredBlock));
  }
}


