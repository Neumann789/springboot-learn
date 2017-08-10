package jode.flow;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import jode.AssertError;
import jode.GlobalOptions;
import jode.decompiler.LocalInfo;
import jode.expr.Expression;
import jode.expr.LocalLoadOperator;
import jode.expr.LocalStoreOperator;
import jode.expr.MonitorExitOperator;
import jode.expr.NopOperator;
import jode.expr.Operator;
import jode.expr.StoreInstruction;
import jode.type.Type;

public class TransformExceptionHandlers
{
  SortedSet handlers = new TreeSet();
  
  public void addHandler(FlowBlock paramFlowBlock1, int paramInt, FlowBlock paramFlowBlock2, Type paramType)
  {
    this.handlers.add(new Handler(paramFlowBlock1, paramInt, paramFlowBlock2, paramType));
  }
  
  static void mergeTryCatch(FlowBlock paramFlowBlock1, FlowBlock paramFlowBlock2)
  {
    if ((GlobalOptions.debuggingFlags & 0x20) != 0) {
      GlobalOptions.err.println("mergeTryCatch(" + paramFlowBlock1.getAddr() + ", " + paramFlowBlock2.getAddr() + ")");
    }
    paramFlowBlock1.updateInOutCatch(paramFlowBlock2);
    paramFlowBlock1.mergeSuccessors(paramFlowBlock2);
    paramFlowBlock1.mergeAddr(paramFlowBlock2);
  }
  
  static void analyzeCatchBlock(Type paramType, FlowBlock paramFlowBlock1, FlowBlock paramFlowBlock2)
  {
    mergeTryCatch(paramFlowBlock1, paramFlowBlock2);
    CatchBlock localCatchBlock = new CatchBlock(paramType);
    ((TryBlock)paramFlowBlock1.block).addCatchBlock(localCatchBlock);
    localCatchBlock.setCatchBlock(paramFlowBlock2.block);
  }
  
  boolean transformSubRoutine(StructuredBlock paramStructuredBlock)
  {
    StructuredBlock localStructuredBlock = paramStructuredBlock;
    if ((localStructuredBlock instanceof SequentialBlock)) {
      localStructuredBlock = paramStructuredBlock.getSubBlocks()[0];
    }
    LocalInfo localLocalInfo = null;
    Object localObject;
    if ((localStructuredBlock instanceof SpecialBlock))
    {
      localObject = (SpecialBlock)localStructuredBlock;
      if ((((SpecialBlock)localObject).type != SpecialBlock.POP) || (((SpecialBlock)localObject).count != 1)) {
        return false;
      }
    }
    else if ((localStructuredBlock instanceof InstructionBlock))
    {
      localObject = ((InstructionBlock)localStructuredBlock).getInstruction();
      if (((localObject instanceof StoreInstruction)) && ((((StoreInstruction)localObject).getLValue() instanceof LocalStoreOperator)))
      {
        LocalStoreOperator localLocalStoreOperator = (LocalStoreOperator)((StoreInstruction)localObject).getLValue();
        localLocalInfo = localLocalStoreOperator.getLocalInfo();
        localObject = ((StoreInstruction)localObject).getSubExpressions()[1];
      }
      if (!(localObject instanceof NopOperator)) {
        return false;
      }
    }
    else
    {
      return false;
    }
    localStructuredBlock.removeBlock();
    while ((paramStructuredBlock instanceof SequentialBlock)) {
      paramStructuredBlock = paramStructuredBlock.getSubBlocks()[1];
    }
    if (((paramStructuredBlock instanceof RetBlock)) && (((RetBlock)paramStructuredBlock).local.equals(localLocalInfo))) {
      paramStructuredBlock.removeBlock();
    }
    return true;
  }
  
  private void removeReturnLocal(ReturnBlock paramReturnBlock)
  {
    StructuredBlock localStructuredBlock = getPredecessor(paramReturnBlock);
    if (!(localStructuredBlock instanceof InstructionBlock)) {
      return;
    }
    Expression localExpression1 = ((InstructionBlock)localStructuredBlock).getInstruction();
    if (!(localExpression1 instanceof StoreInstruction)) {
      return;
    }
    Expression localExpression2 = paramReturnBlock.getInstruction();
    if ((!(localExpression2 instanceof LocalLoadOperator)) || (!((StoreInstruction)localExpression1).lvalueMatches((LocalLoadOperator)localExpression2))) {
      return;
    }
    Expression localExpression3 = ((StoreInstruction)localExpression1).getSubExpressions()[1];
    paramReturnBlock.setInstruction(localExpression3);
    paramReturnBlock.replace(paramReturnBlock.outer);
  }
  
