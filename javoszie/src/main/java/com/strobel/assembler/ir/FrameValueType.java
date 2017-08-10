 package com.strobel.assembler.ir;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public enum FrameValueType
 {
   Empty, 
   Top, 
   Integer, 
   Float, 
   Long, 
   Double, 
   Null, 
   UninitializedThis, 
   Reference, 
   Uninitialized, 
   Address;
   
   private FrameValueType() {}
   public final boolean isDoubleWord() { return (this == Double) || (this == Long); }
 }


