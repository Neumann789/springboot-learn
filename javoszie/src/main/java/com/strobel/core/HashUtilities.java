 package com.strobel.core;
 
 import java.util.Arrays;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class HashUtilities
 {
   public static final int NullHashCode = 1642088727;
   private static final int HashPrime = 101;
   private static final int CombinedHashOffset = 5;
   private static final int MaxPrimeArrayLength = 2146435069;
   
   private HashUtilities()
   {
     throw new UnsupportedOperationException();
   }
   
   public static int hashCode(Object o) {
     if (o == null) {
       return 1642088727;
     }
     
     if (o.getClass().isArray()) {
       if ((o instanceof Object[])) {
         return combineHashCodes((Object[])o);
       }
       
       if ((o instanceof byte[])) {
         return Arrays.hashCode((byte[])o);
       }
       
       if ((o instanceof short[])) {
         return Arrays.hashCode((short[])o);
       }
       
       if ((o instanceof int[])) {
         return Arrays.hashCode((int[])o);
       }
       
       if ((o instanceof long[])) {
         return Arrays.hashCode((long[])o);
       }
       
       if ((o instanceof char[])) {
         return Arrays.hashCode((char[])o);
       }
       
       if ((o instanceof float[])) {
         return Arrays.hashCode((float[])o);
       }
       
       if ((o instanceof double[])) {
         return Arrays.hashCode((double[])o);
       }
       
       if ((o instanceof boolean[])) {
         return Arrays.hashCode((boolean[])o);
       }
     }
     
     return o.hashCode();
   }
   
   public static int hashItems(Iterable<?> items) {
     int hash = 0;
     
     for (Object o : items) {
       hash <<= 5;
       hash ^= hashCode(o);
     }
     
     return hash;
   }
   
   public static int combineHashCodes(int... hashes) {
     int hash = 0;
     
     for (int h : hashes) {
       hash <<= 5;
       hash ^= h;
     }
     
     return hash;
   }
   
   public static int combineHashCodes(Object... objects) {
     int hash = 0;
     
     for (Object o : objects) {
       int entryHash = 1642088727;
       
       if (o != null) {
         if ((o instanceof Object[])) {
           entryHash = combineHashCodes((Object[])o);
         }
         else {
           entryHash = hashCode(o);
         }
       }
       
       hash <<= 5;
       hash ^= entryHash;
     }
     
     return hash;
   }
   
   public static int combineHashCodes(int hash1, int hash2) {
     return hash1 << 5 ^ hash2;
   }
   
   public static int combineHashCodes(int hash1, int hash2, int hash3) {
     return (hash1 << 5 ^ hash2) << 5 ^ hash3;
   }
   
 
   public static int combineHashCodes(int hash1, int hash2, int hash3, int hash4)
   {
     return ((hash1 << 5 ^ hash2) << 5 ^ hash3) << 5 ^ hash4;
   }
   
 
 
 
 
 
 
 
 
   public static int combineHashCodes(int hash1, int hash2, int hash3, int hash4, int hash5)
   {
     return (((hash1 << 5 ^ hash2) << 5 ^ hash3) << 5 ^ hash4) << 5 ^ hash5;
   }
   
 
 
 
 
 
 
 
 
 
 
   public static int combineHashCodes(int hash1, int hash2, int hash3, int hash4, int hash5, int hash6)
   {
     return ((((hash1 << 5 ^ hash2) << 5 ^ hash3) << 5 ^ hash4) << 5 ^ hash5) << 5 ^ hash6;
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
   public static int combineHashCodes(int hash1, int hash2, int hash3, int hash4, int hash5, int hash6, int hash7)
   {
     return (((((hash1 << 5 ^ hash2) << 5 ^ hash3) << 5 ^ hash4) << 5 ^ hash5) << 5 ^ hash6) << 5 ^ hash7;
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public static int combineHashCodes(int hash1, int hash2, int hash3, int hash4, int hash5, int hash6, int hash7, int hash8)
   {
     return ((((((hash1 << 5 ^ hash2) << 5 ^ hash3) << 5 ^ hash4) << 5 ^ hash5) << 5 ^ hash6) << 5 ^ hash7) << 5 ^ hash8;
   }
   
 
 
 
 
 
 
   public static int combineHashCodes(Object o1, Object o2)
   {
     return combineHashCodes(o1 == null ? 1642088727 : hashCode(o1), o2 == null ? 1642088727 : hashCode(o2));
   }
   
 
 
   public static int combineHashCodes(Object o1, Object o2, Object o3)
   {
     return combineHashCodes(o1 == null ? 1642088727 : hashCode(o1), o2 == null ? 1642088727 : hashCode(o2), o3 == null ? 1642088727 : hashCode(o3));
   }
   
 
 
 
   public static int combineHashCodes(Object o1, Object o2, Object o3, Object o4)
   {
     return combineHashCodes(o1 == null ? 1642088727 : hashCode(o1), o2 == null ? 1642088727 : hashCode(o2), o3 == null ? 1642088727 : hashCode(o3), o4 == null ? 1642088727 : hashCode(o4));
   }
   
 
 
 
 
 
 
 
 
 
 
   public static int combineHashCodes(Object o1, Object o2, Object o3, Object o4, Object o5)
   {
     return combineHashCodes(o1 == null ? 1642088727 : hashCode(o1), o2 == null ? 1642088727 : hashCode(o2), o3 == null ? 1642088727 : hashCode(o3), o4 == null ? 1642088727 : hashCode(o4), o5 == null ? 1642088727 : hashCode(o5));
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
   public static int combineHashCodes(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6)
   {
     return combineHashCodes(o1 == null ? 1642088727 : hashCode(o1), o2 == null ? 1642088727 : hashCode(o2), o3 == null ? 1642088727 : hashCode(o3), o4 == null ? 1642088727 : hashCode(o4), o5 == null ? 1642088727 : hashCode(o5), o6 == null ? 1642088727 : hashCode(o6));
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public static int combineHashCodes(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7)
   {
     return combineHashCodes(o1 == null ? 1642088727 : hashCode(o1), o2 == null ? 1642088727 : hashCode(o2), o3 == null ? 1642088727 : hashCode(o3), o4 == null ? 1642088727 : hashCode(o4), o5 == null ? 1642088727 : hashCode(o5), o6 == null ? 1642088727 : hashCode(o6), o7 == null ? 1642088727 : hashCode(o7));
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public static int combineHashCodes(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8)
   {
     return combineHashCodes(o1 == null ? 1642088727 : hashCode(o1), o2 == null ? 1642088727 : hashCode(o2), o3 == null ? 1642088727 : hashCode(o3), o4 == null ? 1642088727 : hashCode(o4), o5 == null ? 1642088727 : hashCode(o5), o6 == null ? 1642088727 : hashCode(o6), o7 == null ? 1642088727 : hashCode(o7), o8 == null ? 1642088727 : hashCode(o8));
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   private static final int[] Primes = { 3, 7, 11, 17, 23, 29, 37, 47, 59, 71, 89, 107, 131, 163, 197, 239, 293, 353, 431, 521, 631, 761, 919, 1103, 1327, 1597, 1931, 2333, 2801, 3371, 4049, 4861, 5839, 7013, 8419, 10103, 12143, 14591, 17519, 21023, 25229, 30293, 36353, 43627, 52361, 62851, 75431, 90523, 108631, 130363, 156437, 187751, 225307, 270371, 324449, 389357, 467237, 560689, 672827, 807403, 968897, 1162687, 1395263, 1674319, 2009191, 2411033, 2893249, 3471899, 4166287, 4999559, 5999471, 7199369 };
   
 
 
 
 
 
   public static boolean isPrime(int candidate)
   {
     if ((candidate & 0x1) != 0) {
       int limit = (int)Math.sqrt(candidate);
       for (int divisor = 3; divisor <= limit; divisor += 2) {
         if (candidate % divisor == 0) {
           return false;
         }
       }
       return true;
     }
     return candidate == 2;
   }
   
   public static int getPrime(int min) {
     VerifyArgument.isNonNegative(min, "min");
     
     for (int prime : Primes) {
       if (prime >= min) {
         return prime;
       }
     }
     
 
 
     for (int i = min | 0x1; i < Integer.MAX_VALUE; i += 2) {
       if ((isPrime(i)) && ((i - 1) % 101 != 0)) {
         return i;
       }
     }
     return min;
   }
   
   public static int getMinPrime() {
     return Primes[0];
   }
   
   public static int expandPrime(int oldSize) {
     int newSize = 2 * oldSize;
     
 
 
     if ((Math.abs(newSize) > 2146435069) && (2146435069 > oldSize)) {
       assert (2146435069 == getPrime(2146435069)) : "Invalid MaxPrimeArrayLength";
       return 2146435069;
     }
     
     return getPrime(newSize);
   }
 }


