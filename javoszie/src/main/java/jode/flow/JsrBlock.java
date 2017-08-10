package jode.flow;

import java.io.IOException;
import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class JsrBlock
  extends StructuredBlock
{
  StructuredBlock innerBlock;
  boolean good = false;
  
  public JsrBlock(Jump paramJump1, Jump paramJump2)
  {
    this.innerBlock = new EmptyBlock(paramJump1);
    this.innerBlock.outer = this;
    setJump(paramJump2);
  }
  
  public void setGood(boolean paramBoolean)
  {
    this.good = paramBoolean;
  }
  
  public boolean isGood()
  {
    return this.good;
  }
  
  public boolean replaceSubBlock(StructuredBlock paramStructuredBlock1, StructuredBlock paramStructuredBlock2)
  {
    if (this.innerBlock == paramStructuredBlock1) {
      this.innerBlock = paramStructuredBlock2;
    } else {
      return false;
    }
    return true;
  }
  
  public VariableStack mapStackToLocal(VariableStack paramVariableStack)
  {
    LocalInfo localLocalInfo = new LocalInfo();
    localLocalInfo.setType(Type.tUObject);
    this.innerBlock.mapStackToLocal(paramVariableStack.push(localLocalInfo));
    if (this.jump != null)
    {
      this.jump.stackMap = paramVariableStack;
      return null;
    }
    return paramVariableStack;
  }
  
  public StructuredBlock[] getSubBlocks()
  {
    return new StructuredBlock[] { this.innerBlock };
  }
  
  public void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.println("JSR");
    paramTabbedPrintWriter.tab();
    this.innerBlock.dumpSource(paramTabbedPrintWriter);
    paramTabbedPrintWriter.untab();
  }
}


