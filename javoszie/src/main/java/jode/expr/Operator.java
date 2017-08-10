package jode.expr;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Set;
import jode.AssertError;
import jode.GlobalOptions;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public abstract class Operator
  extends Expression
{
  public static final int ADD_OP = 1;
  public static final int SUB_OP = 2;
  public static final int SHIFT_OP = 6;
  public static final int AND_OP = 9;
  public static final int ASSIGN_OP = 12;
  public static final int OPASSIGN_OP = 12;
  public static final int INC_OP = 24;
  public static final int DEC_OP = 25;
  public static final int COMPARE_OP = 26;
  public static final int EQUALS_OP = 26;
  public static final int NOTEQUALS_OP = 27;
  public static final int LESS_OP = 28;
  public static final int GREATEREQ_OP = 29;
  public static final int GREATER_OP = 30;
  public static final int LESSEQ_OP = 31;
  public static final int LOG_AND_OP = 32;
  public static final int LOG_OR_OP = 33;
  public static final int LOG_NOT_OP = 34;
  public static final int NEG_OP = 36;
  static String[] opString = { "", " + ", " - ", " * ", " / ", " % ", " << ", " >> ", " >>> ", " & ", " | ", " ^ ", " = ", " += ", " -= ", " *= ", " /= ", " %= ", " <<= ", " >>= ", " >>>= ", " &= ", " |= ", " ^= ", "++", "--", " == ", " != ", " < ", " >= ", " > ", " <= ", " && ", " || ", "!", "~", "-" };
  protected int operatorIndex;
  private int operandcount;
  Expression[] subExpressions;
  
  public Operator(Type paramType)
  {
    this(paramType, 0);
  }
  
  public Operator(Type paramType, int paramInt)
  {
    super(paramType);
    this.operatorIndex = paramInt;
    if (paramType == null) {
      throw new AssertError("type == null");
    }
  }
  
  public void initOperands(int paramInt)
  {
    this.operandcount = paramInt;
    this.subExpressions = new Expression[paramInt];
    for (int i = 0; i < paramInt; i++)
    {
      this.subExpressions[i] = new NopOperator(Type.tUnknown);
      this.subExpressions[i].parent = this;
    }
    updateSubTypes();
  }
  
  public int getFreeOperandCount()
  {
    return this.operandcount;
  }
  
  public boolean isFreeOperator()
  {
    return (this.subExpressions.length == 0) || ((this.subExpressions[(this.subExpressions.length - 1)] instanceof NopOperator));
  }
  
  public boolean isFreeOperator(int paramInt)
  {
    return (this.subExpressions.length == paramInt) && ((paramInt == 0) || ((this.subExpressions[(paramInt - 1)] instanceof NopOperator)));
  }
  
  public Expression addOperand(Expression paramExpression)
  {
    int i = this.subExpressions.length;
    while (i-- > 0)
    {
      int j = this.subExpressions[i].getFreeOperandCount();
      if (j > 0)
      {
        this.subExpressions[i] = this.subExpressions[i].addOperand(paramExpression);
        this.operandcount += this.subExpressions[i].getFreeOperandCount() - j;
        updateType();
        return this;
      }
    }
    GlobalOptions.err.println("addOperand called, but no operand needed " + paramExpression.getType().toString() + " " + paramExpression.getFreeOperandCount() + " " + paramExpression.toString());
    updateType();
    return this;
  }
  
  public Operator getOperator()
  {
    return this;
  }
  
  public Expression[] getSubExpressions()
  {
    return this.subExpressions;
  }
  
  public void setSubExpressions(int paramInt, Expression paramExpression)
  {
    int i = paramExpression.getFreeOperandCount() - this.subExpressions[paramInt].getFreeOperandCount();
    this.subExpressions[paramInt] = paramExpression;
    paramExpression.parent = this;
    for (Operator localOperator = this; localOperator != null; localOperator = localOperator.parent) {
      localOperator.operandcount += i;
    }
    updateType();
  }
  
  public int getOperatorIndex()
  {
    return this.operatorIndex;
  }
  
  public void setOperatorIndex(int paramInt)
  {
    this.operatorIndex = paramInt;
  }
  
  public String getOperatorString()
  {
    return opString[this.operatorIndex];
  }
  
  public boolean opEquals(Operator paramOperator)
  {
    return this == paramOperator;
  }
  
  public Expression simplify()
  {
    for (int i = 0; i < this.subExpressions.length; i++)
    {
      this.subExpressions[i] = this.subExpressions[i].simplify();
      this.subExpressions[i].parent = this;
    }
    return this;
  }
  
  public void fillInGenSet(Collection paramCollection1, Collection paramCollection2)
  {
    if ((this instanceof LocalVarOperator))
    {
      LocalVarOperator localLocalVarOperator = (LocalVarOperator)this;
      if ((localLocalVarOperator.isRead()) && (paramCollection1 != null)) {
        paramCollection1.add(localLocalVarOperator.getLocalInfo());
      }
      if (paramCollection2 != null) {
        paramCollection2.add(localLocalVarOperator.getLocalInfo());
      }
    }
    for (int i = 0; i < this.subExpressions.length; i++) {
      this.subExpressions[i].fillInGenSet(paramCollection1, paramCollection2);
    }
  }
  
  public void fillDeclarables(Collection paramCollection)
  {
    for (int i = 0; i < this.subExpressions.length; i++) {
      this.subExpressions[i].fillDeclarables(paramCollection);
    }
  }
  
  public void makeDeclaration(Set paramSet)
  {
    for (int i = 0; i < this.subExpressions.length; i++) {
      this.subExpressions[i].makeDeclaration(paramSet);
    }
  }
  
  public boolean hasSideEffects(Expression paramExpression)
  {
    if (((paramExpression instanceof MatchableOperator)) && (paramExpression.containsConflictingLoad((MatchableOperator)paramExpression))) {
      return true;
    }
    for (int i = 0; i < this.subExpressions.length; i++) {
      if (this.subExpressions[i].hasSideEffects(paramExpression)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean containsConflictingLoad(MatchableOperator paramMatchableOperator)
  {
    if (paramMatchableOperator.matches(this)) {
      return true;
    }
    for (int i = 0; i < this.subExpressions.length; i++) {
      if (this.subExpressions[i].containsConflictingLoad(paramMatchableOperator)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean containsMatchingLoad(CombineableOperator paramCombineableOperator)
  {
    Operator localOperator = (Operator)paramCombineableOperator;
    if ((paramCombineableOperator.getLValue().matches(this)) && (subsEquals((Operator)paramCombineableOperator.getLValue()))) {
      return true;
    }
    for (int i = 0; i < this.subExpressions.length; i++) {
      if (this.subExpressions[i].containsMatchingLoad(paramCombineableOperator)) {
        return true;
      }
    }
    return false;
  }
  
  public int canCombine(CombineableOperator paramCombineableOperator)
  {
    if (((paramCombineableOperator.getLValue() instanceof LocalStoreOperator)) && (((Operator)paramCombineableOperator).getFreeOperandCount() == 0)) {
      for (int i = 0; i < this.subExpressions.length; i++)
      {
        int j = this.subExpressions[i].canCombine(paramCombineableOperator);
        if (j != 0) {
          return j;
        }
        if (this.subExpressions[i].hasSideEffects((Expression)paramCombineableOperator)) {
          return -1;
        }
      }
    }
    if (paramCombineableOperator.lvalueMatches(this)) {
      return subsEquals((Operator)paramCombineableOperator) ? 1 : -1;
    }
    if (this.subExpressions.length > 0) {
      return this.subExpressions[0].canCombine(paramCombineableOperator);
    }
    return 0;
  }
  
  public Expression combine(CombineableOperator paramCombineableOperator)
  {
    Operator localOperator = (Operator)paramCombineableOperator;
    if (paramCombineableOperator.lvalueMatches(this))
    {
      paramCombineableOperator.makeNonVoid();
      localOperator.parent = this.parent;
      return localOperator;
    }
    for (int i = 0; i < this.subExpressions.length; i++)
    {
      Expression localExpression = this.subExpressions[i].combine(paramCombineableOperator);
      if (localExpression != null)
      {
        this.subExpressions[i] = localExpression;
        updateType();
        return this;
      }
    }
    return null;
  }
  
  public boolean subsEquals(Operator paramOperator)
  {
    if (this == paramOperator) {
      return true;
    }
    if (paramOperator.subExpressions == null) {
      return this.subExpressions == null;
    }
    if (this.subExpressions.length != paramOperator.subExpressions.length) {
      return false;
    }
    for (int i = 0; i < this.subExpressions.length; i++) {
      if (!this.subExpressions[i].equals(paramOperator.subExpressions[i])) {
        return false;
      }
    }
    return true;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Operator)) {
      return false;
    }
    Operator localOperator = (Operator)paramObject;
    return (opEquals(localOperator)) && (subsEquals(localOperator));
  }
  
  public boolean isConstant()
  {
    for (int i = 0; i < this.subExpressions.length; i++) {
      if (!this.subExpressions[i].isConstant()) {
        return false;
      }
    }
    return true;
  }
  
  public abstract void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException;
}


