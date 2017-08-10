package jode.expr;

import java.io.IOException;
import java.io.PrintWriter;
import jode.GlobalOptions;
import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;

public class OuterLocalOperator
  extends Operator
{
  LocalInfo local;
  
  public OuterLocalOperator(LocalInfo paramLocalInfo)
  {
    super(paramLocalInfo.getType());
    this.local = paramLocalInfo;
    initOperands(0);
  }
  
  public boolean isConstant()
  {
    return true;
  }
  
  public int getPriority()
  {
    return 1000;
  }
  
  public LocalInfo getLocalInfo()
  {
    return this.local.getLocalInfo();
  }
  
  public void updateSubTypes()
  {
    if ((GlobalOptions.debuggingFlags & 0x4) != 0) {
      GlobalOptions.err.println("setType of " + this.local.getName() + ": " + this.local.getType());
    }
    this.local.setType(this.type);
  }
  
  public void updateType() {}
  
  public boolean opEquals(Operator paramOperator)
  {
    return ((paramOperator instanceof OuterLocalOperator)) && (((OuterLocalOperator)paramOperator).local.getSlot() == this.local.getSlot());
  }
  
  public Expression simplify()
  {
    return super.simplify();
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.print(this.local.getName());
  }
}


