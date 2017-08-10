 package com.strobel.decompiler.ast;
 
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.assembler.metadata.VariableDefinition;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Variable
 {
   private String _name;
   private boolean _isGenerated;
   private boolean _isLambdaParameter;
   private TypeReference _type;
   private VariableDefinition _originalVariable;
   private ParameterDefinition _originalParameter;
   
   public final String getName()
   {
     return this._name;
   }
   
   public final void setName(String name) {
     this._name = name;
   }
   
   public final boolean isParameter() {
     if (this._originalParameter != null) {
       return true;
     }
     
     VariableDefinition originalVariable = this._originalVariable;
     
     return (originalVariable != null) && (originalVariable.isParameter());
   }
   
   public final boolean isGenerated()
   {
     return this._isGenerated;
   }
   
   public final void setGenerated(boolean generated) {
     this._isGenerated = generated;
   }
   
   public final TypeReference getType() {
     return this._type;
   }
   
   public final void setType(TypeReference type) {
     this._type = type;
   }
   
   public final VariableDefinition getOriginalVariable() {
     return this._originalVariable;
   }
   
   public final void setOriginalVariable(VariableDefinition originalVariable) {
     this._originalVariable = originalVariable;
   }
   
   public final ParameterDefinition getOriginalParameter() {
     ParameterDefinition originalParameter = this._originalParameter;
     
     if (originalParameter != null) {
       return originalParameter;
     }
     
     VariableDefinition originalVariable = this._originalVariable;
     
     if (originalVariable != null) {
       return originalVariable.getParameter();
     }
     
     return null;
   }
   
   public final void setOriginalParameter(ParameterDefinition originalParameter) {
     this._originalParameter = originalParameter;
   }
   
   public final boolean isLambdaParameter() {
     return this._isLambdaParameter;
   }
   
   public final void setLambdaParameter(boolean lambdaParameter) {
     this._isLambdaParameter = lambdaParameter;
   }
   
   public final String toString()
   {
     return this._name;
   }
 }


