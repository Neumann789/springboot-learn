package jode.flow;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;

public class EmptyBlock
  extends StructuredBlock
{
  public EmptyBlock() {}
  
  public EmptyBlock(Jump paramJump)
  {
    setJump(paramJump);
  }
  
  public boolean isEmpty()
  {
    return true;
  }
  
  public StructuredBlock appendBlock(StructuredBlock paramStructuredBlock)
  {
    if ((this.outer instanceof ConditionalBlock))
    {
      IfThenElseBlock localIfThenElseBlock = new IfThenElseBlock(((ConditionalBlock)this.outer).getInstruction());
      localIfThenElseBlock.moveDefinitions(this.outer, this);
      localIfThenElseBlock.replace(this.outer);
      localIfThenElseBlock.moveJump(this.outer.jump);
      localIfThenElseBlock.setThenBlock(this);
    }
    paramStructuredBlock.replace(this);
    return paramStructuredBlock;
  }
  
  public StructuredBlock prependBlock(StructuredBlock paramStructuredBlock)
  {
    paramStructuredBlock = appendBlock(paramStructuredBlock);
    paramStructuredBlock.moveJump(this.jump);
    return paramStructuredBlock;
  }
  
  public void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    if (this.jump == null) {
      paramTabbedPrintWriter.println("/* empty */");
    }
  }
}


