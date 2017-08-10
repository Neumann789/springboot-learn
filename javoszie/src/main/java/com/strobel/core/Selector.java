package com.strobel.core;

public abstract interface Selector<TSource, TResult>
{
  public abstract TResult select(TSource paramTSource);
}


