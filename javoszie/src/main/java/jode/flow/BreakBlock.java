package jode.flow;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;

public class BreakBlock
  extends StructuredBlock
{
  StructuredBlock breaksBlock;
  String label;
  
  public BreakBlock(BreakableBlock paramBreakableBlock, boolean paramBoolean)
  {
    this.breaksBlock = ((StructuredBlock)paramBreakableBlock);
    paramBreakableBlock.setBreaked();
    if (paramBoolean) {
      this.label = paramBreakableBlock.getLabel();
    } else {
      this.label = null;
    }
  }
  
  public void checkConsistent()
  {
    super.checkConsistent();
    for (StructuredBlock localStructuredBlock = this.outer; localStructuredBlock != this.breaksBlock; localStructuredBlock = localStructuredBlock.outer) {
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
    return this.breaksBlock.getNextBlock();
  }
  
  public FlowBlock getNextFlowBlock()
  {
    return this.breaksBlock.getNextFlowBlock();
  }
  
  public VariableStack mapStackToLocal(VariableStack paramVariableStack)
  {
    ((BreakableBlock)this.breaksBlock).mergeBreakedStack(paramVariableStack);
    return null;
  }
  
  public boolean needsBraces()
  {
    return false;
  }
  
  public void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.println("break" + (this.label == null ? "" : new StringBuilder().append(" ").append(this.label).toString()) + ";");
  }
  
  public boolean jumpMayBeChanged()
  {
    return true;
  }
}


