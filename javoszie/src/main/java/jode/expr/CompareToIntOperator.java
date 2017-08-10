package jode.expr;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class CompareToIntOperator
  extends Operator
{
  boolean allowsNaN;
  boolean greaterOnNaN;
  Type compareType;
  
  public CompareToIntOperator(Type paramType, boolean paramBoolean)
  {
    super(Type.tInt, 0);
    this.compareType = paramType;
    this.allowsNaN = ((paramType == Type.tFloat) || (paramType == Type.tDouble));
    this.greaterOnNaN = paramBoolean;
    initOperands(2);
  }
  
  public int getPriority()
  {
    return 499;
  }
  
  public void updateSubTypes()
  {
    this.subExpressions[0].setType(Type.tSubType(this.compareType));
    this.subExpressions[1].setType(Type.tSubType(this.compareType));
  }
  
  public void updateType() {}
  
  public boolean opEquals(Operator paramOperator)
  {
    return paramOperator instanceof CompareToIntOperator;
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 550);
    paramTabbedPrintWriter.breakOp();
    paramTabbedPrintWriter.print(" <=>");
    if (this.allowsNaN) {
      paramTabbedPrintWriter.print(this.greaterOnNaN ? "g" : "l");
    }
    paramTabbedPrintWriter.print(" ");
    this.subExpressions[1].dumpExpression(paramTabbedPrintWriter, 551);
  }
}


