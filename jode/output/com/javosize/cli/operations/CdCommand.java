/* CdCommand - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.cli.operations;
import com.javosize.cli.Command;
import com.javosize.cli.CommandType;
import com.javosize.cli.InvalidParamsException;
import com.javosize.cli.State;
import com.javosize.cli.StateHandler;

public class CdCommand extends Command
{
    public CdCommand(String[] args) {
	setArgs(args);
	setType(CommandType.cd);
    }
    
    public String execute(StateHandler handler) throws InvalidParamsException {
	validateArgs(args, handler);
	String destination = args[1].toLowerCase();
	if ("/".equals(destination)
	    || (("..".equals(destination) || "../".equals(destination))
		&& !handler.getStateHolder().equals(State.root))) {
	    handler.setStateHolder(State.root);
	    return "";
	}
	if (("..".equals(destination) || "../".equals(destination))
	    && handler.getStateHolder().equals(State.root))
	    return new StringBuilder().append("cd: ").append(destination)
		       .append
		       (": No such entity in ROOT\n").toString();
	if (!"..".equals(destination) && !destination.startsWith("../")
	    && handler.getStateHolder().equals(State.root)) {
	    String string;
	    try {
		State newState = State.valueOf(destination);
		handler.setStateHolder(newState);
		string = "";
	    } catch (IllegalArgumentException iae) {
		return new StringBuilder().append("cd: ").append
			   (destination).append
			   (": No such entity\n").toString();
	    }
	    return string;
	}
	if (!"..".equals(destination) && destination.startsWith("../")
	    && destination.length() >= 4
	    && !handler.getStateHolder().equals(State.root)) {
	    String string;
	    try {
		State newState = State.valueOf(destination.substring(3));
		handler.setStateHolder(newState);
		string = "";
	    } catch (IllegalArgumentException iae) {
		return new StringBuilder().append("cd: ").append
			   (destination.substring(3)).append
			   (": No such entity in ROOT\n").toString();
	    }
	    return string;
	}
	return new StringBuilder().append("cd: ").append(args[1]).append
		   (": No such entity in ").append
		   (handler.getStateHolder().toString()).append
		   ("\n").toString();
    }
    
    protected boolean validArgs(String[] args, StateHandler handler) {
	if (args.length != 2)
	    return false;
	return true;
    }
}
