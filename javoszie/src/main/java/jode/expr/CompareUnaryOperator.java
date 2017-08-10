package jode.expr;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class CompareUnaryOperator
  extends Operator
{
  boolean objectType;
  Type compareType;
  
  public CompareUnaryOperator(Type paramType, int paramInt)
  {
    super(Type.tBoolean, paramInt);
    this.compareType = paramType;
    this.objectType = paramType.isOfType(Type.tUObject);
    initOperands(1);
  }
  
  public int getPriority()
  {
    switch (getOperatorIndex())
    {
    case 26: 
    case 27: 
      return 500;
    case 28: 
    case 29: 
    case 30: 
    case 31: 
      return 550;
    }
    throw new RuntimeException("Illegal operator");
  }
  
  public Type getCompareType()
  {
    return this.compareType;
  }
  
  public void updateSubTypes()
  {
    this.subExpressions[0].setType(Type.tSubType(this.compareType));
  }
  
  public void updateType() {}
  
  public Expression simplify()
  {
    if ((this.subExpressions[0] instanceof CompareToIntOperator))
    {
      CompareToIntOperator localCompareToIntOperator = (CompareToIntOperator)this.subExpressions[0];
      int i = 0;
      int j = getOperatorIndex();
      if ((localCompareToIntOperator.allowsNaN) && (getOperatorIndex() > 27)) {
        if (localCompareToIntOperator.greaterOnNaN == ((j == 29) || (j == 30)))
        {
          i = 1;
          j ^= 0x1;
        }
      }
      Expression localExpression = new CompareBinaryOperator(localCompareToIntOperator.compareType, j, localCompareToIntOperator.allowsNaN).addOperand(localCompareToIntOperator.subExpressions[1]).addOperand(localCompareToIntOperator.subExpressions[0]);
      if (i != 0) {
        return localExpression.negate().simplify();
      }
      return localExpression.simplify();
    }
    if (this.subExpressions[0].getType().isOfType(Type.tBoolean))
    {
      if (getOperatorIndex() == 26) {
        return this.subExpressions[0].negate().simplify();
      }
      if (getOperatorIndex() == 27) {
        return this.subExpressions[0].simplify();
      }
    }
    return super.simplify();
  }
  
  public Expression negate()
  {
    if (((getType() != Type.tFloat) && (getType() != Type.tDouble)) || (getOperatorIndex() <= 27))
    {
      setOperatorIndex(getOperatorIndex() ^ 0x1);
      return this;
    }
    return super.negate();
  }
  
  public boolean opEquals(Operator paramOperator)
  {
    return ((paramOperator instanceof CompareUnaryOperator)) && (paramOperator.getOperatorIndex() == getOperatorIndex());
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, getPriority() + 1);
    paramTabbedPrintWriter.breakOp();
    paramTabbedPrintWriter.print(getOperatorString());
    paramTabbedPrintWriter.print(this.objectType ? "null" : "0");
  }
}


