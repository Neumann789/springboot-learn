 package com.javosize.actions;
 
 import com.javosize.agent.session.UserThreadSessionTracker;
 import com.javosize.print.InvalidColumNumber;
 import com.javosize.print.Table;
 import java.lang.management.ManagementFactory;
 import java.lang.management.ThreadInfo;
 import java.lang.management.ThreadMXBean;
 
 public class ListApplicationsThreadsAction extends Action
 {
   private static final long serialVersionUID = -2368200046848456461L;
   private transient ThreadMXBean tmxb;
   
   public ListApplicationsThreadsAction(int terminalWidth, int terminalHeight)
   {
     this.terminalWidth = terminalWidth;
     this.terminalHeight = terminalHeight;
   }
   
   private String dumpApplicationsThreadsInfo() {
     try {
       if (this.tmxb == null) {
         this.tmxb = ManagementFactory.getThreadMXBean();
       }
       ThreadInfo[] tinfos = this.tmxb.dumpAllThreads(true, true);
       
       Table table = new Table(this.terminalWidth);
       table.addColum("Id", 5);
       table.addColum("App", 15);
       table.addColum("URL", 30);
       table.addColum("User", 10);
       table.addColum("Exec. (s)", 10);
       table.addColum("%CPU", 5);
       table.addColum("Allocated Bytes", 10);
       table.addColum("Method", 15);
       
       for (ThreadInfo ti : tinfos) {
         printThread(ti, table);
       }
       return table.toString();
     } catch (Throwable th) {
       return "Remote ERROR executing THDump: " + th.toString();
     }
   }
   
   private void printThread(ThreadInfo ti, Table tb) throws InvalidColumNumber {
     String threadID = "" + ti.getThreadId();
     String appName = UserThreadSessionTracker.getAppByThread(threadID);
     if (appName != null)
     {
 
 
 
 
 
 
 
       String[] row = { "" + ti.getThreadId(), appName, UserThreadSessionTracker.getURLByThread(threadID), UserThreadSessionTracker.getUserForThread(threadID), UserThreadSessionTracker.getCurrentTimeByThread(threadID), UserThreadSessionTracker.getCurrentCPUByThread(threadID), UserThreadSessionTracker.getCurrentAllocatedMemoryByThread(threadID), getCurrentMethod(ti) };
       
       tb.addRow(row);
     }
   }
   
   private String getCurrentMethod(ThreadInfo ti) {
     if (ti.getStackTrace().length == 0) {
       return "";
     }
     StackTraceElement executingMethod = ti.getStackTrace()[0];
     String result = executingMethod.getClassName() + "." + executingMethod.getMethodName();
     if (executingMethod.getLineNumber() > 0) {
       result = result + "[" + executingMethod.getLineNumber() + "]";
     }
     return result;
   }
   
   public String execute()
   {
     return dumpApplicationsThreadsInfo();
   }
 }


