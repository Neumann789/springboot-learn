package jode.expr;

import jode.decompiler.LocalInfo;
import jode.decompiler.MethodAnalyzer;
import jode.type.Type;

public class LocalLoadOperator
  extends LocalVarOperator
{
  MethodAnalyzer methodAnalyzer;
  
  public LocalLoadOperator(Type paramType, MethodAnalyzer paramMethodAnalyzer, LocalInfo paramLocalInfo)
  {
    super(paramType, paramLocalInfo);
    this.methodAnalyzer = paramMethodAnalyzer;
  }
  
  public boolean isRead()
  {
    return true;
  }
  
  public boolean isWrite()
  {
    return false;
  }
  
  public boolean isConstant()
  {
    return false;
  }
  
  public void setMethodAnalyzer(MethodAnalyzer paramMethodAnalyzer)
  {
    this.methodAnalyzer = paramMethodAnalyzer;
  }
  
  public boolean opEquals(Operator paramOperator)
  {
    return ((paramOperator instanceof LocalLoadOperator)) && (((LocalLoadOperator)paramOperator).local.getSlot() == this.local.getSlot());
  }
  
  public Expression simplify()
  {
    if (this.local.getExpression() != null) {
      return this.local.getExpression().simplify();
    }
    return super.simplify();
  }
}


