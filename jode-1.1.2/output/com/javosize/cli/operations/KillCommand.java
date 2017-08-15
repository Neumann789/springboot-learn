/* KillCommand - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.cli.operations;
import com.javosize.actions.KillThreadAction;
import com.javosize.cli.Command;
import com.javosize.cli.CommandType;
import com.javosize.cli.InvalidParamsException;
import com.javosize.cli.Main;
import com.javosize.cli.State;
import com.javosize.cli.StateHandler;
import com.javosize.remote.Controller;

public class KillCommand extends Command
{
    public KillCommand(String[] args) {
	setArgs(args);
	setType(CommandType.kill);
    }
    
    public String execute(StateHandler handler) throws InvalidParamsException {
	validateArgs(args, handler);
	if (handler.getStateHolder().equals(State.threads))
	    return executeInThreads(handler);
	return "Command not available at current entity.\nFor info about available commands type \"help\".\n";
    }
    
    private String executeInThreads(StateHandler handler) {
	KillThreadAction kta
	    = new KillThreadAction(args[1], Main.getTerminalWidth());
	return Controller.getInstance().execute(kta);
    }
    
    public boolean validArgs(String[] args, StateHandler handler) {
	return args.length == 2;
    }
}
