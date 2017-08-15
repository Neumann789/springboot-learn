/* ManCommand - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.cli.operations;
import com.javosize.cli.Command;
import com.javosize.cli.CommandType;
import com.javosize.cli.InvalidParamsException;
import com.javosize.cli.StateHandler;

public class ManCommand extends Command
{
    private CommandType type;
    
    public ManCommand(String[] args) {
	setArgs(args);
	setType(CommandType.man);
    }
    
    public String execute(StateHandler handler) throws InvalidParamsException {
	try {
	    validateArgs(args, handler);
	} catch (IllegalArgumentException iae) {
	    return new StringBuilder().append("Unknown command: ").append
		       (args[1]).append
		       ("\n").toString();
	}
	MoreCommand paginatedManPage = new MoreCommand(type.getManPage());
	return paginatedManPage.execute(handler);
    }
    
    public boolean validArgs(String[] args, StateHandler handler) {
	if (args.length != 2)
	    return false;
	type = CommandType.valueOf(args[1]);
	if (type == null)
	    return false;
	return true;
    }
    
    public String getManText(StateHandler handler) {
	return "usage: man <COMMAND YOU WANT HELP FOR>";
    }
}
