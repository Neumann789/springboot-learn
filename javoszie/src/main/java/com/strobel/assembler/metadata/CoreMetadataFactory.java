 package com.strobel.assembler.metadata;
 
 import com.strobel.assembler.ir.attributes.InnerClassEntry;
 import com.strobel.assembler.ir.attributes.InnerClassesAttribute;
 import com.strobel.assembler.ir.attributes.SourceAttribute;
 import com.strobel.assembler.metadata.signatures.BottomSignature;
 import com.strobel.assembler.metadata.signatures.FieldTypeSignature;
 import com.strobel.assembler.metadata.signatures.MetadataFactory;
 import com.strobel.assembler.metadata.signatures.Reifier;
 import com.strobel.assembler.metadata.signatures.SimpleClassTypeSignature;
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import java.util.List;
 import java.util.Stack;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class CoreMetadataFactory
   implements MetadataFactory
 {
   private final TypeDefinition _owner;
   private final IMetadataResolver _resolver;
   private final IGenericContext _scope;
   private final Stack<GenericParameter> _tempScope;
   
   private CoreMetadataFactory(TypeDefinition owner, IMetadataResolver resolver, IGenericContext scope)
   {
     this._owner = owner;
     this._resolver = resolver;
     this._scope = scope;
     this._tempScope = new Stack();
   }
   
   public static CoreMetadataFactory make(TypeDefinition owner, IGenericContext scope) {
     return new CoreMetadataFactory((TypeDefinition)VerifyArgument.notNull(owner, "owner"), owner.getResolver(), scope);
   }
   
   public static CoreMetadataFactory make(IMetadataResolver resolver, IGenericContext scope) {
     return new CoreMetadataFactory(null, resolver, scope);
   }
   
   private IGenericContext getScope() {
     return this._scope;
   }
   
   public GenericParameter makeTypeVariable(String name, FieldTypeSignature[] bounds) {
     GenericParameter genericParameter = new GenericParameter(name);
     
     if (ArrayUtilities.isNullOrEmpty(bounds)) {
       return genericParameter;
     }
     
     this._tempScope.push(genericParameter);
     try
     {
       TypeReference extendsBound = makeTypeBound(bounds);
       genericParameter.setExtendsBound(extendsBound);
       return genericParameter;
     }
     finally {
       this._tempScope.pop();
     }
   }
   
 
 
   public WildcardType makeWildcard(FieldTypeSignature superBound, FieldTypeSignature extendsBound)
   {
     if ((superBound == null) || (superBound == BottomSignature.make())) {
       if ((extendsBound == null) || (((extendsBound instanceof SimpleClassTypeSignature)) && (StringUtilities.equals("java.lang.Object", ((SimpleClassTypeSignature)extendsBound).getName()))))
       {
 
 
         return WildcardType.unbounded();
       }
       
       return WildcardType.makeExtends(makeTypeBound(new FieldTypeSignature[] { extendsBound }));
     }
     
     return WildcardType.makeSuper(makeTypeBound(new FieldTypeSignature[] { superBound }));
   }
   
 
 
 
   protected TypeReference makeTypeBound(FieldTypeSignature... bounds)
   {
     Reifier reifier = null;
     
     if (ArrayUtilities.isNullOrEmpty(bounds)) {
       return null;
     }
     TypeReference baseType;
     if (bounds[0] != BottomSignature.make()) {
       reifier = Reifier.make(this);
       bounds[0].accept(reifier);
       baseType = reifier.getResult();
       assert baseType!=null;
     }
     else {
       baseType = null;
     }
     
     if (bounds.length == 1) {
       return baseType;
     }
     
     if (reifier == null) {
       reifier = Reifier.make(this);
     }
     
     if ((bounds.length == 2) && (baseType == null)) {
       bounds[1].accept(reifier);
       TypeReference singleInterface = reifier.getResult();
       assert (singleInterface != null);
       return singleInterface;
     }
     
     TypeReference[] it = new TypeReference[bounds.length - 1];
     
     for (int i = 0; i < it.length; i++) {
       bounds[(i + 1)].accept(reifier);
       it[i] = reifier.getResult();
       assert (it[i] != null);
     }
     
     List<TypeReference> interfaceTypes = ArrayUtilities.asUnmodifiableList(it);
     
     return new CompoundTypeReference(baseType, interfaceTypes);
   }
   
 
 
 
   public TypeReference makeParameterizedType(TypeReference declaration, TypeReference owner, TypeReference... typeArguments)
   {
     if (typeArguments.length == 0) {
       return declaration;
     }
     return declaration.makeGenericType(typeArguments);
   }
   
   public GenericParameter findTypeVariable(String name) {
     for (int i = this._tempScope.size() - 1; i >= 0; i--) {
       GenericParameter genericParameter = (GenericParameter)this._tempScope.get(i);
       
       if ((genericParameter != null) && (StringUtilities.equals(genericParameter.getName(), name))) {
         return genericParameter;
       }
     }
     
     IGenericContext scope = getScope();
     
     if (scope != null) {
       return scope.findTypeVariable(name);
     }
     
     return null;
   }
   
   private InnerClassEntry findInnerClassEntry(String name) {
     if (this._owner == null) {
       return null;
     }
     
     String internalName = name.replace('.', '/');
     SourceAttribute attribute = SourceAttribute.find("InnerClasses", this._owner.getSourceAttributes());
     
     if ((attribute instanceof InnerClassesAttribute)) {
       List<InnerClassEntry> entries = ((InnerClassesAttribute)attribute).getEntries();
       
       for (InnerClassEntry entry : entries) {
         if (StringUtilities.equals(entry.getInnerClassName(), internalName)) {
           return entry;
         }
       }
     }
     
     return null;
   }
   
   public TypeReference makeNamedType(String name) {
     int length = name.length();
     
     InnerClassEntry entry = findInnerClassEntry(name);
     
     if (entry != null) {
       String innerClassName = entry.getInnerClassName();
       int packageEnd = innerClassName.lastIndexOf('/');
       String shortName = StringUtilities.isNullOrEmpty(entry.getShortName()) ? null : entry.getShortName();
       TypeReference declaringType;
       if (!StringUtilities.isNullOrEmpty(entry.getOuterClassName())) {
         declaringType = makeNamedType(entry.getOuterClassName().replace('/', '.'));
       }
       else {
         int lastDollarIndex = name.lastIndexOf('$');
         
 
         while ((lastDollarIndex >= 1) && (lastDollarIndex < length) && (name.charAt(lastDollarIndex - 1) == '$'))
         {
 
           if (lastDollarIndex > 1) {
             lastDollarIndex = name.lastIndexOf(lastDollarIndex, lastDollarIndex - 2);
           }
           else {
             lastDollarIndex = -1;
           }
         }
         
         if (lastDollarIndex == length - 1) {
           lastDollarIndex = -1;
         }
         
         declaringType = makeNamedType(name.substring(0, lastDollarIndex).replace('/', '.'));
       }
       
       return new UnresolvedType(declaringType, packageEnd < 0 ? innerClassName : innerClassName.substring(packageEnd + 1), shortName);
     }
     
 
 
 
 
     int packageEnd = name.lastIndexOf('.');
     
     if (packageEnd < 0) {
       return new UnresolvedType("", name, null);
     }
     
     return new CoreMetadataFactory.UnresolvedType(packageEnd < 0 ? "" : name.substring(0, packageEnd), packageEnd < 0 ? name : name.substring(packageEnd + 1), null);
   }
   
 
 
 
   public TypeReference makeArrayType(TypeReference componentType)
   {
     return componentType.makeArrayType();
   }
   
   public TypeReference makeByte() {
     return BuiltinTypes.Byte;
   }
   
   public TypeReference makeBoolean() {
     return BuiltinTypes.Boolean;
   }
   
   public TypeReference makeShort() {
     return BuiltinTypes.Short;
   }
   
   public TypeReference makeChar() {
     return BuiltinTypes.Character;
   }
   
   public TypeReference makeInt() {
     return BuiltinTypes.Integer;
   }
   
   public TypeReference makeLong() {
     return BuiltinTypes.Long;
   }
   
   public TypeReference makeFloat() {
     return BuiltinTypes.Float;
   }
   
   public TypeReference makeDouble() {
     return BuiltinTypes.Double;
   }
   
   public TypeReference makeVoid() {
     return BuiltinTypes.Void;
   }
   
 
 
 
 
 
   public IMethodSignature makeMethodSignature(TypeReference returnType, List<TypeReference> parameterTypes, List<GenericParameter> genericParameters, List<TypeReference> thrownTypes)
   {
     return new MethodSignature(parameterTypes, returnType, genericParameters, thrownTypes);
   }
   
 
 
 
 
 
 
 
 
 
   public IClassSignature makeClassSignature(TypeReference baseType, List<TypeReference> interfaceTypes, List<GenericParameter> genericParameters)
   {
     return new ClassSignature(baseType, interfaceTypes, genericParameters);
   }
   
 
   private static final class ClassSignature
     implements IClassSignature
   {
     private final TypeReference _baseType;
     private final List<TypeReference> _interfaceTypes;
     private final List<GenericParameter> _genericParameters;
     
     private ClassSignature(TypeReference baseType, List<TypeReference> interfaceTypes, List<GenericParameter> genericParameters)
     {
       this._baseType = ((TypeReference)VerifyArgument.notNull(baseType, "baseType"));
       this._interfaceTypes = ((List)VerifyArgument.noNullElements(interfaceTypes, "interfaceTypes"));
       this._genericParameters = ((List)VerifyArgument.noNullElements(genericParameters, "genericParameters"));
     }
     
     public TypeReference getBaseType()
     {
       return this._baseType;
     }
     
     public List<TypeReference> getExplicitInterfaces()
     {
       return this._interfaceTypes;
     }
     
     public boolean hasGenericParameters()
     {
       return !this._genericParameters.isEmpty();
     }
     
     public boolean isGenericDefinition()
     {
       return false;
     }
     
     public List<GenericParameter> getGenericParameters()
     {
       return this._genericParameters;
     }
   }
   
 
   private static final class MethodSignature
     implements IMethodSignature
   {
     private final List<ParameterDefinition> _parameters;
     
     private final TypeReference _returnType;
     private final List<GenericParameter> _genericParameters;
     private final List<TypeReference> _thrownTypes;
     
     MethodSignature(List<TypeReference> parameterTypes, TypeReference returnType, List<GenericParameter> genericParameters, List<TypeReference> thrownTypes)
     {
       VerifyArgument.notNull(parameterTypes, "parameterTypes");
       VerifyArgument.notNull(returnType, "returnType");
       VerifyArgument.notNull(genericParameters, "genericParameters");
       VerifyArgument.notNull(thrownTypes, "thrownTypes");
       
       ParameterDefinition[] parameters = new ParameterDefinition[parameterTypes.size()];
       
       int i = 0;int slot = 0; for (int n = parameters.length; i < n; slot++) {
         TypeReference parameterType = (TypeReference)parameterTypes.get(i);
         
         parameters[i] = new ParameterDefinition(slot, parameterType);
         
         if (parameterType.getSimpleType().isDoubleWord()) {
           slot++;
         }
         i++;
       }
       
 
 
 
 
 
 
 
       this._parameters = ArrayUtilities.asUnmodifiableList(parameters);
       this._returnType = returnType;
       this._genericParameters = genericParameters;
       this._thrownTypes = thrownTypes;
     }
     
     public boolean hasParameters()
     {
       return !this._parameters.isEmpty();
     }
     
     public List<ParameterDefinition> getParameters()
     {
       return this._parameters;
     }
     
     public TypeReference getReturnType()
     {
       return this._returnType;
     }
     
     public List<TypeReference> getThrownTypes()
     {
       return this._thrownTypes;
     }
     
     public boolean hasGenericParameters()
     {
       return !this._genericParameters.isEmpty();
     }
     
     public boolean isGenericDefinition()
     {
       return !this._genericParameters.isEmpty();
     }
     
     public List<GenericParameter> getGenericParameters()
     {
       return this._genericParameters;
     }
     
     public GenericParameter findTypeVariable(String name)
     {
       for (GenericParameter genericParameter : getGenericParameters()) {
         if (StringUtilities.equals(genericParameter.getName(), name)) {
           return genericParameter;
         }
       }
       
       return null;
     }
   }
   
   private final class UnresolvedType extends TypeReference
   {
     private final String _name;
     private final String _shortName;
     private final String _packageName;
     private final GenericParameterCollection _genericParameters;
     private String _fullName;
     private String _internalName;
     private String _signature;
     private String _erasedSignature;
     
     UnresolvedType(TypeReference declaringType, String name, String shortName) {
       this._name = ((String)VerifyArgument.notNull(name, "name"));
       this._shortName = shortName;
       setDeclaringType((TypeReference)VerifyArgument.notNull(declaringType, "declaringType"));
       this._packageName = declaringType.getPackageName();
       this._genericParameters = new GenericParameterCollection(this);
       this._genericParameters.freeze();
     }
     
     UnresolvedType(String packageName, String name, String shortName) {
       this._packageName = ((String)VerifyArgument.notNull(packageName, "packageName"));
       this._name = ((String)VerifyArgument.notNull(name, "name"));
       this._shortName = shortName;
       this._genericParameters = new GenericParameterCollection(this);
       this._genericParameters.freeze();
     }
     
/*     UnresolvedType(String declaringType, String name, List<GenericParameter> shortName) {
       this._name = ((String)VerifyArgument.notNull(name, "name"));
       this._shortName = shortName;
       setDeclaringType((TypeReference)VerifyArgument.notNull(declaringType, "declaringType"));
       this._packageName = declaringType.getPackageName();
       
       this._genericParameters = new GenericParameterCollection(this);
       
       for (GenericParameter genericParameter : genericParameters) {
         this._genericParameters.add(genericParameter);
       }
       
       this._genericParameters.freeze();
     }*/
     
/*     UnresolvedType(String packageName, String name, List<GenericParameter> shortName) {
       this._packageName = ((String)VerifyArgument.notNull(packageName, "packageName"));
       this._name = ((String)VerifyArgument.notNull(name, "name"));
       this._shortName = shortName;
       
       this._genericParameters = new GenericParameterCollection(this);
       
       for (GenericParameter genericParameter : genericParameters) {
         this._genericParameters.add(genericParameter);
       }
     }*/
     
     public String getName()
     {
       return this._name;
     }
     
     public String getPackageName()
     {
       return this._packageName;
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
     
     public String getSimpleName()
     {
       return this._shortName != null ? this._shortName : this._name;
     }
     
     public boolean isGenericDefinition()
     {
       return hasGenericParameters();
     }
     
     public List<GenericParameter> getGenericParameters()
     {
       return this._genericParameters;
     }
     
     public TypeReference makeGenericType(List<? extends TypeReference> typeArguments)
     {
       VerifyArgument.noNullElementsAndNotEmpty(typeArguments, "typeArguments");
       
       return new CoreMetadataFactory.UnresolvedGenericType(this, ArrayUtilities.asUnmodifiableList(typeArguments.toArray(new TypeReference[typeArguments.size()])));
     }
     
 
 
 
     public TypeReference makeGenericType(TypeReference... typeArguments)
     {
       VerifyArgument.noNullElementsAndNotEmpty(typeArguments, "typeArguments");
       
       return new CoreMetadataFactory.UnresolvedGenericType(this, ArrayUtilities.asUnmodifiableList((TypeReference[])typeArguments.clone()));
     }
     
 
 
 
     public TypeDefinition resolve()
     {
       return CoreMetadataFactory.this._resolver.resolve(this);
     }
     
     public FieldDefinition resolve(FieldReference field)
     {
       return CoreMetadataFactory.this._resolver.resolve(field);
     }
     
     public MethodDefinition resolve(MethodReference method)
     {
       return CoreMetadataFactory.this._resolver.resolve(method);
     }
     
     public TypeDefinition resolve(TypeReference type)
     {
       return CoreMetadataFactory.this._resolver.resolve(type);
     }
   }
   
   private final class UnresolvedGenericType extends TypeReference implements IGenericInstance
   {
     private final TypeReference _genericDefinition;
     private final List<TypeReference> _typeParameters;
     private String _signature;
     
     UnresolvedGenericType(TypeReference genericDefinition,List<TypeReference> typeParameters) {
       this._genericDefinition = genericDefinition;
       this._typeParameters = typeParameters;
     }
     
     public TypeReference getElementType()
     {
       return null;
     }
     
     public <R, P> R accept(TypeMetadataVisitor<P, R> visitor, P parameter)
     {
       return (R)visitor.visitParameterizedType(this, parameter);
     }
     
     public String getName()
     {
       return this._genericDefinition.getName();
     }
     
     public String getPackageName()
     {
       return this._genericDefinition.getPackageName();
     }
     
     public TypeReference getDeclaringType()
     {
       return this._genericDefinition.getDeclaringType();
     }
     
     public String getSimpleName()
     {
       return this._genericDefinition.getSimpleName();
     }
     
     public String getFullName()
     {
       return this._genericDefinition.getFullName();
     }
     
     public String getInternalName()
     {
       return this._genericDefinition.getInternalName();
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
       return this._genericDefinition.getErasedSignature();
     }
     
     public boolean isGenericDefinition()
     {
       return false;
     }
     
     public boolean isGenericType()
     {
       return true;
     }
     
     public List<GenericParameter> getGenericParameters()
     {
       if (!this._genericDefinition.isGenericDefinition()) {
         TypeDefinition resolvedDefinition = this._genericDefinition.resolve();
         
         if (resolvedDefinition != null) {
           return resolvedDefinition.getGenericParameters();
         }
       }
       
       return this._genericDefinition.getGenericParameters();
     }
     
     public boolean hasTypeArguments()
     {
       return true;
     }
     
     public List<TypeReference> getTypeArguments()
     {
       return this._typeParameters;
     }
     
     public IGenericParameterProvider getGenericDefinition()
     {
       return this._genericDefinition;
     }
     
     public TypeReference getUnderlyingType()
     {
       return this._genericDefinition;
     }
     
     public TypeDefinition resolve()
     {
       return CoreMetadataFactory.this._resolver.resolve(this);
     }
     
     public FieldDefinition resolve(FieldReference field)
     {
       return CoreMetadataFactory.this._resolver.resolve(field);
     }
     
     public MethodDefinition resolve(MethodReference method)
     {
       return CoreMetadataFactory.this._resolver.resolve(method);
     }
     
     public TypeDefinition resolve(TypeReference type)
     {
       return CoreMetadataFactory.this._resolver.resolve(type);
     }
   }
 }


