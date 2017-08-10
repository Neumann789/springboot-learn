/* InvalidCommand - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.cli.operations;
import com.javosize.cli.Command;
import com.javosize.cli.CommandType;
import com.javosize.cli.StateHandler;

public class InvalidCommand extends Command
{
    public InvalidCommand(String[] args) {
	setArgs(args);
	setType(CommandType.invalid);
    }
    
    public String execute(StateHandler handler) {
	return new StringBuilder().append("javOSize: ").append(args[0]).append
		   (": Command not found!\n").toString();
    }
    
    public boolean validArgs(String[] args, StateHandler handler) {
	return true;
    }
    
    public String getManText(StateHandler handler) {
	return new StringBuilder().append("Invalid command: ").append
		   (args[0]).toString();
    }
}
