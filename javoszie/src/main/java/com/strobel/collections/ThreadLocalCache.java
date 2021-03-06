 package com.strobel.collections;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class ThreadLocalCache<K, V>
   extends Cache<K, V>
 {
   private final Cache<K, V> _parent;
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   private final ThreadLocal<SatelliteCache<K, V>> _threadCaches = new ThreadLocal()
   {
     protected SatelliteCache<K, V> initialValue()
     {
       return new SatelliteCache(ThreadLocalCache.this._parent);
     }
   };
   
   public ThreadLocalCache() {
     this._parent = null;
   }
   
   public Cache<K, V> getSatelliteCache()
   {
     return (Cache)this._threadCaches.get();
   }
   
   public boolean replace(K key, V expectedValue, V updatedValue)
   {
     return ((SatelliteCache)this._threadCaches.get()).replace(key, expectedValue, updatedValue);
   }
   
   public ThreadLocalCache(Cache<K, V> parent) {
     this._parent = parent;
   }
   
   public V cache(K key, V value)
   {
     return (V)((SatelliteCache)this._threadCaches.get()).cache(key, value);
   }
   
   public V get(K key)
   {
     return (V)((SatelliteCache)this._threadCaches.get()).get(key);
   }
 }


