package com.strobel.collections.concurrent;

import com.strobel.annotations.NotNull;
import com.strobel.annotations.Nullable;

public abstract interface ConcurrentIntObjectMap<V>
{
  @NotNull
  public abstract V addOrGet(int paramInt, @NotNull V paramV);
  
  public abstract boolean remove(int paramInt, @NotNull V paramV);
  
  public abstract boolean replace(int paramInt, @NotNull V paramV1, @NotNull V paramV2);
  
  @Nullable
  public abstract V put(int paramInt, @NotNull V paramV);
  
  public abstract V putIfAbsent(int paramInt, @NotNull V paramV);
  
  @Nullable
  public abstract V get(int paramInt);
  
  @Nullable
  public abstract V remove(int paramInt);
  
  public abstract int size();
  
  public abstract boolean isEmpty();
  
  public abstract boolean contains(int paramInt);
  
  public abstract void clear();
  
  @NotNull
  public abstract int[] keys();
  
  @NotNull
  public abstract Iterable<IntObjectEntry<V>> entries();
}


