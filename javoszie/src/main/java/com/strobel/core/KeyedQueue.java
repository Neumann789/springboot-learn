 package com.strobel.core;
 
 import java.util.ArrayDeque;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Queue;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class KeyedQueue<K, V>
 {
   private final Map<K, Queue<V>> _data;
   
   public KeyedQueue()
   {
     this._data = new HashMap();
   }
   
   private Queue<V> getQueue(K key) {
     Queue<V> queue = (Queue)this._data.get(key);
     
     if (queue == null) {
       this._data.put(key, queue = new ArrayDeque());
     }
     
     return queue;
   }
   
   public boolean add(K key, V value) {
     return getQueue(key).add(value);
   }
   
   public boolean offer(K key, V value) {
     return getQueue(key).offer(value);
   }
   
   public V poll(K key) {
     return (V)getQueue(key).poll();
   }
   
   public V peek(K key) {
     return (V)getQueue(key).peek();
   }
   
   public int size(K key) {
     Queue<V> queue = (Queue)this._data.get(key);
     return queue != null ? queue.size() : 0;
   }
   
   public void clear() {
     this._data.clear();
   }
 }


