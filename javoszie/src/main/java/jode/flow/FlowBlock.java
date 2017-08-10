package jode.flow;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import jode.AssertError;
import jode.GlobalOptions;
import jode.decompiler.LocalInfo;
import jode.decompiler.MethodAnalyzer;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.CombineableOperator;
import jode.expr.Expression;
import jode.util.SimpleMap;

public class FlowBlock
{
  public static FlowBlock END_OF_METHOD = new FlowBlock(null, Integer.MAX_VALUE);
  public static FlowBlock NEXT_BY_ADDR;
  MethodAnalyzer method;
  private SlotSet in = new SlotSet();
  VariableSet gen = new VariableSet();
  private int addr;
  private int length;
  StructuredBlock block;
  StructuredBlock lastModified;
  private Map successors = new SimpleMap();
  List predecessors = new ArrayList();
  FlowBlock nextByAddr;
  FlowBlock prevByAddr;
  VariableStack stackMap;
  static int serialno = 0;
  String label = null;
  
  public FlowBlock(MethodAnalyzer paramMethodAnalyzer, int paramInt)
  {
    this.method = paramMethodAnalyzer;
    this.addr = paramInt;
  }
  
  public final int getNextAddr()
  {
    return this.addr + this.length;
  }
  
  public boolean hasNoJumps()
  {
    return (this.successors.size() == 0) && (this.predecessors.size() == 0);
  }
  
