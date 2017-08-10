 package com.strobel.assembler.metadata;
 
 import com.strobel.core.Freezable;
 import com.strobel.core.StringUtilities;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class VariableReference
   extends Freezable
   implements IMetadataTypeMember
 {
   private String _name;
   private TypeReference _variableType;
   
   protected VariableReference(TypeReference variableType)
   {
     this._variableType = variableType;
   }
   
   protected VariableReference(String name, TypeReference variableType) {
     this._name = name;
     this._variableType = variableType;
   }
   
   public final String getName() {
     return this._name;
   }
   
   public abstract TypeReference getDeclaringType();
   
   public final boolean hasName()
   {
     return !StringUtilities.isNullOrEmpty(this._name);
   }
   
   protected final void setName(String name) {
     this._name = name;
   }
   
   public final TypeReference getVariableType() {
     return this._variableType;
   }
   
   protected final void setVariableType(TypeReference variableType) {
     this._variableType = variableType;
   }
   
   public abstract int getSlot();
   
   public abstract VariableDefinition resolve();
   
   public String toString()
   {
     return getName();
   }
 }


