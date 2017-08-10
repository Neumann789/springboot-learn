package jode.expr;

import java.io.IOException;
import jode.decompiler.FieldAnalyzer;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class IfThenElseOperator
  extends Operator
{
  public IfThenElseOperator(Type paramType)
  {
    super(paramType, 0);
    initOperands(3);
  }
  
  public int getPriority()
  {
    return 200;
  }
  
  public void updateSubTypes()
  {
    this.subExpressions[0].setType(Type.tBoolean);
    this.subExpressions[1].setType(Type.tSubType(this.type));
    this.subExpressions[2].setType(Type.tSubType(this.type));
  }
  
  public void updateType()
  {
    Type localType = Type.tSuperType(this.subExpressions[1].getType()).intersection(Type.tSuperType(this.subExpressions[2].getType()));
    updateParentType(localType);
  }
  
  public Expression simplify()
  {
    Object localObject;
    if ((getType().isOfType(Type.tBoolean)) && ((this.subExpressions[1] instanceof ConstOperator)) && ((this.subExpressions[2] instanceof ConstOperator)))
    {
      localObject = (ConstOperator)this.subExpressions[1];
      ConstOperator localConstOperator = (ConstOperator)this.subExpressions[2];
      if ((((ConstOperator)localObject).getValue().equals(new Integer(1))) && (localConstOperator.getValue().equals(new Integer(0)))) {
        return this.subExpressions[0].simplify();
      }
      if ((localConstOperator.getValue().equals(new Integer(1))) && (((ConstOperator)localObject).getValue().equals(new Integer(0)))) {
        return this.subExpressions[0].negate().simplify();
      }
    }
    if (((this.subExpressions[0] instanceof CompareUnaryOperator)) && ((((CompareUnaryOperator)this.subExpressions[0]).getOperatorIndex() & 0xFFFFFFFE) == 26))
    {
      localObject = (CompareUnaryOperator)this.subExpressions[0];
      int i = ((CompareUnaryOperator)localObject).getOperatorIndex() & 0x1;
      if (((this.subExpressions[(2 - i)] instanceof GetFieldOperator)) && ((this.subExpressions[(1 + i)] instanceof StoreInstruction)))
      {
        GetFieldOperator localGetFieldOperator = (GetFieldOperator)this.subExpressions[(2 - i)];
        StoreInstruction localStoreInstruction = (StoreInstruction)this.subExpressions[(1 + i)];
        int j = ((CompareUnaryOperator)localObject).getOperatorIndex();
        FieldAnalyzer localFieldAnalyzer;
        if (((localStoreInstruction.getLValue() instanceof PutFieldOperator)) && ((localFieldAnalyzer = ((PutFieldOperator)localStoreInstruction.getLValue()).getField()) != null) && (localFieldAnalyzer.isSynthetic()) && (localStoreInstruction.lvalueMatches(localGetFieldOperator)) && ((localObject.subExpressions[0] instanceof GetFieldOperator)) && (localStoreInstruction.lvalueMatches((GetFieldOperator)localObject.subExpressions[0])) && ((localStoreInstruction.subExpressions[1] instanceof InvokeOperator)))
        {
          InvokeOperator localInvokeOperator = (InvokeOperator)localStoreInstruction.subExpressions[1];
          if ((localInvokeOperator.isGetClass()) && ((localInvokeOperator.subExpressions[0] instanceof ConstOperator)) && (localInvokeOperator.subExpressions[0].getType().equals(Type.tString)))
          {
            String str = (String)((ConstOperator)localInvokeOperator.subExpressions[0]).getValue();
            if (localFieldAnalyzer.setClassConstant(str)) {
              return new ClassFieldOperator(str.charAt(0) == '[' ? Type.tType(str) : Type.tClass(str));
            }
          }
        }
      }
    }
    return super.simplify();
  }
  
  public boolean opEquals(Operator paramOperator)
  {
    return paramOperator instanceof IfThenElseOperator;
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 201);
    paramTabbedPrintWriter.breakOp();
    paramTabbedPrintWriter.print(" ? ");
    int i = 0;
    if (!this.subExpressions[1].getType().getHint().isOfType(this.subExpressions[2].getType()))
    {
      paramTabbedPrintWriter.startOp(2, 2);
      paramTabbedPrintWriter.print("(");
      paramTabbedPrintWriter.printType(getType().getHint());
      paramTabbedPrintWriter.print(") ");
      i = 700;
    }
    this.subExpressions[1].dumpExpression(paramTabbedPrintWriter, i);
    if (i == 700) {
      paramTabbedPrintWriter.endOp();
    }
    paramTabbedPrintWriter.breakOp();
    paramTabbedPrintWriter.print(" : ");
    this.subExpressions[2].dumpExpression(paramTabbedPrintWriter, 200);
  }
}


