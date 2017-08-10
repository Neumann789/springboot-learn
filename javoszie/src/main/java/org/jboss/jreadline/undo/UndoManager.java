 package org.jboss.jreadline.undo;
 
 import java.util.Stack;
 
 
 
 
 
 
 
 
 
 
 public class UndoManager
 {
   private static short UNDO_SIZE = 50;
   private Stack<UndoAction> undoStack;
   private int counter;
   
   public UndoManager()
   {
     this.undoStack = new Stack();
     this.undoStack.setSize(UNDO_SIZE);
     this.counter = 0;
   }
   
   public UndoAction getNext() {
     if (this.counter > 0) {
       this.counter -= 1;
       return (UndoAction)this.undoStack.pop();
     }
     
     return null;
   }
   
   public void addUndo(UndoAction u) {
     if (this.counter <= UNDO_SIZE) {
       this.counter += 1;
       this.undoStack.push(u);
     }
     else {
       this.undoStack.remove(UNDO_SIZE);
       this.undoStack.push(u);
     }
   }
   
   public void clear()
   {
     this.undoStack.clear();
     this.counter = 0;
   }
   
   public boolean isEmpty() {
     return this.counter == 0;
   }
   
   public int size() {
     return this.counter;
   }
 }

