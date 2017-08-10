/* ListApplicationsAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.actions;
import java.util.Iterator;
import java.util.Map;

import com.javosize.agent.session.UserThreadSessionTracker;
import com.javosize.print.InvalidColumNumber;
import com.javosize.print.Table;

public class ListApplicationsAction extends Action
{
    private static final long serialVersionUID = -2368200046848456461L;
    
    public ListApplicationsAction(int terminalWidth, int terminalHeight) {
	this.terminalWidth = terminalWidth;
	this.terminalHeight = terminalHeight;
    }
    
    private String listApps() {
	String string;
	try {
	    Map map = UserThreadSessionTracker.getAllApplications();
	    java.util.Collection applications = map.keySet();
	    Table table = new Table(terminalWidth);
	    table.addColum("Application", 25);
	    table.addColum("% CPU", 25);
	    table.addColum("AvgRT(ms)", 25);
	    table.addColum("Hits", 25);
	    Map map_0_;
	    MONITORENTER (map_0_ = map);
	    MISSING MONITORENTER
	    synchronized (map_0_) {
		Iterator iterator = applications.iterator();
		while (iterator.hasNext()) {
		    String app = (String) iterator.next();
		    printAppInfo(app, table);
		}
	    }
	    string = table.toString();
	} catch (Throwable th) {
	    return new StringBuilder().append
		       ("Remote ERROR retrieving applications: ").append
		       (th.toString()).toString();
	}
	return string;
    }
    
    private void printAppInfo(String applicationName, Table tb)
	throws InvalidColumNumber {
	if (applicationName != "" && applicationName != null) {
	    String[] row
		= { applicationName,
		    UserThreadSessionTracker.getCpuPerApp(applicationName),
		    UserThreadSessionTracker.getAvgRtPerApp(applicationName),
		    UserThreadSessionTracker.getHitsPerApp(applicationName) };
	    tb.addRow(row);
	}
    }
    
    public String execute() {
	return listApps();
    }
}
