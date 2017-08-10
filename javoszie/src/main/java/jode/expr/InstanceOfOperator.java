package jode.expr;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class InstanceOfOperator
  extends Operator
{
  Type instanceType;
  
  public InstanceOfOperator(Type paramType)
  {
    super(Type.tBoolean, 0);
    this.instanceType = paramType;
    initOperands(1);
  }
  
  public int getPriority()
  {
    return 550;
  }
  
  public void updateSubTypes()
  {
    this.subExpressions[0].setType(Type.tUObject);
  }
  
  public void updateType() {}
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    Type localType = this.instanceType.getCastHelper(this.subExpressions[0].getType());
    if (localType != null)
    {
      paramTabbedPrintWriter.startOp(2, 2);
      paramTabbedPrintWriter.print("(");
      paramTabbedPrintWriter.printType(localType);
      paramTabbedPrintWriter.print(") ");
      paramTabbedPrintWriter.breakOp();
      this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 700);
      paramTabbedPrintWriter.endOp();
    }
    else
    {
      this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 550);
    }
    paramTabbedPrintWriter.breakOp();
    paramTabbedPrintWriter.print(" instanceof ");
    paramTabbedPrintWriter.printType(this.instanceType);
  }
}


