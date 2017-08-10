package jode.flow;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;

public class FinallyBlock
  extends StructuredBlock
{
  StructuredBlock subBlock;
  
  public void setCatchBlock(StructuredBlock paramStructuredBlock)
  {
    this.subBlock = paramStructuredBlock;
    paramStructuredBlock.outer = this;
    paramStructuredBlock.setFlowBlock(this.flowBlock);
  }
  
  public boolean replaceSubBlock(StructuredBlock paramStructuredBlock1, StructuredBlock paramStructuredBlock2)
  {
    if (this.subBlock == paramStructuredBlock1) {
      this.subBlock = paramStructuredBlock2;
    } else {
      return false;
    }
    return true;
  }
  
  public StructuredBlock[] getSubBlocks()
  {
    return new StructuredBlock[] { this.subBlock };
  }
  
  public VariableStack mapStackToLocal(VariableStack paramVariableStack)
  {
    super.mapStackToLocal(paramVariableStack);
    return null;
  }
  
  public StructuredBlock getNextBlock(StructuredBlock paramStructuredBlock)
  {
    return null;
  }
  
  public FlowBlock getNextFlowBlock(StructuredBlock paramStructuredBlock)
  {
    return null;
  }
  
  public void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.closeBraceContinue();
    paramTabbedPrintWriter.print("finally");
    paramTabbedPrintWriter.openBrace();
    paramTabbedPrintWriter.tab();
    this.subBlock.dumpSource(paramTabbedPrintWriter);
    paramTabbedPrintWriter.untab();
  }
}


