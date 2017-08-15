/* RmInterceptionAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.actions;
import com.javosize.agent.Agent;

public class RmInterceptionAction extends Action
{
    private static final long serialVersionUID = 2750107673159153383L;
    private String name;
    
    public RmInterceptionAction(String name) {
	this.name = name;
    }
    
    public String execute() {
	if (Agent.delInterception(name))
	    return "";
	return new StringBuilder().append("Interception ").append(name).append
		   (" no found").toString();
    }
}
