 package com.strobel.concurrent;
 
 import com.strobel.annotations.NotNull;
 import java.util.concurrent.locks.ReentrantLock;
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class StripedReentrantLock
   extends StripedLock<ReentrantLock>
 {
   private static final StripedReentrantLock INSTANCE = new StripedReentrantLock();
   
   public static StripedReentrantLock instance() {
     return INSTANCE;
   }
   
   public StripedReentrantLock() {
     super(ReentrantLock.class);
   }
   
   @NotNull
   protected final ReentrantLock createLock()
   {
     return new ReentrantLock();
   }
   
   public final void lock(int index)
   {
     ((ReentrantLock[])this.locks)[index].lock();
   }
   
   public final void unlock(int index)
   {
     ((ReentrantLock[])this.locks)[index].unlock();
   }
 }


