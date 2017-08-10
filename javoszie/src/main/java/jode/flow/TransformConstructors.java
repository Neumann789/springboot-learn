package jode.flow;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import jode.GlobalOptions;
import jode.bytecode.ClassInfo;
import jode.bytecode.InnerClassInfo;
import jode.bytecode.MethodInfo;
import jode.decompiler.ClassAnalyzer;
import jode.decompiler.FieldAnalyzer;
import jode.decompiler.LocalInfo;
import jode.decompiler.MethodAnalyzer;
import jode.decompiler.Options;
import jode.decompiler.OuterValueListener;
import jode.decompiler.OuterValues;
import jode.expr.Expression;
import jode.expr.FieldOperator;
import jode.expr.IIncOperator;
import jode.expr.InvokeOperator;
import jode.expr.LocalLoadOperator;
import jode.expr.LocalStoreOperator;
import jode.expr.LocalVarOperator;
import jode.expr.Operator;
import jode.expr.PutFieldOperator;
import jode.expr.StoreInstruction;
import jode.expr.ThisOperator;
import jode.type.MethodType;
import jode.type.Type;

public class TransformConstructors
{
  ClassAnalyzer clazzAnalyzer;
  boolean isStatic;
  MethodAnalyzer[] cons;
  int type0Count;
  int type01Count;
  OuterValues outerValues;
  
  public TransformConstructors(ClassAnalyzer paramClassAnalyzer, boolean paramBoolean, MethodAnalyzer[] paramArrayOfMethodAnalyzer)
  {
    this.clazzAnalyzer = paramClassAnalyzer;
    this.isStatic = paramBoolean;
    this.cons = paramArrayOfMethodAnalyzer;
    if (!paramBoolean) {
      this.outerValues = paramClassAnalyzer.getOuterValues();
    }
    lookForConstructorCall();
  }
  
  private int getConstructorType(StructuredBlock paramStructuredBlock)
  {
    InstructionBlock localInstructionBlock;
    if ((paramStructuredBlock instanceof InstructionBlock)) {
      localInstructionBlock = (InstructionBlock)paramStructuredBlock;
    } else if (((paramStructuredBlock instanceof SequentialBlock)) && ((paramStructuredBlock.getSubBlocks()[0] instanceof InstructionBlock))) {
      localInstructionBlock = (InstructionBlock)paramStructuredBlock.getSubBlocks()[0];
    } else {
      return 0;
    }
    Expression localExpression1 = localInstructionBlock.getInstruction().simplify();
    if ((!(localExpression1 instanceof InvokeOperator)) || (localExpression1.getFreeOperandCount() != 0)) {
      return 0;
    }
    InvokeOperator localInvokeOperator = (InvokeOperator)localExpression1;
    if ((!localInvokeOperator.isConstructor()) || (!localInvokeOperator.isSuperOrThis())) {
      return 0;
    }
    Expression localExpression2 = localInvokeOperator.getSubExpressions()[0];
    if (!isThis(localExpression2, this.clazzAnalyzer.getClazz())) {
      return 0;
    }
    if (localInvokeOperator.isThis()) {
      return 2;
    }
    return 1;
  }
  
