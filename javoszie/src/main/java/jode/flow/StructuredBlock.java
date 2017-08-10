package jode.flow;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import jode.AssertError;
import jode.GlobalOptions;
import jode.decompiler.ClassAnalyzer;
import jode.decompiler.Declarable;
import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;
import jode.util.SimpleSet;

public abstract class StructuredBlock
{
  Set used;
  Set declare;
  Set done;
  StructuredBlock outer;
  FlowBlock flowBlock;
  Jump jump;
  
  public StructuredBlock getNextBlock()
  {
    if (this.jump != null) {
      return null;
    }
    if (this.outer != null) {
      return this.outer.getNextBlock(this);
    }
    return null;
  }
  
  public void setJump(Jump paramJump)
  {
    this.jump = paramJump;
    paramJump.prev = this;
  }
  
  public FlowBlock getNextFlowBlock()
  {
    if (this.jump != null) {
      return this.jump.destination;
    }
    if (this.outer != null) {
      return this.outer.getNextFlowBlock(this);
    }
    return null;
  }
  
  public StructuredBlock getNextBlock(StructuredBlock paramStructuredBlock)
  {
    return getNextBlock();
  }
  
  public FlowBlock getNextFlowBlock(StructuredBlock paramStructuredBlock)
  {
    return getNextFlowBlock();
  }
  
  public boolean isEmpty()
  {
    return false;
  }
  
  public boolean isSingleExit(StructuredBlock paramStructuredBlock)
  {
    return false;
  }
  
  public boolean replaceSubBlock(StructuredBlock paramStructuredBlock1, StructuredBlock paramStructuredBlock2)
  {
    return false;
  }
  
  public StructuredBlock[] getSubBlocks()
  {
    return new StructuredBlock[0];
  }
  
  public boolean contains(StructuredBlock paramStructuredBlock)
  {
    while ((paramStructuredBlock != this) && (paramStructuredBlock != null)) {
      paramStructuredBlock = paramStructuredBlock.outer;
    }
    return paramStructuredBlock == this;
  }
  
  public final void removeJump()
  {
    if (this.jump != null)
    {
      this.jump.prev = null;
      this.jump = null;
    }
  }
  
  void moveDefinitions(StructuredBlock paramStructuredBlock1, StructuredBlock paramStructuredBlock2) {}
  
  public void replace(StructuredBlock paramStructuredBlock)
  {
    this.outer = paramStructuredBlock.outer;
    setFlowBlock(paramStructuredBlock.flowBlock);
    if (this.outer != null) {
      this.outer.replaceSubBlock(paramStructuredBlock, this);
    } else {
      this.flowBlock.block = this;
    }
  }
  
  public void swapJump(StructuredBlock paramStructuredBlock)
  {
    Jump localJump = paramStructuredBlock.jump;
    paramStructuredBlock.jump = this.jump;
    this.jump = localJump;
    this.jump.prev = this;
    paramStructuredBlock.jump.prev = paramStructuredBlock;
  }
  
  public void moveJump(Jump paramJump)
  {
    if (this.jump != null) {
      throw new AssertError("overriding with moveJump()");
    }
    this.jump = paramJump;
    if (paramJump != null)
    {
      paramJump.prev.jump = null;
      paramJump.prev = this;
    }
  }
  
  public void copyJump(Jump paramJump)
  {
    if (this.jump != null) {
      throw new AssertError("overriding with moveJump()");
    }
    if (paramJump != null)
    {
      this.jump = new Jump(paramJump);
      this.jump.prev = this;
    }
  }
  
  public StructuredBlock appendBlock(StructuredBlock paramStructuredBlock)
  {
    if ((paramStructuredBlock instanceof EmptyBlock))
    {
      moveJump(paramStructuredBlock.jump);
      return this;
    }
    SequentialBlock localSequentialBlock = new SequentialBlock();
    localSequentialBlock.replace(this);
    localSequentialBlock.setFirst(this);
    localSequentialBlock.setSecond(paramStructuredBlock);
    return localSequentialBlock;
  }
  
  public StructuredBlock prependBlock(StructuredBlock paramStructuredBlock)
  {
    SequentialBlock localSequentialBlock = new SequentialBlock();
    localSequentialBlock.replace(this);
    localSequentialBlock.setFirst(paramStructuredBlock);
    localSequentialBlock.setSecond(this);
    return localSequentialBlock;
  }
  
