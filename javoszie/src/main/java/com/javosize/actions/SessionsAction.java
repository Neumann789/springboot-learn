 package com.javosize.actions;
 
 import com.javosize.agent.HttpSessionHelperFactory;
 import com.javosize.agent.ReflectionUtils;
 import com.javosize.agent.session.UserThreadSessionTracker;
 import com.javosize.agent.session.UserThreadSessionTracker.CpuTimeCounter;
 import com.javosize.log.Log;
 import com.javosize.print.InvalidColumNumber;
 import com.javosize.print.Table;
 import java.util.Map;
 import java.util.Map.Entry;
 
 public class SessionsAction
   extends Action
 {
   private static final long serialVersionUID = 3464512134721516345L;
   private static Log log = new Log(SessionsAction.class.getName());
   
   public SessionsAction(int terminalWidth, int terminalHeight) {
     this.terminalWidth = terminalWidth;
     this.terminalHeight = terminalHeight;
   }
   
   public String execute()
   {
     Class helper = HttpSessionHelperFactory.getSessionHelper();
     Table table = new Table(this.terminalWidth);
     table.addColum("Session ID", 34);
     table.addColum("% CPU", 5);
     table.addColum("# Objs", 5);
     table.addColum("Creation Date", 20);
     table.addColum("Last Accessed Time", 20);
     table.addColum("User", 10);
     
 
     Map<String, UserThreadSessionTracker.CpuTimeCounter> sessions = UserThreadSessionTracker.getCpuForSessions();
     
     synchronized (sessions) {
       for (Map.Entry<String, UserThreadSessionTracker.CpuTimeCounter> sessionEntry : sessions.entrySet()) {
         try
         {
           Object session = UserThreadSessionTracker.getSessionById((String)sessionEntry.getKey());
           if (session == null)
           {
             UserThreadSessionTracker.purgeSession((String)sessionEntry.getKey());
           }
           else
           {
             String elements = (String)ReflectionUtils.invokeStaticMethod(helper, "getNumberOfElements", new Class[] { Object.class }, new Object[] { session });
             
 
             if (elements == null)
             {
               UserThreadSessionTracker.purgeSession((String)sessionEntry.getKey());
             }
             else {
               String date = (String)ReflectionUtils.invokeStaticMethod(helper, "getActiveDate", new Class[] { Object.class }, new Object[] { session });
               
 
               String lastDate = (String)ReflectionUtils.invokeStaticMethod(helper, "getLastAccessedTime", new Class[] { Object.class }, new Object[] { session });
               
 
 
               String user = UserThreadSessionTracker.getUserForSession((String)sessionEntry.getKey());
               table.addRow(new String[] { (String)sessionEntry.getKey(), 
                 ((UserThreadSessionTracker.CpuTimeCounter)sessionEntry.getValue()).getCpu(), elements, date, lastDate, user });
             }
           }
         } catch (InvalidColumNumber e) {
           log.error("Error executing sessions action: " + e, e);
         }
       }
     }
     return table.toString() + "\n";
   }
 }


