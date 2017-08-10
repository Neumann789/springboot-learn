 package org.jboss.jreadline.edit.actions;
 
 
 
 
 
 
 
 
 public class NextSpaceWordAction
   extends EditAction
 {
   public NextSpaceWordAction(int start, Action action)
   {
     super(start, action);
   }
   
   public void doAction(String buffer)
   {
     int cursor = getStart();
     
     if ((cursor < buffer.length()) && (isDelimiter(buffer.charAt(cursor)))) {}
     while ((cursor < buffer.length()) && (isDelimiter(buffer.charAt(cursor)))) {
       cursor++; continue;
       
 
       while ((cursor < buffer.length()) && (!isSpace(buffer.charAt(cursor)))) {
         cursor++;
       }
       
       if ((cursor < buffer.length()) && (isSpace(buffer.charAt(cursor)))) {
         while ((cursor < buffer.length()) && (isSpace(buffer.charAt(cursor))))
           cursor++;
       }
     }
     setEnd(cursor);
   }
 }


