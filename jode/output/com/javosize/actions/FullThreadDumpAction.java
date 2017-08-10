/* FullThreadDumpAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.actions;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import com.javosize.print.TextReport;

public class FullThreadDumpAction extends Action
{
    private static final long serialVersionUID = -2368200046848456461L;
    private transient ThreadMXBean tmxb;
    private static final String INDENT = "    ";
    
    private String dumpThreadDetails() {
	String string;
	try {
	    if (tmxb == null)
		tmxb = ManagementFactory.getThreadMXBean();
	    TextReport report = new TextReport();
	    report.addSection("Full Thread Dump",
			      new String[] { getFullThreadDump(tmxb) });
	    string = report.toString();
	} catch (Throwable th) {
	    return new StringBuilder().append
		       ("Remote ERROR getting full thread dump: ").append
		       (th.toString()).toString();
	}
	return string;
    }
    
    private String getFullThreadDump(ThreadMXBean tmxb) {
	ThreadDetailAction tda = new ThreadDetailAction();
	ThreadInfo[] tinfos = tmxb.dumpAllThreads(true, true);
	StringBuilder sb = new StringBuilder("");
	boolean first = true;
	ThreadInfo[] threadinfos = tinfos;
	int i = threadinfos.length;
	for (int i_0_ = 0; i_0_ < i; i_0_++) {
	    ThreadInfo ti = threadinfos[i_0_];
	    if (ti != null) {
		if (first) {
		    sb.append(new StringBuilder().append
				  (tda.getDumpForThread(ti)).append
				  ("\n").toString());
		    first = false;
		} else
		    sb.append(new StringBuilder().append("    ").append
				  (tda.getDumpForThread(ti)).append
				  ("\n").toString());
	    }
	}
	return sb.toString();
    }
    
    public String execute() {
	return dumpThreadDetails();
    }
}
