 package com.javosize.cli.operations;
 
 import com.javosize.cli.Command;
 import com.javosize.cli.CommandType;
 import com.javosize.cli.StateHandler;
 
 public class ManCommand extends Command
 {
   private CommandType type;
   
   public ManCommand(String[] args)
   {
     setArgs(args);
     setType(CommandType.man);
   }
   
   public String execute(StateHandler handler) throws com.javosize.cli.InvalidParamsException
   {
     try {
       validateArgs(this.args, handler);
     } catch (IllegalArgumentException iae) {
       return "Unknown command: " + this.args[1] + "\n";
     }
     MoreCommand paginatedManPage = new MoreCommand(this.type.getManPage());
     return paginatedManPage.execute(handler);
   }
   
   public boolean validArgs(String[] args, StateHandler handler)
   {
     if (args.length != 2) {
       return false;
     }
     
     this.type = CommandType.valueOf(args[1]);
     if (this.type == null) {
       return false;
     }
     
     return true;
   }
   
   public String getManText(StateHandler handler)
   {
     return "usage: man <COMMAND YOU WANT HELP FOR>";
   }
 }