  public final void removeBlock()
  {
    if ((this.outer instanceof SequentialBlock))
    {
      if (this.outer.getSubBlocks()[1] == this)
      {
        if (this.jump != null) {
          this.outer.getSubBlocks()[0].moveJump(this.jump);
        }
        this.outer.getSubBlocks()[0].replace(this.outer);
      }
      else
      {
        this.outer.getSubBlocks()[1].replace(this.outer);
      }
      return;
    }
    EmptyBlock localEmptyBlock = new EmptyBlock();
    localEmptyBlock.moveJump(this.jump);
    localEmptyBlock.replace(this);
  }
  
  public boolean flowMayBeChanged()
  {
    return (this.jump != null) || (jumpMayBeChanged());
  }
  
  public boolean jumpMayBeChanged()
  {
    return false;
  }
  
  public Set getDeclarables()
  {
    return Collections.EMPTY_SET;
  }
  
  public Set propagateUsage()
  {
    this.used = new SimpleSet();
    this.used.addAll(getDeclarables());
    StructuredBlock[] arrayOfStructuredBlock = getSubBlocks();
    SimpleSet localSimpleSet1 = new SimpleSet();
    localSimpleSet1.addAll(this.used);
    for (int i = 0; i < arrayOfStructuredBlock.length; i++)
    {
      Set localSet = arrayOfStructuredBlock[i].propagateUsage();
      SimpleSet localSimpleSet2 = new SimpleSet();
      localSimpleSet2.addAll(localSet);
      localSimpleSet2.retainAll(localSimpleSet1);
      this.used.addAll(localSimpleSet2);
      localSimpleSet1.addAll(localSet);
    }
    return localSimpleSet1;
  }
  
  public VariableStack mapStackToLocal(VariableStack paramVariableStack)
  {
    StructuredBlock[] arrayOfStructuredBlock = getSubBlocks();
    VariableStack localVariableStack;
    if (arrayOfStructuredBlock.length == 0)
    {
      localVariableStack = paramVariableStack;
    }
    else
    {
      localVariableStack = null;
      for (int i = 0; i < arrayOfStructuredBlock.length; i++) {
        localVariableStack = VariableStack.merge(localVariableStack, arrayOfStructuredBlock[i].mapStackToLocal(paramVariableStack));
      }
    }
    if (this.jump != null)
    {
      this.jump.stackMap = localVariableStack;
      return null;
    }
    return localVariableStack;
  }
  
  public void removePush()
  {
    StructuredBlock[] arrayOfStructuredBlock = getSubBlocks();
    for (int i = 0; i < arrayOfStructuredBlock.length; i++) {
      arrayOfStructuredBlock[i].removePush();
    }
  }
  
  public void removeOnetimeLocals()
  {
    StructuredBlock[] arrayOfStructuredBlock = getSubBlocks();
    for (int i = 0; i < arrayOfStructuredBlock.length; i++) {
      arrayOfStructuredBlock[i].removeOnetimeLocals();
    }
  }
  
  public void makeDeclaration(Set paramSet)
  {
    this.done = new SimpleSet();
    this.done.addAll(paramSet);
    this.declare = new SimpleSet();
    Iterator localIterator1 = this.used.iterator();
    while (localIterator1.hasNext())
    {
      localObject1 = (Declarable)localIterator1.next();
      if (!paramSet.contains(localObject1))
      {
        Object localObject2;
        Object localObject3;
        if ((localObject1 instanceof LocalInfo))
        {
          localObject2 = (LocalInfo)localObject1;
          localObject3 = ((LocalInfo)localObject2).guessName();
          Iterator localIterator2 = paramSet.iterator();
          for (;;)
          {
            if (!localIterator2.hasNext()) {
              break label259;
            }
            Declarable localDeclarable = (Declarable)localIterator2.next();
            if ((localDeclarable instanceof LocalInfo))
            {
              LocalInfo localLocalInfo = (LocalInfo)localDeclarable;
              if ((localLocalInfo.getMethodAnalyzer() == ((LocalInfo)localObject2).getMethodAnalyzer()) && (localLocalInfo.getSlot() == ((LocalInfo)localObject2).getSlot()) && (localLocalInfo.getType().isOfType(((LocalInfo)localObject2).getType())) && ((localLocalInfo.isNameGenerated()) || (((LocalInfo)localObject2).isNameGenerated()) || (((String)localObject3).equals(localLocalInfo.getName()))) && (!localLocalInfo.isFinal()) && (!((LocalInfo)localObject2).isFinal()) && (localLocalInfo.getExpression() == null) && (((LocalInfo)localObject2).getExpression() == null))
              {
                ((LocalInfo)localObject2).combineWith(localLocalInfo);
                break;
              }
            }
          }
        }
        label259:
        if (((Declarable)localObject1).getName() != null)
        {
          localObject2 = paramSet.iterator();
          while (((Iterator)localObject2).hasNext())
          {
            localObject3 = (Declarable)((Iterator)localObject2).next();
            if (((Declarable)localObject1).getName().equals(((Declarable)localObject3).getName()))
            {
              ((Declarable)localObject1).makeNameUnique();
              break;
            }
          }
        }
        paramSet.add(localObject1);
        this.declare.add(localObject1);
        if ((localObject1 instanceof ClassAnalyzer)) {
          ((ClassAnalyzer)localObject1).makeDeclaration(paramSet);
        }
      }
    }
    Object localObject1 = getSubBlocks();
    for (int i = 0; i < localObject1.length; i++) {
      localObject1[i].makeDeclaration(paramSet);
    }
    paramSet.removeAll(this.declare);
  }
  
