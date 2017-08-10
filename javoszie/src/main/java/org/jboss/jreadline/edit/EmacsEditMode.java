 package org.jboss.jreadline.edit;
 
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import org.jboss.jreadline.console.Config;
 import org.jboss.jreadline.edit.actions.Action;
 import org.jboss.jreadline.edit.actions.Operation;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class EmacsEditMode
   implements EditMode
 {
   private Action mode = Action.EDIT;
   
   private KeyOperationManager operationManager;
   private List<KeyOperation> currentOperations = new ArrayList();
   private int operationLevel = 0;
   
   public EmacsEditMode(KeyOperationManager operations) {
     this.operationManager = operations;
   }
   
 
   public Operation parseInput(int[] in)
   {
     int input = in[0];
     Iterator<KeyOperation> operationIterator; if ((Config.isOSPOSIXCompatible()) && (in.length > 1)) {
       KeyOperation ko = KeyOperationFactory.findOperation(this.operationManager.getOperations(), in);
       if (ko != null)
       {
         this.currentOperations.clear();
         this.currentOperations.add(ko);
 
       }
       
 
     }
     else if (this.operationLevel > 0) {
       operationIterator = this.currentOperations.iterator();
       while (operationIterator.hasNext()) {
         if (input != ((KeyOperation)operationIterator.next()).getKeyValues()[this.operationLevel]) {
           operationIterator.remove();
         }
       }
     }
     else {
       for (KeyOperation ko : this.operationManager.getOperations()) {
         if (input == ko.getFirstValue()) {
           this.currentOperations.add(ko);
         }
       }
     }
     
     if (this.mode == Action.SEARCH) {
       if (this.currentOperations.size() == 1) {
         if (((KeyOperation)this.currentOperations.get(0)).getOperation() == Operation.NEW_LINE) {
           this.mode = Action.EDIT;
           this.currentOperations.clear();
           return Operation.SEARCH_END;
         }
         if (((KeyOperation)this.currentOperations.get(0)).getOperation() == Operation.SEARCH_PREV) {
           this.currentOperations.clear();
           return Operation.SEARCH_PREV_WORD;
         }
         if (((KeyOperation)this.currentOperations.get(0)).getOperation() == Operation.SEARCH_NEXT_WORD) {
           this.currentOperations.clear();
           return Operation.SEARCH_NEXT_WORD;
         }
         if (((KeyOperation)this.currentOperations.get(0)).getOperation() == Operation.DELETE_PREV_CHAR) {
           this.currentOperations.clear();
           return Operation.SEARCH_DELETE;
         }
         
 
         this.currentOperations.clear();
         return Operation.NO_ACTION;
       }
       
 
       if (this.currentOperations.size() > 1) {
         this.mode = Action.EDIT;
         this.currentOperations.clear();
         return Operation.SEARCH_EXIT;
       }
       
 
       this.currentOperations.clear();
       return Operation.SEARCH_INPUT;
     }
     
 
 
     if (this.currentOperations.isEmpty())
     {
       if (this.operationLevel > 0) {
         this.operationLevel = 0;
         this.currentOperations.clear();
         return Operation.NO_ACTION;
       }
       
       return Operation.EDIT;
     }
     if (this.currentOperations.size() == 1)
     {
       int level = this.operationLevel + 1;
       if (in.length > level)
         level = in.length;
       if (((KeyOperation)this.currentOperations.get(0)).getKeyValues().length > level) {
         this.operationLevel += 1;
         return Operation.NO_ACTION;
       }
       Operation currentOperation = ((KeyOperation)this.currentOperations.get(0)).getOperation();
       if ((currentOperation == Operation.SEARCH_PREV) || (currentOperation == Operation.SEARCH_NEXT_WORD))
       {
         this.mode = Action.SEARCH;
       }
       this.operationLevel = 0;
       this.currentOperations.clear();
       
       return currentOperation;
     }
     
     this.operationLevel += 1;
     return Operation.NO_ACTION;
   }
   
 
 
   public Action getCurrentAction()
   {
     return this.mode;
   }
   
   public Mode getMode()
   {
     return Mode.EMACS;
   }
 }


