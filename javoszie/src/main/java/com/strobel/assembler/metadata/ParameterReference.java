 package com.strobel.assembler.metadata;
 
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class ParameterReference
   implements IMetadataTypeMember
 {
   private String _name;
   private int _position = -1;
   private TypeReference _parameterType;
   
   protected ParameterReference(String name, TypeReference parameterType) {
     this._name = (name != null ? name : "");
     this._parameterType = ((TypeReference)VerifyArgument.notNull(parameterType, "parameterType"));
   }
   
   public abstract TypeReference getDeclaringType();
   
   public String getName()
   {
     if (StringUtilities.isNullOrEmpty(this._name)) {
       if (this._position < 0) {
         return this._name;
       }
       return "p" + this._position;
     }
     return this._name;
   }
   
   public final boolean hasName() {
     return !StringUtilities.isNullOrEmpty(this._name);
   }
   
   protected void setName(String name) {
     this._name = name;
   }
   
   public int getPosition() {
     return this._position;
   }
   
   protected void setPosition(int position) {
     this._position = position;
   }
   
   public TypeReference getParameterType() {
     return this._parameterType;
   }
   
   protected void setParameterType(TypeReference parameterType) {
     this._parameterType = parameterType;
   }
   
   public String toString()
   {
     return getName();
   }
   
   public abstract ParameterDefinition resolve();
 }


