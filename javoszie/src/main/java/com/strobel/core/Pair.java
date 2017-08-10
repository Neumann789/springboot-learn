 package com.strobel.core;
 
 
 
 
 public final class Pair<TFirst, TSecond>
   implements Comparable<Pair<TFirst, TSecond>>
 {
   private static final int UninitializedHashCode = Integer.MIN_VALUE;
   
 
 
   private static final int FirstNullHash = 1642088727;
   
 
 
   private static final int SecondNullHash = 428791459;
   
 
 
   private final TFirst _first;
   
 
 
   private final TSecond _second;
   
 
   private int _cachedHashCode = Integer.MIN_VALUE;
   
   public Pair(TFirst first, TSecond second) {
     this._first = first;
     this._second = second;
   }
   
   public final TFirst getFirst() {
     return (TFirst)this._first;
   }
   
   public final TSecond getSecond() {
     return (TSecond)this._second;
   }
   
   public final boolean equals(Object obj) {
     if (obj == this) {
       return true;
     }
     
     if (!(obj instanceof Pair)) {
       return false;
     }
     
     Pair<?, ?> other = (Pair)obj;
     
     return (Comparer.equals(this._first, other._first)) && (Comparer.equals(this._second, other._second));
   }
   
   public final boolean equals(Pair<? extends TFirst, ? extends TSecond> other)
   {
     return (other != null) && (Comparer.equals(this._first, other._first)) && (Comparer.equals(this._second, other._second));
   }
   
 
 
   public final int hashCode()
   {
     if (this._cachedHashCode != Integer.MIN_VALUE) {
       return this._cachedHashCode;
     }
     
     int combinedHash = HashUtilities.combineHashCodes(this._first == null ? 1642088727 : this._first.hashCode(), this._second == null ? 428791459 : this._second.hashCode());
     
 
 
 
     this._cachedHashCode = combinedHash;
     
     return combinedHash;
   }
   
   public int compareTo(Pair<TFirst, TSecond> o)
   {
     if (o == this) {
       return 0;
     }
     
     if (o == null) {
       return 1;
     }
     
     int firstCompare = Comparer.compare(this._first, o._first);
     
     if (firstCompare != 0) {
       return firstCompare;
     }
     
     return Comparer.compare(this._second, o._second);
   }
   
   public final String toString()
   {
     return String.format("(%s; %s)", new Object[] { this._first, this._second });
   }
   
   public static <TFirst, TSecond> Pair<TFirst, TSecond> create(TFirst first, TSecond second) {
     return new Pair(first, second);
   }
 }


