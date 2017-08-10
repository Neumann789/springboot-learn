package jode.obfuscator.modules;

import java.io.PrintWriter;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;
import jode.AssertError;
import jode.GlobalOptions;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.LocalVariableInfo;
import jode.bytecode.MethodInfo;
import jode.bytecode.Opcodes;
import jode.bytecode.TypeSignature;
import jode.obfuscator.ClassBundle;
import jode.obfuscator.CodeTransformer;
import jode.obfuscator.Main;

public class LocalOptimizer
  implements Opcodes, CodeTransformer
{
  BytecodeInfo bc;
  TodoQueue changedInfos;
  InstrInfo firstInfo;
  Hashtable instrInfos;
  boolean produceLVT;
  int maxlocals;
  LocalInfo[] paramLocals;
  private InstrInfo CONFLICT = new InstrInfo();
  
  Vector merge(Vector paramVector1, Vector paramVector2)
  {
    if ((paramVector1 == null) || (paramVector1.isEmpty())) {
      return paramVector2;
    }
    if ((paramVector2 == null) || (paramVector2.isEmpty())) {
      return paramVector1;
    }
    Vector localVector = (Vector)paramVector1.clone();
    Enumeration localEnumeration = paramVector2.elements();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject = localEnumeration.nextElement();
      if (!localVector.contains(localObject)) {
        localVector.addElement(localObject);
      }
    }
    return localVector;
  }
  
  void promoteReads(InstrInfo paramInstrInfo, Instruction paramInstruction, BitSet paramBitSet, boolean paramBoolean)
  {
    InstrInfo localInstrInfo = (InstrInfo)this.instrInfos.get(paramInstruction);
    int i = -1;
    if ((paramInstruction.getOpcode() >= 54) && (paramInstruction.getOpcode() <= 58))
    {
      i = paramInstruction.getLocalSlot();
      if (paramInstrInfo.nextReads[i] != null) {
        localInstrInfo.local.combineInto(paramInstrInfo.nextReads[i].local);
      }
    }
    for (int j = 0; j < this.maxlocals; j++) {
      if ((paramInstrInfo.nextReads[j] != null) && (j != i) && ((paramBitSet == null) || (paramBitSet.get(j) != paramBoolean))) {
        if (localInstrInfo.nextReads[j] == null)
        {
          localInstrInfo.nextReads[j] = paramInstrInfo.nextReads[j];
          this.changedInfos.add(localInstrInfo);
        }
        else
        {
          localInstrInfo.nextReads[j].local.combineInto(paramInstrInfo.nextReads[j].local);
        }
      }
    }
  }
  
  void promoteReads(InstrInfo paramInstrInfo, Instruction paramInstruction)
  {
    promoteReads(paramInstrInfo, paramInstruction, null, false);
  }
  
  public LocalVariableInfo findLVTEntry(LocalVariableInfo[] paramArrayOfLocalVariableInfo, int paramInt1, int paramInt2)
  {
    LocalVariableInfo localLocalVariableInfo = null;
    for (int i = 0; i < paramArrayOfLocalVariableInfo.length; i++) {
      if ((paramArrayOfLocalVariableInfo[i].slot == paramInt1) && (paramArrayOfLocalVariableInfo[i].start.getAddr() <= paramInt2) && (paramArrayOfLocalVariableInfo[i].end.getAddr() >= paramInt2))
      {
        if ((localLocalVariableInfo != null) && ((!localLocalVariableInfo.name.equals(paramArrayOfLocalVariableInfo[i].name)) || (!localLocalVariableInfo.type.equals(paramArrayOfLocalVariableInfo[i].type)))) {
          return null;
        }
        localLocalVariableInfo = paramArrayOfLocalVariableInfo[i];
      }
    }
    return localLocalVariableInfo;
  }
  
  public LocalVariableInfo findLVTEntry(LocalVariableInfo[] paramArrayOfLocalVariableInfo, Instruction paramInstruction)
  {
    int i;
    if ((paramInstruction.getOpcode() >= 54) && (paramInstruction.getOpcode() <= 58)) {
      i = paramInstruction.getNextAddr();
    } else {
      i = paramInstruction.getAddr();
    }
    return findLVTEntry(paramArrayOfLocalVariableInfo, paramInstruction.getLocalSlot(), i);
  }
  
  public void calcLocalInfo()
  {
    this.maxlocals = this.bc.getMaxLocals();
    Handler[] arrayOfHandler = this.bc.getExceptionHandlers();
    LocalVariableInfo[] arrayOfLocalVariableInfo = this.bc.getLocalVariableTable();
    if (arrayOfLocalVariableInfo != null) {
      this.produceLVT = true;
    }
    Object localObject1 = this.bc.getMethodInfo().getType();
    int j = (this.bc.getMethodInfo().isStatic() ? 0 : 1) + TypeSignature.getArgumentSize((String)localObject1);
    this.paramLocals = new LocalInfo[j];
    int k = 0;
    Object localObject3;
    if (!this.bc.getMethodInfo().isStatic())
    {
      LocalInfo localLocalInfo = new LocalInfo();
      if (arrayOfLocalVariableInfo != null)
      {
        localObject3 = findLVTEntry(arrayOfLocalVariableInfo, 0, 0);
        if (localObject3 != null)
        {
          localLocalInfo.name = ((LocalVariableInfo)localObject3).name;
          localLocalInfo.type = ((LocalVariableInfo)localObject3).type;
        }
      }
      localLocalInfo.size = 1;
      this.paramLocals[(k++)] = localLocalInfo;
    }
    int n = 1;
    Object localObject4;
    while ((n < ((String)localObject1).length()) && (((String)localObject1).charAt(n) != ')'))
    {
      localObject3 = new LocalInfo();
      if (arrayOfLocalVariableInfo != null)
      {
        localObject4 = findLVTEntry(arrayOfLocalVariableInfo, k, 0);
        if (localObject4 != null) {
          ((LocalInfo)localObject3).name = ((LocalVariableInfo)localObject4).name;
        }
      }
      localObject4 = n;
      n = TypeSignature.skipType((String)localObject1, n);
      ((LocalInfo)localObject3).type = ((String)localObject1).substring(localObject4, n);
      ((LocalInfo)localObject3).size = TypeSignature.getTypeSize(((LocalInfo)localObject3).type);
      this.paramLocals[k] = localObject3;
      k += ((LocalInfo)localObject3).size;
    }
    this.changedInfos = new TodoQueue(null);
    this.instrInfos = new Hashtable();
    localObject1 = this.firstInfo = new InstrInfo();
    Object localObject2 = this.bc.getInstructions().iterator();
    for (;;)
    {
      Instruction localInstruction1 = (Instruction)((Iterator)localObject2).next();
      this.instrInfos.put(localInstruction1, localObject1);
      ((InstrInfo)localObject1).instr = localInstruction1;
      ((InstrInfo)localObject1).nextReads = new InstrInfo[this.maxlocals];
      if (localInstruction1.hasLocalSlot())
      {
        ((InstrInfo)localObject1).local = new LocalInfo((InstrInfo)localObject1);
        if (arrayOfLocalVariableInfo != null)
        {
          LocalVariableInfo localLocalVariableInfo = findLVTEntry(arrayOfLocalVariableInfo, localInstruction1);
          if (localLocalVariableInfo != null)
          {
            ((InstrInfo)localObject1).local.name = localLocalVariableInfo.name;
            ((InstrInfo)localObject1).local.type = localLocalVariableInfo.type;
          }
        }
        ((InstrInfo)localObject1).local.size = 1;
        switch (localInstruction1.getOpcode())
        {
        case 22: 
        case 24: 
          ((InstrInfo)localObject1).local.size = 2;
        case 21: 
        case 23: 
        case 25: 
        case 132: 
          ((InstrInfo)localObject1).nextReads[localInstruction1.getLocalSlot()] = localObject1;
          this.changedInfos.add((InstrInfo)localObject1);
          break;
        case 169: 
          ((InstrInfo)localObject1).usedBySub = new BitSet();
          ((InstrInfo)localObject1).nextReads[localInstruction1.getLocalSlot()] = localObject1;
          this.changedInfos.add((InstrInfo)localObject1);
          break;
        case 55: 
        case 57: 
          ((InstrInfo)localObject1).local.size = 2;
        }
      }
      if (!((Iterator)localObject2).hasNext()) {
        break;
      }
      localObject1 = ((InstrInfo)localObject1).nextInfo = new InstrInfo();
    }
    while (!this.changedInfos.isEmpty())
    {
      localObject1 = this.changedInfos.remove();
      localObject2 = ((InstrInfo)localObject1).instr;
      if (((Instruction)localObject2).hasLocalSlot())
      {
        int m = ((Instruction)localObject2).getLocalSlot();
        for (int i1 = 0; i1 < this.maxlocals; i1++)
        {
          localObject3 = localObject1.nextReads[i1];
          if ((localObject3 != null) && (((InstrInfo)localObject3).instr.getOpcode() == 169) && (!((InstrInfo)localObject3).usedBySub.get(m)))
          {
            ((InstrInfo)localObject3).usedBySub.set(m);
            if (((InstrInfo)localObject3).jsrTargetInfo != null) {
              this.changedInfos.add(((InstrInfo)localObject3).jsrTargetInfo);
            }
          }
        }
      }
      Instruction localInstruction2 = ((Instruction)localObject2).getPrevByAddr();
      if (localInstruction2 != null) {
        if (!localInstruction2.doesAlwaysJump())
        {
          promoteReads((InstrInfo)localObject1, localInstruction2);
        }
        else if (localInstruction2.getOpcode() == 168)
        {
          InstrInfo localInstrInfo1 = (InstrInfo)this.instrInfos.get(localInstruction2.getSingleSucc());
          if (localInstrInfo1.retInfo != null)
          {
            promoteReads((InstrInfo)localObject1, localInstrInfo1.retInfo.instr, localInstrInfo1.retInfo.usedBySub, false);
            promoteReads((InstrInfo)localObject1, localInstruction2, localInstrInfo1.retInfo.usedBySub, true);
          }
        }
      }
      if (((Instruction)localObject2).getPreds() != null) {
        for (i2 = 0; i2 < ((Instruction)localObject2).getPreds().length; i2++)
        {
          localObject3 = localObject2.getPreds()[i2];
          if (localObject2.getPreds()[i2].getOpcode() == 168)
          {
            if (((InstrInfo)localObject1).instr.getOpcode() != 58) {
              throw new AssertError("Non standard jsr");
            }
            localObject4 = localObject1.nextInfo.nextReads[localObject1.instr.getLocalSlot()];
            if (localObject4 != null)
            {
              if (((InstrInfo)localObject4).instr.getOpcode() != 169) {
                throw new AssertError("reading return address");
              }
              ((InstrInfo)localObject1).retInfo = ((InstrInfo)localObject4);
              ((InstrInfo)localObject4).jsrTargetInfo = ((InstrInfo)localObject1);
              Instruction localInstruction3 = ((Instruction)localObject3).getNextByAddr();
              InstrInfo localInstrInfo2 = (InstrInfo)this.instrInfos.get(localInstruction3);
              promoteReads(localInstrInfo2, ((InstrInfo)localObject4).instr, ((InstrInfo)localObject4).usedBySub, false);
              promoteReads(localInstrInfo2, (Instruction)localObject3, ((InstrInfo)localObject4).usedBySub, true);
            }
          }
          promoteReads((InstrInfo)localObject1, localObject2.getPreds()[i2]);
        }
      }
      for (int i2 = 0; i2 < arrayOfHandler.length; i2++) {
        if (arrayOfHandler[i2].catcher == localObject2) {
          for (localObject3 = arrayOfHandler[i2].start; localObject3 != arrayOfHandler[i2].end.getNextByAddr(); localObject3 = ((Instruction)localObject3).getNextByAddr()) {
            promoteReads((InstrInfo)localObject1, (Instruction)localObject3);
          }
        }
      }
    }
    this.changedInfos = null;
    for (int i = 0; i < this.paramLocals.length; i++) {
      if (this.firstInfo.nextReads[i] != null)
      {
        this.firstInfo.nextReads[i].local.combineInto(this.paramLocals[i]);
        this.paramLocals[i] = this.paramLocals[i].getReal();
      }
    }
  }
  
  public void stripLocals()
  {
    ListIterator localListIterator = this.bc.getInstructions().listIterator();
    for (InstrInfo localInstrInfo = this.firstInfo; localInstrInfo != null; localInstrInfo = localInstrInfo.nextInfo)
    {
      Instruction localInstruction = (Instruction)localListIterator.next();
      if ((localInstrInfo.local != null) && (localInstrInfo.local.usingInstrs.size() == 1)) {
        switch (localInstruction.getOpcode())
        {
        case 54: 
        case 56: 
        case 58: 
          localListIterator.set(new Instruction(87));
          break;
        case 55: 
        case 57: 
          localListIterator.set(new Instruction(88));
        }
      }
    }
  }
  
  void distributeLocals(Vector paramVector)
  {
    if (paramVector.size() == 0) {
      return;
    }
    int i = Integer.MAX_VALUE;
    Object localObject1 = null;
    Enumeration localEnumeration1 = paramVector.elements();
    Object localObject2;
    while (localEnumeration1.hasMoreElements())
    {
      LocalInfo localLocalInfo = (LocalInfo)localEnumeration1.nextElement();
      int k = 0;
      localObject2 = localLocalInfo.conflictingLocals.elements();
      while (((Enumeration)localObject2).hasMoreElements()) {
        if (((LocalInfo)((Enumeration)localObject2).nextElement()).newSlot != -2) {
          k++;
        }
      }
      if (k < i)
      {
        i = k;
        localObject1 = localLocalInfo;
      }
    }
    paramVector.removeElement(localObject1);
    ((LocalInfo)localObject1).newSlot = -2;
    distributeLocals(paramVector);
    label241:
    for (int j = 0;; j++)
    {
      Enumeration localEnumeration2 = ((LocalInfo)localObject1).conflictingLocals.elements();
      while (localEnumeration2.hasMoreElements())
      {
        localObject2 = (LocalInfo)localEnumeration2.nextElement();
        if ((((LocalInfo)localObject1).size == 2) && (((LocalInfo)localObject2).newSlot == j + 1))
        {
          j++;
          break label241;
        }
        if ((((LocalInfo)localObject2).size == 2) && (((LocalInfo)localObject2).newSlot + 1 == j)) {
          break label241;
        }
        if (((LocalInfo)localObject2).newSlot == j)
        {
          if (((LocalInfo)localObject2).size != 2) {
            break label241;
          }
          j++;
          break label241;
        }
      }
      ((LocalInfo)localObject1).newSlot = j;
      break;
    }
  }
  
  public void distributeLocals()
  {
    for (int i = 0; i < this.paramLocals.length; i++) {
      if (this.paramLocals[i] != null) {
        this.paramLocals[i].newSlot = i;
      }
    }
    for (Object localObject = this.firstInfo; localObject != null; localObject = ((InstrInfo)localObject).nextInfo) {
      if ((((InstrInfo)localObject).instr.getOpcode() >= 54) && (((InstrInfo)localObject).instr.getOpcode() <= 58)) {
        for (int j = 0; j < this.maxlocals; j++)
        {
          if ((j != ((InstrInfo)localObject).instr.getLocalSlot()) && (localObject.nextReads[j] != null)) {
            ((InstrInfo)localObject).local.conflictsWith(localObject.nextReads[j].local);
          }
          if ((localObject.nextInfo.nextReads[j] != null) && (localObject.nextInfo.nextReads[j].jsrTargetInfo != null))
          {
            Instruction[] arrayOfInstruction = localObject.nextInfo.nextReads[j].jsrTargetInfo.instr.getPreds();
            for (int k = 0; k < arrayOfInstruction.length; k++)
            {
              InstrInfo localInstrInfo2 = (InstrInfo)this.instrInfos.get(arrayOfInstruction[k]);
              for (int m = 0; m < this.maxlocals; m++) {
                if ((!localObject.nextInfo.nextReads[j].usedBySub.get(m)) && (localInstrInfo2.nextReads[m] != null)) {
                  ((InstrInfo)localObject).local.conflictsWith(localInstrInfo2.nextReads[m].local);
                }
              }
            }
          }
        }
      }
    }
    localObject = new Vector();
    for (InstrInfo localInstrInfo1 = this.firstInfo; localInstrInfo1 != null; localInstrInfo1 = localInstrInfo1.nextInfo) {
      if ((localInstrInfo1.local != null) && (localInstrInfo1.local.newSlot == -1) && (!((Vector)localObject).contains(localInstrInfo1.local))) {
        ((Vector)localObject).addElement(localInstrInfo1.local);
      }
    }
    distributeLocals((Vector)localObject);
    for (localInstrInfo1 = this.firstInfo; localInstrInfo1 != null; localInstrInfo1 = localInstrInfo1.nextInfo) {
      if (localInstrInfo1.local != null) {
        localInstrInfo1.instr.setLocalSlot(localInstrInfo1.local.newSlot);
      }
    }
    if (this.produceLVT) {
      buildNewLVT();
    }
  }
  
  boolean promoteLifeLocals(LocalInfo[] paramArrayOfLocalInfo, InstrInfo paramInstrInfo)
  {
    if (paramInstrInfo.lifeLocals == null)
    {
      paramInstrInfo.lifeLocals = ((LocalInfo[])paramArrayOfLocalInfo.clone());
      return true;
    }
    boolean bool = false;
    for (int i = 0; i < this.maxlocals; i++)
    {
      LocalInfo localLocalInfo1 = paramInstrInfo.lifeLocals[i];
      if (localLocalInfo1 != null)
      {
        localLocalInfo1 = localLocalInfo1.getReal();
        LocalInfo localLocalInfo2 = paramArrayOfLocalInfo[i];
        if (localLocalInfo2 != null) {
          localLocalInfo2 = localLocalInfo2.getReal();
        }
        if (localLocalInfo1 != localLocalInfo2)
        {
          paramInstrInfo.lifeLocals[i] = null;
          bool = true;
        }
      }
    }
    return bool;
  }
  
  public void buildNewLVT()
  {
    for (InstrInfo localInstrInfo1 = this.firstInfo; localInstrInfo1 != null; localInstrInfo1 = localInstrInfo1.nextInfo) {
      if (localInstrInfo1.usedBySub != null) {
        localInstrInfo1.usedBySub = new BitSet();
      }
    }
    for (localInstrInfo1 = this.firstInfo; localInstrInfo1 != null; localInstrInfo1 = localInstrInfo1.nextInfo) {
      if (localInstrInfo1.local != null) {
        for (int j = 0; j < localInstrInfo1.nextReads.length; j++) {
          if ((localInstrInfo1.nextReads[j] != null) && (localInstrInfo1.nextReads[j].instr.getOpcode() == 169)) {
            localInstrInfo1.nextReads[j].usedBySub.set(localInstrInfo1.local.newSlot);
          }
        }
      }
    }
    this.firstInfo.lifeLocals = new LocalInfo[this.maxlocals];
    for (int i = 0; i < this.paramLocals.length; i++) {
      this.firstInfo.lifeLocals[i] = this.paramLocals[i];
    }
    Stack localStack = new Stack();
    localStack.push(this.firstInfo);
    Handler[] arrayOfHandler = this.bc.getExceptionHandlers();
    LocalInfo[] arrayOfLocalInfo2;
    while (!localStack.isEmpty())
    {
      localObject1 = (InstrInfo)localStack.pop();
      localObject2 = ((InstrInfo)localObject1).instr;
      arrayOfLocalInfo1 = ((InstrInfo)localObject1).lifeLocals;
      if (((Instruction)localObject2).hasLocalSlot())
      {
        int k = ((Instruction)localObject2).getLocalSlot();
        LocalInfo localLocalInfo = ((InstrInfo)localObject1).local.getReal();
        arrayOfLocalInfo1 = (LocalInfo[])arrayOfLocalInfo1.clone();
        arrayOfLocalInfo1[k] = localLocalInfo;
        if (localLocalInfo.name != null) {
          for (int i4 = 0; i4 < arrayOfLocalInfo1.length; i4++) {
            if ((i4 != k) && (arrayOfLocalInfo1[i4] != null) && (localLocalInfo.name.equals(arrayOfLocalInfo1[i4].name))) {
              arrayOfLocalInfo1[i4] = null;
            }
          }
        }
      }
      Object localObject3;
      if (!((Instruction)localObject2).doesAlwaysJump())
      {
        localObject3 = ((InstrInfo)localObject1).nextInfo;
        if (promoteLifeLocals(arrayOfLocalInfo1, (InstrInfo)localObject3)) {
          localStack.push(localObject3);
        }
      }
      InstrInfo localInstrInfo4;
      if (((Instruction)localObject2).hasSuccs())
      {
        localObject3 = ((Instruction)localObject2).getSuccs();
        for (int i1 = 0; i1 < localObject3.length; i1++)
        {
          localInstrInfo4 = (InstrInfo)this.instrInfos.get(localObject3[i1]);
          if (promoteLifeLocals(arrayOfLocalInfo1, localInstrInfo4)) {
            localStack.push(localInstrInfo4);
          }
        }
      }
      InstrInfo localInstrInfo2;
      for (int m = 0; m < arrayOfHandler.length; m++) {
        if ((arrayOfHandler[m].start.compareTo((Instruction)localObject2) <= 0) && (arrayOfHandler[m].end.compareTo((Instruction)localObject2) >= 0))
        {
          localInstrInfo2 = (InstrInfo)this.instrInfos.get(arrayOfHandler[m].catcher);
          if (promoteLifeLocals(arrayOfLocalInfo1, localInstrInfo2)) {
            localStack.push(localInstrInfo2);
          }
        }
      }
      Instruction localInstruction1;
      int i6;
      if (((InstrInfo)localObject1).instr.getOpcode() == 168)
      {
        localInstruction1 = ((InstrInfo)localObject1).instr.getSingleSucc();
        localInstrInfo2 = (InstrInfo)this.instrInfos.get(localInstruction1);
        localInstrInfo4 = localInstrInfo2.retInfo;
        if ((localInstrInfo4 != null) && (localInstrInfo4.lifeLocals != null))
        {
          arrayOfLocalInfo2 = (LocalInfo[])arrayOfLocalInfo1.clone();
          for (i6 = 0; i6 < this.maxlocals; i6++) {
            if (localInstrInfo4.usedBySub.get(i6)) {
              arrayOfLocalInfo2[i6] = localInstrInfo4.lifeLocals[i6];
            }
          }
          if (promoteLifeLocals(arrayOfLocalInfo2, ((InstrInfo)localObject1).nextInfo)) {
            localStack.push(((InstrInfo)localObject1).nextInfo);
          }
        }
      }
      if (((InstrInfo)localObject1).jsrTargetInfo != null)
      {
        localInstruction1 = ((InstrInfo)localObject1).jsrTargetInfo.instr;
        for (int i2 = 0; i2 < localInstruction1.getPreds().length; i2++)
        {
          localInstrInfo4 = (InstrInfo)this.instrInfos.get(localInstruction1.getPreds()[i2]);
          if (localInstrInfo4.lifeLocals != null)
          {
            arrayOfLocalInfo2 = (LocalInfo[])arrayOfLocalInfo1.clone();
            for (i6 = 0; i6 < this.maxlocals; i6++) {
              if (!((InstrInfo)localObject1).usedBySub.get(i6)) {
                arrayOfLocalInfo2[i6] = localInstrInfo4.lifeLocals[i6];
              }
            }
            if (promoteLifeLocals(arrayOfLocalInfo2, localInstrInfo4.nextInfo)) {
              localStack.push(localInstrInfo4.nextInfo);
            }
          }
        }
      }
    }
    Object localObject1 = new Vector();
    Object localObject2 = new LocalVariableInfo[this.maxlocals];
    LocalInfo[] arrayOfLocalInfo1 = new LocalInfo[this.maxlocals];
    for (int n = 0; n < this.paramLocals.length; n++) {
      if (this.paramLocals[n] != null)
      {
        arrayOfLocalInfo1[n] = this.paramLocals[n];
        if (arrayOfLocalInfo1[n].name != null)
        {
          localObject2[n] = new LocalVariableInfo();
          ((Vector)localObject1).addElement(localObject2[n]);
          localObject2[n].name = arrayOfLocalInfo1[n].name;
          localObject2[n].type = Main.getClassBundle().getTypeAlias(arrayOfLocalInfo1[n].type);
          localObject2[n].start = ((Instruction)this.bc.getInstructions().get(0));
          localObject2[n].slot = n;
        }
      }
    }
    Instruction localInstruction2 = null;
    for (InstrInfo localInstrInfo3 = this.firstInfo; localInstrInfo3 != null; localInstrInfo3 = localInstrInfo3.nextInfo)
    {
      for (int i5 = 0; i5 < this.maxlocals; i5++)
      {
        arrayOfLocalInfo2 = localInstrInfo3.lifeLocals != null ? localInstrInfo3.lifeLocals[i5] : null;
        if ((arrayOfLocalInfo2 != arrayOfLocalInfo1[i5]) && ((arrayOfLocalInfo2 == null) || (arrayOfLocalInfo1[i5] == null) || (arrayOfLocalInfo2.name == null) || (arrayOfLocalInfo2.type == null) || (!arrayOfLocalInfo2.name.equals(arrayOfLocalInfo1[i5].name)) || (!arrayOfLocalInfo2.type.equals(arrayOfLocalInfo1[i5].type))))
        {
          if (localObject2[i5] != null) {
            localObject2[i5].end = localInstrInfo3.instr.getPrevByAddr();
          }
          localObject2[i5] = null;
          arrayOfLocalInfo1[i5] = arrayOfLocalInfo2;
          if ((arrayOfLocalInfo1[i5] != null) && (arrayOfLocalInfo1[i5].name != null) && (arrayOfLocalInfo1[i5].type != null))
          {
            localObject2[i5] = new LocalVariableInfo();
            ((Vector)localObject1).addElement(localObject2[i5]);
            localObject2[i5].name = arrayOfLocalInfo1[i5].name;
            localObject2[i5].type = Main.getClassBundle().getTypeAlias(arrayOfLocalInfo1[i5].type);
            localObject2[i5].start = localInstrInfo3.instr;
            localObject2[i5].slot = i5;
          }
        }
      }
      localInstruction2 = localInstrInfo3.instr;
    }
    for (int i3 = 0; i3 < this.maxlocals; i3++) {
      if (localObject2[i3] != null) {
        localObject2[i3].end = localInstruction2;
      }
    }
    LocalVariableInfo[] arrayOfLocalVariableInfo = new LocalVariableInfo[((Vector)localObject1).size()];
    ((Vector)localObject1).copyInto(arrayOfLocalVariableInfo);
    this.bc.setLocalVariableTable(arrayOfLocalVariableInfo);
  }
  
  public void dumpLocals()
  {
    Vector localVector = new Vector();
    for (Object localObject1 = this.firstInfo; localObject1 != null; localObject1 = ((InstrInfo)localObject1).nextInfo)
    {
      GlobalOptions.err.println(((InstrInfo)localObject1).instr.getDescription());
      GlobalOptions.err.print("nextReads: ");
      for (int i = 0; i < this.maxlocals; i++) {
        if (localObject1.nextReads[i] == null) {
          GlobalOptions.err.print("-,");
        } else {
          GlobalOptions.err.print(localObject1.nextReads[i].instr.getAddr() + ",");
        }
      }
      if (((InstrInfo)localObject1).usedBySub != null) {
        GlobalOptions.err.print("  usedBySub: " + ((InstrInfo)localObject1).usedBySub);
      }
      if (((InstrInfo)localObject1).retInfo != null) {
        GlobalOptions.err.print("  ret info: " + ((InstrInfo)localObject1).retInfo.instr.getAddr());
      }
      if (((InstrInfo)localObject1).jsrTargetInfo != null) {
        GlobalOptions.err.print("  jsr info: " + ((InstrInfo)localObject1).jsrTargetInfo.instr.getAddr());
      }
      GlobalOptions.err.println();
      if ((((InstrInfo)localObject1).local != null) && (!localVector.contains(((InstrInfo)localObject1).local))) {
        localVector.addElement(((InstrInfo)localObject1).local);
      }
    }
    localObject1 = localVector.elements();
    while (((Enumeration)localObject1).hasMoreElements())
    {
      LocalInfo localLocalInfo = (LocalInfo)((Enumeration)localObject1).nextElement();
      int j = ((InstrInfo)localLocalInfo.usingInstrs.elementAt(0)).instr.getLocalSlot();
      GlobalOptions.err.print("Slot: " + j + " conflicts:");
      Enumeration localEnumeration = localLocalInfo.conflictingLocals.elements();
      while (localEnumeration.hasMoreElements())
      {
        localObject2 = (LocalInfo)localEnumeration.nextElement();
        GlobalOptions.err.print(((LocalInfo)localObject2).getFirstAddr() + ", ");
      }
      GlobalOptions.err.println();
      GlobalOptions.err.print(localLocalInfo.getFirstAddr());
      GlobalOptions.err.print("     instrs: ");
      Object localObject2 = localLocalInfo.usingInstrs.elements();
      while (((Enumeration)localObject2).hasMoreElements()) {
        GlobalOptions.err.print(((InstrInfo)((Enumeration)localObject2).nextElement()).instr.getAddr() + ", ");
      }
      GlobalOptions.err.println();
    }
    GlobalOptions.err.println("-----------");
  }
  
  public void transformCode(BytecodeInfo paramBytecodeInfo)
  {
    this.bc = paramBytecodeInfo;
    calcLocalInfo();
    if ((GlobalOptions.debuggingFlags & 0x100) != 0)
    {
      GlobalOptions.err.println("Before Local Optimization: ");
      dumpLocals();
    }
    stripLocals();
    distributeLocals();
    if ((GlobalOptions.debuggingFlags & 0x100) != 0)
    {
      GlobalOptions.err.println("After Local Optimization: ");
      dumpLocals();
    }
    this.firstInfo = null;
    this.changedInfos = null;
    this.instrInfos = null;
    this.paramLocals = null;
  }
  
  static class InstrInfo
  {
    InstrInfo nextTodo;
    LocalOptimizer.LocalInfo local;
    InstrInfo[] nextReads;
    BitSet usedBySub;
    LocalOptimizer.LocalInfo[] lifeLocals;
    InstrInfo retInfo;
    InstrInfo jsrTargetInfo;
    Instruction instr;
    InstrInfo nextInfo;
  }
  
  private static class TodoQueue
  {
    public final LocalOptimizer.InstrInfo LAST = new LocalOptimizer.InstrInfo();
    LocalOptimizer.InstrInfo first = this.LAST;
    
    public void add(LocalOptimizer.InstrInfo paramInstrInfo)
    {
      if (paramInstrInfo.nextTodo == null)
      {
        paramInstrInfo.nextTodo = this.first;
        this.first = paramInstrInfo;
      }
    }
    
    public boolean isEmpty()
    {
      return this.first == this.LAST;
    }
    
    public LocalOptimizer.InstrInfo remove()
    {
      if (this.first == this.LAST) {
        throw new NoSuchElementException();
      }
      LocalOptimizer.InstrInfo localInstrInfo = this.first;
      this.first = localInstrInfo.nextTodo;
      localInstrInfo.nextTodo = null;
      return localInstrInfo;
    }
  }
  
  class LocalInfo
  {
    LocalInfo shadow = null;
    String name;
    String type;
    Vector usingInstrs = new Vector();
    Vector conflictingLocals = new Vector();
    int size;
    int newSlot = -1;
    
    public LocalInfo getReal()
    {
      for (LocalInfo localLocalInfo = this; localLocalInfo.shadow != null; localLocalInfo = localLocalInfo.shadow) {}
      return localLocalInfo;
    }
    
    LocalInfo() {}
    
    LocalInfo(LocalOptimizer.InstrInfo paramInstrInfo)
    {
      this.usingInstrs.addElement(paramInstrInfo);
    }
    
    void conflictsWith(LocalInfo paramLocalInfo)
    {
      if (this.shadow != null)
      {
        getReal().conflictsWith(paramLocalInfo);
      }
      else
      {
        paramLocalInfo = paramLocalInfo.getReal();
        if (!this.conflictingLocals.contains(paramLocalInfo))
        {
          this.conflictingLocals.addElement(paramLocalInfo);
          paramLocalInfo.conflictingLocals.addElement(this);
        }
      }
    }
    
    void combineInto(LocalInfo paramLocalInfo)
    {
      if (this.shadow != null)
      {
        getReal().combineInto(paramLocalInfo);
        return;
      }
      paramLocalInfo = paramLocalInfo.getReal();
      if (this == paramLocalInfo) {
        return;
      }
      this.shadow = paramLocalInfo;
      if (this.shadow.name == null)
      {
        this.shadow.name = this.name;
        this.shadow.type = this.type;
      }
      Enumeration localEnumeration = this.usingInstrs.elements();
      while (localEnumeration.hasMoreElements())
      {
        LocalOptimizer.InstrInfo localInstrInfo = (LocalOptimizer.InstrInfo)localEnumeration.nextElement();
        localInstrInfo.local = paramLocalInfo;
        paramLocalInfo.usingInstrs.addElement(localInstrInfo);
      }
    }
    
    public int getFirstAddr()
    {
      int i = Integer.MAX_VALUE;
      Enumeration localEnumeration = this.usingInstrs.elements();
      while (localEnumeration.hasMoreElements())
      {
        LocalOptimizer.InstrInfo localInstrInfo = (LocalOptimizer.InstrInfo)localEnumeration.nextElement();
        if (localInstrInfo.instr.getAddr() < i) {
          i = localInstrInfo.instr.getAddr();
        }
      }
      return i;
    }
  }
}