  private void removeJSR(FlowBlock paramFlowBlock1, FlowBlock paramFlowBlock2)
  {
    for (Jump localJump = paramFlowBlock1.removeJumps(paramFlowBlock2); localJump != null; localJump = localJump.next)
    {
      StructuredBlock localStructuredBlock = localJump.prev;
      localStructuredBlock.removeJump();
      Object localObject;
      if (((localStructuredBlock instanceof EmptyBlock)) && ((localStructuredBlock.outer instanceof JsrBlock)) && (((JsrBlock)localStructuredBlock.outer).isGood()))
      {
        localObject = localStructuredBlock.outer.getNextBlock();
        localStructuredBlock.outer.removeBlock();
        if ((localObject instanceof ReturnBlock)) {
          removeReturnLocal((ReturnBlock)localObject);
        }
      }
      else
      {
        localObject = new DescriptionBlock("ERROR: invalid jump to finally block!");
        localStructuredBlock.appendBlock((StructuredBlock)localObject);
      }
    }
  }
  
  private static StructuredBlock getPredecessor(StructuredBlock paramStructuredBlock)
  {
    if ((paramStructuredBlock.outer instanceof SequentialBlock))
    {
      SequentialBlock localSequentialBlock = (SequentialBlock)paramStructuredBlock.outer;
      if (localSequentialBlock.subBlocks[1] == paramStructuredBlock) {
        return localSequentialBlock.subBlocks[0];
      }
      if ((localSequentialBlock.outer instanceof SequentialBlock)) {
        return localSequentialBlock.outer.getSubBlocks()[0];
      }
    }
    return null;
  }
  
  private static int getMonitorExitSlot(StructuredBlock paramStructuredBlock)
  {
    if ((paramStructuredBlock instanceof InstructionBlock))
    {
      Expression localExpression = ((InstructionBlock)paramStructuredBlock).getInstruction();
      if ((localExpression instanceof MonitorExitOperator))
      {
        MonitorExitOperator localMonitorExitOperator = (MonitorExitOperator)localExpression;
        if ((localMonitorExitOperator.getFreeOperandCount() == 0) && ((localMonitorExitOperator.getSubExpressions()[0] instanceof LocalLoadOperator))) {
          return ((LocalLoadOperator)localMonitorExitOperator.getSubExpressions()[0]).getLocalInfo().getSlot();
        }
      }
    }
    return -1;
  }
  
  private boolean isMonitorExitSubRoutine(FlowBlock paramFlowBlock, LocalInfo paramLocalInfo)
  {
    return (transformSubRoutine(paramFlowBlock.block)) && (getMonitorExitSlot(paramFlowBlock.block) == paramLocalInfo.getSlot());
  }
  
  private static StructuredBlock skipFinExitChain(StructuredBlock paramStructuredBlock)
  {
    StructuredBlock localStructuredBlock1;
    if ((paramStructuredBlock instanceof ReturnBlock)) {
      localStructuredBlock1 = getPredecessor(paramStructuredBlock);
    } else {
      localStructuredBlock1 = paramStructuredBlock;
    }
    StructuredBlock localStructuredBlock2 = null;
    while (((localStructuredBlock1 instanceof JsrBlock)) || (getMonitorExitSlot(localStructuredBlock1) >= 0))
    {
      localStructuredBlock2 = localStructuredBlock1;
      localStructuredBlock1 = getPredecessor(localStructuredBlock1);
    }
    return localStructuredBlock2;
  }
  
