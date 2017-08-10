 package com.strobel.collections;
 
 import java.util.HashMap;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class SatelliteCache<K, V>
   extends Cache<K, V>
 {
   private final Cache<K, V> _parent;
   private final HashMap<K, V> _cache = new HashMap();
   
   public SatelliteCache() {
     this._parent = null;
   }
   
   public Cache<K, V> getSatelliteCache()
   {
     return this;
   }
   
   public boolean replace(K key, V expectedValue, V updatedValue)
   {
     if ((this._parent != null) && (!this._parent.replace(key, expectedValue, updatedValue))) {
       return false;
     }
     this._cache.put(key, updatedValue);
     return true;
   }
   
   public SatelliteCache(Cache<K, V> parent) {
     this._parent = parent;
   }
   
   public V cache(K key, V value)
   {
     V cachedValue = this._cache.get(key);
     
     if (cachedValue != null) {
       return cachedValue;
     }
     
     if (this._parent != null) {
       cachedValue = this._parent.cache(key, value);
     }
     else {
       cachedValue = value;
     }
     
     this._cache.put(key, cachedValue);
     
     return cachedValue;
   }
   
   public V get(K key)
   {
     V cachedValue = this._cache.get(key);
     
     if (cachedValue != null) {
       return cachedValue;
     }
     
     if (this._parent != null) {
       cachedValue = this._parent.get(key);
       
       if (cachedValue != null) {
         this._cache.put(key, cachedValue);
       }
     }
     
     return cachedValue;
   }
 }


