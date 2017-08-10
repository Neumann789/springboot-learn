package jode.expr;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class BinaryOperator
  extends Operator
{
  public BinaryOperator(Type paramType, int paramInt)
  {
    super(paramType, paramInt);
    initOperands(2);
  }
  
  public int getPriority()
  {
    switch (this.operatorIndex)
    {
    case 1: 
    case 2: 
      return 610;
    case 3: 
    case 4: 
    case 5: 
      return 650;
    case 6: 
    case 7: 
    case 8: 
      return 600;
    case 9: 
      return 450;
    case 10: 
      return 410;
    case 11: 
      return 420;
    case 12: 
    case 13: 
    case 14: 
    case 15: 
    case 16: 
    case 17: 
    case 18: 
    case 19: 
    case 20: 
    case 21: 
    case 22: 
    case 23: 
      return 100;
    case 33: 
      return 310;
    case 32: 
      return 350;
    }
    throw new RuntimeException("Illegal operator");
  }
  
  public void updateSubTypes()
  {
    this.subExpressions[0].setType(Type.tSubType(this.type));
    this.subExpressions[1].setType(Type.tSubType(this.type));
  }
  
  public void updateType()
  {
    Type localType1 = Type.tSuperType(this.subExpressions[0].getType());
    Type localType2 = Type.tSuperType(this.subExpressions[1].getType());
    this.subExpressions[0].setType(Type.tSubType(localType2));
    this.subExpressions[1].setType(Type.tSubType(localType1));
    updateParentType(localType1.intersection(localType2));
  }
  
  public Expression negate()
  {
    if ((getOperatorIndex() == 32) || (getOperatorIndex() == 33))
    {
      setOperatorIndex(getOperatorIndex() ^ 0x1);
      for (int i = 0; i < 2; i++)
      {
        this.subExpressions[i] = this.subExpressions[i].negate();
        this.subExpressions[i].parent = this;
      }
      return this;
    }
    return super.negate();
  }
  
  public boolean opEquals(Operator paramOperator)
  {
    return ((paramOperator instanceof BinaryOperator)) && (paramOperator.operatorIndex == this.operatorIndex);
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, getPriority());
    paramTabbedPrintWriter.breakOp();
    paramTabbedPrintWriter.print(getOperatorString());
    this.subExpressions[1].dumpExpression(paramTabbedPrintWriter, getPriority() + 1);
  }
}


