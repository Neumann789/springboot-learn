package jode.expr;

import jode.type.Type;

public abstract class NoArgOperator
  extends Operator
{
  public NoArgOperator(Type paramType, int paramInt)
  {
    super(paramType, paramInt);
    initOperands(0);
  }
  
  public NoArgOperator(Type paramType)
  {
    this(paramType, 0);
  }
  
  public void updateType() {}
  
  public void updateSubTypes() {}
}


