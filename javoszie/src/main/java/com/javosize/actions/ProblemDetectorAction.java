 package com.javosize.actions;
 
 import com.javosize.log.Log;
 import com.javosize.print.TextReport;
 import java.lang.management.GarbageCollectorMXBean;
 import java.lang.management.ManagementFactory;
 import java.lang.management.RuntimeMXBean;
 
 public class ProblemDetectorAction extends Action
 {
   private static final long serialVersionUID = 1095734772709624349L;
   private static Log log = new Log(ProblemDetectorAction.class.getName());
   private static final int GC_THRESHOLD_PCTG = 2;
   
   public ProblemDetectorAction(int cols) {
     this.terminalWidth = cols;
   }
   
 
 
 
 
 
 
 
 
 
 
   public String execute()
   {
     TextReport report = new TextReport();
     report.addSection("Concurrency", new String[] { "Deadlocked: " + isThereadDeadLocked() });
     report.addSection("Memory", new String[] { "High GC (>2%): " + isGCVeryActive() });
     return report.toString();
   }
   
   private boolean isGCVeryActive() {
     try {
       long gcTime = 0L;
       for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
         gcTime += gc.getCollectionTime();
       }
       long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
       return gcTime * 100.0D / uptime > 2.0D;
     } catch (Exception e) {}
     return false;
   }
   
   private boolean isThereadDeadLocked()
   {
     long[] threadLocked = ManagementFactory.getThreadMXBean().findDeadlockedThreads();
     return (threadLocked != null) && (threadLocked.length > 0);
   }
 }


