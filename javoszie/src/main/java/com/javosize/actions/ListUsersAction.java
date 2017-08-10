 package com.javosize.actions;
 
 import com.javosize.agent.session.UserThreadSessionTracker;
 import com.javosize.print.Table;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Map;
 
 
 public class ListUsersAction
   extends Action
 {
   private static final long serialVersionUID = -2368200046848456461L;
   
   public ListUsersAction(int terminalWidth, int terminalHeight)
   {
     this.terminalWidth = terminalWidth;
     this.terminalHeight = terminalHeight;
   }
   
   private String listUsers()
   {
     try {
       Map<String, Object> m = UserThreadSessionTracker.getAllSessionsMap();
       Collection<String> sessionIds = m.keySet();
       Table table = new Table(this.terminalWidth);
       table.addColum("User", 25);
       table.addColum("% CPU", 25);
       table.addColum("AvgRT(ms)", 25);
       table.addColum("Hits", 25);
       
 
       HashMap<String, String> alreadyAddedUsers = new HashMap();
       synchronized (m) {
         for (String sessionId : sessionIds) {
           String userId = UserThreadSessionTracker.getUserForSession(sessionId);
           if ((userId != "") && (!alreadyAddedUsers.containsKey(userId))) {
             String[] row = { userId, UserThreadSessionTracker.getCpuPerUser(userId), UserThreadSessionTracker.getAvgRtPerUser(userId), UserThreadSessionTracker.getHitPerUser(userId) };
             table.addRow(row);
             alreadyAddedUsers.put(userId, userId);
           }
         }
       }
       return table.toString();
     } catch (Throwable th) {
       return "Remote ERROR retrieving users: " + th.toString();
     }
   }
   
 
 
   public String execute()
   {
     return listUsers();
   }
 }


