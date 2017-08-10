package jode.expr;

import jode.decompiler.LocalInfo;
import jode.type.Type;

public class LocalStoreOperator
  extends LocalVarOperator
  implements LValueExpression
{
  public LocalStoreOperator(Type paramType, LocalInfo paramLocalInfo)
  {
    super(paramType, paramLocalInfo);
  }
  
  public boolean isRead()
  {
    return (this.parent != null) && (this.parent.getOperatorIndex() != 12);
  }
  
  public boolean isWrite()
  {
    return true;
  }
  
  public boolean matches(Operator paramOperator)
  {
    return ((paramOperator instanceof LocalLoadOperator)) && (((LocalLoadOperator)paramOperator).getLocalInfo().getSlot() == this.local.getSlot());
  }
}


