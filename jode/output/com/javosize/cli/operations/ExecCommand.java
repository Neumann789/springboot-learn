/* ExecCommand - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.cli.operations;
import com.javosize.actions.MbeanOperationExecutionAction;
import com.javosize.actions.ShellAction;
import com.javosize.cli.Command;
import com.javosize.cli.CommandType;
import com.javosize.cli.Environment;
import com.javosize.cli.InvalidParamsException;
import com.javosize.cli.State;
import com.javosize.cli.StateHandler;
import com.javosize.recipes.Recipe;
import com.javosize.recipes.Repository;
import com.javosize.remote.Controller;

public class ExecCommand extends Command
{
    public ExecCommand(String[] args) {
	setArgs(args);
	setType(CommandType.exec);
    }
    
    public String execute(StateHandler handler) throws InvalidParamsException {
	validateArgs(args, handler);
	if (handler.getStateHolder().equals(State.sh))
	    return executeInSh(handler);
	if (handler.getStateHolder().equals(State.jmx))
	    return executeInJmx(handler);
	if (handler.getStateHolder().equals(State.repository))
	    return executeInRepository(handler);
	return "Command not available at current entity.\nFor info about available commands type \"help\".\n";
    }
    
    private String executeInRepository(StateHandler handler) {
	throwable = throwable_2_;
	break;
    }
    
    private String executeInJmx(StateHandler handler) {
	MbeanOperationExecutionAction moea
	    = new MbeanOperationExecutionAction();
	StringBuffer sb = new StringBuffer("");
	for (int i = 1; i < args.length; i++)
	    sb.append(new StringBuilder().append(args[i]).append(" ")
			  .toString());
	moea.setCommand(sb.toString().trim());
	return Controller.getInstance().execute(moea);
    }
    
    private String executeInSh(StateHandler handler) {
	String javaCode = getJavaCodeFromArgs(args);
	ShellAction sh
	    = new ShellAction(javaCode, Environment.get("APPCLASSLOADER"));
	return Controller.getInstance().execute(sh);
    }
    
    private String getJavaCodeFromArgs(String[] args) {
	StringBuffer sb = new StringBuffer();
	for (int i = 1; i < args.length; i++) {
	    String arg = args[i];
	    sb.append(arg);
	    sb.append(" ");
	}
	return sb.toString();
    }
    
    public boolean validArgs(String[] args, StateHandler handler) {
	return args.length >= 2;
    }
}
