/* ThreadDumpAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.actions;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;

import com.javosize.agent.session.UserThreadSessionTracker;
import com.javosize.print.InvalidColumNumber;
import com.javosize.print.Table;

public class ThreadDumpAction extends Action
{
    private static final long serialVersionUID = -2368200046848456461L;
    private transient ThreadMXBean tmxb;
    
    public ThreadDumpAction(int terminalWidth, int terminalHeight) {
	this.terminalWidth = terminalWidth;
	this.terminalHeight = terminalHeight;
    }
    
    private String dumpThreadInfoWithLocks() {
	String string;
	try {
	    if (tmxb == null)
		tmxb = ManagementFactory.getThreadMXBean();
	    ThreadInfo[] tinfos = tmxb.dumpAllThreads(true, true);
	    Table table = new Table(terminalWidth);
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
	    ThreadInfo[] threadinfos = tinfos;
	    int i = threadinfos.length;
	    for (int i_0_ = 0; i_0_ < i; i_0_++) {
		ThreadInfo ti = threadinfos[i_0_];
		printThread(ti, table);
	    }
	    string = table.toString();
	} catch (Throwable th) {
	    return new StringBuilder().append
		       ("Remote ERROR executing THDump: ").append
		       (th.toString()).toString();
	}
	return string;
    }
    
    private void printThread(ThreadInfo ti, Table tb)
	throws InvalidColumNumber {
	String[] row
	    = { new StringBuilder().append("").append(ti.getThreadId())
		    .toString(),
		ti.getThreadName(),
		new StringBuilder().append("").append
		    (ti.getLockName() != null).toString(),
		ti.getThreadState().name(), getUsedCpu(ti.getThreadId()),
		getThreadLoad(ti),
		UserThreadSessionTracker
		    .getCurrentTimeByThread(String.valueOf(ti.getThreadId())),
		getCurrentMethod(ti), getLockName(ti), getLockOwnerName(ti) };
	tb.addRow(row);
    }
    
    private String getUsedCpu(long threadId) {
	long jvmUpTime = ManagementFactory.getRuntimeMXBean().getUptime();
	float value
	    = (float) ((double) (100L * tmxb.getThreadCpuTime(threadId))
		       / (1000000.0 * (double) jvmUpTime));
	return new StringBuilder().append("").append
		   (new DecimalFormat("###.##").format((double) value))
		   .toString();
    }
    
    private String getThreadLoad(ThreadInfo ti) {
	exception = exception_2_;
	break;
    }
    
    private String getCurrentMethod(ThreadInfo ti) {
	if (ti.getStackTrace().length == 0)
	    return "";
	StackTraceElement executingMethod = ti.getStackTrace()[0];
	String result
	    = new StringBuilder().append(executingMethod.getClassName()).append
		  (".").append
		  (executingMethod.getMethodName()).toString();
	if (executingMethod.getLineNumber() > 0)
	    result = new StringBuilder().append(result).append("[").append
			 (executingMethod.getLineNumber()).append
			 ("]").toString();
	return result;
    }
    
    private String getLockName(ThreadInfo ti) {
	if (ti.getLockName() == null)
	    return "";
	return ti.getLockName();
    }
    
    private String getLockOwnerName(ThreadInfo ti) {
	if (ti.getLockOwnerName() == null)
	    return "";
	return ti.getLockOwnerName();
    }
    
    public String execute() {
	return dumpThreadInfoWithLocks();
    }
}
