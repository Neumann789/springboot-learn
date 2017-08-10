package jode.expr;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class CheckCastOperator
  extends Operator
{
  Type castType;
  
  public CheckCastOperator(Type paramType)
  {
    super(paramType, 0);
    this.castType = paramType;
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
  
  public Expression simplify()
  {
    if (this.subExpressions[0].getType().getCanonic().isOfType(Type.tSubType(this.castType))) {
      return this.subExpressions[0].simplify();
    }
    return super.simplify();
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.print("(");
    paramTabbedPrintWriter.printType(this.castType);
    paramTabbedPrintWriter.print(") ");
    paramTabbedPrintWriter.breakOp();
    Type localType = this.castType.getCastHelper(this.subExpressions[0].getType());
    if (localType != null)
    {
      paramTabbedPrintWriter.print("(");
      paramTabbedPrintWriter.printType(localType);
      paramTabbedPrintWriter.print(") ");
      paramTabbedPrintWriter.breakOp();
    }
    this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 700);
  }
}


