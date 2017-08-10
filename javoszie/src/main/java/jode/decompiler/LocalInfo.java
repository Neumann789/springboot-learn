package jode.decompiler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;
import jode.AssertError;
import jode.GlobalOptions;
import jode.expr.Expression;
import jode.expr.LocalVarOperator;
import jode.type.Type;

public class LocalInfo
  implements Declarable
{
  private static int serialnr = 0;
  private static int nextAnonymousSlot = -1;
  private int slot;
  private MethodAnalyzer methodAnalyzer;
  private boolean nameIsGenerated = false;
  private boolean isUnique;
  private String name = null;
  private Type type = Type.tUnknown;
  private LocalInfo shadow;
  private Vector operators = new Vector();
  private Vector hints = new Vector();
  private boolean removed = false;
  private boolean isFinal = false;
  private Expression constExpr = null;
  private int loopCount = 0;
  
  public LocalInfo()
  {
    this.slot = (nextAnonymousSlot--);
  }
  
  public LocalInfo(MethodAnalyzer paramMethodAnalyzer, int paramInt)
  {
    this.methodAnalyzer = paramMethodAnalyzer;
    this.slot = paramInt;
  }
  
  public static void init()
  {
    serialnr = 0;
  }
  
  public void setOperator(LocalVarOperator paramLocalVarOperator)
  {
    getLocalInfo().operators.addElement(paramLocalVarOperator);
  }
  
  public void addHint(String paramString, Type paramType)
  {
    getLocalInfo().hints.addElement(new Hint(paramString, paramType));
  }
  
  public int getUseCount()
  {
    return getLocalInfo().operators.size();
  }
  
  public void combineWith(LocalInfo paramLocalInfo)
  {
    if (this.shadow != null)
    {
      getLocalInfo().combineWith(paramLocalInfo);
      return;
    }
    paramLocalInfo = paramLocalInfo.getLocalInfo();
    if (this == paramLocalInfo) {
      return;
    }
    this.shadow = paramLocalInfo;
    if (!this.nameIsGenerated) {
      paramLocalInfo.name = this.name;
    }
    if (this.constExpr != null)
    {
      if (paramLocalInfo.constExpr != null) {
        throw new AssertError("local has multiple constExpr");
      }
      paramLocalInfo.constExpr = this.constExpr;
    }
    paramLocalInfo.setType(this.type);
    int i = !paramLocalInfo.type.equals(this.type) ? 1 : 0;
    Enumeration localEnumeration = this.operators.elements();
    Object localObject;
    while (localEnumeration.hasMoreElements())
    {
      localObject = (LocalVarOperator)localEnumeration.nextElement();
      if (i != 0)
      {
        if ((GlobalOptions.debuggingFlags & 0x4) != 0) {
          GlobalOptions.err.println("updating " + localObject);
        }
        ((LocalVarOperator)localObject).updateType();
      }
      paramLocalInfo.operators.addElement(localObject);
    }
    localEnumeration = this.hints.elements();
    while (localEnumeration.hasMoreElements())
    {
      localObject = localEnumeration.nextElement();
      if (!paramLocalInfo.hints.contains(localObject)) {
        paramLocalInfo.hints.addElement(localObject);
      }
    }
    this.type = null;
    this.name = null;
    this.operators = null;
  }
  
  public LocalInfo getLocalInfo()
  {
    if (this.shadow != null)
    {
      while (this.shadow.shadow != null) {
        this.shadow = this.shadow.shadow;
      }
      return this.shadow;
    }
    return this;
  }
  
  public boolean hasName()
  {
    return getLocalInfo().name != null;
  }
  
  public String guessName()
  {
    if (this.shadow != null)
    {
      while (this.shadow.shadow != null) {
        this.shadow = this.shadow.shadow;
      }
      return this.shadow.guessName();
    }
    if (this.name == null)
    {
      Enumeration localEnumeration = this.hints.elements();
      while (localEnumeration.hasMoreElements())
      {
        Hint localHint = (Hint)localEnumeration.nextElement();
        if (this.type.isOfType(localHint.getType()))
        {
          this.name = localHint.getName();
          setType(localHint.getType());
          return this.name;
        }
      }
      this.nameIsGenerated = true;
      if ((GlobalOptions.debuggingFlags & 0x4) != 0) {
        GlobalOptions.err.println(getName() + " set type to getHint()");
      }
      setType(this.type.getHint());
      if ((Options.options & 0x10) != 0)
      {
        this.name = this.type.getDefaultName();
      }
      else
      {
        this.name = (this.type.getDefaultName() + (this.slot >= 0 ? "_" + this.slot : "") + "_" + serialnr++ + "_");
        this.isUnique = true;
      }
      if ((GlobalOptions.debuggingFlags & 0x100) != 0)
      {
        GlobalOptions.err.println("Guessed name: " + this.name + " from type: " + this.type);
        Thread.dumpStack();
      }
    }
    return this.name;
  }
  
  public String getName()
  {
    if (this.shadow != null)
    {
      while (this.shadow.shadow != null) {
        this.shadow = this.shadow.shadow;
      }
      return this.shadow.getName();
    }
    if (this.name == null) {
      return "local_" + this.slot + "_" + Integer.toHexString(hashCode());
    }
    return this.name;
  }
  
  public boolean isNameGenerated()
  {
    return getLocalInfo().nameIsGenerated;
  }
  
  public int getSlot()
  {
    return getLocalInfo().slot;
  }
  
  public void setName(String paramString)
  {
    LocalInfo localLocalInfo = getLocalInfo();
    localLocalInfo.name = paramString;
  }
  
  public void makeNameUnique()
  {
    LocalInfo localLocalInfo = getLocalInfo();
    String str = localLocalInfo.getName();
    if (!localLocalInfo.isUnique)
    {
      localLocalInfo.name = (str + "_" + serialnr++ + "_");
      localLocalInfo.isUnique = true;
    }
  }
  
  public Type getType()
  {
    return getLocalInfo().type;
  }
  
  public Type setType(Type paramType)
  {
    LocalInfo localLocalInfo = getLocalInfo();
    if (localLocalInfo.loopCount++ > 5)
    {
      GlobalOptions.err.println("Type error in local " + getName() + ": " + localLocalInfo.type + " seems to be recursive.");
      Thread.dumpStack();
      paramType = Type.tError;
    }
    Type localType = localLocalInfo.type.intersection(paramType);
    if ((localType == Type.tError) && (paramType != Type.tError) && (localLocalInfo.type != Type.tError))
    {
      GlobalOptions.err.println("Type error in local " + getName() + ": " + localLocalInfo.type + " and " + paramType);
      Thread.dumpStack();
    }
    else if ((GlobalOptions.debuggingFlags & 0x4) != 0)
    {
      GlobalOptions.err.println(getName() + " setType, new: " + localType + " old: " + localLocalInfo.type);
    }
    if (!localLocalInfo.type.equals(localType))
    {
      localLocalInfo.type = localType;
      Enumeration localEnumeration = localLocalInfo.operators.elements();
      while (localEnumeration.hasMoreElements())
      {
        LocalVarOperator localLocalVarOperator = (LocalVarOperator)localEnumeration.nextElement();
        if ((GlobalOptions.debuggingFlags & 0x4) != 0) {
          GlobalOptions.err.println("updating " + localLocalVarOperator);
        }
        localLocalVarOperator.updateType();
      }
    }
    localLocalInfo.loopCount -= 1;
    return localLocalInfo.type;
  }
  
  public void setExpression(Expression paramExpression)
  {
    setType(paramExpression.getType());
    getLocalInfo().constExpr = paramExpression;
  }
  
  public Expression getExpression()
  {
    return getLocalInfo().constExpr;
  }
  
  public boolean isShadow()
  {
    return this.shadow != null;
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof LocalInfo)) && (((LocalInfo)paramObject).getLocalInfo() == getLocalInfo());
  }
  
  public void remove()
  {
    this.removed = true;
  }
  
  public boolean isRemoved()
  {
    return this.removed;
  }
  
  public boolean isConstant()
  {
    return true;
  }
  
  public MethodAnalyzer getMethodAnalyzer()
  {
    return this.methodAnalyzer;
  }
  
  public boolean markFinal()
  {
    LocalInfo localLocalInfo = getLocalInfo();
    Enumeration localEnumeration = localLocalInfo.operators.elements();
    int i = 0;
    while (localEnumeration.hasMoreElements()) {
      if (((LocalVarOperator)localEnumeration.nextElement()).isWrite()) {
        i++;
      }
    }
    localLocalInfo.isFinal = true;
    return true;
  }
  
  public boolean isFinal()
  {
    return getLocalInfo().isFinal;
  }
  
  public String toString()
  {
    return getName();
  }
  
  public void dumpDeclaration(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    LocalInfo localLocalInfo = getLocalInfo();
    if (localLocalInfo.isFinal) {
      paramTabbedPrintWriter.print("final ");
    }
    paramTabbedPrintWriter.printType(localLocalInfo.getType().getHint());
    paramTabbedPrintWriter.print(" " + localLocalInfo.getName().toString());
  }
  
  static class Hint
  {
    String name;
    Type type;
    
    public Hint(String paramString, Type paramType)
    {
      this.name = paramString;
      this.type = paramType;
    }
    
    public final Type getType()
    {
      return this.type;
    }
    
    public final String getName()
    {
      return this.name;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof Hint))
      {
        Hint localHint = (Hint)paramObject;
        return (this.name.equals(localHint.name)) && (this.type.equals(localHint.type));
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.name.hashCode() ^ this.type.hashCode();
    }
  }
}


