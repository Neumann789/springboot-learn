package jode.flow;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import jode.decompiler.Declarable;
import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.Expression;
import jode.expr.LocalLoadOperator;
import jode.expr.LocalStoreOperator;
import jode.expr.NopOperator;
import jode.expr.StoreInstruction;
import jode.type.Type;

public class CatchBlock
  extends StructuredBlock
{
  StructuredBlock catchBlock;
  Type exceptionType;
  LocalInfo exceptionLocal;
  LocalInfo pushedLocal;
  
  public CatchBlock(Type paramType)
  {
    this.exceptionType = paramType;
  }
  
  public Type getExceptionType()
  {
    return this.exceptionType;
  }
  
  public LocalInfo getLocal()
  {
    return this.exceptionLocal;
  }
  
  public void setCatchBlock(StructuredBlock paramStructuredBlock)
  {
    this.catchBlock = paramStructuredBlock;
    paramStructuredBlock.outer = this;
    paramStructuredBlock.setFlowBlock(this.flowBlock);
    if (this.exceptionLocal == null) {
      combineLocal();
    }
  }
  
  public boolean replaceSubBlock(StructuredBlock paramStructuredBlock1, StructuredBlock paramStructuredBlock2)
  {
    if (this.catchBlock == paramStructuredBlock1) {
      this.catchBlock = paramStructuredBlock2;
    } else {
      return false;
    }
    return true;
  }
  
  public StructuredBlock[] getSubBlocks()
  {
    return new StructuredBlock[] { this.catchBlock };
  }
  
  public VariableStack mapStackToLocal(VariableStack paramVariableStack)
  {
    VariableStack localVariableStack;
    if (this.exceptionLocal == null)
    {
      this.pushedLocal = new LocalInfo();
      this.pushedLocal.setType(this.exceptionType);
      localVariableStack = paramVariableStack.push(this.pushedLocal);
    }
    else
    {
      localVariableStack = paramVariableStack;
    }
    return super.mapStackToLocal(localVariableStack);
  }
  
  public void removePush()
  {
    if (this.pushedLocal != null) {
      this.exceptionLocal = this.pushedLocal;
    }
    super.removePush();
  }
  
  public Set getDeclarables()
  {
    if (this.exceptionLocal != null) {
      return Collections.singleton(this.exceptionLocal);
    }
    return Collections.EMPTY_SET;
  }
  
  public void makeDeclaration(Set paramSet)
  {
    super.makeDeclaration(paramSet);
    if (this.exceptionLocal != null) {
      if (this.declare.contains(this.exceptionLocal))
      {
        this.declare.remove(this.exceptionLocal);
      }
      else
      {
        LocalInfo localLocalInfo = new LocalInfo();
        Expression localExpression = new StoreInstruction(new LocalStoreOperator(this.exceptionLocal.getType(), this.exceptionLocal)).addOperand(new LocalLoadOperator(localLocalInfo.getType(), null, localLocalInfo));
        InstructionBlock localInstructionBlock = new InstructionBlock(localExpression);
        localInstructionBlock.setFlowBlock(this.flowBlock);
        localInstructionBlock.appendBlock(this.catchBlock);
        this.catchBlock = localInstructionBlock;
        this.exceptionLocal = localLocalInfo;
        String str = localLocalInfo.guessName();
        Iterator localIterator = paramSet.iterator();
        while (localIterator.hasNext())
        {
          Declarable localDeclarable = (Declarable)localIterator.next();
          if (str.equals(localDeclarable.getName()))
          {
            localLocalInfo.makeNameUnique();
            break;
          }
        }
      }
    }
  }
  
  public void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.closeBraceContinue();
    paramTabbedPrintWriter.print("catch (");
    paramTabbedPrintWriter.printType(this.exceptionType);
    paramTabbedPrintWriter.print(" " + (this.exceptionLocal != null ? this.exceptionLocal.getName() : "PUSH") + ")");
    paramTabbedPrintWriter.openBrace();
    paramTabbedPrintWriter.tab();
    this.catchBlock.dumpSource(paramTabbedPrintWriter);
    paramTabbedPrintWriter.untab();
  }
  
  public boolean jumpMayBeChanged()
  {
    return (this.catchBlock.jump != null) || (this.catchBlock.jumpMayBeChanged());
  }
  
  public boolean combineLocal()
  {
    StructuredBlock localStructuredBlock = (this.catchBlock instanceof SequentialBlock) ? this.catchBlock.getSubBlocks()[0] : this.catchBlock;
    if (((localStructuredBlock instanceof SpecialBlock)) && (((SpecialBlock)localStructuredBlock).type == SpecialBlock.POP) && (((SpecialBlock)localStructuredBlock).count == 1))
    {
      this.exceptionLocal = new LocalInfo();
      this.exceptionLocal.setType(this.exceptionType);
      localStructuredBlock.removeBlock();
      return true;
    }
    if ((localStructuredBlock instanceof InstructionBlock))
    {
      Expression localExpression = ((InstructionBlock)localStructuredBlock).getInstruction();
      if ((localExpression instanceof StoreInstruction))
      {
        StoreInstruction localStoreInstruction = (StoreInstruction)localExpression;
        if ((localStoreInstruction.getOperatorIndex() == 12) && ((localStoreInstruction.getSubExpressions()[1] instanceof NopOperator)) && ((localStoreInstruction.getLValue() instanceof LocalStoreOperator)))
        {
          this.exceptionLocal = ((LocalStoreOperator)localStoreInstruction.getLValue()).getLocalInfo();
          this.exceptionLocal.setType(this.exceptionType);
          localStructuredBlock.removeBlock();
          return true;
        }
      }
    }
    return false;
  }
}


