 package com.strobel.assembler.metadata.signatures;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class LongSignature
   implements BaseType
 {
   private static final LongSignature _singleton = new LongSignature();
   
 
 
   public static LongSignature make()
   {
     return _singleton;
   }
   
   public void accept(TypeTreeVisitor<?> v) {
     v.visitLongSignature(this);
   }
 }


