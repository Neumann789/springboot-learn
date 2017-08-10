/* ThreadDetailAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.actions;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import com.javosize.agent.session.UserThreadSessionTracker;
import com.javosize.print.TextReport;

public class ThreadDetailAction extends Action
{
    private static final long serialVersionUID = -2368200046848456461L;
    private transient ThreadMXBean tmxb;
    private String threadId = "";
    
    private String dumpThreadDetails() {
	throwable = throwable_5_;
	break while_1_;
    }
    
    public String getDumpForThread(ThreadInfo ti) {
	StringBuilder sb
	    = new StringBuilder(new StringBuilder().append("\"").append
				    (ti.getThreadName()).append
				    ("\"").append
				    (" Id=").append
				    (ti.getThreadId()).append
				    (" ").append
				    (ti.getThreadState()).toString());
	if (ti.getLockName() != null)
	    sb.append(new StringBuilder().append(" on ").append
			  (ti.getLockName()).toString());
	if (ti.getLockOwnerName() != null)
	    sb.append(new StringBuilder().append(" owned by \"").append
			  (ti.getLockOwnerName()).append
			  ("\" Id=").append
			  (ti.getLockOwnerId()).toString());
	if (ti.isSuspended())
	    sb.append(" (suspended)");
	if (ti.isInNative())
	    sb.append(" (in native)");
	sb.append('\n');
	int i = 0;
	StackTraceElement[] stackTrace;
	for (stackTrace = ti.getStackTrace(); i < stackTrace.length; i++) {
	    StackTraceElement ste = stackTrace[i];
	    sb.append(new StringBuilder().append("\tat ").append
			  (ste.toString()).toString());
	    sb.append('\n');
	    if (i == 0 && ti.getLockInfo() != null) {
		Thread.State ts = ti.getThreadState();
		if (ts.equals(Thread.State.BLOCKED)) {
		    sb.append(new StringBuilder().append
				  ("\t-  blocked on ").append
				  (ti.getLockInfo()).toString());
		    sb.append('\n');
		} else if (ts.equals(Thread.State.WAITING)) {
		    sb.append(new StringBuilder().append
				  ("\t-  waiting on ").append
				  (ti.getLockInfo()).toString());
		    sb.append('\n');
		} else if (ts.equals(Thread.State.TIMED_WAITING)) {
		    sb.append(new StringBuilder().append
				  ("\t-  waiting on ").append
				  (ti.getLockInfo()).toString());
		    sb.append('\n');
		}
	    }
	    MonitorInfo[] lockedMonitors = ti.getLockedMonitors();
	    MonitorInfo[] monitorinfos = lockedMonitors;
	    int i_6_ = monitorinfos.length;
	    for (int i_7_ = 0; i_7_ < i_6_; i_7_++) {
		MonitorInfo mi = monitorinfos[i_7_];
		if (mi.getLockedStackDepth() == i) {
		    sb.append(new StringBuilder().append("\t-  locked ").append
				  (mi).toString());
		    sb.append('\n');
		}
	    }
	}
	if (i < stackTrace.length) {
	    sb.append("\t...");
	    sb.append('\n');
	}
	java.lang.management.LockInfo[] locks = ti.getLockedSynchronizers();
	if (locks.length > 0) {
	    sb.append(new StringBuilder().append
			  ("\n\tNumber of locked synchronizers = ").append
			  (locks.length).toString());
	    sb.append('\n');
	    java.lang.management.LockInfo[] lockinfos = locks;
	    int i_8_ = lockinfos.length;
	    for (int i_9_ = 0; i_9_ < i_8_; i_9_++) {
		java.lang.management.LockInfo li = lockinfos[i_9_];
		sb.append(new StringBuilder().append("\t- ").append(li)
			      .toString());
		sb.append('\n');
	    }
	}
	sb.append('\n');
	return sb.toString();
    }
    
    private String emptyIfNull(String str) {
	if (str == null)
	    return "";
	return str;
    }
    
    public void setThreadId(String threadId) {
	this.threadId = threadId;
    }
    
    public String execute() {
	return dumpThreadDetails();
    }
}
