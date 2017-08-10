package com.strobel.core;

public abstract interface SafeCloseable
  extends AutoCloseable
{
  public abstract void close();
}


