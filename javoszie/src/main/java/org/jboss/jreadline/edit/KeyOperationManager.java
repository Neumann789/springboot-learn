 package org.jboss.jreadline.edit;
 
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Iterator;
 import java.util.List;
 import org.jboss.jreadline.edit.actions.Action;
 
 
 
 
 
 
 
 
 
 public class KeyOperationManager
 {
   private List<KeyOperation> operations;
   
   public KeyOperationManager()
   {
     this.operations = new ArrayList();
   }
   
   public List<KeyOperation> getOperations() {
     return this.operations;
   }
   
   public void clear() {
     this.operations.clear();
   }
   
   public void addOperations(List<KeyOperation> newOperations) {
     for (KeyOperation ko : newOperations) {
       checkAndRemove(ko);
       this.operations.add(ko);
     }
   }
   
   public void addOperation(KeyOperation operation) {
     checkAndRemove(operation);
     this.operations.add(operation);
   }
   
   private boolean exists(KeyOperation operation) {
     for (KeyOperation ko : this.operations) {
       if (Arrays.equals(ko.getKeyValues(), operation.getKeyValues()))
         return true;
     }
     return false;
   }
   
   private void checkAndRemove(KeyOperation ko) {
     Iterator<KeyOperation> iter = this.operations.iterator();
     while (iter.hasNext()) {
       KeyOperation operation = (KeyOperation)iter.next();
       if ((Arrays.equals(operation.getKeyValues(), ko.getKeyValues())) && 
         (operation.getWorkingMode().equals(ko.getWorkingMode()))) {
         iter.remove();
         return;
       }
     }
   }
   
   public KeyOperation findOperation(int[] input) {
     for (KeyOperation operation : this.operations) {
       if (operation.equalValues(input))
         return operation;
     }
     return null;
   }
 }


