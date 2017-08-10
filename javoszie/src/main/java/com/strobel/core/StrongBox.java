 package com.strobel.core;
 
 import com.strobel.functions.Block;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class StrongBox<T>
   implements IStrongBox, Block<T>
 {
   public T value;
   
   public StrongBox() {}
   
   public StrongBox(T value)
   {
     this.value = value;
   }
   
   public T get()
   {
     return (T)this.value;
   }
   
 
   public void set(Object value)
   {
     this.value = value;
   }
   
   public void accept(T input)
   {
     this.value = input;
   }
   
   public String toString()
   {
     return "StrongBox{value=" + this.value + '}';
   }
 }


