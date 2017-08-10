 package com.javosize.thirdparty.org.github.jamm;
 
 public class FactoryImpl implements MemoryMeterListener.Factory
 {
   private static MemoryMeterListener inst;
   
   public FactoryImpl(MemoryMeterListener inst)
   {
     inst = inst;
   }
   
   public MemoryMeterListener newInstance()
   {
     return inst;
   }
 }