  public void checkConsistent()
  {
    StructuredBlock[] arrayOfStructuredBlock = getSubBlocks();
    for (int i = 0; i < arrayOfStructuredBlock.length; i++)
    {
      if ((arrayOfStructuredBlock[i].outer != this) || (arrayOfStructuredBlock[i].flowBlock != this.flowBlock)) {
        throw new AssertError("Inconsistency");
      }
      arrayOfStructuredBlock[i].checkConsistent();
    }
    if ((this.jump != null) && (this.jump.destination != null)) {
      for (Jump localJump = this.flowBlock.getJumps(this.jump.destination); localJump != this.jump; localJump = localJump.next) {
        if (localJump == null) {
          throw new AssertError("Inconsistency");
        }
      }
    }
  }
  
  public void setFlowBlock(FlowBlock paramFlowBlock)
  {
    if (this.flowBlock != paramFlowBlock)
    {
      this.flowBlock = paramFlowBlock;
      StructuredBlock[] arrayOfStructuredBlock = getSubBlocks();
      for (int i = 0; i < arrayOfStructuredBlock.length; i++) {
        if (arrayOfStructuredBlock[i] != null) {
          arrayOfStructuredBlock[i].setFlowBlock(paramFlowBlock);
        }
      }
    }
  }
  
  public boolean needsBraces()
  {
    return true;
  }
  
  public void fillInGenSet(Set paramSet1, Set paramSet2) {}
  
  public void fillSuccessors()
  {
    if (this.jump != null) {
      this.flowBlock.addSuccessor(this.jump);
    }
    StructuredBlock[] arrayOfStructuredBlock = getSubBlocks();
    for (int i = 0; i < arrayOfStructuredBlock.length; i++) {
      arrayOfStructuredBlock[i].fillSuccessors();
    }
  }
  
  public void dumpSource(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    if ((GlobalOptions.debuggingFlags & 0x100) != 0)
    {
      if (this.declare != null) {
        paramTabbedPrintWriter.println("declaring: " + this.declare);
      }
      if (this.done != null) {
        paramTabbedPrintWriter.println("done: " + this.done);
      }
      paramTabbedPrintWriter.println("using: " + this.used);
    }
    if (this.declare != null)
    {
      Iterator localIterator = this.declare.iterator();
      while (localIterator.hasNext())
      {
        Declarable localDeclarable = (Declarable)localIterator.next();
        localDeclarable.dumpDeclaration(paramTabbedPrintWriter);
        paramTabbedPrintWriter.println(";");
      }
    }
    dumpInstruction(paramTabbedPrintWriter);
    if (this.jump != null) {
      this.jump.dumpSource(paramTabbedPrintWriter);
    }
  }
  
  public abstract void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException;
  
  public String toString()
  {
    try
    {
      StringWriter localStringWriter = new StringWriter();
      TabbedPrintWriter localTabbedPrintWriter = new TabbedPrintWriter(localStringWriter);
      localTabbedPrintWriter.println(super.toString());
      localTabbedPrintWriter.tab();
      dumpSource(localTabbedPrintWriter);
      return localStringWriter.toString();
    }
    catch (IOException localIOException) {}
    return super.toString();
  }
  
  public void simplify()
  {
    StructuredBlock[] arrayOfStructuredBlock = getSubBlocks();
    for (int i = 0; i < arrayOfStructuredBlock.length; i++) {
      arrayOfStructuredBlock[i].simplify();
    }
  }
  
  public boolean doTransformations()
  {
    return false;
  }
}


