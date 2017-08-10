 package com.strobel.core;
 
 import com.strobel.util.ContractUtils;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Aggregate
 {
   private Aggregate()
   {
     throw ContractUtils.unreachable();
   }
   
 
 
   public static <TSource, TAccumulate> TAccumulate aggregate(Iterable<TSource> source, Accumulator<TSource, TAccumulate> accumulator)
   {
     return (TAccumulate)aggregate(source, null, accumulator);
   }
   
 
 
 
   public static <TSource, TAccumulate> TAccumulate aggregate(Iterable<TSource> source, TAccumulate seed, Accumulator<TSource, TAccumulate> accumulator)
   {
     VerifyArgument.notNull(source, "source");
     VerifyArgument.notNull(accumulator, "accumulator");
     
     TAccumulate accumulate = seed;
     
     for (TSource item : source) {
       accumulate = accumulator.accumulate(accumulate, item);
     }
     
     return accumulate;
   }
   
 
 
 
   public static <TSource, TAccumulate, TResult> TResult aggregate(Iterable<TSource> source, Accumulator<TSource, TAccumulate> accumulator, Selector<TAccumulate, TResult> resultSelector)
   {
     return (TResult)aggregate(source, null, accumulator, resultSelector);
   }
   
 
 
 
 
   public static <TSource, TAccumulate, TResult> TResult aggregate(Iterable<TSource> source, TAccumulate seed, Accumulator<TSource, TAccumulate> accumulator, Selector<TAccumulate, TResult> resultSelector)
   {
     VerifyArgument.notNull(source, "source");
     VerifyArgument.notNull(accumulator, "accumulator");
     VerifyArgument.notNull(resultSelector, "resultSelector");
     
     TAccumulate accumulate = seed;
     
     for (TSource item : source) {
       accumulate = accumulator.accumulate(accumulate, item);
     }
     
     return (TResult)resultSelector.select(accumulate);
   }
 }


