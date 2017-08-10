 package com.javosize.actions;
 
 import com.javosize.log.Log;
 import java.lang.reflect.Method;
 
 
 public class KillThreadAction
   extends Action
 {
   private static final long serialVersionUID = -2368200046848456461L;
   private static Log log = new Log(KillThreadAction.class.getName());
   private String threadId = "";
   
   public KillThreadAction(String threadId, int cols)
   {
     this.terminalWidth = cols;
     this.threadId = threadId;
   }
   
   public String execute() {
     ThreadGroup g = Thread.currentThread().getThreadGroup();
     for (;;) {
       ThreadGroup g2 = g.getParent();
       if (g2 == null) {
         break;
       }
       g = g2;
     }
     
 
     int size = 256;
     Thread[] threads;
     for (;;) { threads = new Thread[size];
       if (g.enumerate(threads) < size) {
         break;
       }
       size *= 2;
     }
     
     boolean found = false;
     for (Thread thread : threads) {
       if (thread != null)
       {
 
         String id = String.valueOf(thread.getId());
         if (this.threadId.equals(id)) {
           found = true;
           try
           {
             thread.interrupt();
           }
           catch (Exception localException) {}
           try
           {
             thread.stop();
           }
           catch (Exception localException1) {}
           try
           {
             thread.suspend();
           }
           catch (Exception localException2) {}
           
           if (thread.isAlive())
             ultraKill(thread);
         }
       }
     }
     if (!found) {
       return this.threadId + " not found\n";
     }
     return "Thread [" + this.threadId + "] killed\n";
   }
   
   private void ultraKill(Thread thread) {
     try {
       Method m = Thread.class.getDeclaredMethod("stop0", new Class[] { Object.class });
       m.setAccessible(true);
       m.invoke(thread, new Object[] { new ThreadDeath() });
     }
     catch (Throwable localThrowable) {}
   }
 }


