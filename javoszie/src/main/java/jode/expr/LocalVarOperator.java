package jode.expr;

import java.io.PrintWriter;
import java.util.Collection;
import jode.GlobalOptions;
import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public abstract class LocalVarOperator
  extends Operator
{
  LocalInfo local;
  
  public LocalVarOperator(Type paramType, LocalInfo paramLocalInfo)
  {
    super(paramType);
    this.local = paramLocalInfo;
    paramLocalInfo.setOperator(this);
    initOperands(0);
  }
  
  public abstract boolean isRead();
  
  public abstract boolean isWrite();
  
  public void updateSubTypes()
  {
    if ((this.parent != null) && ((GlobalOptions.debuggingFlags & 0x4) != 0)) {
      GlobalOptions.err.println("local type changed in: " + this.parent);
    }
    this.local.setType(this.type);
  }
  
  public void updateType()
  {
    updateParentType(this.local.getType());
  }
  
  public void fillDeclarables(Collection paramCollection)
  {
    paramCollection.add(this.local);
    super.fillDeclarables(paramCollection);
  }
  
  public LocalInfo getLocalInfo()
  {
    return this.local.getLocalInfo();
  }
  
  public void setLocalInfo(LocalInfo paramLocalInfo)
  {
    this.local = paramLocalInfo;
    updateType();
  }
  
  public int getPriority()
  {
    return 1000;
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
  {
    paramTabbedPrintWriter.print(this.local.getName());
  }
}


