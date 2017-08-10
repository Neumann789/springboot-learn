 package com.strobel.assembler.metadata;
 
 import com.strobel.assembler.Collection;
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class AnonymousLocalTypeCollection
   extends Collection<TypeDefinition>
 {
   private final MethodDefinition _owner;
   
   public AnonymousLocalTypeCollection(MethodDefinition owner)
   {
     this._owner = ((MethodDefinition)VerifyArgument.notNull(owner, "owner"));
   }
   
   protected void afterAdd(int index, TypeDefinition type, boolean appended)
   {
     type.setDeclaringMethod(this._owner);
   }
   
   protected void beforeSet(int index, TypeDefinition type)
   {
     TypeDefinition current = (TypeDefinition)get(index);
     
     current.setDeclaringMethod(null);
     type.setDeclaringMethod(this._owner);
   }
   
   protected void afterRemove(int index, TypeDefinition type)
   {
     type.setDeclaringMethod(null);
   }
   
   protected void beforeClear()
   {
     for (int i = 0; i < size(); i++) {
       ((TypeDefinition)get(i)).setDeclaringMethod(null);
     }
   }
 }


