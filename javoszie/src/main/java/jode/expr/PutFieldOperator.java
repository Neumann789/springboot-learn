package jode.expr;

import jode.bytecode.Reference;
import jode.decompiler.MethodAnalyzer;

public class PutFieldOperator
  extends FieldOperator
  implements LValueExpression
{
  public PutFieldOperator(MethodAnalyzer paramMethodAnalyzer, boolean paramBoolean, Reference paramReference)
  {
    super(paramMethodAnalyzer, paramBoolean, paramReference);
  }
  
  public boolean matches(Operator paramOperator)
  {
    return ((paramOperator instanceof GetFieldOperator)) && (((GetFieldOperator)paramOperator).ref.equals(this.ref));
  }
  
  public boolean opEquals(Operator paramOperator)
  {
    return ((paramOperator instanceof PutFieldOperator)) && (((PutFieldOperator)paramOperator).ref.equals(this.ref));
  }
}


