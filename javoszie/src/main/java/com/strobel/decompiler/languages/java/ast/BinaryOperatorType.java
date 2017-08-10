 package com.strobel.decompiler.languages.java.ast;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public enum BinaryOperatorType
 {
   ANY, 
   BITWISE_AND, 
   BITWISE_OR, 
   EXCLUSIVE_OR, 
   LOGICAL_AND, 
   LOGICAL_OR, 
   GREATER_THAN, 
   GREATER_THAN_OR_EQUAL, 
   LESS_THAN, 
   LESS_THAN_OR_EQUAL, 
   EQUALITY, 
   INEQUALITY, 
   ADD, 
   SUBTRACT, 
   MULTIPLY, 
   DIVIDE, 
   MODULUS, 
   SHIFT_LEFT, 
   SHIFT_RIGHT, 
   UNSIGNED_SHIFT_RIGHT;
   
   private BinaryOperatorType() {}
   public final boolean isCommutative() { switch (this) {
     case BITWISE_AND: 
     case BITWISE_OR: 
     case EXCLUSIVE_OR: 
     case EQUALITY: 
     case INEQUALITY: 
     case ADD: 
     case MULTIPLY: 
       return true;
     }
     
     return false;
   }
 }


