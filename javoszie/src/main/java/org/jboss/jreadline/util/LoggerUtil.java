 package org.jboss.jreadline.util;
 
 import java.io.IOException;
 import java.util.logging.FileHandler;
 import java.util.logging.Handler;
 import java.util.logging.Logger;
 import java.util.logging.SimpleFormatter;
 import org.jboss.jreadline.console.settings.Settings;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class LoggerUtil
 {
   private static Handler logHandler;
   
   public static synchronized Logger getLogger(String name)
   {
     if (logHandler == null) {
       try {
         logHandler = new FileHandler(Settings.getInstance().getLogFile());
         logHandler.setFormatter(new SimpleFormatter());
       }
       catch (IOException e) {
         e.printStackTrace();
       }
     }
     Logger log = Logger.getLogger(name);
     log.setUseParentHandlers(false);
     log.addHandler(logHandler);
     
     return log;
   }
 }


