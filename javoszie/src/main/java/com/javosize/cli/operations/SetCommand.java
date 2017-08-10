 package com.javosize.cli.operations;
 
 import com.javosize.cli.Command;
 import com.javosize.cli.CommandType;
 import com.javosize.cli.Environment;
 import com.javosize.cli.InvalidParamsException;
 import com.javosize.cli.Main;
 import com.javosize.cli.StateHandler;
 import com.javosize.log.Log;
 
 public class SetCommand extends Command
 {
   private static Log log = new Log(SetCommand.class.getSimpleName());
   
   public SetCommand(String[] args) {
     setArgs(args);
     setType(CommandType.set);
   }
   
   public String execute(StateHandler handler) throws InvalidParamsException
   {
     validateArgs(this.args, handler);
     
 
     String finalValue = this.args[2];
     for (int i = 3; i < this.args.length; i++) {
       finalValue = finalValue + " " + this.args[i];
     }
     
 
     if (this.args[1].equals("LOG_LEVEL")) {
       try {
         Log.setLogLevel(finalValue);
         Main.updateAgentConfiguration();
       } catch (Throwable th) {
         log.trace("Error updating log level: " + th, th);
         return th.getMessage() + "\n";
       }
     }
     
 
     Environment.set(this.args[1], finalValue);
     return "Set key(" + this.args[1] + ") value(" + finalValue + ")\n";
   }
   
   public boolean validArgs(String[] args, StateHandler handler)
   {
     return args.length >= 3;
   }
 }


