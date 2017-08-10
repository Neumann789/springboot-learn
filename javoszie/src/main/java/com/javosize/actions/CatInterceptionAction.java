 package com.javosize.actions;
 
 import com.javosize.agent.Interception;
 import com.javosize.agent.Interception.Type;
 import com.javosize.print.TextReport;
 import java.util.List;
 
 public class CatInterceptionAction extends Action
 {
   private static final long serialVersionUID = 6309038877613667056L;
   private String id = "";
   
   public CatInterceptionAction(String id) {
     this.id = id;
   }
   
   public String execute()
   {
     List<Interception> result = com.javosize.agent.Agent.getInterceptions();
     TextReport results = new TextReport();
     for (Interception inter : result) {
       if (inter.getId().equals(this.id)) {
         results.addSection("General", new String[] { "Id: " + inter.getId(), "ClassName RegExp: " + inter.getClassNameRegexp(), "MethodName Regexp: " + inter.getMethodNameRegexp(), "Type: " + inter.getType().toString() });
         results.addSection("Detail", new String[] { "src: " + inter.getInterceptorCode() });
       }
     }
     return results.toString() + "\n";
   }
 }


