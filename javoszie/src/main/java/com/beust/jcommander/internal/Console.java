package com.beust.jcommander.internal;

public abstract interface Console
{
  public abstract void print(String paramString);
  
  public abstract void println(String paramString);
  
  public abstract char[] readPassword(boolean paramBoolean);
}