  public Jump resolveSomeJumps(Jump paramJump, FlowBlock paramFlowBlock)
  {
    Object localObject1 = null;
    if (this.lastModified.jump == null)
    {
      localJump = new Jump(paramFlowBlock);
      this.lastModified.setJump(localJump);
      localObject1 = localJump;
    }
    StructuredBlock localStructuredBlock1;
    Object localObject2;
    Object localObject3;
    for (Jump localJump = paramJump; localJump != null; localJump = localJump.next) {
      if (((localJump.prev.outer instanceof ConditionalBlock)) && (localJump.prev.outer.jump != null))
      {
        localStructuredBlock1 = localJump.prev;
        localObject2 = (ConditionalBlock)localStructuredBlock1.outer;
        localObject3 = ((ConditionalBlock)localObject2).getInstruction();
        ((ConditionalBlock)localObject2).setInstruction(((Expression)localObject3).negate());
        ((ConditionalBlock)localObject2).swapJump(localStructuredBlock1);
      }
    }
    while (paramJump != null)
    {
      localJump = paramJump;
      paramJump = paramJump.next;
      if (localJump.prev == this.lastModified)
      {
        localJump.next = ((Jump)localObject1);
        localObject1 = localJump;
      }
      else
      {
        Object localObject4;
        if ((localJump.prev.outer instanceof ConditionalBlock))
        {
          localStructuredBlock1 = localJump.prev;
          localObject2 = (ConditionalBlock)localStructuredBlock1.outer;
          localObject3 = ((ConditionalBlock)localObject2).getInstruction();
          if (((ConditionalBlock)localObject2).jump != null)
          {
            localStructuredBlock1.removeJump();
            localObject4 = new IfThenElseBlock(((ConditionalBlock)localObject2).getInstruction().negate());
            ((IfThenElseBlock)localObject4).moveDefinitions((StructuredBlock)localObject2, localStructuredBlock1);
            ((IfThenElseBlock)localObject4).replace((StructuredBlock)localObject2);
            ((IfThenElseBlock)localObject4).moveJump(((ConditionalBlock)localObject2).jump);
            ((IfThenElseBlock)localObject4).setThenBlock(localStructuredBlock1);
            if (localObject2 != this.lastModified) {
              continue;
            }
            this.lastModified = ((StructuredBlock)localObject4);
            continue;
          }
          Object localObject5;
          if (((((ConditionalBlock)localObject2).outer instanceof LoopBlock)) || (((((ConditionalBlock)localObject2).outer instanceof SequentialBlock)) && (localObject2.outer.getSubBlocks()[0] == localObject2) && ((((ConditionalBlock)localObject2).outer.outer instanceof LoopBlock))))
          {
            localObject4 = (((ConditionalBlock)localObject2).outer instanceof LoopBlock) ? (LoopBlock)((ConditionalBlock)localObject2).outer : (LoopBlock)((ConditionalBlock)localObject2).outer.outer;
            if ((((LoopBlock)localObject4).getCondition() == LoopBlock.TRUE) && (((LoopBlock)localObject4).getType() != 1) && ((((LoopBlock)localObject4).jumpMayBeChanged()) || (((LoopBlock)localObject4).getNextFlowBlock() == paramFlowBlock)))
            {
              if (((LoopBlock)localObject4).jump == null)
              {
                ((LoopBlock)localObject4).moveJump(localJump);
                paramJump = localJump;
              }
              else
              {
                localJump.prev.removeJump();
              }
              ((LoopBlock)localObject4).setCondition(((Expression)localObject3).negate());
              ((LoopBlock)localObject4).moveDefinitions((StructuredBlock)localObject2, null);
              ((ConditionalBlock)localObject2).removeBlock();
              continue;
            }
          }
          else if (((((ConditionalBlock)localObject2).outer instanceof SequentialBlock)) && (localObject2.outer.getSubBlocks()[1] == localObject2))
          {
            for (localObject4 = ((ConditionalBlock)localObject2).outer.outer; (localObject4 instanceof SequentialBlock); localObject4 = ((StructuredBlock)localObject4).outer) {}
            if ((localObject4 instanceof LoopBlock))
            {
              localObject5 = (LoopBlock)localObject4;
              if ((((LoopBlock)localObject5).getCondition() == LoopBlock.TRUE) && (((LoopBlock)localObject5).getType() == 0) && ((((LoopBlock)localObject5).jumpMayBeChanged()) || (((LoopBlock)localObject5).getNextFlowBlock() == paramFlowBlock)))
              {
                if (((LoopBlock)localObject5).jump == null)
                {
                  ((LoopBlock)localObject5).moveJump(localJump);
                  paramJump = localJump;
                }
                else
                {
                  localJump.prev.removeJump();
                }
                ((LoopBlock)localObject5).setType(1);
                ((LoopBlock)localObject5).setCondition(((Expression)localObject3).negate());
                ((LoopBlock)localObject5).moveDefinitions((StructuredBlock)localObject2, null);
                ((ConditionalBlock)localObject2).removeBlock();
                continue;
              }
            }
          }
          if (((((ConditionalBlock)localObject2).outer instanceof SequentialBlock)) && (localObject2.outer.getSubBlocks()[0] == localObject2) && ((((ConditionalBlock)localObject2).outer.getNextFlowBlock() == paramFlowBlock) || (((ConditionalBlock)localObject2).outer.jumpMayBeChanged())))
          {
            localObject4 = (SequentialBlock)((ConditionalBlock)localObject2).outer;
            localObject5 = new IfThenElseBlock(((Expression)localObject3).negate());
            StructuredBlock localStructuredBlock2 = localObject4.getSubBlocks()[1];
            ((IfThenElseBlock)localObject5).moveDefinitions((StructuredBlock)localObject4, localStructuredBlock2);
            ((IfThenElseBlock)localObject5).replace((StructuredBlock)localObject4);
            ((IfThenElseBlock)localObject5).setThenBlock(localStructuredBlock2);
            if (localStructuredBlock2.contains(this.lastModified))
            {
              if (this.lastModified.jump.destination == paramFlowBlock)
              {
                ((IfThenElseBlock)localObject5).moveJump(this.lastModified.jump);
                this.lastModified = ((StructuredBlock)localObject5);
                localJump.prev.removeJump();
                continue;
              }
              this.lastModified = ((StructuredBlock)localObject5);
            }
            ((IfThenElseBlock)localObject5).moveJump(localJump);
            paramJump = localJump;
            continue;
          }
        }
        else
        {
          if (localJump.destination == localJump.prev.outer.getNextFlowBlock(localJump.prev))
          {
            localJump.prev.removeJump();
            continue;
          }
          for (localStructuredBlock1 = localJump.prev.outer; (localStructuredBlock1 instanceof SequentialBlock); localStructuredBlock1 = localStructuredBlock1.outer) {}
          if ((localStructuredBlock1 instanceof IfThenElseBlock))
          {
            localObject2 = (IfThenElseBlock)localStructuredBlock1;
            if ((((IfThenElseBlock)localObject2).elseBlock == null) && (((IfThenElseBlock)localObject2).jump != null))
            {
              ((IfThenElseBlock)localObject2).setElseBlock(new EmptyBlock());
              ((IfThenElseBlock)localObject2).elseBlock.moveJump(((IfThenElseBlock)localObject2).jump);
              ((IfThenElseBlock)localObject2).moveJump(localJump);
              paramJump = localJump;
              continue;
            }
          }
          if (((localStructuredBlock1 instanceof IfThenElseBlock)) && ((localStructuredBlock1.outer instanceof SequentialBlock)) && (localStructuredBlock1.outer.getSubBlocks()[0] == localStructuredBlock1))
          {
            localObject2 = (IfThenElseBlock)localStructuredBlock1;
            localObject3 = (SequentialBlock)localStructuredBlock1.outer;
            localObject4 = localObject3.subBlocks[1];
            if ((((IfThenElseBlock)localObject2).elseBlock == null) && ((((StructuredBlock)localObject4).getNextFlowBlock() == paramFlowBlock) || (((StructuredBlock)localObject4).jump != null) || (((StructuredBlock)localObject4).jumpMayBeChanged())))
            {
              ((IfThenElseBlock)localObject2).replace((StructuredBlock)localObject3);
              ((IfThenElseBlock)localObject2).setElseBlock((StructuredBlock)localObject4);
              if (((StructuredBlock)localObject4).contains(this.lastModified))
              {
                if (this.lastModified.jump.destination == paramFlowBlock)
                {
                  ((IfThenElseBlock)localObject2).moveJump(this.lastModified.jump);
                  this.lastModified = ((StructuredBlock)localObject2);
                  localJump.prev.removeJump();
                  continue;
                }
                this.lastModified = ((StructuredBlock)localObject2);
              }
              ((IfThenElseBlock)localObject2).moveJump(localJump);
              paramJump = localJump;
              continue;
            }
          }
        }
        for (localStructuredBlock1 = localJump.prev.outer; localStructuredBlock1 != null; localStructuredBlock1 = localStructuredBlock1.outer) {
          if ((localStructuredBlock1 instanceof BreakableBlock))
          {
            if (localStructuredBlock1.getNextFlowBlock() == paramFlowBlock) {
              break;
            }
            if (localStructuredBlock1.jumpMayBeChanged())
            {
              localStructuredBlock1.setJump(new Jump(paramFlowBlock));
              localStructuredBlock1.jump.next = paramJump;
              paramJump = localStructuredBlock1.jump;
            }
            else
            {
              if (paramFlowBlock == END_OF_METHOD) {
                break;
              }
            }
          }
        }
        localJump.next = ((Jump)localObject1);
        localObject1 = localJump;
      }
    }
    return (Jump)localObject1;
  }
  
