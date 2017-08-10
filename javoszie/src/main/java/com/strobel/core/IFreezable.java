package com.strobel.core;

public abstract interface IFreezable
{
  public abstract boolean canFreeze();
  
  public abstract boolean isFrozen();
  
  public abstract void freeze()
    throws IllegalStateException;
  
  public abstract boolean tryFreeze();
  
  public abstract void freezeIfUnfrozen()
    throws IllegalStateException;
}