  private void checkAndRemoveJSR(FlowBlock paramFlowBlock1, FlowBlock paramFlowBlock2, int paramInt1, int paramInt2)
  {
    Iterator localIterator = paramFlowBlock1.getSuccessors().iterator();
    label373:
    label386:
    while (localIterator.hasNext())
    {
      FlowBlock localFlowBlock = (FlowBlock)localIterator.next();
      if (localFlowBlock != paramFlowBlock2)
      {
        int i = 1;
        Jump localJump = paramFlowBlock1.getJumps(localFlowBlock);
        for (;;)
        {
          if (localJump == null) {
            break label386;
          }
          StructuredBlock localStructuredBlock1 = localJump.prev;
          if ((!(localStructuredBlock1 instanceof ThrowBlock)) && ((!(localStructuredBlock1 instanceof EmptyBlock)) || (!(localStructuredBlock1.outer instanceof JsrBlock))))
          {
            StructuredBlock localStructuredBlock2 = skipFinExitChain(localStructuredBlock1);
            StructuredBlock localStructuredBlock3;
            if ((localStructuredBlock2 instanceof JsrBlock))
            {
              localObject = (JsrBlock)localStructuredBlock2;
              localStructuredBlock3 = ((JsrBlock)localObject).innerBlock;
              if (((localStructuredBlock3 instanceof EmptyBlock)) && (localStructuredBlock3.jump != null) && (localStructuredBlock3.jump.destination == paramFlowBlock2))
              {
                ((JsrBlock)localObject).setGood(true);
                break label373;
              }
            }
            if ((localStructuredBlock2 == null) && (i != 0) && (localJump.destination.predecessors.size() == 1) && (localJump.destination.getAddr() >= paramInt1) && (localJump.destination.getNextAddr() <= paramInt2))
            {
              localJump.destination.analyze(paramInt1, paramInt2);
              localObject = localJump.destination.block;
              if ((localObject instanceof SequentialBlock)) {
                localObject = localObject.getSubBlocks()[0];
              }
              if (((localObject instanceof JsrBlock)) && ((localObject.getSubBlocks()[0] instanceof EmptyBlock)) && (localObject.getSubBlocks()[0].jump.destination == paramFlowBlock2))
              {
                localStructuredBlock3 = localObject.getSubBlocks()[0];
                localJump.destination.removeSuccessor(localStructuredBlock3.jump);
                localStructuredBlock3.removeJump();
                ((StructuredBlock)localObject).removeBlock();
                break;
              }
            }
            Object localObject = new DescriptionBlock("ERROR: no jsr to finally");
            if (localStructuredBlock2 != null)
            {
              localStructuredBlock2.prependBlock((StructuredBlock)localObject);
            }
            else
            {
              localStructuredBlock1.appendBlock((StructuredBlock)localObject);
              ((DescriptionBlock)localObject).moveJump(localStructuredBlock1.jump);
            }
          }
          localJump = localJump.next;
          i = 0;
        }
      }
    }
    if (paramFlowBlock1.getSuccessors().contains(paramFlowBlock2)) {
      removeJSR(paramFlowBlock1, paramFlowBlock2);
    }
  }
  