  void resolveRemaining(Jump paramJump)
  {
    LoopBlock localLoopBlock = null;
    StructuredBlock localStructuredBlock1 = this.lastModified;
    int i = 0;
    while (paramJump != null)
    {
      StructuredBlock localStructuredBlock2 = paramJump.prev;
      if (localStructuredBlock2 == this.lastModified)
      {
        i = 1;
      }
      else
      {
        int j = 0;
        BreakableBlock localBreakableBlock = null;
        for (StructuredBlock localStructuredBlock3 = localStructuredBlock2.outer; localStructuredBlock3 != null; localStructuredBlock3 = localStructuredBlock3.outer) {
          if ((localStructuredBlock3 instanceof BreakableBlock))
          {
            j++;
            if (localStructuredBlock3.getNextFlowBlock() == paramJump.destination)
            {
              localBreakableBlock = (BreakableBlock)localStructuredBlock3;
              break;
            }
          }
        }
        localStructuredBlock2.removeJump();
        if (localBreakableBlock == null)
        {
          if (localLoopBlock == null) {
            localLoopBlock = new LoopBlock(1, LoopBlock.FALSE);
          }
          while (!localStructuredBlock1.contains(localStructuredBlock2)) {
            localStructuredBlock1 = localStructuredBlock1.outer;
          }
          localStructuredBlock2.appendBlock(new BreakBlock(localLoopBlock, j > 0));
        }
        else
        {
          localStructuredBlock2.appendBlock(new BreakBlock(localBreakableBlock, j > 1));
        }
      }
      paramJump = paramJump.next;
    }
    if (i != 0) {
      this.lastModified.removeJump();
    }
    if (localLoopBlock != null)
    {
      localLoopBlock.replace(localStructuredBlock1);
      localLoopBlock.setBody(localStructuredBlock1);
      this.lastModified = localLoopBlock;
    }
  }
  
