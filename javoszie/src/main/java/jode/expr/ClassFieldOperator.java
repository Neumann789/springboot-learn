package jode.expr;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class ClassFieldOperator
  extends NoArgOperator
{
  Type classType;
  
  public ClassFieldOperator(Type paramType)
  {
    super(Type.tJavaLangClass);
    this.classType = paramType;
  }
  
  public int getPriority()
  {
    return 950;
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.printType(this.classType);
    paramTabbedPrintWriter.print(".class");
  }
}


