/* ConsoleCommand - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.console;
import java.io.IOException;

import org.jboss.jreadline.console.operator.ControlOperator;
import org.jboss.jreadline.edit.actions.Operation;

public abstract class ConsoleCommand
{
    boolean attached = false;
    protected Console console = null;
    ConsoleOutput consoleOutput;
    
    public ConsoleCommand(Console console) {
	this.console = console;
    }
    
    public final void attach(ConsoleOutput output) throws IOException {
	attached = true;
	console.attachProcess(this);
	consoleOutput = output;
	afterAttach();
    }
    
    public final boolean isAttached() {
	return attached;
    }
    
    public final void detach() throws IOException {
	attached = false;
	afterDetach();
    }
    
    public final boolean hasRedirectOut() {
	return ControlOperator
		   .isRedirectionOut(consoleOutput.getControlOperator());
    }
    
    public final ConsoleOutput getConsoleOutput() {
	return consoleOutput;
    }
    
    protected abstract void afterAttach() throws IOException;
    
    protected abstract void afterDetach() throws IOException;
    
    public abstract void processOperation(Operation operation)
	throws IOException;
}