  void mergeSuccessors(FlowBlock paramFlowBlock)
  {
    Iterator localIterator = paramFlowBlock.successors.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      FlowBlock localFlowBlock = (FlowBlock)localEntry.getKey();
      SuccessorInfo localSuccessorInfo1 = (SuccessorInfo)localEntry.getValue();
      SuccessorInfo localSuccessorInfo2 = (SuccessorInfo)this.successors.get(localFlowBlock);
      if (localFlowBlock != END_OF_METHOD) {
        localFlowBlock.predecessors.remove(paramFlowBlock);
      }
      if (localSuccessorInfo2 == null)
      {
        if (localFlowBlock != END_OF_METHOD) {
          localFlowBlock.predecessors.add(this);
        }
        this.successors.put(localFlowBlock, localSuccessorInfo1);
      }
      else
      {
        localSuccessorInfo2.gen.addAll(localSuccessorInfo1.gen);
        localSuccessorInfo2.kill.retainAll(localSuccessorInfo1.kill);
        for (Jump localJump = localSuccessorInfo2.jumps; localJump.next != null; localJump = localJump.next) {}
        localJump.next = localSuccessorInfo1.jumps;
      }
    }
  }
  
  public void mergeAddr(FlowBlock paramFlowBlock)
  {
    if ((paramFlowBlock.nextByAddr == this) || (paramFlowBlock.prevByAddr == null))
    {
      paramFlowBlock.nextByAddr.addr = paramFlowBlock.addr;
      paramFlowBlock.nextByAddr.length += paramFlowBlock.length;
      paramFlowBlock.nextByAddr.prevByAddr = paramFlowBlock.prevByAddr;
      if (paramFlowBlock.prevByAddr != null) {
        paramFlowBlock.prevByAddr.nextByAddr = paramFlowBlock.nextByAddr;
      }
    }
    else
    {
      paramFlowBlock.prevByAddr.length += paramFlowBlock.length;
      paramFlowBlock.prevByAddr.nextByAddr = paramFlowBlock.nextByAddr;
      if (paramFlowBlock.nextByAddr != null) {
        paramFlowBlock.nextByAddr.prevByAddr = paramFlowBlock.prevByAddr;
      }
    }
  }
  
  void updateInOut(FlowBlock paramFlowBlock, SuccessorInfo paramSuccessorInfo)
  {
    SlotSet localSlotSet1 = paramSuccessorInfo.kill;
    VariableSet localVariableSet = paramSuccessorInfo.gen;
    paramFlowBlock.in.merge(localVariableSet);
    SlotSet localSlotSet2 = (SlotSet)paramFlowBlock.in.clone();
    localSlotSet2.removeAll(localSlotSet1);
    Iterator localIterator = paramFlowBlock.successors.values().iterator();
    while (localIterator.hasNext())
    {
      SuccessorInfo localSuccessorInfo = (SuccessorInfo)localIterator.next();
      localSuccessorInfo.gen.mergeGenKill(localVariableSet, localSuccessorInfo.kill);
      if (paramFlowBlock != this) {
        localSuccessorInfo.kill.mergeKill(localSlotSet1);
      }
    }
    this.in.addAll(localSlotSet2);
    this.gen.addAll(paramFlowBlock.gen);
    if ((GlobalOptions.debuggingFlags & 0x10) != 0)
    {
      GlobalOptions.err.println("UpdateInOut: gens : " + localVariableSet);
      GlobalOptions.err.println("             kills: " + localSlotSet1);
      GlobalOptions.err.println("             s.in : " + paramFlowBlock.in);
      GlobalOptions.err.println("             in   : " + this.in);
    }
  }
  
  public void updateInOutCatch(FlowBlock paramFlowBlock)
  {
    VariableSet localVariableSet = ((TryBlock)this.block).gen;
    paramFlowBlock.in.merge(localVariableSet);
    Iterator localIterator = paramFlowBlock.successors.values().iterator();
    while (localIterator.hasNext())
    {
      SuccessorInfo localSuccessorInfo = (SuccessorInfo)localIterator.next();
      localSuccessorInfo.gen.mergeGenKill(localVariableSet, localSuccessorInfo.kill);
    }
    this.in.addAll(paramFlowBlock.in);
    this.gen.addAll(paramFlowBlock.gen);
    if ((GlobalOptions.debuggingFlags & 0x10) != 0)
    {
      GlobalOptions.err.println("UpdateInOutCatch: gens : " + localVariableSet);
      GlobalOptions.err.println("                  s.in : " + paramFlowBlock.in);
      GlobalOptions.err.println("                  in   : " + this.in);
    }
  }
  
  public void checkConsistent()
  {
    if ((GlobalOptions.debuggingFlags & 0x80) == 0) {
      return;
    }
    try
    {
      if ((this.block.outer != null) || (this.block.flowBlock != this)) {
        throw new AssertError("Inconsistency");
      }
      this.block.checkConsistent();
      Object localObject1 = this.predecessors.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (FlowBlock)((Iterator)localObject1).next();
        if (localObject2 != null) {
          if (!((FlowBlock)localObject2).successors.containsKey(this)) {
            throw new AssertError("Inconsistency");
          }
        }
      }
      for (localObject1 = this.lastModified; ((((StructuredBlock)localObject1).outer instanceof SequentialBlock)) || ((((StructuredBlock)localObject1).outer instanceof TryBlock)) || ((((StructuredBlock)localObject1).outer instanceof FinallyBlock)); localObject1 = ((StructuredBlock)localObject1).outer) {}
      if (((StructuredBlock)localObject1).outer != null) {
        throw new AssertError("Inconsistency");
      }
      Object localObject2 = this.successors.entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        Map.Entry localEntry = (Map.Entry)((Iterator)localObject2).next();
        FlowBlock localFlowBlock = (FlowBlock)localEntry.getKey();
        if (localFlowBlock.predecessors.contains(this) == (localFlowBlock == END_OF_METHOD)) {
          throw new AssertError("Inconsistency");
        }
        Jump localJump = ((SuccessorInfo)localEntry.getValue()).jumps;
        if (localJump == null) {
          throw new AssertError("Inconsistency");
        }
        while (localJump != null)
        {
          if (localJump.destination != localFlowBlock) {
            throw new AssertError("Inconsistency");
          }
          if ((localJump.prev == null) || (localJump.prev.flowBlock != this) || (localJump.prev.jump != localJump)) {
            throw new AssertError("Inconsistency");
          }
          label434:
          for (StructuredBlock localStructuredBlock = localJump.prev; localStructuredBlock != this.block; localStructuredBlock = localStructuredBlock.outer)
          {
            if (localStructuredBlock.outer == null) {
              throw new RuntimeException("Inconsistency");
            }
            StructuredBlock[] arrayOfStructuredBlock = localStructuredBlock.outer.getSubBlocks();
            for (int i = 0; i < arrayOfStructuredBlock.length; i++) {
              if (arrayOfStructuredBlock[i] == localStructuredBlock) {
                break label434;
              }
            }
            throw new AssertError("Inconsistency");
          }
          localJump = localJump.next;
        }
      }
    }
    catch (AssertError localAssertError)
    {
      GlobalOptions.err.println("Inconsistency in: " + this);
      throw localAssertError;
    }
  }
  
  public void appendBlock(StructuredBlock paramStructuredBlock, int paramInt)
  {
    SlotSet localSlotSet1 = new SlotSet();
    SlotSet localSlotSet2 = new SlotSet();
    VariableSet localVariableSet = new VariableSet();
    paramStructuredBlock.fillInGenSet(localSlotSet1, localSlotSet2);
    localVariableSet.addAll(localSlotSet2);
    Object localObject;
    if (this.block == null)
    {
      this.block = paramStructuredBlock;
      this.lastModified = paramStructuredBlock;
      paramStructuredBlock.setFlowBlock(this);
      paramStructuredBlock.fillSuccessors();
      this.length = paramInt;
      this.in = localSlotSet1;
      this.gen = localVariableSet;
      localObject = this.successors.values().iterator();
      while (((Iterator)localObject).hasNext())
      {
        SuccessorInfo localSuccessorInfo = (SuccessorInfo)((Iterator)localObject).next();
        localSuccessorInfo.gen = new VariableSet();
        localSuccessorInfo.kill = new SlotSet();
        localSuccessorInfo.gen.addAll(localVariableSet);
        localSuccessorInfo.kill.addAll(localSlotSet2);
      }
    }
    else if (!(paramStructuredBlock instanceof EmptyBlock))
    {
      checkConsistent();
      if ((GlobalOptions.debuggingFlags & 0x8) != 0) {
        GlobalOptions.err.println("appending Block: " + paramStructuredBlock);
      }
      localObject = (SuccessorInfo)this.successors.get(NEXT_BY_ADDR);
      localSlotSet1.merge(((SuccessorInfo)localObject).gen);
      localSlotSet1.removeAll(((SuccessorInfo)localObject).kill);
      localVariableSet.mergeGenKill(((SuccessorInfo)localObject).gen, localSlotSet2);
      localSlotSet2.mergeKill(((SuccessorInfo)localObject).kill);
      this.in.addAll(localSlotSet1);
      this.gen.addAll(localSlotSet2);
      removeSuccessor(this.lastModified.jump);
      this.lastModified.removeJump();
      this.lastModified = this.lastModified.appendBlock(paramStructuredBlock);
      paramStructuredBlock.fillSuccessors();
      localObject = (SuccessorInfo)this.successors.get(NEXT_BY_ADDR);
      ((SuccessorInfo)localObject).gen = localVariableSet;
      ((SuccessorInfo)localObject).kill = localSlotSet2;
      this.length += paramInt;
      checkConsistent();
      doTransformations();
    }
    checkConsistent();
  }
  
  public void setNextByAddr(FlowBlock paramFlowBlock)
  {
    if ((paramFlowBlock == END_OF_METHOD) || (paramFlowBlock == NEXT_BY_ADDR)) {
      throw new IllegalArgumentException("nextByAddr mustn't be special");
    }
    SuccessorInfo localSuccessorInfo1 = (SuccessorInfo)this.successors.remove(NEXT_BY_ADDR);
    SuccessorInfo localSuccessorInfo2 = (SuccessorInfo)this.successors.get(paramFlowBlock);
    if (localSuccessorInfo1 != null)
    {
      NEXT_BY_ADDR.predecessors.remove(this);
      Jump localJump = localSuccessorInfo1.jumps;
      for (localJump.destination = paramFlowBlock; localJump.next != null; localJump.destination = paramFlowBlock) {
        localJump = localJump.next;
      }
      this.successors.put(paramFlowBlock, localSuccessorInfo1);
      if (localSuccessorInfo2 != null)
      {
        localSuccessorInfo1.gen.addAll(localSuccessorInfo2.gen);
        localSuccessorInfo1.kill.retainAll(localSuccessorInfo2.kill);
        localJump.next = localSuccessorInfo2.jumps;
      }
      else
      {
        paramFlowBlock.predecessors.add(this);
      }
    }
    checkConsistent();
    this.nextByAddr = paramFlowBlock;
    paramFlowBlock.prevByAddr = this;
  }
  
  public boolean doT2(FlowBlock paramFlowBlock)
  {
    if ((paramFlowBlock.predecessors.size() != 1) || (paramFlowBlock.predecessors.get(0) != this)) {
      return false;
    }
    checkConsistent();
    paramFlowBlock.checkConsistent();
    if ((GlobalOptions.debuggingFlags & 0x20) != 0) {
      GlobalOptions.err.println("T2([" + this.addr + "," + getNextAddr() + "],[" + paramFlowBlock.addr + "," + paramFlowBlock.getNextAddr() + "])");
    }
    SuccessorInfo localSuccessorInfo = (SuccessorInfo)this.successors.remove(paramFlowBlock);
    updateInOut(paramFlowBlock, localSuccessorInfo);
    if ((GlobalOptions.debuggingFlags & 0x8) != 0) {
      GlobalOptions.err.println("before Resolve: " + this);
    }
    Jump localJump = resolveSomeJumps(localSuccessorInfo.jumps, paramFlowBlock);
    if ((GlobalOptions.debuggingFlags & 0x8) != 0) {
      GlobalOptions.err.println("before Remaining: " + this);
    }
    resolveRemaining(localJump);
    if ((GlobalOptions.debuggingFlags & 0x8) != 0) {
      GlobalOptions.err.println("after Resolve: " + this);
    }
    this.lastModified = this.lastModified.appendBlock(paramFlowBlock.block);
    mergeSuccessors(paramFlowBlock);
    doTransformations();
    mergeAddr(paramFlowBlock);
    checkConsistent();
    return true;
  }
  
  public void mergeEndBlock()
  {
    checkConsistent();
    SuccessorInfo localSuccessorInfo = (SuccessorInfo)this.successors.remove(END_OF_METHOD);
    if (localSuccessorInfo == null) {
      return;
    }
    Jump localJump = localSuccessorInfo.jumps;
    Object localObject1 = null;
    Object localObject2;
    while (localJump != null)
    {
      localObject2 = localJump;
      localJump = localJump.next;
      if ((((Jump)localObject2).prev instanceof ReturnBlock))
      {
        ((Jump)localObject2).prev.removeJump();
      }
      else
      {
        ((Jump)localObject2).next = ((Jump)localObject1);
        localObject1 = localObject2;
      }
    }
    for (localObject1 = resolveSomeJumps((Jump)localObject1, END_OF_METHOD); localObject1 != null; localObject1 = ((Jump)localObject1).next)
    {
      localObject2 = ((Jump)localObject1).prev;
      if (this.lastModified != localObject2)
      {
        BreakableBlock localBreakableBlock = null;
        for (StructuredBlock localStructuredBlock = ((StructuredBlock)localObject2).outer; localStructuredBlock != null; localStructuredBlock = localStructuredBlock.outer) {
          if ((localStructuredBlock instanceof BreakableBlock))
          {
            if (localStructuredBlock.getNextFlowBlock() != END_OF_METHOD) {
              break;
            }
            localBreakableBlock = (BreakableBlock)localStructuredBlock;
            break;
          }
        }
        ((StructuredBlock)localObject2).removeJump();
        if (localBreakableBlock == null) {
          ((StructuredBlock)localObject2).appendBlock(new ReturnBlock());
        } else {
          ((StructuredBlock)localObject2).appendBlock(new BreakBlock(localBreakableBlock, false));
        }
      }
    }
    if (this.lastModified.jump.destination == END_OF_METHOD) {
      this.lastModified.removeJump();
    }
    doTransformations();
    checkConsistent();
  }
  
  public boolean doT1(int paramInt1, int paramInt2)
  {
    if (!this.predecessors.contains(this)) {
      return false;
    }
    Object localObject1 = this.predecessors.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (FlowBlock)((Iterator)localObject1).next();
      if ((localObject2 != null) && (localObject2 != this) && (((FlowBlock)localObject2).addr >= paramInt1) && (((FlowBlock)localObject2).addr < paramInt2)) {
        return false;
      }
    }
    checkConsistent();
    if ((GlobalOptions.debuggingFlags & 0x20) != 0) {
      GlobalOptions.err.println("T1([" + this.addr + "," + getNextAddr() + "])");
    }
    localObject1 = (SuccessorInfo)this.successors.remove(this);
    updateInOut(this, (SuccessorInfo)localObject1);
    Object localObject2 = ((SuccessorInfo)localObject1).jumps;
    StructuredBlock localStructuredBlock1 = this.block;
    int i = 0;
    LoopBlock localLoopBlock;
    Object localObject3;
    if ((((Jump)localObject2).next == null) && (((Jump)localObject2).prev == this.lastModified) && ((this.lastModified instanceof InstructionBlock)) && (((InstructionBlock)this.lastModified).getInstruction().isVoid()))
    {
      if (((this.lastModified.outer instanceof SequentialBlock)) && ((this.lastModified.outer.getSubBlocks()[0] instanceof LoopBlock)))
      {
        localLoopBlock = (LoopBlock)this.lastModified.outer.getSubBlocks()[0];
        if ((localLoopBlock.cond == LoopBlock.FALSE) && (localLoopBlock.type == 1))
        {
          this.lastModified.removeJump();
          localObject3 = new LoopBlock(2, LoopBlock.TRUE);
          ((LoopBlock)localObject3).replace(localStructuredBlock1);
          ((LoopBlock)localObject3).setBody(localStructuredBlock1);
          ((LoopBlock)localObject3).incrInstr = ((InstructionBlock)this.lastModified).getInstruction();
          ((LoopBlock)localObject3).replaceBreakContinue(localLoopBlock);
          localLoopBlock.bodyBlock.replace(this.lastModified.outer);
          i = 1;
        }
      }
      if ((i == 0) && ((((InstructionBlock)this.lastModified).getInstruction() instanceof CombineableOperator)))
      {
        this.lastModified.removeJump();
        localLoopBlock = new LoopBlock(3, LoopBlock.TRUE);
        localLoopBlock.replace(localStructuredBlock1);
        localLoopBlock.setBody(localStructuredBlock1);
        localLoopBlock.incrBlock = ((InstructionBlock)this.lastModified);
        i = 1;
      }
    }
    if (i == 0)
    {
      localObject2 = resolveSomeJumps((Jump)localObject2, this);
      localLoopBlock = new LoopBlock(0, LoopBlock.TRUE);
      localStructuredBlock1 = this.block;
      localLoopBlock.replace(localStructuredBlock1);
      localLoopBlock.setBody(localStructuredBlock1);
      while (localObject2 != null)
      {
        if (((Jump)localObject2).prev != this.lastModified)
        {
          localObject3 = ((Jump)localObject2).prev;
          int j = 0;
          int k = 0;
          BreakableBlock localBreakableBlock = null;
          for (StructuredBlock localStructuredBlock2 = ((StructuredBlock)localObject3).outer; localStructuredBlock2 != localLoopBlock; localStructuredBlock2 = localStructuredBlock2.outer) {
            if ((localStructuredBlock2 instanceof BreakableBlock))
            {
              if ((localStructuredBlock2 instanceof LoopBlock)) {
                k++;
              }
              j++;
              if (localStructuredBlock2.getNextFlowBlock() == this)
              {
                localBreakableBlock = (BreakableBlock)localStructuredBlock2;
                break;
              }
            }
          }
          ((StructuredBlock)localObject3).removeJump();
          if (localBreakableBlock == null) {
            ((StructuredBlock)localObject3).appendBlock(new ContinueBlock(localLoopBlock, k > 0));
          } else {
            ((StructuredBlock)localObject3).appendBlock(new BreakBlock(localBreakableBlock, j > 1));
          }
        }
        localObject2 = ((Jump)localObject2).next;
      }
      if (this.lastModified.jump.destination == this) {
        this.lastModified.removeJump();
      }
    }
    this.predecessors.remove(this);
    this.lastModified = this.block;
    doTransformations();
    checkConsistent();
    return true;
  }
  
  public void doTransformations()
  {
    if ((GlobalOptions.debuggingFlags & 0x8) != 0) {
      GlobalOptions.err.println("before Transformation: " + this);
    }
    while ((this.lastModified instanceof SequentialBlock)) {
      if (!this.lastModified.getSubBlocks()[0].doTransformations()) {
        this.lastModified = this.lastModified.getSubBlocks()[1];
      }
    }
    while (this.lastModified.doTransformations()) {}
    if ((GlobalOptions.debuggingFlags & 0x8) != 0) {
      GlobalOptions.err.println("after Transformation: " + this);
    }
  }
  
  FlowBlock getSuccessor(int paramInt1, int paramInt2)
  {
    Iterator localIterator = this.successors.keySet().iterator();
    label18:
    FlowBlock localFlowBlock;
    for (Object localObject = null; localIterator.hasNext(); localObject = localFlowBlock)
    {
      localFlowBlock = (FlowBlock)localIterator.next();
      if ((localFlowBlock.addr < paramInt1) || (localFlowBlock.addr >= paramInt2) || (localFlowBlock == this)) {
        break label18;
      }
      if ((localObject != null) && (localFlowBlock.addr >= ((FlowBlock)localObject).addr)) {}
    }
    return (FlowBlock)localObject;
  }
  
  public void analyze()
  {
    analyze(0, Integer.MAX_VALUE);
    mergeEndBlock();
  }
  
  public boolean analyze(int paramInt1, int paramInt2)
  {
    if ((GlobalOptions.debuggingFlags & 0x20) != 0) {
      GlobalOptions.err.println("analyze(" + paramInt1 + ", " + paramInt2 + ")");
    }
    checkConsistent();
    boolean bool = false;
    for (;;)
    {
      if ((this.lastModified instanceof SwitchBlock)) {
        analyzeSwitch(paramInt1, paramInt2);
      }
      if (doT1(paramInt1, paramInt2))
      {
        if ((GlobalOptions.debuggingFlags & 0x8) != 0) {
          GlobalOptions.err.println("after T1: " + this);
        }
        if (this.addr != 0) {
          return true;
        }
      }
      for (FlowBlock localFlowBlock = getSuccessor(paramInt1, paramInt2);; localFlowBlock = getSuccessor(localFlowBlock.addr + 1, paramInt2))
      {
        if (localFlowBlock == null)
        {
          if ((GlobalOptions.debuggingFlags & 0x20) != 0) {
            GlobalOptions.err.println("No more successors applicable: " + paramInt1 + " - " + paramInt2 + "; " + this.addr + " - " + getNextAddr());
          }
          return bool;
        }
        if (((this.nextByAddr == localFlowBlock) || (localFlowBlock.nextByAddr == this)) && (doT2(localFlowBlock)))
        {
          bool = true;
          if ((GlobalOptions.debuggingFlags & 0x8) != 0) {
            GlobalOptions.err.println("after T2: " + this);
          }
        }
        else
        {
          Iterator localIterator = localFlowBlock.predecessors.iterator();
          while (localIterator.hasNext())
          {
            j = ((FlowBlock)localIterator.next()).addr;
            if ((j < paramInt1) || (j >= paramInt2))
            {
              if ((GlobalOptions.debuggingFlags & 0x20) != 0) {
                GlobalOptions.err.println("breaking analyze(" + paramInt1 + ", " + paramInt2 + "); " + this.addr + " - " + getNextAddr());
              }
              return bool;
            }
          }
          int i = localFlowBlock.addr > this.addr ? getNextAddr() : paramInt1;
          int j = localFlowBlock.addr > this.addr ? paramInt2 : this.addr;
          if (localFlowBlock.analyze(i, j)) {
            break;
          }
        }
      }
    }
  }
  
  public boolean analyzeSwitch(int paramInt1, int paramInt2)
  {
    if ((GlobalOptions.debuggingFlags & 0x20) != 0) {
      GlobalOptions.err.println("analyzeSwitch(" + paramInt1 + ", " + paramInt2 + ")");
    }
    SwitchBlock localSwitchBlock = (SwitchBlock)this.lastModified;
    boolean bool = false;
    int i = -1;
    Object localObject = null;
    for (int j = 0; j < localSwitchBlock.caseBlocks.length; j++) {
      if (((localSwitchBlock.caseBlocks[j].subBlock instanceof EmptyBlock)) && (localSwitchBlock.caseBlocks[j].subBlock.jump != null))
      {
        FlowBlock localFlowBlock = localSwitchBlock.caseBlocks[j].subBlock.jump.destination;
        if (localFlowBlock.addr >= paramInt2) {
          break;
        }
        if (localFlowBlock.addr >= paramInt1)
        {
          while (localFlowBlock.analyze(getNextAddr(), paramInt2)) {
            bool = true;
          }
          if ((localFlowBlock.addr != getNextAddr()) || (localFlowBlock.predecessors.size() > 2) || ((localFlowBlock.predecessors.size() > 1) && ((localObject == null) || (!localFlowBlock.predecessors.contains(localObject)))) || (((SuccessorInfo)this.successors.get(localFlowBlock)).jumps.next != null)) {
            break;
          }
          checkConsistent();
          SuccessorInfo localSuccessorInfo1 = (SuccessorInfo)this.successors.remove(localFlowBlock);
          if (localFlowBlock.predecessors.size() == 2)
          {
            SuccessorInfo localSuccessorInfo2 = (SuccessorInfo)((FlowBlock)localObject).successors.remove(localFlowBlock);
            localSuccessorInfo1.kill.retainAll(localSuccessorInfo2.kill);
            localSuccessorInfo1.gen.addAll(localSuccessorInfo2.gen);
            Jump localJump = ((FlowBlock)localObject).resolveSomeJumps(localSuccessorInfo2.jumps, localFlowBlock);
            ((FlowBlock)localObject).resolveRemaining(localJump);
            localSwitchBlock.caseBlocks[(i + 1)].isFallThrough = true;
          }
          updateInOut(localFlowBlock, localSuccessorInfo1);
          if (localObject != null)
          {
            ((FlowBlock)localObject).block.replace(localSwitchBlock.caseBlocks[i].subBlock);
            mergeSuccessors((FlowBlock)localObject);
          }
          localSwitchBlock.caseBlocks[j].subBlock.removeJump();
          mergeAddr(localFlowBlock);
          localObject = localFlowBlock;
          i = j;
          checkConsistent();
          bool = true;
        }
      }
    }
    if (localObject != null)
    {
      ((FlowBlock)localObject).block.replace(localSwitchBlock.caseBlocks[i].subBlock);
      mergeSuccessors((FlowBlock)localObject);
    }
    if ((GlobalOptions.debuggingFlags & 0x8) != 0) {
      GlobalOptions.err.println("after analyzeSwitch: " + this);
    }
    if ((GlobalOptions.debuggingFlags & 0x20) != 0) {
      GlobalOptions.err.println("analyzeSwitch done: " + paramInt1 + " - " + paramInt2 + "; " + this.addr + " - " + getNextAddr());
    }
    checkConsistent();
    return bool;
  }
  
  public void makeStartBlock()
  {
    this.predecessors.add(null);
  }
  
  public void removeSuccessor(Jump paramJump)
  {
    SuccessorInfo localSuccessorInfo = (SuccessorInfo)this.successors.get(paramJump.destination);
    Object localObject = null;
    for (Jump localJump = localSuccessorInfo.jumps; (localJump != paramJump) && (localJump != null); localJump = localJump.next) {
      localObject = localJump;
    }
    if (localJump == null) {
      throw new IllegalArgumentException(this.addr + ": removing non existent jump: " + paramJump);
    }
    if (localObject != null)
    {
      ((Jump)localObject).next = localJump.next;
    }
    else if (localJump.next == null)
    {
      this.successors.remove(paramJump.destination);
      paramJump.destination.predecessors.remove(this);
    }
    else
    {
      localSuccessorInfo.jumps = localJump.next;
    }
  }
  
  public Jump getJumps(FlowBlock paramFlowBlock)
  {
    return ((SuccessorInfo)this.successors.get(paramFlowBlock)).jumps;
  }
  
  public Jump removeJumps(FlowBlock paramFlowBlock)
  {
    if (paramFlowBlock != END_OF_METHOD) {
      paramFlowBlock.predecessors.remove(this);
    }
    return ((SuccessorInfo)this.successors.remove(paramFlowBlock)).jumps;
  }
  
  public Set getSuccessors()
  {
    return this.successors.keySet();
  }
  
  public void addSuccessor(Jump paramJump)
  {
    SuccessorInfo localSuccessorInfo = (SuccessorInfo)this.successors.get(paramJump.destination);
    if (localSuccessorInfo == null)
    {
      localSuccessorInfo = new SuccessorInfo();
      localSuccessorInfo.jumps = paramJump;
      if (paramJump.destination != END_OF_METHOD) {
        paramJump.destination.predecessors.add(this);
      }
      this.successors.put(paramJump.destination, localSuccessorInfo);
    }
    else
    {
      paramJump.next = localSuccessorInfo.jumps;
      localSuccessorInfo.jumps = paramJump;
    }
  }
  
  public final boolean mapStackToLocal()
  {
    mapStackToLocal(VariableStack.EMPTY);
    return true;
  }
  
  public void mapStackToLocal(VariableStack paramVariableStack)
  {
    this.stackMap = paramVariableStack;
    this.block.mapStackToLocal(paramVariableStack);
    Iterator localIterator = this.successors.values().iterator();
    while (localIterator.hasNext())
    {
      SuccessorInfo localSuccessorInfo = (SuccessorInfo)localIterator.next();
      Jump localJump = localSuccessorInfo.jumps;
      FlowBlock localFlowBlock = localJump.destination;
      if (localFlowBlock != END_OF_METHOD)
      {
        VariableStack localVariableStack = localFlowBlock.stackMap;
        while (localJump != null)
        {
          if (localJump.stackMap == null) {
            GlobalOptions.err.println("Dead jump? " + localJump.prev + " in " + this);
          }
          localVariableStack = VariableStack.merge(localVariableStack, localJump.stackMap);
          localJump = localJump.next;
        }
        if (localFlowBlock.stackMap == null) {
          localFlowBlock.mapStackToLocal(localVariableStack);
        }
      }
    }
  }
  
  public void removePush()
  {
    if (this.stackMap == null) {
      return;
    }
    this.stackMap = null;
    this.block.removePush();
    Iterator localIterator = this.successors.keySet().iterator();
    while (localIterator.hasNext())
    {
      FlowBlock localFlowBlock = (FlowBlock)localIterator.next();
      localFlowBlock.removePush();
    }
  }
  
  public void removeOnetimeLocals()
  {
    this.block.removeOnetimeLocals();
    if (this.nextByAddr != null) {
      this.nextByAddr.removeOnetimeLocals();
    }
  }
  
  private void promoteInSets()
  {
    Iterator localIterator = this.predecessors.iterator();
    while (localIterator.hasNext())
    {
      FlowBlock localFlowBlock = (FlowBlock)localIterator.next();
      SuccessorInfo localSuccessorInfo = (SuccessorInfo)localFlowBlock.successors.get(this);
      if (localSuccessorInfo != null)
      {
        VariableSet localVariableSet = localSuccessorInfo.gen;
        SlotSet localSlotSet1 = localSuccessorInfo.kill;
        this.in.merge(localVariableSet);
        SlotSet localSlotSet2 = (SlotSet)this.in.clone();
        localSlotSet2.removeAll(localSlotSet1);
        if (localFlowBlock.in.addAll(localSlotSet2)) {
          localFlowBlock.promoteInSets();
        }
      }
    }
    if (this.nextByAddr != null) {
      this.nextByAddr.promoteInSets();
    }
  }
  
  public void mergeParams(LocalInfo[] paramArrayOfLocalInfo)
  {
    promoteInSets();
    VariableSet localVariableSet = new VariableSet(paramArrayOfLocalInfo);
    this.in.merge(localVariableSet);
  }
  
  public void makeDeclaration(Set paramSet)
  {
    this.block.propagateUsage();
    this.block.makeDeclaration(paramSet);
    if (this.nextByAddr != null) {
      this.nextByAddr.makeDeclaration(paramSet);
    }
  }
  
  public void simplify()
  {
    this.block.simplify();
    if (this.nextByAddr != null) {
      this.nextByAddr.simplify();
    }
  }
  
  public void dumpSource(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    if (this.predecessors.size() != 0)
    {
      paramTabbedPrintWriter.untab();
      paramTabbedPrintWriter.println(getLabel() + ":");
      paramTabbedPrintWriter.tab();
    }
    if ((GlobalOptions.debuggingFlags & 0x10) != 0) {
      paramTabbedPrintWriter.println("in: " + this.in);
    }
    this.block.dumpSource(paramTabbedPrintWriter);
    if ((GlobalOptions.debuggingFlags & 0x10) != 0)
    {
      Iterator localIterator = this.successors.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        FlowBlock localFlowBlock = (FlowBlock)localEntry.getKey();
        SuccessorInfo localSuccessorInfo = (SuccessorInfo)localEntry.getValue();
        paramTabbedPrintWriter.println("successor: " + localFlowBlock.getLabel() + "  gen : " + localSuccessorInfo.gen + "  kill: " + localSuccessorInfo.kill);
      }
    }
    if (this.nextByAddr != null) {
      this.nextByAddr.dumpSource(paramTabbedPrintWriter);
    }
  }
  
  public int getAddr()
  {
    return this.addr;
  }
  
  public String getLabel()
  {
    if (this.label == null) {
      this.label = ("flow_" + this.addr + "_" + serialno++ + "_");
    }
    return this.label;
  }
  
  public StructuredBlock getBlock()
  {
    return this.block;
  }
  
  public String toString()
  {
    try
    {
      StringWriter localStringWriter = new StringWriter();
      TabbedPrintWriter localTabbedPrintWriter = new TabbedPrintWriter(localStringWriter);
      localTabbedPrintWriter.println(super.toString() + ": " + this.addr + "-" + (this.addr + this.length));
      if ((GlobalOptions.debuggingFlags & 0x10) != 0) {
        localTabbedPrintWriter.println("in: " + this.in);
      }
      localTabbedPrintWriter.tab();
      this.block.dumpSource(localTabbedPrintWriter);
      localTabbedPrintWriter.untab();
      if ((GlobalOptions.debuggingFlags & 0x10) != 0)
      {
        Iterator localIterator = this.successors.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          FlowBlock localFlowBlock = (FlowBlock)localEntry.getKey();
          SuccessorInfo localSuccessorInfo = (SuccessorInfo)localEntry.getValue();
          localTabbedPrintWriter.println("successor: " + localFlowBlock.getLabel() + "  gen : " + localSuccessorInfo.gen + "  kill: " + localSuccessorInfo.kill);
        }
      }
      return localStringWriter.toString();
    }
    catch (RuntimeException localRuntimeException)
    {
      return super.toString() + ": (RUNTIME EXCEPTION)";
    }
    catch (IOException localIOException) {}
    return super.toString();
  }
  
  static
  {
    END_OF_METHOD.appendBlock(new EmptyBlock(), 0);
    END_OF_METHOD.label = "END_OF_METHOD";
    NEXT_BY_ADDR = new FlowBlock(null, -1);
    NEXT_BY_ADDR.appendBlock(new DescriptionBlock("FALL THROUGH"), 0);
    NEXT_BY_ADDR.label = "NEXT_BY_ADDR";
  }
  
  static class SuccessorInfo
  {
    SlotSet kill;
    VariableSet gen;
    Jump jumps;
  }
}


