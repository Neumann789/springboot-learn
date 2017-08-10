 package com.javosize.thirdparty.org.github.jamm;
 
 
 
 
 
 
 public final class NoopMemoryMeterListener
   implements MemoryMeterListener
 {
   private static final MemoryMeterListener INSTANCE = new NoopMemoryMeterListener();
   
   public static final MemoryMeterListener.Factory FACTORY = new FactoryImpl(INSTANCE);
   
   public void objectMeasured(Object current, long size) {}
   
   public void fieldAdded(Object obj, String fieldName, Object fieldValue) {}
   
   public void done(long size) {}
   
   public void started(Object obj) {}
   
   public void objectCounted(Object current) {}
 }


