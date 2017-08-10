 package com.javosize.actions;
 
 import com.javosize.print.TextReport;
 import java.lang.management.GarbageCollectorMXBean;
 import java.lang.management.ManagementFactory;
 import java.lang.management.MemoryMXBean;
 import java.lang.management.MemoryUsage;
 import java.lang.management.RuntimeMXBean;
 import java.text.DecimalFormat;
 import java.text.NumberFormat;
 import java.util.List;
 import javax.management.MBeanServerConnection;
 import javax.management.ObjectName;
 
 
 public class MemoryAction
   extends Action
 {
   private static final long serialVersionUID = 9092593371084874414L;
   private static MemoryMXBean mem;
   private static MBeanServerConnection mbs = null;
   private static ObjectName osname = null;
   
   private static List<GarbageCollectorMXBean> gcs;
   
   private static RuntimeMXBean runTime;
   private long previousGCtime = 0L;
   private long previousUptime = 0L;
   private static final long BYTES_MB = 1048576L;
   private int cols;
   
   public MemoryAction(int cols)
   {
     this.cols = cols;
   }
   
   private static synchronized void init() {
     if ((mem == null) || (runTime == null) || (gcs == null)) {
       mem = ManagementFactory.getMemoryMXBean();
       runTime = ManagementFactory.getRuntimeMXBean();
       gcs = ManagementFactory.getGarbageCollectorMXBeans();
       mbs = ManagementFactory.getPlatformMBeanServer();
       try {
         osname = new ObjectName("java.lang:type=OperatingSystem");
       }
       catch (Exception localException) {}
     }
   }
   
 
   public String execute()
   {
     init();
     
     long memHeapMax = 0L;
     long memHeapUsed = 0L;
     long memHeapFree = 0L;
     
     long memNonHeapMax = 0L;
     long memNonHeapUsed = 0L;
     long memNonHeapFree = 0L;
     
     double gcPercentage = 0.0D;
     memHeapMax = mem.getHeapMemoryUsage().getMax() / 1048576L;
     if (memHeapMax <= 0L) {
       memHeapMax = mem.getHeapMemoryUsage().getCommitted() / 1048576L;
     }
     memHeapUsed = mem.getHeapMemoryUsage().getUsed() / 1048576L;
     memHeapFree = memHeapMax - memHeapUsed;
     
     memNonHeapMax = mem.getNonHeapMemoryUsage().getMax() / 1048576L;
     if (memNonHeapMax <= 0L) {
       memNonHeapMax = mem.getNonHeapMemoryUsage().getCommitted() / 1048576L;
     }
     memNonHeapUsed = mem.getNonHeapMemoryUsage().getUsed() / 1048576L;
     memNonHeapFree = memNonHeapMax - memNonHeapUsed;
     
     gcPercentage = getGcUsedPercentage().doubleValue();
     
     TextReport report = new TextReport();
     report.addSection("JVM Memory (Non-HEAP) (MB) ", new String[] { "Max: " + memNonHeapMax, "In Use: " + memNonHeapUsed, "Free: " + memNonHeapFree });
     report.addSection("JVM Memory (HEAP) (MB)", new String[] { "Max: " + memHeapMax, "In Use: " + memHeapUsed, "Free: " + memHeapFree });
     try
     {
       long memTotalRam = 0L;
       long memFreeRam = 0L;
       long memUsedRam = 0L;
       long memTotalSwap = 0L;
       long memFreeSwap = 0L;
       long memUsedSwap = 0L;
       memTotalRam = ((Long)mbs.getAttribute(osname, "TotalPhysicalMemorySize")).longValue() / 1048576L;
       memFreeRam = ((Long)mbs.getAttribute(osname, "FreePhysicalMemorySize")).longValue() / 1048576L;
       memUsedRam = memTotalRam - memFreeRam;
       
       memTotalSwap = ((Long)mbs.getAttribute(osname, "TotalSwapSpaceSize")).longValue() / 1048576L;
       memFreeSwap = ((Long)mbs.getAttribute(osname, "FreeSwapSpaceSize")).longValue() / 1048576L;
       memUsedSwap = memTotalSwap - memFreeSwap;
       
       report.addSection("OS RAM (MB)", new String[] { "Total: " + memTotalRam, "In Use: " + memUsedRam, "Free: " + memFreeRam });
       report.addSection("SWAP (MB)", new String[] { "Total: " + memTotalSwap, "In Use: " + memUsedSwap, "Free: " + memFreeSwap });
     }
     catch (Exception localException) {}
     
     NumberFormat formatter = new DecimalFormat("#0.00");
     report.addSection("GC", new String[] { "Used time(%): " + formatter.format(gcPercentage) });
     return report.toString();
   }
   
   private Double getGcUsedPercentage() {
     try {
       long gcTime = 0L;
       for (GarbageCollectorMXBean gc : gcs) {
         gcTime += gc.getCollectionTime();
       }
       long uptime = runTime.getUptime();
       return new Double(gcTime * 100.0D / uptime);
     } catch (Exception e) {}
     return new Double(0.0D);
   }
 }


