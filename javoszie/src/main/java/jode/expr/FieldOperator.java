package jode.expr;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import jode.AssertError;
import jode.bytecode.ClassInfo;
import jode.bytecode.FieldInfo;
import jode.bytecode.InnerClassInfo;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;
import jode.decompiler.ClassAnalyzer;
import jode.decompiler.FieldAnalyzer;
import jode.decompiler.MethodAnalyzer;
import jode.decompiler.Options;
import jode.decompiler.Scope;
import jode.decompiler.TabbedPrintWriter;
import jode.type.ClassInterfacesType;
import jode.type.NullType;
import jode.type.Type;

public abstract class FieldOperator
  extends Operator
{
  MethodAnalyzer methodAnalyzer;
  boolean staticFlag;
  Reference ref;
  Type classType;
  
  public FieldOperator(MethodAnalyzer paramMethodAnalyzer, boolean paramBoolean, Reference paramReference)
  {
    super(Type.tType(paramReference.getType()));
    this.methodAnalyzer = paramMethodAnalyzer;
    this.staticFlag = paramBoolean;
    this.classType = Type.tType(paramReference.getClazz());
    this.ref = paramReference;
    if (paramBoolean) {
      paramMethodAnalyzer.useType(this.classType);
    }
    initOperands(paramBoolean ? 0 : 1);
  }
  
  public int getPriority()
  {
    return 950;
  }
  
  public void updateSubTypes()
  {
    if (!this.staticFlag) {
      this.subExpressions[0].setType(Type.tSubType(this.classType));
    }
  }
  
  public void updateType()
  {
    updateParentType(getFieldType());
  }
  
  public boolean isStatic()
  {
    return this.staticFlag;
  }
  
  public ClassInfo getClassInfo()
  {
    if ((this.classType instanceof ClassInterfacesType)) {
      return ((ClassInterfacesType)this.classType).getClassInfo();
    }
    return null;
  }
  
  public FieldAnalyzer getField()
  {
    ClassInfo localClassInfo = getClassInfo();
    if (localClassInfo != null)
    {
      ClassAnalyzer localClassAnalyzer = this.methodAnalyzer.getClassAnalyzer();
      for (;;)
      {
        if (localClassInfo == localClassAnalyzer.getClazz())
        {
          int i = localClassAnalyzer.getFieldIndex(this.ref.getName(), Type.tType(this.ref.getType()));
          if (i >= 0) {
            return localClassAnalyzer.getField(i);
          }
          return null;
        }
        if (localClassAnalyzer.getParent() == null) {
          return null;
        }
        if ((localClassAnalyzer.getParent() instanceof MethodAnalyzer))
        {
          localClassAnalyzer = ((MethodAnalyzer)localClassAnalyzer.getParent()).getClassAnalyzer();
        }
        else
        {
          if (!(localClassAnalyzer.getParent() instanceof ClassAnalyzer)) {
            break;
          }
          localClassAnalyzer = (ClassAnalyzer)localClassAnalyzer.getParent();
        }
      }
      throw new AssertError("Unknown parent");
    }
    return null;
  }
  
  public String getFieldName()
  {
    return this.ref.getName();
  }
  
  public Type getFieldType()
  {
    return Type.tType(this.ref.getType());
  }
  
  private static FieldInfo getFieldInfo(ClassInfo paramClassInfo, String paramString1, String paramString2)
  {
    while (paramClassInfo != null)
    {
      FieldInfo localFieldInfo = paramClassInfo.findField(paramString1, paramString2);
      if (localFieldInfo != null) {
        return localFieldInfo;
      }
      ClassInfo[] arrayOfClassInfo = paramClassInfo.getInterfaces();
      for (int i = 0; i < arrayOfClassInfo.length; i++)
      {
        localFieldInfo = getFieldInfo(arrayOfClassInfo[i], paramString1, paramString2);
        if (localFieldInfo != null) {
          return localFieldInfo;
        }
      }
      paramClassInfo = paramClassInfo.getSuperclass();
    }
    return null;
  }
  
  public FieldInfo getFieldInfo()
  {
    ClassInfo localClassInfo;
    if (this.ref.getClazz().charAt(0) == '[') {
      localClassInfo = ClassInfo.javaLangObject;
    } else {
      localClassInfo = TypeSignature.getClassInfo(this.ref.getClazz());
    }
    return getFieldInfo(localClassInfo, this.ref.getName(), this.ref.getType());
  }
  
  public boolean needsCast(Type paramType)
  {
    if ((paramType instanceof NullType)) {
      return true;
    }
    if ((!(paramType instanceof ClassInterfacesType)) || (!(this.classType instanceof ClassInterfacesType))) {
      return false;
    }
    ClassInfo localClassInfo1 = ((ClassInterfacesType)this.classType).getClassInfo();
    ClassInfo localClassInfo2 = ((ClassInterfacesType)paramType).getClassInfo();
    int j;
    for (FieldInfo localFieldInfo = localClassInfo1.findField(this.ref.getName(), this.ref.getType()); localFieldInfo == null; localFieldInfo = localClassInfo1.findField(this.ref.getName(), this.ref.getType()))
    {
      ClassInfo[] arrayOfClassInfo = localClassInfo1.getInterfaces();
      for (j = 0; j < arrayOfClassInfo.length; j++)
      {
        localFieldInfo = arrayOfClassInfo[j].findField(this.ref.getName(), this.ref.getType());
        if (localFieldInfo != null) {
          break label161;
        }
      }
      localClassInfo1 = localClassInfo1.getSuperclass();
      if (localClassInfo1 == null) {
        return false;
      }
    }
    label161:
    if (Modifier.isPrivate(localFieldInfo.getModifiers())) {
      return localClassInfo2 != localClassInfo1;
    }
    if ((localFieldInfo.getModifiers() & 0x5) == 0)
    {
      int i = localClassInfo1.getName().lastIndexOf('.');
      if ((i == -1) || (i != localClassInfo2.getName().lastIndexOf('.')) || (!localClassInfo2.getName().startsWith(localClassInfo1.getName().substring(0, i)))) {
        return true;
      }
    }
    while ((localClassInfo1 != localClassInfo2) && (localClassInfo1 != null))
    {
      FieldInfo[] arrayOfFieldInfo = localClassInfo2.getFields();
      for (j = 0; j < arrayOfFieldInfo.length; j++) {
        if (arrayOfFieldInfo[j].getName().equals(this.ref.getName())) {
          return true;
        }
      }
      localClassInfo2 = localClassInfo2.getSuperclass();
    }
    return false;
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
    super.fillDeclarables(paramCollection);
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    int i = (!this.staticFlag) && ((this.subExpressions[0] instanceof ThisOperator)) ? 1 : 0;
    String str = this.ref.getName();
    if (this.staticFlag)
    {
      if ((!this.classType.equals(Type.tClass(this.methodAnalyzer.getClazz()))) || (this.methodAnalyzer.findLocal(str) != null))
      {
        paramTabbedPrintWriter.printType(this.classType);
        paramTabbedPrintWriter.breakOp();
        paramTabbedPrintWriter.print(".");
      }
      paramTabbedPrintWriter.print(str);
    }
    else if (needsCast(this.subExpressions[0].getType().getCanonic()))
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
      paramTabbedPrintWriter.breakOp();
      paramTabbedPrintWriter.print(".");
      paramTabbedPrintWriter.print(str);
    }
    else
    {
      if (i != 0)
      {
        ThisOperator localThisOperator = (ThisOperator)this.subExpressions[0];
        Scope localScope = paramTabbedPrintWriter.getScope(localThisOperator.getClassInfo(), 1);
        if ((localScope == null) || (paramTabbedPrintWriter.conflicts(str, localScope, 3)))
        {
          localThisOperator.dumpExpression(paramTabbedPrintWriter, 950);
          paramTabbedPrintWriter.breakOp();
          paramTabbedPrintWriter.print(".");
        }
        else if ((paramTabbedPrintWriter.conflicts(str, localScope, 4)) || ((getField() == null) && (paramTabbedPrintWriter.conflicts(str, null, 13))))
        {
          localThisOperator.dumpExpression(paramTabbedPrintWriter, 950);
          paramTabbedPrintWriter.breakOp();
          paramTabbedPrintWriter.print(".");
        }
      }
      else
      {
        this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 950);
        paramTabbedPrintWriter.breakOp();
        paramTabbedPrintWriter.print(".");
      }
      paramTabbedPrintWriter.print(str);
    }
  }
}


