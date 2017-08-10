 package com.strobel.assembler.metadata;
 
 import com.strobel.assembler.Collection;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ParameterDefinitionCollection
   extends Collection<ParameterDefinition>
 {
   final IMethodSignature signature;
   private TypeReference _declaringType;
   
   ParameterDefinitionCollection(IMethodSignature signature)
   {
     this.signature = signature;
   }
   
   public final TypeReference getDeclaringType() {
     return this._declaringType;
   }
   
   final void setDeclaringType(TypeReference declaringType) {
     this._declaringType = declaringType;
     
     for (int i = 0; i < size(); i++) {
       ((ParameterDefinition)get(i)).setDeclaringType(declaringType);
     }
   }
   
   protected void afterAdd(int index, ParameterDefinition p, boolean appended)
   {
     p.setMethod(this.signature);
     p.setPosition(index);
     p.setDeclaringType(this._declaringType);
     
     if (!appended) {
       for (int i = index + 1; i < size(); i++) {
         ((ParameterDefinition)get(i)).setPosition(i + 1);
       }
     }
   }
   
   protected void beforeSet(int index, ParameterDefinition p)
   {
     ParameterDefinition current = (ParameterDefinition)get(index);
     
     current.setMethod(null);
     current.setPosition(-1);
     current.setDeclaringType(null);
     
     p.setMethod(this.signature);
     p.setPosition(index);
     p.setDeclaringType(this._declaringType);
   }
   
   protected void afterRemove(int index, ParameterDefinition p)
   {
     p.setMethod(null);
     p.setPosition(-1);
     p.setDeclaringType(null);
     
     for (int i = index; i < size(); i++) {
       ((ParameterDefinition)get(i)).setPosition(i);
     }
   }
   
   protected void beforeClear()
   {
     for (int i = 0; i < size(); i++) {
       ((ParameterDefinition)get(i)).setMethod(null);
       ((ParameterDefinition)get(i)).setPosition(-1);
       ((ParameterDefinition)get(i)).setDeclaringType(null);
     }
   }
 }


