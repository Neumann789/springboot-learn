package jode.expr;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Set;
import jode.GlobalOptions;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public abstract class Expression
{
  protected Type type;
  Operator parent = null;
  public static Expression EMPTYSTRING = new ConstOperator("");
  
  public Expression(Type paramType)
  {
    this.type = paramType;
  }
  
  public void setType(Type paramType)
  {
    Type localType = paramType.intersection(this.type);
    if (this.type.equals(localType)) {
      return;
    }
    if ((localType == Type.tError) && (paramType != Type.tError))
    {
      GlobalOptions.err.println("setType: Type error in " + this + ": merging " + this.type + " and " + paramType);
      if (this.parent != null) {
        GlobalOptions.err.println("\tparent is " + this.parent);
      }
      if ((GlobalOptions.debuggingFlags & 0x4) != 0) {
        Thread.dumpStack();
      }
    }
    this.type = localType;
    if (this.type != Type.tError) {
      updateSubTypes();
    }
  }
  
  public void updateParentType(Type paramType)
  {
    setType(paramType);
    if (this.parent != null) {
      this.parent.updateType();
    }
  }
  
  public abstract void updateType();
  
  public abstract void updateSubTypes();
  
  public Type getType()
  {
    return this.type;
  }
  
  public Operator getParent()
  {
    return this.parent;
  }
  
  public abstract int getPriority();
  
  public int getBreakPenalty()
  {
    return 0;
  }
  
  public abstract int getFreeOperandCount();
  
  public abstract Expression addOperand(Expression paramExpression);
  
  public Expression negate()
  {
    UnaryOperator localUnaryOperator = new UnaryOperator(Type.tBoolean, 34);
    localUnaryOperator.addOperand(this);
    return localUnaryOperator;
  }
  
  public boolean hasSideEffects(Expression paramExpression)
  {
    return false;
  }
  
  public int canCombine(CombineableOperator paramCombineableOperator)
  {
    return 0;
  }
  
  public boolean containsMatchingLoad(CombineableOperator paramCombineableOperator)
  {
    return false;
  }
  
  public boolean containsConflictingLoad(MatchableOperator paramMatchableOperator)
  {
    return false;
  }
  
  public Expression combine(CombineableOperator paramCombineableOperator)
  {
    return null;
  }
  
  public Expression removeOnetimeLocals()
  {
    return this;
  }
  
  public Expression simplify()
  {
    return this;
  }
  
  public Expression simplifyString()
  {
    return this;
  }
  
  public Expression simplifyStringBuffer()
  {
    return null;
  }
  
  public void makeInitializer(Type paramType) {}
  
  public boolean isConstant()
  {
    return true;
  }
  
  public void fillInGenSet(Collection paramCollection1, Collection paramCollection2) {}
  
  public void fillDeclarables(Collection paramCollection) {}
  
  public void makeDeclaration(Set paramSet) {}
  
  public abstract void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException;
  
  public void dumpExpression(int paramInt, TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.startOp(paramInt, getBreakPenalty());
    dumpExpression(paramTabbedPrintWriter);
    paramTabbedPrintWriter.endOp();
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter, int paramInt)
    throws IOException
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    String str = "";
    if ((this.type != Type.tError) && ((GlobalOptions.debuggingFlags & 0x4) != 0)) {
      str = "(TYPE " + this.type + ")";
    }
    if (str != "")
    {
      if (paramInt > 700)
      {
        i = 1;
        k = 1;
        paramTabbedPrintWriter.print("(");
        paramTabbedPrintWriter.startOp(0, 0);
      }
      else if (paramInt < 700)
      {
        k = 1;
        paramTabbedPrintWriter.startOp(2, 1);
      }
      paramTabbedPrintWriter.print(str);
      paramTabbedPrintWriter.breakOp();
      paramTabbedPrintWriter.print(" ");
      paramInt = 700;
    }
    int n = getPriority();
    if (n < paramInt)
    {
      j = 1;
      m = 1;
      paramTabbedPrintWriter.print("(");
      paramTabbedPrintWriter.startOp(0, getBreakPenalty());
    }
    else if (n != paramInt)
    {
      m = 1;
      if (getType() == Type.tVoid) {
        paramTabbedPrintWriter.startOp(1, getBreakPenalty());
      } else {
        paramTabbedPrintWriter.startOp(2, 1 + getBreakPenalty());
      }
    }
    try
    {
      dumpExpression(paramTabbedPrintWriter);
    }
    catch (RuntimeException localRuntimeException)
    {
      paramTabbedPrintWriter.print("(RUNTIME ERROR IN EXPRESSION)");
      localRuntimeException.printStackTrace(GlobalOptions.err);
    }
    if (m != 0)
    {
      paramTabbedPrintWriter.endOp();
      if (j != 0) {
        paramTabbedPrintWriter.print(")");
      }
    }
    if (k != 0)
    {
      paramTabbedPrintWriter.endOp();
      if (i != 0) {
        paramTabbedPrintWriter.print(")");
      }
    }
  }
  
  public String toString()
  {
    try
    {
      StringWriter localStringWriter = new StringWriter();
      TabbedPrintWriter localTabbedPrintWriter = new TabbedPrintWriter(localStringWriter);
      dumpExpression(localTabbedPrintWriter);
      return localStringWriter.toString();
    }
    catch (IOException localIOException)
    {
      return "/*IOException*/" + super.toString();
    }
    catch (RuntimeException localRuntimeException) {}
    return "/*RuntimeException*/" + super.toString();
  }
  
  public boolean isVoid()
  {
    return getType() == Type.tVoid;
  }
}


