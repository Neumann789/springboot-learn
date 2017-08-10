package jode.decompiler;

import java.io.IOException;

public abstract interface Declarable
{
  public abstract String getName();
  
  public abstract void makeNameUnique();
  
  public abstract void dumpDeclaration(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException;
}


