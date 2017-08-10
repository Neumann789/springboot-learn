 package com.strobel.core;
 
 
 
 
 
 public final class IntegerBox
   implements IStrongBox
 {
   public int value;
   
 
 
 
 
   public IntegerBox() {}
   
 
 
 
   public IntegerBox(int value)
   {
     this.value = value;
   }
   
   public Integer get()
   {
     return Integer.valueOf(this.value);
   }
   
 
   public void set(Object value)
   {
     this.value = ((Integer)value).intValue();
   }
 }


