package jode.expr;

import jode.type.Type;

public abstract class SimpleOperator
  extends Operator
{
  public SimpleOperator(Type paramType, int paramInt1, int paramInt2)
  {
    super(paramType, paramInt1);
    initOperands(paramInt2);
  }
}


