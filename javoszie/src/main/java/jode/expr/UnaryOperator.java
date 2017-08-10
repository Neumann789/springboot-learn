package jode.expr;

import java.io.IOException;
import jode.decompiler.Options;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class UnaryOperator
  extends Operator
{
  public UnaryOperator(Type paramType, int paramInt)
  {
    super(paramType, paramInt);
    initOperands(1);
  }
  
  public int getPriority()
  {
    return 700;
  }
  
  public Expression negate()
  {
    if (getOperatorIndex() == 34)
    {
      if (this.subExpressions != null) {
        return this.subExpressions[0];
      }
      return new NopOperator(Type.tBoolean);
    }
    return super.negate();
  }
  
  public void updateSubTypes()
  {
    this.subExpressions[0].setType(Type.tSubType(this.type));
  }
  
  public void updateType()
  {
    updateParentType(Type.tSuperType(this.subExpressions[0].getType()));
  }
  
  public boolean opEquals(Operator paramOperator)
  {
    return ((paramOperator instanceof UnaryOperator)) && (paramOperator.operatorIndex == this.operatorIndex);
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.print(getOperatorString());
    if ((Options.outputStyle & 0x40) != 0) {
      paramTabbedPrintWriter.print(" ");
    }
    this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 700);
  }
}


