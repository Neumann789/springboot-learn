 package com.strobel.assembler.ir;
 
 
 
 
 
 
 
 
 
 
 public final class ErrorOperand
 {
   private final String _message;
   
 
 
 
 
 
 
 
 
   public ErrorOperand(String message)
   {
     this._message = message;
   }
   
   public String toString()
   {
     if (this._message != null) {
       return this._message;
     }
     
     return "!!! BAD OPERAND !!!";
   }
 }


