 package org.jboss.jreadline.edit.actions;
 
 
 
 
 
 
 
 
 
 public class DeleteAction
   extends EditAction
 {
   private boolean backspace = false;
   
   public DeleteAction(int start, Action action) {
     super(start, action);
   }
   
   public DeleteAction(int start, Action action, boolean backspace) {
     super(start, action);
     this.backspace = backspace;
   }
   
   public void doAction(String buffer)
   {
     if (this.backspace) {
       if (getStart() == 0) {
         setEnd(0);
       } else {
         setEnd(getStart() - 1);
       }
     }
     else if (buffer.length() <= getStart()) {
       setEnd(getStart());
     } else {
       setEnd(getStart() + 1);
     }
   }
 }


