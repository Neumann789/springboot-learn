/* CliRemoteController - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.remote;
import com.javosize.actions.Action;
import com.javosize.actions.TerminateAction;
import com.javosize.remote.server.Server;

public class CliRemoteController extends Controller
{
    private Server server = null;
    
    public CliRemoteController() {
	server = Server.getInstance();
	server.start();
    }
    
    public int getPort() {
	return server.getPort();
    }
    
    public String execute(Action a) {
	try {
	    Action action;
	    MONITORENTER (action = a);
	    MISSING MONITORENTER
	    synchronized (action) {
		server.enqueueAction(a);
		a.wait();
	    }
	} catch (InterruptedException interruptedexception) {
	    /* empty */
	}
	return a.getResult();
    }
    
    public void finish() {
	server.enqueueAction(new TerminateAction());
	try {
	    Thread.sleep(500L);
	} catch (InterruptedException interruptedexception) {
	    /* empty */
	}
	server.finish();
    }
    
    public boolean isConnected() {
	return server.isConnected();
    }
}
