/* JavaAgentController - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.remote;
import com.javosize.actions.Action;

public class JavaAgentController extends Controller
{
    public int getPort() {
	return -1;
    }
    
    public String execute(Action a) {
	return a.execute();
    }
    
    public void finish() {
	/* empty */
    }
    
    public boolean isConnected() {
	return false;
    }
}
