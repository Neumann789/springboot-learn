 package com.javosize.cli.operations;
 
 import com.javosize.actions.GetClassStaticElementsTotalSizeAction;
 import com.javosize.actions.GetClassStaticVariablesSizeAction;
 import com.javosize.actions.SessionsDuAction;
 import com.javosize.cli.Command;
 import com.javosize.cli.CommandType;
 import com.javosize.cli.InvalidParamsException;
 import com.javosize.cli.Main;
 import com.javosize.cli.State;
 import com.javosize.cli.StateHandler;
 import com.javosize.log.Log;
 import com.javosize.remote.Controller;
 
 public class DuCommand extends Command
 {
   public static Log log = new Log(DuCommand.class.getName());
   
   public DuCommand(String[] args) {
     setArgs(args);
     setType(CommandType.du);
   }
   
   public String execute(StateHandler handler) throws InvalidParamsException
   {
     validateArgs(this.args, handler);
     if (handler.getStateHolder().equals(State.classes))
       return executeInClasses(handler);
     if (handler.getStateHolder().equals(State.sessions)) {
       return executeInSessions(handler);
     }
     return "Command not available at current entity.\nFor info about available commands type \"help\".\n";
   }
   
 
   private String executeInSessions(StateHandler handler)
   {
     SessionsDuAction da = new SessionsDuAction(this.args[1]);
     return Controller.getInstance().execute(da);
   }
   
   private String executeInClasses(StateHandler handler) {
     String result = "Unable to obtain static variables size. More info at standard output of your JVM process.\n";
     
     if ((this.args[1] == null) || (!this.args[1].contains(".class"))) {
       return "Invalid class name: " + this.args[1] + "\n" + " - Usage: du package.className.class\n" + " - Example: du com.javosize.examples.example.class\n" + " - Hint: You can use autocomplete or the ls command to search the class name.\n";
     }
     
 
 
     try
     {
       GetClassStaticElementsTotalSizeAction getSizeAction = new GetClassStaticElementsTotalSizeAction(this.args[1]);
       String staticSize = Controller.getInstance().execute(getSizeAction);
       
       GetClassStaticVariablesSizeAction getSizeOfElementsAction = new GetClassStaticVariablesSizeAction(this.args[1], Main.getTerminalWidth());
       String detailedStaticSize = Controller.getInstance().execute(getSizeOfElementsAction);
       
 
       if ((staticSize != null) && (!staticSize.startsWith("ERROR"))) {
         String sizeMessage = "";
         if ((detailedStaticSize != null) && (!detailedStaticSize.equals("")) && (!detailedStaticSize.toLowerCase().startsWith("error"))) {
           sizeMessage = sizeMessage + detailedStaticSize + " \n";
         } else if ((detailedStaticSize != null) && (detailedStaticSize.toLowerCase().startsWith("error"))) {
           sizeMessage = sizeMessage + "Just recovered the total size of variables. Unable to obtain detailed size per static variable. Cause: " + detailedStaticSize + " \n\n";
         }
         
         sizeMessage = sizeMessage + "TOTAL size of static variables: " + staticSize + " bytes\n";
         
         result = sizeMessage;
       } else if ((staticSize != null) && (staticSize.startsWith("ERROR"))) {
         result = staticSize + "\n";
       }
     }
     catch (Throwable th) {
       log.error("Error obtaining class size: " + th, th);
     }
     
     return result;
   }
   
 
   protected boolean validArgs(String[] args, StateHandler handler)
   {
     return args.length >= 2;
   }
 }


