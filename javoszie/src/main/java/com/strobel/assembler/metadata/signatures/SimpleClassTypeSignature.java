 package com.strobel.assembler.metadata.signatures;
 
 
 
 
 
 
 
 public final class SimpleClassTypeSignature
   implements FieldTypeSignature
 {
   private final boolean _dollar;
   
 
 
 
 
 
   private final String _name;
   
 
 
 
 
   private final TypeArgument[] _typeArguments;
   
 
 
 
 
 
   private SimpleClassTypeSignature(String n, boolean dollar, TypeArgument[] tas)
   {
     this._name = n;
     this._dollar = dollar;
     this._typeArguments = tas;
   }
   
 
 
   public static SimpleClassTypeSignature make(String n, boolean dollar, TypeArgument[] tas)
   {
     return new SimpleClassTypeSignature(n, dollar, tas);
   }
   
 
 
 
 
 
   public boolean useDollar()
   {
     return this._dollar;
   }
   
   public String getName() {
     return this._name;
   }
   
   public TypeArgument[] getTypeArguments() {
     return this._typeArguments;
   }
   
   public void accept(TypeTreeVisitor<?> v) {
     v.visitSimpleClassTypeSignature(this);
   }
 }


