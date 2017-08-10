 package com.javosize.cli.operations;
 
 import com.javosize.cli.Command;
 import com.javosize.cli.StateHandler;
 
 public class InvalidCommand extends Command
 {
   public InvalidCommand(String[] args)
   {
     setArgs(args);
     setType(com.javosize.cli.CommandType.invalid);
   }
   
   public String execute(StateHandler handler)
   {
     return "javOSize: " + this.args[0] + ": Command not found!\n";
   }
   
   public boolean validArgs(String[] args, StateHandler handler)
   {
     return true;
   }
   
   public String getManText(StateHandler handler)
   {
     return "Invalid command: " + this.args[0];
   }
 }


