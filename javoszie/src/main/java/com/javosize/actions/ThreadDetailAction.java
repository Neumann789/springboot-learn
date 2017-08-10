 package com.javosize.actions;
 
 import com.javosize.agent.session.UserThreadSessionTracker;
 import com.javosize.print.TextReport;
 import java.lang.management.LockInfo;
 import java.lang.management.ManagementFactory;
 import java.lang.management.MonitorInfo;
 import java.lang.management.ThreadInfo;
 import java.lang.management.ThreadMXBean;
 
 
 
 
 public class ThreadDetailAction
   extends Action
 {
   private static final long serialVersionUID = -2368200046848456461L;
   private transient ThreadMXBean tmxb;
   private String threadId = "";
   
   private String dumpThreadDetails() {
     try {
       if (Integer.valueOf(this.threadId).intValue() <= 0) {
         return "You must specify a valid thread id\n";
       }
       if (this.tmxb == null) {
         this.tmxb = ManagementFactory.getThreadMXBean();
       }
       long[] tids = { Long.valueOf(this.threadId).longValue() };
       ThreadInfo[] tinfo = this.tmxb.getThreadInfo(tids, true, true);
       ThreadInfo ti = tinfo[0];
       if (ti == null) {
         return "The requested thread does not longer exist\n";
       }
       
       TextReport report = new TextReport();
       report.addSection("Thread name", new String[] { ti.getThreadName() });
       report.addSection("Thread state", new String[] { ti.getThreadState().name() });
       report.addSection("Locked at / waiting on", new String[] { emptyIfNull(ti.getLockName()) });
       report.addSection("Blocked by", new String[] { emptyIfNull(ti.getLockOwnerName()) });
       report.addSection("Stack trace", new String[] { getDumpForThread(ti) });
       report.addSection("Application", new String[] { emptyIfNull(UserThreadSessionTracker.getAppByThread(this.threadId)) });
       report.addSection("User id", new String[] { UserThreadSessionTracker.getUserForThread(this.threadId) });
       report.addSection("Session id", new String[] { UserThreadSessionTracker.getSessionForThread(this.threadId) });
       report.addSection("URL", new String[] { emptyIfNull(UserThreadSessionTracker.getURLByThread(this.threadId)) });
       report.addSection("Execution time (s)", new String[] { UserThreadSessionTracker.getCurrentTimeByThread(this.threadId) });
       
       return report.toString();
     }
     catch (NumberFormatException nfe) {
       return "You must specify a valid thread id\n";
     }
     catch (Throwable th) {
       return "Remote ERROR getting thread details for thread " + this.threadId + " : " + th.toString();
     }
   }
   
 
 
   public String getDumpForThread(ThreadInfo ti)
   {
     StringBuilder sb = new StringBuilder("\"" + ti.getThreadName() + "\"" + " Id=" + ti.getThreadId() + " " + ti.getThreadState());
     if (ti.getLockName() != null) {
       sb.append(" on " + ti.getLockName());
     }
     if (ti.getLockOwnerName() != null) {
       sb.append(" owned by \"" + ti.getLockOwnerName() + "\" Id=" + ti
         .getLockOwnerId());
     }
     if (ti.isSuspended()) {
       sb.append(" (suspended)");
     }
     if (ti.isInNative()) {
       sb.append(" (in native)");
     }
     sb.append('\n');
     int i = 0;
     StackTraceElement[] stackTrace = ti.getStackTrace();
     MonitorInfo[] lockedMonitors; for (; i < stackTrace.length; i++) {
       StackTraceElement ste = stackTrace[i];
       sb.append("\tat " + ste.toString());
       sb.append('\n');
       if ((i == 0) && (ti.getLockInfo() != null)) {
         Thread.State ts = ti.getThreadState();
         if (ts.equals(Thread.State.BLOCKED)) {
           sb.append("\t-  blocked on " + ti.getLockInfo());
           sb.append('\n');
         } else if (ts.equals(Thread.State.WAITING)) {
           sb.append("\t-  waiting on " + ti.getLockInfo());
           sb.append('\n');
         } else if (ts.equals(Thread.State.TIMED_WAITING)) {
           sb.append("\t-  waiting on " + ti.getLockInfo());
           sb.append('\n');
         }
       }
       lockedMonitors = ti.getLockedMonitors();
       for (MonitorInfo mi : lockedMonitors) {
         if (mi.getLockedStackDepth() == i) {
           sb.append("\t-  locked " + mi);
           sb.append('\n');
         }
       }
     }
     if (i < stackTrace.length) {
       sb.append("\t...");
       sb.append('\n');
     }
     
     LockInfo[] locks = ti.getLockedSynchronizers();
     if (locks.length > 0) {
       sb.append("\n\tNumber of locked synchronizers = " + locks.length);
       sb.append('\n');
       for (Object li : locks) {
         sb.append("\t- " + li);
         sb.append('\n');
       }
     }
     sb.append('\n');
     return sb.toString();
   }
   
   private String emptyIfNull(String str) {
     if (str == null) {
       return "";
     }
     return str;
   }
   
   public void setThreadId(String threadId)
   {
     this.threadId = threadId;
   }
   
   public String execute()
   {
     return dumpThreadDetails();
   }
 }


