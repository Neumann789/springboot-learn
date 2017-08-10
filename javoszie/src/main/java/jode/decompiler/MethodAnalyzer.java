package jode.decompiler;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import jode.GlobalOptions;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.ClassInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.LocalVariableInfo;
import jode.bytecode.MethodInfo;
import jode.expr.CheckNullOperator;
import jode.expr.Expression;
import jode.expr.InvokeOperator;
import jode.expr.LocalLoadOperator;
import jode.expr.OuterLocalOperator;
import jode.expr.ThisOperator;
import jode.flow.EmptyBlock;
import jode.flow.FlowBlock;
import jode.flow.Jump;
import jode.flow.StructuredBlock;
import jode.flow.TransformExceptionHandlers;
import jode.jvm.SyntheticAnalyzer;
import jode.type.MethodType;
import jode.type.Type;

public class MethodAnalyzer
  implements Scope, ClassDeclarer
{
  private static double STEP_COMPLEXITY = 0.01D;
  private static int STRICTFP = 2048;
  ImportHandler imports;
  ClassAnalyzer classAnalyzer;
  MethodInfo minfo;
  BytecodeInfo code;
  String methodName;
  MethodType methodType;
  boolean isConstructor;
  Type[] exceptions;
  SyntheticAnalyzer synth;
  FlowBlock methodHeader;
  Vector allLocals = new Vector();
  LocalInfo[] param;
  LocalVariableTable lvt;
  MethodAnalyzer jikesConstructor;
  boolean hasJikesOuterValue;
  boolean isAnonymousConstructor;
  boolean isJikesBlockInitializer;
  Vector anonConstructors = new Vector();
  Vector innerAnalyzers;
  Collection usedAnalyzers;
  
  public MethodAnalyzer(ClassAnalyzer paramClassAnalyzer, MethodInfo paramMethodInfo, ImportHandler paramImportHandler)
  {
    this.classAnalyzer = paramClassAnalyzer;
    this.imports = paramImportHandler;
    this.minfo = paramMethodInfo;
    this.methodName = paramMethodInfo.getName();
    this.methodType = Type.tMethod(paramMethodInfo.getType());
    this.isConstructor = ((this.methodName.equals("<init>")) || (this.methodName.equals("<clinit>")));
    if (paramMethodInfo.getBytecode() != null) {
      this.code = paramMethodInfo.getBytecode();
    }
    String[] arrayOfString = paramMethodInfo.getExceptions();
    if (arrayOfString == null)
    {
      this.exceptions = new Type[0];
    }
    else
    {
      int i = arrayOfString.length;
      this.exceptions = new Type[i];
      for (int j = 0; j < i; j++) {
        this.exceptions[j] = Type.tClass(arrayOfString[j]);
      }
    }
    if ((paramMethodInfo.isSynthetic()) || (this.methodName.indexOf('$') != -1)) {
      this.synth = new SyntheticAnalyzer(paramMethodInfo, true);
    }
  }
  
  public String getName()
  {
    return this.methodName;
  }
  
  public MethodType getType()
  {
    return this.methodType;
  }
  
  public FlowBlock getMethodHeader()
  {
    return this.methodHeader;
  }
  
  public final BytecodeInfo getBytecodeInfo()
  {
    return this.code;
  }
  
  public final ImportHandler getImportHandler()
  {
    return this.imports;
  }
  
  public final void useType(Type paramType)
  {
    this.imports.useType(paramType);
  }
  
  public void insertStructuredBlock(StructuredBlock paramStructuredBlock)
  {
    if (this.methodHeader != null)
    {
      paramStructuredBlock.setJump(new Jump(FlowBlock.NEXT_BY_ADDR));
      FlowBlock localFlowBlock = new FlowBlock(this, 0);
      localFlowBlock.appendBlock(paramStructuredBlock, 0);
      localFlowBlock.setNextByAddr(this.methodHeader);
      localFlowBlock.doT2(this.methodHeader);
      this.methodHeader = localFlowBlock;
    }
    else
    {
      throw new IllegalStateException();
    }
  }
  
  public final boolean isConstructor()
  {
    return this.isConstructor;
  }
  
  public final boolean isStatic()
  {
    return this.minfo.isStatic();
  }
  
  public final boolean isSynthetic()
  {
    return this.minfo.isSynthetic();
  }
  
  public final boolean isStrictFP()
  {
    return (this.minfo.getModifiers() & STRICTFP) != 0;
  }
  
  public final void setJikesConstructor(MethodAnalyzer paramMethodAnalyzer)
  {
    this.jikesConstructor = paramMethodAnalyzer;
  }
  
  public final void setJikesBlockInitializer(boolean paramBoolean)
  {
    this.isJikesBlockInitializer = paramBoolean;
  }
  
  public final void setHasOuterValue(boolean paramBoolean)
  {
    this.hasJikesOuterValue = paramBoolean;
  }
  
  public final void setAnonymousConstructor(boolean paramBoolean)
  {
    this.isAnonymousConstructor = paramBoolean;
  }
  
  public final boolean isAnonymousConstructor()
  {
    return this.isAnonymousConstructor;
  }
  
  public final SyntheticAnalyzer getSynthetic()
  {
    return this.synth;
  }
  
  public Type getReturnType()
  {
    return this.methodType.getReturnType();
  }
  
  public ClassAnalyzer getClassAnalyzer()
  {
    return this.classAnalyzer;
  }
  
  public ClassInfo getClazz()
  {
    return this.classAnalyzer.clazz;
  }
  
  public final LocalInfo getParamInfo(int paramInt)
  {
    return this.param[paramInt];
  }
  
  public final int getParamCount()
  {
    return this.param.length;
  }
  
  public LocalInfo getLocalInfo(int paramInt1, int paramInt2)
  {
    LocalInfo localLocalInfo = new LocalInfo(this, paramInt2);
    if (this.lvt != null)
    {
      LocalVarEntry localLocalVarEntry = this.lvt.getLocal(paramInt2, paramInt1);
      if (localLocalVarEntry != null) {
        localLocalInfo.addHint(localLocalVarEntry.getName(), localLocalVarEntry.getType());
      }
    }
    this.allLocals.addElement(localLocalInfo);
    return localLocalInfo;
  }
  
  public double getComplexity()
  {
    if (this.code == null) {
      return 0.0D;
    }
    return this.code.getInstructions().size();
  }
  
  private void analyzeCode(ProgressListener paramProgressListener, double paramDouble1, double paramDouble2)
  {
    int i = Integer.MAX_VALUE;
    if (GlobalOptions.verboseLevel > 0) {
      GlobalOptions.err.print(this.methodName + ": ");
    }
    if (paramProgressListener != null) {
      i = (int)(this.code.getInstructions().size() * STEP_COMPLEXITY / (paramDouble2 * 0.9D));
    }
    DeadCodeAnalysis.removeDeadCode(this.code);
    Handler[] arrayOfHandler = this.code.getExceptionHandlers();
    Iterator localIterator1 = this.code.getInstructions().iterator();
    Instruction localInstruction1;
    while (localIterator1.hasNext())
    {
      localInstruction1 = (Instruction)localIterator1.next();
      if ((localInstruction1.getPrevByAddr() == null) || (localInstruction1.getPrevByAddr().doesAlwaysJump()) || (localInstruction1.getPreds() != null)) {
        localInstruction1.setTmpInfo(new FlowBlock(this, localInstruction1.getAddr()));
      }
    }
    for (int j = 0; j < arrayOfHandler.length; j++)
    {
      localInstruction1 = arrayOfHandler[j].start;
      if (localInstruction1.getTmpInfo() == null) {
        localInstruction1.setTmpInfo(new FlowBlock(this, localInstruction1.getAddr()));
      }
      localInstruction1 = arrayOfHandler[j].end.getNextByAddr();
      if (localInstruction1.getTmpInfo() == null) {
        localInstruction1.setTmpInfo(new FlowBlock(this, localInstruction1.getAddr()));
      }
      localInstruction1 = arrayOfHandler[j].catcher;
      if (localInstruction1.getTmpInfo() == null) {
        localInstruction1.setTmpInfo(new FlowBlock(this, localInstruction1.getAddr()));
      }
    }
    j = 1000;
    int k = 0;
    Object localObject1 = null;
    int m = 0;
    Iterator localIterator3 = this.code.getInstructions().iterator();
    Object localObject2;
    Object localObject3;
    while (localIterator3.hasNext())
    {
      localObject2 = (Instruction)localIterator3.next();
      localObject3 = Opcodes.readOpcode((Instruction)localObject2, this);
      if ((GlobalOptions.verboseLevel > 0) && (((Instruction)localObject2).getAddr() > j))
      {
        GlobalOptions.err.print('.');
        j += 1000;
      }
      k++;
      if (k >= i)
      {
        paramDouble1 += k * paramDouble2 / this.code.getInstructions().size();
        paramProgressListener.updateProgress(paramDouble1, this.methodName);
        k = 0;
      }
      if ((m != 0) && (((Instruction)localObject2).getTmpInfo() == null) && (!((Instruction)localObject2).doesAlwaysJump()) && (((Instruction)localObject2).getSuccs() == null))
      {
        ((FlowBlock)localObject1).appendBlock((StructuredBlock)localObject3, ((Instruction)localObject2).getLength());
      }
      else
      {
        if (((Instruction)localObject2).getTmpInfo() == null) {
          ((Instruction)localObject2).setTmpInfo(new FlowBlock(this, ((Instruction)localObject2).getAddr()));
        }
        FlowBlock localFlowBlock1 = (FlowBlock)((Instruction)localObject2).getTmpInfo();
        localFlowBlock1.appendBlock((StructuredBlock)localObject3, ((Instruction)localObject2).getLength());
        if (localObject1 != null) {
          ((FlowBlock)localObject1).setNextByAddr(localFlowBlock1);
        }
        ((Instruction)localObject2).setTmpInfo(localObject1 = localFlowBlock1);
        m = (!((Instruction)localObject2).doesAlwaysJump()) && (((Instruction)localObject2).getSuccs() == null) ? 1 : 0;
      }
    }
    this.methodHeader = ((FlowBlock)((Instruction)this.code.getInstructions().get(0)).getTmpInfo());
    TransformExceptionHandlers localTransformExceptionHandlers = new TransformExceptionHandlers();
    for (int n = 0; n < arrayOfHandler.length; n++)
    {
      localObject2 = null;
      localObject3 = (FlowBlock)arrayOfHandler[n].start.getTmpInfo();
      int i1 = arrayOfHandler[n].end.getNextByAddr().getAddr();
      FlowBlock localFlowBlock2 = (FlowBlock)arrayOfHandler[n].catcher.getTmpInfo();
      if (arrayOfHandler[n].type != null) {
        localObject2 = Type.tClass(arrayOfHandler[n].type);
      }
      localTransformExceptionHandlers.addHandler((FlowBlock)localObject3, i1, localFlowBlock2, (Type)localObject2);
    }
    Iterator localIterator2 = this.code.getInstructions().iterator();
    while (localIterator2.hasNext())
    {
      Instruction localInstruction2 = (Instruction)localIterator2.next();
      localInstruction2.setTmpInfo(null);
    }
    if (GlobalOptions.verboseLevel > 0) {
      GlobalOptions.err.print('-');
    }
    localTransformExceptionHandlers.analyze();
    this.methodHeader.analyze();
    if (((Options.options & 0x8) == 0) && (this.methodHeader.mapStackToLocal())) {
      this.methodHeader.removePush();
    }
    if ((Options.options & 0x40) != 0) {
      this.methodHeader.removeOnetimeLocals();
    }
    this.methodHeader.mergeParams(this.param);
    if (GlobalOptions.verboseLevel > 0) {
      GlobalOptions.err.println("");
    }
    if (paramProgressListener != null)
    {
      paramDouble1 += 0.1D * paramDouble2;
      paramProgressListener.updateProgress(paramDouble1, this.methodName);
    }
  }
  
  public void analyze(ProgressListener paramProgressListener, double paramDouble1, double paramDouble2)
    throws ClassFormatError
  {
    if (paramProgressListener != null) {
      paramProgressListener.updateProgress(paramDouble1, this.methodName);
    }
    if ((this.code != null) && ((Options.options & 0x1) != 0))
    {
      localObject = this.code.getLocalVariableTable();
      if (localObject != null) {
        this.lvt = new LocalVariableTable(this.code.getMaxLocals(), (LocalVariableInfo[])localObject);
      }
    }
    Object localObject = getType().getParameterTypes();
    int i = (isStatic() ? 0 : 1) + localObject.length;
    this.param = new LocalInfo[i];
    int j = 0;
    int k = 0;
    if (!isStatic())
    {
      ClassInfo localClassInfo = this.classAnalyzer.getClazz();
      LocalInfo localLocalInfo = getLocalInfo(0, k++);
      localLocalInfo.setExpression(new ThisOperator(localClassInfo, true));
      this.param[(j++)] = localLocalInfo;
    }
    for (int m = 0; m < localObject.length; m++)
    {
      this.param[j] = getLocalInfo(0, k);
      this.param[j].setType(localObject[m]);
      k += localObject[m].stackSize();
      j++;
    }
    for (m = 0; m < this.exceptions.length; m++) {
      this.imports.useType(this.exceptions[m]);
    }
    if (!this.isConstructor) {
      this.imports.useType(this.methodType.getReturnType());
    }
    if (this.code != null) {
      analyzeCode(paramProgressListener, paramDouble1, paramDouble2);
    }
  }
  
  public void analyzeInnerClasses()
    throws ClassFormatError
  {
    int i = 0;
    Enumeration localEnumeration = this.anonConstructors.elements();
    while (localEnumeration.hasMoreElements())
    {
      InvokeOperator localInvokeOperator = (InvokeOperator)localEnumeration.nextElement();
      analyzeInvokeOperator(localInvokeOperator);
    }
  }
  
  public void makeDeclaration(Set paramSet)
  {
    Object localObject1;
    Object localObject2;
    if (this.innerAnalyzers != null)
    {
      localEnumeration = this.innerAnalyzers.elements();
      while (localEnumeration.hasMoreElements())
      {
        localObject1 = (ClassAnalyzer)localEnumeration.nextElement();
        if (((ClassAnalyzer)localObject1).getParent() == this)
        {
          localObject2 = ((ClassAnalyzer)localObject1).getOuterValues();
          for (int j = 0; j < ((OuterValues)localObject2).getCount(); j++)
          {
            Expression localExpression = ((OuterValues)localObject2).getValue(j);
            if ((localExpression instanceof OuterLocalOperator))
            {
              LocalInfo localLocalInfo = ((OuterLocalOperator)localExpression).getLocalInfo();
              if (localLocalInfo.getMethodAnalyzer() == this) {
                localLocalInfo.markFinal();
              }
            }
          }
        }
      }
    }
    Enumeration localEnumeration = this.allLocals.elements();
    while (localEnumeration.hasMoreElements())
    {
      localObject1 = (LocalInfo)localEnumeration.nextElement();
      if (!((LocalInfo)localObject1).isShadow()) {
        this.imports.useType(((LocalInfo)localObject1).getType());
      }
    }
    for (int i = 0; i < this.param.length; i++)
    {
      this.param[i].guessName();
      localObject1 = paramSet.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Declarable)((Iterator)localObject1).next();
        if (this.param[i].getName().equals(((Declarable)localObject2).getName()))
        {
          this.param[i].makeNameUnique();
          break;
        }
      }
      paramSet.add(this.param[i]);
    }
    if (this.code != null)
    {
      this.methodHeader.makeDeclaration(paramSet);
      this.methodHeader.simplify();
    }
    for (i = 0; i < this.param.length; i++) {
      paramSet.remove(this.param[i]);
    }
  }
  
  public boolean skipWriting()
  {
    if (this.synth != null)
    {
      if (this.synth.getKind() == 1) {
        return true;
      }
      if ((this.synth.getKind() >= 2) && (this.synth.getKind() <= 10) && ((Options.options & 0x2) != 0) && ((Options.options & 0x4) != 0)) {
        return true;
      }
    }
    if (this.jikesConstructor == this) {
      return true;
    }
    boolean bool = this.isConstructor;
    int i = 0;
    if ((isConstructor()) && (!isStatic()) && (this.classAnalyzer.outerValues != null)) {
      i = this.classAnalyzer.outerValues.getCount();
    }
    if (this.jikesConstructor != null)
    {
      bool = true;
      i = (this.hasJikesOuterValue) && (this.classAnalyzer.outerValues.getCount() > 0) ? 1 : 0;
    }
    if (this.isJikesBlockInitializer) {
      return true;
    }
    if ((getMethodHeader() == null) || (!(getMethodHeader().getBlock() instanceof EmptyBlock)) || (!getMethodHeader().hasNoJumps()) || (this.exceptions.length > 0)) {
      return false;
    }
    if ((bool) && (((this.minfo.getModifiers() & 0x52F) == (getClassAnalyzer().getModifiers() & 0x5)) || (this.classAnalyzer.getName() == null)) && (this.classAnalyzer.constructors.length == 1) && ((this.methodType.getParameterTypes().length == i) || (this.isAnonymousConstructor))) {
      return true;
    }
    return (isConstructor()) && (isStatic());
  }
  
  public void dumpSource(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    boolean bool = this.isConstructor;
    int i = 0;
    int j = this.minfo.getModifiers();
    if ((isConstructor()) && (!isStatic()) && ((Options.options & 0x200) != 0) && (this.classAnalyzer.outerValues != null)) {
      i = this.classAnalyzer.outerValues.getCount();
    }
    if (this.jikesConstructor != null)
    {
      bool = true;
      i = (this.hasJikesOuterValue) && (this.classAnalyzer.outerValues.getCount() > 0) ? 1 : 0;
      j = this.jikesConstructor.minfo.getModifiers();
    }
    if (this.minfo.isDeprecated())
    {
      paramTabbedPrintWriter.println("/**");
      paramTabbedPrintWriter.println(" * @deprecated");
      paramTabbedPrintWriter.println(" */");
    }
    paramTabbedPrintWriter.pushScope(this);
    if (this.classAnalyzer.getClazz().isInterface()) {
      j &= 0xFBFF;
    }
    if ((isConstructor()) && (isStatic())) {
      j &= 0xFFFFFFE8;
    }
    j &= (STRICTFP ^ 0xFFFFFFFF);
    paramTabbedPrintWriter.startOp(1, 1);
    String str1 = "";
    if (this.minfo.isSynthetic())
    {
      paramTabbedPrintWriter.print("/*synthetic*/");
      str1 = " ";
    }
    String str2 = Modifier.toString(j);
    if (str2.length() > 0)
    {
      paramTabbedPrintWriter.print(str1 + str2);
      str1 = " ";
    }
    if ((isStrictFP()) && (!this.classAnalyzer.isStrictFP()) && (!isConstructor()) && ((j & 0x100) == 0))
    {
      paramTabbedPrintWriter.print(str1 + "strictfp");
      str1 = " ";
    }
    int k;
    if ((!this.isConstructor) || ((!isStatic()) && ((this.classAnalyzer.getName() != null) || (i != this.methodType.getParameterTypes().length))))
    {
      paramTabbedPrintWriter.print(str1);
      if (bool)
      {
        paramTabbedPrintWriter.print(this.classAnalyzer.getName());
      }
      else
      {
        paramTabbedPrintWriter.printType(getReturnType());
        paramTabbedPrintWriter.print(" " + this.methodName);
      }
      paramTabbedPrintWriter.breakOp();
      if ((Options.outputStyle & 0x40) != 0) {
        paramTabbedPrintWriter.print(" ");
      }
      paramTabbedPrintWriter.print("(");
      paramTabbedPrintWriter.startOp(0, 0);
      k = i + (isStatic() ? 0 : 1);
      for (int m = k; m < this.param.length; m++)
      {
        if (m > k)
        {
          paramTabbedPrintWriter.print(", ");
          paramTabbedPrintWriter.breakOp();
        }
        this.param[m].dumpDeclaration(paramTabbedPrintWriter);
      }
      paramTabbedPrintWriter.endOp();
      paramTabbedPrintWriter.print(")");
    }
    if (this.exceptions.length > 0)
    {
      paramTabbedPrintWriter.breakOp();
      paramTabbedPrintWriter.print(" throws ");
      paramTabbedPrintWriter.startOp(0, 2);
      for (k = 0; k < this.exceptions.length; k++)
      {
        if (k > 0)
        {
          paramTabbedPrintWriter.print(",");
          paramTabbedPrintWriter.breakOp();
          paramTabbedPrintWriter.print(" ");
        }
        paramTabbedPrintWriter.printType(this.exceptions[k]);
      }
      paramTabbedPrintWriter.endOp();
    }
    paramTabbedPrintWriter.endOp();
    if (this.code != null)
    {
      paramTabbedPrintWriter.openBraceNoIndent();
      paramTabbedPrintWriter.tab();
      this.methodHeader.dumpSource(paramTabbedPrintWriter);
      paramTabbedPrintWriter.untab();
      paramTabbedPrintWriter.closeBraceNoIndent();
    }
    else
    {
      paramTabbedPrintWriter.println(";");
    }
    paramTabbedPrintWriter.popScope();
  }
  
  public LocalInfo findLocal(String paramString)
  {
    Enumeration localEnumeration = this.allLocals.elements();
    while (localEnumeration.hasMoreElements())
    {
      LocalInfo localLocalInfo = (LocalInfo)localEnumeration.nextElement();
      if (localLocalInfo.getName().equals(paramString)) {
        return localLocalInfo;
      }
    }
    return null;
  }
  
  public ClassAnalyzer findAnonClass(String paramString)
  {
    if (this.innerAnalyzers != null)
    {
      Enumeration localEnumeration = this.innerAnalyzers.elements();
      while (localEnumeration.hasMoreElements())
      {
        ClassAnalyzer localClassAnalyzer = (ClassAnalyzer)localEnumeration.nextElement();
        if ((localClassAnalyzer.getParent() == this) && (localClassAnalyzer.getName() != null) && (localClassAnalyzer.getName().equals(paramString))) {
          return localClassAnalyzer;
        }
      }
    }
    return null;
  }
  
  public boolean isScopeOf(Object paramObject, int paramInt)
  {
    if ((paramInt == 2) && ((paramObject instanceof ClassInfo)))
    {
      ClassAnalyzer localClassAnalyzer = getClassAnalyzer((ClassInfo)paramObject);
      if (localClassAnalyzer != null) {
        return localClassAnalyzer.getParent() == this;
      }
    }
    return false;
  }
  
  public boolean conflicts(String paramString, int paramInt)
  {
    if ((paramInt == 4) || (paramInt == 5)) {
      return findLocal(paramString) != null;
    }
    if ((paramInt == 4) || (paramInt == 1)) {
      return findAnonClass(paramString) != null;
    }
    return false;
  }
  
  public ClassDeclarer getParent()
  {
    return getClassAnalyzer();
  }
  
  public void addAnonymousConstructor(InvokeOperator paramInvokeOperator)
  {
    this.anonConstructors.addElement(paramInvokeOperator);
  }
  
  public void analyzeInvokeOperator(InvokeOperator paramInvokeOperator)
  {
    ClassInfo localClassInfo = paramInvokeOperator.getClassInfo();
    ClassAnalyzer localClassAnalyzer = getParent().getClassAnalyzer(localClassInfo);
    Expression[] arrayOfExpression1;
    Object localObject;
    int i;
    Expression localExpression;
    if (localClassAnalyzer == null)
    {
      arrayOfExpression1 = paramInvokeOperator.getSubExpressions();
      localObject = new Expression[arrayOfExpression1.length - 1];
      for (i = 0; i < localObject.length; i++)
      {
        localExpression = arrayOfExpression1[(i + 1)].simplify();
        if ((localExpression instanceof CheckNullOperator)) {
          localExpression = ((CheckNullOperator)localExpression).getSubExpressions()[0];
        }
        if ((localExpression instanceof ThisOperator))
        {
          localObject[i] = new ThisOperator(((ThisOperator)localExpression).getClassInfo());
        }
        else
        {
          LocalInfo localLocalInfo = null;
          if ((localExpression instanceof LocalLoadOperator))
          {
            localLocalInfo = ((LocalLoadOperator)localExpression).getLocalInfo();
            if (!localLocalInfo.isConstant()) {
              localLocalInfo = null;
            }
          }
          if ((localExpression instanceof OuterLocalOperator)) {
            localLocalInfo = ((OuterLocalOperator)localExpression).getLocalInfo();
          }
          if (localLocalInfo != null)
          {
            localObject[i] = new OuterLocalOperator(localLocalInfo);
          }
          else
          {
            Expression[] arrayOfExpression2 = new Expression[i];
            System.arraycopy(localObject, 0, arrayOfExpression2, 0, i);
            localObject = arrayOfExpression2;
            break;
          }
        }
      }
      localClassAnalyzer = new ClassAnalyzer(this, localClassInfo, this.imports, (Expression[])localObject);
      addClassAnalyzer(localClassAnalyzer);
      localClassAnalyzer.initialize();
      localClassAnalyzer.analyze(null, 0.0D, 0.0D);
      localClassAnalyzer.analyzeInnerClasses(null, 0.0D, 0.0D);
    }
    else
    {
      localObject = localClassAnalyzer.getOuterValues();
      arrayOfExpression1 = paramInvokeOperator.getSubExpressions();
      for (i = 0; i < ((OuterValues)localObject).getCount(); i++) {
        if (i + 1 < arrayOfExpression1.length)
        {
          localExpression = arrayOfExpression1[(i + 1)].simplify();
          if ((localExpression instanceof CheckNullOperator)) {
            localExpression = ((CheckNullOperator)localExpression).getSubExpressions()[0];
          }
          if (((OuterValues)localObject).unifyOuterValues(i, localExpression)) {}
        }
        else
        {
          ((OuterValues)localObject).setCount(i);
          break;
        }
      }
    }
    if (this.usedAnalyzers == null) {
      this.usedAnalyzers = new ArrayList();
    }
    this.usedAnalyzers.add(localClassAnalyzer);
  }
  
  public ClassAnalyzer getClassAnalyzer(ClassInfo paramClassInfo)
  {
    if (this.innerAnalyzers != null)
    {
      Enumeration localEnumeration = this.innerAnalyzers.elements();
      while (localEnumeration.hasMoreElements())
      {
        ClassAnalyzer localClassAnalyzer = (ClassAnalyzer)localEnumeration.nextElement();
        if (localClassAnalyzer.getClazz().equals(paramClassInfo))
        {
          if (localClassAnalyzer.getParent() != this)
          {
            for (ClassDeclarer localClassDeclarer = localClassAnalyzer.getParent(); localClassDeclarer != this; localClassDeclarer = localClassDeclarer.getParent()) {
              if ((localClassDeclarer instanceof MethodAnalyzer)) {
                ((MethodAnalyzer)localClassDeclarer).innerAnalyzers.removeElement(localClassAnalyzer);
              }
            }
            localClassAnalyzer.setParent(this);
          }
          return localClassAnalyzer;
        }
      }
    }
    return getParent().getClassAnalyzer(paramClassInfo);
  }
  
  public void addClassAnalyzer(ClassAnalyzer paramClassAnalyzer)
  {
    if (this.innerAnalyzers == null) {
      this.innerAnalyzers = new Vector();
    }
    this.innerAnalyzers.addElement(paramClassAnalyzer);
    getParent().addClassAnalyzer(paramClassAnalyzer);
  }
  
  public void fillDeclarables(Collection paramCollection)
  {
    if (this.usedAnalyzers != null) {
      paramCollection.addAll(this.usedAnalyzers);
    }
    if (this.innerAnalyzers != null)
    {
      Enumeration localEnumeration = this.innerAnalyzers.elements();
      while (localEnumeration.hasMoreElements())
      {
        ClassAnalyzer localClassAnalyzer = (ClassAnalyzer)localEnumeration.nextElement();
        if (localClassAnalyzer.getParent() == this) {
          localClassAnalyzer.fillDeclarables(paramCollection);
        }
      }
    }
  }
  
  public boolean isMoreOuterThan(ClassDeclarer paramClassDeclarer)
  {
    for (ClassDeclarer localClassDeclarer = paramClassDeclarer; localClassDeclarer != null; localClassDeclarer = localClassDeclarer.getParent()) {
      if (localClassDeclarer == this) {
        return true;
      }
    }
    return false;
  }
  
  public String toString()
  {
    return getClass().getName() + "[" + getClazz() + "." + getName() + "]";
  }
}


