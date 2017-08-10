 package com.strobel.assembler.metadata;
 
 import com.strobel.assembler.Collection;
 import com.strobel.assembler.ir.ConstantPool;
 import com.strobel.assembler.ir.attributes.SourceAttribute;
 import com.strobel.assembler.metadata.annotations.CustomAnnotation;
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;

import java.util.ArrayList;
import java.util.Collections;
 import java.util.List;
 import javax.lang.model.element.Modifier;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class TypeDefinition
   extends TypeReference
   implements IMemberDefinition
 {
   private final GenericParameterCollection _genericParameters;
   private final Collection<TypeDefinition> _declaredTypes;
   private final Collection<FieldDefinition> _declaredFields;
   private final Collection<MethodDefinition> _declaredMethods;
   private final Collection<TypeReference> _explicitInterfaces;
   private final Collection<CustomAnnotation> _customAnnotations;
   private final Collection<SourceAttribute> _sourceAttributes;
   private final List<GenericParameter> _genericParametersView;
   private final List<TypeDefinition> _declaredTypesView;
   private final List<FieldDefinition> _declaredFieldsView;
   private final List<MethodDefinition> _declaredMethodsView;
   private final List<TypeReference> _explicitInterfacesView;
   private final List<CustomAnnotation> _customAnnotationsView;
   private final List<SourceAttribute> _sourceAttributesView;
   private IMetadataResolver _resolver;
   private String _simpleName;
   private String _packageName;
   private String _internalName;
   private String _fullName;
   private String _signature;
   private String _erasedSignature;
   private TypeReference _baseType;
   private long _flags;
   private int _compilerVersion;
   private List<Enum> _enumConstants;
   private TypeReference _rawType;
   private MethodReference _declaringMethod;
   private ConstantPool _constantPool;
   private ITypeLoader _typeLoader;
   
   public TypeDefinition()
   {
     this._genericParameters = new GenericParameterCollection(this);
     this._declaredTypes = new Collection();
     this._declaredFields = new Collection();
     this._declaredMethods = new Collection();
     this._explicitInterfaces = new Collection();
     this._customAnnotations = new Collection();
     this._sourceAttributes = new Collection();
     this._genericParametersView = Collections.unmodifiableList(this._genericParameters);
     this._declaredTypesView = Collections.unmodifiableList(this._declaredTypes);
     this._declaredFieldsView = Collections.unmodifiableList(this._declaredFields);
     this._declaredMethodsView = Collections.unmodifiableList(this._declaredMethods);
     this._explicitInterfacesView = Collections.unmodifiableList(this._explicitInterfaces);
     this._customAnnotationsView = Collections.unmodifiableList(this._customAnnotations);
     this._sourceAttributesView = Collections.unmodifiableList(this._sourceAttributes);
   }
   
   public TypeDefinition(IMetadataResolver resolver) {
     this();
     this._resolver = ((IMetadataResolver)VerifyArgument.notNull(resolver, "resolver"));
   }
   
   final ITypeLoader getTypeLoader() {
     return this._typeLoader;
   }
   
   final void setTypeLoader(ITypeLoader typeLoader) {
     this._typeLoader = typeLoader;
   }
   
   public final int getCompilerMajorVersion() {
     return this._compilerVersion >>> 16;
   }
   
   public final int getCompilerMinorVersion() {
     return this._compilerVersion & 0xFFFF;
   }
   
   public final ConstantPool getConstantPool() {
     return this._constantPool;
   }
   
   protected final void setConstantPool(ConstantPool constantPool) {
     this._constantPool = constantPool;
   }
   
   protected final void setCompilerVersion(int majorVersion, int minorVersion) {
     this._compilerVersion = ((majorVersion & 0xFFFF) << 16 | minorVersion & 0xFFFF);
   }
   
   public final IMetadataResolver getResolver() {
     return this._resolver;
   }
   
   protected final void setResolver(IMetadataResolver resolver) {
     this._resolver = resolver;
   }
   
   public String getPackageName() {
     TypeReference declaringType = getDeclaringType();
     
     if (declaringType != null) {
       return declaringType.getPackageName();
     }
     
     return this._packageName != null ? this._packageName : "";
   }
   
 
   public String getSimpleName()
   {
     return this._simpleName != null ? this._simpleName : getName();
   }
   
   protected final void setSimpleName(String simpleName)
   {
     this._simpleName = simpleName;
   }
   
   protected void setPackageName(String packageName) {
     this._packageName = packageName;
     this._fullName = null;
     this._internalName = null;
   }
   
   public String getFullName() {
     if (this._fullName == null) {
       this._fullName = super.getFullName();
     }
     return this._fullName;
   }
   
   public String getErasedSignature()
   {
     if (this._erasedSignature == null) {
       this._erasedSignature = super.getErasedSignature();
     }
     return this._erasedSignature;
   }
   
   public String getSignature()
   {
     if (this._signature == null) {
       this._signature = super.getSignature();
     }
     return this._signature;
   }
   
   public String getInternalName() {
     if (this._internalName == null) {
       this._internalName = super.getInternalName();
     }
     return this._internalName;
   }
   
   public <R, P> R accept(TypeMetadataVisitor<P, R> visitor, P parameter)
   {
     return (R)visitor.visitClassType(this, parameter);
   }
   
   public final MethodReference getDeclaringMethod() {
     return this._declaringMethod;
   }
   
   protected final void setDeclaringMethod(MethodReference declaringMethod) {
     this._declaringMethod = declaringMethod;
   }
   
   public final TypeReference getBaseType() {
     return this._baseType;
   }
   
   protected final void setBaseType(TypeReference baseType) {
     this._baseType = baseType;
   }
   
   public final List<Enum> getEnumConstants() {
     if (isEnum()) {
       //return this._enumConstants != null ? this._enumConstants : Collections.emptyList();
    	 return this._enumConstants != null ? this._enumConstants : new ArrayList<Enum>();
     }
     
     throw Error.notEnumType(this);
   }
   
   protected final void setEnumConstants(Enum... values) {
     VerifyArgument.notNull(values, "values");
     
     this._enumConstants = (values.length == 0 ? null : ArrayUtilities.asUnmodifiableList(values));
   }
   
   public final List<TypeReference> getExplicitInterfaces()
   {
     return this._explicitInterfacesView;
   }
   
   public final List<CustomAnnotation> getAnnotations()
   {
     return this._customAnnotationsView;
   }
   
   public final List<SourceAttribute> getSourceAttributes() {
     return this._sourceAttributesView;
   }
   
   public final List<GenericParameter> getGenericParameters()
   {
     return this._genericParametersView;
   }
   
   public TypeReference getRawType()
   {
     if (isGenericType()) {
       if (this._rawType == null) {
         synchronized (this) {
           if (this._rawType == null) {
             this._rawType = new RawType(this);
           }
         }
       }
       return this._rawType;
     }
     return this;
   }
   
   public GenericParameter findTypeVariable(String name)
   {
     for (GenericParameter genericParameter : getGenericParameters()) {
       if (StringUtilities.equals(genericParameter.getName(), name)) {
         return genericParameter;
       }
     }
     
     MethodReference declaringMethod = getDeclaringMethod();
     
     if (declaringMethod != null) {
       return declaringMethod.findTypeVariable(name);
     }
     
     TypeReference declaringType = getDeclaringType();
     
     if ((declaringType != null) && (!isStatic())) {
       return declaringType.findTypeVariable(name);
     }
     
     return null;
   }
   
   protected StringBuilder appendName(StringBuilder sb, boolean fullName, boolean dottedName)
   {
     if ((fullName) && (dottedName) && (isNested()) && (!isAnonymous()) && (this._simpleName != null)) {
       return getDeclaringType().appendName(sb, true, true).append('.').append(this._simpleName);
     }
     
     return super.appendName(sb, fullName, dottedName);
   }
   
   protected final GenericParameterCollection getGenericParametersInternal() {
     return this._genericParameters;
   }
   
   protected final Collection<TypeDefinition> getDeclaredTypesInternal() {
     return this._declaredTypes;
   }
   
   protected final Collection<FieldDefinition> getDeclaredFieldsInternal() {
     return this._declaredFields;
   }
   
   protected final Collection<MethodDefinition> getDeclaredMethodsInternal() {
     return this._declaredMethods;
   }
   
   protected final Collection<TypeReference> getExplicitInterfacesInternal() {
     return this._explicitInterfaces;
   }
   
   protected final Collection<CustomAnnotation> getAnnotationsInternal() {
     return this._customAnnotations;
   }
   
   protected final Collection<SourceAttribute> getSourceAttributesInternal() {
     return this._sourceAttributes;
   }
   
   public TypeDefinition resolve()
   {
     return this;
   }
   
 
   public final long getFlags()
   {
     return this._flags;
   }
   
   protected final void setFlags(long flags) {
     this._flags = flags;
   }
   
   public final int getModifiers() {
     return Flags.toModifiers(getFlags());
   }
   
   public final boolean isFinal() {
     return Flags.testAny(getFlags(), 16L);
   }
   
   public final boolean isNonPublic() {
     return !Flags.testAny(getFlags(), 1L);
   }
   
   public final boolean isPrivate() {
     return Flags.testAny(getFlags(), 2L);
   }
   
   public final boolean isProtected() {
     return Flags.testAny(getFlags(), 4L);
   }
   
   public final boolean isPublic() {
     return Flags.testAny(getFlags(), 1L);
   }
   
   public final boolean isStatic() {
     return Flags.testAny(getFlags(), 8L);
   }
   
   public final boolean isSynthetic() {
     return Flags.testAny(getFlags(), 4096L);
   }
   
   public final boolean isDeprecated() {
     return Flags.testAny(getFlags(), 131072L);
   }
   
   public final boolean isPackagePrivate() {
     return !Flags.testAny(getFlags(), 7L);
   }
   
   public JvmType getSimpleType() {
     return JvmType.Object;
   }
   
   public final boolean isAnnotation() {
     return (isInterface()) && (Flags.testAny(getFlags(), 8192L));
   }
   
   public final boolean isClass()
   {
     return (!isPrimitive()) && (!isInterface()) && (!isEnum());
   }
   
   public final boolean isInterface() {
     return Flags.testAny(getFlags(), 512L);
   }
   
   public final boolean isEnum() {
     return Flags.testAny(getFlags(), 16384L);
   }
   
   public final boolean isAnonymous() {
     return Flags.testAny(getFlags(), 17592186044416L);
   }
   
   public final boolean isInnerClass() {
     return getDeclaringType() != null;
   }
   
   public final boolean isLocalClass() {
     return getDeclaringMethod() != null;
   }
   
   public boolean isNested() {
     return (isInnerClass()) || (isLocalClass());
   }
   
   public boolean isArray() {
     return getSimpleType() == JvmType.Array;
   }
   
   public boolean isPrimitive() {
     return false;
   }
   
   public final boolean isDefinition()
   {
     return true;
   }
   
 
 
 
   public final List<FieldDefinition> getDeclaredFields()
   {
     return this._declaredFieldsView;
   }
   
   public final List<MethodDefinition> getDeclaredMethods() {
     return this._declaredMethodsView;
   }
   
   public final List<TypeDefinition> getDeclaredTypes() {
     return this._declaredTypesView;
   }
   
 
 
 
 
   public boolean isCompoundType()
   {
     return Flags.testAny(getFlags(), 16777216L);
   }
   
   protected StringBuilder appendDescription(StringBuilder sb)
   {
     for (Modifier modifier : Flags.asModifierSet(getModifiers() & 0xFF7F)) {
       sb.append(modifier.toString());
       sb.append(' ');
     }
     
     if (isEnum()) {
       sb.append("enum ");
     }
     else if (isInterface()) {
       sb.append("interface ");
       
       if (isAnnotation()) {
         sb.append('@');
       }
     }
     else {
       sb.append("class ");
     }
     
     StringBuilder s = super.appendDescription(sb);
     
     TypeReference baseType = getBaseType();
     
     if (baseType != null) {
       s.append(" extends ");
       s = baseType.appendBriefDescription(s);
     }
     
     List<TypeReference> interfaces = getExplicitInterfaces();
     int interfaceCount = interfaces.size();
     
     if (interfaceCount > 0) {
       s.append(" implements ");
       for (int i = 0; i < interfaceCount; i++) {
         if (i != 0) {
           s.append(",");
         }
         s = ((TypeReference)interfaces.get(i)).appendBriefDescription(s);
       }
     }
     
     return s;
   }
   
   protected StringBuilder appendGenericSignature(StringBuilder sb)
   {
     StringBuilder s = super.appendGenericSignature(sb);
     
     TypeReference baseType = getBaseType();
     List<TypeReference> interfaces = getExplicitInterfaces();
     
     if (baseType == null) {
       if (interfaces.isEmpty()) {
         s = BuiltinTypes.Object.appendSignature(s);
       }
     }
     else {
       s = baseType.appendSignature(s);
     }
     
     for (TypeReference interfaceType : interfaces) {
       s = interfaceType.appendSignature(s);
     }
     
     return s;
   }
 }


