/* MBeanDetailAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.actions;
import java.lang.management.ManagementFactory;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.javosize.print.TextReport;

public class MBeanDetailAction extends Action
{
    private static final long serialVersionUID = -2368200046848456461L;
    private String mbeanId = "";
    
    private String dumpMbeanDetails() {
	String string;
	try {
	    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
	    ObjectName mbean = new ObjectName(mbeanId);
	    MBeanInfo mbi = server.getMBeanInfo(mbean);
	    MBeanAttributeInfo[] mbai = mbi.getAttributes();
	    String[] attributes = new String[mbai.length];
	    int i = 0;
	    MBeanAttributeInfo[] mbeanattributeinfos = mbai;
	    int i_0_ = mbeanattributeinfos.length;
	    for (int i_1_ = 0; i_1_ < i_0_; i_1_++) {
		MBeanAttributeInfo attributeInfo = mbeanattributeinfos[i_1_];
		String attribute = attributeInfo.getName();
		Object o;
		try {
		    o = server.getAttribute(mbean, attribute);
		} catch (Exception e) {
		    continue;
		}
		if (o != null)
		    attributes[i]
			= new StringBuilder().append(attribute).append
			      (": ").append
			      (o.toString()).toString();
		else
		    attributes[i]
			= new StringBuilder().append(attribute).append
			      (": ").toString();
		i++;
	    }
	    MBeanOperationInfo[] mbois = mbi.getOperations();
	    String[] operations = new String[mbois.length];
	    i = 0;
	    StringBuffer sb = new StringBuffer("");
	    MBeanOperationInfo[] mbeanoperationinfos = mbois;
	    int i_2_ = mbeanoperationinfos.length;
	    for (int i_3_ = 0; i_3_ < i_2_; i_3_++) {
		MBeanOperationInfo mboi = mbeanoperationinfos[i_3_];
		sb.append(new StringBuilder().append(mboi.getReturnType())
			      .append
			      (" ").append
			      (mboi.getName()).append
			      (" (").toString());
		MBeanParameterInfo[] params = mboi.getSignature();
		for (int j = 0; j < params.length; j++) {
		    MBeanParameterInfo param = params[j];
		    sb.append(new StringBuilder().append(param.getType())
				  .append
				  (" ").append
				  (param.getName()).toString());
		    if (j != params.length - 1)
			sb.append(", ");
		}
		sb.append(")");
		operations[i] = sb.toString();
		sb.delete(0, sb.length());
		i++;
	    }
	    TextReport report = new TextReport();
	    report.addSection("Attributes", attributes);
	    report.addSection("Operations", operations);
	    string = report.toString();
	} catch (MalformedObjectNameException mone) {
	    return "You must specify a valid mbean name\n";
	} catch (InstanceNotFoundException infe) {
	    return "You must specify a valid mbean name\n";
	} catch (Throwable th) {
	    th.printStackTrace();
	    return new StringBuilder().append
		       ("Remote ERROR getting mbean details for  ").append
		       (mbeanId).append
		       (" : ").append
		       (th.toString()).append
		       ("\n").toString();
	}
	return string;
    }
    
    public void setMBean(String mbeanId) {
	this.mbeanId = mbeanId;
    }
    
    public String execute() {
	return dumpMbeanDetails();
    }
}
