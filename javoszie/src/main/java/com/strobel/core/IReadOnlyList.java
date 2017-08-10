package com.strobel.core;

import com.strobel.annotations.NotNull;
import java.util.ListIterator;
import java.util.RandomAccess;

public abstract interface IReadOnlyList<T>
  extends Iterable<T>, RandomAccess
{
  public abstract int size();
  
  public abstract <U extends T> int indexOf(U paramU);
  
  public abstract <U extends T> int lastIndexOf(U paramU);
  
  public abstract boolean isEmpty();
  
  public abstract <U extends T> boolean contains(U paramU);
  
  public abstract boolean containsAll(Iterable<? extends T> paramIterable);
  
  public abstract T get(int paramInt);
  
  @NotNull
  public abstract T[] toArray();
  
  @NotNull
  public abstract <T> T[] toArray(T[] paramArrayOfT);
  
  @NotNull
  public abstract ListIterator<T> listIterator();
  
  @NotNull
  public abstract ListIterator<T> listIterator(int paramInt);
}


