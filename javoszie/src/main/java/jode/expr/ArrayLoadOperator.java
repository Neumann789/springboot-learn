package jode.expr;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.type.ArrayType;
import jode.type.Type;

public class ArrayLoadOperator
  extends Operator
{
  public ArrayLoadOperator(Type paramType)
  {
    super(paramType, 0);
    initOperands(2);
  }
  
  public int getPriority()
  {
    return 950;
  }
  
  public void updateSubTypes()
  {
    this.subExpressions[0].setType(Type.tSubType(Type.tArray(this.type)));
    this.subExpressions[1].setType(Type.tSubType(Type.tInt));
  }
  
  public void updateType()
  {
    Type localType = Type.tSuperType(this.subExpressions[0].getType()).intersection(Type.tArray(this.type));
    if (!(localType instanceof ArrayType)) {
      updateParentType(Type.tError);
    } else {
      updateParentType(((ArrayType)localType).getElementType());
    }
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 950);
    paramTabbedPrintWriter.breakOp();
    paramTabbedPrintWriter.print("[");
    this.subExpressions[1].dumpExpression(paramTabbedPrintWriter, 0);
    paramTabbedPrintWriter.print("]");
  }
}


