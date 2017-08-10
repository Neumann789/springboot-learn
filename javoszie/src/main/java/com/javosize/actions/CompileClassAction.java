 package com.javosize.actions;
 
 import com.javosize.compiler.HotSwapper;
 import com.javosize.log.Log;
 
 
 
 
 
 
 
 public class CompileClassAction
   extends Action
 {
   private static final long serialVersionUID = 2699220557087814085L;
   private static Log log = new Log(CompileClassAction.class.getName());
   
   private String javaCode = null;
   private String className = null;
   
 
 
 
 
 
 
 
 
   public CompileClassAction(String javaCode, String className)
   {
     this.javaCode = javaCode;
     if (className != null) {
       if (className.endsWith(".class")) {
         this.className = className.substring(0, className.lastIndexOf(".")).trim();
       } else {
         this.className = className.trim();
       }
     }
   }
   
 
   public String execute()
   {
     String result = "false";
     try {
       log.debug("Requested the compilation of code for class " + this.className);
       if (HotSwapper.validateJavaCode(this.className, this.javaCode)) {
         result = "true";
         log.debug("The code of class " + this.className + " has compiled properly.");
       } else {
         result = "false";
         log.debug("Unable to compile the code of class " + this.className + ".");
       }
     } catch (Throwable th) {
       log.error("Error compiling class " + this.className + ": " + th, th);
       result = "false";
     }
     return result;
   }
 }


