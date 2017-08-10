 package com.strobel.assembler.metadata.signatures;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class BottomSignature
   implements FieldTypeSignature
 {
   private static final BottomSignature _singleton = new BottomSignature();
   
 
 
   public static BottomSignature make()
   {
     return _singleton;
   }
   
   public void accept(TypeTreeVisitor<?> v) {
     v.visitBottomSignature(this);
   }
 }


