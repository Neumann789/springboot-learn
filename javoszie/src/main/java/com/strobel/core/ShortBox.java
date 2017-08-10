 package com.strobel.core;
 
 
 
 
 
 public final class ShortBox
   implements IStrongBox
 {
   public short value;
   
 
 
 
 
   public ShortBox() {}
   
 
 
 
   public ShortBox(short value)
   {
     this.value = value;
   }
   
   public Short get()
   {
     return Short.valueOf(this.value);
   }
   
 
   public void set(Object value)
   {
     this.value = ((Short)value).shortValue();
   }
 }


