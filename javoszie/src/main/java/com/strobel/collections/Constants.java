 package com.strobel.collections;
 
 
 
 
 
 
 
 
 
 
 final class Constants
 {
   public static final int DEFAULT_INT_NO_ENTRY_VALUE;
   
 
 
 
 
 
 
 
 
 
   static
   {
     String property = System.getProperty("gnu.trove.no_entry.int", "0");
     int value;
     if ("MAX_VALUE".equalsIgnoreCase(property)) {
       value = Integer.MAX_VALUE;
     } else {
       if ("MIN_VALUE".equalsIgnoreCase(property)) {
         value = Integer.MIN_VALUE;
       }
       else {
         value = Integer.valueOf(property).intValue();
       }
     }
     DEFAULT_INT_NO_ENTRY_VALUE = value;
   }
 }


