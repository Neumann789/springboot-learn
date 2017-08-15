/* ListApplicationsThreadsAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.actions;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import com.javosize.agent.session.UserThreadSessionTracker;
import com.javosize.print.InvalidColumNumber;
import com.javosize.print.Table;

public class ListApplicationsThreadsAction extends Action
{
    private static final long serialVersionUID = -2368200046848456461L;
    private transient ThreadMXBean tmxb;
    
    public ListApplicationsThreadsAction(int terminalWidth,
					 int terminalHeight) {
	this.terminalWidth = terminalWidth;
	this.terminalHeight = terminalHeight;
    }
    
    private String dumpApplicationsThreadsInfo() {
	String string;
	try {
	    if (tmxb == null)
		tmxb = ManagementFactory.getThreadMXBean();
	    ThreadInfo[] tinfos = tmxb.dumpAllThreads(true, true);
	    Table table = new Table(terminalWidth);
	    table.addColum("Id", 5);
	    table.addColum("App", 15);
	    table.addColum("URL", 30);
	    table.addColum("User", 10);
	    table.addColum("Exec. (s)", 10);
	    table.addColum("%CPU", 5);
	    table.addColum("Allocated Bytes", 10);
	    table.addColum("Method", 15);
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
	String threadID = new StringBuilder().append("").append
			      (ti.getThreadId()).toString();
	String appName = UserThreadSessionTracker.getAppByThread(threadID);
	if (appName != null) {
	    String[] row
		= { new StringBuilder().append("").append(ti.getThreadId())
			.toString(),
		    appName, UserThreadSessionTracker.getURLByThread(threadID),
		    UserThreadSessionTracker.getUserForThread(threadID),
		    UserThreadSessionTracker.getCurrentTimeByThread(threadID),
		    UserThreadSessionTracker.getCurrentCPUByThread(threadID),
		    UserThreadSessionTracker
			.getCurrentAllocatedMemoryByThread(threadID),
		    getCurrentMethod(ti) };
	    tb.addRow(row);
	}
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
    
    public String execute() {
	return dumpApplicationsThreadsInfo();
    }
}
