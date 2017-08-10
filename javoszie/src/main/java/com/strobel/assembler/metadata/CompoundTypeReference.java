 package com.strobel.assembler.metadata;
 
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class CompoundTypeReference
   extends TypeReference
 {
   private final TypeReference _baseType;
   private final List<TypeReference> _interfaces;
   
   public CompoundTypeReference(TypeReference baseType, List<TypeReference> interfaces)
   {
     this._baseType = baseType;
     this._interfaces = interfaces;
   }
   
   public final TypeReference getBaseType() {
     return this._baseType;
   }
   
   public final List<TypeReference> getInterfaces() {
     return this._interfaces;
   }
   
   public TypeReference getDeclaringType()
   {
     return null;
   }
   
   public String getSimpleName()
   {
     if (this._baseType != null) {
       return this._baseType.getSimpleName();
     }
     return ((TypeReference)this._interfaces.get(0)).getSimpleName();
   }
   
   public boolean containsGenericParameters()
   {
     TypeReference baseType = getBaseType();
     
     if ((baseType != null) && (baseType.containsGenericParameters())) {
       return true;
     }
     
     for (TypeReference t : this._interfaces) {
       if (t.containsGenericParameters()) {
         return true;
       }
     }
     
     return false;
   }
   
   public String getName()
   {
     if (this._baseType != null) {
       return this._baseType.getName();
     }
     return ((TypeReference)this._interfaces.get(0)).getName();
   }
   
   public String getFullName()
   {
     if (this._baseType != null) {
       return this._baseType.getFullName();
     }
     return ((TypeReference)this._interfaces.get(0)).getFullName();
   }
   
   public String getInternalName()
   {
     if (this._baseType != null) {
       return this._baseType.getInternalName();
     }
     return ((TypeReference)this._interfaces.get(0)).getInternalName();
   }
   
   public final <R, P> R accept(TypeMetadataVisitor<P, R> visitor, P parameter)
   {
     return (R)visitor.visitCompoundType(this, parameter);
   }
   
   public StringBuilder appendBriefDescription(StringBuilder sb)
   {
     TypeReference baseType = this._baseType;
     List<TypeReference> interfaces = this._interfaces;
     
     StringBuilder s = sb;
     
     if (baseType != null) {
       s = baseType.appendBriefDescription(s);
       if (!interfaces.isEmpty()) {
         s.append(" & ");
       }
     }
     
     int i = 0; for (int n = interfaces.size(); i < n; i++) {
       if (i != 0) {
         s.append(" & ");
       }
       s = ((TypeReference)interfaces.get(i)).appendBriefDescription(s);
     }
     
     return s;
   }
   
   public StringBuilder appendSimpleDescription(StringBuilder sb)
   {
     TypeReference baseType = this._baseType;
     List<TypeReference> interfaces = this._interfaces;
     
     StringBuilder s = sb;
     
     if (baseType != null) {
       s = baseType.appendSimpleDescription(s);
       if (!interfaces.isEmpty()) {
         s.append(" & ");
       }
     }
     
     int i = 0; for (int n = interfaces.size(); i < n; i++) {
       if (i != 0) {
         s.append(" & ");
       }
       s = ((TypeReference)interfaces.get(i)).appendSimpleDescription(s);
     }
     
     return s;
   }
   
   public StringBuilder appendErasedDescription(StringBuilder sb)
   {
     TypeReference baseType = this._baseType;
     List<TypeReference> interfaces = this._interfaces;
     
     StringBuilder s = sb;
     
     if (baseType != null) {
       s = baseType.appendErasedDescription(s);
       if (!interfaces.isEmpty()) {
         s.append(" & ");
       }
     }
     
     int i = 0; for (int n = interfaces.size(); i < n; i++) {
       if (i != 0) {
         s.append(" & ");
       }
       s = ((TypeReference)interfaces.get(i)).appendErasedDescription(s);
     }
     
     return s;
   }
   
   public StringBuilder appendDescription(StringBuilder sb)
   {
     return appendBriefDescription(sb);
   }
   
   public StringBuilder appendSignature(StringBuilder sb)
   {
     StringBuilder s = sb;
     
     if (this._baseType != null) {
       s = this._baseType.appendSignature(s);
     }
     
     if (this._interfaces.isEmpty()) {
       return s;
     }
     
     for (TypeReference interfaceType : this._interfaces) {
       s.append(':');
       s = interfaceType.appendSignature(s);
     }
     
     return s;
   }
   
   public StringBuilder appendErasedSignature(StringBuilder sb)
   {
     if (this._baseType != null) {
       return this._baseType.appendErasedSignature(sb);
     }
     
     if (!this._interfaces.isEmpty()) {
       return ((TypeReference)this._interfaces.get(0)).appendErasedSignature(sb);
     }
     
     return BuiltinTypes.Object.appendErasedSignature(sb);
   }
 }


