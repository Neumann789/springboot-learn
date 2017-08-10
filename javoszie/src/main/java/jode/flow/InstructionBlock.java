package jode.flow;

import java.io.IOException;
import java.util.Set;
import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.Expression;
import jode.expr.LocalStoreOperator;
import jode.expr.StoreInstruction;
import jode.type.Type;

public class InstructionBlock
  extends InstructionContainer
{
  VariableStack stack;
  LocalInfo pushedLocal = null;
  boolean isDeclaration = false;
  
  public InstructionBlock(Expression paramExpression)
  {
    super(paramExpression);
  }
  
  public InstructionBlock(Expression paramExpression, Jump paramJump)
  {
    super(paramExpression, paramJump);
  }
  
  public VariableStack mapStackToLocal(VariableStack paramVariableStack)
  {
    VariableStack localVariableStack = null;
    int i = this.instr.getFreeOperandCount();
    if (i > 0) {
      this.stack = paramVariableStack.peek(i);
    }
    if (this.instr.getType() != Type.tVoid)
    {
      this.pushedLocal = new LocalInfo();
      this.pushedLocal.setType(this.instr.getType());
      if (paramVariableStack != null) {
        localVariableStack = paramVariableStack.poppush(i, this.pushedLocal);
      }
    }
    else if (i > 0)
    {
      localVariableStack = paramVariableStack.pop(i);
    }
    else
    {
      localVariableStack = paramVariableStack;
    }
    return super.mapStackToLocal(localVariableStack);
  }
  
  public void removePush()
  {
    if (this.stack != null) {
      this.instr = this.stack.mergeIntoExpression(this.instr);
    }
    if (this.pushedLocal != null)
    {
      Expression localExpression = new StoreInstruction(new LocalStoreOperator(this.pushedLocal.getType(), this.pushedLocal)).addOperand(this.instr);
      this.instr = localExpression;
    }
    super.removePush();
  }
  
  public boolean needsBraces()
  {
    return (this.isDeclaration) || ((this.declare != null) && (!this.declare.isEmpty()));
  }
  
  public void checkDeclaration(Set paramSet)
  {
    if (((this.instr instanceof StoreInstruction)) && ((((StoreInstruction)this.instr).getLValue() instanceof LocalStoreOperator)))
    {
      StoreInstruction localStoreInstruction = (StoreInstruction)this.instr;
      LocalInfo localLocalInfo = ((LocalStoreOperator)localStoreInstruction.getLValue()).getLocalInfo();
      if (paramSet.contains(localLocalInfo))
      {
        this.isDeclaration = true;
        paramSet.remove(localLocalInfo);
      }
    }
  }
  
  public void makeDeclaration(Set paramSet)
  {
    super.makeDeclaration(paramSet);
    checkDeclaration(this.declare);
  }
  
  public void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    if (this.isDeclaration)
    {
      StoreInstruction localStoreInstruction = (StoreInstruction)this.instr;
      LocalInfo localLocalInfo = ((LocalStoreOperator)localStoreInstruction.getLValue()).getLocalInfo();
      paramTabbedPrintWriter.startOp(1, 0);
      localLocalInfo.dumpDeclaration(paramTabbedPrintWriter);
      paramTabbedPrintWriter.breakOp();
      paramTabbedPrintWriter.print(" = ");
      localStoreInstruction.getSubExpressions()[1].makeInitializer(localLocalInfo.getType());
      localStoreInstruction.getSubExpressions()[1].dumpExpression(2, paramTabbedPrintWriter);
      paramTabbedPrintWriter.endOp();
    }
    else
    {
      try
      {
        if (this.instr.getType() != Type.tVoid)
        {
          paramTabbedPrintWriter.print("PUSH ");
          this.instr.dumpExpression(2, paramTabbedPrintWriter);
        }
        else
        {
          this.instr.dumpExpression(1, paramTabbedPrintWriter);
        }
      }
      catch (RuntimeException localRuntimeException)
      {
        paramTabbedPrintWriter.print("(RUNTIME ERROR IN EXPRESSION)");
      }
    }
    paramTabbedPrintWriter.println(";");
  }
}


