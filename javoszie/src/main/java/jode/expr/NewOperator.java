package jode.expr;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class NewOperator
  extends NoArgOperator
{
  public NewOperator(Type paramType)
  {
    super(paramType);
  }
  
  public int getPriority()
  {
    return 950;
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.print("new ");
    paramTabbedPrintWriter.printType(this.type);
  }
}


