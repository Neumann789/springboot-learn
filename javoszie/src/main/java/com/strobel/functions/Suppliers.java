 package com.strobel.functions;
 
 import com.strobel.util.ContractUtils;
 
 public final class Suppliers {
   private Suppliers() {
     throw ContractUtils.unreachable();
   }
   
   public static <T> Supplier<T> forValue(T value) {
     new Supplier()
     {
       public T get() {
         return (T)this.val$value;
       }
     };
   }
 }


