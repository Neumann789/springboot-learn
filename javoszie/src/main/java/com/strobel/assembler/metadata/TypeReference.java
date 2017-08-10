 package com.strobel.assembler.metadata;
 
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.util.ContractUtils;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class TypeReference
   extends MemberReference
   implements IGenericParameterProvider, IGenericContext
 {
   private String _name;
   private TypeReference _declaringType;
   private ArrayType _arrayType;
   
   public boolean containsGenericParameters()
   {
     if (isGenericType()) {
       if (isGenericDefinition()) {
         if (hasGenericParameters()) {
           return true;
         }
       }
       else if ((this instanceof IGenericInstance)) {
         List<TypeReference> typeArguments = ((IGenericInstance)this).getTypeArguments();
         
         int i = 0; for (int n = typeArguments.size(); i < n; i++) {
           if (((TypeReference)typeArguments.get(i)).containsGenericParameters()) {
             return true;
           }
         }
       }
     }
     
     return super.containsGenericParameters();
   }
   
   public String getName()
   {
     return this._name;
   }
   
   public String getPackageName() {
     return "";
   }
   
   public TypeReference getDeclaringType()
   {
     return this._declaringType;
   }
   
   public boolean isEquivalentTo(MemberReference member)
   {
     return ((member instanceof TypeReference)) && (MetadataResolver.areEquivalent(this, (TypeReference)member));
   }
   
   protected void setName(String name)
   {
     this._name = name;
   }
   
   protected final void setDeclaringType(TypeReference declaringType) {
     this._declaringType = declaringType;
   }
   
   public abstract String getSimpleName();
   
   public String getFullName() {
     StringBuilder name = new StringBuilder();
     appendName(name, true, true);
     return name.toString();
   }
   
   public String getInternalName() {
     StringBuilder name = new StringBuilder();
     appendName(name, true, false);
     return name.toString();
   }
   
   public TypeReference getUnderlyingType() {
     return this;
   }
   
   public TypeReference getElementType() {
     return null;
   }
   
   public abstract <R, P> R accept(TypeMetadataVisitor<P, R> paramTypeMetadataVisitor, P paramP);
   
   public int hashCode()
   {
     return getInternalName().hashCode();
   }
   
   public boolean equals(Object obj)
   {
     return ((obj instanceof TypeReference)) && (MetadataHelper.isSameType(this, (TypeReference)obj, true));
   }
   
 
 
   public TypeReference makeArrayType()
   {
     if (this._arrayType == null) {
       synchronized (this) {
         if (this._arrayType == null) {
           this._arrayType = ArrayType.create(this);
         }
       }
     }
     return this._arrayType;
   }
   
   public TypeReference makeGenericType(List<? extends TypeReference> typeArguments) {
     VerifyArgument.notNull(typeArguments, "typeArguments");
     
     return makeGenericType((TypeReference[])typeArguments.toArray(new TypeReference[typeArguments.size()]));
   }
   
 
   public TypeReference makeGenericType(TypeReference... typeArguments)
   {
     VerifyArgument.noNullElementsAndNotEmpty(typeArguments, "typeArguments");
     
     if (isGenericDefinition()) {
       return new ParameterizedType(this, ArrayUtilities.asUnmodifiableList(typeArguments));
     }
     
 
 
 
     if ((this instanceof IGenericInstance)) {
       return new ParameterizedType((TypeReference)((IGenericInstance)this).getGenericDefinition(), ArrayUtilities.asUnmodifiableList(typeArguments));
     }
     
 
 
 
     throw Error.notGenericType(this);
   }
   
 
 
 
   public boolean isWildcardType()
   {
     return false;
   }
   
   public boolean isCompoundType() {
     return false;
   }
   
   public boolean isBoundedType() {
     return (isGenericParameter()) || (isWildcardType()) || ((this instanceof ICapturedType)) || ((this instanceof CompoundTypeReference));
   }
   
 
 
   public boolean isUnbounded()
   {
     return true;
   }
   
   public boolean hasExtendsBound() {
     return (isGenericParameter()) || ((isWildcardType()) && (!BuiltinTypes.Object.equals(getExtendsBound())) && (MetadataResolver.areEquivalent(BuiltinTypes.Bottom, getSuperBound())));
   }
   
 
 
   public boolean hasSuperBound()
   {
     return (isWildcardType()) && (!MetadataResolver.areEquivalent(BuiltinTypes.Bottom, getSuperBound()));
   }
   
   public TypeReference getExtendsBound()
   {
     throw ContractUtils.unsupported();
   }
   
   public TypeReference getSuperBound() {
     throw ContractUtils.unsupported();
   }
   
 
 
 
   public JvmType getSimpleType()
   {
     return JvmType.Object;
   }
   
   public boolean isNested() {
     return (getDeclaringType() != null) && (!isGenericParameter());
   }
   
   public boolean isArray() {
     return getSimpleType() == JvmType.Array;
   }
   
   public boolean isPrimitive() {
     return false;
   }
   
   public boolean isVoid() {
     return false;
   }
   
 
 
 
 
   public boolean hasGenericParameters()
   {
     return !getGenericParameters().isEmpty();
   }
   
   public boolean isGenericDefinition()
   {
     return (hasGenericParameters()) && (isDefinition());
   }
   
 
   public List<GenericParameter> getGenericParameters()
   {
     return Collections.emptyList();
   }
   
   public boolean isGenericParameter() {
     return getSimpleType() == JvmType.TypeVariable;
   }
   
   public boolean isGenericType() {
     return hasGenericParameters();
   }
   
   public TypeReference getRawType() {
     if (isGenericType()) {
       TypeReference underlyingType = getUnderlyingType();
       
       if (underlyingType != this) {
         return underlyingType.getRawType();
       }
       
       return new RawType(this);
     }
     throw ContractUtils.unsupported();
   }
   
   public GenericParameter findTypeVariable(String name)
   {
     for (GenericParameter genericParameter : getGenericParameters()) {
       if (StringUtilities.equals(genericParameter.getName(), name)) {
         return genericParameter;
       }
     }
     
     TypeReference declaringType = getDeclaringType();
     
     if (declaringType != null) {
       return declaringType.findTypeVariable(name);
     }
     
     return null;
   }
   
 
 
 
 
 
 
 
   public String getBriefDescription()
   {
     return appendBriefDescription(new StringBuilder()).toString();
   }
   
 
 
   public String getDescription()
   {
     return appendDescription(new StringBuilder()).toString();
   }
   
 
 
   public String getErasedDescription()
   {
     return appendErasedDescription(new StringBuilder()).toString();
   }
   
 
 
   public String getSimpleDescription()
   {
     return appendSimpleDescription(new StringBuilder()).toString();
   }
   
   protected StringBuilder appendName(StringBuilder sb, boolean fullName, boolean dottedName)
   {
     String simpleName = getSimpleName();
     TypeReference declaringType = getDeclaringType();
     
     if ((dottedName) && (simpleName != null) && (declaringType != null)) {
       return declaringType.appendName(sb, fullName, true).append('.').append(simpleName);
     }
     
     String name = fullName ? getName() : simpleName;
     String packageName = fullName ? getPackageName() : null;
     
     if (StringUtilities.isNullOrEmpty(packageName)) {
       return sb.append(name);
     }
     
     if (dottedName) {
       return sb.append(packageName).append('.').append(name);
     }
     
 
 
     int packageEnd = packageName.length();
     
     for (int i = 0; i < packageEnd; i++) {
       char c = packageName.charAt(i);
       sb.append(c == '.' ? '/' : c);
     }
     
     sb.append('/');
     
     return sb.append(name);
   }
   
   protected StringBuilder appendBriefDescription(StringBuilder sb) {
     StringBuilder s = appendName(sb, true, true);
     
     List<? extends TypeReference> typeArguments;
     if ((this instanceof IGenericInstance)) {
       typeArguments = ((IGenericInstance)this).getTypeArguments();
     } else {
       if (isGenericDefinition()) {
         typeArguments = getGenericParameters();
       }
       else {
         typeArguments = Collections.emptyList();
       }
     }
     int count = typeArguments.size();
     
     if (count > 0) {
       s.append('<');
       for (int i = 0; i < count; i++) {
         if (i != 0) {
           s.append(", ");
         }
         s = ((TypeReference)typeArguments.get(i)).appendBriefDescription(s);
       }
       s.append('>');
     }
     
     return s;
   }
   
   protected StringBuilder appendSimpleDescription(StringBuilder sb) {
     StringBuilder s = sb.append(getSimpleName());
     
     if (isGenericType()) {
       List<? extends TypeReference> typeArguments;
       if ((this instanceof IGenericInstance)) {
         typeArguments = ((IGenericInstance)this).getTypeArguments();
       }
       else {
         typeArguments = getGenericParameters();
       }
       
       int count = typeArguments.size();
       
       if (count > 0) {
         s.append('<');
         for (int i = 0; i < count; i++) {
           if (i != 0) {
             s.append(", ");
           }
           
           TypeReference typeArgument = (TypeReference)typeArguments.get(i);
           
           if ((typeArgument instanceof GenericParameter)) {
             s.append(typeArgument.getSimpleName());
           }
           else {
             s = typeArgument.appendSimpleDescription(s);
           }
         }
         s.append('>');
       }
     }
     
     return s;
   }
   
   protected StringBuilder appendErasedDescription(StringBuilder sb) {
     return appendName(sb, true, true);
   }
   
   protected StringBuilder appendDescription(StringBuilder sb) {
     StringBuilder s = appendName(sb, false, true);
     
     if ((this instanceof IGenericInstance)) {
       List<TypeReference> typeArguments = ((IGenericInstance)this).getTypeArguments();
       int count = typeArguments.size();
       
       if (count > 0) {
         s.append('<');
         for (int i = 0; i < count; i++) {
           if (i != 0) {
             s.append(", ");
           }
           s = ((TypeReference)typeArguments.get(i)).appendBriefDescription(s);
         }
         s.append('>');
       }
     }
     
     return s;
   }
   
   protected StringBuilder appendSignature(StringBuilder sb) {
     if (isGenericParameter()) {
       sb.append('T');
       sb.append(getName());
       sb.append(';');
       return sb;
     }
     
     return appendClassSignature(sb);
   }
   
   protected StringBuilder appendErasedSignature(StringBuilder sb) {
     if ((isGenericType()) && (!isGenericDefinition())) {
       return getUnderlyingType().appendErasedSignature(sb);
     }
     return appendErasedClassSignature(sb);
   }
   
   public String toString()
   {
     return getBriefDescription();
   }
   
   protected StringBuilder appendGenericSignature(StringBuilder sb) {
     StringBuilder s = sb;
     
     if (isGenericParameter()) {
       TypeReference extendsBound = getExtendsBound();
       TypeDefinition resolvedBound = extendsBound.resolve();
       
       s.append(getName());
       
       if ((resolvedBound != null) && (resolvedBound.isInterface())) {
         s.append(':');
       }
       
       s.append(':');
       s = extendsBound.appendSignature(s);
       
       return s;
     }
     
     if ((this instanceof IGenericInstance)) {
       List<TypeReference> typeArguments = ((IGenericInstance)this).getTypeArguments();
       int count = typeArguments.size();
       
       if (count > 0) {
         s.append('<');
         
         for (int i = 0; i < count; i++) {
           s = ((TypeReference)typeArguments.get(i)).appendGenericSignature(s);
         }
         s.append('>');
       }
     }
     
     return s;
   }
   
   protected StringBuilder appendClassSignature(StringBuilder sb) {
     StringBuilder s = sb;
     
     s.append('L');
     s = appendName(s, true, false);
     
     if ((this instanceof IGenericInstance)) {
       List<TypeReference> typeArguments = ((IGenericInstance)this).getTypeArguments();
       int count = typeArguments.size();
       
       if (count > 0) {
         s.append('<');
         for (int i = 0; i < count; i++) {
           TypeReference type = (TypeReference)typeArguments.get(i);
           if (type.isGenericDefinition()) {
             s = type.appendErasedSignature(s);
           }
           else {
             s = type.appendSignature(s);
           }
         }
         s.append('>');
       }
     }
     
     s.append(';');
     return s;
   }
   
   protected StringBuilder appendErasedClassSignature(StringBuilder sb) {
     sb.append('L');
     sb = appendName(sb, true, false);
     sb.append(';');
     return sb;
   }
   
   protected StringBuilder appendClassDescription(StringBuilder sb) {
     StringBuilder s = sb;
     
     appendName(sb, true, true);
     
     if ((this instanceof IGenericInstance)) {
       List<TypeReference> typeArguments = ((IGenericInstance)this).getTypeArguments();
       int count = typeArguments.size();
       
       if (count > 0) {
         s.append('<');
         
         for (int i = 0; i < count; i++) {
           s = ((TypeReference)typeArguments.get(i)).appendErasedClassSignature(s);
         }
         s.append('>');
       }
     }
     
     return s;
   }
   
 
 
 
   public TypeDefinition resolve()
   {
     TypeReference declaringType = getDeclaringType();
     
     return declaringType != null ? declaringType.resolve(this) : null;
   }
   
   public FieldDefinition resolve(FieldReference field) {
     TypeDefinition resolvedType = resolve();
     
     if (resolvedType != null) {
       return MetadataResolver.getField(resolvedType.getDeclaredFields(), field);
     }
     
     return null;
   }
   
   public MethodDefinition resolve(MethodReference method) {
     TypeDefinition resolvedType = resolve();
     
     if (resolvedType != null) {
       return MetadataResolver.getMethod(resolvedType.getDeclaredMethods(), method);
     }
     
     return null;
   }
   
   public TypeDefinition resolve(TypeReference type) {
     TypeDefinition resolvedType = resolve();
     
     if (resolvedType != null) {
       return MetadataResolver.getNestedType(resolvedType.getDeclaredTypes(), type);
     }
     
     return null;
   }
 }


