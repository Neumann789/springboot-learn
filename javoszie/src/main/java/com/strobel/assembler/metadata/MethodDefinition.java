 package com.strobel.assembler.metadata;
 
 import com.strobel.assembler.Collection;
 import com.strobel.assembler.ir.ConstantPool;
 import com.strobel.assembler.ir.attributes.CodeAttribute;
 import com.strobel.assembler.ir.attributes.ExceptionTableEntry;
 import com.strobel.assembler.ir.attributes.SourceAttribute;
 import com.strobel.assembler.metadata.annotations.CustomAnnotation;
 import com.strobel.core.HashUtilities;
 import com.strobel.core.StringUtilities;
 import java.lang.ref.SoftReference;
 import java.util.Collections;
 import java.util.List;
 import javax.lang.model.element.Modifier;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class MethodDefinition
   extends MethodReference
   implements IMemberDefinition
 {
   private final GenericParameterCollection _genericParameters;
   private final ParameterDefinitionCollection _parameters;
   private final AnonymousLocalTypeCollection _declaredTypes;
   private final Collection<TypeReference> _thrownTypes;
   private final Collection<CustomAnnotation> _customAnnotations;
   private final Collection<SourceAttribute> _sourceAttributes;
   private final List<GenericParameter> _genericParametersView;
   private final List<TypeDefinition> _declaredTypesView;
   private final List<ParameterDefinition> _parametersView;
   private final List<TypeReference> _thrownTypesView;
   private final List<CustomAnnotation> _customAnnotationsView;
   private final List<SourceAttribute> _sourceAttributesView;
   private SoftReference<MethodBody> _body;
   private String _name;
   private String _fullName;
   private String _erasedSignature;
   private String _signature;
   private TypeReference _returnType;
   private TypeDefinition _declaringType;
   private long _flags;
   
   protected MethodDefinition()
   {
     this._genericParameters = new GenericParameterCollection(this);
     this._parameters = new ParameterDefinitionCollection(this);
     this._declaredTypes = new AnonymousLocalTypeCollection(this);
     this._thrownTypes = new Collection();
     this._customAnnotations = new Collection();
     this._sourceAttributes = new Collection();
     this._genericParametersView = Collections.unmodifiableList(this._genericParameters);
     this._parametersView = Collections.unmodifiableList(this._parameters);
     this._declaredTypesView = Collections.unmodifiableList(this._declaredTypes);
     this._thrownTypesView = Collections.unmodifiableList(this._thrownTypes);
     this._customAnnotationsView = Collections.unmodifiableList(this._customAnnotations);
     this._sourceAttributesView = Collections.unmodifiableList(this._sourceAttributes);
   }
   
   public final boolean hasBody() {
     SoftReference<MethodBody> bodyCache = this._body;
     return (bodyCache != null) && (bodyCache.get() != null);
   }
   
   public final MethodBody getBody()
   {
     SoftReference<MethodBody> cachedBody = this._body;
     MethodBody body;
     if ((cachedBody == null) || ((body = (MethodBody)this._body.get()) == null)) {
       return tryLoadBody();
     }
     MethodBody body;
     return body;
   }
   
   public final boolean hasThis() {
     return !isStatic();
   }
   
   protected final void setBody(MethodBody body) {
     this._body = new SoftReference(body);
   }
   
   public final boolean isDefinition()
   {
     return true;
   }
   
   public final boolean isAnonymousClassConstructor() {
     return Flags.testAny(this._flags, 536870912L);
   }
   
   public final List<TypeDefinition> getDeclaredTypes() {
     return this._declaredTypesView;
   }
   
   protected final AnonymousLocalTypeCollection getDeclaredTypesInternal() {
     return this._declaredTypes;
   }
   
   public final List<GenericParameter> getGenericParameters()
   {
     return this._genericParametersView;
   }
   
   public final List<TypeReference> getThrownTypes()
   {
     return this._thrownTypesView;
   }
   
   public final TypeDefinition getDeclaringType()
   {
     return this._declaringType;
   }
   
   public final List<CustomAnnotation> getAnnotations()
   {
     return this._customAnnotationsView;
   }
   
   public final List<SourceAttribute> getSourceAttributes() {
     return this._sourceAttributesView;
   }
   
   public final String getName()
   {
     return this._name;
   }
   
   public String getFullName()
   {
     if (this._fullName == null) {
       this._fullName = super.getFullName();
     }
     return this._fullName;
   }
   
   public String getSignature()
   {
     if (this._signature == null) {
       this._signature = super.getSignature();
     }
     return this._signature;
   }
   
   public String getErasedSignature()
   {
     if (this._erasedSignature == null) {
       this._erasedSignature = super.getErasedSignature();
     }
     return this._erasedSignature;
   }
   
   public final TypeReference getReturnType()
   {
     return this._returnType;
   }
   
   public final List<ParameterDefinition> getParameters()
   {
     return this._parametersView;
   }
   
   protected final void setName(String name) {
     this._name = name;
   }
   
   protected final void setReturnType(TypeReference returnType) {
     this._returnType = returnType;
   }
   
   protected final void setDeclaringType(TypeDefinition declaringType) {
     this._declaringType = declaringType;
     this._parameters.setDeclaringType(declaringType);
   }
   
   protected final void setFlags(long flags) {
     this._flags = flags;
   }
   
   protected final GenericParameterCollection getGenericParametersInternal() {
     return this._genericParameters;
   }
   
   protected final ParameterDefinitionCollection getParametersInternal() {
     return this._parameters;
   }
   
   protected final Collection<TypeReference> getThrownTypesInternal() {
     return this._thrownTypes;
   }
   
   protected final Collection<CustomAnnotation> getAnnotationsInternal() {
     return this._customAnnotations;
   }
   
   protected final Collection<SourceAttribute> getSourceAttributesInternal() {
     return this._sourceAttributes;
   }
   
   public int hashCode()
   {
     return HashUtilities.hashCode(getFullName());
   }
   
   public boolean equals(Object obj)
   {
     if ((obj instanceof MethodDefinition)) {
       MethodDefinition other = (MethodDefinition)obj;
       
       return (StringUtilities.equals(getName(), other.getName())) && (StringUtilities.equals(getErasedSignature(), other.getErasedSignature())) && (typeNamesMatch(getDeclaringType(), other.getDeclaringType()));
     }
     
 
 
     return false;
   }
   
   private boolean typeNamesMatch(TypeReference t1, TypeReference t2) {
     return (t1 != null) && (t2 != null) && (StringUtilities.equals(t1.getFullName(), t2.getFullName()));
   }
   
 
 
 
   public final boolean isAbstract()
   {
     return Flags.testAny(getFlags(), 1024L);
   }
   
   public final boolean isDefault() {
     return Flags.testAny(getFlags(), 8796093022208L);
   }
   
   public final boolean isBridgeMethod() {
     return Flags.testAny(getFlags(), 2147483712L);
   }
   
   public final boolean isVarArgs() {
     return Flags.testAny(getFlags(), 17179869312L);
   }
   
 
 
 
 
   public final long getFlags()
   {
     return this._flags;
   }
   
   public final int getModifiers()
   {
     return Flags.toModifiers(getFlags());
   }
   
   public final boolean isFinal()
   {
     return Flags.testAny(getFlags(), 16L);
   }
   
   public final boolean isNonPublic()
   {
     return !Flags.testAny(getFlags(), 1L);
   }
   
   public final boolean isPrivate()
   {
     return Flags.testAny(getFlags(), 2L);
   }
   
   public final boolean isProtected()
   {
     return Flags.testAny(getFlags(), 4L);
   }
   
   public final boolean isPublic()
   {
     return Flags.testAny(getFlags(), 1L);
   }
   
   public final boolean isStatic()
   {
     return Flags.testAny(getFlags(), 8L);
   }
   
   public final boolean isSynthetic()
   {
     return Flags.testAny(getFlags(), 4096L);
   }
   
   public final boolean isDeprecated()
   {
     return Flags.testAny(getFlags(), 131072L);
   }
   
   public final boolean isPackagePrivate()
   {
     return !Flags.testAny(getFlags(), 7L);
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
     if (fullName) {
       TypeDefinition declaringType = getDeclaringType();
       
       if (declaringType != null) {
         return declaringType.appendName(sb, true, false).append('.').append(getName());
       }
     }
     
     return sb.append(this._name);
   }
   
   public StringBuilder appendDescription(StringBuilder sb)
   {
     StringBuilder s = sb;
     
     for (Modifier modifier : Flags.asModifierSet(getModifiers() & 0xFF7F)) {
       s.append(modifier.toString());
       s.append(' ');
     }
     
     List<? extends TypeReference> typeArguments;
     List<? extends TypeReference> typeArguments;
     if ((this instanceof IGenericInstance)) {
       typeArguments = ((IGenericInstance)this).getTypeArguments();
     } else { List<? extends TypeReference> typeArguments;
       if (hasGenericParameters()) {
         typeArguments = getGenericParameters();
       }
       else {
         typeArguments = Collections.emptyList();
       }
     }
     if (!typeArguments.isEmpty()) {
       int count = typeArguments.size();
       
       s.append('<');
       
       for (int i = 0; i < count; i++) {
         if (i != 0) {
           s.append(", ");
         }
         s = ((TypeReference)typeArguments.get(i)).appendSimpleDescription(s);
       }
       
       s.append('>');
       s.append(' ');
     }
     
     TypeReference returnType = getReturnType();
     
     while (returnType.isWildcardType()) {
       returnType = returnType.getExtendsBound();
     }
     
     if (returnType.isGenericParameter()) {
       s.append(returnType.getName());
     }
     else {
       s = returnType.appendSimpleDescription(s);
     }
     
     s.append(' ');
     s.append(getName());
     s.append('(');
     
     List<ParameterDefinition> parameters = getParameters();
     
     int i = 0; for (int n = parameters.size(); i < n; i++) {
       ParameterDefinition p = (ParameterDefinition)parameters.get(i);
       
       if (i != 0) {
         s.append(", ");
       }
       
       TypeReference parameterType = p.getParameterType();
       
       while (parameterType.isWildcardType()) {
         parameterType = parameterType.getExtendsBound();
       }
       
       if (parameterType.isGenericParameter()) {
         s.append(parameterType.getName());
       }
       else {
         s = parameterType.appendSimpleDescription(s);
       }
       
       s.append(" ").append(p.getName());
     }
     
     s.append(')');
     
     List<TypeReference> thrownTypes = getThrownTypes();
     
     if (!thrownTypes.isEmpty()) {
       s.append(" throws ");
       
       int i = 0; for (int n = thrownTypes.size(); i < n; i++) {
         TypeReference t = (TypeReference)thrownTypes.get(i);
         if (i != 0) {
           s.append(", ");
         }
         s = t.appendBriefDescription(s);
       }
     }
     
     return s;
   }
   
   public StringBuilder appendSimpleDescription(StringBuilder sb) {
     StringBuilder s = sb;
     
     for (Modifier modifier : Flags.asModifierSet(getModifiers() & 0xFF7F)) {
       s.append(modifier.toString());
       s.append(' ');
     }
     
     List<? extends TypeReference> typeArguments;
     List<? extends TypeReference> typeArguments;
     if ((this instanceof IGenericInstance)) {
       typeArguments = ((IGenericInstance)this).getTypeArguments();
     } else { List<? extends TypeReference> typeArguments;
       if (hasGenericParameters()) {
         typeArguments = getGenericParameters();
       }
       else {
         typeArguments = Collections.emptyList();
       }
     }
     if (!typeArguments.isEmpty()) {
       s.append('<');
       int i = 0; for (int n = typeArguments.size(); i < n; i++) {
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
       s.append(' ');
     }
     
     TypeReference returnType = getReturnType();
     
     while (returnType.isWildcardType()) {
       returnType = returnType.getExtendsBound();
     }
     
     if (returnType.isGenericParameter()) {
       s.append(returnType.getName());
     }
     else {
       s = returnType.appendSimpleDescription(s);
     }
     
     s.append(' ');
     s.append(getName());
     s.append('(');
     
     List<ParameterDefinition> parameters = getParameters();
     
     int i = 0; for (int n = parameters.size(); i < n; i++) {
       ParameterDefinition p = (ParameterDefinition)parameters.get(i);
       
       if (i != 0) {
         s.append(", ");
       }
       
       TypeReference parameterType = p.getParameterType();
       
       while (parameterType.isWildcardType()) {
         parameterType = parameterType.getExtendsBound();
       }
       
       if (parameterType.isGenericParameter()) {
         s.append(parameterType.getName());
       }
       else {
         s = parameterType.appendSimpleDescription(s);
       }
     }
     
     s.append(')');
     
     List<TypeReference> thrownTypes = getThrownTypes();
     
     if (!thrownTypes.isEmpty()) {
       s.append(" throws ");
       
       int i = 0; for (int n = thrownTypes.size(); i < n; i++) {
         TypeReference t = (TypeReference)thrownTypes.get(i);
         if (i != 0) {
           s.append(", ");
         }
         s = t.appendSimpleDescription(s);
       }
     }
     
     return s;
   }
   
   public StringBuilder appendBriefDescription(StringBuilder sb) {
     StringBuilder s = sb;
     
     TypeReference returnType = getReturnType();
     
     while (returnType.isWildcardType()) {
       returnType = returnType.getExtendsBound();
     }
     
     if (returnType.isGenericParameter()) {
       s.append(returnType.getName());
     }
     else {
       s = returnType.appendBriefDescription(s);
     }
     
     s.append(' ');
     s.append(getName());
     s.append('(');
     
     List<ParameterDefinition> parameters = getParameters();
     
     int i = 0; for (int n = parameters.size(); i < n; i++) {
       ParameterDefinition p = (ParameterDefinition)parameters.get(i);
       
       if (i != 0) {
         s.append(", ");
       }
       
       TypeReference parameterType = p.getParameterType();
       
       while (parameterType.isWildcardType()) {
         parameterType = parameterType.getExtendsBound();
       }
       
       if (parameterType.isGenericParameter()) {
         s.append(parameterType.getName());
       }
       else {
         s = parameterType.appendBriefDescription(s);
       }
     }
     
     s.append(')');
     
     return s;
   }
   
   public StringBuilder appendErasedDescription(StringBuilder sb) {
     if ((hasGenericParameters()) && (!isGenericDefinition())) {
       MethodDefinition definition = resolve();
       if (definition != null) {
         return definition.appendErasedDescription(sb);
       }
     }
     
     for (Modifier modifier : Flags.asModifierSet(getModifiers() & 0xFF7F)) {
       sb.append(modifier.toString());
       sb.append(' ');
     }
     
     List<ParameterDefinition> parameterTypes = getParameters();
     
     StringBuilder s = getReturnType().appendErasedDescription(sb);
     
     s.append(' ');
     s.append(getName());
     s.append('(');
     
     int i = 0; for (int n = parameterTypes.size(); i < n; i++) {
       if (i != 0) {
         s.append(", ");
       }
       s = ((ParameterDefinition)parameterTypes.get(i)).getParameterType().appendErasedDescription(s);
     }
     
     s.append(')');
     return s;
   }
   
   public String toString()
   {
     return getSimpleDescription();
   }
   
 
 
 
   private MethodBody tryLoadBody()
   {
     if (Flags.testAny(this._flags, 70368744177664L)) {
       return null;
     }
     
     CodeAttribute codeAttribute = (CodeAttribute)SourceAttribute.find("Code", this._sourceAttributes);
     
     if (codeAttribute == null) {
       return null;
     }
     
     int codeAttributeIndex = this._sourceAttributes.indexOf(codeAttribute);
     
     Buffer code = codeAttribute.getCode();
     ConstantPool constantPool = this._declaringType.getConstantPool();
     
     if (code == null) {
       ITypeLoader typeLoader = this._declaringType.getTypeLoader();
       
       if (typeLoader == null) {
         this._flags |= 0x400000000000;
         return null;
       }
       
       code = new Buffer();
       
       if (!typeLoader.tryLoadType(this._declaringType.getInternalName(), code)) {
         this._flags |= 0x400000000000;
         return null;
       }
       
       List<ExceptionTableEntry> exceptionTableEntries = codeAttribute.getExceptionTableEntries();
       List<SourceAttribute> codeAttributes = codeAttribute.getAttributes();
       
       CodeAttribute newCode = new CodeAttribute(codeAttribute.getLength(), codeAttribute.getMaxStack(), codeAttribute.getMaxLocals(), codeAttribute.getCodeOffset(), codeAttribute.getCodeSize(), code, (ExceptionTableEntry[])exceptionTableEntries.toArray(new ExceptionTableEntry[exceptionTableEntries.size()]), (SourceAttribute[])codeAttributes.toArray(new SourceAttribute[codeAttributes.size()]));
       
 
 
 
 
 
 
 
 
 
       if (constantPool == null) {
         long magic = code.readInt() & 0xFFFFFFFF;
         
         if (magic != 3405691582L) {
           throw new IllegalStateException(String.format("Could not load method body for '%s:%s'; wrong magic number in class header: 0x%8X.", new Object[] { getFullName(), getSignature(), Long.valueOf(magic) }));
         }
         
 
 
 
 
 
 
 
         code.readUnsignedShort();
         code.readUnsignedShort();
         
         constantPool = ConstantPool.read(code);
       }
       
       this._sourceAttributes.set(codeAttributeIndex, newCode);
     }
     
     MetadataParser parser = new MetadataParser(this._declaringType);
     IMetadataScope scope = new ClassFileReader.Scope(parser, this._declaringType, constantPool);
     
     MethodBody body = new MethodReader(this, scope).readBody();
     
     this._body = new SoftReference(body);
     this._sourceAttributes.set(codeAttributeIndex, codeAttribute);
     
     body.tryFreeze();
     
     return body;
   }
 }


