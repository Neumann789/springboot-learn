package com.strobel.collections.concurrent;

import com.strobel.annotations.NotNull;

public abstract interface IntObjectEntry<V>
{
  public abstract int key();
  
  @NotNull
  public abstract V value();
}


