 package com.javosize.actions;
 
 import com.javosize.agent.session.UserThreadSessionTracker;
 import com.javosize.print.InvalidColumNumber;
 import com.javosize.print.Table;
 import java.util.Collection;
 import java.util.Map;
 
 public class ListApplicationsAction
   extends Action
 {
   private static final long serialVersionUID = -2368200046848456461L;
   
   public ListApplicationsAction(int terminalWidth, int terminalHeight)
   {
     this.terminalWidth = terminalWidth;
     this.terminalHeight = terminalHeight;
   }
   
   private String listApps()
   {
     try {
       Map<String, ClassLoader> map = UserThreadSessionTracker.getAllApplications();
       Collection<String> applications = map.keySet();
       Table table = new Table(this.terminalWidth);
       table.addColum("Application", 25);
       table.addColum("% CPU", 25);
       table.addColum("AvgRT(ms)", 25);
       table.addColum("Hits", 25);
       
       synchronized (map) {
         for (String app : applications) {
           printAppInfo(app, table);
         }
       }
       
       return table.toString();
     } catch (Throwable th) {
       return "Remote ERROR retrieving applications: " + th.toString();
     }
   }
   
   private void printAppInfo(String applicationName, Table tb) throws InvalidColumNumber {
     if ((applicationName != "") && (applicationName != null)) {
       String[] row = { applicationName, UserThreadSessionTracker.getCpuPerApp(applicationName), UserThreadSessionTracker.getAvgRtPerApp(applicationName), UserThreadSessionTracker.getHitsPerApp(applicationName) };
       tb.addRow(row);
     }
   }
   
   public String execute()
   {
     return listApps();
   }
 }


