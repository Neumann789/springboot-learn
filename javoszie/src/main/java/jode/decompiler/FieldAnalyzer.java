package jode.decompiler;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Set;
import jode.bytecode.FieldInfo;
import jode.expr.ConstOperator;
import jode.expr.Expression;
import jode.expr.OuterLocalOperator;
import jode.type.Type;

public class FieldAnalyzer
  implements Analyzer
{
  ClassAnalyzer clazz;
  ImportHandler imports;
  int modifiers;
  Type type;
  String fieldName;
  Expression constant;
  boolean isSynthetic;
  boolean isDeprecated;
  boolean analyzedSynthetic = false;
  
  public FieldAnalyzer(ClassAnalyzer paramClassAnalyzer, FieldInfo paramFieldInfo, ImportHandler paramImportHandler)
  {
    this.clazz = paramClassAnalyzer;
    this.imports = paramImportHandler;
    this.modifiers = paramFieldInfo.getModifiers();
    this.type = Type.tType(paramFieldInfo.getType());
    this.fieldName = paramFieldInfo.getName();
    this.constant = null;
    this.isSynthetic = paramFieldInfo.isSynthetic();
    this.isDeprecated = paramFieldInfo.isDeprecated();
    if (paramFieldInfo.getConstant() != null)
    {
      this.constant = new ConstOperator(paramFieldInfo.getConstant());
      this.constant.setType(this.type);
      this.constant.makeInitializer(this.type);
    }
  }
  
  public String getName()
  {
    return this.fieldName;
  }
  
  public Type getType()
  {
    return this.type;
  }
  
  public ClassAnalyzer getClassAnalyzer()
  {
    return this.clazz;
  }
  
  public Expression getConstant()
  {
    return this.constant;
  }
  
  public boolean isSynthetic()
  {
    return this.isSynthetic;
  }
  
  public boolean isFinal()
  {
    return Modifier.isFinal(this.modifiers);
  }
  
  public void analyzedSynthetic()
  {
    this.analyzedSynthetic = true;
  }
  
  public boolean setInitializer(Expression paramExpression)
  {
    if (this.constant != null) {
      return this.constant.equals(paramExpression);
    }
    if ((this.isSynthetic) && ((this.fieldName.startsWith("this$")) || (this.fieldName.startsWith("val$"))))
    {
      if ((this.fieldName.startsWith("val$")) && (this.fieldName.length() > 4) && ((paramExpression instanceof OuterLocalOperator)))
      {
        LocalInfo localLocalInfo = ((OuterLocalOperator)paramExpression).getLocalInfo();
        localLocalInfo.addHint(this.fieldName.substring(4), this.type);
      }
      analyzedSynthetic();
    }
    else
    {
      paramExpression.makeInitializer(this.type);
    }
    this.constant = paramExpression;
    return true;
  }
  
  public boolean setClassConstant(String paramString)
  {
    if (this.constant != null) {
      return false;
    }
    if (paramString.charAt(0) == '[')
    {
      if (paramString.charAt(paramString.length() - 1) == ';') {
        paramString = paramString.substring(0, paramString.length() - 1);
      }
      if (this.fieldName.equals("array" + paramString.replace('[', '$').replace('.', '$')))
      {
        analyzedSynthetic();
        return true;
      }
    }
    else if ((this.fieldName.equals("class$" + paramString.replace('.', '$'))) || (this.fieldName.equals("class$L" + paramString.replace('.', '$'))))
    {
      analyzedSynthetic();
      return true;
    }
    return false;
  }
  
  public void analyze()
  {
    this.imports.useType(this.type);
  }
  
  public void makeDeclaration(Set paramSet)
  {
    if (this.constant != null)
    {
      this.constant.makeDeclaration(paramSet);
      this.constant = this.constant.simplify();
    }
  }
  
  public boolean skipWriting()
  {
    return this.analyzedSynthetic;
  }
  
  public void dumpSource(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    if (this.isDeprecated)
    {
      paramTabbedPrintWriter.println("/**");
      paramTabbedPrintWriter.println(" * @deprecated");
      paramTabbedPrintWriter.println(" */");
    }
    if (this.isSynthetic) {
      paramTabbedPrintWriter.print("/*synthetic*/ ");
    }
    int i = this.modifiers;
    paramTabbedPrintWriter.startOp(1, 0);
    String str = Modifier.toString(i);
    if (str.length() > 0) {
      paramTabbedPrintWriter.print(str + " ");
    }
    paramTabbedPrintWriter.printType(this.type);
    paramTabbedPrintWriter.print(" " + this.fieldName);
    if (this.constant != null)
    {
      paramTabbedPrintWriter.breakOp();
      paramTabbedPrintWriter.print(" = ");
      this.constant.dumpExpression(2, paramTabbedPrintWriter);
    }
    paramTabbedPrintWriter.endOp();
    paramTabbedPrintWriter.println(";");
  }
  
  public String toString()
  {
    return getClass().getName() + "[" + this.clazz.getClazz() + "." + getName() + "]";
  }
}


