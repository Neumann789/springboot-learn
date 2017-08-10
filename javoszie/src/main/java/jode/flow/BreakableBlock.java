package jode.flow;

public abstract interface BreakableBlock
{
  public abstract String getLabel();
  
  public abstract void setBreaked();
  
  public abstract void mergeBreakedStack(VariableStack paramVariableStack);
}


