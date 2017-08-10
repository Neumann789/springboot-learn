package jode.flow;

import java.io.IOException;
import java.util.Set;
import java.util.Stack;
import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.CombineableOperator;
import jode.expr.ConstOperator;
import jode.expr.Expression;
import jode.expr.LocalStoreOperator;
import jode.expr.StoreInstruction;
import jode.util.SimpleSet;

public class LoopBlock
  extends StructuredBlock
  implements BreakableBlock
{
  public static final int WHILE = 0;
  public static final int DOWHILE = 1;
  public static final int FOR = 2;
  public static final int POSSFOR = 3;
  public static final Expression TRUE = new ConstOperator(Boolean.TRUE);
  public static final Expression FALSE = new ConstOperator(Boolean.FALSE);
  Expression cond;
  VariableStack condStack;
  InstructionBlock initBlock;
  InstructionBlock incrBlock;
  Expression initInstr;
  Expression incrInstr;
  boolean isDeclaration;
  int type;
  StructuredBlock bodyBlock;
  VariableStack breakedStack;
  VariableStack continueStack;
  boolean mayChangeJump = true;
  static int serialno = 0;
  String label = null;
  
  public StructuredBlock getNextBlock(StructuredBlock paramStructuredBlock)
  {
    return this;
  }
  
  public FlowBlock getNextFlowBlock(StructuredBlock paramStructuredBlock)
  {
    return null;
  }
  
  public LoopBlock(int paramInt, Expression paramExpression)
  {
    this.type = paramInt;
    this.cond = paramExpression;
    this.mayChangeJump = (paramExpression == TRUE);
  }
  
  public void setBody(StructuredBlock paramStructuredBlock)
  {
    this.bodyBlock = paramStructuredBlock;
    this.bodyBlock.outer = this;
    paramStructuredBlock.setFlowBlock(this.flowBlock);
  }
  
  public void setInit(InstructionBlock paramInstructionBlock)
  {
    if (this.type == 3)
    {
      this.initBlock = paramInstructionBlock;
    }
    else if (this.type == 2)
    {
      this.initInstr = paramInstructionBlock.getInstruction();
      paramInstructionBlock.removeBlock();
    }
  }
  
  public boolean conditionMatches(CombineableOperator paramCombineableOperator)
  {
    return (this.type == 3) || (this.cond.containsMatchingLoad(paramCombineableOperator));
  }
  
  public Expression getCondition()
  {
    return this.cond;
  }
  
  public void setCondition(Expression paramExpression)
  {
    this.cond = paramExpression;
    if (this.type == 3)
    {
      if (paramExpression.containsMatchingLoad((CombineableOperator)this.incrBlock.getInstruction()))
      {
        this.type = 2;
        this.incrInstr = this.incrBlock.getInstruction();
        this.incrBlock.removeBlock();
        if ((this.initBlock != null) && (paramExpression.containsMatchingLoad((CombineableOperator)this.initBlock.getInstruction())))
        {
          this.initInstr = this.initBlock.getInstruction();
          this.initBlock.removeBlock();
        }
      }
      else
      {
        this.type = 0;
      }
      this.initBlock = (this.incrBlock = null);
    }
    this.mayChangeJump = false;
  }
  
  public int getType()
  {
    return this.type;
  }
  
  public void setType(int paramInt)
  {
    this.type = paramInt;
  }
  
  public boolean replaceSubBlock(StructuredBlock paramStructuredBlock1, StructuredBlock paramStructuredBlock2)
  {
    if (this.bodyBlock == paramStructuredBlock1) {
      this.bodyBlock = paramStructuredBlock2;
    } else {
      return false;
    }
    return true;
  }
  
  public StructuredBlock[] getSubBlocks()
  {
    return new StructuredBlock[] { this.bodyBlock };
  }
  
  public void removeLocallyDeclareable(Set paramSet)
  {
    if ((this.type == 2) && ((this.initInstr instanceof StoreInstruction)))
    {
      StoreInstruction localStoreInstruction = (StoreInstruction)this.initInstr;
      if ((localStoreInstruction.getLValue() instanceof LocalStoreOperator))
      {
        LocalInfo localLocalInfo = ((LocalStoreOperator)localStoreInstruction.getLValue()).getLocalInfo();
        paramSet.remove(localLocalInfo);
      }
    }
  }
  
  public Set getDeclarables()
  {
    SimpleSet localSimpleSet = new SimpleSet();
    if (this.type == 2)
    {
      this.incrInstr.fillDeclarables(localSimpleSet);
      if (this.initInstr != null) {
        this.initInstr.fillDeclarables(localSimpleSet);
      }
    }
    this.cond.fillDeclarables(localSimpleSet);
    return localSimpleSet;
  }
  
  public void checkDeclaration(Set paramSet)
  {
    if (((this.initInstr instanceof StoreInstruction)) && ((((StoreInstruction)this.initInstr).getLValue() instanceof LocalStoreOperator)))
    {
      StoreInstruction localStoreInstruction = (StoreInstruction)this.initInstr;
      LocalInfo localLocalInfo = ((LocalStoreOperator)localStoreInstruction.getLValue()).getLocalInfo();
      if (paramSet.contains(localLocalInfo))
      {
        this.isDeclaration = true;
        paramSet.remove(localLocalInfo);
      }
    }
  }
  
  public void makeDeclaration(Set paramSet)
  {
    if (this.type == 2)
    {
      if (this.initInstr != null) {
        this.initInstr.makeDeclaration(paramSet);
      }
      this.incrInstr.makeDeclaration(paramSet);
    }
    this.cond.makeDeclaration(paramSet);
    super.makeDeclaration(paramSet);
    if ((this.type == 2) && (this.initInstr != null)) {
      checkDeclaration(this.declare);
    }
  }
  
  public void dumpSource(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    super.dumpSource(paramTabbedPrintWriter);
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
    boolean bool = this.bodyBlock.needsBraces();
    switch (this.type)
    {
    case 0: 
    case 3: 
      if (this.cond == TRUE)
      {
        paramTabbedPrintWriter.print("for (;;)");
      }
      else
      {
        paramTabbedPrintWriter.print("while (");
        this.cond.dumpExpression(0, paramTabbedPrintWriter);
        paramTabbedPrintWriter.print(")");
      }
      break;
    case 1: 
      paramTabbedPrintWriter.print("do");
      break;
    case 2: 
      paramTabbedPrintWriter.print("for (");
      paramTabbedPrintWriter.startOp(0, 0);
      if (this.initInstr != null)
      {
        if (this.isDeclaration)
        {
          StoreInstruction localStoreInstruction = (StoreInstruction)this.initInstr;
          LocalInfo localLocalInfo = ((LocalStoreOperator)localStoreInstruction.getLValue()).getLocalInfo();
          paramTabbedPrintWriter.startOp(1, 1);
          localLocalInfo.dumpDeclaration(paramTabbedPrintWriter);
          paramTabbedPrintWriter.breakOp();
          paramTabbedPrintWriter.print(" = ");
          localStoreInstruction.getSubExpressions()[1].makeInitializer(localLocalInfo.getType());
          localStoreInstruction.getSubExpressions()[1].dumpExpression(paramTabbedPrintWriter, 100);
          paramTabbedPrintWriter.endOp();
        }
        else
        {
          this.initInstr.dumpExpression(1, paramTabbedPrintWriter);
        }
      }
      else {
        paramTabbedPrintWriter.print("/**/");
      }
      paramTabbedPrintWriter.print("; ");
      paramTabbedPrintWriter.breakOp();
      this.cond.dumpExpression(2, paramTabbedPrintWriter);
      paramTabbedPrintWriter.print("; ");
      paramTabbedPrintWriter.breakOp();
      this.incrInstr.dumpExpression(1, paramTabbedPrintWriter);
      paramTabbedPrintWriter.endOp();
      paramTabbedPrintWriter.print(")");
    }
    if (bool) {
      paramTabbedPrintWriter.openBrace();
    } else {
      paramTabbedPrintWriter.println();
    }
    paramTabbedPrintWriter.tab();
    this.bodyBlock.dumpSource(paramTabbedPrintWriter);
    paramTabbedPrintWriter.untab();
    if (this.type == 1)
    {
      if (bool) {
        paramTabbedPrintWriter.closeBraceContinue();
      }
      paramTabbedPrintWriter.print("while (");
      this.cond.dumpExpression(0, paramTabbedPrintWriter);
      paramTabbedPrintWriter.println(");");
    }
    else if (bool)
    {
      paramTabbedPrintWriter.closeBrace();
    }
  }
  
  public String getLabel()
  {
    if (this.label == null) {
      this.label = ("while_" + serialno++ + "_");
    }
    return this.label;
  }
  
  public void setBreaked()
  {
    this.mayChangeJump = false;
  }
  
  public VariableStack mapStackToLocal(VariableStack paramVariableStack)
  {
    VariableStack localVariableStack1;
    if (this.type == 1)
    {
      localVariableStack1 = this.bodyBlock.mapStackToLocal(paramVariableStack);
      if (localVariableStack1 != null) {
        mergeContinueStack(localVariableStack1);
      }
      if (this.continueStack != null)
      {
        int j = this.cond.getFreeOperandCount();
        VariableStack localVariableStack2;
        if (j > 0)
        {
          this.condStack = this.continueStack.peek(j);
          localVariableStack2 = this.continueStack.pop(j);
        }
        else
        {
          localVariableStack2 = this.continueStack;
        }
        if (this.cond != TRUE) {
          mergeBreakedStack(localVariableStack2);
        }
        if (this.cond != FALSE) {
          paramVariableStack.merge(localVariableStack2);
        }
      }
    }
    else
    {
      this.continueStack = paramVariableStack;
      int i = this.cond.getFreeOperandCount();
      if (i > 0)
      {
        this.condStack = paramVariableStack.peek(i);
        localVariableStack1 = paramVariableStack.pop(i);
      }
      else
      {
        localVariableStack1 = paramVariableStack;
      }
      if (this.cond != TRUE) {
        this.breakedStack = localVariableStack1;
      }
      VariableStack localVariableStack3 = this.bodyBlock.mapStackToLocal(localVariableStack1);
      if (localVariableStack3 != null) {
        mergeContinueStack(localVariableStack3);
      }
    }
    return this.breakedStack;
  }
  
  public void mergeContinueStack(VariableStack paramVariableStack)
  {
    if (this.continueStack == null) {
      this.continueStack = paramVariableStack;
    } else {
      this.continueStack.merge(paramVariableStack);
    }
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
    if (this.condStack != null) {
      this.cond = this.condStack.mergeIntoExpression(this.cond);
    }
    this.bodyBlock.removePush();
  }
  
  public void removeOnetimeLocals()
  {
    this.cond = this.cond.removeOnetimeLocals();
    if (this.type == 2)
    {
      if (this.initInstr != null) {
        this.initInstr.removeOnetimeLocals();
      }
      this.incrInstr.removeOnetimeLocals();
    }
    super.removeOnetimeLocals();
  }
  
  public void replaceBreakContinue(BreakableBlock paramBreakableBlock)
  {
    Stack localStack = new Stack();
    localStack.push(paramBreakableBlock);
    while (!localStack.isEmpty())
    {
      StructuredBlock[] arrayOfStructuredBlock = ((StructuredBlock)localStack.pop()).getSubBlocks();
      for (int i = 0; i < arrayOfStructuredBlock.length; i++)
      {
        if ((arrayOfStructuredBlock[i] instanceof BreakBlock))
        {
          BreakBlock localBreakBlock = (BreakBlock)arrayOfStructuredBlock[i];
          if (localBreakBlock.breaksBlock == paramBreakableBlock) {
            new ContinueBlock(this, localBreakBlock.label != null).replace(localBreakBlock);
          }
        }
        localStack.push(arrayOfStructuredBlock[i]);
      }
    }
  }
  
  public boolean jumpMayBeChanged()
  {
    return this.mayChangeJump;
  }
  
  public void simplify()
  {
    this.cond = this.cond.simplify();
    if (this.type == 2)
    {
      this.incrInstr = this.incrInstr.simplify();
      if (this.initInstr != null) {
        this.initInstr = this.initInstr.simplify();
      }
    }
    super.simplify();
  }
  
  public boolean doTransformations()
  {
    return ((this.initBlock == null) && (this.type == 3)) || ((this.initInstr == null) && (this.type == 2) && (CreateForInitializer.transform(this, this.flowBlock.lastModified)));
  }
}


