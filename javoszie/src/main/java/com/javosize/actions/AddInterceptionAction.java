 package com.javosize.actions;
 
 import com.javosize.agent.Agent;
 import com.javosize.agent.Interception;
 import com.javosize.log.Log;
 
 public class AddInterceptionAction
   extends Action
 {
   private static final long serialVersionUID = 8781181992700288827L;
   private Interception interception;
   private static Log log = new Log(AddInterceptionAction.class.getName());
   
   public AddInterceptionAction(String name, String type, String classNameRegexp, String methodNameRegexp, String interceptorCode, String appName) {
     this.interception = new Interception();
     this.interception.setId(name);
     this.interception.setClassNameRegexp(classNameRegexp);
     this.interception.setMethodNameRegexp(methodNameRegexp);
     this.interception.setType(type);
     this.interception.setInterceptorCode(interceptorCode);
     this.interception.setAppName(appName);
   }
   
   public String execute()
   {
     try {
       this.interception.initInterceptor();
       Agent.addInterception(this.interception);
     }
     catch (Throwable th) {
       log.error("Error creating interception: " + th, th);
       return "Unable to create new interception. Detail: " + th + "\n";
     }
     return "Interception added\n";
   }
 }


