 package com.strobel.core;
 
 
 
 
 public final class Triple<TFirst, TSecond, TThird>
   implements Comparable<Triple<TFirst, TSecond, TThird>>
 {
   private static final int UninitializedHashCode = Integer.MIN_VALUE;
   
 
   private static final int FirstNullHash = 1642088727;
   
 
   private static final int SecondNullHash = 428791459;
   
 
   private static final int ThirdNullHash = 1090263159;
   
 
   private final TFirst _first;
   
 
   private final TSecond _second;
   
 
   private final TThird _third;
   
 
   private int _cachedHashCode = Integer.MIN_VALUE;
   
   public Triple(TFirst first, TSecond second, TThird third) {
     this._first = first;
     this._second = second;
     this._third = third;
   }
   
   public final TFirst getFirst() {
     return (TFirst)this._first;
   }
   
   public final TSecond getSecond() {
     return (TSecond)this._second;
   }
   
   public final TThird getThird() {
     return (TThird)this._third;
   }
   
   public final boolean equals(Object obj) {
     if (obj == this) {
       return true;
     }
     
     if (!(obj instanceof Triple)) {
       return false;
     }
     
     Triple<?, ?, ?> other = (Triple)obj;
     
     return (Comparer.equals(this._first, other._first)) && (Comparer.equals(this._second, other._second)) && (Comparer.equals(this._third, other._third));
   }
   
 
   public final boolean equals(Triple<? extends TFirst, ? extends TSecond, ? extends TThird> other)
   {
     return (other != null) && (Comparer.equals(this._first, other._first)) && (Comparer.equals(this._second, other._second)) && (Comparer.equals(this._third, other._third));
   }
   
 
 
 
   public final int hashCode()
   {
     if (this._cachedHashCode != Integer.MIN_VALUE) {
       return this._cachedHashCode;
     }
     
     int combinedHash = HashUtilities.combineHashCodes(this._first == null ? 1642088727 : this._first.hashCode(), this._second == null ? 428791459 : this._second.hashCode(), this._third == null ? 1090263159 : this._third.hashCode());
     
 
 
 
 
     this._cachedHashCode = combinedHash;
     
     return combinedHash;
   }
   
   public int compareTo(Triple<TFirst, TSecond, TThird> o)
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
     
     int secondCompare = Comparer.compare(this._second, o._second);
     
     if (secondCompare != 0) {
       return secondCompare;
     }
     
     return Comparer.compare(this._third, o._third);
   }
   
   public final String toString()
   {
     return String.format("Triple[%s, %s, %s]", new Object[] { this._first, this._second, this._third });
   }
   
 
 
 
   public static <TFirst, TSecond, TThird> Triple<TFirst, TSecond, TThird> create(TFirst first, TSecond second, TThird third)
   {
     return new Triple(first, second, third);
   }
 }


