package com.strobel.core;

public abstract interface Aggregator<TSource, TAccumulate, TResult>
{
  public abstract TResult aggregate(TSource paramTSource, TAccumulate paramTAccumulate, Accumulator<TSource, TAccumulate> paramAccumulator, Selector<TAccumulate, TResult> paramSelector);
}


