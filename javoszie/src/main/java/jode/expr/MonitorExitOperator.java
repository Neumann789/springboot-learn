package jode.expr;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class MonitorExitOperator
  extends Operator
{
  public MonitorExitOperator()
  {
    super(Type.tVoid, 0);
    initOperands(1);
  }
  
  public int getPriority()
  {
    return 700;
  }
  
  public void updateSubTypes()
  {
    this.subExpressions[0].setType(Type.tUObject);
  }
  
  public void updateType() {}
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.print("MONITOREXIT ");
    this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 700);
  }
}


