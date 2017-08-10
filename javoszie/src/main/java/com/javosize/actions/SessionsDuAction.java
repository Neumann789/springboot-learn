 package com.javosize.actions;
 
 import com.javosize.agent.Agent;
 import com.javosize.agent.HttpSessionHelperFactory;
 import com.javosize.agent.session.UserThreadSessionTracker;
 import com.javosize.print.TextReport;
 
 
 public class SessionsDuAction
   extends Action
 {
   private static final long serialVersionUID = 3464512134721516345L;
   private String id;
   
   public SessionsDuAction(String id)
   {
     this.id = id;
   }
   
   public String execute()
   {
     Class helper = HttpSessionHelperFactory.getSessionHelper();
     TextReport report = new TextReport();
     Object session = UserThreadSessionTracker.getSessionById(this.id);
     String size = "" + Agent.getObjectDeepSize(session);
     report.addSection("Deep Session Size (bytes)", new String[] { size });
     return report.toString() + "\n";
   }
 }


