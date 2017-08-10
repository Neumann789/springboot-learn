 package com.strobel.concurrent;
 
 import com.strobel.annotations.NotNull;
 import java.lang.reflect.Array;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class StripedLock<T>
 {
   private static final int LOCK_COUNT = 256;
   protected final T[] locks;
   private int _lockAllocationCounter;
   
   protected StripedLock(Class<T> lockType)
   {
     this.locks = ((Object[])Array.newInstance(lockType, 256));
     
     for (int i = 0; i < this.locks.length; i++) {
       this.locks[i] = createLock();
     }
   }
   
   @NotNull
   public T allocateLock() {
     return (T)this.locks[allocateLockIndex()];
   }
   
   public int allocateLockIndex() {
     return this._lockAllocationCounter = (this._lockAllocationCounter + 1) % 256;
   }
   
   @NotNull
   protected abstract T createLock();
   
   public abstract void lock(int paramInt);
   
   public abstract void unlock(int paramInt);
 }


