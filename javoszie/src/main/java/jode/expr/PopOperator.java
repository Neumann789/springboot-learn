package jode.expr;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class PopOperator
  extends Operator
{
  Type popType;
  
  public PopOperator(Type paramType)
  {
    super(Type.tVoid, 0);
    this.popType = paramType;
    initOperands(1);
  }
  
  public int getPriority()
  {
    return 0;
  }
  
  public void updateSubTypes()
  {
    this.subExpressions[0].setType(Type.tSubType(this.popType));
  }
  
  public void updateType() {}
  
  public int getBreakPenalty()
  {
    if ((this.subExpressions[0] instanceof Operator)) {
      return ((Operator)this.subExpressions[0]).getBreakPenalty();
    }
    return 0;
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    this.subExpressions[0].dumpExpression(paramTabbedPrintWriter);
  }
}


