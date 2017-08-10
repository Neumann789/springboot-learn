package jode.expr;

public abstract interface CombineableOperator
{
  public abstract LValueExpression getLValue();
  
  public abstract boolean lvalueMatches(Operator paramOperator);
  
  public abstract void makeNonVoid();
}


