/* LoadCommand - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.cli.operations;
import java.io.IOException;

import com.javosize.cli.Command;
import com.javosize.cli.CommandType;
import com.javosize.cli.InvalidParamsException;
import com.javosize.cli.State;
import com.javosize.cli.StateHandler;
import com.javosize.recipes.Repository;

import org.json.JSONException;

public class LoadCommand extends Command
{
    public LoadCommand(String[] args) {
	setArgs(args);
	setType(CommandType.create);
    }
    
    public String execute(StateHandler handler) throws InvalidParamsException {
	validateArgs(args, handler);
	if (handler.getStateHolder().equals(State.repository))
	    return executeInRepository(handler);
	return "Command not available at current entity.\nFor info about available commands type \"help\".\n";
    }
    
    private String executeInRepository(StateHandler handler) {
	String url = args[1];
	String result = null;
	try {
	    result = Repository.importFromCloud(url);
	} catch (IOException e) {
	    return new StringBuilder().append("Error importing recipe: ")
		       .append
		       (e.getMessage()).append
		       ("\n").toString();
	} catch (JSONException je) {
	    return new StringBuilder().append("Invalid recipe: ").append
		       (je.getMessage()).append
		       ("\n").toString();
	}
	return result;
    }
    
    public boolean validArgs(String[] args, StateHandler handler) {
	return args.length == 2;
    }
}
