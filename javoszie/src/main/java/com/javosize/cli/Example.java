 package com.javosize.cli;
 
 import com.javosize.remote.Controller;
 import com.javosize.remote.client.Client;
 import java.io.IOException;
 import org.jboss.jreadline.complete.Completion;
 import org.jboss.jreadline.console.Console;
 import org.jboss.jreadline.console.ConsoleCommand;
 import org.jboss.jreadline.console.ConsoleOutput;
 import org.jboss.jreadline.console.operator.ControlOperator;
 import org.jboss.jreadline.console.settings.Settings;
 import org.jboss.jreadline.edit.actions.Operation;
 import org.jboss.jreadline.util.ANSI;
 
 
 
 
 public class Example
 {
   public static void main(String[] args)
     throws IOException
   {
     Controller controller = Controller.getInstance();
     
 
     Thread client = new Thread(new Client("127.0.0.1", controller.getPort()));
     client.start();
     
 
     StateHandler stateHandler = new StateHandler();
     
     Settings.getInstance().setReadInputrc(false);
     Console exampleConsole = new Console();
     
     ConsoleCommand test = new ConsoleCommand(exampleConsole)
     {
       protected void afterAttach() throws IOException
       {
         if (!hasRedirectOut()) {
           this.console.pushToStdOut(ANSI.getAlternateBufferScreen());
         }
         
         readFromFile();
         
 
         if (hasRedirectOut()) {
           detach();
         }
       }
       
       protected void afterDetach() throws IOException
       {
         if (!hasRedirectOut())
           this.console.pushToStdOut(ANSI.getMainBufferScreen());
       }
       
       private void readFromFile() throws IOException {
         if ((getConsoleOutput().getStdOut() != null) && 
           (getConsoleOutput().getStdOut().length() > 0)) {
           this.console.pushToStdOut("FROM STDOUT: " + getConsoleOutput().getStdOut());
         }
         else {
           this.console.pushToStdOut("here should we present some text... press 'q' to quit");
         }
       }
       
       public void processOperation(Operation operation) throws IOException {
         if (operation.getInput()[0] == 113) {
           detach();
         }
         else if (operation.getInput()[0] == 97) {
           readFromFile();
 
         }
         
       }
       
 
     };
     Completion completer = new ConsoleCompletion(stateHandler);
     exampleConsole.addCompletion(completer);
     
     ConsoleOutput line;
     while ((line = exampleConsole.read("[javOSize@JVM /" + stateHandler.getStateHolder() + "]~> ", "[javOSize@JVM /" + stateHandler.getStateHolder() + "]~> ")) != null)
     {
       if ((line.getBuffer().equalsIgnoreCase("quit")) || (line.getBuffer().equalsIgnoreCase("exit"))) {
         break;
       }
       
 
       if (line.getControlOperator() == ControlOperator.PIPE) {
         stateHandler.setPreviousCommandResult(stateHandler.executeOperation(line.getBuffer(), false));
       }
       else
       {
         exampleConsole.pushToStdOut(stateHandler.executeOperation(line.getBuffer(), false));
       }
     }
     try
     {
       exampleConsole.stop();
       Controller.getInstance().finish();
     }
     catch (Exception localException) {}
   }
 }


