package jode.obfuscator;

import java.util.Iterator;

public abstract interface Renamer
{
  public abstract Iterator generateNames(Identifier paramIdentifier);
}


