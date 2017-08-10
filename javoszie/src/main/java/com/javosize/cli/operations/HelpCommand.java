 package com.javosize.cli.operations;
 
 import com.javosize.cli.Command;
 import com.javosize.cli.CommandType;
 import com.javosize.cli.State;
 import com.javosize.cli.StateHandler;
 import com.javosize.log.Log;
 
 public class HelpCommand extends Command
 {
   public static Log log = new Log(HelpCommand.class.getName());
   
   public HelpCommand(String[] args) {
     setArgs(args);
     setType(CommandType.help);
   }
   
   public String execute(StateHandler handler) throws com.javosize.cli.InvalidParamsException
   {
     MoreCommand paginatedHelp = new MoreCommand(handler.getStateHolder().getHelpText());
     return paginatedHelp.execute(handler);
   }
   
   protected boolean validArgs(String[] args, StateHandler handler)
   {
     return true;
   }
 }


