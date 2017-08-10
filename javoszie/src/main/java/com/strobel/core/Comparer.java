 package com.strobel.core;
 
 import java.util.Arrays;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Comparer
 {
   public static <T> boolean notEqual(T o1, T o2)
   {
     return o2 != null;
   }
   
   public static <T> boolean equals(T o1, T o2)
   {
     return o1 == null ? false : o2 == null ? true : o1.equals(o2);
   }
   
   public static <T> boolean referenceEquals(T o1, T o2)
   {
     return o1 == o2;
   }
   
   public static <T extends Comparable<? super T>> int compare(T o1, T o2) {
     if (o1 == null) {
       return o2 == null ? 0 : -1;
     }
     return o1.compareTo(o2);
   }
   
   public static <T> boolean deepEquals(T o1, T o2) {
     if (o1 == o2) {
       return true;
     }
     
     if ((o1 == null) || (o2 == null)) {
       return false;
     }
     
     return deepEqualsCore(o1, o2);
   }
   
   public static boolean deepEquals(Object[] a1, Object[] a2) {
     if (a1 == a2) {
       return true;
     }
     
     if ((a1 == null) || (a2 == null)) {
       return false;
     }
     
     int length = a1.length;
     
     if (a2.length != length) {
       return false;
     }
     
     for (int i = 0; i < length; i++) {
       Object e1 = a1[i];
       Object e2 = a2[i];
       
       if (e1 != e2)
       {
 
 
         if (e1 == null) {
           return false;
         }
         
         if (!deepEqualsCore(e1, e2)) {
           return false;
         }
       }
     }
     return true;
   }
   
   private static final boolean deepEqualsCore(Object e1, Object e2) {
     if (e1.getClass().isArray()) {
       if (((e1 instanceof Object[])) && ((e2 instanceof Object[]))) {
         return deepEquals((Object[])e1, (Object[])e2);
       }
       
       if (((e1 instanceof byte[])) && ((e2 instanceof byte[]))) {
         return Arrays.equals((byte[])e1, (byte[])e2);
       }
       
       if (((e1 instanceof short[])) && ((e2 instanceof short[]))) {
         return Arrays.equals((short[])e1, (short[])e2);
       }
       
       if (((e1 instanceof int[])) && ((e2 instanceof int[]))) {
         return Arrays.equals((int[])e1, (int[])e2);
       }
       
       if (((e1 instanceof long[])) && ((e2 instanceof long[]))) {
         return Arrays.equals((long[])e1, (long[])e2);
       }
       
       if (((e1 instanceof char[])) && ((e2 instanceof char[]))) {
         return Arrays.equals((char[])e1, (char[])e2);
       }
       
       if (((e1 instanceof float[])) && ((e2 instanceof float[]))) {
         return Arrays.equals((float[])e1, (float[])e2);
       }
       
       if (((e1 instanceof double[])) && ((e2 instanceof double[]))) {
         return Arrays.equals((double[])e1, (double[])e2);
       }
       
       if (((e1 instanceof boolean[])) && ((e2 instanceof boolean[]))) {
         return Arrays.equals((boolean[])e1, (boolean[])e2);
       }
     }
     return e1.equals(e2);
   }
   
   public static int compare(Object a, Object b)
   {
     if (a == b) {
       return 0;
     }
     
     if (a == null) {
       return -1;
     }
     
     if (b == null) {
       return 1;
     }
     
     Class<?> aClass = a.getClass();
     Class<?> bClass = b.getClass();
     
     if ((Comparable.class.isInstance(a)) && (aClass.isAssignableFrom(bClass))) {
       return ((Comparable)a).compareTo(b);
     }
     
     if ((Comparable.class.isInstance(b)) && (bClass.isAssignableFrom(aClass))) {
       return ((Comparable)b).compareTo(a);
     }
     
     throw new IllegalArgumentException("Values must be comparable.");
   }
 }


