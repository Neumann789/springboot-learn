package jode.flow;

import java.io.IOException;
import java.util.Set;
import jode.AssertError;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.ConstOperator;
import jode.expr.Expression;
import jode.type.Type;

public class CaseBlock
  extends StructuredBlock
{
  StructuredBlock subBlock;
  int value;
  boolean isDefault = false;
  boolean isFallThrough = false;
  boolean isLastBlock = false;
  
  public CaseBlock(int paramInt)
  {
    this.value = paramInt;
    this.subBlock = null;
  }
  
  public CaseBlock(int paramInt, Jump paramJump)
  {
    this.value = paramInt;
    this.subBlock = new EmptyBlock(paramJump);
    this.subBlock.outer = this;
  }
  
  public void checkConsistent()
  {
    if (!(this.outer instanceof SwitchBlock)) {
      throw new AssertError("Inconsistency");
    }
    super.checkConsistent();
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
  
  protected boolean wantBraces()
  {
    StructuredBlock localStructuredBlock = this.subBlock;
    if (localStructuredBlock == null) {
      return false;
    }
    for (;;)
    {
      if ((localStructuredBlock.declare != null) && (!localStructuredBlock.declare.isEmpty())) {
        return true;
      }
      if (!(localStructuredBlock instanceof SequentialBlock)) {
        return ((localStructuredBlock instanceof InstructionBlock)) && (((InstructionBlock)localStructuredBlock).isDeclaration);
      }
      StructuredBlock[] arrayOfStructuredBlock = localStructuredBlock.getSubBlocks();
      if (((arrayOfStructuredBlock[0] instanceof InstructionBlock)) && (((InstructionBlock)arrayOfStructuredBlock[0]).isDeclaration)) {
        return true;
      }
      localStructuredBlock = arrayOfStructuredBlock[1];
    }
  }
  
  public StructuredBlock[] getSubBlocks()
  {
    return this.subBlock != null ? new StructuredBlock[] { this.subBlock } : new StructuredBlock[0];
  }
  
  public void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    if (this.isDefault)
    {
      if ((this.isLastBlock) && ((this.subBlock instanceof EmptyBlock)) && (this.subBlock.jump == null)) {
        return;
      }
      if (((this.subBlock instanceof BreakBlock)) && (((BreakBlock)this.subBlock).breaksBlock == this))
      {
        if (this.isFallThrough)
        {
          paramTabbedPrintWriter.tab();
          this.subBlock.dumpSource(paramTabbedPrintWriter);
          paramTabbedPrintWriter.untab();
        }
        return;
      }
      if (this.isFallThrough)
      {
        paramTabbedPrintWriter.tab();
        paramTabbedPrintWriter.println("/* fall through */");
        paramTabbedPrintWriter.untab();
      }
      paramTabbedPrintWriter.print("default:");
    }
    else
    {
      if (this.isFallThrough)
      {
        paramTabbedPrintWriter.tab();
        paramTabbedPrintWriter.println("/* fall through */");
        paramTabbedPrintWriter.untab();
      }
      ConstOperator localConstOperator = new ConstOperator(new Integer(this.value));
      Type localType = ((SwitchBlock)this.outer).getInstruction().getType();
      localConstOperator.setType(localType);
      localConstOperator.makeInitializer(localType);
      paramTabbedPrintWriter.print("case " + localConstOperator.toString() + ":");
    }
    if (this.subBlock != null)
    {
      boolean bool = wantBraces();
      if (bool) {
        paramTabbedPrintWriter.openBrace();
      } else {
        paramTabbedPrintWriter.println();
      }
      if (this.subBlock != null)
      {
        paramTabbedPrintWriter.tab();
        this.subBlock.dumpSource(paramTabbedPrintWriter);
        paramTabbedPrintWriter.untab();
      }
      if (bool) {
        paramTabbedPrintWriter.closeBrace();
      }
    }
    else
    {
      paramTabbedPrintWriter.println();
    }
  }
  
  public boolean jumpMayBeChanged()
  {
    return (this.subBlock.jump != null) || (this.subBlock.jumpMayBeChanged());
  }
}


