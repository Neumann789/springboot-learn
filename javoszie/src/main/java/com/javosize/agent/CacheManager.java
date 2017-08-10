 package com.javosize.agent;
 
 import java.util.Collections;
 import java.util.Map;
 
 
 
 
 
 
 
 public class CacheManager
 {
   public static Map getCache()
   {
     return getCache(1000);
   }
   
 
 
 
 
 
 
   public static Map getCache(int capacity)
   {
     return getCache(capacity, true);
   }
   
 
 
 
 
 
 
 
 
   public static Map getCache(int capacity, boolean sync)
   {
     return getCache(capacity, false, sync);
   }
   
 
 
 
 
 
 
   public static Map getCacheLRU(int capacity)
   {
     return getCacheLRU(capacity, true);
   }
   
 
 
 
 
 
 
 
 
   public static Map getCacheLRU(int capacity, boolean sync)
   {
     return getCache(capacity, true, sync);
   }
   
   public static Map getCache(int capacity, boolean accessOrder, boolean sync) {
     if (!sync) {
       return new JavosizeHashMap(capacity, accessOrder);
     }
     return Collections.synchronizedMap(new JavosizeHashMap(capacity, accessOrder));
   }
 }


