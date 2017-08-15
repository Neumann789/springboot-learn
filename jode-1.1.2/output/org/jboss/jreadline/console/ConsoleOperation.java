/* ConsoleOperation - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.console;
import org.jboss.jreadline.console.operator.ControlOperator;

public class ConsoleOperation
{
    private ControlOperator controlOperator;
    private String buffer;
    
    public ConsoleOperation(ControlOperator controlOperator, String buffer) {
	this.controlOperator = controlOperator;
	this.buffer = buffer;
    }
    
    public String getBuffer() {
	return buffer;
    }
    
    public ControlOperator getControlOperator() {
	return controlOperator;
    }
    
    public boolean equals(Object o) {
	if (o instanceof ConsoleOperation) {
	    ConsoleOperation r = (ConsoleOperation) o;
	    if (r.getBuffer().equals(getBuffer())
		&& r.getControlOperator().equals(getControlOperator()))
		return true;
	}
	return false;
    }
    
    public int hashCode() {
	return 129384;
    }
    
    public String toString() {
	return new StringBuilder().append("ControlOperator: ").append
		   (getControlOperator()).append
		   (", Buffer: ").append
		   (buffer).toString();
    }
}
