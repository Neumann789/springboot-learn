package jode.expr;

import java.io.IOException;
import jode.bytecode.ClassInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class ThisOperator
  extends NoArgOperator
{
  boolean isInnerMost;
  ClassInfo classInfo;
  
  public ThisOperator(ClassInfo paramClassInfo, boolean paramBoolean)
  {
    super(Type.tClass(paramClassInfo));
    this.classInfo = paramClassInfo;
    this.isInnerMost = paramBoolean;
  }
  
  public ThisOperator(ClassInfo paramClassInfo)
  {
    this(paramClassInfo, false);
  }
  
  public ClassInfo getClassInfo()
  {
    return this.classInfo;
  }
  
  public int getPriority()
  {
    return 1000;
  }
  
  public String toString()
  {
    return this.classInfo + ".this";
  }
  
  public boolean opEquals(Operator paramOperator)
  {
    return ((paramOperator instanceof ThisOperator)) && (((ThisOperator)paramOperator).classInfo.equals(this.classInfo));
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    if (!this.isInnerMost)
    {
      paramTabbedPrintWriter.print(paramTabbedPrintWriter.getClassString(this.classInfo, 4));
      paramTabbedPrintWriter.print(".");
    }
    paramTabbedPrintWriter.print("this");
  }
}


