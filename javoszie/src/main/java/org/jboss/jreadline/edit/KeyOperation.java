 package org.jboss.jreadline.edit;
 
 import java.util.Arrays;
 import org.jboss.jreadline.edit.actions.Action;
 import org.jboss.jreadline.edit.actions.Operation;
 
 
 
 
 
 
 
 
 
 
 
 public class KeyOperation
 {
   private int[] keyValues;
   private Operation operation;
   private Action workingMode = Action.NO_ACTION;
   
   public KeyOperation(int value, Operation operation) {
     this.keyValues = new int[] { value };
     this.operation = operation;
   }
   
   public KeyOperation(int[] value, Operation operation) {
     this.keyValues = value;
     this.operation = operation;
   }
   
   public KeyOperation(int value, Operation operation, Action workingMode) {
     this.keyValues = new int[] { value };
     this.operation = operation;
     this.workingMode = workingMode;
   }
   
   public KeyOperation(int[] value, Operation operation, Action workingMode) {
     this.keyValues = value;
     this.operation = operation;
     this.workingMode = workingMode;
   }
   
   public int[] getKeyValues() {
     return this.keyValues;
   }
   
   public int getFirstValue() {
     return this.keyValues[0];
   }
   
   public boolean hasMoreThanOneKeyValue() {
     return this.keyValues.length > 1;
   }
   
   public Operation getOperation() {
     return this.operation;
   }
   
   public Action getWorkingMode() {
     return this.workingMode;
   }
   
   public boolean equals(Object o) {
     if ((o instanceof KeyOperation)) {
       KeyOperation ko = (KeyOperation)o;
       if ((ko.getOperation() == this.operation) && 
         (ko.getKeyValues().length == this.keyValues.length)) {
         for (int i = 0; i < this.keyValues.length; i++)
           if (ko.getKeyValues()[i] != this.keyValues[i])
             return false;
         return true;
       }
     }
     
     return false;
   }
   
   public int hashCode() {
     return 1481003;
   }
   
   public String toString() {
     return "Operation: " + this.operation + ", " + Arrays.toString(this.keyValues);
   }
   
   public boolean equalValues(int[] values) {
     return Arrays.equals(this.keyValues, values);
   }
 }


