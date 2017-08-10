 package com.javosize.actions;
 
 import com.javosize.agent.Tools;
 import com.javosize.encoding.Base64;
 import com.javosize.log.Log;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class GetClassByteCodeAction
   extends Action
 {
   private static final long serialVersionUID = 5440274298566252764L;
   private static Log log = new Log(GetClassByteCodeAction.class.getName());
   
   private String className = null;
   
   public GetClassByteCodeAction(String className) {
     this.className = className;
   }
   
   public String execute()
   {
     log.info("Requested bytecode of class " + this.className);
     try
     {
       byte[] byteCode = Tools.getClassBytes(this.className);
       if (byteCode != null) {
         String s = Base64.encodeBytesToString(byteCode);
         log.info("Bytecode for class " + this.className + " found (Size: " + byteCode.length + " bytes)");
         return s;
       }
       log.warn("Bytecode for class " + this.className + " not found!");
       return null;
     }
     catch (Throwable th) {
       log.error("Error recovering class bytes: " + th, th); }
     return null;
   }
 }


