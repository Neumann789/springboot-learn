 package com.strobel.assembler.metadata.signatures;
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ArrayTypeSignature
   implements FieldTypeSignature
 {
   private final TypeSignature _componentType;
   
 
 
 
 
 
 
 
 
 
 
 
   private ArrayTypeSignature(TypeSignature componentType)
   {
     this._componentType = componentType;
   }
   
   public static ArrayTypeSignature make(TypeSignature ct) {
     return new ArrayTypeSignature(ct);
   }
   
   public TypeSignature getComponentType() {
     return this._componentType;
   }
   
   public void accept(TypeTreeVisitor<?> v) {
     v.visitArrayTypeSignature(this);
   }
 }


