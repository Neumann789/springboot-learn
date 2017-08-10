package jode.expr;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.type.ArrayType;
import jode.type.Type;

public class NewArrayOperator
  extends Operator
{
  String baseTypeString;
  
  public NewArrayOperator(Type paramType, int paramInt)
  {
    super(paramType, 0);
    initOperands(paramInt);
  }
  
  public int getDimensions()
  {
    return this.subExpressions.length;
  }
  
  public int getPriority()
  {
    return 900;
  }
  
  public void updateSubTypes()
  {
    for (int i = 0; i < this.subExpressions.length; i++) {
      this.subExpressions[i].setType(Type.tUInt);
    }
  }
  
  public void updateType() {}
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    Type localType = this.type.getCanonic();
    for (int i = 0; (localType instanceof ArrayType); i++) {
      localType = ((ArrayType)localType).getElementType();
    }
    paramTabbedPrintWriter.print("new ");
    paramTabbedPrintWriter.printType(localType.getHint());
    for (int j = 0; j < i; j++)
    {
      paramTabbedPrintWriter.breakOp();
      paramTabbedPrintWriter.print("[");
      if (j < this.subExpressions.length) {
        this.subExpressions[j].dumpExpression(paramTabbedPrintWriter, 0);
      }
      paramTabbedPrintWriter.print("]");
    }
  }
}


