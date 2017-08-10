 package com.javosize.actions;
 
 import com.javosize.agent.Agent;
 import com.javosize.log.Log;
 import java.lang.instrument.Instrumentation;
 
 
 public class CountClassesAction
   extends Action
 {
   private static final long serialVersionUID = -2368200046848456461L;
   private static Log log = new Log(CountClassesAction.class.getName());
   
 
 
   public String execute()
   {
     try
     {
       log.debug("Number of classes requested by client.");
       String numOfClasses = countListOfClasses();
       log.debug("Found " + numOfClasses + " classes.");
       return numOfClasses;
     } catch (Throwable th) {
       th.printStackTrace();
       return "Error!! " + th.getMessage();
     }
   }
   
 
   private String countListOfClasses()
   {
     Instrumentation instrumentation = Agent.getInstrumentation();
     Class[] classes = instrumentation.getAllLoadedClasses();
     
     return "" + classes.length;
   }
 }