  private void checkAndRemoveMonitorExit(FlowBlock paramFlowBlock, LocalInfo paramLocalInfo, int paramInt1, int paramInt2)
  {
    Object localObject1 = null;
    Iterator localIterator = paramFlowBlock.getSuccessors().iterator();
    label489:
    label502:
    while (localIterator.hasNext())
    {
      int i = 1;
      FlowBlock localFlowBlock1 = (FlowBlock)localIterator.next();
      Jump localJump = paramFlowBlock.getJumps(localFlowBlock1);
      for (;;)
      {
        if (localJump == null) {
          break label502;
        }
        StructuredBlock localStructuredBlock1 = localJump.prev;
        if ((!(localStructuredBlock1 instanceof ThrowBlock)) && ((!(localStructuredBlock1 instanceof EmptyBlock)) || (!(localStructuredBlock1.outer instanceof JsrBlock))))
        {
          StructuredBlock localStructuredBlock2 = skipFinExitChain(localStructuredBlock1);
          StructuredBlock localStructuredBlock3;
          FlowBlock localFlowBlock2;
          if ((localStructuredBlock2 instanceof JsrBlock))
          {
            localObject2 = (JsrBlock)localStructuredBlock2;
            localStructuredBlock3 = ((JsrBlock)localObject2).innerBlock;
            if (((localStructuredBlock3 instanceof EmptyBlock)) && (localStructuredBlock3.jump != null))
            {
              localFlowBlock2 = localStructuredBlock3.jump.destination;
              if ((localObject1 == null) && (localFlowBlock2.getAddr() >= paramInt1) && (localFlowBlock2.getNextAddr() <= paramInt2))
              {
                localFlowBlock2.analyze(paramInt1, paramInt2);
                if (isMonitorExitSubRoutine(localFlowBlock2, paramLocalInfo)) {
                  localObject1 = localFlowBlock2;
                }
              }
              if (localFlowBlock2 == localObject1)
              {
                ((JsrBlock)localObject2).setGood(true);
                break label489;
              }
            }
          }
          else if (getMonitorExitSlot(localStructuredBlock2) == paramLocalInfo.getSlot())
          {
            localStructuredBlock2.removeBlock();
            if (!(localStructuredBlock1 instanceof ReturnBlock)) {
              break label489;
            }
            removeReturnLocal((ReturnBlock)localStructuredBlock1);
            break label489;
          }
          if ((localStructuredBlock2 == null) && (i != 0) && (localFlowBlock1.predecessors.size() == 1) && (localFlowBlock1.getAddr() >= paramInt1) && (localFlowBlock1.getNextAddr() <= paramInt2))
          {
            localFlowBlock1.analyze(paramInt1, paramInt2);
            localObject2 = localFlowBlock1.block;
            if ((localObject2 instanceof SequentialBlock)) {
              localObject2 = localObject2.getSubBlocks()[0];
            }
            if (((localObject2 instanceof JsrBlock)) && ((localObject2.getSubBlocks()[0] instanceof EmptyBlock)))
            {
              localStructuredBlock3 = localObject2.getSubBlocks()[0];
              localFlowBlock2 = localStructuredBlock3.jump.destination;
              if ((localObject1 == null) && (localFlowBlock2.getAddr() >= paramInt1) && (localFlowBlock2.getNextAddr() <= paramInt2))
              {
                localFlowBlock2.analyze(paramInt1, paramInt2);
                if (isMonitorExitSubRoutine(localFlowBlock2, paramLocalInfo)) {
                  localObject1 = localFlowBlock2;
                }
              }
              if (localObject1 == localFlowBlock2)
              {
                localFlowBlock1.removeSuccessor(localStructuredBlock3.jump);
                localStructuredBlock3.removeJump();
                ((StructuredBlock)localObject2).removeBlock();
                break;
              }
            }
            if (getMonitorExitSlot((StructuredBlock)localObject2) == paramLocalInfo.getSlot())
            {
              ((StructuredBlock)localObject2).removeBlock();
              break;
            }
          }
          Object localObject2 = new DescriptionBlock("ERROR: no monitorexit");
          localStructuredBlock1.appendBlock((StructuredBlock)localObject2);
          ((DescriptionBlock)localObject2).moveJump(localJump);
        }
        localJump = localJump.next;
        i = 0;
      }
    }
    if (localObject1 != null)
    {
      if (paramFlowBlock.getSuccessors().contains(localObject1)) {
        removeJSR(paramFlowBlock, (FlowBlock)localObject1);
      }
      if (((FlowBlock)localObject1).predecessors.size() == 0) {
        paramFlowBlock.mergeAddr((FlowBlock)localObject1);
      }
    }
  }
  
  private StoreInstruction getExceptionStore(StructuredBlock paramStructuredBlock)
  {
    if ((!(paramStructuredBlock instanceof SequentialBlock)) || (!(paramStructuredBlock.getSubBlocks()[0] instanceof InstructionBlock))) {
      return null;
    }
    Expression localExpression = ((InstructionBlock)paramStructuredBlock.getSubBlocks()[0]).getInstruction();
    if (!(localExpression instanceof StoreInstruction)) {
      return null;
    }
    StoreInstruction localStoreInstruction = (StoreInstruction)localExpression;
    if ((!(localStoreInstruction.getLValue() instanceof LocalStoreOperator)) || (!(localStoreInstruction.getSubExpressions()[1] instanceof NopOperator))) {
      return null;
    }
    return localStoreInstruction;
  }
  
