package jode.expr;

import java.io.IOException;
import java.util.Collection;
import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class CheckNullOperator
  extends Operator
{
  LocalInfo local;
  
  public CheckNullOperator(Type paramType, LocalInfo paramLocalInfo)
  {
    super(paramType, 0);
    this.local = paramLocalInfo;
    initOperands(1);
  }
  
  public int getPriority()
  {
    return 200;
  }
  
  public void updateSubTypes()
  {
    this.local.setType(this.type);
    this.subExpressions[0].setType(Type.tSubType(this.type));
  }
  
  public void updateType()
  {
    Type localType = Type.tSuperType(this.subExpressions[0].getType()).intersection(this.type);
    this.local.setType(localType);
    updateParentType(localType);
  }
  
  public void removeLocal()
  {
    this.local.remove();
  }
  
  public void fillInGenSet(Collection paramCollection1, Collection paramCollection2)
  {
    if (paramCollection2 != null) {
      paramCollection2.add(this.local);
    }
    super.fillInGenSet(paramCollection1, paramCollection2);
  }
  
  public void fillDeclarables(Collection paramCollection)
  {
    paramCollection.add(this.local);
    super.fillDeclarables(paramCollection);
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.print("(" + this.local.getName() + " = ");
    this.subExpressions[0].dumpExpression(paramTabbedPrintWriter, 0);
    paramTabbedPrintWriter.print(").getClass() != null ? " + this.local.getName() + " : null");
  }
  
  public boolean opEquals(Operator paramOperator)
  {
    return paramOperator instanceof CheckNullOperator;
  }
}


