 package com.javosize.thirdparty.org.github.jamm;
 
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.Set;
 import java.util.concurrent.Callable;
 
 public class AlwaysEmptySet<T> implements Set<T>
 {
   public static final Set EMPTY_SET = new AlwaysEmptySet();
   
 
 
   public static <T> Set<T> create()
   {
     return EMPTY_SET;
   }
   
   public static <T> Callable<Set<T>> provider() {
    return new Callable() {
       public Set<T> call() throws Exception {
         return AlwaysEmptySet.create();
       }
     };
   }
   
   public int size() {
     return 0;
   }
   
   public boolean isEmpty() {
     return true;
   }
   
   public boolean contains(Object o) {
     return false;
   }
   
   public Iterator<T> iterator() {
     return (Iterator<T>)Collections.emptySet().iterator();
   }
   
   public Object[] toArray() {
     return new Object[0];
   }
   
   public <K> K[] toArray(K[] a) {
     return (K[])Collections.emptySet().toArray();
   }
   
   public boolean add(T t) {
     return false;
   }
   
   public boolean remove(Object o) {
     return false;
   }
   
   public boolean containsAll(Collection<?> c) {
     return false;
   }
   
   public boolean addAll(Collection<? extends T> c) {
     return false;
   }
   
   public boolean retainAll(Collection<?> c) {
     return false;
   }
   
   public boolean removeAll(Collection<?> c) {
     return false;
   }
   
   public void clear() {}
 }


