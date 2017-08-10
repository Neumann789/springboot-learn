 package com.javosize.agent;
 
 import com.javosize.compiler.ClassLoaderInjector;
 import java.util.Map;
 
 public class HttpSessionHelperFactory
 {
   private static volatile Class helper;
   
   public static synchronized Class getSessionHelper()
   {
     if (helper == null) {
       loadHelper();
     }
     return helper;
   }
   
   private static synchronized void loadHelper() {
     if (helper != null) {
       return;
     }
     Map<String, Object> sessions = com.javosize.agent.session.UserThreadSessionTracker.getAllSessionsMap();
     synchronized (sessions) {
       if (sessions.isEmpty()) {
         return;
       }
       Object targetSession = sessions.values().iterator().next();
       try {
         helper = ClassLoaderInjector.injectClassInClassLoader("com.javosize.agent.HttpSessionHelper", targetSession
         
           .getClass().getClassLoader());
       } catch (Throwable e) {
         throw new RuntimeException(e);
       }
     }
   }
 }


