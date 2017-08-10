 package com.strobel.collections;
 
 import com.strobel.annotations.Nullable;
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class Cache<K, V>
 {
   public boolean contains(K key)
   {
     return get(key) != null;
   }
   
 
 
 
   public boolean contains(K key, V value)
   {
     V cachedValue = get(key);
     return (cachedValue != null) && (cachedValue.equals(value));
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
   public abstract Cache<K, V> getSatelliteCache();
   
 
 
 
 
 
 
 
 
 
 
 
   public abstract boolean replace(K paramK, @Nullable V paramV1, V paramV2);
   
 
 
 
 
 
 
 
 
 
 
 
   public abstract V get(K paramK);
   
 
 
 
 
 
 
 
 
 
 
 
   public abstract V cache(K paramK, V paramV);
   
 
 
 
 
 
 
 
 
 
 
 
   public static <K, V> Cache<K, V> createTopLevelCache()
   {
     return new TopLevelCache();
   }
   
 
 
 
 
 
 
 
 
 
 
   public static <K, V> Cache<K, V> createSatelliteCache()
   {
     return new SatelliteCache();
   }
   
 
 
 
 
 
 
 
 
 
 
 
   public static <K, V> Cache<K, V> createSatelliteCache(Cache<K, V> parent)
   {
     return new SatelliteCache((Cache)VerifyArgument.notNull(parent, "parent"));
   }
   
 
 
 
 
 
 
 
 
 
 
   public static <K, V> Cache<K, V> createSatelliteIdentityCache()
   {
     return new SatelliteCache();
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
   public static <K, V> Cache<K, V> createSatelliteIdentityCache(Cache<K, V> parent)
   {
     return new SatelliteCache((Cache)VerifyArgument.notNull(parent, "parent"));
   }
   
 
 
 
 
 
 
 
 
 
 
   public static <K, V> Cache<K, V> createThreadLocalCache()
   {
     return new ThreadLocalCache();
   }
   
 
 
 
 
 
 
 
 
 
 
   public static <K, V> Cache<K, V> createThreadLocalIdentityCache()
   {
     return new ThreadLocalCache();
   }
   
 
 
 
 
 
 
 
 
 
 
 
   public static <K, V> Cache<K, V> createThreadLocalCache(Cache<K, V> parent)
   {
     return new ThreadLocalCache((Cache)VerifyArgument.notNull(parent, "parent"));
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
   public static <K, V> Cache<K, V> createThreadLocalIdentityCache(Cache<K, V> parent)
   {
     return new ThreadLocalIdentityCache((Cache)VerifyArgument.notNull(parent, "parent"));
   }
 }


