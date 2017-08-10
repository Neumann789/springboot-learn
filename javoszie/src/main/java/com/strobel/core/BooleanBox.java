 package com.strobel.core;
 
 
 
 
 
 public final class BooleanBox
   implements IStrongBox
 {
   public boolean value;
   
 
 
 
 
   public BooleanBox() {}
   
 
 
 
   public BooleanBox(boolean value)
   {
     this.value = value;
   }
   
   public Boolean get()
   {
     return Boolean.valueOf(this.value);
   }
   
 
   public void set(Object value)
   {
     this.value = ((Boolean)value).booleanValue();
   }
 }


