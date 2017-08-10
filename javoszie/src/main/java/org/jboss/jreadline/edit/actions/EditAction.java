 package org.jboss.jreadline.edit.actions;
 
 
 
 public abstract class EditAction
 {
   private int start;
   
 
   private int end;
   
 
   private Action action;
   
 
   protected EditAction(int start, Action action)
   {
     setStart(start);
     setAction(action);
   }
   
 
 
   public abstract void doAction(String paramString);
   
 
 
   private void setAction(Action action)
   {
     this.action = action;
   }
   
   public final Action getAction()
   {
     return this.action;
   }
   
   private void setStart(int start) {
     this.start = start;
   }
   
   public final int getStart() {
     return this.start;
   }
   
   protected void setEnd(int end) {
     this.end = end;
   }
   
   public final int getEnd() {
     return this.end;
   }
   
 
 
 
 
 
 
   protected final boolean isDelimiter(char c)
   {
     return !Character.isLetterOrDigit(c);
   }
   
 
 
 
 
 
   protected final boolean isSpace(char c)
   {
     return Character.isWhitespace(c);
   }
 }


