 package com.strobel.assembler.metadata.signatures;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class DoubleSignature
   implements BaseType
 {
   private static final DoubleSignature _singleton = new DoubleSignature();
   
 
 
   public static DoubleSignature make()
   {
     return _singleton;
   }
   
   public void accept(TypeTreeVisitor<?> v) {
     v.visitDoubleSignature(this);
   }
 }


