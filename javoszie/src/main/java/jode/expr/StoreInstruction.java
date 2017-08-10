package jode.expr;

import java.io.IOException;
import jode.AssertError;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class StoreInstruction
  extends Operator
  implements CombineableOperator
{
  boolean opAssign = false;
  
  public StoreInstruction(LValueExpression paramLValueExpression)
  {
    super(Type.tVoid, 12);
    initOperands(2);
    setSubExpressions(0, (Operator)paramLValueExpression);
  }
  
  public LValueExpression getLValue()
  {
    return (LValueExpression)this.subExpressions[0];
  }
  
  public void makeOpAssign(int paramInt)
  {
    setOperatorIndex(paramInt);
    if ((this.subExpressions[1] instanceof NopOperator)) {
      this.subExpressions[1].type = Type.tUnknown;
    }
    this.opAssign = true;
  }
  
  public boolean isOpAssign()
  {
    return this.opAssign;
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
  
  public int getPriority()
  {
    return 100;
  }
  
  public void updateSubTypes()
  {
    if (!isVoid())
    {
      this.subExpressions[0].setType(this.type);
      this.subExpressions[1].setType(Type.tSubType(this.type));
    }
  }
  
  public void updateType()
  {
    if (!this.opAssign)
    {
      Type localType1 = this.subExpressions[0].getType();
      Type localType2 = this.subExpressions[1].getType();
      this.subExpressions[0].setType(Type.tSuperType(localType2));
      this.subExpressions[1].setType(Type.tSubType(localType1));
    }
    if (!isVoid()) {
      updateParentType(this.subExpressions[0].getType());
    }
  }
  
  public Expression simplify()
  {
    if ((this.subExpressions[1] instanceof ConstOperator))
    {
      ConstOperator localConstOperator = (ConstOperator)this.subExpressions[1];
      if (((getOperatorIndex() == 13) || (getOperatorIndex() == 14)) && (localConstOperator.isOne(this.subExpressions[0].getType())))
      {
        int i = getOperatorIndex() == 13 ? 24 : 25;
        return new PrePostFixOperator(getType(), i, getLValue(), isVoid()).simplify();
      }
    }
    return super.simplify();
  }
  
  public boolean opEquals(Operator paramOperator)
  {
    return ((paramOperator instanceof StoreInstruction)) && (paramOperator.operatorIndex == this.operatorIndex) && (paramOperator.isVoid() == isVoid());
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.startOp(1, 2);
    this.subExpressions[0].dumpExpression(paramTabbedPrintWriter);
    paramTabbedPrintWriter.endOp();
    paramTabbedPrintWriter.breakOp();
    paramTabbedPrintWriter.print(getOperatorString());
    this.subExpressions[1].dumpExpression(paramTabbedPrintWriter, 100);
  }
}


