 package com.javosize.actions;
 
 import com.javosize.agent.session.UserThreadSessionTracker;
 import com.javosize.log.Log;
 import com.javosize.print.InvalidColumNumber;
 import com.javosize.print.Table;
 import java.util.Set;
 
 public class ListApplicationsDetailAction
   extends Action
 {
   private static final long serialVersionUID = -2368200046848456461L;
   private static Log log = new Log(ListApplicationsDetailAction.class.getName());
   
   private String appName;
   
   public ListApplicationsDetailAction(String appName, int terminalWidth)
   {
     this.terminalWidth = terminalWidth;
     this.appName = appName;
   }
   
   private String listAppDetail()
   {
     try {
       Set<String> urls = UserThreadSessionTracker.getURLsPerApp(this.appName);
       
       Table table = new Table(this.terminalWidth);
       table.addColum("URL", 40);
       table.addColum("% CPU", 20);
       table.addColum("AvgRT(ms)", 20);
       table.addColum("Hits", 20);
       for (String url : urls) {
         printAppInfoDetail(url, table);
       }
       
       return table.toString();
     } catch (Throwable th) {
       log.error("Error retrieving app(" + this.appName + ") detail: " + th, th);
       return "Remote ERROR retrieving application detail: " + th.toString();
     }
   }
   
   private void printAppInfoDetail(String url, Table tb) throws InvalidColumNumber {
     if ((url != "") && (url != null)) {
       String[] row = { url, UserThreadSessionTracker.getCpuPerUrl(url), UserThreadSessionTracker.getAvgRtPerURL(url), UserThreadSessionTracker.getHitPerURL(url) };
       tb.addRow(row);
     }
   }
   
   public String execute()
   {
     return listAppDetail();
   }
 }


