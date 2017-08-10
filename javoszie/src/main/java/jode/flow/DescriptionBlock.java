package jode.flow;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;

public class DescriptionBlock
  extends StructuredBlock
{
  String description;
  
  public DescriptionBlock(String paramString)
  {
    this.description = paramString;
  }
  
  public boolean isEmpty()
  {
    return true;
  }
  
  public void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.println(this.description);
  }
}


