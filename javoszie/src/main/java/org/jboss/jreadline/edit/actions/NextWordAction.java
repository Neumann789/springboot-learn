 package org.jboss.jreadline.edit.actions;
 
 
 
 
 
 
 
 
 
 public class NextWordAction
   extends EditAction
 {
   private boolean removeTrailingSpaces = true;
   
   public NextWordAction(int start, Action action) {
     super(start, action);
     if (getAction() == Action.CHANGE) {
       this.removeTrailingSpaces = false;
     }
   }
   
   public void doAction(String buffer) {
     int cursor = getStart();
     
 
     if ((cursor < buffer.length()) && (isDelimiter(buffer.charAt(cursor)))) {}
     while ((cursor < buffer.length()) && (isDelimiter(buffer.charAt(cursor)))) {
       cursor++; continue;
       
 
       while ((cursor < buffer.length()) && (!isDelimiter(buffer.charAt(cursor)))) {
         cursor++;
       }
       
       if ((this.removeTrailingSpaces) && 
         (cursor < buffer.length()) && (isSpace(buffer.charAt(cursor)))) {
         while ((cursor < buffer.length()) && (isSpace(buffer.charAt(cursor))))
           cursor++;
       }
     }
     setEnd(cursor);
   }
 }


