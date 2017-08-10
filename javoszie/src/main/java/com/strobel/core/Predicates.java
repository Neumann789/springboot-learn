 package com.strobel.core;
 
 import java.lang.reflect.Array;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.List;
 import java.util.Map;
 import java.util.Objects;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Predicates
 {
   public static final Predicate<Object> IS_NULL = new Predicate()
   {
     public boolean test(Object o) {
       return o == null;
     }
   };
   
 
 
 
 
   public static final Predicate<Object> NON_NULL = new Predicate()
   {
     public boolean test(Object o) {
       return o != null;
     }
   };
   
 
 
 
   public static final Predicate<Object> FALSE = new Predicate()
   {
     public boolean test(Object o) {
       return false;
     }
   };
   
 
 
 
 
   public static final Predicate<Object> TRUE = new Predicate()
   {
     public boolean test(Object o) {
       return true;
     }
   };
   
 
 
   private Predicates()
   {
     throw new AssertionError("No instances!");
   }
   
 
 
 
 
 
 
   public static <T> Predicate<T> isNull()
   {
     return IS_NULL;
   }
   
 
 
 
 
 
 
   public static <T> Predicate<T> nonNull()
   {
     return NON_NULL;
   }
   
 
 
 
 
   public static <T> Predicate<T> alwaysFalse()
   {
     return FALSE;
   }
   
 
 
 
 
   public static <T> Predicate<T> alwaysTrue()
   {
     return TRUE;
   }
   
 
 
 
 
 
 
 
 
   public static <T> Predicate<T> instanceOf(Class<?> clazz)
   {
     new Predicate()
     {
       public boolean test(T o) {
         return this.val$clazz.isInstance(o);
       }
     };
   }
   
 
 
 
 
 
 
   public static <T> Predicate<T> isSame(T target)
   {
     new Predicate()
     {
       public boolean test(T t) {
         return t == this.val$target;
       }
     };
   }
   
 
 
 
 
 
 
 
   public static <T> Predicate<T> isEqual(T target)
   {
     if (null == target)
       return isNull();
     new Predicate()
     {
       public boolean test(T t) {
         return this.val$target.equals(t);
       }
     };
   }
   
 
 
 
 
 
 
 
 
 
 
   public static <T> Predicate<T> contains(Collection<? extends T> target)
   {
     new Predicate()
     {
       public boolean test(T t) {
         return this.val$target.contains(t);
       }
     };
   }
   
 
 
 
 
 
 
 
 
 
   public static <T> Predicate<T> containsKey(Map<? extends T, ?> target)
   {
     new Predicate()
     {
       public boolean test(T t) {
         return this.val$target.containsKey(t);
       }
     };
   }
   
 
 
 
 
 
 
 
 
 
   public static <T, P extends Predicate<? super T>> Predicate<T> negate(P predicate)
   {
     new Predicate()
     {
       public boolean test(T t) {
         return !this.val$predicate.test(t);
       }
     };
   }
   
 
 
 
 
 
 
 
 
 
 
 
   public static <T, P extends Predicate<? super T>> Predicate<T> and(Predicate<T> first, final P second)
   {
     if ((first != null) && (first == second)) {
       return first;
     }
     
     Objects.requireNonNull(first);
     Objects.requireNonNull(second);
     
     new Predicate()
     {
       public boolean test(T t)
       {
         return (this.val$first.test(t)) && (second.test(t));
       }
     };
   }
   
 
 
 
 
 
 
 
 
 
 
   public static <T, P extends Predicate<? super T>> Predicate<T> and(Iterable<P> components)
   {
     List<P> predicates = safeCopyOf(components);
     
     if (predicates.isEmpty()) {
       throw new IllegalArgumentException("no predicates");
     }
     
     new Predicate()
     {
       public boolean test(T t) {
         for (P predicate : this.val$predicates) {
           if (!predicate.test(t)) {
             return false;
           }
         }
         return true;
       }
     };
   }
   
 
 
 
 
 
 
 
 
 
 
   static <T, P extends Predicate<? super T>> Predicate<T> and(P first, Iterable<P> components)
   {
     List<P> predicates = safeCopyOf(first, components);
     
     new Predicate()
     {
       public boolean test(T t) {
         for (P predicate : this.val$predicates) {
           if (!predicate.test(t)) {
             return false;
           }
         }
         return true;
       }
     };
   }
   
 
 
 
 
 
 
 
 
 
 
 
   @SafeVarargs
   public static <T, P extends Predicate<? super T>> Predicate<T> and(P... components)
   {
     P[] predicates = (Predicate[])safeCopyOf(components);
     
     if (0 == predicates.length) {
       throw new IllegalArgumentException("no predicates");
     }
     
     new Predicate()
     {
       public boolean test(T t) {
         for (P predicate : this.val$predicates) {
           if (!predicate.test(t)) {
             return false;
           }
         }
         return true;
       }
     };
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
   @SafeVarargs
   static <T, P extends Predicate<? super T>> Predicate<T> and(P first, P... components)
   {
     P[] predicates = (Predicate[])safeCopyOf(first, components);
     
     if (0 == predicates.length) {
       throw new IllegalArgumentException("no predicates");
     }
     
     new Predicate()
     {
       public boolean test(T t) {
         for (P predicate : this.val$predicates) {
           if (!predicate.test(t)) {
             return false;
           }
         }
         return true;
       }
     };
   }
   
 
 
 
 
 
 
 
 
 
 
 
   public static <T, P extends Predicate<? super T>> Predicate<T> or(Predicate<T> first, final P second)
   {
     if ((first != null) && (first == second)) {
       return first;
     }
     
     Objects.requireNonNull(first);
     Objects.requireNonNull(second);
     
     new Predicate()
     {
       public boolean test(T t)
       {
         return (this.val$first.test(t)) || (second.test(t));
       }
     };
   }
   
 
 
 
 
 
 
 
 
 
 
 
   public static <T, P extends Predicate<? super T>> Predicate<T> or(Iterable<P> components)
   {
     List<P> predicates = safeCopyOf(components);
     
     if (predicates.isEmpty()) {
       throw new IllegalArgumentException("no predicates");
     }
     
     new Predicate()
     {
       public boolean test(T t) {
         for (P predicate : this.val$predicates) {
           if (predicate.test(t)) {
             return true;
           }
         }
         return false;
       }
     };
   }
   
 
 
 
 
 
 
 
 
 
 
 
   static <T, P extends Predicate<? super T>> Predicate<T> or(P first, Iterable<P> components)
   {
     List<P> predicates = safeCopyOf(first, components);
     
     new Predicate()
     {
       public boolean test(T t) {
         for (P predicate : this.val$predicates) {
           if (predicate.test(t)) {
             return true;
           }
         }
         return false;
       }
     };
   }
   
 
 
 
 
 
 
 
 
 
 
 
   @SafeVarargs
   public static <T, P extends Predicate<? super T>> Predicate<T> or(P... components)
   {
     P[] predicates = (Predicate[])safeCopyOf(components);
     
     if (0 == predicates.length) {
       throw new IllegalArgumentException("no predicates");
     }
     
     new Predicate()
     {
       public boolean test(T t) {
         for (P predicate : this.val$predicates) {
           if (predicate.test(t)) {
             return true;
           }
         }
         return false;
       }
     };
   }
   
 
 
 
 
 
 
 
 
 
 
 
   @SafeVarargs
   static <T, P extends Predicate<? super T>> Predicate<T> or(Predicate<T> first, P... components)
   {
     P[] predicates = (Predicate[])safeCopyOf(first, components);
     
     new Predicate()
     {
       public boolean test(T t) {
         for (P predicate : this.val$predicates) {
           if (predicate.test(t)) {
             return true;
           }
         }
         return false;
       }
     };
   }
   
 
 
 
 
 
 
 
 
 
 
 
   public static <T, P extends Predicate<? super T>> Predicate<T> xor(Predicate<T> first, final P second)
   {
     if ((null != first) && (first == second)) {
       return alwaysFalse();
     }
     
     Objects.requireNonNull(first);
     Objects.requireNonNull(second);
     
     new Predicate()
     {
       public boolean test(T t)
       {
         return this.val$first.test(t) ^ second.test(t);
       }
     };
   }
   
 
 
 
 
 
 
 
 
 
 
 
   public static <T, P extends Predicate<? super T>> Predicate<T> xor(Iterable<P> components)
   {
     List<P> predicates = safeCopyOf(components);
     
     if (predicates.isEmpty()) {
       throw new IllegalArgumentException("no predicates");
     }
     
     new Predicate()
     {
       public boolean test(T t) {
         Boolean initial = null;
         
         for (P predicate : this.val$predicates) {
           if (null == initial) {
             initial = Boolean.valueOf(predicate.test(t));
 
           }
           else if (!(initial.booleanValue() ^ predicate.test(t))) {
             return true;
           }
         }
         
         return false;
       }
     };
   }
   
 
 
 
 
 
 
 
 
 
 
 
   @SafeVarargs
   public static <T, P extends Predicate<? super T>> Predicate<T> xor(P... components)
   {
     P[] predicates = (Predicate[])safeCopyOf(components);
     
     if (0 == predicates.length) {
       throw new IllegalArgumentException("no predicates");
     }
     
     new Predicate()
     {
       public boolean test(T t) {
         Boolean initial = null;
         
         for (P predicate : this.val$predicates) {
           if (null == initial) {
             initial = Boolean.valueOf(predicate.test(t));
 
           }
           else if (!(initial.booleanValue() ^ predicate.test(t))) {
             return true;
           }
         }
         
         return false;
       }
     };
   }
   
 
 
 
 
 
 
 
 
 
 
 
   @SafeVarargs
   static <T, P extends Predicate<? super T>> Predicate<T> xor(Predicate<T> first, P... components)
   {
     P[] predicates = (Predicate[])safeCopyOf(first, components);
     
     new Predicate()
     {
       public boolean test(T t) {
         Boolean initial = null;
         
         for (P predicate : this.val$predicates) {
           if (null == initial) {
             initial = Boolean.valueOf(predicate.test(t));
 
           }
           else if (!(initial.booleanValue() ^ predicate.test(t))) {
             return true;
           }
         }
         
         return false;
       }
     };
   }
   
 
 
 
 
 
 
 
 
 
 
 
   static <T, P extends Predicate<? super T>> Predicate<T> xor(Predicate<T> first, Iterable<P> components)
   {
     List<P> predicates = safeCopyOf(first, components);
     
     new Predicate()
     {
       public boolean test(T t) {
         Boolean initial = null;
         
         for (P predicate : this.val$predicates) {
           if (null == initial) {
             initial = Boolean.valueOf(predicate.test(t));
 
           }
           else if (!(initial.booleanValue() ^ predicate.test(t))) {
             return true;
           }
         }
         
         return false;
       }
     };
   }
   
   @SafeVarargs
   private static <T> T[] safeCopyOf(T... array) {
     T[] copy = Arrays.copyOf(array, array.length);
     
     for (T each : copy) {
       Objects.requireNonNull(each);
     }
     
     return copy;
   }
   
   @SafeVarargs
   private static <T> T[] safeCopyOf(T first, T... array) {
     T[] copy = (Object[])Array.newInstance(array.getClass().getComponentType(), array.length + 1);
     
     copy[0] = Objects.requireNonNull(first);
     System.arraycopy(array, 0, copy, 1, array.length);
     
     for (T each : copy) {
       Objects.requireNonNull(each);
     }
     
     return copy;
   }
   
   private static <T> List<T> safeCopyOf(T first, Iterable<T> iterable) {
     ArrayList<T> list = new ArrayList();
     list.add(Objects.requireNonNull(first));
     
     for (T element : iterable) {
       list.add(Objects.requireNonNull(element));
     }
     return list;
   }
   
   private static <T> List<T> safeCopyOf(Iterable<T> iterable) {
     ArrayList<T> list = new ArrayList();
     
     for (T element : iterable) {
       list.add(Objects.requireNonNull(element));
     }
     
     return list;
   }
 }


