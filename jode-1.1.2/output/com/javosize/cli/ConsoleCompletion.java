/* ConsoleCompletion - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.cli;
import org.jboss.jreadline.complete.CompleteOperation;
import org.jboss.jreadline.complete.Completion;

public class ConsoleCompletion extends Completion
{
    private StateHandler stateHandler;
    
    public ConsoleCompletion(StateHandler stateHandler) {
	this.stateHandler = stateHandler;
    }
    
    public void complete(CompleteOperation completeOperation,
			 boolean afterPipe) {
	stateHandler.getCommandList(this, completeOperation,
				    completeOperation.getBuffer(), afterPipe);
    }
}
