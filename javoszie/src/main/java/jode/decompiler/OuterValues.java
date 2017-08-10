package jode.decompiler;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;
import jode.GlobalOptions;
import jode.expr.Expression;
import jode.expr.LocalLoadOperator;
import jode.expr.OuterLocalOperator;
import jode.expr.ThisOperator;
import jode.type.MethodType;
import jode.type.Type;

public class OuterValues
{
  private ClassAnalyzer clazzAnalyzer;
  private Expression[] head;
  private Vector ovListeners;
  private boolean jikesAnonymousInner;
  private boolean implicitOuterClass;
  private int headCount;
  private int headMinCount;
  
  public OuterValues(ClassAnalyzer paramClassAnalyzer, Expression[] paramArrayOfExpression)
  {
    this.clazzAnalyzer = paramClassAnalyzer;
    this.head = paramArrayOfExpression;
    this.headMinCount = 0;
    this.headCount = paramArrayOfExpression.length;
    if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
      GlobalOptions.err.println("Created OuterValues: " + this);
    }
  }
  
  public Expression getValue(int paramInt)
  {
    return this.head[paramInt];
  }
  
  public int getCount()
  {
    return this.headCount;
  }
  
  private int getNumberBySlot(int paramInt)
  {
    
    for (int i = 0; (paramInt >= 0) && (i < this.headCount); i++)
    {
      if (paramInt == 0) {
        return i;
      }
      paramInt -= this.head[i].getType().stackSize();
    }
    return -1;
  }
  
  public Expression getValueBySlot(int paramInt)
  {
    
    for (int i = 0; i < this.headCount; i++)
    {
      if (paramInt == 0)
      {
        Expression localExpression = this.head[i];
        if (i >= this.headMinCount) {
          this.headMinCount = i;
        }
        return localExpression;
      }
      paramInt -= this.head[i].getType().stackSize();
    }
    return null;
  }
  
  private Expression liftOuterValue(LocalInfo paramLocalInfo, final int paramInt)
  {
    MethodAnalyzer localMethodAnalyzer = paramLocalInfo.getMethodAnalyzer();
    if ((!localMethodAnalyzer.isConstructor()) || (localMethodAnalyzer.isStatic())) {
      return null;
    }
    OuterValues localOuterValues = localMethodAnalyzer.getClassAnalyzer().getOuterValues();
    if (localOuterValues == null) {
      return null;
    }
    int i = localOuterValues.getNumberBySlot(paramLocalInfo.getSlot());
    if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
      GlobalOptions.err.println("  ovNr " + i + "," + localOuterValues);
    }
    if ((i < 0) && (localOuterValues.getCount() >= 1) && (localOuterValues.isJikesAnonymousInner()))
    {
      Type[] arrayOfType = localMethodAnalyzer.getType().getParameterTypes();
      int k = 1;
      for (int m = 0; m < arrayOfType.length - 1; m++) {
        k += arrayOfType[m].stackSize();
      }
      if (paramLocalInfo.getSlot() == k) {
        i = 0;
      }
    }
    if (i < 0) {
      return null;
    }
    if ((localOuterValues != this) || (i > paramInt))
    {
      final int j = i;
      localOuterValues.addOuterValueListener(new OuterValueListener()
      {
        public void shrinkingOuterValues(OuterValues paramAnonymousOuterValues, int paramAnonymousInt)
        {
          if (paramAnonymousInt <= j) {
            OuterValues.this.setCount(paramInt);
          }
        }
      });
    }
    return localOuterValues.head[i];
  }
  
  public boolean unifyOuterValues(int paramInt, Expression paramExpression)
  {
    if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
      GlobalOptions.err.println("unifyOuterValues: " + this + "," + paramInt + "," + paramExpression);
    }
    Expression localExpression1 = paramExpression;
    Expression localExpression2 = this.head[paramInt];
    LocalInfo localLocalInfo1;
    if ((localExpression1 instanceof ThisOperator)) {
      localLocalInfo1 = null;
    } else if ((localExpression1 instanceof OuterLocalOperator)) {
      localLocalInfo1 = ((OuterLocalOperator)localExpression1).getLocalInfo();
    } else if ((localExpression1 instanceof LocalLoadOperator)) {
      localLocalInfo1 = ((LocalLoadOperator)localExpression1).getLocalInfo();
    } else {
      return false;
    }
    while ((localLocalInfo1 != null) && (!localLocalInfo1.getMethodAnalyzer().isMoreOuterThan(this.clazzAnalyzer)))
    {
      localExpression1 = liftOuterValue(localLocalInfo1, paramInt);
      if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
        GlobalOptions.err.println("  lift1 " + localLocalInfo1 + " in " + localLocalInfo1.getMethodAnalyzer() + "  to " + localExpression1);
      }
      if ((localExpression1 instanceof ThisOperator)) {
        localLocalInfo1 = null;
      } else if ((localExpression1 instanceof OuterLocalOperator)) {
        localLocalInfo1 = ((OuterLocalOperator)localExpression1).getLocalInfo();
      } else {
        return false;
      }
    }
    while (!localExpression1.equals(localExpression2)) {
      if ((localExpression2 instanceof OuterLocalOperator))
      {
        LocalInfo localLocalInfo2 = ((OuterLocalOperator)localExpression2).getLocalInfo();
        if (!localLocalInfo2.equals(localLocalInfo1))
        {
          localExpression2 = liftOuterValue(localLocalInfo2, paramInt);
          if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
            GlobalOptions.err.println("  lift2 " + localLocalInfo2 + " in " + localLocalInfo2.getMethodAnalyzer() + "  to " + localExpression2);
          }
        }
      }
      else
      {
        return false;
      }
    }
    if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
      GlobalOptions.err.println("unifyOuterValues succeeded.");
    }
    return true;
  }
  
  public boolean isJikesAnonymousInner()
  {
    return this.jikesAnonymousInner;
  }
  
  public boolean isImplicitOuterClass()
  {
    return this.implicitOuterClass;
  }
  
  public void addOuterValueListener(OuterValueListener paramOuterValueListener)
  {
    if (this.ovListeners == null) {
      this.ovListeners = new Vector();
    }
    this.ovListeners.addElement(paramOuterValueListener);
  }
  
  public void setJikesAnonymousInner(boolean paramBoolean)
  {
    this.jikesAnonymousInner = paramBoolean;
  }
  
  public void setImplicitOuterClass(boolean paramBoolean)
  {
    this.implicitOuterClass = paramBoolean;
  }
  
  private static int countSlots(Expression[] paramArrayOfExpression, int paramInt)
  {
    int i = 0;
    for (int j = 0; j < paramInt; j++) {
      i += paramArrayOfExpression[j].getType().stackSize();
    }
    return i;
  }
  
  public void setMinCount(int paramInt)
  {
    if (this.headCount < paramInt)
    {
      GlobalOptions.err.println("WARNING: something got wrong with scoped class " + this.clazzAnalyzer.getClazz() + ": " + paramInt + "," + this.headCount);
      new Throwable().printStackTrace(GlobalOptions.err);
      this.headMinCount = this.headCount;
    }
    else if (paramInt > this.headMinCount)
    {
      this.headMinCount = paramInt;
    }
  }
  
  public void setCount(int paramInt)
  {
    if (paramInt >= this.headCount) {
      return;
    }
    this.headCount = paramInt;
    if ((GlobalOptions.debuggingFlags & 0x200) != 0)
    {
      GlobalOptions.err.println("setCount: " + this + "," + paramInt);
      new Throwable().printStackTrace(GlobalOptions.err);
    }
    if (paramInt < this.headMinCount)
    {
      GlobalOptions.err.println("WARNING: something got wrong with scoped class " + this.clazzAnalyzer.getClazz() + ": " + this.headMinCount + "," + this.headCount);
      new Throwable().printStackTrace(GlobalOptions.err);
      this.headMinCount = paramInt;
    }
    if (this.ovListeners != null)
    {
      Enumeration localEnumeration = this.ovListeners.elements();
      while (localEnumeration.hasMoreElements()) {
        ((OuterValueListener)localEnumeration.nextElement()).shrinkingOuterValues(this, paramInt);
      }
    }
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer().append(this.clazzAnalyzer.getClazz()).append(".OuterValues[");
    String str = "";
    int i = 1;
    for (int j = 0; j < this.headCount; j++)
    {
      if (j == this.headMinCount) {
        localStringBuffer.append("<-");
      }
      localStringBuffer.append(str).append(i).append(":").append(this.head[j]);
      i += this.head[j].getType().stackSize();
      str = ",";
    }
    if (this.jikesAnonymousInner) {
      localStringBuffer.append("!jikesAnonymousInner");
    }
    if (this.implicitOuterClass) {
      localStringBuffer.append("!implicitOuterClass");
    }
    return "]";
  }
}


