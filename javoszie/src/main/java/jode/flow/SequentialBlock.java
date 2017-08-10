package jode.flow;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Set;
import jode.AssertError;
import jode.GlobalOptions;
import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.Expression;
import jode.expr.LocalStoreOperator;
import jode.expr.StoreInstruction;
import jode.util.SimpleSet;

public class SequentialBlock
  extends StructuredBlock
{
  StructuredBlock[] subBlocks = new StructuredBlock[2];
  
  public void setFirst(StructuredBlock paramStructuredBlock)
  {
    this.subBlocks[0] = paramStructuredBlock;
    paramStructuredBlock.outer = this;
    paramStructuredBlock.setFlowBlock(this.flowBlock);
  }
  
  public void setSecond(StructuredBlock paramStructuredBlock)
  {
    this.subBlocks[1] = paramStructuredBlock;
    paramStructuredBlock.outer = this;
    paramStructuredBlock.setFlowBlock(this.flowBlock);
  }
  
  public void checkConsistent()
  {
    super.checkConsistent();
    if ((this.subBlocks[0].jump != null) || ((this.subBlocks[0] instanceof SequentialBlock)) || (this.jump != null)) {
      throw new AssertError("Inconsistency");
    }
  }
  
  public VariableStack mapStackToLocal(VariableStack paramVariableStack)
  {
    if (paramVariableStack == null) {
      GlobalOptions.err.println("map stack to local called with null: " + this + " in " + this.flowBlock);
    }
    VariableStack localVariableStack = this.subBlocks[0].mapStackToLocal(paramVariableStack);
    if (localVariableStack != null) {
      return this.subBlocks[1].mapStackToLocal(localVariableStack);
    }
    GlobalOptions.err.println("Dead code after Block " + this.subBlocks[0]);
    return null;
  }
  
  public void removeOnetimeLocals()
  {
    StructuredBlock localStructuredBlock1 = this.subBlocks[1];
    if ((localStructuredBlock1 instanceof SequentialBlock)) {
      localStructuredBlock1 = ((SequentialBlock)localStructuredBlock1).subBlocks[0];
    }
    if (((this.subBlocks[0] instanceof InstructionBlock)) && ((localStructuredBlock1 instanceof InstructionContainer)))
    {
      InstructionBlock localInstructionBlock = (InstructionBlock)this.subBlocks[0];
      InstructionContainer localInstructionContainer = (InstructionContainer)localStructuredBlock1;
      if ((localInstructionBlock.getInstruction() instanceof StoreInstruction))
      {
        StoreInstruction localStoreInstruction = (StoreInstruction)localInstructionBlock.getInstruction();
        if (((localStoreInstruction.getLValue() instanceof LocalStoreOperator)) && (((LocalStoreOperator)localStoreInstruction.getLValue()).getLocalInfo().getUseCount() == 2) && (localInstructionContainer.getInstruction().canCombine(localStoreInstruction) > 0))
        {
          System.err.println("before: " + localInstructionBlock + localInstructionContainer);
          localInstructionContainer.setInstruction(localInstructionContainer.getInstruction().combine(localStoreInstruction));
          System.err.println("after: " + localInstructionContainer);
          StructuredBlock localStructuredBlock2 = this.subBlocks[1];
          localStructuredBlock2.moveDefinitions(this, localStructuredBlock2);
          localStructuredBlock2.replace(this);
          localStructuredBlock2.removeOnetimeLocals();
          return;
        }
      }
    }
    super.removeOnetimeLocals();
  }
  
  public StructuredBlock getNextBlock(StructuredBlock paramStructuredBlock)
  {
    if (paramStructuredBlock == this.subBlocks[0])
    {
      if (this.subBlocks[1].isEmpty()) {
        return this.subBlocks[1].getNextBlock();
      }
      return this.subBlocks[1];
    }
    return getNextBlock();
  }
  
  public FlowBlock getNextFlowBlock(StructuredBlock paramStructuredBlock)
  {
    if (paramStructuredBlock == this.subBlocks[0])
    {
      if (this.subBlocks[1].isEmpty()) {
        return this.subBlocks[1].getNextFlowBlock();
      }
      return null;
    }
    return getNextFlowBlock();
  }
  
  public boolean isSingleExit(StructuredBlock paramStructuredBlock)
  {
    return paramStructuredBlock == this.subBlocks[1];
  }
  
  public Set propagateUsage()
  {
    this.used = new SimpleSet();
    SimpleSet localSimpleSet = new SimpleSet();
    Set localSet1 = this.subBlocks[0].propagateUsage();
    Set localSet2 = this.subBlocks[1].propagateUsage();
    this.used.addAll(this.subBlocks[0].used);
    if ((this.subBlocks[0] instanceof LoopBlock)) {
      ((LoopBlock)this.subBlocks[0]).removeLocallyDeclareable(this.used);
    }
    localSimpleSet.addAll(localSet1);
    localSimpleSet.addAll(localSet2);
    localSet1.retainAll(localSet2);
    this.used.addAll(localSet1);
    return localSimpleSet;
  }
  
  public void makeDeclaration(Set paramSet)
  {
    super.makeDeclaration(paramSet);
    if ((this.subBlocks[0] instanceof InstructionBlock)) {
      ((InstructionBlock)this.subBlocks[0]).checkDeclaration(this.declare);
    }
  }
  
  public void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    this.subBlocks[0].dumpSource(paramTabbedPrintWriter);
    this.subBlocks[1].dumpSource(paramTabbedPrintWriter);
  }
  
  public boolean replaceSubBlock(StructuredBlock paramStructuredBlock1, StructuredBlock paramStructuredBlock2)
  {
    for (int i = 0; i < 2; i++) {
      if (this.subBlocks[i] == paramStructuredBlock1)
      {
        this.subBlocks[i] = paramStructuredBlock2;
        return true;
      }
    }
    return false;
  }
  
  public StructuredBlock[] getSubBlocks()
  {
    return this.subBlocks;
  }
  
  public boolean jumpMayBeChanged()
  {
    return (this.subBlocks[1].jump != null) || (this.subBlocks[1].jumpMayBeChanged());
  }
}


