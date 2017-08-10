package jode.flow;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;

public class ContinueBlock
  extends StructuredBlock
{
  LoopBlock continuesBlock;
  String continueLabel;
  
  public ContinueBlock(LoopBlock paramLoopBlock, boolean paramBoolean)
  {
    this.continuesBlock = paramLoopBlock;
    if (paramBoolean) {
      this.continueLabel = paramLoopBlock.getLabel();
    } else {
      this.continueLabel = null;
    }
  }
  
  public void checkConsistent()
  {
    super.checkConsistent();
    for (StructuredBlock localStructuredBlock = this.outer; localStructuredBlock != this.continuesBlock; localStructuredBlock = localStructuredBlock.outer) {
      if (localStructuredBlock == null) {
        throw new RuntimeException("Inconsistency");
      }
    }
  }
  
  public boolean isEmpty()
  {
    return true;
  }
  
  public StructuredBlock getNextBlock()
  {
    return this.continuesBlock;
  }
  
  public FlowBlock getNextFlowBlock()
  {
    return null;
  }
  
  public VariableStack mapStackToLocal(VariableStack paramVariableStack)
  {
    this.continuesBlock.mergeContinueStack(paramVariableStack);
    return null;
  }
  
  public void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.println("continue" + (this.continueLabel == null ? "" : new StringBuilder().append(" ").append(this.continueLabel).toString()) + ";");
  }
  
  public boolean needsBraces()
  {
    return false;
  }
  
  public boolean jumpMayBeChanged()
  {
    return true;
  }
}


