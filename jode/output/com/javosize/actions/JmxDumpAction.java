/* JmxDumpAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.actions;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class JmxDumpAction extends Action
{
    private static final long serialVersionUID = -2368200046848456461L;
    
    private String dumpMbeans() {
	String string;
	try {
	    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
	    Set mbeans = server.queryNames(null, null);
	    StringBuffer sb = new StringBuffer();
	    Iterator iterator = mbeans.iterator();
	    while (iterator.hasNext()) {
		ObjectName mbean = (ObjectName) iterator.next();
		sb.append(mbean);
		sb.append("\n");
	    }
	    string = sb.toString();
	} catch (Throwable th) {
	    return new StringBuilder().append
		       ("Remote ERROR listing the MBeans: ").append
		       (th.toString()).toString();
	}
	return string;
    }
    
    public String execute() {
	return dumpMbeans();
    }
}
