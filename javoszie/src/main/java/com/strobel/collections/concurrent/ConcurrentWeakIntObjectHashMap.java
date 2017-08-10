 package com.strobel.collections.concurrent;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.core.Comparer;
 import java.lang.ref.ReferenceQueue;
 import java.lang.ref.WeakReference;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ConcurrentWeakIntObjectHashMap<V>
   extends ConcurrentRefValueIntObjectHashMap<V>
 {
   protected final ConcurrentRefValueIntObjectHashMap.IntReference<V> createReference(int key, @NotNull V value, ReferenceQueue<V> queue)
   {
     return new WeakIntReference(key, value, queue);
   }
   
   private static final class WeakIntReference<V> extends WeakReference<V> implements ConcurrentRefValueIntObjectHashMap.IntReference<V> {
     private final int _hash;
     private final int _key;
     
     WeakIntReference(int key, V referent, ReferenceQueue<? super V> q) {
       super(q);
       this._key = key;
       this._hash = referent.hashCode();
     }
     
     public final int key()
     {
       return this._key;
     }
     
     public final int hashCode()
     {
       return this._hash;
     }
     
 
     public final boolean equals(Object obj)
     {
       if (!(obj instanceof WeakIntReference)) {
         return false;
       }
       
       WeakIntReference<V> other = (WeakIntReference)obj;
       
       return (other._hash == this._hash) && (Comparer.equals(other.get(), get()));
     }
   }
 }


