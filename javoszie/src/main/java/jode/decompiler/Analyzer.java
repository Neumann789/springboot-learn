package jode.decompiler;

import java.io.IOException;

public abstract interface Analyzer
{
  public abstract void analyze();
  
  public abstract void dumpSource(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException;
}


