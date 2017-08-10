package jode.expr;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import jode.AssertError;
import jode.GlobalOptions;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.ClassInfo;
import jode.bytecode.InnerClassInfo;
import jode.bytecode.MethodInfo;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;
import jode.decompiler.ClassAnalyzer;
import jode.decompiler.MethodAnalyzer;
import jode.decompiler.Options;
import jode.decompiler.OuterValues;
import jode.decompiler.Scope;
import jode.decompiler.TabbedPrintWriter;
import jode.jvm.Interpreter;
import jode.jvm.InterpreterException;
import jode.jvm.SimpleRuntimeEnvironment;
import jode.jvm.SyntheticAnalyzer;
import jode.type.ClassInterfacesType;
import jode.type.IntegerType;
import jode.type.MethodType;
import jode.type.NullType;
import jode.type.Type;
import jode.util.SimpleMap;
import jode.util.SimpleMap.SimpleEntry;

public final class InvokeOperator
  extends Operator
  implements MatchableOperator
{
  public static final int VIRTUAL = 0;
  public static final int SPECIAL = 1;
  public static final int STATIC = 2;
  public static final int CONSTRUCTOR = 3;
  public static final int ACCESSSPECIAL = 4;
  MethodAnalyzer methodAnalyzer;
  int methodFlag;
  MethodType methodType;
  String methodName;
  Reference ref;
  int skippedArgs;
  Type classType;
  Type[] hints;
  private static final Hashtable hintTypes = new Hashtable();
  
  public InvokeOperator(MethodAnalyzer paramMethodAnalyzer, int paramInt, Reference paramReference)
  {
    super(Type.tUnknown, 0);
    this.ref = paramReference;
    this.methodType = Type.tMethod(paramReference.getType());
    this.methodName = paramReference.getName();
    this.classType = Type.tType(paramReference.getClazz());
    this.hints = null;
    Map localMap = (Map)hintTypes.get(this.methodName + "." + this.methodType);
    if (localMap != null)
    {
      Iterator localIterator = localMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        if (this.classType.isOfType(((Type)localEntry.getKey()).getSubType()))
        {
          this.hints = ((Type[])localEntry.getValue());
          break;
        }
      }
    }
    if ((this.hints != null) && (this.hints[0] != null)) {
      this.type = this.hints[0];
    } else {
      this.type = this.methodType.getReturnType();
    }
    this.methodAnalyzer = paramMethodAnalyzer;
    this.methodFlag = paramInt;
    if (paramInt == 2) {
      paramMethodAnalyzer.useType(this.classType);
    }
    this.skippedArgs = (paramInt == 2 ? 0 : 1);
    initOperands(this.skippedArgs + this.methodType.getParameterTypes().length);
    checkAnonymousClasses();
  }
  
  public final boolean isStatic()
  {
    return this.methodFlag == 2;
  }
  
  public MethodType getMethodType()
  {
    return this.methodType;
  }
  
  public String getMethodName()
  {
    return this.methodName;
  }
  
  private static MethodInfo getMethodInfo(ClassInfo paramClassInfo, String paramString1, String paramString2)
  {
    while (paramClassInfo != null)
    {
      MethodInfo localMethodInfo = paramClassInfo.findMethod(paramString1, paramString2);
      if (localMethodInfo != null) {
        return localMethodInfo;
      }
      paramClassInfo = paramClassInfo.getSuperclass();
    }
    return null;
  }
  
  public MethodInfo getMethodInfo()
  {
    ClassInfo localClassInfo;
    if (this.ref.getClazz().charAt(0) == '[') {
      localClassInfo = ClassInfo.javaLangObject;
    } else {
      localClassInfo = TypeSignature.getClassInfo(this.ref.getClazz());
    }
    return getMethodInfo(localClassInfo, this.ref.getName(), this.ref.getType());
  }
  
  public Type getClassType()
  {
    return this.classType;
  }
  
  public int getPriority()
  {
    return 950;
  }
  
  public void checkAnonymousClasses()
  {
    if ((this.methodFlag != 3) || ((Options.options & 0x4) == 0)) {
      return;
    }
    InnerClassInfo localInnerClassInfo = getOuterClassInfo(getClassInfo());
    if ((localInnerClassInfo != null) && ((localInnerClassInfo.outer == null) || (localInnerClassInfo.name == null))) {
      this.methodAnalyzer.addAnonymousConstructor(this);
    }
  }
  
  public void updateSubTypes()
  {
    int i = 0;
    if (!isStatic()) {
      this.subExpressions[(i++)].setType(Type.tSubType(getClassType()));
    }
    Type[] arrayOfType = this.methodType.getParameterTypes();
    for (int j = 0; j < arrayOfType.length; j++)
    {
      Type localType = (this.hints != null) && (this.hints[(j + 1)] != null) ? this.hints[(j + 1)] : arrayOfType[j];
      this.subExpressions[(i++)].setType(Type.tSubType(localType));
    }
  }
  
  public void updateType() {}
  
  public void makeNonVoid()
  {
    if (this.type != Type.tVoid) {
      throw new AssertError("already non void");
    }
    ClassInfo localClassInfo = getClassInfo();
    InnerClassInfo localInnerClassInfo = getOuterClassInfo(localClassInfo);
    if ((localInnerClassInfo != null) && (localInnerClassInfo.name == null))
    {
      if (localClassInfo.getInterfaces().length > 0) {
        this.type = Type.tClass(localClassInfo.getInterfaces()[0]);
      } else {
        this.type = Type.tClass(localClassInfo.getSuperclass());
      }
    }
    else {
      this.type = this.subExpressions[0].getType();
    }
  }
  
  public boolean isConstructor()
  {
    return this.methodFlag == 3;
  }
  
  public ClassInfo getClassInfo()
  {
    if ((this.classType instanceof ClassInterfacesType)) {
      return ((ClassInterfacesType)this.classType).getClassInfo();
    }
    return null;
  }
  
  public boolean isThis()
  {
    return getClassInfo() == this.methodAnalyzer.getClazz();
  }
  
  public InnerClassInfo getOuterClassInfo(ClassInfo paramClassInfo)
  {
    if (paramClassInfo != null)
    {
      InnerClassInfo[] arrayOfInnerClassInfo = paramClassInfo.getOuterClasses();
      if (arrayOfInnerClassInfo != null) {
        return arrayOfInnerClassInfo[0];
      }
    }
    return null;
  }
  
  public ClassAnalyzer getClassAnalyzer()
  {
    if ((Options.options & 0x6) == 0) {
      return null;
    }
    ClassInfo localClassInfo = getClassInfo();
    if (localClassInfo == null) {
      return null;
    }
    int i = 0;
    InnerClassInfo[] arrayOfInnerClassInfo = localClassInfo.getOuterClasses();
    if (((Options.options & 0x2) != 0) && (arrayOfInnerClassInfo != null))
    {
      i = arrayOfInnerClassInfo.length;
      if ((arrayOfInnerClassInfo[(i - 1)].outer == null) || (arrayOfInnerClassInfo[(i - 1)].name == null)) {
        i--;
      }
      if (i > 0) {
        localClassInfo = ClassInfo.forName(arrayOfInnerClassInfo[(i - 1)].outer);
      }
    }
    ClassAnalyzer localClassAnalyzer = this.methodAnalyzer.getClassAnalyzer(localClassInfo);
    if (localClassAnalyzer == null)
    {
      localClassAnalyzer = this.methodAnalyzer.getClassAnalyzer();
      while (localClassInfo != localClassAnalyzer.getClazz())
      {
        if (localClassAnalyzer.getParent() == null) {
          return null;
        }
        if (((localClassAnalyzer.getParent() instanceof MethodAnalyzer)) && ((Options.options & 0x4) != 0)) {
          localClassAnalyzer = ((MethodAnalyzer)localClassAnalyzer.getParent()).getClassAnalyzer();
        } else if (((localClassAnalyzer.getParent() instanceof ClassAnalyzer)) && ((Options.options & 0x2) != 0)) {
          localClassAnalyzer = (ClassAnalyzer)localClassAnalyzer.getParent();
        } else {
          throw new AssertError("Unknown parent: " + localClassAnalyzer + ": " + localClassAnalyzer.getParent());
        }
      }
    }
    while (i > 0)
    {
      i--;
      localClassAnalyzer = localClassAnalyzer.getInnerClassAnalyzer(arrayOfInnerClassInfo[i].name);
      if (localClassAnalyzer == null) {
        return null;
      }
    }
    return localClassAnalyzer;
  }
  
  public boolean isOuter()
  {
    if ((this.classType instanceof ClassInterfacesType))
    {
      ClassInfo localClassInfo = ((ClassInterfacesType)this.classType).getClassInfo();
      ClassAnalyzer localClassAnalyzer = this.methodAnalyzer.getClassAnalyzer();
      for (;;)
      {
        if (localClassInfo == localClassAnalyzer.getClazz()) {
          return true;
        }
        if (localClassAnalyzer.getParent() == null) {
          break label149;
        }
        if (((localClassAnalyzer.getParent() instanceof MethodAnalyzer)) && ((Options.options & 0x4) != 0))
        {
          localClassAnalyzer = ((MethodAnalyzer)localClassAnalyzer.getParent()).getClassAnalyzer();
        }
        else
        {
          if ((!(localClassAnalyzer.getParent() instanceof ClassAnalyzer)) || ((Options.options & 0x2) == 0)) {
            break;
          }
          localClassAnalyzer = (ClassAnalyzer)localClassAnalyzer.getParent();
        }
      }
      throw new AssertError("Unknown parent: " + localClassAnalyzer + ": " + localClassAnalyzer.getParent());
    }
    label149:
    return false;
  }
  
  public MethodAnalyzer getMethodAnalyzer()
  {
    ClassAnalyzer localClassAnalyzer = getClassAnalyzer();
    if (localClassAnalyzer == null) {
      return null;
    }
    return localClassAnalyzer.getMethod(this.methodName, this.methodType);
  }
  
  public boolean isSuperOrThis()
  {
    ClassInfo localClassInfo = getClassInfo();
    if (localClassInfo != null) {
      return localClassInfo.superClassOf(this.methodAnalyzer.getClazz());
    }
    return false;
  }
  
  public boolean isConstant()
  {
    if ((Options.options & 0x4) == 0) {
      return super.isConstant();
    }
    ClassInfo localClassInfo = getClassInfo();
    InnerClassInfo localInnerClassInfo = getOuterClassInfo(localClassInfo);
    ClassAnalyzer localClassAnalyzer = this.methodAnalyzer.getClassAnalyzer(localClassInfo);
    if ((localClassAnalyzer != null) && (localInnerClassInfo != null) && (localInnerClassInfo.outer == null) && (localInnerClassInfo.name != null) && (localClassAnalyzer.getParent() == this.methodAnalyzer)) {
      return false;
    }
    return super.isConstant();
  }
  
  public boolean matches(Operator paramOperator)
  {
    return ((paramOperator instanceof InvokeOperator)) || ((paramOperator instanceof GetFieldOperator));
  }
  
  public boolean isGetClass()
  {
    MethodAnalyzer localMethodAnalyzer = getMethodAnalyzer();
    if (localMethodAnalyzer == null) {
      return false;
    }
    SyntheticAnalyzer localSyntheticAnalyzer = getMethodAnalyzer().getSynthetic();
    return (localSyntheticAnalyzer != null) && (localSyntheticAnalyzer.getKind() == 1);
  }
  
  public ConstOperator deobfuscateString(ConstOperator paramConstOperator)
  {
    ClassAnalyzer localClassAnalyzer = this.methodAnalyzer.getClassAnalyzer();
    MethodAnalyzer localMethodAnalyzer = localClassAnalyzer.getMethod(this.methodName, this.methodType);
    if (localMethodAnalyzer == null) {
      return null;
    }
    Environment localEnvironment = new Environment("L" + this.methodAnalyzer.getClazz().getName().replace('.', '/') + ";");
    Interpreter localInterpreter = new Interpreter(localEnvironment);
    localEnvironment.interpreter = localInterpreter;
    String str;
    try
    {
      str = (String)localInterpreter.interpretMethod(localMethodAnalyzer.getBytecodeInfo(), null, new Object[] { paramConstOperator.getValue() });
    }
    catch (InterpreterException localInterpreterException)
    {
      if ((GlobalOptions.debuggingFlags & 0x400) != 0)
      {
        GlobalOptions.err.println("Warning: Can't interpret method " + this.methodName);
        localInterpreterException.printStackTrace(GlobalOptions.err);
      }
      return null;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      if ((GlobalOptions.debuggingFlags & 0x400) != 0)
      {
        GlobalOptions.err.println("Warning: Interpreted method throws an uncaught exception: ");
        localInvocationTargetException.getTargetException().printStackTrace(GlobalOptions.err);
      }
      return null;
    }
    return new ConstOperator(str);
  }
  
  public Expression simplifyStringBuffer()
  {
    if (getClassType().equals(Type.tStringBuffer))
    {
      if ((isConstructor()) && ((this.subExpressions[0] instanceof NewOperator)))
      {
        if (this.methodType.getParameterTypes().length == 0) {
          return EMPTYSTRING;
        }
        if ((this.methodType.getParameterTypes().length == 1) && (this.methodType.getParameterTypes()[0].equals(Type.tString))) {
          return this.subExpressions[1].simplifyString();
        }
      }
      if ((!isStatic()) && (getMethodName().equals("append")) && (getMethodType().getParameterTypes().length == 1))
      {
        Expression localExpression = this.subExpressions[0].simplifyStringBuffer();
        if (localExpression == null) {
          return null;
        }
        this.subExpressions[1] = this.subExpressions[1].simplifyString();
        if ((localExpression == EMPTYSTRING) && (this.subExpressions[1].getType().isOfType(Type.tString))) {
          return this.subExpressions[1];
        }
        if (((localExpression instanceof StringAddOperator)) && (((Operator)localExpression).getSubExpressions()[0] == EMPTYSTRING)) {
          localExpression = ((Operator)localExpression).getSubExpressions()[1];
        }
        Object localObject1 = this.subExpressions[1];
        Type[] arrayOfType = { Type.tStringBuffer, ((Expression)localObject1).getType().getCanonic() };
        if (needsCast(1, arrayOfType))
        {
          localObject2 = this.methodType.getParameterTypes()[0];
          ConvertOperator localConvertOperator = new ConvertOperator((Type)localObject2, (Type)localObject2);
          localConvertOperator.addOperand((Expression)localObject1);
          localObject1 = localConvertOperator;
        }
        Object localObject2 = new StringAddOperator();
        ((Operator)localObject2).addOperand((Expression)localObject1);
        ((Operator)localObject2).addOperand(localExpression);
        return (Expression)localObject2;
      }
    }
    return null;
  }
  
  public Expression simplifyString()
  {
    Object localObject;
    if ((getMethodName().equals("toString")) && (!isStatic()) && (getClassType().equals(Type.tStringBuffer)) && (this.subExpressions.length == 1))
    {
      localObject = this.subExpressions[0].simplifyStringBuffer();
      if (localObject != null) {
        return (Expression)localObject;
      }
    }
    else if ((getMethodName().equals("valueOf")) && (isStatic()) && (getClassType().equals(Type.tString)) && (this.subExpressions.length == 1))
    {
      if (this.subExpressions[0].getType().isOfType(Type.tString)) {
        return this.subExpressions[0];
      }
      localObject = new StringAddOperator();
      ((Operator)localObject).addOperand(this.subExpressions[0]);
      ((Operator)localObject).addOperand(EMPTYSTRING);
    }
    else if ((getMethodName().equals("concat")) && (!isStatic()) && (getClassType().equals(Type.tString)))
    {
      localObject = new StringAddOperator();
      Expression localExpression = this.subExpressions[1].simplify();
      if ((localExpression instanceof StringAddOperator))
      {
        Operator localOperator = (Operator)localExpression;
        if ((localOperator.subExpressions != null) && (localOperator.subExpressions[0] == EMPTYSTRING)) {
          localExpression = localOperator.subExpressions[1];
        }
      }
      ((Expression)localObject).addOperand(localExpression);
      ((Expression)localObject).addOperand(this.subExpressions[0].simplify());
    }
    else if (((Options.options & 0x20) != 0) && (isThis()) && (isStatic()) && (this.methodType.getParameterTypes().length == 1) && (this.methodType.getParameterTypes()[0].equals(Type.tString)) && (this.methodType.getReturnType().equals(Type.tString)))
    {
      localObject = this.subExpressions[0].simplifyString();
      if ((localObject instanceof ConstOperator))
      {
        localObject = deobfuscateString((ConstOperator)localObject);
        if (localObject != null) {
          return (Expression)localObject;
        }
      }
    }
    return this;
  }
  
  public Expression simplifyAccess()
  {
    if (getMethodAnalyzer() != null)
    {
      SyntheticAnalyzer localSyntheticAnalyzer = getMethodAnalyzer().getSynthetic();
      if (localSyntheticAnalyzer != null)
      {
        int i = localSyntheticAnalyzer.getUnifyParam();
        Object localObject = null;
        switch (localSyntheticAnalyzer.getKind())
        {
        case 2: 
          localObject = new GetFieldOperator(this.methodAnalyzer, false, localSyntheticAnalyzer.getReference());
          break;
        case 5: 
          localObject = new GetFieldOperator(this.methodAnalyzer, true, localSyntheticAnalyzer.getReference());
          break;
        case 3: 
        case 9: 
          localObject = new StoreInstruction(new PutFieldOperator(this.methodAnalyzer, false, localSyntheticAnalyzer.getReference()));
          if (localSyntheticAnalyzer.getKind() == 9) {
            ((StoreInstruction)localObject).makeNonVoid();
          }
          break;
        case 6: 
        case 10: 
          localObject = new StoreInstruction(new PutFieldOperator(this.methodAnalyzer, true, localSyntheticAnalyzer.getReference()));
          if (localSyntheticAnalyzer.getKind() == 10) {
            ((StoreInstruction)localObject).makeNonVoid();
          }
          break;
        case 4: 
          localObject = new InvokeOperator(this.methodAnalyzer, 4, localSyntheticAnalyzer.getReference());
          break;
        case 7: 
          localObject = new InvokeOperator(this.methodAnalyzer, 2, localSyntheticAnalyzer.getReference());
          break;
        case 8: 
          if (((this.subExpressions[i] instanceof ConstOperator)) && (((ConstOperator)this.subExpressions[i]).getValue() == null)) {
            localObject = new InvokeOperator(this.methodAnalyzer, 3, localSyntheticAnalyzer.getReference());
          }
          break;
        }
        if (localObject != null)
        {
          if (this.subExpressions != null)
          {
            int j = this.subExpressions.length;
            while (j-- > 0) {
              if ((j != i) || (localSyntheticAnalyzer.getKind() != 8))
              {
                localObject = ((Expression)localObject).addOperand(this.subExpressions[j]);
                if (this.subExpressions[j].getFreeOperandCount() > 0) {
                  break;
                }
              }
            }
          }
          return (Expression)localObject;
        }
      }
    }
    return null;
  }
  
  public boolean needsCast(int paramInt, Type[] paramArrayOfType)
  {
    Type localType;
    if (this.methodFlag == 2)
    {
      localType = this.classType;
    }
    else
    {
      if (paramInt == 0)
      {
        if ((paramArrayOfType[0] instanceof NullType)) {
          return true;
        }
        if ((!(paramArrayOfType[0] instanceof ClassInterfacesType)) || (!(this.classType instanceof ClassInterfacesType))) {
          return false;
        }
        localClassInfo1 = ((ClassInterfacesType)this.classType).getClassInfo();
        ClassInfo localClassInfo2 = ((ClassInterfacesType)paramArrayOfType[0]).getClassInfo();
        localObject = getMethodInfo();
        if (localObject == null) {
          return false;
        }
        if (Modifier.isPrivate(((MethodInfo)localObject).getModifiers())) {
          return localClassInfo2 != localClassInfo1;
        }
        if ((((MethodInfo)localObject).getModifiers() & 0x5) == 0)
        {
          int j = localClassInfo1.getName().lastIndexOf('.');
          if ((j != localClassInfo2.getName().lastIndexOf('.')) || (!localClassInfo2.getName().startsWith(localClassInfo1.getName().substring(0, j + 1)))) {
            return true;
          }
        }
        return false;
      }
      localType = paramArrayOfType[0];
    }
    if (!(localType instanceof ClassInterfacesType)) {
      return false;
    }
    ClassInfo localClassInfo1 = ((ClassInterfacesType)localType).getClassInfo();
    int i = this.skippedArgs;
    Object localObject = this.methodType.getParameterTypes();
    if (localObject[(paramInt - i)].equals(paramArrayOfType[paramInt])) {
      return false;
    }
    while (localClassInfo1 != null)
    {
      MethodInfo[] arrayOfMethodInfo = localClassInfo1.getMethods();
      label373:
      for (int k = 0; k < arrayOfMethodInfo.length; k++) {
        if (arrayOfMethodInfo[k].getName().equals(this.methodName))
        {
          Type[] arrayOfType = Type.tMethod(arrayOfMethodInfo[k].getType()).getParameterTypes();
          if ((arrayOfType.length == localObject.length) && (!localObject[(paramInt - i)].isOfType(Type.tSubType(arrayOfType[(paramInt - i)]))))
          {
            for (int m = i; m < paramArrayOfType.length; m++) {
              if (!paramArrayOfType[m].isOfType(Type.tSubType(arrayOfType[(m - i)]))) {
                break label373;
              }
            }
            return true;
          }
        }
      }
      localClassInfo1 = localClassInfo1.getSuperclass();
    }
    return false;
  }
  
  public Expression simplify()
  {
    Expression localExpression = simplifyAccess();
    if (localExpression != null) {
      return localExpression.simplify();
    }
    localExpression = simplifyString();
    if (localExpression != this) {
      return localExpression.simplify();
    }
    return super.simplify();
  }
  
  public void fillDeclarables(Collection paramCollection)
  {
    ClassInfo localClassInfo = getClassInfo();
    InnerClassInfo localInnerClassInfo = getOuterClassInfo(localClassInfo);
    ClassAnalyzer localClassAnalyzer = this.methodAnalyzer.getClassAnalyzer(localClassInfo);
    if (((Options.options & 0x4) != 0) && (localInnerClassInfo != null) && (localInnerClassInfo.outer == null) && (localInnerClassInfo.name != null) && (localClassAnalyzer != null) && (localClassAnalyzer.getParent() == this.methodAnalyzer))
    {
      localClassAnalyzer.fillDeclarables(paramCollection);
      paramCollection.add(localClassAnalyzer);
    }
    if ((!isConstructor()) || (isStatic()))
    {
      super.fillDeclarables(paramCollection);
      return;
    }
    int i = 1;
    Object localObject1 = this.subExpressions.length;
    boolean bool1 = false;
    boolean bool2 = false;
    Object localObject3;
    if (((Options.options & 0x4) != 0) && (localClassAnalyzer != null) && (localInnerClassInfo != null) && ((localInnerClassInfo.outer == null) || (localInnerClassInfo.name == null)))
    {
      localObject2 = localClassAnalyzer.getOuterValues();
      i += ((OuterValues)localObject2).getCount();
      bool1 = ((OuterValues)localObject2).isJikesAnonymousInner();
      bool2 = ((OuterValues)localObject2).isImplicitOuterClass();
      Object localObject4;
      for (int j = 1; j < i; j++)
      {
        localObject4 = this.subExpressions[j];
        if ((localObject4 instanceof CheckNullOperator))
        {
          CheckNullOperator localCheckNullOperator = (CheckNullOperator)localObject4;
          localObject4 = localCheckNullOperator.subExpressions[0];
        }
        ((Expression)localObject4).fillDeclarables(paramCollection);
      }
      if (localInnerClassInfo.name == null)
      {
        localObject3 = localClassInfo.getSuperclass();
        localObject4 = localClassInfo.getInterfaces();
        if ((localObject4.length == 1) && ((localObject3 == null) || (localObject3 == ClassInfo.javaLangObject))) {
          localClassInfo = localObject4[0];
        } else {
          localClassInfo = localObject3 != null ? localObject3 : ClassInfo.javaLangObject;
        }
        localInnerClassInfo = getOuterClassInfo(localClassInfo);
      }
    }
    if (((Options.options & 0x2) != 0) && (localInnerClassInfo != null) && (localInnerClassInfo.outer != null) && (localInnerClassInfo.name != null) && (!Modifier.isStatic(localInnerClassInfo.modifiers)) && (!bool2) && (i < localObject1))
    {
      localObject2 = bool1 ? this.subExpressions[(--localObject1)] : this.subExpressions[(i++)];
      if ((localObject2 instanceof CheckNullOperator))
      {
        localObject3 = (CheckNullOperator)localObject2;
        localObject2 = localObject3.subExpressions[0];
      }
      ((Expression)localObject2).fillDeclarables(paramCollection);
    }
    for (Object localObject2 = i; localObject2 < localObject1; localObject2++) {
      this.subExpressions[localObject2].fillDeclarables(paramCollection);
    }
  }
  
  public void makeDeclaration(Set paramSet)
  {
    super.makeDeclaration(paramSet);
    if ((isConstructor()) && (!isStatic()) && ((Options.options & 0x4) != 0))
    {
      ClassInfo localClassInfo = getClassInfo();
      InnerClassInfo localInnerClassInfo = getOuterClassInfo(localClassInfo);
      ClassAnalyzer localClassAnalyzer = this.methodAnalyzer.getClassAnalyzer(localClassInfo);
      if ((localClassAnalyzer != null) && (localInnerClassInfo != null) && (localInnerClassInfo.name == null)) {
        localClassAnalyzer.makeDeclaration(paramSet);
      }
    }
  }
  
  public int getBreakPenalty()
  {
    return 5;
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    int i = 1;
    int j = this.subExpressions.length;
    int k = 0;
    ClassInfo localClassInfo = getClassInfo();
    ClassAnalyzer localClassAnalyzer = null;
    Type[] arrayOfType = new Type[this.subExpressions.length];
    for (int m = 0; m < this.subExpressions.length; m++) {
      arrayOfType[m] = this.subExpressions[m].getType().getCanonic();
    }
    paramTabbedPrintWriter.startOp(1, 0);
    boolean bool2;
    Object localObject3;
    Object localObject1;
    switch (this.methodFlag)
    {
    case 3: 
      m = 0;
      boolean bool1 = false;
      bool2 = false;
      localObject3 = getOuterClassInfo(localClassInfo);
      if ((localObject3 != null) && (((InnerClassInfo)localObject3).name == null) && ((Options.options & 0x4) != 0)) {
        k = 1;
      }
      localClassAnalyzer = this.methodAnalyzer.getClassAnalyzer(localClassInfo);
      Object localObject4;
      Object localObject5;
      if ((((Options.options ^ 0xFFFFFFFF) & 0x204) == 0) && (localClassAnalyzer != null) && (localObject3 != null) && ((((InnerClassInfo)localObject3).outer == null) || (((InnerClassInfo)localObject3).name == null)))
      {
        localObject4 = localClassAnalyzer.getOuterValues();
        i += ((OuterValues)localObject4).getCount();
        bool1 = ((OuterValues)localObject4).isJikesAnonymousInner();
        bool2 = ((OuterValues)localObject4).isImplicitOuterClass();
        if (((InnerClassInfo)localObject3).name == null)
        {
          localObject5 = localClassInfo.getSuperclass();
          ClassInfo[] arrayOfClassInfo = localClassInfo.getInterfaces();
          if ((arrayOfClassInfo.length == 1) && ((localObject5 == null) || (localObject5 == ClassInfo.javaLangObject)))
          {
            localClassInfo = arrayOfClassInfo[0];
          }
          else
          {
            if (arrayOfClassInfo.length > 0) {
              paramTabbedPrintWriter.print("too many supers in ANONYMOUS ");
            }
            localClassInfo = localObject5 != null ? localObject5 : ClassInfo.javaLangObject;
          }
          localObject3 = getOuterClassInfo(localClassInfo);
          if ((bool1) && (localObject3 != null) && (((InnerClassInfo)localObject3).outer == null) && (((InnerClassInfo)localObject3).name != null))
          {
            Expression localExpression = this.subExpressions[(--j)];
            if ((localExpression instanceof CheckNullOperator))
            {
              CheckNullOperator localCheckNullOperator = (CheckNullOperator)localExpression;
              localExpression = localCheckNullOperator.subExpressions[0];
            }
            if ((!(localExpression instanceof ThisOperator)) || (((ThisOperator)localExpression).getClassInfo() != this.methodAnalyzer.getClazz())) {
              paramTabbedPrintWriter.print("ILLEGAL ANON CONSTR");
            }
          }
        }
      }
      if ((localObject3 != null) && (((InnerClassInfo)localObject3).outer != null) && (((InnerClassInfo)localObject3).name != null) && (!Modifier.isStatic(((InnerClassInfo)localObject3).modifiers)) && (((Options.options ^ 0xFFFFFFFF) & 0x202) == 0) && (!bool2)) {
        if (i < j)
        {
          localObject4 = bool1 ? this.subExpressions[(--j)] : this.subExpressions[(i++)];
          if ((localObject4 instanceof CheckNullOperator))
          {
            localObject5 = (CheckNullOperator)localObject4;
            localObject4 = localObject5.subExpressions[0];
          }
          if ((localObject4 instanceof ThisOperator))
          {
            localObject5 = paramTabbedPrintWriter.getScope(((ThisOperator)localObject4).getClassInfo(), 1);
            if (paramTabbedPrintWriter.conflicts(((InnerClassInfo)localObject3).name, (Scope)localObject5, 1))
            {
              m = 1;
              ((Expression)localObject4).dumpExpression(paramTabbedPrintWriter, 950);
              paramTabbedPrintWriter.breakOp();
              paramTabbedPrintWriter.print(".");
            }
          }
          else
          {
            m = 1;
            if ((((Expression)localObject4).getType().getCanonic() instanceof NullType))
            {
              paramTabbedPrintWriter.print("(");
              paramTabbedPrintWriter.startOp(0, 1);
              paramTabbedPrintWriter.print("(");
              paramTabbedPrintWriter.printType(Type.tClass(ClassInfo.forName(((InnerClassInfo)localObject3).outer)));
              paramTabbedPrintWriter.print(") ");
              paramTabbedPrintWriter.breakOp();
              ((Expression)localObject4).dumpExpression(paramTabbedPrintWriter, 700);
              paramTabbedPrintWriter.endOp();
              paramTabbedPrintWriter.print(")");
            }
            else
            {
              ((Expression)localObject4).dumpExpression(paramTabbedPrintWriter, 950);
            }
            paramTabbedPrintWriter.breakOp();
            paramTabbedPrintWriter.print(".");
          }
        }
        else
        {
          paramTabbedPrintWriter.print("MISSING OUTEREXPR ");
        }
      }
      if (((this.subExpressions[0] instanceof NewOperator)) && (arrayOfType[0].equals(this.classType)))
      {
        paramTabbedPrintWriter.print("new ");
        if (m != 0) {
          paramTabbedPrintWriter.print(((InnerClassInfo)localObject3).name);
        } else {
          paramTabbedPrintWriter.printType(Type.tClass(localClassInfo));
        }
      }
      else if (((this.subExpressions[0] instanceof ThisOperator)) && (((ThisOperator)this.subExpressions[0]).getClassInfo() == this.methodAnalyzer.getClazz()))
      {
        if (isThis()) {
          paramTabbedPrintWriter.print("this");
        } else {
          paramTabbedPrintWriter.print("super");
        }
      }
      else
      {
        paramTabbedPrintWriter.print("(");
        paramTabbedPrintWriter.startOp(0, 0);
        paramTabbedPrintWriter.print("(UNCONSTRUCTED)");
        paramTabbedPrintWriter.breakOp();
        this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 700);
        paramTabbedPrintWriter.endOp();
        paramTabbedPrintWriter.print(")");
        paramTabbedPrintWriter.breakOp();
        paramTabbedPrintWriter.print(".");
        paramTabbedPrintWriter.printType(Type.tClass(localClassInfo));
      }
      break;
    case 1: 
      if ((isSuperOrThis()) && ((this.subExpressions[0] instanceof ThisOperator)) && (((ThisOperator)this.subExpressions[0]).getClassInfo() == this.methodAnalyzer.getClazz()))
      {
        if (!isThis())
        {
          paramTabbedPrintWriter.print("super");
          localObject1 = getClassInfo().getSuperclass();
          arrayOfType[0] = (localObject1 == null ? Type.tObject : Type.tClass((ClassInfo)localObject1));
          paramTabbedPrintWriter.breakOp();
          paramTabbedPrintWriter.print(".");
        }
      }
      else if (isThis())
      {
        if (needsCast(0, arrayOfType))
        {
          paramTabbedPrintWriter.print("(");
          paramTabbedPrintWriter.startOp(0, 1);
          paramTabbedPrintWriter.print("(");
          paramTabbedPrintWriter.printType(this.classType);
          paramTabbedPrintWriter.print(") ");
          paramTabbedPrintWriter.breakOp();
          this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 700);
          paramTabbedPrintWriter.endOp();
          paramTabbedPrintWriter.print(")");
          arrayOfType[0] = this.classType;
        }
        else
        {
          this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 950);
        }
        paramTabbedPrintWriter.breakOp();
        paramTabbedPrintWriter.print(".");
      }
      else
      {
        paramTabbedPrintWriter.print("(");
        paramTabbedPrintWriter.startOp(0, 0);
        paramTabbedPrintWriter.print("(NON VIRTUAL ");
        paramTabbedPrintWriter.printType(this.classType);
        paramTabbedPrintWriter.print(") ");
        paramTabbedPrintWriter.breakOp();
        this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 700);
        paramTabbedPrintWriter.endOp();
        paramTabbedPrintWriter.print(")");
        paramTabbedPrintWriter.breakOp();
        paramTabbedPrintWriter.print(".");
      }
      paramTabbedPrintWriter.print(this.methodName);
      break;
    case 4: 
      if (arrayOfType[0].equals(this.classType))
      {
        this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 950);
      }
      else
      {
        paramTabbedPrintWriter.print("(");
        paramTabbedPrintWriter.startOp(0, 0);
        paramTabbedPrintWriter.print("(");
        paramTabbedPrintWriter.printType(this.classType);
        paramTabbedPrintWriter.print(") ");
        paramTabbedPrintWriter.breakOp();
        arrayOfType[0] = this.classType;
        this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 700);
        paramTabbedPrintWriter.endOp();
        paramTabbedPrintWriter.print(")");
      }
      paramTabbedPrintWriter.breakOp();
      paramTabbedPrintWriter.print(".");
      paramTabbedPrintWriter.print(this.methodName);
      break;
    case 2: 
      i = 0;
      localObject1 = paramTabbedPrintWriter.getScope(getClassInfo(), 1);
      if ((localObject1 == null) || (paramTabbedPrintWriter.conflicts(this.methodName, (Scope)localObject1, 2)))
      {
        paramTabbedPrintWriter.printType(this.classType);
        paramTabbedPrintWriter.breakOp();
        paramTabbedPrintWriter.print(".");
      }
      paramTabbedPrintWriter.print(this.methodName);
      break;
    case 0: 
      if ((this.subExpressions[0] instanceof ThisOperator))
      {
        localObject1 = (ThisOperator)this.subExpressions[0];
        Scope localScope = paramTabbedPrintWriter.getScope(((ThisOperator)localObject1).getClassInfo(), 1);
        if ((paramTabbedPrintWriter.conflicts(this.methodName, localScope, 2)) || ((getMethodAnalyzer() == null) && ((!isThis()) || (paramTabbedPrintWriter.conflicts(this.methodName, null, 12)))))
        {
          ((ThisOperator)localObject1).dumpExpression(paramTabbedPrintWriter, 950);
          paramTabbedPrintWriter.breakOp();
          paramTabbedPrintWriter.print(".");
        }
      }
      else
      {
        if (needsCast(0, arrayOfType))
        {
          paramTabbedPrintWriter.print("(");
          paramTabbedPrintWriter.startOp(0, 1);
          paramTabbedPrintWriter.print("(");
          paramTabbedPrintWriter.printType(this.classType);
          paramTabbedPrintWriter.print(") ");
          paramTabbedPrintWriter.breakOp();
          this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 700);
          paramTabbedPrintWriter.endOp();
          paramTabbedPrintWriter.print(")");
          arrayOfType[0] = this.classType;
        }
        else
        {
          this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 950);
        }
        paramTabbedPrintWriter.breakOp();
        paramTabbedPrintWriter.print(".");
      }
      paramTabbedPrintWriter.print(this.methodName);
    }
    paramTabbedPrintWriter.endOp();
    paramTabbedPrintWriter.breakOp();
    if ((Options.outputStyle & 0x40) != 0) {
      paramTabbedPrintWriter.print(" ");
    }
    paramTabbedPrintWriter.print("(");
    paramTabbedPrintWriter.startOp(0, 0);
    int n = 1;
    int i1 = this.skippedArgs;
    while (i < j)
    {
      if (n == 0)
      {
        paramTabbedPrintWriter.print(", ");
        paramTabbedPrintWriter.breakOp();
      }
      else
      {
        n = 0;
      }
      bool2 = false;
      int i2;
      if (needsCast(i, arrayOfType))
      {
        localObject3 = this.methodType.getParameterTypes()[(i - i1)];
        paramTabbedPrintWriter.startOp(2, 1);
        paramTabbedPrintWriter.print("(");
        paramTabbedPrintWriter.printType((Type)localObject3);
        paramTabbedPrintWriter.print(") ");
        paramTabbedPrintWriter.breakOp();
        arrayOfType[i] = localObject3;
        i2 = 700;
      }
      this.subExpressions[(i++)].dumpExpression(paramTabbedPrintWriter, i2);
      if (i2 == 700) {
        paramTabbedPrintWriter.endOp();
      }
    }
    paramTabbedPrintWriter.endOp();
    paramTabbedPrintWriter.print(")");
    if (k != 0)
    {
      Object localObject2 = paramTabbedPrintWriter.saveOps();
      paramTabbedPrintWriter.openBraceClass();
      paramTabbedPrintWriter.tab();
      localClassAnalyzer.dumpBlock(paramTabbedPrintWriter);
      paramTabbedPrintWriter.untab();
      paramTabbedPrintWriter.closeBraceClass();
      paramTabbedPrintWriter.restoreOps(localObject2);
    }
  }
  
  public boolean opEquals(Operator paramOperator)
  {
    if ((paramOperator instanceof InvokeOperator))
    {
      InvokeOperator localInvokeOperator = (InvokeOperator)paramOperator;
      return (this.classType.equals(localInvokeOperator.classType)) && (this.methodName.equals(localInvokeOperator.methodName)) && (this.methodType.equals(localInvokeOperator.methodType)) && (this.methodFlag == localInvokeOperator.methodFlag);
    }
    return false;
  }
  
  static
  {
    IntegerType localIntegerType = new IntegerType(2, 4);
    Type[] arrayOfType1 = { localIntegerType };
    Type[] arrayOfType2 = { null, localIntegerType };
    Type[] arrayOfType3 = { null, localIntegerType, null };
    SimpleMap localSimpleMap1 = new SimpleMap(Collections.singleton(new SimpleMap.SimpleEntry(Type.tString, arrayOfType2)));
    SimpleMap localSimpleMap2 = new SimpleMap(Collections.singleton(new SimpleMap.SimpleEntry(Type.tString, arrayOfType3)));
    hintTypes.put("indexOf.(I)I", localSimpleMap1);
    hintTypes.put("lastIndexOf.(I)I", localSimpleMap1);
    hintTypes.put("indexOf.(II)I", localSimpleMap2);
    hintTypes.put("lastIndexOf.(II)I", localSimpleMap2);
    hintTypes.put("write.(I)V", new SimpleMap(Collections.singleton(new SimpleMap.SimpleEntry(Type.tClass("java.io.Writer"), arrayOfType2))));
    hintTypes.put("read.()I", new SimpleMap(Collections.singleton(new SimpleMap.SimpleEntry(Type.tClass("java.io.Reader"), arrayOfType1))));
    hintTypes.put("unread.(I)V", new SimpleMap(Collections.singleton(new SimpleMap.SimpleEntry(Type.tClass("java.io.PushbackReader"), arrayOfType2))));
  }
  
  class Environment
    extends SimpleRuntimeEnvironment
  {
    Interpreter interpreter;
    String classSig;
    
    public Environment(String paramString)
    {
      this.classSig = paramString.intern();
    }
    
    public Object invokeMethod(Reference paramReference, boolean paramBoolean, Object paramObject, Object[] paramArrayOfObject)
      throws InterpreterException, InvocationTargetException
    {
      if ((paramObject == null) && (paramReference.getClazz().equals(this.classSig)))
      {
        String str = paramReference.getClazz();
        str = str.substring(1, paramReference.getClazz().length() - 1).replace('/', '.');
        BytecodeInfo localBytecodeInfo = ClassInfo.forName(str).findMethod(paramReference.getName(), paramReference.getType()).getBytecode();
        if (localBytecodeInfo != null) {
          return this.interpreter.interpretMethod(localBytecodeInfo, null, paramArrayOfObject);
        }
        throw new InterpreterException("Can't interpret static native method: " + paramReference);
      }
      return super.invokeMethod(paramReference, paramBoolean, paramObject, paramArrayOfObject);
    }
  }
}


