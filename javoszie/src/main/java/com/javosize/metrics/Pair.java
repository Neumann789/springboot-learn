 package com.javosize.metrics;
 
 public class Pair
 {
   private long timestamp;
   private Object object;
   
   public Pair(long timestamp, Object object) {
     this.timestamp = timestamp;
     this.object = object;
   }
   
   public long getTimestamp() { return this.timestamp; }
   
   public void setTimestamp(long timestamp) {
     this.timestamp = timestamp;
   }
   
   public Object getObject() { return this.object; }
   
   public void setObject(Object object) {
     this.object = object;
   }
 }


