 package com.javosize.cli;
 
 import org.jboss.jreadline.complete.CompleteOperation;
 import org.jboss.jreadline.complete.Completion;
 
 
 
 
 
 public class ConsoleCompletion
   extends Completion
 {
   private StateHandler stateHandler;
   
   public ConsoleCompletion(StateHandler stateHandler)
   {
     this.stateHandler = stateHandler;
   }
   
   public void complete(CompleteOperation completeOperation, boolean afterPipe) {
     this.stateHandler.getCommandList(this, completeOperation, completeOperation.getBuffer(), afterPipe);
   }
 }


