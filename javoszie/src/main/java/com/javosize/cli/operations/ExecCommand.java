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
 import com.javosize.remote.Controller;
 
 public class ExecCommand extends Command
 {
   public ExecCommand(String[] args)
   {
     setArgs(args);
     setType(CommandType.exec);
   }
   
   public String execute(StateHandler handler) throws InvalidParamsException
   {
     validateArgs(this.args, handler);
     if (handler.getStateHolder().equals(State.sh))
       return executeInSh(handler);
     if (handler.getStateHolder().equals(State.jmx))
       return executeInJmx(handler);
     if (handler.getStateHolder().equals(State.repository)) {
       return executeInRepository(handler);
     }
     return "Command not available at current entity.\nFor info about available commands type \"help\".\n";
   }
   
   private String executeInRepository(StateHandler handler)
   {
     StringBuffer sb = new StringBuffer("");
     String recipeName = this.args[1];
     for (int i = 2; i < this.args.length; i++) {
       if (i != this.args.length - 1) {
         sb.append(this.args[i] + ",");
       } else {
         sb.append(this.args[i]);
       }
     }
     String params = sb.toString().trim();
     try {
       Recipe r = com.javosize.recipes.Repository.getRecipe(recipeName);
       if (r == null) {
         return "Recipe " + recipeName + " not found.\n";
       }
       if ((r.getNumberOfParameters() > 0) && (r.getNumberOfParameters() != params.split(",").length)) {
         return "Invalid number of parameters, you can execute command \"cat " + recipeName + "\" for more info.\n";
       }
       return handler.executeRecipe(r, params);
     } catch (Throwable th) {
       return "Error executing recipe: \"" + recipeName + "\". " + th.toString() + "\n";
     }
   }
   
   private String executeInJmx(StateHandler handler) {
     MbeanOperationExecutionAction moea = new MbeanOperationExecutionAction();
     StringBuffer sb = new StringBuffer("");
     for (int i = 1; i < this.args.length; i++) {
       sb.append(this.args[i] + " ");
     }
     moea.setCommand(sb.toString().trim());
     return Controller.getInstance().execute(moea);
   }
   
   private String executeInSh(StateHandler handler) {
     String javaCode = getJavaCodeFromArgs(this.args);
     ShellAction sh = new ShellAction(javaCode, Environment.get("APPCLASSLOADER"));
     return Controller.getInstance().execute(sh);
   }
   
   private String getJavaCodeFromArgs(String[] args)
   {
     StringBuffer sb = new StringBuffer();
     for (int i = 1; i < args.length; i++) {
       String arg = args[i];
       sb.append(arg);
       sb.append(" ");
     }
     return sb.toString();
   }
   
   public boolean validArgs(String[] args, StateHandler handler)
   {
     return args.length >= 2;
   }
 }


