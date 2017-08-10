 package com.strobel.core;
 
 
 
 
 
 public final class FloatBox
   implements IStrongBox
 {
   public float value;
   
 
 
 
 
   public FloatBox() {}
   
 
 
 
   public FloatBox(float value)
   {
     this.value = value;
   }
   
   public Float get()
   {
     return Float.valueOf(this.value);
   }
   
 
   public void set(Object value)
   {
     this.value = ((Float)value).floatValue();
   }
 }


