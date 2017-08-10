package jode.expr;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.type.ArrayType;
import jode.type.Type;

public class ArrayStoreOperator
  extends ArrayLoadOperator
  implements LValueExpression
{
  public ArrayStoreOperator(Type paramType)
  {
    super(paramType);
  }
  
  public boolean matches(Operator paramOperator)
  {
    return paramOperator instanceof ArrayLoadOperator;
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    Type localType1 = this.subExpressions[0].getType().getHint();
    if ((localType1 instanceof ArrayType))
    {
      Type localType2 = ((ArrayType)localType1).getElementType();
      if (!localType2.isOfType(getType()))
      {
        paramTabbedPrintWriter.print("(");
        paramTabbedPrintWriter.startOp(0, 1);
        paramTabbedPrintWriter.print("(");
        paramTabbedPrintWriter.printType(Type.tArray(getType().getHint()));
        paramTabbedPrintWriter.print(") ");
        paramTabbedPrintWriter.breakOp();
        this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 700);
        paramTabbedPrintWriter.print(")");
        paramTabbedPrintWriter.breakOp();
        paramTabbedPrintWriter.print("[");
        this.subExpressions[1].dumpExpression(paramTabbedPrintWriter, 0);
        paramTabbedPrintWriter.print("]");
        return;
      }
    }
    super.dumpExpression(paramTabbedPrintWriter);
  }
}


