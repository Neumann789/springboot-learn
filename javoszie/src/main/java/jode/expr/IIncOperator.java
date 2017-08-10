package jode.expr;

import java.io.IOException;
import jode.AssertError;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class IIncOperator
  extends Operator
  implements CombineableOperator
{
  int value;
  
  public IIncOperator(LocalStoreOperator paramLocalStoreOperator, int paramInt1, int paramInt2)
  {
    super(Type.tVoid, paramInt2);
    this.value = paramInt1;
    initOperands(1);
    setSubExpressions(0, paramLocalStoreOperator);
  }
  
  public LValueExpression getLValue()
  {
    return (LValueExpression)this.subExpressions[0];
  }
  
  public int getValue()
  {
    return this.value;
  }
  
  public int getPriority()
  {
    return 100;
  }
  
  public void updateSubTypes()
  {
    this.subExpressions[0].setType(this.type != Type.tVoid ? this.type : Type.tInt);
  }
  
  public void updateType()
  {
    if (this.type != Type.tVoid) {
      updateParentType(this.subExpressions[0].getType());
    }
  }
  
  public void makeNonVoid()
  {
    if (this.type != Type.tVoid) {
      throw new AssertError("already non void");
    }
    this.type = this.subExpressions[0].getType();
  }
  
  public boolean lvalueMatches(Operator paramOperator)
  {
    return getLValue().matches(paramOperator);
  }
  
  public Expression simplify()
  {
    if (this.value == 1)
    {
      int i = getOperatorIndex() == 13 ? 24 : 25;
      return new PrePostFixOperator(getType(), i, getLValue(), isVoid()).simplify();
    }
    return super.simplify();
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.startOp(1, 2);
    this.subExpressions[0].dumpExpression(paramTabbedPrintWriter);
    paramTabbedPrintWriter.endOp();
    paramTabbedPrintWriter.print(getOperatorString() + this.value);
  }
}


