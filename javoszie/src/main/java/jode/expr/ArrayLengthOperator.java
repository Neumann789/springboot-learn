package jode.expr;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class ArrayLengthOperator
  extends Operator
{
  public ArrayLengthOperator()
  {
    super(Type.tInt, 0);
    initOperands(1);
  }
  
  public int getPriority()
  {
    return 950;
  }
  
  public void updateSubTypes()
  {
    this.subExpressions[0].setType(Type.tArray(Type.tUnknown));
  }
  
  public void updateType() {}
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 900);
    paramTabbedPrintWriter.print(".length");
  }
}