  private boolean analyzeSynchronized(FlowBlock paramFlowBlock1, FlowBlock paramFlowBlock2, int paramInt)
  {
    StructuredBlock localStructuredBlock = paramFlowBlock2.block;
    StoreInstruction localStoreInstruction = getExceptionStore(localStructuredBlock);
    if (localStoreInstruction != null) {
      localStructuredBlock = localStructuredBlock.getSubBlocks()[1];
    }
    if ((!(localStructuredBlock instanceof SequentialBlock)) || (!(localStructuredBlock.getSubBlocks()[0] instanceof InstructionBlock))) {
      return false;
    }
    Expression localExpression1 = ((InstructionBlock)localStructuredBlock.getSubBlocks()[0]).getInstruction();
    if ((!(localExpression1 instanceof MonitorExitOperator)) || (localExpression1.getFreeOperandCount() != 0) || (!(((MonitorExitOperator)localExpression1).getSubExpressions()[0] instanceof LocalLoadOperator)) || (!(localStructuredBlock.getSubBlocks()[1] instanceof ThrowBlock))) {
      return false;
    }
    Expression localExpression2 = ((ThrowBlock)localStructuredBlock.getSubBlocks()[1]).getInstruction();
    if (localStoreInstruction != null)
    {
      if ((!(localExpression2 instanceof Operator)) || (!localStoreInstruction.lvalueMatches((Operator)localExpression2))) {
        return false;
      }
    }
    else if (!(localExpression2 instanceof NopOperator)) {
      return false;
    }
    paramFlowBlock2.removeSuccessor(localStructuredBlock.getSubBlocks()[1].jump);
    mergeTryCatch(paramFlowBlock1, paramFlowBlock2);
    MonitorExitOperator localMonitorExitOperator = (MonitorExitOperator)((InstructionBlock)localStructuredBlock.getSubBlocks()[0]).instr;
    LocalInfo localLocalInfo = ((LocalLoadOperator)localMonitorExitOperator.getSubExpressions()[0]).getLocalInfo();
    if ((GlobalOptions.debuggingFlags & 0x20) != 0) {
      GlobalOptions.err.println("analyzeSynchronized(" + paramFlowBlock1.getAddr() + "," + paramFlowBlock1.getNextAddr() + "," + paramInt + ")");
    }
    checkAndRemoveMonitorExit(paramFlowBlock1, localLocalInfo, paramFlowBlock1.getNextAddr(), paramInt);
    SynchronizedBlock localSynchronizedBlock = new SynchronizedBlock(localLocalInfo);
    TryBlock localTryBlock = (TryBlock)paramFlowBlock1.block;
    localSynchronizedBlock.replace(localTryBlock);
    localSynchronizedBlock.moveJump(localTryBlock.jump);
    localSynchronizedBlock.setBodyBlock(localTryBlock.subBlocks.length == 1 ? localTryBlock.subBlocks[0] : localTryBlock);
    paramFlowBlock1.lastModified = localSynchronizedBlock;
    return true;
  }
  
  private void mergeFinallyBlock(FlowBlock paramFlowBlock1, FlowBlock paramFlowBlock2, StructuredBlock paramStructuredBlock)
  {
    Object localObject1 = (TryBlock)paramFlowBlock1.block;
    if ((localObject1.getSubBlocks()[0] instanceof TryBlock))
    {
      localObject2 = (TryBlock)localObject1.getSubBlocks()[0];
      ((TryBlock)localObject2).gen = ((TryBlock)localObject1).gen;
      ((TryBlock)localObject2).replace((StructuredBlock)localObject1);
      localObject1 = localObject2;
      paramFlowBlock1.lastModified = ((StructuredBlock)localObject1);
      paramFlowBlock1.block = ((StructuredBlock)localObject1);
    }
    mergeTryCatch(paramFlowBlock1, paramFlowBlock2);
    Object localObject2 = new FinallyBlock();
    ((FinallyBlock)localObject2).setCatchBlock(paramStructuredBlock);
    ((TryBlock)localObject1).addCatchBlock((StructuredBlock)localObject2);
  }
  
