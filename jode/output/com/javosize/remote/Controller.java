/* Controller - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.remote;
import com.javosize.actions.Action;
import com.javosize.agent.Agent;

public abstract class Controller
{
    private static volatile Controller instance;
    
    public static synchronized Controller getInstance() {
	if (instance == null) {
	    if (Agent.isAgent())
		instance = new JavaAgentController();
	    else
		instance = new CliRemoteController();
	}
	return instance;
    }
    
    public abstract int getPort();
    
    public abstract String execute(Action action);
    
    public abstract void finish();
    
    public abstract boolean isConnected();
}
