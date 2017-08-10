package jode.expr;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class StringAddOperator
  extends Operator
{
  protected Type operandType;
  
  public StringAddOperator()
  {
    super(Type.tString, 1);
    initOperands(2);
  }
  
  public int getPriority()
  {
    return 610;
  }
  
  public boolean opEquals(Operator paramOperator)
  {
    return paramOperator instanceof StringAddOperator;
  }
  
  public void updateSubTypes() {}
  
  public void updateType() {}
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    if ((!this.subExpressions[0].getType().isOfType(Type.tString)) && (!this.subExpressions[1].getType().isOfType(Type.tString)))
    {
      paramTabbedPrintWriter.print("\"\"");
      paramTabbedPrintWriter.breakOp();
      paramTabbedPrintWriter.print(getOperatorString());
    }
    this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 610);
    paramTabbedPrintWriter.breakOp();
    paramTabbedPrintWriter.print(getOperatorString());
    this.subExpressions[1].dumpExpression(paramTabbedPrintWriter, 611);
  }
}


