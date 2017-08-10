 package com.strobel.collections;
 
 import java.util.concurrent.ConcurrentHashMap;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class TopLevelCache<K, V>
   extends Cache<K, V>
 {
   private final ConcurrentHashMap<K, V> _cache = new ConcurrentHashMap();
   
   public V cache(K key, V value)
   {
     V cachedValue = this._cache.putIfAbsent(key, value);
     return cachedValue != null ? cachedValue : value;
   }
   
   public Cache<K, V> getSatelliteCache()
   {
     return createSatelliteCache(this);
   }
   
   public boolean replace(K key, V expectedValue, V updatedValue)
   {
     if (expectedValue == null) {
       return this._cache.putIfAbsent(key, updatedValue) == null;
     }
     return this._cache.replace(key, expectedValue, updatedValue);
   }
   
   public V get(K key)
   {
     return (V)this._cache.get(key);
   }
 }


