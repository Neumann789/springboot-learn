 package com.strobel.core;
 
 import java.lang.reflect.UndeclaredThrowableException;
 import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
 
 
 
 public final class Closeables
 {
   private static final SafeCloseable EMPTY = new SafeCloseable()
   {
     public void close() {}
   };
   
   public static SafeCloseable empty()
   {
     return EMPTY;
   }
   
   public static SafeCloseable create(Runnable delegate) {
     return new AnonymousCloseable((Runnable)VerifyArgument.notNull(delegate, "delegate"), null);
   }
   
   public static void close(AutoCloseable closeable) {
     try {
       closeable.close();
     }
     catch (Error|RuntimeException e) {
       throw e;
     }
     catch (Throwable t) {
       throw new UndeclaredThrowableException(t);
     }
   }
   
   public static void close(AutoCloseable... closeables) {
     for (AutoCloseable closeable : closeables) {
       close(closeable);
     }
   }
   
   public static void tryClose(AutoCloseable closeable) {
     if (closeable != null) {
       try {
         closeable.close();
       }
       catch (Exception ignored) {}
     }
   }
   
   public static void tryClose(AutoCloseable... closeables)
   {
     if (closeables != null) {
       for (AutoCloseable closeable : closeables) {
         tryClose(closeable);
       }
     }
   }
   
   private static final class AnonymousCloseable
     implements SafeCloseable
   {
     private static final AtomicIntegerFieldUpdater<AnonymousCloseable> CLOSED_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AnonymousCloseable.class, "_closed");
     
 
     private final Runnable _delegate;
     
     private volatile int _closed;
     
 
     private AnonymousCloseable(Runnable delegate)
     {
       this._delegate = ((Runnable)VerifyArgument.notNull(delegate, "delegate"));
     }
     
     public void close()
     {
       if (CLOSED_UPDATER.getAndSet(this, 1) == 0) {
         this._delegate.run();
       }
     }
   }
 }


