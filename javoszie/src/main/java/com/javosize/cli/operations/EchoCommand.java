 package com.javosize.cli.operations;
 
 import com.javosize.cli.Command;
 import com.javosize.cli.CommandType;
 import com.javosize.cli.Environment;
 import com.javosize.cli.StateHandler;
 
 public class EchoCommand extends Command
 {
   public EchoCommand(String[] args)
   {
     setArgs(args);
     setType(CommandType.echo);
   }
   
   public String execute(StateHandler handler) throws com.javosize.cli.InvalidParamsException
   {
     validateArgs(this.args, handler);
     String key = this.args[1].substring(1);
     String value = Environment.get(key);
     if (value == null) {
       return "Environment variable " + key + " is not defined.\n";
     }
     return value + "\n";
   }
   
 
   public boolean validArgs(String[] args, StateHandler handler)
   {
     if ((args.length == 2) && (args[1].startsWith("$"))) {
       return true;
     }
     return false;
   }
 }


