package com.strobel.core;

public abstract interface Accumulator<TSource, TAccumulate>
{
  public abstract TAccumulate accumulate(TAccumulate paramTAccumulate, TSource paramTSource);
}


