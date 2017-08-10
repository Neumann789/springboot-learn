/* RmCommand - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.cli.operations;
import com.javosize.actions.RmInterceptionAction;
import com.javosize.breakpoints.BreakPointManger;
import com.javosize.cli.Command;
import com.javosize.cli.CommandType;
import com.javosize.cli.InvalidParamsException;
import com.javosize.cli.State;
import com.javosize.cli.StateHandler;
import com.javosize.recipes.Recipe;
import com.javosize.recipes.Repository;
import com.javosize.remote.Controller;
import com.javosize.scheduler.Schedule;
import com.javosize.scheduler.Scheduler;

public class RmCommand extends Command
{
    public RmCommand(String[] args) {
	setArgs(args);
	setType(CommandType.rm);
    }
    
    public String execute(StateHandler handler) throws InvalidParamsException {
	validateArgs(args, handler);
	if (handler.getStateHolder().equals(State.interceptor))
	    return executeInInterception(handler);
	if (handler.getStateHolder().equals(State.repository))
	    return executeInRepository(handler);
	if (handler.getStateHolder().equals(State.scheduler))
	    return executeInScheduler(handler);
	if (handler.getStateHolder().equals(State.breakpoints))
	    return executeInBreakpoint(handler);
	return "Command not available at current entity.\nFor info about available commands type \"help\".\n";
    }
    
    private String executeInBreakpoint(StateHandler handler) {
	StringBuffer sb = new StringBuffer("");
	for (int i = 1; i < args.length; i++)
	    sb.append(new StringBuilder().append(args[i]).append(" ")
			  .toString());
	String breakpointName = sb.toString().trim();
	if (BreakPointManger.rmBreakPoint(breakpointName))
	    return "";
	return new StringBuilder().append("BreakPoint ").append
		   (breakpointName).append
		   (" does not exist\n").toString();
    }
    
    private String executeInScheduler(StateHandler handler) {
	StringBuffer sb = new StringBuffer("");
	for (int i = 1; i < args.length; i++)
	    sb.append(new StringBuilder().append(args[i]).append(" ")
			  .toString());
	String scheduleName = sb.toString().trim();
	try {
	    Schedule r = Scheduler.getSchedule(scheduleName);
	    Scheduler.removeSchedule(r);
	} catch (Throwable th) {
	    return new StringBuilder().append
		       ("Error deleting schedule: \"").append
		       (scheduleName).append
		       ("\". ").append
		       (th.toString()).toString();
	}
	return "";
    }
    
    private String executeInRepository(StateHandler handler) {
	throwable = throwable_1_;
	break;
    }
    
    private String executeInInterception(StateHandler handler) {
	RmInterceptionAction lsi = new RmInterceptionAction(args[1]);
	return Controller.getInstance().execute(lsi);
    }
    
    public boolean validArgs(String[] args, StateHandler handler) {
	return args.length >= 2;
    }
}
