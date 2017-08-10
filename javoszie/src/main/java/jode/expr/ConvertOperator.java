package jode.expr;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class ConvertOperator
  extends Operator
{
  Type from;
  
  public ConvertOperator(Type paramType1, Type paramType2)
  {
    super(paramType2, 0);
    this.from = paramType1;
    initOperands(1);
  }
  
  public boolean opEquals(Operator paramOperator)
  {
    return ((paramOperator instanceof ConvertOperator)) && (this.type == paramOperator.type);
  }
  
  public int getPriority()
  {
    return 700;
  }
  
  public void updateSubTypes()
  {
    this.subExpressions[0].setType(Type.tSubType(this.from));
  }
  
  public void updateType() {}
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.print("(");
    paramTabbedPrintWriter.printType(this.type.getCanonic());
    paramTabbedPrintWriter.print(") ");
    this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 700);
  }
}


