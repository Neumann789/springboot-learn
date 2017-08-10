 package com.javosize.agent;
 
 import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
 
 
 public class JavosizeHashMap<K, V>
   extends LinkedHashMap<K, V>
 {
   private static final long serialVersionUID = -3216599441788189122L;
   private int maxCapacity = 1000;
   
   protected JavosizeHashMap(int capacity) {
     super(capacity);
     this.maxCapacity = capacity;
   }
   
   protected JavosizeHashMap(int capacity, boolean accessOrder) {
     super(capacity, 0.75F, accessOrder);
     this.maxCapacity = capacity;
   }
   
   protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
   {
     return size() >= this.maxCapacity;
   }
 }


