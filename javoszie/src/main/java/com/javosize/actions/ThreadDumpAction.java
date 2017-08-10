 package com.javosize.actions;
 
 import com.javosize.agent.session.UserThreadSessionTracker;
 import com.javosize.print.Table;
 import java.lang.management.ManagementFactory;
 import java.lang.management.RuntimeMXBean;
 import java.lang.management.ThreadInfo;
 import java.lang.management.ThreadMXBean;
 import java.text.DecimalFormat;
 
 public class ThreadDumpAction extends Action
 {
   private static final long serialVersionUID = -2368200046848456461L;
   private transient ThreadMXBean tmxb;
   
   public ThreadDumpAction(int terminalWidth, int terminalHeight)
   {
     this.terminalWidth = terminalWidth;
     this.terminalHeight = terminalHeight;
   }
   
   private String dumpThreadInfoWithLocks() {
     try {
       if (this.tmxb == null) {
         this.tmxb = ManagementFactory.getThreadMXBean();
       }
       ThreadInfo[] tinfos = this.tmxb.dumpAllThreads(true, true);
       
       Table table = new Table(this.terminalWidth);
       table.addColum("Id", 5);
       table.addColum("Name", 15);
       table.addColum("Lock.", 5);
       table.addColum("State", 10);
       table.addColum("%CPU", 5);
       table.addColum("load", 10);
       table.addColum("Exec. (s)", 10);
       table.addColum("Method", 15);
       table.addColum("Lock/wait on", 15);
       table.addColum("Blocked by", 10);
       
       for (ThreadInfo ti : tinfos) {
         printThread(ti, table);
       }
       return table.toString();
     } catch (Throwable th) {
       return "Remote ERROR executing THDump: " + th.toString();
     }
   }
   
   private void printThread(ThreadInfo ti, Table tb) throws com.javosize.print.InvalidColumNumber {
     String[] row = { "" + ti.getThreadId(), ti.getThreadName(), "" + (ti.getLockName() != null ? 1 : false), ti.getThreadState().name(), getUsedCpu(ti.getThreadId()), getThreadLoad(ti), UserThreadSessionTracker.getCurrentTimeByThread(String.valueOf(ti.getThreadId())), getCurrentMethod(ti), getLockName(ti), getLockOwnerName(ti) };
     tb.addRow(row);
   }
   
   private String getUsedCpu(long threadId) {
     long jvmUpTime = ManagementFactory.getRuntimeMXBean().getUptime();
     float value = (float)(100L * this.tmxb.getThreadCpuTime(threadId) / (1000000.0D * jvmUpTime));
     return "" + new DecimalFormat("###.##").format(value);
   }
   
   private String getThreadLoad(ThreadInfo ti)
   {
     try {
       long cpuTime = this.tmxb.getThreadCpuTime(ti.getThreadId());
       long waitingTime = ti.getWaitedTime();
       long bloquedTime = ti.getBlockedTime();
       
       if ((cpuTime == -1L) || (waitingTime == -1L) || (bloquedTime == -1L)) {
         return "Disabled";
       }
       return "" + new DecimalFormat("###.##").format(100L * cpuTime / (cpuTime + 1000000.0D * waitingTime + 1000000.0D * bloquedTime)) + "%";
     }
     catch (Exception e) {}
     return "Unsupported";
   }
   
   private String getCurrentMethod(ThreadInfo ti)
   {
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
   
   private String getLockName(ThreadInfo ti) {
     if (ti.getLockName() == null) {
       return "";
     }
     return ti.getLockName();
   }
   
   private String getLockOwnerName(ThreadInfo ti)
   {
     if (ti.getLockOwnerName() == null) {
       return "";
     }
     return ti.getLockOwnerName();
   }
   
 
   public String execute()
   {
     return dumpThreadInfoWithLocks();
   }
 }


