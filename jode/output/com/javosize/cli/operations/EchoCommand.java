/* EchoCommand - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.cli.operations;
import com.javosize.cli.Command;
import com.javosize.cli.CommandType;
import com.javosize.cli.Environment;
import com.javosize.cli.InvalidParamsException;
import com.javosize.cli.StateHandler;

public class EchoCommand extends Command
{
    public EchoCommand(String[] args) {
	setArgs(args);
	setType(CommandType.echo);
    }
    
    public String execute(StateHandler handler) throws InvalidParamsException {
	validateArgs(args, handler);
	String key = args[1].substring(1);
	String value = Environment.get(key);
	if (value == null)
	    return new StringBuilder().append("Environment variable ").append
		       (key).append
		       (" is not defined.\n").toString();
	return new StringBuilder().append(value).append("\n").toString();
    }
    
    public boolean validArgs(String[] args, StateHandler handler) {
	if (args.length == 2 && args[1].startsWith("$"))
	    return true;
	return false;
    }
}
