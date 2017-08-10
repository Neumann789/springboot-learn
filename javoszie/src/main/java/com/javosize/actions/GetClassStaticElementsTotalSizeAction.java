 package com.javosize.actions;
 
 import com.javosize.agent.memory.MemoryConsumptionUtils;
 import com.javosize.log.Log;
 
 
 
 
 
 
 
 public class GetClassStaticElementsTotalSizeAction
   extends Action
 {
   private static final long serialVersionUID = 5440274298566252764L;
   private static Log log = new Log(GetClassStaticElementsTotalSizeAction.class.getName());
   
   private String className = null;
   
   public GetClassStaticElementsTotalSizeAction(String className) {
     this.className = className;
   }
   
   public String execute()
   {
     log.info("Requested size of static elements of class " + this.className);
     try
     {
       Long size = MemoryConsumptionUtils.getClassSize(this.className);
       return "" + size;
     } catch (Throwable th) {
       log.warn("Error obtaining size of static elements for class " + this.className + ": " + th, th);
       return "ERROR: " + th.getMessage();
     }
   }
 }


