/* ListUsersAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.actions;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.javosize.agent.session.UserThreadSessionTracker;
import com.javosize.print.Table;

public class ListUsersAction extends Action
{
    private static final long serialVersionUID = -2368200046848456461L;
    
    public ListUsersAction(int terminalWidth, int terminalHeight) {
	this.terminalWidth = terminalWidth;
	this.terminalHeight = terminalHeight;
    }
    
    private String listUsers() {
	String string;
	try {
	    Map m = UserThreadSessionTracker.getAllSessionsMap();
	    java.util.Collection sessionIds = m.keySet();
	    Table table = new Table(terminalWidth);
	    table.addColum("User", 25);
	    table.addColum("% CPU", 25);
	    table.addColum("AvgRT(ms)", 25);
	    table.addColum("Hits", 25);
	    HashMap alreadyAddedUsers = new HashMap();
	    Map map;
	    MONITORENTER (map = m);
	    MISSING MONITORENTER
	    synchronized (map) {
		Iterator iterator = sessionIds.iterator();
		while (iterator.hasNext()) {
		    String sessionId = (String) iterator.next();
		    String userId = UserThreadSessionTracker
					.getUserForSession(sessionId);
		    if (userId != ""
			&& !alreadyAddedUsers.containsKey(userId)) {
			String[] row
			    = { userId,
				UserThreadSessionTracker.getCpuPerUser(userId),
				UserThreadSessionTracker
				    .getAvgRtPerUser(userId),
				UserThreadSessionTracker
				    .getHitPerUser(userId) };
			table.addRow(row);
			alreadyAddedUsers.put(userId, userId);
		    }
		}
	    }
	    string = table.toString();
	} catch (Throwable th) {
	    return new StringBuilder().append
		       ("Remote ERROR retrieving users: ").append
		       (th.toString()).toString();
	}
	return string;
    }
    
    public String execute() {
	return listUsers();
    }
}
