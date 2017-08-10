 package com.javosize.cli.operations;
 
 import com.javosize.cli.Command;
 import com.javosize.cli.CommandType;
 import com.javosize.cli.State;
 import com.javosize.cli.StateHandler;
 
 public class CdCommand extends Command
 {
   public CdCommand(String[] args)
   {
     setArgs(args);
     setType(CommandType.cd);
   }
   
   public String execute(StateHandler handler) throws com.javosize.cli.InvalidParamsException
   {
     validateArgs(this.args, handler);
     
     String destination = this.args[1].toLowerCase();
     
 
 
 
 
 
 
     if (("/".equals(destination)) || (
       (("..".equals(destination)) || ("../".equals(destination))) && (!handler.getStateHolder().equals(State.root))))
     {
 
       handler.setStateHolder(State.root);
       return "";
     }
     
 
 
     if ((("..".equals(destination)) || ("../".equals(destination))) && (handler.getStateHolder().equals(State.root)))
     {
       return "cd: " + destination + ": No such entity in ROOT\n";
     }
     
 
 
     if ((!"..".equals(destination)) && (!destination.startsWith("../")) && (handler.getStateHolder().equals(State.root))) {
       try {
         State newState = State.valueOf(destination);
         handler.setStateHolder(newState);
         return "";
       } catch (IllegalArgumentException iae) {
         return "cd: " + destination + ": No such entity\n";
       }
     }
     
 
     if ((!"..".equals(destination)) && (destination.startsWith("../")) && (destination.length() >= 4) && (!handler.getStateHolder().equals(State.root))) {
       try {
         State newState = State.valueOf(destination.substring(3));
         handler.setStateHolder(newState);
         return "";
       } catch (IllegalArgumentException iae) {
         return "cd: " + destination.substring(3) + ": No such entity in ROOT\n";
       }
     }
     return "cd: " + this.args[1] + ": No such entity in " + handler.getStateHolder().toString() + "\n";
   }
   
 
   protected boolean validArgs(String[] args, StateHandler handler)
   {
     if (args.length != 2) {
       return false;
     }
     return true;
   }
 }


