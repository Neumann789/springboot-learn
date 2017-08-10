 package com.strobel.core;
 
 
 
 
 
 
 
 
 public abstract class Mapping<T>
 {
   private final String _name;
   
 
 
 
 
 
 
 
   protected Mapping()
   {
     this(null);
   }
   
   protected Mapping(String name) {
     this._name = name;
   }
   
   public abstract T apply(T paramT);
   
   public String toString()
   {
     if (this._name != null) {
       return this._name;
     }
     return super.toString();
   }
 }


