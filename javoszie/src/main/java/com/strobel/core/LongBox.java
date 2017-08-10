 package com.strobel.core;
 
 
 
 
 
 public final class LongBox
   implements IStrongBox
 {
   public long value;
   
 
 
 
 
   public LongBox() {}
   
 
 
 
   public LongBox(long value)
   {
     this.value = value;
   }
   
   public Long get()
   {
     return Long.valueOf(this.value);
   }
   
 
   public void set(Object value)
   {
     this.value = ((Long)value).longValue();
   }
 }


