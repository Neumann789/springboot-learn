/* MemoryAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.actions;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import com.javosize.print.TextReport;

public class MemoryAction extends Action
{
    private static final long serialVersionUID = 9092593371084874414L;
    private static MemoryMXBean mem;
    private static MBeanServerConnection mbs = null;
    private static ObjectName osname = null;
    private static List gcs;
    private static RuntimeMXBean runTime;
    private long previousGCtime = 0L;
    private long previousUptime = 0L;
    private static final long BYTES_MB = 1048576L;
    private int cols;
    
    public MemoryAction(int cols) {
	this.cols = cols;
    }
    
    private static synchronized void init() {
	if (mem == null || runTime == null || gcs == null) {
	    mem = ManagementFactory.getMemoryMXBean();
	    runTime = ManagementFactory.getRuntimeMXBean();
	    gcs = ManagementFactory.getGarbageCollectorMXBeans();
	    mbs = ManagementFactory.getPlatformMBeanServer();
	    try {
		osname = new ObjectName("java.lang:type=OperatingSystem");
	    } catch (Exception exception) {
		/* empty */
	    }
	}
    }
    
    public String execute() {
	init();
	long memHeapMax = 0L;
	long memHeapUsed = 0L;
	long memHeapFree = 0L;
	long memNonHeapMax = 0L;
	long memNonHeapUsed = 0L;
	long memNonHeapFree = 0L;
	double gcPercentage = 0.0;
	memHeapMax = mem.getHeapMemoryUsage().getMax() / 1048576L;
	if (memHeapMax <= 0L)
	    memHeapMax = mem.getHeapMemoryUsage().getCommitted() / 1048576L;
	memHeapUsed = mem.getHeapMemoryUsage().getUsed() / 1048576L;
	memHeapFree = memHeapMax - memHeapUsed;
	memNonHeapMax = mem.getNonHeapMemoryUsage().getMax() / 1048576L;
	if (memNonHeapMax <= 0L)
	    memNonHeapMax
		= mem.getNonHeapMemoryUsage().getCommitted() / 1048576L;
	memNonHeapUsed = mem.getNonHeapMemoryUsage().getUsed() / 1048576L;
	memNonHeapFree = memNonHeapMax - memNonHeapUsed;
	gcPercentage = getGcUsedPercentage().doubleValue();
	TextReport report = new TextReport();
	report.addSection("JVM Memory (Non-HEAP) (MB) ",
			  (new String[]
			   { new StringBuilder().append("Max: ").append
				 (memNonHeapMax).toString(),
			     new StringBuilder().append("In Use: ").append
				 (memNonHeapUsed).toString(),
			     new StringBuilder().append("Free: ").append
				 (memNonHeapFree).toString() }));
	report.addSection("JVM Memory (HEAP) (MB)",
			  (new String[]
			   { new StringBuilder().append("Max: ").append
				 (memHeapMax).toString(),
			     new StringBuilder().append("In Use: ").append
				 (memHeapUsed).toString(),
			     new StringBuilder().append("Free: ").append
				 (memHeapFree).toString() }));
	try {
	    long memTotalRam = 0L;
	    long memFreeRam = 0L;
	    long memUsedRam = 0L;
	    long memTotalSwap = 0L;
	    long memFreeSwap = 0L;
	    long memUsedSwap = 0L;
	    memTotalRam
		= ((Long) mbs.getAttribute(osname, "TotalPhysicalMemorySize"))
		      .longValue() / 1048576L;
	    memFreeRam
		= ((Long) mbs.getAttribute(osname, "FreePhysicalMemorySize"))
		      .longValue() / 1048576L;
	    memUsedRam = memTotalRam - memFreeRam;
	    memTotalSwap
		= ((Long) mbs.getAttribute(osname, "TotalSwapSpaceSize"))
		      .longValue() / 1048576L;
	    memFreeSwap
		= ((Long) mbs.getAttribute(osname, "FreeSwapSpaceSize"))
		      .longValue() / 1048576L;
	    memUsedSwap = memTotalSwap - memFreeSwap;
	    report.addSection("OS RAM (MB)",
			      (new String[]
			       { new StringBuilder().append("Total: ").append
				     (memTotalRam).toString(),
				 new StringBuilder().append("In Use: ").append
				     (memUsedRam).toString(),
				 new StringBuilder().append("Free: ").append
				     (memFreeRam).toString() }));
	    report.addSection("SWAP (MB)",
			      (new String[]
			       { new StringBuilder().append("Total: ").append
				     (memTotalSwap).toString(),
				 new StringBuilder().append("In Use: ").append
				     (memUsedSwap).toString(),
				 new StringBuilder().append("Free: ").append
				     (memFreeSwap).toString() }));
	} catch (Exception exception) {
	    /* empty */
	}
	java.text.NumberFormat formatter = new DecimalFormat("#0.00");
	report.addSection("GC",
			  new String[] { new StringBuilder().append
					     ("Used time(%): ").append
					     (formatter.format(gcPercentage))
					     .toString() });
	return report.toString();
    }
    
    private Double getGcUsedPercentage() {
	Double var_double;
	try {
	    long gcTime = 0L;
	    Iterator iterator = gcs.iterator();
	    while (iterator.hasNext()) {
		GarbageCollectorMXBean gc
		    = (GarbageCollectorMXBean) iterator.next();
		gcTime += gc.getCollectionTime();
	    }
	    long uptime = runTime.getUptime();
	    var_double = new Double((double) gcTime * 100.0 / (double) uptime);
	} catch (Exception e) {
	    return new Double(0.0);
	}
	return var_double;
    }
}
