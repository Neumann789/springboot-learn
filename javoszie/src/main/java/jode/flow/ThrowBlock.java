package jode.flow;

import java.io.IOException;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.Expression;

public class ThrowBlock
  extends ReturnBlock
{
  public ThrowBlock(Expression paramExpression)
  {
    super(paramExpression);
  }
  
  public void dumpInstruction(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.print("throw ");
    this.instr.dumpExpression(1, paramTabbedPrintWriter);
    paramTabbedPrintWriter.println(";");
  }
}


