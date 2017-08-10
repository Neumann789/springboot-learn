 package com.strobel.assembler.metadata.signatures;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class VoidSignature
   implements BaseType
 {
   private static final VoidSignature _singleton = new VoidSignature();
   
 
 
   public static VoidSignature make()
   {
     return _singleton;
   }
   
   public void accept(TypeTreeVisitor<?> v) {
     v.visitVoidSignature(this);
   }
 }


