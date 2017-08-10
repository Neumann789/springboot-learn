 package com.strobel.assembler.metadata.signatures;
 
 
 
 
 
 
 
 
 
 public final class FormalTypeParameter
   implements TypeTree
 {
   private final String _name;
   
 
 
 
 
 
 
   private final FieldTypeSignature[] _bounds;
   
 
 
 
 
 
 
 
   private FormalTypeParameter(String name, FieldTypeSignature[] bounds)
   {
     this._name = name;
     this._bounds = bounds;
   }
   
   public static FormalTypeParameter make(String name, FieldTypeSignature[] bounds) {
     return new FormalTypeParameter(name, bounds);
   }
   
   public FieldTypeSignature[] getBounds() {
     return this._bounds;
   }
   
   public String getName() {
     return this._name;
   }
   
   public void accept(TypeTreeVisitor<?> v) {
     v.visitFormalTypeParameter(this);
   }
 }


