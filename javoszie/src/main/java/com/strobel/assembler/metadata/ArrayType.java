 package com.strobel.assembler.metadata;
 
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ArrayType
   extends TypeReference
 {
   private final TypeReference _elementType;
   private String _internalName;
   private String _fullName;
   private String _simpleName;
   
   ArrayType(TypeReference elementType)
   {
     this._elementType = ((TypeReference)VerifyArgument.notNull(elementType, "elementType"));
     
     setName(elementType.getName() + "[]");
   }
   
   public boolean containsGenericParameters()
   {
     return this._elementType.containsGenericParameters();
   }
   
   public String getPackageName()
   {
     return this._elementType.getPackageName();
   }
   
   public String getSimpleName() {
     if (this._simpleName == null) {
       this._simpleName = (this._elementType.getSimpleName() + "[]");
     }
     return this._simpleName;
   }
   
   public String getFullName() {
     if (this._fullName == null) {
       this._fullName = (this._elementType.getFullName() + "[]");
     }
     return this._fullName;
   }
   
   public String getInternalName() {
     if (this._internalName == null) {
       this._internalName = ("[" + this._elementType.getInternalName());
     }
     return this._internalName;
   }
   
   public final boolean isArray()
   {
     return true;
   }
   
   public final TypeReference getElementType()
   {
     return this._elementType;
   }
   
   public final <R, P> R accept(TypeMetadataVisitor<P, R> visitor, P parameter)
   {
     return (R)visitor.visitArrayType(this, parameter);
   }
   
   public final TypeReference getUnderlyingType()
   {
     return this._elementType.getUnderlyingType();
   }
   
   public final StringBuilder appendSignature(StringBuilder sb)
   {
     sb.append('[');
     return this._elementType.appendSignature(sb);
   }
   
   public final StringBuilder appendErasedSignature(StringBuilder sb)
   {
     return this._elementType.appendErasedSignature(sb.append('['));
   }
   
   public final StringBuilder appendBriefDescription(StringBuilder sb) {
     return this._elementType.appendBriefDescription(sb).append("[]");
   }
   
   public final StringBuilder appendSimpleDescription(StringBuilder sb) {
     return this._elementType.appendSimpleDescription(sb).append("[]");
   }
   
   public final StringBuilder appendDescription(StringBuilder sb) {
     return appendBriefDescription(sb);
   }
   
   public static ArrayType create(TypeReference elementType) {
     return new ArrayType(elementType);
   }
   
   public final TypeDefinition resolve()
   {
     TypeDefinition resolvedElementType = this._elementType.resolve();
     
     if (resolvedElementType != null) {
       return resolvedElementType;
     }
     
     return super.resolve();
   }
 }


