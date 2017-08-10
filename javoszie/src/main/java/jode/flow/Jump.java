package jode.flow;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;

public class Jump
{
  StructuredBlock prev;
  FlowBlock destination;
  Jump next;
  VariableStack stackMap;
  
  public Jump(FlowBlock paramFlowBlock)
  {
    this.destination = paramFlowBlock;
  }
  
  public Jump(Jump paramJump)
  {
    this.destination = paramJump.destination;
    this.next = paramJump.next;
    paramJump.next = this;
  }
  
  public void dumpSource(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    if (this.destination == null) {
      paramTabbedPrintWriter.println("GOTO null-ptr!!!!!");
    } else {
      paramTabbedPrintWriter.println("GOTO " + this.destination.getLabel());
    }
  }
}


