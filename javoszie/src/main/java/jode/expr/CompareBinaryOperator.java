package jode.expr;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class CompareBinaryOperator
  extends Operator
{
  boolean allowsNaN = false;
  Type compareType;
  
  public CompareBinaryOperator(Type paramType, int paramInt)
  {
    super(Type.tBoolean, paramInt);
    this.compareType = paramType;
    initOperands(2);
  }
  
  public CompareBinaryOperator(Type paramType, int paramInt, boolean paramBoolean)
  {
    super(Type.tBoolean, paramInt);
    this.compareType = paramType;
    this.allowsNaN = paramBoolean;
    initOperands(2);
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
    this.subExpressions[1].setType(Type.tSubType(this.compareType));
  }
  
  public void updateType()
  {
    Type localType1 = Type.tSuperType(this.subExpressions[0].getType());
    Type localType2 = Type.tSuperType(this.subExpressions[1].getType());
    this.compareType = this.compareType.intersection(localType1).intersection(localType2);
    this.subExpressions[0].setType(Type.tSubType(localType2));
    this.subExpressions[1].setType(Type.tSubType(localType1));
  }
  
  public Expression negate()
  {
    if ((!this.allowsNaN) || (getOperatorIndex() <= 27))
    {
      setOperatorIndex(getOperatorIndex() ^ 0x1);
      return this;
    }
    return super.negate();
  }
  
  public boolean opEquals(Operator paramOperator)
  {
    return ((paramOperator instanceof CompareBinaryOperator)) && (paramOperator.operatorIndex == this.operatorIndex);
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, getPriority() + 1);
    paramTabbedPrintWriter.breakOp();
    paramTabbedPrintWriter.print(getOperatorString());
    this.subExpressions[1].dumpExpression(paramTabbedPrintWriter, getPriority() + 1);
  }
}


