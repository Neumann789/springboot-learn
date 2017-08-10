 package com.strobel.assembler.metadata.signatures;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class FloatSignature
   implements BaseType
 {
   private static final FloatSignature _singleton = new FloatSignature();
   
 
 
   public static FloatSignature make()
   {
     return _singleton;
   }
   
   public void accept(TypeTreeVisitor<?> v) {
     v.visitFloatSignature(this);
   }
 }


