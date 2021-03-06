 package org.jboss.jreadline.console.helper;
 
 import org.jboss.jreadline.edit.actions.Operation;
 
 
 
 
 
 
 
 
 
 
 public class Search
 {
   private StringBuilder searchTerm;
   private Operation operation;
   private String result;
   private int input;
   private boolean finished;
   
   public Search(Operation operation, int input)
   {
     setOperation(operation);
     setSearchTerm(new StringBuilder());
     setResult(null);
     setInput(input);
   }
   
   public StringBuilder getSearchTerm() {
     return this.searchTerm;
   }
   
   public void setSearchTerm(String searchTerm) {
     this.searchTerm = new StringBuilder(searchTerm);
   }
   
   public void setSearchTerm(StringBuilder searchTerm) {
     this.searchTerm = searchTerm;
   }
   
   public Operation getOperation() {
     return this.operation;
   }
   
   public void setOperation(Operation operation) {
     this.operation = operation;
   }
   
   public String getResult() {
     return this.result;
   }
   
   public void setResult(String result) {
     this.result = result;
   }
   
   public int getInput() {
     return this.input;
   }
   
   public void setInput(int input) {
     this.input = input;
   }
   
   public boolean isFinished() {
     return this.finished;
   }
   
   public void setFinished(boolean finished) {
     this.finished = finished;
   }
 }


