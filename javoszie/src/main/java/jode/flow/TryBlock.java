package jode.flow;

import java.io.IOException;
import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.Expression;
import jode.expr.InvokeOperator;
import jode.expr.LocalLoadOperator;
import jode.type.MethodType;
import jode.type.Type;

public class TryBlock
  extends StructuredBlock
{
  VariableSet gen;
  StructuredBlock[] subBlocks = new StructuredBlock[1];
  
  public TryBlock(FlowBlock paramFlowBlock)
  {
    this.gen = ((VariableSet)paramFlowBlock.gen.clone());
    this.flowBlock = paramFlowBlock;
    StructuredBlock localStructuredBlock = paramFlowBlock.block;
    replace(localStructuredBlock);
    this.subBlocks = new StructuredBlock[] { localStructuredBlock };
    localStructuredBlock.outer = this;
    paramFlowBlock.lastModified = this;
    paramFlowBlock.checkConsistent();
  }
  
  public void addCatchBlock(StructuredBlock paramStructuredBlock)
  {
    StructuredBlock[] arrayOfStructuredBlock = new StructuredBlock[this.subBlocks.length + 1];
    System.arraycopy(this.subBlocks, 0, arrayOfStructuredBlock, 0, this.subBlocks.length);
    arrayOfStructuredBlock[this.subBlocks.length] = paramStructuredBlock;
    this.subBlocks = arrayOfStructuredBlock;
    paramStructuredBlock.outer = this;
    paramStructuredBlock.setFlowBlock(this.flowBlock);
  }
  
  public boolean replaceSubBlock(StructuredBlock paramStructuredBlock1, StructuredBlock paramStructuredBlock2)
  {
    for (int i = 0; i < this.subBlocks.length; i++) {
      if (this.subBlocks[i] == paramStructuredBlock1)
      {
        this.subBlocks[i] = paramStructuredBlock2;
        return true;
      }
    }
    return false;
  }
  
  public StructuredBlock[] getSubBlocks()
  {
    return this.subBlocks;
  }
  
  public VariableStack mapStackToLocal(VariableStack paramVariableStack)
  {
    VariableStack localVariableStack = this.subBlocks[0].mapStackToLocal(paramVariableStack);
    for (int i = 1; i < this.subBlocks.length; i++) {
      localVariableStack = VariableStack.merge(localVariableStack, this.subBlocks[i].mapStackToLocal(VariableStack.EMPTY));
    }
    if (this.jump != null)
    {
      this.jump.stackMap = localVariableStack;
      return null;
    }
    return localVariableStack;
  }
  
  public void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.print("try");
    paramTabbedPrintWriter.openBrace();
    paramTabbedPrintWriter.tab();
    this.subBlocks[0].dumpSource(paramTabbedPrintWriter);
    paramTabbedPrintWriter.untab();
    for (int i = 1; i < this.subBlocks.length; i++) {
      this.subBlocks[i].dumpSource(paramTabbedPrintWriter);
    }
    paramTabbedPrintWriter.closeBrace();
  }
  
  public boolean jumpMayBeChanged()
  {
    for (int i = 0; i < this.subBlocks.length; i++) {
      if ((this.subBlocks[i].jump == null) && (!this.subBlocks[i].jumpMayBeChanged())) {
        return false;
      }
    }
    return true;
  }
  
  public boolean checkJikesArrayClone()
  {
    if ((this.subBlocks.length != 2) || (!(this.subBlocks[0] instanceof InstructionBlock)) || (!(this.subBlocks[1] instanceof CatchBlock))) {
      return false;
    }
    Expression localExpression1 = ((InstructionBlock)this.subBlocks[0]).getInstruction();
    CatchBlock localCatchBlock = (CatchBlock)this.subBlocks[1];
    if ((localExpression1.isVoid()) || (localExpression1.getFreeOperandCount() != 0) || (!(localExpression1 instanceof InvokeOperator)) || (!(localCatchBlock.catchBlock instanceof ThrowBlock)) || (!localCatchBlock.exceptionType.equals(Type.tClass("java.lang.CloneNotSupportedException")))) {
      return false;
    }
    InvokeOperator localInvokeOperator1 = (InvokeOperator)localExpression1;
    if ((!localInvokeOperator1.getMethodName().equals("clone")) || (localInvokeOperator1.isStatic()) || (!localInvokeOperator1.getMethodType().getTypeSignature().equals("()Ljava/lang/Object;")) || (!localInvokeOperator1.getSubExpressions()[0].getType().isOfType(Type.tArray(Type.tUnknown)))) {
      return false;
    }
    Expression localExpression2 = ((ThrowBlock)localCatchBlock.catchBlock).getInstruction();
    if ((localExpression2.getFreeOperandCount() != 0) || (!(localExpression2 instanceof InvokeOperator))) {
      return false;
    }
    InvokeOperator localInvokeOperator2 = (InvokeOperator)localExpression2;
    if ((!localInvokeOperator2.isConstructor()) || (!localInvokeOperator2.getClassType().equals(Type.tClass("java.lang.InternalError"))) || (localInvokeOperator2.getMethodType().getParameterTypes().length != 1)) {
      return false;
    }
    Expression localExpression3 = localInvokeOperator2.getSubExpressions()[1];
    if (!(localExpression3 instanceof InvokeOperator)) {
      return false;
    }
    InvokeOperator localInvokeOperator3 = (InvokeOperator)localExpression3;
    if ((!localInvokeOperator3.getMethodName().equals("getMessage")) || (localInvokeOperator3.isStatic()) || (localInvokeOperator3.getMethodType().getParameterTypes().length != 0) || (localInvokeOperator3.getMethodType().getReturnType() != Type.tString)) {
      return false;
    }
    Expression localExpression4 = localInvokeOperator3.getSubExpressions()[0];
    if ((!(localExpression4 instanceof LocalLoadOperator)) || (!((LocalLoadOperator)localExpression4).getLocalInfo().equals(localCatchBlock.exceptionLocal))) {
      return false;
    }
    this.subBlocks[0].replace(this);
    if (this.flowBlock.lastModified == this) {
      this.flowBlock.lastModified = this.subBlocks[0];
    }
    return true;
  }
  
  public boolean doTransformations()
  {
    return checkJikesArrayClone();
  }
}


