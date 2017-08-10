 package com.strobel.core;
 
 
 
 
 
 public final class DoubleBox
   implements IStrongBox
 {
   public double value;
   
 
 
 
 
   public DoubleBox() {}
   
 
 
 
   public DoubleBox(double value)
   {
     this.value = value;
   }
   
   public Double get()
   {
     return Double.valueOf(this.value);
   }
   
 
   public void set(Object value)
   {
     this.value = ((Double)value).doubleValue();
   }
 }


