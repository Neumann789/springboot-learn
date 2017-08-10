 package com.strobel.decompiler.languages.java.ast;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public enum AssignmentOperatorType
 {
   ASSIGN, 
   ADD, 
   SUBTRACT, 
   MULTIPLY, 
   DIVIDE, 
   MODULUS, 
   SHIFT_LEFT, 
   SHIFT_RIGHT, 
   UNSIGNED_SHIFT_RIGHT, 
   BITWISE_AND, 
   BITWISE_OR, 
   EXCLUSIVE_OR, 
   ANY;
   
   private AssignmentOperatorType() {}
   public final boolean isCompoundAssignment() { switch (this) {
     case ASSIGN: 
     case ANY: 
       return false;
     }
     
     return true;
   }
 }


