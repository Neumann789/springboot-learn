 package com.strobel.util;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ContractUtils
 {
   public static IllegalStateException unreachable()
   {
     return new IllegalStateException("Code supposed to be unreachable");
   }
   
   public static UnsupportedOperationException unsupported() {
     return new UnsupportedOperationException("The requested operation is not supported.");
   }
 }


