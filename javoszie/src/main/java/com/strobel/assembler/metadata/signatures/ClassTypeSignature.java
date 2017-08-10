 package com.strobel.assembler.metadata.signatures;
 
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ClassTypeSignature
   implements FieldTypeSignature
 {
   private final List<SimpleClassTypeSignature> _path;
   
   private ClassTypeSignature(List<SimpleClassTypeSignature> path)
   {
     this._path = path;
   }
   
   public static ClassTypeSignature make(List<SimpleClassTypeSignature> p) {
     return new ClassTypeSignature(p);
   }
   
   public List<SimpleClassTypeSignature> getPath() {
     return this._path;
   }
   
   public void accept(TypeTreeVisitor<?> v) {
     v.visitClassTypeSignature(this);
   }
 }


