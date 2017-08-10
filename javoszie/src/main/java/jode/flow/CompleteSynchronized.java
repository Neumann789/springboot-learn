package jode.flow;

import java.io.PrintWriter;
import jode.GlobalOptions;
import jode.decompiler.LocalInfo;
import jode.expr.Expression;
import jode.expr.LocalLoadOperator;
import jode.expr.LocalStoreOperator;
import jode.expr.MonitorEnterOperator;
import jode.expr.StoreInstruction;

public class CompleteSynchronized
{
  public static boolean enter(SynchronizedBlock paramSynchronizedBlock, StructuredBlock paramStructuredBlock)
  {
    if (!(paramStructuredBlock.outer instanceof SequentialBlock)) {
      return false;
    }
    SequentialBlock localSequentialBlock = (SequentialBlock)paramSynchronizedBlock.outer;
    if (!(localSequentialBlock.subBlocks[0] instanceof InstructionBlock)) {
      return false;
    }
    Expression localExpression1 = ((InstructionBlock)localSequentialBlock.subBlocks[0]).getInstruction();
    if (!(localExpression1 instanceof MonitorEnterOperator)) {
      return false;
    }
    Expression localExpression2 = ((MonitorEnterOperator)localExpression1).getSubExpressions()[0];
    if ((!(localExpression2 instanceof LocalLoadOperator)) || (((LocalLoadOperator)localExpression2).getLocalInfo() != paramSynchronizedBlock.local.getLocalInfo())) {
      return false;
    }
    if (GlobalOptions.verboseLevel > 0) {
      GlobalOptions.err.print('s');
    }
    paramSynchronizedBlock.isEntered = true;
    paramSynchronizedBlock.moveDefinitions(paramStructuredBlock.outer, paramStructuredBlock);
    paramStructuredBlock.replace(paramStructuredBlock.outer);
    return true;
  }
  
  public static boolean combineObject(SynchronizedBlock paramSynchronizedBlock, StructuredBlock paramStructuredBlock)
  {
    if (!(paramStructuredBlock.outer instanceof SequentialBlock)) {
      return false;
    }
    SequentialBlock localSequentialBlock = (SequentialBlock)paramStructuredBlock.outer;
    if (!(localSequentialBlock.subBlocks[0] instanceof InstructionBlock)) {
      return false;
    }
    InstructionBlock localInstructionBlock = (InstructionBlock)localSequentialBlock.subBlocks[0];
    if (!(localInstructionBlock.getInstruction() instanceof StoreInstruction)) {
      return false;
    }
    StoreInstruction localStoreInstruction = (StoreInstruction)localInstructionBlock.getInstruction();
    if (!(localStoreInstruction.getLValue() instanceof LocalStoreOperator)) {
      return false;
    }
    LocalStoreOperator localLocalStoreOperator = (LocalStoreOperator)localStoreInstruction.getLValue();
    if ((localLocalStoreOperator.getLocalInfo() != paramSynchronizedBlock.local.getLocalInfo()) || (localStoreInstruction.getSubExpressions()[1] == null)) {
      return false;
    }
    paramSynchronizedBlock.object = localStoreInstruction.getSubExpressions()[1];
    paramSynchronizedBlock.moveDefinitions(paramStructuredBlock.outer, paramStructuredBlock);
    paramStructuredBlock.replace(paramStructuredBlock.outer);
    return true;
  }
}


