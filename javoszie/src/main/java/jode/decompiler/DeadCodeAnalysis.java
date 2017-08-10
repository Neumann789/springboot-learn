package jode.decompiler;

import java.util.Iterator;
import java.util.List;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;

public class DeadCodeAnalysis
{
  private static final String REACHABLE = "R";
  private static final String REACHCHANGED = "C";
  
  private static void propagateReachability(BytecodeInfo paramBytecodeInfo)
  {
    int i;
    do
    {
      i = 0;
      Iterator localIterator = paramBytecodeInfo.getInstructions().iterator();
      while (localIterator.hasNext())
      {
        Instruction localInstruction = (Instruction)localIterator.next();
        if (localInstruction.getTmpInfo() == "C")
        {
          i = 1;
          localInstruction.setTmpInfo("R");
          Instruction[] arrayOfInstruction = localInstruction.getSuccs();
          if (arrayOfInstruction != null) {
            for (int j = 0; j < arrayOfInstruction.length; j++) {
              if (arrayOfInstruction[j].getTmpInfo() == null) {
                arrayOfInstruction[j].setTmpInfo("C");
              }
            }
          }
          if ((!localInstruction.doesAlwaysJump()) && (localInstruction.getNextByAddr() != null) && (localInstruction.getNextByAddr().getTmpInfo() == null)) {
            localInstruction.getNextByAddr().setTmpInfo("C");
          }
          if ((localInstruction.getOpcode() == 168) && (localInstruction.getNextByAddr().getTmpInfo() == null)) {
            localInstruction.getNextByAddr().setTmpInfo("C");
          }
        }
      }
    } while (i != 0);
  }
  
  public static void removeDeadCode(BytecodeInfo paramBytecodeInfo)
  {
    ((Instruction)paramBytecodeInfo.getInstructions().get(0)).setTmpInfo("C");
    propagateReachability(paramBytecodeInfo);
    Object localObject1 = paramBytecodeInfo.getExceptionHandlers();
    int i;
    Object localObject2;
    do
    {
      i = 0;
      for (j = 0; j < localObject1.length; j++) {
        if (localObject1[j].catcher.getTmpInfo() == null) {
          for (localObject2 = localObject1[j].start; localObject2 != null; localObject2 = ((Instruction)localObject2).getNextByAddr()) {
            if (((Instruction)localObject2).getTmpInfo() != null)
            {
              localObject1[j].catcher.setTmpInfo("C");
              propagateReachability(paramBytecodeInfo);
              i = 1;
            }
            else
            {
              if (localObject2 == localObject1[j].end) {
                break;
              }
            }
          }
        }
      }
    } while (i != 0);
    for (int j = 0; j < localObject1.length; j++) {
      if (localObject1[j].catcher.getTmpInfo() == null)
      {
        localObject2 = new Handler[localObject1.length - 1];
        System.arraycopy(localObject1, 0, localObject2, 0, j);
        System.arraycopy(localObject1, j + 1, localObject2, j, localObject1.length - (j + 1));
        localObject1 = localObject2;
        paramBytecodeInfo.setExceptionHandlers((Handler[])localObject2);
        j--;
      }
      else
      {
        while (localObject1[j].start.getTmpInfo() == null) {
          localObject1[j].start = localObject1[j].start.getNextByAddr();
        }
        while (localObject1[j].end.getTmpInfo() == null) {
          localObject1[j].end = localObject1[j].end.getPrevByAddr();
        }
      }
    }
    Iterator localIterator = paramBytecodeInfo.getInstructions().iterator();
    while (localIterator.hasNext())
    {
      localObject2 = (Instruction)localIterator.next();
      if (((Instruction)localObject2).getTmpInfo() != null) {
        ((Instruction)localObject2).setTmpInfo(null);
      } else {
        localIterator.remove();
      }
    }
  }
}


