package jode.flow;

import java.io.IOException;
import java.util.Set;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.Expression;

public class ReturnBlock
  extends InstructionContainer
{
  VariableStack stack;
  
  public ReturnBlock()
  {
    super(null);
  }
  
  public ReturnBlock(Expression paramExpression)
  {
    super(paramExpression, new Jump(FlowBlock.END_OF_METHOD));
  }
  
  public VariableStack mapStackToLocal(VariableStack paramVariableStack)
  {
    VariableStack localVariableStack = paramVariableStack;
    if (this.instr != null)
    {
      int i = this.instr.getFreeOperandCount();
      if (i > 0)
      {
        this.stack = paramVariableStack.peek(i);
        localVariableStack = paramVariableStack.pop(i);
      }
    }
    if (this.jump != null) {
      this.jump.stackMap = localVariableStack;
    }
    return null;
  }
  
  public void removePush()
  {
    if (this.stack != null) {
      this.instr = this.stack.mergeIntoExpression(this.instr);
    }
  }
  
  public boolean needsBraces()
  {
    return (this.declare != null) && (!this.declare.isEmpty());
  }
  
  public void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.print("return");
    if (this.instr != null)
    {
      paramTabbedPrintWriter.print(" ");
      this.instr.dumpExpression(2, paramTabbedPrintWriter);
    }
    paramTabbedPrintWriter.println(";");
  }
}


