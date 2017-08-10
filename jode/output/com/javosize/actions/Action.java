/* Action - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.actions;
import java.io.Serializable;

public abstract class Action implements Serializable
{
    private static final long serialVersionUID = 2329429515526101305L;
    public String result = null;
    int terminalWidth = 0;
    int terminalHeight = 0;
    
    public String getResult() {
	return result;
    }
    
    public void setResult(String result) {
	this.result = result;
    }
    
    public abstract String execute();
}
