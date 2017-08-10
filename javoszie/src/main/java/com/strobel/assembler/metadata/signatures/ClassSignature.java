 package com.strobel.assembler.metadata.signatures;
 
 
 
 
 
 
 
 
 public final class ClassSignature
   implements Signature
 {
   private final FormalTypeParameter[] _formalTypeParameters;
   
 
 
 
 
 
 
   private final ClassTypeSignature _baseClass;
   
 
 
 
 
 
   private final ClassTypeSignature[] _interfaces;
   
 
 
 
 
 
 
   private ClassSignature(FormalTypeParameter[] ftps, ClassTypeSignature sc, ClassTypeSignature[] sis)
   {
     this._formalTypeParameters = ftps;
     this._baseClass = sc;
     this._interfaces = sis;
   }
   
 
 
   public static ClassSignature make(FormalTypeParameter[] ftps, ClassTypeSignature sc, ClassTypeSignature[] sis)
   {
     return new ClassSignature(ftps, sc, sis);
   }
   
   public FormalTypeParameter[] getFormalTypeParameters() {
     return this._formalTypeParameters;
   }
   
   public ClassTypeSignature getSuperType() {
     return this._baseClass;
   }
   
   public ClassTypeSignature[] getInterfaces() {
     return this._interfaces;
   }
   
   public void accept(Visitor v) {
     v.visitClassSignature(this);
   }
 }


