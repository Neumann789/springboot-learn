package com.javosize.thirdparty.org.github.jamm;

public abstract interface MemoryMeterListener
{
  public abstract void started(Object paramObject);
  
  public abstract void fieldAdded(Object paramObject1, String paramString, Object paramObject2);
  
  public abstract void objectMeasured(Object paramObject, long paramLong);
  
  public abstract void objectCounted(Object paramObject);
  
  public abstract void done(long paramLong);
  
  public static abstract interface Factory
  {
    public abstract MemoryMeterListener newInstance();
  }
}


