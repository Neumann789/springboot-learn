/* MvCommand - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.cli.operations;
import com.javosize.actions.HotSwapAction;
import com.javosize.cli.Command;
import com.javosize.cli.CommandType;
import com.javosize.cli.InvalidParamsException;
import com.javosize.cli.State;
import com.javosize.cli.StateHandler;
import com.javosize.remote.Controller;

public class MvCommand extends Command
{
    public MvCommand(String[] args) {
	setArgs(args);
	setType(CommandType.mv);
    }
    
    public String execute(StateHandler handler) throws InvalidParamsException {
	validateArgs(args, handler);
	if (handler.getStateHolder().equals(State.classes))
	    return executeInClasses(handler);
	return "Command not available at current entity.\nFor info about available commands type \"help\".\n";
    }
    
    private String executeInClasses(StateHandler handler) {
	HotSwapAction hotSwap = new HotSwapAction(args[1], args[2]);
	return Controller.getInstance().execute(hotSwap);
    }
    
    protected boolean validArgs(String[] args, StateHandler handler) {
	return args.length == 3;
    }
}
