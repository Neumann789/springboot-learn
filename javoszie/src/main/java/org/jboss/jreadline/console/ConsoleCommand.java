 package org.jboss.jreadline.console;
 
 import java.io.IOException;
 import org.jboss.jreadline.console.operator.ControlOperator;
 import org.jboss.jreadline.edit.actions.Operation;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class ConsoleCommand
 {
   boolean attached = false;
   protected Console console = null;
   ConsoleOutput consoleOutput;
   
   public ConsoleCommand(Console console) {
     this.console = console;
   }
   
 
 
 
 
   public final void attach(ConsoleOutput output)
     throws IOException
   {
     this.attached = true;
     this.console.attachProcess(this);
     this.consoleOutput = output;
     afterAttach();
   }
   
 
 
 
   public final boolean isAttached()
   {
     return this.attached;
   }
   
 
 
 
 
   public final void detach()
     throws IOException
   {
     this.attached = false;
     afterDetach();
   }
   
   public final boolean hasRedirectOut() {
     return ControlOperator.isRedirectionOut(this.consoleOutput.getControlOperator());
   }
   
   public final ConsoleOutput getConsoleOutput() {
     return this.consoleOutput;
   }
   
   protected abstract void afterAttach()
     throws IOException;
   
   protected abstract void afterDetach()
     throws IOException;
   
   public abstract void processOperation(Operation paramOperation)
     throws IOException;
 }


