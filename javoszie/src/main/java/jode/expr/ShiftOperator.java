package jode.expr;

import jode.type.Type;

public class ShiftOperator
  extends BinaryOperator
{
  public ShiftOperator(Type paramType, int paramInt)
  {
    super(paramType, paramInt);
  }
  
  public void updateSubTypes()
  {
    this.subExpressions[0].setType(Type.tSubType(this.type));
    this.subExpressions[1].setType(Type.tSubType(Type.tInt));
  }
  
  public void updateType()
  {
    updateParentType(Type.tSuperType(this.subExpressions[0].getType()));
  }
}


