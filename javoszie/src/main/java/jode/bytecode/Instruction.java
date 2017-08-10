package jode.bytecode;

import jode.AssertError;

public final class Instruction
  implements Opcodes
{
  private int opcode;
  private int shortData;
  private int addr;
  private Object objData;
  private Object succs;
  private Instruction[] preds;
  Instruction nextByAddr;
  Instruction prevByAddr;
  private Object tmpInfo;
  private static final String stackDelta = "\000\b\b\b\b\b\b\b\b\020\020\b\b\b\020\020\b\b\b\b\020\b\020\b\020\b\b\b\b\b\020\020\020\020\b\b\b\b\020\020\020\020\b\b\b\b\n\022\n\022\n\n\n\n\001\002\001\002\001\001\001\001\001\002\002\002\002\001\001\001\001\002\002\002\002\001\001\001\001\003\004\003\004\003\003\003\003\001\002\021\032#\"+4\022\n\024\n\024\n\024\n\024\n\024\n\024\n\024\n\024\n\024\n\024\t\022\t\022\n\023\n\023\n\023\n\024\n\024\n\024\000\021\t\021\n\n\022\t\021\021\n\022\n\t\t\t\f\n\n\f\f\001\001\001\001\001\001\002\002\002\002\002\002\002\002\000\b\000\001\001\001\002\001\002\001\000@@@@@@@@\b\t\t\t\001\t\t\001\001@\001\001\000\b";
  
  public Instruction(int paramInt)
  {
    this.opcode = paramInt;
  }
  
  public final int getOpcode()
  {
    return this.opcode;
  }
  
  public final int getAddr()
  {
    return this.addr;
  }
  
  public final int getNextAddr()
  {
    return this.nextByAddr.addr;
  }
  
  public final int getLength()
  {
    return getNextAddr() - this.addr;
  }
  
  final void setAddr(int paramInt)
  {
    this.addr = paramInt;
  }
  
  public final boolean hasLocalSlot()
  {
    return (this.opcode == 132) || (this.opcode == 169) || ((this.opcode >= 21) && (this.opcode <= 25)) || ((this.opcode >= 54) && (this.opcode <= 58));
  }
  
  public final int getLocalSlot()
  {
    return this.shortData;
  }
  
  public final void setLocalSlot(int paramInt)
  {
    this.shortData = paramInt;
  }
  
  public final int getIncrement()
  {
    return ((Short)this.objData).shortValue();
  }
  
  public final void setIncrement(int paramInt)
  {
    this.objData = new Short((short)paramInt);
  }
  
  public final int getDimensions()
  {
    return this.shortData;
  }
  
  public final void setDimensions(int paramInt)
  {
    this.shortData = paramInt;
  }
  
  public final Object getConstant()
  {
    return this.objData;
  }
  
  public final void setConstant(Object paramObject)
  {
    this.objData = paramObject;
  }
  
  public final Reference getReference()
  {
    return (Reference)this.objData;
  }
  
  public final void setReference(Reference paramReference)
  {
    this.objData = paramReference;
  }
  
  public final String getClazzType()
  {
    return (String)this.objData;
  }
  
  public final void setClazzType(String paramString)
  {
    this.objData = paramString;
  }
  
  public final int[] getValues()
  {
    return (int[])this.objData;
  }
  
  public final void setValues(int[] paramArrayOfInt)
  {
    this.objData = paramArrayOfInt;
  }
  
  public final boolean doesAlwaysJump()
  {
    switch (this.opcode)
    {
    case 167: 
    case 168: 
    case 169: 
    case 170: 
    case 171: 
    case 172: 
    case 173: 
    case 174: 
    case 175: 
    case 176: 
    case 177: 
    case 191: 
      return true;
    }
    return false;
  }
  
  public final Instruction[] getPreds()
  {
    return this.preds;
  }
  
  public boolean hasSuccs()
  {
    return this.succs != null;
  }
  
  public final Instruction[] getSuccs()
  {
    if ((this.succs instanceof Instruction)) {
      return new Instruction[] { (Instruction)this.succs };
    }
    return (Instruction[])this.succs;
  }
  
  public final Instruction getSingleSucc()
  {
    return (Instruction)this.succs;
  }
  
  public final Instruction getPrevByAddr()
  {
    if (this.prevByAddr.opcode == 254) {
      return null;
    }
    return this.prevByAddr;
  }
  
  public final Instruction getNextByAddr()
  {
    if (this.nextByAddr.opcode == 254) {
      return null;
    }
    return this.nextByAddr;
  }
  
  public final Object getTmpInfo()
  {
    return this.tmpInfo;
  }
  
  public final void setTmpInfo(Object paramObject)
  {
    this.tmpInfo = paramObject;
  }
  
  final void removeSuccs()
  {
    if (this.succs == null) {
      return;
    }
    if ((this.succs instanceof Instruction[]))
    {
      Instruction[] arrayOfInstruction = (Instruction[])this.succs;
      for (int i = 0; i < arrayOfInstruction.length; i++) {
        if (arrayOfInstruction[i] != null) {
          arrayOfInstruction[i].removePredecessor(this);
        }
      }
    }
    else
    {
      ((Instruction)this.succs).removePredecessor(this);
    }
    this.succs = null;
  }
  
  private final void promoteSuccs(Instruction paramInstruction1, Instruction paramInstruction2)
  {
    if (this.succs == paramInstruction1)
    {
      this.succs = paramInstruction2;
    }
    else if ((this.succs instanceof Instruction[]))
    {
      Instruction[] arrayOfInstruction = (Instruction[])this.succs;
      for (int i = 0; i < arrayOfInstruction.length; i++) {
        if (arrayOfInstruction[i] == paramInstruction1) {
          arrayOfInstruction[i] = paramInstruction2;
        }
      }
    }
  }
  
  public final void setSuccs(Object paramObject)
  {
    if (this.succs == paramObject) {
      return;
    }
    removeSuccs();
    if (paramObject == null) {
      return;
    }
    if ((paramObject instanceof Instruction[]))
    {
      Instruction[] arrayOfInstruction = (Instruction[])paramObject;
      switch (arrayOfInstruction.length)
      {
      case 0: 
        break;
      case 1: 
        this.succs = arrayOfInstruction[0];
        arrayOfInstruction[0].addPredecessor(this);
        break;
      default: 
        this.succs = arrayOfInstruction;
        for (int i = 0; i < arrayOfInstruction.length; i++) {
          arrayOfInstruction[i].addPredecessor(this);
        }
      }
    }
    else
    {
      this.succs = paramObject;
      ((Instruction)paramObject).addPredecessor(this);
    }
  }
  
  void addPredecessor(Instruction paramInstruction)
  {
    if (this.preds == null)
    {
      this.preds = new Instruction[] { paramInstruction };
      return;
    }
    int i = this.preds.length;
    Instruction[] arrayOfInstruction = new Instruction[i + 1];
    System.arraycopy(this.preds, 0, arrayOfInstruction, 0, i);
    arrayOfInstruction[i] = paramInstruction;
    this.preds = arrayOfInstruction;
  }
  
  void removePredecessor(Instruction paramInstruction)
  {
    int i = this.preds.length;
    if (i == 1)
    {
      if (this.preds[0] != paramInstruction) {
        throw new AssertError("removing not existing predecessor");
      }
      this.preds = null;
    }
    else
    {
      Instruction[] arrayOfInstruction = new Instruction[i - 1];
      for (int j = 0; this.preds[j] != paramInstruction; j++) {
        arrayOfInstruction[j] = this.preds[j];
      }
      System.arraycopy(this.preds, j + 1, arrayOfInstruction, j, i - j - 1);
      this.preds = arrayOfInstruction;
    }
  }
  
  public final void replaceInstruction(Instruction paramInstruction, BytecodeInfo paramBytecodeInfo)
  {
    removeSuccs();
    paramInstruction.addr = this.addr;
    this.nextByAddr.prevByAddr = paramInstruction;
    paramInstruction.nextByAddr = this.nextByAddr;
    this.prevByAddr.nextByAddr = paramInstruction;
    paramInstruction.prevByAddr = this.prevByAddr;
    this.prevByAddr = null;
    this.nextByAddr = null;
    if (this.preds != null)
    {
      for (int i = 0; i < this.preds.length; i++) {
        this.preds[i].promoteSuccs(this, paramInstruction);
      }
      paramInstruction.preds = this.preds;
      this.preds = null;
    }
    Handler[] arrayOfHandler = paramBytecodeInfo.getExceptionHandlers();
    for (int j = 0; j < arrayOfHandler.length; j++)
    {
      if (arrayOfHandler[j].start == this) {
        arrayOfHandler[j].start = paramInstruction;
      }
      if (arrayOfHandler[j].end == this) {
        arrayOfHandler[j].end = paramInstruction;
      }
      if (arrayOfHandler[j].catcher == this) {
        arrayOfHandler[j].catcher = paramInstruction;
      }
    }
    LocalVariableInfo[] arrayOfLocalVariableInfo = paramBytecodeInfo.getLocalVariableTable();
    if (arrayOfLocalVariableInfo != null) {
      for (int k = 0; k < arrayOfLocalVariableInfo.length; k++)
      {
        if (arrayOfLocalVariableInfo[k].start == this) {
          arrayOfLocalVariableInfo[k].start = paramInstruction;
        }
        if (arrayOfLocalVariableInfo[k].end == this) {
          arrayOfLocalVariableInfo[k].end = paramInstruction;
        }
      }
    }
    LineNumber[] arrayOfLineNumber = paramBytecodeInfo.getLineNumberTable();
    if (arrayOfLineNumber != null) {
      for (int m = 0; m < arrayOfLineNumber.length; m++) {
        if (arrayOfLineNumber[m].start == this) {
          arrayOfLineNumber[m].start = paramInstruction;
        }
      }
    }
  }
  
  void appendInstruction(Instruction paramInstruction, BytecodeInfo paramBytecodeInfo)
  {
    paramInstruction.addr = this.nextByAddr.addr;
    paramInstruction.nextByAddr = this.nextByAddr;
    this.nextByAddr.prevByAddr = paramInstruction;
    paramInstruction.prevByAddr = this;
    this.nextByAddr = paramInstruction;
    Handler[] arrayOfHandler = paramBytecodeInfo.getExceptionHandlers();
    if (arrayOfHandler != null) {
      for (int i = 0; i < arrayOfHandler.length; i++) {
        if (arrayOfHandler[i].end == this) {
          arrayOfHandler[i].end = paramInstruction;
        }
      }
    }
  }
  
  void removeInstruction(BytecodeInfo paramBytecodeInfo)
  {
    this.prevByAddr.nextByAddr = this.nextByAddr;
    this.nextByAddr.prevByAddr = this.prevByAddr;
    removeSuccs();
    if (this.preds != null)
    {
      for (int i = 0; i < this.preds.length; i++) {
        this.preds[i].promoteSuccs(this, this.nextByAddr);
      }
      if (this.nextByAddr.preds == null)
      {
        this.nextByAddr.preds = this.preds;
      }
      else
      {
        localObject1 = new Instruction[this.nextByAddr.preds.length + this.preds.length];
        System.arraycopy(this.nextByAddr.preds, 0, localObject1, 0, this.nextByAddr.preds.length);
        System.arraycopy(this.preds, 0, localObject1, this.nextByAddr.preds.length, this.preds.length);
        this.nextByAddr.preds = ((Instruction[])localObject1);
      }
      this.preds = null;
    }
    Object localObject1 = paramBytecodeInfo.getExceptionHandlers();
    for (int j = 0; j < localObject1.length; j++) {
      if ((localObject1[j].start == this) && (localObject1[j].end == this))
      {
        Handler[] arrayOfHandler = new Handler[localObject1.length - 1];
        System.arraycopy(localObject1, 0, arrayOfHandler, 0, j);
        System.arraycopy(localObject1, j + 1, arrayOfHandler, j, localObject1.length - (j + 1));
        localObject1 = arrayOfHandler;
        paramBytecodeInfo.setExceptionHandlers(arrayOfHandler);
        j--;
      }
      else
      {
        if (localObject1[j].start == this) {
          localObject1[j].start = this.nextByAddr;
        }
        if (localObject1[j].end == this) {
          localObject1[j].end = this.prevByAddr;
        }
        if (localObject1[j].catcher == this) {
          localObject1[j].catcher = this.nextByAddr;
        }
      }
    }
    Object localObject2 = paramBytecodeInfo.getLocalVariableTable();
    if (localObject2 != null) {
      for (int k = 0; k < localObject2.length; k++) {
        if ((localObject2[k].start == this) && (localObject2[k].end == this))
        {
          LocalVariableInfo[] arrayOfLocalVariableInfo = new LocalVariableInfo[localObject2.length - 1];
          System.arraycopy(localObject2, 0, arrayOfLocalVariableInfo, 0, k);
          System.arraycopy(localObject2, k + 1, arrayOfLocalVariableInfo, k, arrayOfLocalVariableInfo.length - k);
          localObject2 = arrayOfLocalVariableInfo;
          paramBytecodeInfo.setLocalVariableTable(arrayOfLocalVariableInfo);
          k--;
        }
        else
        {
          if (localObject2[k].start == this) {
            localObject2[k].start = this.nextByAddr;
          }
          if (localObject2[k].end == this) {
            localObject2[k].end = this.prevByAddr;
          }
        }
      }
    }
    Object localObject3 = paramBytecodeInfo.getLineNumberTable();
    if (localObject3 != null) {
      for (int m = 0; m < localObject3.length; m++) {
        if (localObject3[m].start == this) {
          if ((this.nextByAddr.opcode == 254) || ((m + 1 < localObject3.length) && (localObject3[(m + 1)].start == this.nextByAddr)))
          {
            LineNumber[] arrayOfLineNumber = new LineNumber[localObject3.length - 1];
            System.arraycopy(localObject3, 0, arrayOfLineNumber, 0, m);
            System.arraycopy(localObject3, m + 1, arrayOfLineNumber, m, arrayOfLineNumber.length - m);
            localObject3 = arrayOfLineNumber;
            paramBytecodeInfo.setLineNumberTable(arrayOfLineNumber);
            m--;
          }
          else
          {
            localObject3[m].start = this.nextByAddr;
          }
        }
      }
    }
    this.prevByAddr = null;
    this.nextByAddr = null;
  }
  
  public int compareTo(Instruction paramInstruction)
  {
    if (this.addr != paramInstruction.addr) {
      return this.addr - paramInstruction.addr;
    }
    if (this == paramInstruction) {
      return 0;
    }
    do
    {
      paramInstruction = paramInstruction.nextByAddr;
      if (paramInstruction.addr > this.addr) {
        return -1;
      }
    } while (paramInstruction != this);
    return 1;
  }
  
  public void getStackPopPush(int[] paramArrayOfInt)
  {
    int i = (byte)"\000\b\b\b\b\b\b\b\b\020\020\b\b\b\020\020\b\b\b\b\020\b\020\b\020\b\b\b\b\b\020\020\020\020\b\b\b\b\020\020\020\020\b\b\b\b\n\022\n\022\n\n\n\n\001\002\001\002\001\001\001\001\001\002\002\002\002\001\001\001\001\002\002\002\002\001\001\001\001\003\004\003\004\003\003\003\003\001\002\021\032#\"+4\022\n\024\n\024\n\024\n\024\n\024\n\024\n\024\n\024\n\024\n\024\t\022\t\022\n\023\n\023\n\023\n\024\n\024\n\024\000\021\t\021\n\n\022\t\021\021\n\022\n\t\t\t\f\n\n\f\f\001\001\001\001\001\001\002\002\002\002\002\002\002\002\000\b\000\001\001\001\002\001\002\001\000@@@@@@@@\b\t\t\t\001\t\t\001\001@\001\001\000\b".charAt(this.opcode);
    if (i < 64)
    {
      paramArrayOfInt[0] = (i & 0x7);
      paramArrayOfInt[1] = (i >> 3);
    }
    else
    {
      Reference localReference;
      switch (this.opcode)
      {
      case 182: 
      case 183: 
      case 184: 
      case 185: 
        localReference = getReference();
        String str = localReference.getType();
        paramArrayOfInt[0] = (this.opcode != 184 ? 1 : 0);
        paramArrayOfInt[0] += TypeSignature.getArgumentSize(str);
        paramArrayOfInt[1] = TypeSignature.getReturnSize(str);
        break;
      case 179: 
      case 181: 
        localReference = getReference();
        paramArrayOfInt[1] = 0;
        paramArrayOfInt[0] = TypeSignature.getTypeSize(localReference.getType());
        if (this.opcode == 181) {
          paramArrayOfInt[0] += 1;
        }
        break;
      case 178: 
      case 180: 
        localReference = getReference();
        paramArrayOfInt[1] = TypeSignature.getTypeSize(localReference.getType());
        paramArrayOfInt[0] = (this.opcode == 180 ? 1 : 0);
        break;
      case 197: 
        paramArrayOfInt[1] = 1;
        paramArrayOfInt[0] = getDimensions();
        break;
      case 186: 
      case 187: 
      case 188: 
      case 189: 
      case 190: 
      case 191: 
      case 192: 
      case 193: 
      case 194: 
      case 195: 
      case 196: 
      default: 
        throw new AssertError("Unknown Opcode: " + this.opcode);
      }
    }
  }
  
  public Instruction findMatchingPop()
  {
    int[] arrayOfInt = new int[2];
    getStackPopPush(arrayOfInt);
    int i = arrayOfInt[1];
    Instruction localInstruction = this;
    for (;;)
    {
      if ((localInstruction.succs != null) || (localInstruction.doesAlwaysJump())) {
        return null;
      }
      localInstruction = localInstruction.nextByAddr;
      if (localInstruction.preds != null) {
        return null;
      }
      localInstruction.getStackPopPush(arrayOfInt);
      if (i == arrayOfInt[0]) {
        return localInstruction;
      }
      i += arrayOfInt[1] - arrayOfInt[0];
    }
  }
  
  public Instruction findMatchingPush()
  {
    int i = 0;
    Instruction localInstruction = this;
    int[] arrayOfInt = new int[2];
    for (;;)
    {
      if (localInstruction.preds != null) {
        return null;
      }
      localInstruction = localInstruction.prevByAddr;
      if ((localInstruction == null) || (localInstruction.succs != null) || (localInstruction.doesAlwaysJump())) {
        return null;
      }
      localInstruction.getStackPopPush(arrayOfInt);
      if (i < arrayOfInt[1]) {
        return i == 0 ? localInstruction : null;
      }
      i += arrayOfInt[0] - arrayOfInt[1];
    }
  }
  
  public String getDescription()
  {
    StringBuffer localStringBuffer = new StringBuffer(String.valueOf(this.addr)).append('_').append(Integer.toHexString(hashCode())).append(": ").append(opcodeString[this.opcode]);
    if (this.opcode != 171)
    {
      if (hasLocalSlot()) {
        localStringBuffer.append(' ').append(getLocalSlot());
      }
      if (this.succs != null) {
        localStringBuffer.append(' ').append(((Instruction)this.succs).addr);
      }
      if (this.objData != null) {
        localStringBuffer.append(' ').append(this.objData);
      }
      if (this.opcode == 197) {
        localStringBuffer.append(' ').append(getDimensions());
      }
    }
    else
    {
      int[] arrayOfInt = getValues();
      Instruction[] arrayOfInstruction = getSuccs();
      for (int i = 0; i < arrayOfInt.length; i++) {
        localStringBuffer.append(' ').append(arrayOfInt[i]).append("->").append(arrayOfInstruction[i].addr);
      }
      localStringBuffer.append(' ').append("default: ").append(arrayOfInstruction[arrayOfInt.length].addr);
    }
    return localStringBuffer.toString();
  }
  
  public String toString()
  {
    return "" + this.addr + "_" + Integer.toHexString(hashCode());
  }
}