  private boolean analyzeFinally(FlowBlock paramFlowBlock1, FlowBlock paramFlowBlock2, int paramInt)
  {
    Object localObject1 = paramFlowBlock2.block;
    StoreInstruction localStoreInstruction = getExceptionStore((StructuredBlock)localObject1);
    if (localStoreInstruction == null) {
      return false;
    }
    localObject1 = localObject1.getSubBlocks()[1];
    if (!(localObject1 instanceof SequentialBlock)) {
      return false;
    }
    StructuredBlock localStructuredBlock1 = null;
    if ((localObject1.getSubBlocks()[0] instanceof LoopBlock))
    {
      localObject2 = (LoopBlock)localObject1.getSubBlocks()[0];
      if ((((LoopBlock)localObject2).type == 1) && (((LoopBlock)localObject2).cond == LoopBlock.FALSE) && ((((LoopBlock)localObject2).bodyBlock instanceof SequentialBlock)) && (transformSubRoutine(localObject1.getSubBlocks()[1])))
      {
        localStructuredBlock1 = localObject1.getSubBlocks()[1];
        localObject1 = (SequentialBlock)((LoopBlock)localObject2).bodyBlock;
      }
    }
    if ((!(localObject1 instanceof SequentialBlock)) || (!(localObject1.getSubBlocks()[0] instanceof JsrBlock)) || (!(localObject1.getSubBlocks()[1] instanceof ThrowBlock))) {
      return false;
    }
    Object localObject2 = (JsrBlock)localObject1.getSubBlocks()[0];
    ThrowBlock localThrowBlock = (ThrowBlock)localObject1.getSubBlocks()[1];
    if ((!(localThrowBlock.getInstruction() instanceof Operator)) || (!localStoreInstruction.lvalueMatches((Operator)localThrowBlock.getInstruction()))) {
      return false;
    }
    FlowBlock localFlowBlock;
    if (localStructuredBlock1 != null)
    {
      if (!(((JsrBlock)localObject2).innerBlock instanceof BreakBlock)) {
        return false;
      }
      localObject1 = localStructuredBlock1;
      localFlowBlock = null;
      paramFlowBlock2.removeSuccessor(localThrowBlock.jump);
    }
    else
    {
      if (!(((JsrBlock)localObject2).innerBlock instanceof EmptyBlock)) {
        return false;
      }
      localStructuredBlock1 = ((JsrBlock)localObject2).innerBlock;
      localFlowBlock = localStructuredBlock1.jump.destination;
      paramFlowBlock2.removeSuccessor(localThrowBlock.jump);
      checkAndRemoveJSR(paramFlowBlock1, localFlowBlock, paramFlowBlock1.getNextAddr(), paramInt);
      while (localFlowBlock.analyze(paramFlowBlock1.getNextAddr(), paramInt)) {}
      if (localFlowBlock.predecessors.size() == 1)
      {
        paramFlowBlock2.removeSuccessor(localStructuredBlock1.jump);
        localFlowBlock.mergeAddr(paramFlowBlock2);
        paramFlowBlock2 = localFlowBlock;
        if (!transformSubRoutine(localFlowBlock.block))
        {
          localStructuredBlock1 = localFlowBlock.block;
          DescriptionBlock localDescriptionBlock = new DescriptionBlock("ERROR: Missing return address handling");
          StructuredBlock localStructuredBlock2 = localFlowBlock.block;
          localDescriptionBlock.replace(localStructuredBlock1);
          localDescriptionBlock.appendBlock(localStructuredBlock1);
        }
        localStructuredBlock1 = localFlowBlock.block;
      }
    }
    mergeFinallyBlock(paramFlowBlock1, paramFlowBlock2, localStructuredBlock1);
    return true;
  }
  
