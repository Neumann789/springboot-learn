package jode.expr;

import jode.bytecode.Reference;
import jode.decompiler.FieldAnalyzer;
import jode.decompiler.MethodAnalyzer;

public class GetFieldOperator
  extends FieldOperator
{
  public GetFieldOperator(MethodAnalyzer paramMethodAnalyzer, boolean paramBoolean, Reference paramReference)
  {
    super(paramMethodAnalyzer, paramBoolean, paramReference);
  }
  
  public Expression simplify()
  {
    if (!this.staticFlag)
    {
      this.subExpressions[0] = this.subExpressions[0].simplify();
      this.subExpressions[0].parent = this;
      if ((this.subExpressions[0] instanceof ThisOperator))
      {
        FieldAnalyzer localFieldAnalyzer = getField();
        if ((localFieldAnalyzer != null) && (localFieldAnalyzer.isSynthetic()))
        {
          Expression localExpression = localFieldAnalyzer.getConstant();
          if (((localExpression instanceof ThisOperator)) || ((localExpression instanceof OuterLocalOperator))) {
            return localExpression;
          }
        }
      }
    }
    return this;
  }
  
  public boolean opEquals(Operator paramOperator)
  {
    return ((paramOperator instanceof GetFieldOperator)) && (((GetFieldOperator)paramOperator).ref.equals(this.ref));
  }
}


