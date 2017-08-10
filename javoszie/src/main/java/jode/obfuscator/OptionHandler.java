package jode.obfuscator;

import java.util.Collection;

public abstract interface OptionHandler
{
  public abstract void setOption(String paramString, Collection paramCollection)
    throws IllegalArgumentException;
}


