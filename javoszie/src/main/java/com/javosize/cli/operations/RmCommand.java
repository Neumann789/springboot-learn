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
   public RmCommand(String[] args)
   {
     setArgs(args);
     setType(CommandType.rm);
   }
   
   public String execute(StateHandler handler) throws InvalidParamsException
   {
     validateArgs(this.args, handler);
     if (handler.getStateHolder().equals(State.interceptor))
       return executeInInterception(handler);
     if (handler.getStateHolder().equals(State.repository))
       return executeInRepository(handler);
     if (handler.getStateHolder().equals(State.scheduler))
       return executeInScheduler(handler);
     if (handler.getStateHolder().equals(State.breakpoints)) {
       return executeInBreakpoint(handler);
     }
     return "Command not available at current entity.\nFor info about available commands type \"help\".\n";
   }
   
   private String executeInBreakpoint(StateHandler handler)
   {
     StringBuffer sb = new StringBuffer("");
     for (int i = 1; i < this.args.length; i++) {
       sb.append(this.args[i] + " ");
     }
     String breakpointName = sb.toString().trim();
     if (BreakPointManger.rmBreakPoint(breakpointName)) {
       return "";
     }
     return "BreakPoint " + breakpointName + " does not exist\n";
   }
   
   private String executeInScheduler(StateHandler handler)
   {
     StringBuffer sb = new StringBuffer("");
     for (int i = 1; i < this.args.length; i++) {
       sb.append(this.args[i] + " ");
     }
     String scheduleName = sb.toString().trim();
     try
     {
       Schedule r = Scheduler.getSchedule(scheduleName);
       Scheduler.removeSchedule(r);
     } catch (Throwable th) {
       return "Error deleting schedule: \"" + scheduleName + "\". " + th.toString();
     }
     return "";
   }
   
   private String executeInRepository(StateHandler handler) {
     StringBuffer sb = new StringBuffer("");
     for (int i = 1; i < this.args.length; i++) {
       sb.append(this.args[i] + " ");
     }
     String recipeName = sb.toString().trim();
     try
     {
       Recipe r = Repository.getRecipe(recipeName);
       if (r == null) {
         return "Recipe " + recipeName + " not found.\n";
       }
       Repository.removeRecipe(r);
     } catch (Throwable th) {
       return "Error deleting recipe: \"" + recipeName + "\". " + th.toString();
     }
     return "";
   }
   
   private String executeInInterception(StateHandler handler) {
     RmInterceptionAction lsi = new RmInterceptionAction(this.args[1]);
     return Controller.getInstance().execute(lsi);
   }
   
   public boolean validArgs(String[] args, StateHandler handler) {
     return args.length >= 2;
   }
 }


