 package com.javosize.cli.operations;
 
 import com.javosize.breakpoints.BreakPoint;
 import com.javosize.breakpoints.BreakPointManger;
 import com.javosize.cli.Command;
 import com.javosize.cli.CommandType;
 import com.javosize.cli.InvalidParamsException;
 import com.javosize.cli.State;
 import com.javosize.cli.StateHandler;
 import com.javosize.log.Log;
 
 public class NextCommand extends Command
 {
   public static Log log = new Log(NextCommand.class.getName());
   
   public NextCommand(String[] args) {
     setArgs(args);
     setType(CommandType.cat);
   }
   
   public String execute(StateHandler handler) throws InvalidParamsException
   {
     if (handler.getStateHolder().equals(State.breakpoints)) {
       return executeInBreakPoints(handler);
     }
     return "Command not available at current entity.\nFor info about available commands type \"help\".\n";
   }
   
   private String executeInBreakPoints(StateHandler handler) throws InvalidParamsException
   {
     validateArgs(this.args, handler);
     String breakpointName = this.args[1];
     BreakPoint bp = BreakPointManger.getBreakPoint(breakpointName);
     if (bp != null) {
       bp.next();
       return "OK\n";
     }
     return breakpointName + " does not exists\n";
   }
   
 
   protected boolean validArgs(String[] args, StateHandler handler)
   {
     return args.length >= 2;
   }
 }


