 package com.strobel.assembler.metadata;
 
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class CompositeTypeLoader
   implements ITypeLoader
 {
   private final ITypeLoader[] _typeLoaders;
   
   public CompositeTypeLoader(ITypeLoader... typeLoaders)
   {
     this._typeLoaders = ((ITypeLoader[])((ITypeLoader[])VerifyArgument.noNullElementsAndNotEmpty(typeLoaders, "typeLoaders")).clone());
   }
   
   public boolean tryLoadType(String internalName, Buffer buffer)
   {
     for (ITypeLoader typeLoader : this._typeLoaders) {
       if (typeLoader.tryLoadType(internalName, buffer)) {
         return true;
       }
       
       buffer.reset();
     }
     
     return false;
   }
 }


