package jode.flow;

import java.io.IOException;
import java.util.Set;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.Expression;
import jode.util.SimpleSet;

public class IfThenElseBlock
  extends StructuredBlock
{
  Expression cond;
  VariableStack condStack;
  StructuredBlock thenBlock;
  StructuredBlock elseBlock;
  
  public IfThenElseBlock(Expression paramExpression)
  {
    this.cond = paramExpression;
  }
  
  public void setThenBlock(StructuredBlock paramStructuredBlock)
  {
    this.thenBlock = paramStructuredBlock;
    paramStructuredBlock.outer = this;
    paramStructuredBlock.setFlowBlock(this.flowBlock);
  }
  
  public void setElseBlock(StructuredBlock paramStructuredBlock)
  {
    this.elseBlock = paramStructuredBlock;
    paramStructuredBlock.outer = this;
    paramStructuredBlock.setFlowBlock(this.flowBlock);
  }
  
  public boolean replaceSubBlock(StructuredBlock paramStructuredBlock1, StructuredBlock paramStructuredBlock2)
  {
    if (this.thenBlock == paramStructuredBlock1) {
      this.thenBlock = paramStructuredBlock2;
    } else if (this.elseBlock == paramStructuredBlock1) {
      this.elseBlock = paramStructuredBlock2;
    } else {
      return false;
    }
    return true;
  }
  
  public VariableStack mapStackToLocal(VariableStack paramVariableStack)
  {
    int i = this.cond.getFreeOperandCount();
    VariableStack localVariableStack1;
    if (i > 0)
    {
      this.condStack = paramVariableStack.peek(i);
      localVariableStack1 = paramVariableStack.pop(i);
    }
    else
    {
      localVariableStack1 = paramVariableStack;
    }
    VariableStack localVariableStack2 = VariableStack.merge(this.thenBlock.mapStackToLocal(localVariableStack1), this.elseBlock == null ? localVariableStack1 : this.elseBlock.mapStackToLocal(localVariableStack1));
    if (this.jump != null)
    {
      this.jump.stackMap = localVariableStack2;
      return null;
    }
    return localVariableStack2;
  }
  
  public void removePush()
  {
    if (this.condStack != null) {
      this.cond = this.condStack.mergeIntoExpression(this.cond);
    }
    this.thenBlock.removePush();
    if (this.elseBlock != null) {
      this.elseBlock.removePush();
    }
  }
  
  public Set getDeclarables()
  {
    SimpleSet localSimpleSet = new SimpleSet();
    this.cond.fillDeclarables(localSimpleSet);
    return localSimpleSet;
  }
  
  public void makeDeclaration(Set paramSet)
  {
    this.cond.makeDeclaration(paramSet);
    super.makeDeclaration(paramSet);
  }
  
  public void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    boolean bool = this.thenBlock.needsBraces();
    paramTabbedPrintWriter.print("if (");
    this.cond.dumpExpression(0, paramTabbedPrintWriter);
    paramTabbedPrintWriter.print(")");
    if (bool) {
      paramTabbedPrintWriter.openBrace();
    } else {
      paramTabbedPrintWriter.println();
    }
    paramTabbedPrintWriter.tab();
    this.thenBlock.dumpSource(paramTabbedPrintWriter);
    paramTabbedPrintWriter.untab();
    if (this.elseBlock != null)
    {
      if (bool) {
        paramTabbedPrintWriter.closeBraceContinue();
      }
      if (((this.elseBlock instanceof IfThenElseBlock)) && ((this.elseBlock.declare == null) || (this.elseBlock.declare.isEmpty())))
      {
        bool = false;
        paramTabbedPrintWriter.print("else ");
        this.elseBlock.dumpSource(paramTabbedPrintWriter);
      }
      else
      {
        bool = this.elseBlock.needsBraces();
        paramTabbedPrintWriter.print("else");
        if (bool) {
          paramTabbedPrintWriter.openBrace();
        } else {
          paramTabbedPrintWriter.println();
        }
        paramTabbedPrintWriter.tab();
        this.elseBlock.dumpSource(paramTabbedPrintWriter);
        paramTabbedPrintWriter.untab();
      }
    }
    if (bool) {
      paramTabbedPrintWriter.closeBrace();
    }
  }
  
  public StructuredBlock[] getSubBlocks()
  {
    return new StructuredBlock[] { this.thenBlock, this.elseBlock == null ? new StructuredBlock[] { this.thenBlock } : this.elseBlock };
  }
  
  public boolean jumpMayBeChanged()
  {
    return ((this.thenBlock.jump != null) || (this.thenBlock.jumpMayBeChanged())) && (this.elseBlock != null) && ((this.elseBlock.jump != null) || (this.elseBlock.jumpMayBeChanged()));
  }
  
  public void simplify()
  {
    this.cond = this.cond.simplify();
    super.simplify();
  }
  
  public boolean doTransformations()
  {
    StructuredBlock localStructuredBlock = this.flowBlock.lastModified;
    return (CreateCheckNull.transformJikes(this, localStructuredBlock)) || (CreateClassField.transform(this, localStructuredBlock));
  }
}


