/* ConsoleOutput - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.console;
import org.jboss.jreadline.console.operator.ControlOperator;

public class ConsoleOutput
{
    private String stdOut;
    private String stdErr;
    private ConsoleOperation consoleOperation;
    
    public ConsoleOutput(ConsoleOperation consoleOperation) {
	this.consoleOperation = consoleOperation;
    }
    
    public ConsoleOutput(ConsoleOperation consoleOperation, String stdOut,
			 String stdErr) {
	this(consoleOperation);
	this.stdOut = stdOut;
	this.stdErr = stdErr;
    }
    
    public String getBuffer() {
	return consoleOperation.getBuffer();
    }
    
    public ControlOperator getControlOperator() {
	return consoleOperation.getControlOperator();
    }
    
    public void setConsoleOperation(ConsoleOperation co) {
	consoleOperation = co;
    }
    
    public String getStdOut() {
	return stdOut;
    }
    
    public String getStdErr() {
	return stdErr;
    }
    
    public String toString() {
	return new StringBuilder().append("Buffer: ").append(getBuffer())
		   .append
		   ("\nControlOperator: ").append
		   (getControlOperator()).append
		   ("\nStdOut: ").append
		   (getStdOut()).append
		   ("\nStdErr: ").append
		   (getStdErr()).toString();
    }
}
