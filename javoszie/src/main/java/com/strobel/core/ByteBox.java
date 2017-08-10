 package com.strobel.core;
 
 
 
 
 
 public final class ByteBox
   implements IStrongBox
 {
   public byte value;
   
 
 
 
 
   public ByteBox() {}
   
 
 
 
   public ByteBox(byte value)
   {
     this.value = value;
   }
   
   public Byte get()
   {
     return Byte.valueOf(this.value);
   }
   
 
   public void set(Object value)
   {
     this.value = ((Byte)value).byteValue();
   }
 }


