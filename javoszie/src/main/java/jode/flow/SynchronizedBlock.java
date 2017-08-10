package jode.flow;

import java.io.IOException;
import java.util.Set;
import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.Expression;
import jode.util.SimpleSet;

public class SynchronizedBlock
  extends StructuredBlock
{
  Expression object;
  LocalInfo local;
  boolean isEntered;
  StructuredBlock bodyBlock;
  
  public SynchronizedBlock(LocalInfo paramLocalInfo)
  {
    this.local = paramLocalInfo;
  }
  
  public void setBodyBlock(StructuredBlock paramStructuredBlock)
  {
    this.bodyBlock = paramStructuredBlock;
    paramStructuredBlock.outer = this;
    paramStructuredBlock.setFlowBlock(this.flowBlock);
  }
  
  public StructuredBlock[] getSubBlocks()
  {
    return new StructuredBlock[] { this.bodyBlock };
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
  
  public Set getDeclarables()
  {
    SimpleSet localSimpleSet = new SimpleSet();
    if (this.object != null) {
      this.object.fillDeclarables(localSimpleSet);
    } else {
      localSimpleSet.add(this.local);
    }
    return localSimpleSet;
  }
  
  public void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    if (!this.isEntered) {
      paramTabbedPrintWriter.println("MISSING MONITORENTER");
    }
    paramTabbedPrintWriter.print("synchronized (");
    if (this.object != null) {
      this.object.dumpExpression(0, paramTabbedPrintWriter);
    } else {
      paramTabbedPrintWriter.print(this.local.getName());
    }
    paramTabbedPrintWriter.print(")");
    paramTabbedPrintWriter.openBrace();
    paramTabbedPrintWriter.tab();
    this.bodyBlock.dumpSource(paramTabbedPrintWriter);
    paramTabbedPrintWriter.untab();
    paramTabbedPrintWriter.closeBrace();
  }
  
  public void simplify()
  {
    if (this.object != null) {
      this.object = this.object.simplify();
    }
    super.simplify();
  }
  
  public boolean doTransformations()
  {
    StructuredBlock localStructuredBlock = this.flowBlock.lastModified;
    return ((!this.isEntered) && (CompleteSynchronized.enter(this, localStructuredBlock))) || ((this.isEntered) && (this.object == null) && (CompleteSynchronized.combineObject(this, localStructuredBlock)));
  }
}


