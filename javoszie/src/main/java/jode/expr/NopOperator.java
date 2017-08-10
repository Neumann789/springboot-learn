package jode.expr;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class NopOperator
  extends Expression
{
  public NopOperator(Type paramType)
  {
    super(paramType);
  }
  
  public int getFreeOperandCount()
  {
    return 1;
  }
  
  public int getPriority()
  {
    return 1000;
  }
  
  public void updateSubTypes() {}
  
  public void updateType() {}
  
  public Expression addOperand(Expression paramExpression)
  {
    paramExpression.setType(this.type);
    paramExpression.parent = this.parent;
    return paramExpression;
  }
  
  public boolean isConstant()
  {
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    return paramObject instanceof NopOperator;
  }
  
  public Expression simplify()
  {
    return this;
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.print("POP");
  }
}


