package jode.flow;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.Expression;

public class SwitchBlock
  extends InstructionContainer
  implements BreakableBlock
{
  CaseBlock[] caseBlocks;
  VariableStack exprStack;
  VariableStack breakedStack;
  boolean isBreaked = false;
  static int serialno = 0;
  String label = null;
  
  public SwitchBlock(Expression paramExpression, int[] paramArrayOfInt, FlowBlock[] paramArrayOfFlowBlock)
  {
    super(paramExpression);
    int i = paramArrayOfFlowBlock.length;
    FlowBlock localFlowBlock1 = paramArrayOfFlowBlock[paramArrayOfInt.length];
    for (int j = 0; j < paramArrayOfInt.length; j++) {
      if (paramArrayOfFlowBlock[j] == localFlowBlock1)
      {
        paramArrayOfFlowBlock[j] = null;
        i--;
      }
    }
    this.caseBlocks = new CaseBlock[i];
    FlowBlock localFlowBlock2 = null;
    for (int k = i - 1; k >= 0; k--)
    {
      int m = 0;
      for (int n = 1; n < paramArrayOfFlowBlock.length; n++) {
        if ((paramArrayOfFlowBlock[n] != null) && ((paramArrayOfFlowBlock[m] == null) || (paramArrayOfFlowBlock[n].getAddr() >= paramArrayOfFlowBlock[m].getAddr()))) {
          m = n;
        }
      }
      if (m == paramArrayOfInt.length) {
        n = -1;
      } else {
        n = paramArrayOfInt[m];
      }
      if (paramArrayOfFlowBlock[m] == localFlowBlock2) {
        this.caseBlocks[k] = new CaseBlock(n);
      } else {
        this.caseBlocks[k] = new CaseBlock(n, new Jump(paramArrayOfFlowBlock[m]));
      }
      this.caseBlocks[k].outer = this;
      localFlowBlock2 = paramArrayOfFlowBlock[m];
      paramArrayOfFlowBlock[m] = null;
      if (m == paramArrayOfInt.length) {
        this.caseBlocks[k].isDefault = true;
      }
    }
    this.caseBlocks[(i - 1)].isLastBlock = true;
    this.jump = null;
    this.isBreaked = false;
  }
  
  public VariableStack mapStackToLocal(VariableStack paramVariableStack)
  {
    int i = this.instr.getFreeOperandCount();
    VariableStack localVariableStack1;
    if (i > 0)
    {
      this.exprStack = paramVariableStack.peek(i);
      localVariableStack1 = paramVariableStack.pop(i);
    }
    else
    {
      localVariableStack1 = paramVariableStack;
    }
    VariableStack localVariableStack2 = localVariableStack1;
    for (int j = 0; j < this.caseBlocks.length; j++)
    {
      if (localVariableStack2 != null) {
        localVariableStack1.merge(localVariableStack2);
      }
      localVariableStack2 = this.caseBlocks[j].mapStackToLocal(localVariableStack1);
    }
    if (localVariableStack2 != null) {
      mergeBreakedStack(localVariableStack2);
    }
    if (this.jump != null)
    {
      this.jump.stackMap = this.breakedStack;
      return null;
    }
    return this.breakedStack;
  }
  
  public void mergeBreakedStack(VariableStack paramVariableStack)
  {
    if (this.breakedStack != null) {
      this.breakedStack.merge(paramVariableStack);
    } else {
      this.breakedStack = paramVariableStack;
    }
  }
  
  public void removePush()
  {
    if (this.exprStack != null) {
      this.instr = this.exprStack.mergeIntoExpression(this.instr);
    }
    super.removePush();
  }
  
  public StructuredBlock findCase(FlowBlock paramFlowBlock)
  {
    for (int i = 0; i < this.caseBlocks.length; i++) {
      if ((this.caseBlocks[i].subBlock != null) && ((this.caseBlocks[i].subBlock instanceof EmptyBlock)) && (this.caseBlocks[i].subBlock.jump != null) && (this.caseBlocks[i].subBlock.jump.destination == paramFlowBlock)) {
        return this.caseBlocks[i].subBlock;
      }
    }
    return null;
  }
  
  public StructuredBlock prevCase(StructuredBlock paramStructuredBlock)
  {
    for (int i = this.caseBlocks.length - 1; i >= 0; i--) {
      if (this.caseBlocks[i].subBlock == paramStructuredBlock)
      {
        i--;
        while (i >= 0)
        {
          if (this.caseBlocks[i].subBlock != null) {
            return this.caseBlocks[i].subBlock;
          }
          i--;
        }
      }
    }
    return null;
  }
  
  public StructuredBlock getNextBlock(StructuredBlock paramStructuredBlock)
  {
    for (int i = 0; i < this.caseBlocks.length - 1; i++) {
      if (paramStructuredBlock == this.caseBlocks[i]) {
        return this.caseBlocks[(i + 1)];
      }
    }
    return getNextBlock();
  }
  
  public FlowBlock getNextFlowBlock(StructuredBlock paramStructuredBlock)
  {
    for (int i = 0; i < this.caseBlocks.length - 1; i++) {
      if (paramStructuredBlock == this.caseBlocks[i]) {
        return null;
      }
    }
    return getNextFlowBlock();
  }
  
  public void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    if (this.label != null)
    {
      paramTabbedPrintWriter.untab();
      paramTabbedPrintWriter.println(this.label + ":");
      paramTabbedPrintWriter.tab();
    }
    paramTabbedPrintWriter.print("switch (");
    this.instr.dumpExpression(0, paramTabbedPrintWriter);
    paramTabbedPrintWriter.print(")");
    paramTabbedPrintWriter.openBrace();
    for (int i = 0; i < this.caseBlocks.length; i++) {
      this.caseBlocks[i].dumpSource(paramTabbedPrintWriter);
    }
    paramTabbedPrintWriter.closeBrace();
  }
  
  public StructuredBlock[] getSubBlocks()
  {
    return this.caseBlocks;
  }
  
  public String getLabel()
  {
    if (this.label == null) {
      this.label = ("switch_" + serialno++ + "_");
    }
    return this.label;
  }
  
  public void setBreaked()
  {
    this.isBreaked = true;
  }
  
  public boolean jumpMayBeChanged()
  {
    return (!this.isBreaked) && ((this.caseBlocks[(this.caseBlocks.length - 1)].jump != null) || (this.caseBlocks[(this.caseBlocks.length - 1)].jumpMayBeChanged()));
  }
}


