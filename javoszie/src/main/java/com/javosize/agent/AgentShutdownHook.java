 package com.javosize.agent;
 
 import com.javosize.log.Log;
 import com.javosize.remote.Controller;
 import com.javosize.scheduler.Scheduler;
 import org.jboss.jreadline.console.settings.Settings;
 import org.jboss.jreadline.terminal.POSIXTerminal;
 
 public class AgentShutdownHook
   extends Thread
 {
   private static Log log = new Log(AgentShutdownHook.class.getName());
   
   private boolean backgroundMode = false;
   private boolean javaagentMode = false;
   
   public AgentShutdownHook(boolean backgroundMode, boolean javaagentMode)
   {
     this.backgroundMode = backgroundMode;
     this.javaagentMode = javaagentMode;
   }
   
   public void run()
   {
     try
     {
       if ((this.javaagentMode) || (this.backgroundMode)) {
         log.info("Shutting down " + (this.javaagentMode ? "java agent" : "background") + " javosize");
         
         if (this.backgroundMode) {
           Controller.getInstance().finish();
         }
         Scheduler.getScheduler(false);Scheduler.stop();
         
         log.info("Gracefull shutdown");
       }
       else {
         try {
           POSIXTerminal t = (POSIXTerminal)Settings.getInstance().getTerminal();
           t.cleanUp();
         }
         catch (Exception localException) {}
       }
     }
     catch (Throwable th) {
       log.warn("Error shutdown javosize: " + th, th);
     }
   }
 }


