 package com.strobel.decompiler.languages.java;
 
 
 
 
 
 
 public abstract interface OffsetToLineNumberConverter
 {
   public static final int UNKNOWN_LINE_NUMBER = -100;
   
 
 
 
 
   public static final OffsetToLineNumberConverter NOOP_CONVERTER = new OffsetToLineNumberConverter() {
     public int getLineForOffset(int offset) {
       return -100;
     }
   };
   
   public abstract int getLineForOffset(int paramInt);
 }


