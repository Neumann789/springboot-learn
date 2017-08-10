package jode.flow;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;

public class RetBlock
  extends StructuredBlock
{
  LocalInfo local;
  
  public RetBlock(LocalInfo paramLocalInfo)
  {
    this.local = paramLocalInfo;
  }
  
  public void fillInGenSet(Set paramSet1, Set paramSet2)
  {
    paramSet1.add(this.local);
    paramSet2.add(this.local);
  }
  
  public VariableStack mapStackToLocal(VariableStack paramVariableStack)
  {
    if (!paramVariableStack.isEmpty()) {
      throw new IllegalArgumentException("stack is not empty at RET");
    }
    return null;
  }
  
  public Set getDeclarables()
  {
    return Collections.singleton(this.local);
  }
  
  public void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.println("RET " + this.local);
  }
}


