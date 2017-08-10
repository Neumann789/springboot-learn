 package org.jboss.jreadline.console;
 
 import org.jboss.jreadline.console.operator.ControlOperator;
 
 
 
 
 
 
 
 
 
 
 
 
 public class ConsoleOutput
 {
   private String stdOut;
   private String stdErr;
   private ConsoleOperation consoleOperation;
   
   public ConsoleOutput(ConsoleOperation consoleOperation)
   {
     this.consoleOperation = consoleOperation;
   }
   
   public ConsoleOutput(ConsoleOperation consoleOperation, String stdOut, String stdErr) {
     this(consoleOperation);
     this.stdOut = stdOut;
     this.stdErr = stdErr;
   }
   
   public String getBuffer() {
     return this.consoleOperation.getBuffer();
   }
   
   public ControlOperator getControlOperator() {
     return this.consoleOperation.getControlOperator();
   }
   
   public void setConsoleOperation(ConsoleOperation co) {
     this.consoleOperation = co;
   }
   
   public String getStdOut() {
     return this.stdOut;
   }
   
   public String getStdErr() {
     return this.stdErr;
   }
   
 
 
 
   public String toString()
   {
     return "Buffer: " + getBuffer() + "\nControlOperator: " + getControlOperator() + "\nStdOut: " + getStdOut() + "\nStdErr: " + getStdErr();
   }
 }


