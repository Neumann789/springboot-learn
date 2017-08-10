package jode.flow;

import jode.AssertError;
import jode.decompiler.LocalInfo;
import jode.expr.Expression;
import jode.expr.LocalLoadOperator;
import jode.type.Type;

public class VariableStack
{
  public static final VariableStack EMPTY = new VariableStack();
  final LocalInfo[] stackMap;
  
  private VariableStack()
  {
    this.stackMap = new LocalInfo[0];
  }
  
  private VariableStack(LocalInfo[] paramArrayOfLocalInfo)
  {
    this.stackMap = paramArrayOfLocalInfo;
  }
  
  public boolean isEmpty()
  {
    return this.stackMap.length == 0;
  }
  
  public VariableStack pop(int paramInt)
  {
    LocalInfo[] arrayOfLocalInfo = new LocalInfo[this.stackMap.length - paramInt];
    System.arraycopy(this.stackMap, 0, arrayOfLocalInfo, 0, this.stackMap.length - paramInt);
    return new VariableStack(arrayOfLocalInfo);
  }
  
  public VariableStack push(LocalInfo paramLocalInfo)
  {
    return poppush(0, paramLocalInfo);
  }
  
  public VariableStack poppush(int paramInt, LocalInfo paramLocalInfo)
  {
    LocalInfo[] arrayOfLocalInfo = new LocalInfo[this.stackMap.length - paramInt + 1];
    System.arraycopy(this.stackMap, 0, arrayOfLocalInfo, 0, this.stackMap.length - paramInt);
    arrayOfLocalInfo[(this.stackMap.length - paramInt)] = paramLocalInfo;
    return new VariableStack(arrayOfLocalInfo);
  }
  
  public VariableStack peek(int paramInt)
  {
    LocalInfo[] arrayOfLocalInfo = new LocalInfo[paramInt];
    System.arraycopy(this.stackMap, this.stackMap.length - paramInt, arrayOfLocalInfo, 0, paramInt);
    return new VariableStack(arrayOfLocalInfo);
  }
  
  public void merge(VariableStack paramVariableStack)
  {
    if (this.stackMap.length != paramVariableStack.stackMap.length) {
      throw new IllegalArgumentException("stack length differs");
    }
    for (int i = 0; i < this.stackMap.length; i++)
    {
      if (this.stackMap[i].getType().stackSize() != paramVariableStack.stackMap[i].getType().stackSize()) {
        throw new IllegalArgumentException("stack element length differs at " + i);
      }
      this.stackMap[i].combineWith(paramVariableStack.stackMap[i]);
    }
  }
  
  public static VariableStack merge(VariableStack paramVariableStack1, VariableStack paramVariableStack2)
  {
    if (paramVariableStack1 == null) {
      return paramVariableStack2;
    }
    if (paramVariableStack2 == null) {
      return paramVariableStack1;
    }
    paramVariableStack1.merge(paramVariableStack2);
    return paramVariableStack1;
  }
  
  public Expression mergeIntoExpression(Expression paramExpression)
  {
    for (int i = this.stackMap.length - 1; i >= 0; i--) {
      paramExpression = paramExpression.addOperand(new LocalLoadOperator(this.stackMap[i].getType(), null, this.stackMap[i]));
    }
    return paramExpression;
  }
  
  public VariableStack executeSpecial(SpecialBlock paramSpecialBlock)
  {
    int i;
    int j;
    if (paramSpecialBlock.type == SpecialBlock.POP)
    {
      i = 0;
      j = this.stackMap.length;
      while (i < paramSpecialBlock.count)
      {
        j--;
        i += this.stackMap[j].getType().stackSize();
      }
      if (i != paramSpecialBlock.count) {
        throw new IllegalArgumentException("wrong POP");
      }
      LocalInfo[] arrayOfLocalInfo2 = new LocalInfo[j];
      System.arraycopy(this.stackMap, 0, arrayOfLocalInfo2, 0, j);
      return new VariableStack(arrayOfLocalInfo2);
    }
    if (paramSpecialBlock.type == SpecialBlock.DUP)
    {
      i = 0;
      j = 0;
      int k = this.stackMap.length;
      while (i < paramSpecialBlock.count)
      {
        k--;
        j++;
        i += this.stackMap[k].getType().stackSize();
      }
      if (i != paramSpecialBlock.count) {
        throw new IllegalArgumentException("wrong DUP");
      }
      int m = k;
      int n = 0;
      while (n < paramSpecialBlock.depth)
      {
        m--;
        n += this.stackMap[m].getType().stackSize();
      }
      if (n != paramSpecialBlock.depth) {
        throw new IllegalArgumentException("wrong DUP");
      }
      LocalInfo[] arrayOfLocalInfo3 = new LocalInfo[this.stackMap.length + j];
      System.arraycopy(this.stackMap, 0, arrayOfLocalInfo3, 0, m);
      System.arraycopy(this.stackMap, k, arrayOfLocalInfo3, m, j);
      System.arraycopy(this.stackMap, m, arrayOfLocalInfo3, m + j, k - m);
      System.arraycopy(this.stackMap, k, arrayOfLocalInfo3, k + j, j);
      return new VariableStack(arrayOfLocalInfo3);
    }
    if (paramSpecialBlock.type == SpecialBlock.SWAP)
    {
      LocalInfo[] arrayOfLocalInfo1 = new LocalInfo[this.stackMap.length];
      System.arraycopy(this.stackMap, 0, arrayOfLocalInfo1, 0, this.stackMap.length - 2);
      if ((this.stackMap[(this.stackMap.length - 2)].getType().stackSize() != 1) || (this.stackMap[(this.stackMap.length - 1)].getType().stackSize() != 1)) {
        throw new IllegalArgumentException("wrong SWAP");
      }
      arrayOfLocalInfo1[(this.stackMap.length - 2)] = this.stackMap[(this.stackMap.length - 1)];
      arrayOfLocalInfo1[(this.stackMap.length - 1)] = this.stackMap[(this.stackMap.length - 2)];
      return new VariableStack(arrayOfLocalInfo1);
    }
    throw new AssertError("Unknown SpecialBlock");
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("[");
    for (int i = 0; i < this.stackMap.length; i++)
    {
      if (i > 0) {
        localStringBuffer.append(", ");
      }
      localStringBuffer.append(this.stackMap[i].getName());
    }
    return "]";
  }
}