  public void lookForConstructorCall()
  {
    this.type01Count = this.cons.length;
    int i = 0;
    while (i < this.type01Count)
    {
      MethodAnalyzer localMethodAnalyzer = this.cons[i];
      FlowBlock localFlowBlock = this.cons[i].getMethodHeader();
      if ((localFlowBlock == null) || (!localFlowBlock.hasNoJumps())) {
        return;
      }
      StructuredBlock localStructuredBlock = this.cons[i].getMethodHeader().block;
      int j = this.isStatic ? 0 : getConstructorType(localStructuredBlock);
      if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
        GlobalOptions.err.println("constr " + i + ": type" + j + " " + localStructuredBlock);
      }
      switch (j)
      {
      case 0: 
        this.cons[i] = this.cons[this.type0Count];
        this.cons[(this.type0Count++)] = localMethodAnalyzer;
      case 1: 
        i++;
        break;
      case 2: 
        this.cons[i] = this.cons[(--this.type01Count)];
        this.cons[this.type01Count] = localMethodAnalyzer;
      }
    }
  }
  
  public static boolean isThis(Expression paramExpression, ClassInfo paramClassInfo)
  {
    return ((paramExpression instanceof ThisOperator)) && (((ThisOperator)paramExpression).getClassInfo() == paramClassInfo);
  }
  
  private void checkAnonymousConstructor()
  {
    if ((this.isStatic) || (this.cons.length != 1) || (this.type01Count - this.type0Count != 1) || (this.clazzAnalyzer.getName() != null)) {
      return;
    }
    if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
      GlobalOptions.err.println("checkAnonymousConstructor of " + this.clazzAnalyzer.getClazz());
    }
    StructuredBlock localStructuredBlock = this.cons[0].getMethodHeader().block;
    if ((localStructuredBlock instanceof SequentialBlock)) {
      localStructuredBlock = localStructuredBlock.getSubBlocks()[0];
    }
    InstructionBlock localInstructionBlock = (InstructionBlock)localStructuredBlock;
    Expression localExpression = localInstructionBlock.getInstruction().simplify();
    InvokeOperator localInvokeOperator = (InvokeOperator)localExpression;
    Expression[] arrayOfExpression = localInvokeOperator.getSubExpressions();
    for (int i = 1; i < arrayOfExpression.length; i++) {
      if (!(arrayOfExpression[i] instanceof LocalLoadOperator)) {
        return;
      }
    }
    Type[] arrayOfType = this.cons[0].getType().getParameterTypes();
    int j = 0;
    int k = arrayOfType.length;
    int m = 1;
    for (int n = 0; n < arrayOfType.length - 1; n++) {
      m += arrayOfType[n].stackSize();
    }
    n = 1;
    if (arrayOfExpression.length > 2)
    {
      LocalLoadOperator localLocalLoadOperator1 = (LocalLoadOperator)arrayOfExpression[1];
      if (localLocalLoadOperator1.getLocalInfo().getSlot() == m)
      {
        j = 1;
        n++;
        k--;
        m -= arrayOfType[(k - 1)].stackSize();
      }
    }
    int i1 = arrayOfExpression.length - 1;
    while (i1 >= n)
    {
      localObject = (LocalLoadOperator)arrayOfExpression[i1];
      if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
        GlobalOptions.err.println("  pos " + i1 + ": " + m + "," + ((LocalLoadOperator)localObject).getLocalInfo().getSlot() + "; " + k);
      }
      if (((LocalLoadOperator)localObject).getLocalInfo().getSlot() != m)
      {
        m += arrayOfType[(k - 1)].stackSize();
        break;
      }
      i1--;
      k--;
      if (k == 0) {
        break;
      }
      m -= arrayOfType[(k - 1)].stackSize();
    }
    Object localObject = localInvokeOperator.getClassAnalyzer();
    OuterValues localOuterValues = null;
    if ((localObject != null) && ((((ClassAnalyzer)localObject).getParent() instanceof MethodAnalyzer))) {
      localOuterValues = ((ClassAnalyzer)localObject).getOuterValues();
    }
    int i2 = i1 - n + 1;
    if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
      GlobalOptions.err.println("  super outer: " + localOuterValues);
    }
    LocalLoadOperator localLocalLoadOperator2;
    while (i1 >= n)
    {
      localLocalLoadOperator2 = (LocalLoadOperator)arrayOfExpression[i1];
      if (localLocalLoadOperator2.getLocalInfo().getSlot() >= m)
      {
        if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
          GlobalOptions.err.println("  Illegal slot at " + i1 + ":" + localLocalLoadOperator2.getLocalInfo().getSlot());
        }
        return;
      }
      i1--;
    }
    if ((i2 == 1) && (localObject != null) && ((((ClassAnalyzer)localObject).getParent() instanceof ClassAnalyzer)))
    {
      localLocalLoadOperator2 = (LocalLoadOperator)arrayOfExpression[n];
      if ((this.outerValues.getValueBySlot(localLocalLoadOperator2.getLocalInfo().getSlot()) instanceof ThisOperator))
      {
        i2 = 0;
        this.outerValues.setImplicitOuterClass(true);
      }
    }
    if (i2 > 0)
    {
      if ((localOuterValues == null) || (localOuterValues.getCount() < i2))
      {
        if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
          GlobalOptions.err.println("  super outer doesn't match: " + i2);
        }
        return;
      }
      localOuterValues.setMinCount(i2);
    }
    this.outerValues.setMinCount(k);
    if (localOuterValues != null)
    {
      final int i3 = k - i2;
      this.outerValues.setCount(localOuterValues.getCount() + i3);
      localOuterValues.addOuterValueListener(new OuterValueListener()
      {
        public void shrinkingOuterValues(OuterValues paramAnonymousOuterValues, int paramAnonymousInt)
        {
          TransformConstructors.this.outerValues.setCount(paramAnonymousInt + i3);
        }
      });
    }
    else
    {
      this.outerValues.setCount(k);
    }
    if (j != 0) {
      this.outerValues.setJikesAnonymousInner(true);
    }
    if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
      GlobalOptions.err.println("  succeeded: " + this.outerValues);
    }
    this.cons[0].setAnonymousConstructor(true);
    localInstructionBlock.removeBlock();
    this.type0Count += 1;
  }
  
  private boolean checkJikesSuper(Expression paramExpression)
  {
    if (((paramExpression instanceof LocalStoreOperator)) || ((paramExpression instanceof IIncOperator))) {
      return false;
    }
    if ((paramExpression instanceof Operator))
    {
      Expression[] arrayOfExpression = ((Operator)paramExpression).getSubExpressions();
      for (int i = 0; i < arrayOfExpression.length; i++) {
        if (!checkJikesSuper(arrayOfExpression[i])) {
          return false;
        }
      }
    }
    return true;
  }
  
  private Expression renameJikesSuper(Expression paramExpression, MethodAnalyzer paramMethodAnalyzer, int paramInt1, int paramInt2)
  {
    Object localObject1;
    int i;
    Object localObject2;
    if ((paramExpression instanceof LocalLoadOperator))
    {
      localObject1 = (LocalLoadOperator)paramExpression;
      i = ((LocalLoadOperator)localObject1).getLocalInfo().getSlot();
      if ((i >= paramInt1) && (i < paramInt2)) {
        return this.outerValues.getValueBySlot(i);
      }
      localObject2 = paramMethodAnalyzer.getType().getParameterTypes();
      if (i >= paramInt2) {
        i -= paramInt2 - paramInt1;
      }
      for (int j = 0; (i > 1) && (j < localObject2.length); j++) {
        i -= localObject2[j].stackSize();
      }
      ((LocalLoadOperator)localObject1).setLocalInfo(paramMethodAnalyzer.getParamInfo(1 + j));
      ((LocalLoadOperator)localObject1).setMethodAnalyzer(paramMethodAnalyzer);
      return (Expression)localObject1;
    }
    if ((paramExpression instanceof Operator))
    {
      localObject1 = ((Operator)paramExpression).getSubExpressions();
      for (i = 0; i < localObject1.length; i++)
      {
        localObject2 = renameJikesSuper(localObject1[i], paramMethodAnalyzer, paramInt1, paramInt2);
        if (localObject2 != localObject1[i]) {
          ((Operator)paramExpression).setSubExpressions(i, (Expression)localObject2);
        }
      }
    }
    return paramExpression;
  }
  
  public void checkJikesContinuation()
  {
    if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
      System.err.println("checkJikesContinuation: " + this.outerValues);
    }
    label809:
    for (int i = 0; i < this.cons.length; i++)
    {
      if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
        GlobalOptions.err.println("constr " + i + " type" + (i < this.type01Count ? 1 : i < this.type0Count ? 0 : 2) + " : " + this.cons[i].getMethodHeader());
      }
      MethodAnalyzer localMethodAnalyzer = this.cons[i];
      MethodType localMethodType1 = localMethodAnalyzer.getType();
      StructuredBlock localStructuredBlock = localMethodAnalyzer.getMethodHeader().block;
      Object localObject1 = null;
      InstructionBlock localInstructionBlock = null;
      Expression localExpression1;
      InvokeOperator localInvokeOperator;
      Object localObject2;
      if (i >= this.type0Count)
      {
        if (((localStructuredBlock instanceof SequentialBlock)) && ((localStructuredBlock.getSubBlocks()[1] instanceof InstructionBlock)))
        {
          localInstructionBlock = (InstructionBlock)localStructuredBlock.getSubBlocks()[0];
          localStructuredBlock = localStructuredBlock.getSubBlocks()[1];
          localExpression1 = localInstructionBlock.getInstruction().simplify();
          localInvokeOperator = (InvokeOperator)localExpression1;
          localInstructionBlock.setInstruction(localInvokeOperator);
          localObject2 = localInvokeOperator.getSubExpressions();
          for (int j = 1; j < localObject2.length; j++) {
            if (!checkJikesSuper(localObject2[j])) {
              break label809;
            }
          }
        }
      }
      else if ((localStructuredBlock instanceof InstructionBlock))
      {
        localExpression1 = ((InstructionBlock)localStructuredBlock).getInstruction().simplify();
        if ((localExpression1 instanceof InvokeOperator))
        {
          localInvokeOperator = (InvokeOperator)localExpression1;
          if ((localInvokeOperator.isThis()) && (localInvokeOperator.getFreeOperandCount() == 0))
          {
            localObject2 = localInvokeOperator.getMethodAnalyzer();
            if (localObject2 != null)
            {
              MethodType localMethodType2 = ((MethodAnalyzer)localObject2).getType();
              Expression[] arrayOfExpression = localInvokeOperator.getSubExpressions();
              if ((((MethodAnalyzer)localObject2).getName().startsWith("constructor$")) && (localMethodType2.getReturnType() == Type.tVoid) && (isThis(arrayOfExpression[0], this.clazzAnalyzer.getClazz())))
              {
                for (int k = 1; k < arrayOfExpression.length; k++) {
                  if (!(arrayOfExpression[k] instanceof LocalLoadOperator)) {
                    break label809;
                  }
                }
                Type[] arrayOfType = localMethodAnalyzer.getType().getParameterTypes();
                int m = arrayOfType.length;
                if (this.outerValues.isJikesAnonymousInner()) {
                  m--;
                }
                int n = m - arrayOfExpression.length + 2;
                int i1 = n - 1;
                int i2 = 1;
                int i3 = 1;
                Expression localExpression2 = null;
                int i4;
                if ((n > 0) && (arrayOfExpression.length > 1) && (this.outerValues.getCount() > 0))
                {
                  if (((LocalLoadOperator)arrayOfExpression[i3]).getLocalInfo().getSlot() == 1)
                  {
                    i1 = n;
                    localExpression2 = this.outerValues.getValue(0);
                    i3++;
                  }
                  else
                  {
                    n--;
                  }
                  for (i4 = 0; i4 < n; i4++) {
                    i2 += arrayOfType[i4].stackSize();
                  }
                }
                if (i1 <= this.outerValues.getCount())
                {
                  i4 = i2;
                  int i5 = i3;
                  int i6 = i4 - i5;
                  for (int i7 = i3; i7 < arrayOfExpression.length; i7++)
                  {
                    if (((LocalLoadOperator)arrayOfExpression[i7]).getLocalInfo().getSlot() != i2) {
                      break label809;
                    }
                    i2 += arrayOfExpression[i7].getType().stackSize();
                  }
                  this.outerValues.setMinCount(i1);
                  this.outerValues.setCount(n);
                  if (localInstructionBlock != null)
                  {
                    Expression localExpression3 = renameJikesSuper(localInstructionBlock.getInstruction(), (MethodAnalyzer)localObject2, i5, i4);
                    localInstructionBlock.removeBlock();
                    ((MethodAnalyzer)localObject2).insertStructuredBlock(localInstructionBlock);
                  }
                  if (localExpression2 != null)
                  {
                    ((MethodAnalyzer)localObject2).getParamInfo(1).setExpression(localExpression2);
                    ((MethodAnalyzer)localObject2).getMethodHeader().simplify();
                  }
                  if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
                    GlobalOptions.err.println("  succeeded");
                  }
                  localMethodAnalyzer.setJikesConstructor(localMethodAnalyzer);
                  ((MethodAnalyzer)localObject2).setJikesConstructor(localMethodAnalyzer);
                  ((MethodAnalyzer)localObject2).setHasOuterValue(i5 == 2);
                  if (localMethodAnalyzer.isAnonymousConstructor()) {
                    ((MethodAnalyzer)localObject2).setAnonymousConstructor(true);
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  private Expression transformFieldInitializer(int paramInt, Expression paramExpression)
  {
    Object localObject2;
    if ((paramExpression instanceof LocalVarOperator))
    {
      if (!(paramExpression instanceof LocalLoadOperator))
      {
        if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
          GlobalOptions.err.println("illegal local op: " + paramExpression);
        }
        return null;
      }
      if ((this.outerValues != null) && ((Options.options & 0x200) != 0))
      {
        int i = ((LocalLoadOperator)paramExpression).getLocalInfo().getSlot();
        localObject2 = this.outerValues.getValueBySlot(i);
        if (localObject2 != null) {
          return (Expression)localObject2;
        }
      }
      if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
        GlobalOptions.err.println("not outerValue: " + paramExpression + " " + this.outerValues);
      }
      return null;
    }
    Object localObject1;
    if ((paramExpression instanceof FieldOperator))
    {
      if ((paramExpression instanceof PutFieldOperator)) {
        return null;
      }
      localObject1 = (FieldOperator)paramExpression;
      if ((((FieldOperator)localObject1).getClassInfo() == this.clazzAnalyzer.getClazz()) && (this.clazzAnalyzer.getFieldIndex(((FieldOperator)localObject1).getFieldName(), ((FieldOperator)localObject1).getFieldType()) >= paramInt)) {
        return null;
      }
    }
    Object localObject3;
    if ((paramExpression instanceof InvokeOperator))
    {
      localObject1 = ((InvokeOperator)paramExpression).getMethodInfo();
      localObject2 = localObject1 == null ? null : ((MethodInfo)localObject1).getExceptions();
      if (localObject2 != null)
      {
        ClassInfo localClassInfo1 = ClassInfo.forName("java.lang.RuntimeException");
        localObject3 = ClassInfo.forName("java.lang.Error");
        for (int k = 0; k < localObject2.length; k++)
        {
          ClassInfo localClassInfo2 = ClassInfo.forName(localObject2[k]);
          if ((!localClassInfo1.superClassOf(localClassInfo2)) && (!((ClassInfo)localObject3).superClassOf(localClassInfo2))) {
            return null;
          }
        }
      }
    }
    if ((paramExpression instanceof Operator))
    {
      localObject1 = (Operator)paramExpression;
      localObject2 = ((Operator)localObject1).getSubExpressions();
      for (int j = 0; j < localObject2.length; j++)
      {
        localObject3 = transformFieldInitializer(paramInt, localObject2[j]);
        if (localObject3 == null) {
          return null;
        }
        if (localObject3 != localObject2[j]) {
          ((Operator)localObject1).setSubExpressions(j, (Expression)localObject3);
        }
      }
    }
    return paramExpression;
  }
  
  public void removeSynthInitializers()
  {
    if (((Options.options & 0x200) == 0) || (this.isStatic) || (this.type01Count == 0)) {
      return;
    }
    checkAnonymousConstructor();
    if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
      GlobalOptions.err.println("removeSynthInitializers of " + this.clazzAnalyzer.getClazz());
    }
    StructuredBlock[] arrayOfStructuredBlock = new StructuredBlock[this.type01Count];
    for (int i = 0; i < this.type01Count; i++)
    {
      arrayOfStructuredBlock[i] = this.cons[i].getMethodHeader().block;
      if (i >= this.type0Count) {
        if ((arrayOfStructuredBlock[i] instanceof SequentialBlock)) {
          arrayOfStructuredBlock[i] = arrayOfStructuredBlock[i].getSubBlocks()[1];
        } else {
          return;
        }
      }
    }
    for (;;)
    {
      StructuredBlock localStructuredBlock1 = (arrayOfStructuredBlock[0] instanceof SequentialBlock) ? arrayOfStructuredBlock[0].getSubBlocks()[0] : arrayOfStructuredBlock[0];
      if (!(localStructuredBlock1 instanceof InstructionBlock)) {
        break;
      }
      Expression localExpression1 = ((InstructionBlock)localStructuredBlock1).getInstruction().simplify();
      if ((!(localExpression1 instanceof StoreInstruction)) || (localExpression1.getFreeOperandCount() != 0)) {
        break;
      }
      StoreInstruction localStoreInstruction = (StoreInstruction)localExpression1;
      if (!(localStoreInstruction.getLValue() instanceof PutFieldOperator)) {
        break;
      }
      PutFieldOperator localPutFieldOperator = (PutFieldOperator)localStoreInstruction.getLValue();
      if ((localPutFieldOperator.isStatic() != this.isStatic) || (localPutFieldOperator.getClassInfo() != this.clazzAnalyzer.getClazz()) || (!isThis(localPutFieldOperator.getSubExpressions()[0], this.clazzAnalyzer.getClazz()))) {
        break;
      }
      int j = this.clazzAnalyzer.getFieldIndex(localPutFieldOperator.getFieldName(), localPutFieldOperator.getFieldType());
      if (j < 0) {
        break;
      }
      FieldAnalyzer localFieldAnalyzer = this.clazzAnalyzer.getField(j);
      if (!localFieldAnalyzer.isSynthetic()) {
        break;
      }
      Expression localExpression2 = localStoreInstruction.getSubExpressions()[1];
      localExpression2 = transformFieldInitializer(j, localExpression2);
      if (localExpression2 == null) {
        break;
      }
      for (int k = 1; k < this.type01Count; k++)
      {
        localStructuredBlock1 = (arrayOfStructuredBlock[k] instanceof SequentialBlock) ? arrayOfStructuredBlock[k].getSubBlocks()[0] : arrayOfStructuredBlock[k];
        if ((!(localStructuredBlock1 instanceof InstructionBlock)) || (!((InstructionBlock)localStructuredBlock1).getInstruction().simplify().equals(localExpression1)))
        {
          if ((GlobalOptions.debuggingFlags & 0x200) == 0) {
            return;
          }
          GlobalOptions.err.println("  constr 0 and " + k + " differ: " + localExpression1 + "<-/->" + localStructuredBlock1);
          return;
        }
      }
      if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
        GlobalOptions.err.println("  field " + localPutFieldOperator.getFieldName() + " = " + localExpression2);
      }
      if (!localFieldAnalyzer.setInitializer(localExpression2))
      {
        if ((GlobalOptions.debuggingFlags & 0x200) == 0) {
          break;
        }
        GlobalOptions.err.println("    setField failed");
        break;
      }
      k = 0;
      for (int m = 0; m < this.type01Count; m++) {
        if ((arrayOfStructuredBlock[m] instanceof SequentialBlock))
        {
          StructuredBlock localStructuredBlock2 = arrayOfStructuredBlock[m].getSubBlocks()[1];
          localStructuredBlock2.replace(arrayOfStructuredBlock[m]);
          arrayOfStructuredBlock[m] = localStructuredBlock2;
        }
        else
        {
          arrayOfStructuredBlock[m].removeBlock();
          arrayOfStructuredBlock[m] = null;
          k = 1;
        }
      }
      if (k != 0)
      {
        if ((GlobalOptions.debuggingFlags & 0x200) == 0) {
          break;
        }
        GlobalOptions.err.println("one constr is over");
        break;
      }
    }
  }
  
  public int transformOneField(int paramInt, StructuredBlock paramStructuredBlock)
  {
    if (!(paramStructuredBlock instanceof InstructionBlock)) {
      return -1;
    }
    Expression localExpression1 = ((InstructionBlock)paramStructuredBlock).getInstruction().simplify();
    if ((!(localExpression1 instanceof StoreInstruction)) || (localExpression1.getFreeOperandCount() != 0)) {
      return -1;
    }
    StoreInstruction localStoreInstruction = (StoreInstruction)localExpression1;
    if (!(localStoreInstruction.getLValue() instanceof PutFieldOperator)) {
      return -1;
    }
    PutFieldOperator localPutFieldOperator = (PutFieldOperator)localStoreInstruction.getLValue();
    if ((localPutFieldOperator.isStatic() != this.isStatic) || (localPutFieldOperator.getClassInfo() != this.clazzAnalyzer.getClazz())) {
      return -1;
    }
    if ((!this.isStatic) && (!isThis(localPutFieldOperator.getSubExpressions()[0], this.clazzAnalyzer.getClazz())))
    {
      if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
        GlobalOptions.err.println("  not this: " + localExpression1);
      }
      return -1;
    }
    int i = this.clazzAnalyzer.getFieldIndex(localPutFieldOperator.getFieldName(), localPutFieldOperator.getFieldType());
    if (i <= paramInt) {
      return -1;
    }
    Expression localExpression2 = localStoreInstruction.getSubExpressions()[1];
    localExpression2 = transformFieldInitializer(i, localExpression2);
    if (localExpression2 == null) {
      return -1;
    }
    if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
      GlobalOptions.err.println("  field " + localPutFieldOperator.getFieldName() + " = " + localExpression2);
    }
    if ((i <= paramInt) || (!this.clazzAnalyzer.getField(i).setInitializer(localExpression2)))
    {
      if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
        GlobalOptions.err.println("set field failed");
      }
      return -1;
    }
    return i;
  }
  
  public void transformBlockInitializer(StructuredBlock paramStructuredBlock)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    int i = -1;
    while ((paramStructuredBlock instanceof SequentialBlock))
    {
      StructuredBlock localStructuredBlock = paramStructuredBlock.getSubBlocks()[0];
      int j = transformOneField(i, localStructuredBlock);
      if (j < 0) {
        this.clazzAnalyzer.addBlockInitializer(i + 1, localStructuredBlock);
      } else {
        i = j;
      }
      paramStructuredBlock = paramStructuredBlock.getSubBlocks()[1];
    }
    if (transformOneField(i, paramStructuredBlock) < 0) {
      this.clazzAnalyzer.addBlockInitializer(i + 1, paramStructuredBlock);
    }
  }
  
  public boolean checkBlockInitializer(InvokeOperator paramInvokeOperator)
  {
    if ((!paramInvokeOperator.isThis()) || (paramInvokeOperator.getFreeOperandCount() != 0)) {
      return false;
    }
    MethodAnalyzer localMethodAnalyzer = paramInvokeOperator.getMethodAnalyzer();
    if (localMethodAnalyzer == null) {
      return false;
    }
    FlowBlock localFlowBlock = localMethodAnalyzer.getMethodHeader();
    MethodType localMethodType = localMethodAnalyzer.getType();
    if ((!localMethodAnalyzer.getName().startsWith("block$")) || (localMethodType.getParameterTypes().length != 0) || (localMethodType.getReturnType() != Type.tVoid)) {
      return false;
    }
    if ((localFlowBlock == null) || (!localFlowBlock.hasNoJumps())) {
      return false;
    }
    if (!isThis(paramInvokeOperator.getSubExpressions()[0], this.clazzAnalyzer.getClazz())) {
      return false;
    }
    localMethodAnalyzer.setJikesBlockInitializer(true);
    transformBlockInitializer(localFlowBlock.block);
    return true;
  }
  
  private void removeDefaultSuper()
  {
    if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
      GlobalOptions.err.println("removeDefaultSuper of " + this.clazzAnalyzer.getClazz());
    }
    for (int i = this.type0Count; i < this.type01Count; i++)
    {
      MethodAnalyzer localMethodAnalyzer = this.cons[i];
      FlowBlock localFlowBlock = this.cons[i].getMethodHeader();
      StructuredBlock localStructuredBlock = localFlowBlock.block;
      if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
        GlobalOptions.err.println("constr " + i + ": " + localStructuredBlock);
      }
      InstructionBlock localInstructionBlock;
      if ((localStructuredBlock instanceof InstructionBlock)) {
        localInstructionBlock = (InstructionBlock)localStructuredBlock;
      } else {
        localInstructionBlock = (InstructionBlock)localStructuredBlock.getSubBlocks()[0];
      }
      InvokeOperator localInvokeOperator = (InvokeOperator)localInstructionBlock.getInstruction().simplify();
      ClassInfo localClassInfo = localInvokeOperator.getClassInfo();
      InnerClassInfo[] arrayOfInnerClassInfo = localClassInfo.getOuterClasses();
      int j = localInvokeOperator.getSubExpressions().length - 1;
      if (((Options.options & 0x2) != 0) && (arrayOfInnerClassInfo != null) && (arrayOfInnerClassInfo[0].outer != null) && (arrayOfInnerClassInfo[0].name != null) && (!Modifier.isStatic(arrayOfInnerClassInfo[0].modifiers)))
      {
        if (j != 1) {
          continue;
        }
        if (!(localInvokeOperator.getSubExpressions()[1] instanceof ThisOperator)) {
          continue;
        }
      }
      else
      {
        ClassAnalyzer localClassAnalyzer = localInvokeOperator.getClassAnalyzer();
        OuterValues localOuterValues = null;
        if (localClassAnalyzer != null) {
          localOuterValues = localClassAnalyzer.getOuterValues();
        }
        if ((j > 0) && ((localOuterValues == null) || (j > localOuterValues.getCount()))) {
          continue;
        }
      }
      localInstructionBlock.removeBlock();
      if (i > this.type0Count)
      {
        this.cons[i] = this.cons[this.type0Count];
        this.cons[this.type0Count] = localMethodAnalyzer;
      }
      this.type0Count += 1;
    }
  }
  
  private void removeInitializers()
  {
    if (this.type01Count == 0) {
      return;
    }
    if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
      GlobalOptions.err.println("removeInitializers");
    }
    StructuredBlock[] arrayOfStructuredBlock = new StructuredBlock[this.type01Count];
    Object localObject;
    for (int i = 0; i < this.type01Count; i++)
    {
      localObject = this.cons[i].getMethodHeader();
      arrayOfStructuredBlock[i] = ((FlowBlock)localObject).block;
      if (i >= this.type0Count) {
        if ((arrayOfStructuredBlock[i] instanceof SequentialBlock))
        {
          arrayOfStructuredBlock[i] = arrayOfStructuredBlock[i].getSubBlocks()[1];
        }
        else
        {
          arrayOfStructuredBlock[i] = null;
          return;
        }
      }
    }
    i = -1;
    for (;;)
    {
      localObject = (arrayOfStructuredBlock[0] instanceof SequentialBlock) ? arrayOfStructuredBlock[0].getSubBlocks()[0] : arrayOfStructuredBlock[0];
      if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
        GlobalOptions.err.println("Instruction: " + localObject);
      }
      if (!(localObject instanceof InstructionBlock)) {
        break;
      }
      Expression localExpression = ((InstructionBlock)localObject).getInstruction().simplify();
      for (int j = 1; j < this.type01Count; j++)
      {
        localObject = (arrayOfStructuredBlock[j] instanceof SequentialBlock) ? arrayOfStructuredBlock[j].getSubBlocks()[0] : arrayOfStructuredBlock[j];
        if ((!(localObject instanceof InstructionBlock)) || (!((InstructionBlock)localObject).getInstruction().simplify().equals(localExpression)))
        {
          if ((GlobalOptions.debuggingFlags & 0x200) == 0) {
            return;
          }
          GlobalOptions.err.println("constr " + j + " differs: " + localObject);
          return;
        }
      }
      if (((localExpression instanceof InvokeOperator)) && (checkBlockInitializer((InvokeOperator)localExpression)))
      {
        for (j = 0; j < this.type01Count; j++) {
          if ((arrayOfStructuredBlock[j] instanceof SequentialBlock))
          {
            StructuredBlock localStructuredBlock1 = arrayOfStructuredBlock[j].getSubBlocks()[1];
            localStructuredBlock1.replace(arrayOfStructuredBlock[j]);
            arrayOfStructuredBlock[j] = localStructuredBlock1;
          }
          else
          {
            arrayOfStructuredBlock[j].removeBlock();
            arrayOfStructuredBlock[j] = null;
          }
        }
        break;
      }
      j = transformOneField(i, (StructuredBlock)localObject);
      if (j < 0) {
        break;
      }
      i = j;
      int k = 0;
      for (int m = 0; m < this.type01Count; m++) {
        if ((arrayOfStructuredBlock[m] instanceof SequentialBlock))
        {
          StructuredBlock localStructuredBlock2 = arrayOfStructuredBlock[m].getSubBlocks()[1];
          localStructuredBlock2.replace(arrayOfStructuredBlock[m]);
          arrayOfStructuredBlock[m] = localStructuredBlock2;
        }
        else
        {
          arrayOfStructuredBlock[m].removeBlock();
          arrayOfStructuredBlock[m] = null;
          k = 1;
        }
      }
      if (k != 0)
      {
        if ((GlobalOptions.debuggingFlags & 0x200) == 0) {
          break;
        }
        GlobalOptions.err.println("one constr is over");
        break;
      }
    }
  }
  
  public void transform()
  {
    if (((Options.options & 0x200) == 0) || (this.cons.length == 0)) {
      return;
    }
    removeDefaultSuper();
    removeInitializers();
    checkJikesContinuation();
    if (this.outerValues != null) {
      for (int i = 0; i < this.cons.length; i++)
      {
        for (int j = 0; j < this.outerValues.getCount(); j++) {
          this.cons[i].getParamInfo(j + 1).setExpression(this.outerValues.getValue(j));
        }
        this.cons[i].getMethodHeader().simplify();
      }
    }
  }
}