  private boolean analyzeSpecialFinally(FlowBlock paramFlowBlock1, FlowBlock paramFlowBlock2, int paramInt)
  {
    Object localObject1 = paramFlowBlock2.block;
    Object localObject2 = (localObject1 instanceof SequentialBlock) ? localObject1.getSubBlocks()[0] : localObject1;
    if ((!(localObject2 instanceof SpecialBlock)) || (((SpecialBlock)localObject2).type != SpecialBlock.POP) || (((SpecialBlock)localObject2).count != 1)) {
      return false;
    }
    paramFlowBlock1.lastModified = paramFlowBlock1.block.getSubBlocks()[0];
    FlowBlock localFlowBlock1;
    if ((localObject1 instanceof SequentialBlock))
    {
      localObject1 = localObject1.getSubBlocks()[1];
      localFlowBlock1 = null;
    }
    else
    {
      localObject1 = new EmptyBlock();
      ((StructuredBlock)localObject1).moveJump(((StructuredBlock)localObject2).jump);
      localFlowBlock1 = ((StructuredBlock)localObject1).jump.destination;
      if (paramFlowBlock1.getSuccessors().contains(localFlowBlock1))
      {
        localObject3 = paramFlowBlock1.removeJumps(localFlowBlock1);
        localObject3 = paramFlowBlock1.resolveSomeJumps((Jump)localObject3, localFlowBlock1);
        paramFlowBlock1.resolveRemaining((Jump)localObject3);
      }
    }
    Object localObject3 = paramFlowBlock1.getSuccessors();
    Iterator localIterator = ((Set)localObject3).iterator();
    while (localIterator.hasNext())
    {
      FlowBlock localFlowBlock2 = (FlowBlock)localIterator.next();
      Jump localJump;
      if ((localFlowBlock2 != FlowBlock.END_OF_METHOD) && ((localFlowBlock2.block instanceof EmptyBlock)) && (localFlowBlock2.block.jump.destination == localFlowBlock1))
      {
        localJump = paramFlowBlock1.removeJumps(localFlowBlock2);
        localJump = paramFlowBlock1.resolveSomeJumps(localJump, localFlowBlock1);
        paramFlowBlock1.resolveRemaining(localJump);
        if (localFlowBlock2.predecessors.size() == 0)
        {
          localFlowBlock2.removeJumps(localFlowBlock1);
          paramFlowBlock1.mergeAddr(localFlowBlock2);
        }
      }
      else
      {
        for (localJump = paramFlowBlock1.getJumps(localFlowBlock2); localJump != null; localJump = localJump.next) {
          if (!(localJump.prev instanceof ThrowBlock))
          {
            DescriptionBlock localDescriptionBlock = new DescriptionBlock("ERROR: doesn't go through finally block!");
            if ((localJump.prev instanceof ReturnBlock))
            {
              localDescriptionBlock.replace(localJump.prev);
              localDescriptionBlock.appendBlock(localJump.prev);
            }
            else
            {
              localJump.prev.appendBlock(localDescriptionBlock);
              localDescriptionBlock.moveJump(localJump);
            }
          }
        }
      }
    }
    mergeFinallyBlock(paramFlowBlock1, paramFlowBlock2, (StructuredBlock)localObject1);
    paramFlowBlock1.lastModified = ((StructuredBlock)localObject1);
    return true;
  }
  
  void checkTryCatchOrder()
  {
    Object localObject = null;
    Iterator localIterator = this.handlers.iterator();
    while (localIterator.hasNext())
    {
      Handler localHandler = (Handler)localIterator.next();
      int i = localHandler.start.getAddr();
      int j = localHandler.endAddr;
      int k = localHandler.handler.getAddr();
      if ((i >= j) || (k < j)) {
        throw new AssertError("ExceptionHandler order failed: not " + i + " < " + j + " <= " + k);
      }
      if ((localObject != null) && ((((Handler)localObject).start.getAddr() != i) || (((Handler)localObject).endAddr != j)) && (j > ((Handler)localObject).start.getAddr()) && (j < ((Handler)localObject).endAddr)) {
        throw new AssertError("Exception handlers ranges are intersecting: [" + ((Handler)localObject).start.getAddr() + ", " + ((Handler)localObject).endAddr + "] and [" + i + ", " + j + "].");
      }
      localObject = localHandler;
    }
  }
  
