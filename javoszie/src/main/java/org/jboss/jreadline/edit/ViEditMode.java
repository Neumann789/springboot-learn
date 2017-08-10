 package org.jboss.jreadline.edit;
 
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import org.jboss.jreadline.console.Config;
 import org.jboss.jreadline.edit.actions.Action;
 import org.jboss.jreadline.edit.actions.Operation;
 
 
 
 
 
 
 
 
 
 
 
 public class ViEditMode
   implements EditMode
 {
   private Action mode;
   private Action previousMode;
   private Operation previousAction;
   private KeyOperationManager operationManager;
   private List<KeyOperation> currentOperations = new ArrayList();
   private int operationLevel = 0;
   
   public ViEditMode(KeyOperationManager operations) {
     this.mode = Action.EDIT;
     this.previousMode = Action.EDIT;
     this.operationManager = operations;
   }
   
   public boolean isInEditMode() {
     return this.mode == Action.EDIT;
   }
   
   private void switchEditMode() {
     if (this.mode == Action.EDIT) {
       this.mode = Action.MOVE;
     } else
       this.mode = Action.EDIT;
   }
   
   private boolean isDeleteMode() {
     return this.mode == Action.DELETE;
   }
   
   private boolean isChangeMode() {
     return this.mode == Action.CHANGE;
   }
   
   private boolean isInReplaceMode() {
     return this.mode == Action.REPLACE;
   }
   
   private boolean isYankMode() {
     return this.mode == Action.YANK;
   }
   
   private Operation saveAction(Operation action) {
     this.previousMode = this.mode;
     
     if (action.getAction() != Action.MOVE) {
       this.previousAction = action;
     }
     
     if ((isDeleteMode()) || (isYankMode()))
       this.mode = Action.MOVE;
     if (isChangeMode()) {
       this.mode = Action.EDIT;
     }
     return action;
   }
   
   public Operation parseInput(int[] in)
   {
     int input = in[0];
     Iterator<KeyOperation> operationIterator;
     if ((Config.isOSPOSIXCompatible()) && (in.length > 1)) {
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
         if ((input == ko.getFirstValue()) && (ko.getKeyValues().length == in.length)) {
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
         if (((KeyOperation)this.currentOperations.get(0)).getOperation() == Operation.DELETE_PREV_CHAR) {
           this.currentOperations.clear();
           return Operation.SEARCH_DELETE;
         }
         
         if (((KeyOperation)this.currentOperations.get(0)).getOperation() == Operation.ESCAPE) {
           this.mode = Action.EDIT;
           this.currentOperations.clear();
           return Operation.SEARCH_EXIT;
         }
         
 
         this.currentOperations.clear();
         return Operation.SEARCH_INPUT;
       }
       
 
       if (this.currentOperations.size() > 1) {
         this.mode = Action.EDIT;
         this.currentOperations.clear();
         return Operation.SEARCH_EXIT;
       }
       
 
       this.currentOperations.clear();
       return Operation.SEARCH_INPUT;
     }
     
 
     if (isInReplaceMode()) {
       if ((this.currentOperations.size() == 1) && 
         (((KeyOperation)this.currentOperations.get(0)).getOperation() == Operation.ESCAPE)) {
         this.operationLevel = 0;
         this.currentOperations.clear();
         this.mode = Action.MOVE;
         return Operation.NO_ACTION;
       }
       
       this.operationLevel = 0;
       this.currentOperations.clear();
       this.mode = Action.MOVE;
       return saveAction(Operation.REPLACE);
     }
     
 
 
     if (this.currentOperations.isEmpty()) {
       if (isInEditMode()) {
         return Operation.EDIT;
       }
       return Operation.NO_ACTION;
     }
     
     if (this.currentOperations.size() == 1) {
       Operation operation = ((KeyOperation)this.currentOperations.get(0)).getOperation();
       Action workingMode = ((KeyOperation)this.currentOperations.get(0)).getWorkingMode();
       this.operationLevel = 0;
       this.currentOperations.clear();
       
       if (operation == Operation.NEW_LINE) {
         this.mode = Action.EDIT;
         return Operation.NEW_LINE;
       }
       if ((operation == Operation.REPLACE) && (!isInEditMode())) {
         this.mode = Action.REPLACE;
         return Operation.NO_ACTION;
       }
       if ((operation == Operation.DELETE_PREV_CHAR) && (workingMode == Action.NO_ACTION)) {
         if (isInEditMode()) {
           return Operation.DELETE_PREV_CHAR;
         }
         return Operation.MOVE_PREV_CHAR;
       }
       if ((operation == Operation.DELETE_NEXT_CHAR) && (workingMode == Action.COMMAND)) {
         if (isInEditMode()) {
           return Operation.NO_ACTION;
         }
         return saveAction(Operation.DELETE_NEXT_CHAR);
       }
       
       if (operation == Operation.COMPLETE) {
         if (isInEditMode()) {
           return Operation.COMPLETE;
         }
         return Operation.NO_ACTION;
       }
       if (operation == Operation.ESCAPE) {
         switchEditMode();
         if (isInEditMode()) {
           return Operation.NO_ACTION;
         }
         return Operation.MOVE_PREV_CHAR;
       }
       if (operation == Operation.SEARCH_PREV) {
         this.mode = Action.SEARCH;
         return Operation.SEARCH_PREV;
       }
       if (operation == Operation.CLEAR) {
         return Operation.CLEAR;
       }
       if ((operation == Operation.MOVE_PREV_CHAR) && (workingMode.equals(Action.EDIT)))
         return Operation.MOVE_PREV_CHAR;
       if ((operation == Operation.MOVE_NEXT_CHAR) && (workingMode.equals(Action.EDIT)))
         return Operation.MOVE_NEXT_CHAR;
       if ((operation == Operation.HISTORY_PREV) && (workingMode.equals(Action.EDIT)))
         return operation;
       if ((operation == Operation.HISTORY_NEXT) && (workingMode.equals(Action.EDIT))) {
         return operation;
       }
       
       if (!isInEditMode()) {
         return inCommandMode(operation, workingMode);
       }
       return Operation.EDIT;
     }
     
     this.operationLevel += 1;
     return Operation.NO_ACTION;
   }
   
 
 
   private Operation inCommandMode(Operation operation, Action workingMode)
   {
     if (operation == Operation.PREV_CHAR) {
       if (this.mode == Action.MOVE)
         return saveAction(Operation.MOVE_PREV_CHAR);
       if (this.mode == Action.DELETE)
         return saveAction(Operation.DELETE_PREV_CHAR);
       if (this.mode == Action.CHANGE) {
         return saveAction(Operation.CHANGE_PREV_CHAR);
       }
       return saveAction(Operation.YANK_PREV_CHAR);
     }
     if (operation == Operation.NEXT_CHAR) {
       if (this.mode == Action.MOVE)
         return saveAction(Operation.MOVE_NEXT_CHAR);
       if (this.mode == Action.DELETE)
         return saveAction(Operation.DELETE_NEXT_CHAR);
       if (this.mode == Action.CHANGE) {
         return saveAction(Operation.CHANGE_NEXT_CHAR);
       }
       return saveAction(Operation.YANK_NEXT_CHAR);
     }
     if (operation == Operation.HISTORY_NEXT) {
       return saveAction(Operation.HISTORY_NEXT);
     }
     if (operation == Operation.HISTORY_PREV)
       return saveAction(Operation.HISTORY_PREV);
     if (operation == Operation.PREV_WORD) {
       if (this.mode == Action.MOVE)
         return saveAction(Operation.MOVE_PREV_WORD);
       if (this.mode == Action.DELETE)
         return saveAction(Operation.DELETE_PREV_WORD);
       if (this.mode == Action.CHANGE) {
         return saveAction(Operation.CHANGE_PREV_WORD);
       }
       return saveAction(Operation.YANK_PREV_WORD);
     }
     if (operation == Operation.PREV_BIG_WORD) {
       if (this.mode == Action.MOVE)
         return saveAction(Operation.MOVE_PREV_BIG_WORD);
       if (this.mode == Action.DELETE)
         return saveAction(Operation.DELETE_PREV_BIG_WORD);
       if (this.mode == Action.CHANGE) {
         return saveAction(Operation.CHANGE_PREV_BIG_WORD);
       }
       return saveAction(Operation.YANK_PREV_BIG_WORD);
     }
     if (operation == Operation.NEXT_WORD) {
       if (this.mode == Action.MOVE)
         return saveAction(Operation.MOVE_NEXT_WORD);
       if (this.mode == Action.DELETE)
         return saveAction(Operation.DELETE_NEXT_WORD);
       if (this.mode == Action.CHANGE) {
         return saveAction(Operation.CHANGE_NEXT_WORD);
       }
       return saveAction(Operation.YANK_NEXT_WORD);
     }
     if (operation == Operation.NEXT_BIG_WORD) {
       if (this.mode == Action.MOVE)
         return saveAction(Operation.MOVE_NEXT_BIG_WORD);
       if (this.mode == Action.DELETE)
         return saveAction(Operation.DELETE_NEXT_BIG_WORD);
       if (this.mode == Action.CHANGE) {
         return saveAction(Operation.CHANGE_NEXT_BIG_WORD);
       }
       return saveAction(Operation.YANK_NEXT_BIG_WORD);
     }
     if (operation == Operation.BEGINNING) {
       if (this.mode == Action.MOVE)
         return saveAction(Operation.MOVE_BEGINNING);
       if (this.mode == Action.DELETE)
         return saveAction(Operation.DELETE_BEGINNING);
       if (this.mode == Action.CHANGE) {
         return saveAction(Operation.CHANGE_BEGINNING);
       }
       return saveAction(Operation.YANK_BEGINNING);
     }
     if (operation == Operation.END) {
       if (this.mode == Action.MOVE)
         return saveAction(Operation.MOVE_END);
       if (this.mode == Action.DELETE)
         return saveAction(Operation.DELETE_END);
       if (this.mode == Action.CHANGE) {
         return saveAction(Operation.CHANGE_END);
       }
       return saveAction(Operation.YANK_END);
     }
     
 
     if (operation == Operation.DELETE_NEXT_CHAR) {
       return saveAction(operation);
     }
     if ((operation == Operation.DELETE_PREV_CHAR) && (workingMode == Action.COMMAND)) {
       return saveAction(operation);
     }
     if (operation == Operation.PASTE_AFTER) {
       return saveAction(operation);
     }
     if (operation == Operation.PASTE_BEFORE) {
       return saveAction(operation);
     }
     if (operation == Operation.CHANGE_NEXT_CHAR) {
       switchEditMode();
       return saveAction(operation);
     }
     if (operation == Operation.CHANGE_ALL) {
       this.mode = Action.CHANGE;
       return saveAction(operation);
     }
     
     if (operation == Operation.MOVE_NEXT_CHAR) {
       switchEditMode();
       return saveAction(operation);
     }
     if (operation == Operation.MOVE_END) {
       switchEditMode();
       return saveAction(operation);
     }
     if (operation == Operation.INSERT) {
       switchEditMode();
       return saveAction(Operation.NO_ACTION);
     }
     if (operation == Operation.INSERT_BEGINNING) {
       switchEditMode();
       return saveAction(Operation.MOVE_BEGINNING);
     }
     
     if (operation == Operation.DELETE_ALL)
     {
       if (isDeleteMode()) {
         return saveAction(operation);
       }
       this.mode = Action.DELETE;
     } else {
       if (operation == Operation.DELETE_END) {
         this.mode = Action.DELETE;
         return saveAction(operation);
       }
       if (operation == Operation.CHANGE) {
         if (isChangeMode()) {
           return saveAction(Operation.CHANGE_ALL);
         }
         this.mode = Action.CHANGE;
       } else {
         if (operation == Operation.CHANGE_END) {
           this.mode = Action.CHANGE;
           return saveAction(operation);
         }
         
 
 
 
 
 
         if (operation == Operation.REPEAT) {
           this.mode = this.previousMode;
           return this.previousAction;
         }
         if (operation == Operation.UNDO) {
           return saveAction(operation);
         }
         if (operation == Operation.CASE) {
           return saveAction(operation);
         }
         if (operation == Operation.YANK_ALL)
         {
           if (isYankMode()) {
             return saveAction(operation);
           }
           this.mode = Action.YANK;
         }
         else if ((operation == Operation.VI_EDIT_MODE) || (operation == Operation.EMACS_EDIT_MODE))
         {
           return operation;
         } } }
     return Operation.NO_ACTION;
   }
   
   public Action getCurrentAction()
   {
     return this.mode;
   }
   
   public Mode getMode()
   {
     return Mode.VI;
   }
 }


