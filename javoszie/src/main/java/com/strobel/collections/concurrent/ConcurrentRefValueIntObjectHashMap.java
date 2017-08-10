 package com.strobel.collections.concurrent;
 
 import com.strobel.annotations.NotNull;
 import java.lang.ref.ReferenceQueue;
 import java.util.Iterator;
 
 abstract class ConcurrentRefValueIntObjectHashMap<V> implements ConcurrentIntObjectMap<V>
 {
   private final ConcurrentIntObjectHashMap<IntReference<V>> _map;
   private final ReferenceQueue<V> _queue;
   
   ConcurrentRefValueIntObjectHashMap()
   {
     this._map = new ConcurrentIntObjectHashMap();
     this._queue = new ReferenceQueue();
   }
   
 
 
   protected abstract IntReference<V> createReference(int paramInt, @NotNull V paramV, ReferenceQueue<V> paramReferenceQueue);
   
 
   private void processQueue()
   {
     for (;;)
     {
       IntReference<V> reference = (IntReference)this._queue.poll();
       
       if (reference == null) {
         return;
       }
       
       this._map.remove(reference.key(), reference);
     }
   }
   
   @NotNull
   public V addOrGet(int key, @NotNull V value)
   {
     processQueue();
     
     IntReference<V> newReference = createReference(key, value, this._queue);
     for (;;)
     {
       IntReference<V> oldReference = (IntReference)this._map.putIfAbsent(key, newReference);
       
       if (oldReference == null) {
         return value;
       }
       
       V oldValue = oldReference.get();
       
       if (oldValue != null) {
         return oldValue;
       }
       
       boolean replaced = this._map.replace(key, oldReference, newReference);
       
       if (replaced) {
         return value;
       }
     }
   }
   
   public V putIfAbsent(int key, @NotNull V value)
   {
     processQueue();
     
     IntReference<V> newReference = createReference(key, value, this._queue);
     for (;;)
     {
       IntReference<V> oldReference = (IntReference)this._map.putIfAbsent(key, newReference);
       
       if (oldReference == null) {
         return null;
       }
       
       V oldValue = oldReference.get();
       
       if (oldValue != null) {
         return oldValue;
       }
       
       boolean replaced = this._map.replace(key, oldReference, newReference);
       
       if (replaced) {
         return null;
       }
     }
   }
   
   public boolean remove(int key, @NotNull V value)
   {
     processQueue();
     return this._map.remove(key, createReference(key, value, this._queue));
   }
   
   public boolean replace(int key, @NotNull V oldValue, @NotNull V newValue)
   {
     processQueue();
     
     return this._map.replace(key, createReference(key, oldValue, this._queue), createReference(key, newValue, this._queue));
   }
   
 
 
 
 
   public V put(int key, @NotNull V value)
   {
     processQueue();
     
     IntReference<V> oldReference = (IntReference)this._map.put(key, createReference(key, value, this._queue));
     
     return (V)(oldReference != null ? oldReference.get() : null);
   }
   
 
   public V get(int key)
   {
     IntReference<V> reference = (IntReference)this._map.get(key);
     
     return (V)(reference != null ? reference.get() : null);
   }
   
 
   public V remove(int key)
   {
     processQueue();
     
     IntReference<V> reference = (IntReference)this._map.remove(key);
     
     return (V)(reference != null ? reference.get() : null);
   }
   
 
   public int size()
   {
     return this._map.size();
   }
   
   public boolean isEmpty()
   {
     return this._map.isEmpty();
   }
   
   public boolean contains(int key)
   {
     return this._map.contains(key);
   }
   
   public void clear()
   {
     this._map.clear();
     processQueue();
   }
   
   @NotNull
   public int[] keys()
   {
     return this._map.keys();
   }
   
   @NotNull
   public Iterable<IntObjectEntry<V>> entries()
   {
     return new Iterable()
     {
       public Iterator<IntObjectEntry<V>> iterator() {
        return  new Iterator() {
           final Iterator<IntObjectEntry<ConcurrentRefValueIntObjectHashMap.IntReference<V>>> entryIterator = ConcurrentRefValueIntObjectHashMap.this._map.entries().iterator();
           
           IntObjectEntry<V> next = nextLiveEntry();
           
           public boolean hasNext()
           {
             return this.next != null;
           }
           
           public IntObjectEntry<V> next()
           {
             if (!hasNext()) {
               throw new java.util.NoSuchElementException();
             }
             
             IntObjectEntry<V> result = this.next;
             this.next = nextLiveEntry();
             return result;
           }
           
           public void remove()
           {
             throw com.strobel.util.ContractUtils.unsupported();
           }
           
           private IntObjectEntry<V> nextLiveEntry() {
             while (this.entryIterator.hasNext()) {
               IntObjectEntry<ConcurrentRefValueIntObjectHashMap.IntReference<V>> entry = (IntObjectEntry)this.entryIterator.next();
               final V value = (V)((ConcurrentRefValueIntObjectHashMap.IntReference)entry.value()).get();
               
               if (value != null)
               {
 
 
                 final int key = entry.key();
                 
                 new IntObjectEntry()
                 {
                   public int key() {
                     return key;
                   }
                   
                   @NotNull
                   public V value()
                   {
                     return (V)value;
                   }
                 };
               }
             }
             return null;
           }
         };
       }
     };
   }
   
   protected static abstract interface IntReference<V>
   {
     public abstract int key();
     
     public abstract V get();
   }
 }