  public void analyze()
  {
    checkTryCatchOrder();
    Iterator localIterator = this.handlers.iterator();
    Object localObject1 = null;
    Object localObject2 = localIterator.hasNext() ? (Handler)localIterator.next() : null;
    while (localObject2 != null)
    {
      Object localObject3 = localObject1;
      localObject1 = localObject2;
      localObject2 = localIterator.hasNext() ? (Handler)localIterator.next() : null;
      int i = Integer.MAX_VALUE;
      if ((localObject2 != null) && (((Handler)localObject2).endAddr > ((Handler)localObject1).endAddr)) {
        i = ((Handler)localObject2).endAddr;
      }
      FlowBlock localFlowBlock = ((Handler)localObject1).start;
      localFlowBlock.checkConsistent();
      if ((localObject3 == null) || (((Handler)localObject1).type == null) || (((Handler)localObject3).start.getAddr() != ((Handler)localObject1).start.getAddr()) || (((Handler)localObject3).endAddr != ((Handler)localObject1).endAddr))
      {
        if ((GlobalOptions.debuggingFlags & 0x20) != 0) {
          GlobalOptions.err.println("analyzeTry(" + ((Handler)localObject1).start.getAddr() + ", " + ((Handler)localObject1).endAddr + ")");
        }
        while (localFlowBlock.analyze(localFlowBlock.getAddr(), ((Handler)localObject1).endAddr)) {}
        localObject4 = new TryBlock(localFlowBlock);
      }
      else if (!(localFlowBlock.block instanceof TryBlock))
      {
        throw new AssertError("no TryBlock");
      }
      Object localObject4 = ((Handler)localObject1).handler;
      int j = ((FlowBlock)localObject4).predecessors.size() != 0 ? 1 : 0;
      Object localObject5;
      Object localObject6;
      if ((j == 0) && (localObject2 != null))
      {
        localObject5 = this.handlers.tailSet(localObject2).iterator();
        while (((Iterator)localObject5).hasNext())
        {
          localObject6 = (Handler)((Iterator)localObject5).next();
          if (((Handler)localObject6).handler == localObject4)
          {
            j = 1;
            break;
          }
        }
      }
      if (j != 0)
      {
        localObject5 = new EmptyBlock(new Jump((FlowBlock)localObject4));
        localObject6 = new FlowBlock(((FlowBlock)localObject4).method, ((FlowBlock)localObject4).getAddr());
        ((FlowBlock)localObject6).appendBlock((StructuredBlock)localObject5, 0);
        ((FlowBlock)localObject4).prevByAddr.nextByAddr = ((FlowBlock)localObject6);
        ((FlowBlock)localObject6).prevByAddr = ((FlowBlock)localObject4).prevByAddr;
        ((FlowBlock)localObject6).nextByAddr = ((FlowBlock)localObject4);
        ((FlowBlock)localObject4).prevByAddr = ((FlowBlock)localObject6);
        localObject4 = localObject6;
      }
      else
      {
        if ((GlobalOptions.debuggingFlags & 0x20) != 0) {
          GlobalOptions.err.println("analyzeCatch(" + ((FlowBlock)localObject4).getAddr() + ", " + i + ")");
        }
        while (((FlowBlock)localObject4).analyze(((FlowBlock)localObject4).getAddr(), i)) {}
      }
      if (((Handler)localObject1).type != null) {
        analyzeCatchBlock(((Handler)localObject1).type, localFlowBlock, (FlowBlock)localObject4);
      } else if ((!analyzeSynchronized(localFlowBlock, (FlowBlock)localObject4, i)) && (!analyzeFinally(localFlowBlock, (FlowBlock)localObject4, i)) && (!analyzeSpecialFinally(localFlowBlock, (FlowBlock)localObject4, i))) {
        analyzeCatchBlock(Type.tObject, localFlowBlock, (FlowBlock)localObject4);
      }
      localFlowBlock.checkConsistent();
      if ((GlobalOptions.debuggingFlags & 0x20) != 0) {
        GlobalOptions.err.println("analyzeTryCatch(" + localFlowBlock.getAddr() + ", " + localFlowBlock.getNextAddr() + ") done.");
      }
    }
  }
  
  static class Handler
    implements Comparable
  {
    FlowBlock start;
    int endAddr;
    FlowBlock handler;
    Type type;
    
    public Handler(FlowBlock paramFlowBlock1, int paramInt, FlowBlock paramFlowBlock2, Type paramType)
    {
      this.start = paramFlowBlock1;
      this.endAddr = paramInt;
      this.handler = paramFlowBlock2;
      this.type = paramType;
    }
    
    public int compareTo(Object paramObject)
    {
      Handler localHandler = (Handler)paramObject;
      if (this.start.getAddr() != localHandler.start.getAddr()) {
        return localHandler.start.getAddr() - this.start.getAddr();
      }
      if (this.endAddr != localHandler.endAddr) {
        return this.endAddr - localHandler.endAddr;
      }
      if (this.handler.getAddr() != localHandler.handler.getAddr()) {
        return this.handler.getAddr() - localHandler.handler.getAddr();
      }
      if (this.type == localHandler.type) {
        return 0;
      }
      if (this.type == null) {
        return -1;
      }
      if (localHandler.type == null) {
        return 1;
      }
      return this.type.getTypeSignature().compareTo(localHandler.type.getTypeSignature());
    }
  }
}


