 package org.jboss.jreadline.complete;
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class Completion
 {
   private boolean hasToAskForConfirmation = true;
   private boolean askForConfirmationResponse = true;
   
 
 
 
   public abstract void complete(CompleteOperation paramCompleteOperation, boolean paramBoolean);
   
 
 
 
   public void setHasToAskForConfirmation(boolean hasToAsk)
   {
     this.hasToAskForConfirmation = hasToAsk;
   }
   
   public boolean hasToAskForConfirmation() {
     return this.hasToAskForConfirmation;
   }
   
   public void setAskForConfirmationResponse(boolean showAll) {
     this.askForConfirmationResponse = showAll;
   }
   
   public boolean getAskForConfirmationResponse() {
     return this.askForConfirmationResponse;
   }
 }


