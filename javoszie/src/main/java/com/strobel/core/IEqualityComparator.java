package com.strobel.core;

public abstract interface IEqualityComparator<T>
{
  public abstract boolean equals(T paramT1, T paramT2);
  
  public abstract int hash(T paramT);
}


