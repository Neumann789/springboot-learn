package jode.decompiler;

import jode.bytecode.Instruction;
import jode.bytecode.LocalVariableInfo;
import jode.type.Type;

public class LocalVariableTable
{
  LocalVariableRangeList[] locals;
  
  public LocalVariableTable(int paramInt, LocalVariableInfo[] paramArrayOfLocalVariableInfo)
  {
    this.locals = new LocalVariableRangeList[paramInt];
    for (int i = 0; i < paramInt; i++) {
      this.locals[i] = new LocalVariableRangeList();
    }
    for (i = 0; i < paramArrayOfLocalVariableInfo.length; i++) {
      this.locals[paramArrayOfLocalVariableInfo[i].slot].addLocal(paramArrayOfLocalVariableInfo[i].start.getAddr(), paramArrayOfLocalVariableInfo[i].end.getAddr(), paramArrayOfLocalVariableInfo[i].name, Type.tType(paramArrayOfLocalVariableInfo[i].type));
    }
  }
  
  public LocalVarEntry getLocal(int paramInt1, int paramInt2)
    throws ArrayIndexOutOfBoundsException
  {
    return this.locals[paramInt1].getInfo(paramInt2);
  }
}


