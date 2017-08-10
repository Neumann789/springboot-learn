 package com.strobel.assembler.metadata;
 
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class VariableDefinition
   extends VariableReference
 {
   private final int _slot;
   private final MethodDefinition _declaringMethod;
   private int _scopeStart;
   private int _scopeEnd;
   private boolean _isTypeKnown;
   private boolean _fromMetadata;
   private ParameterDefinition _parameter;
   
   public VariableDefinition(int slot, String name, MethodDefinition declaringMethod)
   {
     super(name, ((MethodDefinition)VerifyArgument.notNull(declaringMethod, "declaringMethod")).getDeclaringType());
     this._declaringMethod = declaringMethod;
     this._slot = slot;
   }
   
   public VariableDefinition(int slot, String name, MethodDefinition declaringMethod, TypeReference variableType) {
     this(slot, name, declaringMethod);
     setVariableType(variableType);
   }
   
   public final boolean isParameter() {
     return this._parameter != null;
   }
   
   public final ParameterDefinition getParameter() {
     return this._parameter;
   }
   
   public final void setParameter(ParameterDefinition parameter) {
     verifyNotFrozen();
     this._parameter = parameter;
   }
   
   public final TypeReference getDeclaringType()
   {
     return this._declaringMethod.getDeclaringType();
   }
   
   public final int getSlot() {
     return this._slot;
   }
   
   public final int getSize() {
     return getVariableType().getSimpleType().stackSlots();
   }
   
   public final int getScopeStart() {
     return this._scopeStart;
   }
   
   public final void setScopeStart(int scopeStart) {
     verifyNotFrozen();
     this._scopeStart = scopeStart;
   }
   
   public final int getScopeEnd() {
     return this._scopeEnd;
   }
   
   public final void setScopeEnd(int scopeEnd) {
     verifyNotFrozen();
     this._scopeEnd = scopeEnd;
   }
   
   public final boolean isTypeKnown() {
     return this._isTypeKnown;
   }
   
   public final void setTypeKnown(boolean typeKnown) {
     verifyNotFrozen();
     this._isTypeKnown = typeKnown;
   }
   
   public final boolean isFromMetadata() {
     return this._fromMetadata;
   }
   
   public final void setFromMetadata(boolean fromMetadata) {
     verifyNotFrozen();
     this._fromMetadata = fromMetadata;
   }
   
   public VariableDefinition resolve()
   {
     return this;
   }
   
   public String toString()
   {
     return "VariableDefinition{Slot=" + this._slot + ", ScopeStart=" + this._scopeStart + ", ScopeEnd=" + this._scopeEnd + ", Name=" + getName() + ", IsFromMetadata=" + this._fromMetadata + ", IsTypeKnown=" + this._isTypeKnown + ", Type=" + getVariableType().getSignature() + '}';
   }
 }


