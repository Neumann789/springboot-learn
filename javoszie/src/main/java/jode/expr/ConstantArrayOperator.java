package jode.expr;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.type.ArrayType;
import jode.type.Type;

public class ConstantArrayOperator
  extends Operator
{
  boolean isInitializer;
  ConstOperator empty;
  Type argType;
  
  public ConstantArrayOperator(Type paramType, int paramInt)
  {
    super(paramType);
    this.argType = ((paramType instanceof ArrayType) ? Type.tSubType(((ArrayType)paramType).getElementType()) : Type.tError);
    Object localObject;
    if ((this.argType == Type.tError) || (this.argType.isOfType(Type.tUObject))) {
      localObject = null;
    } else if (this.argType.isOfType(Type.tBoolUInt)) {
      localObject = new Integer(0);
    } else if (this.argType.isOfType(Type.tLong)) {
      localObject = new Long(0L);
    } else if (this.argType.isOfType(Type.tFloat)) {
      localObject = new Float(0.0F);
    } else if (this.argType.isOfType(Type.tDouble)) {
      localObject = new Double(0.0D);
    } else {
      throw new IllegalArgumentException("Illegal Type: " + this.argType);
    }
    this.empty = new ConstOperator(localObject);
    this.empty.setType(this.argType);
    this.empty.makeInitializer(this.argType);
    initOperands(paramInt);
    for (int i = 0; i < this.subExpressions.length; i++) {
      setSubExpressions(i, this.empty);
    }
  }
  
  public void updateSubTypes()
  {
    this.argType = ((this.type instanceof ArrayType) ? Type.tSubType(((ArrayType)this.type).getElementType()) : Type.tError);
    for (int i = 0; i < this.subExpressions.length; i++) {
      if (this.subExpressions[i] != null) {
        this.subExpressions[i].setType(this.argType);
      }
    }
  }
  
  public void updateType() {}
  
  public boolean setValue(int paramInt, Expression paramExpression)
  {
    if ((paramInt < 0) || (paramInt > this.subExpressions.length) || (this.subExpressions[paramInt] != this.empty)) {
      return false;
    }
    paramExpression.setType(this.argType);
    setType(Type.tSuperType(Type.tArray(paramExpression.getType())));
    this.subExpressions[paramInt] = paramExpression;
    paramExpression.parent = this;
    paramExpression.makeInitializer(this.argType);
    return true;
  }
  
  public int getPriority()
  {
    return 200;
  }
  
  public void makeInitializer(Type paramType)
  {
    if (paramType.getHint().isOfType(getType())) {
      this.isInitializer = true;
    }
  }
  
  public Expression simplify()
  {
    for (int i = 0; i < this.subExpressions.length; i++) {
      if (this.subExpressions[i] != null) {
        this.subExpressions[i] = this.subExpressions[i].simplify();
      }
    }
    return this;
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    if (!this.isInitializer)
    {
      paramTabbedPrintWriter.print("new ");
      paramTabbedPrintWriter.printType(this.type.getHint());
      paramTabbedPrintWriter.breakOp();
      paramTabbedPrintWriter.print(" ");
    }
    paramTabbedPrintWriter.print("{ ");
    paramTabbedPrintWriter.startOp(0, 0);
    for (int i = 0; i < this.subExpressions.length; i++)
    {
      if (i > 0)
      {
        paramTabbedPrintWriter.print(", ");
        paramTabbedPrintWriter.breakOp();
      }
      if (this.subExpressions[i] != null) {
        this.subExpressions[i].dumpExpression(paramTabbedPrintWriter, 0);
      } else {
        this.empty.dumpExpression(paramTabbedPrintWriter, 0);
      }
    }
    paramTabbedPrintWriter.endOp();
    paramTabbedPrintWriter.print(" }");
  }
}


