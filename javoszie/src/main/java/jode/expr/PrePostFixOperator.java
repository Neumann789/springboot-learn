package jode.expr;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class PrePostFixOperator
  extends Operator
{
  boolean postfix;
  
  public PrePostFixOperator(Type paramType, int paramInt, LValueExpression paramLValueExpression, boolean paramBoolean)
  {
    super(paramType);
    this.postfix = paramBoolean;
    setOperatorIndex(paramInt);
    initOperands(1);
    setSubExpressions(0, (Operator)paramLValueExpression);
  }
  
  public int getPriority()
  {
    return this.postfix ? 800 : 700;
  }
  
  public void updateSubTypes()
  {
    if (!isVoid()) {
      this.subExpressions[0].setType(this.type);
    }
  }
  
  public void updateType()
  {
    if (!isVoid()) {
      updateParentType(this.subExpressions[0].getType());
    }
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    if (!this.postfix) {
      paramTabbedPrintWriter.print(getOperatorString());
    }
    paramTabbedPrintWriter.startOp(1, 2);
    this.subExpressions[0].dumpExpression(paramTabbedPrintWriter);
    paramTabbedPrintWriter.endOp();
    if (this.postfix) {
      paramTabbedPrintWriter.print(getOperatorString());
    }
  }
}


