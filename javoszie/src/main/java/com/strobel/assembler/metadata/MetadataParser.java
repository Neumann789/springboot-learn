 package com.strobel.assembler.metadata;
 
 import com.strobel.assembler.metadata.signatures.ClassSignature;
 import com.strobel.assembler.metadata.signatures.ClassTypeSignature;
 import com.strobel.assembler.metadata.signatures.FieldTypeSignature;
 import com.strobel.assembler.metadata.signatures.FormalTypeParameter;
 import com.strobel.assembler.metadata.signatures.MethodTypeSignature;
 import com.strobel.assembler.metadata.signatures.Reifier;
 import com.strobel.assembler.metadata.signatures.ReturnType;
 import com.strobel.assembler.metadata.signatures.SignatureParser;
 import com.strobel.assembler.metadata.signatures.TypeSignature;
 import com.strobel.compilerservices.RuntimeHelpers;
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.SafeCloseable;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.util.EmptyArrayCache;
 import java.util.Collections;
 import java.util.List;
 import java.util.Stack;
 import java.util.concurrent.atomic.AtomicInteger;
 
 
 
 
 
 
 
 
 
 
 
 public final class MetadataParser
 {
   private final IMetadataResolver _resolver;
   private final SignatureParser _signatureParser;
   private final Stack<IGenericContext> _genericContexts;
   private final CoreMetadataFactory _factory;
   private final AtomicInteger _suppressResolveDepth;
   
   public MetadataParser()
   {
     this(MetadataSystem.instance());
   }
   
   public MetadataParser(IMetadataResolver resolver) {
     this._resolver = ((IMetadataResolver)VerifyArgument.notNull(resolver, "resolver"));
     this._signatureParser = SignatureParser.make();
     this._genericContexts = new Stack();
     this._factory = CoreMetadataFactory.make(resolver, new StackBasedGenericContext());
     this._suppressResolveDepth = new AtomicInteger();
   }
   
   public MetadataParser(TypeDefinition owner) {
     VerifyArgument.notNull(owner, "owner");
     
     this._resolver = (owner.getResolver() != null ? owner.getResolver() : MetadataSystem.instance());
     this._signatureParser = SignatureParser.make();
     this._genericContexts = new Stack();
     this._factory = CoreMetadataFactory.make(owner, new StackBasedGenericContext());
     this._suppressResolveDepth = new AtomicInteger();
   }
   
   public final SafeCloseable suppressTypeResolution() {
     this._suppressResolveDepth.incrementAndGet();
     
    return  new SafeCloseable()
     {
       public void close() { MetadataParser.this._suppressResolveDepth.decrementAndGet(); }
     };
   }
   
   private final class StackBasedGenericContext implements IGenericContext {
     private StackBasedGenericContext() {}
     
     public GenericParameter findTypeVariable(String name) {
       for (int i = MetadataParser.this._genericContexts.size() - 1; i >= 0; i--) {
         IGenericContext context = (IGenericContext)MetadataParser.this._genericContexts.get(i);
         GenericParameter typeVariable = context.findTypeVariable(name);
         
         if (typeVariable != null) {
           return typeVariable;
         }
       }
       
       if ((MetadataParser.this._resolver instanceof IGenericContext)) {
         return ((IGenericContext)MetadataParser.this._resolver).findTypeVariable(name);
       }
       
       return null;
     }
   }
   
   public final IMetadataResolver getResolver() {
     return this._resolver;
   }
   
   public void pushGenericContext(IGenericContext context) {
     this._genericContexts.push(VerifyArgument.notNull(context, "context"));
   }
   
   public void popGenericContext() {
     this._genericContexts.pop();
   }
   
   public TypeReference parseTypeDescriptor(String descriptor) {
     VerifyArgument.notNull(descriptor, "descriptor");
     
     if (descriptor.startsWith("[")) {
       return parseTypeSignature(descriptor);
     }
     
     return parseTypeSignature("L" + descriptor + ";");
   }
   
   public TypeReference parseTypeSignature(String signature) {
     VerifyArgument.notNull(signature, "signature");
     
     TypeSignature typeSignature = this._signatureParser.parseTypeSignature(signature);
     Reifier reifier = Reifier.make(this._factory);
     
     typeSignature.accept(reifier);
     
     return reifier.getResult();
   }
   
   public FieldReference parseField(TypeReference declaringType, String name, String signature) {
     VerifyArgument.notNull(declaringType, "declaringType");
     VerifyArgument.notNull(name, "name");
     VerifyArgument.notNull(signature, "signature");
     
     pushGenericContext(declaringType);
     try
     {
       return new UnresolvedField(declaringType, name, parseTypeSignature(signature));
 
 
     }
     finally
     {
 
       popGenericContext();
     }
   }
   
   public MethodReference parseMethod(TypeReference declaringType, String name, String descriptor) {
     VerifyArgument.notNull(declaringType, "declaringType");
     VerifyArgument.notNull(name, "name");
     VerifyArgument.notNull(descriptor, "descriptor");
     
     pushGenericContext(declaringType);
     try
     {
       IMethodSignature signature = parseMethodSignature(descriptor);
       return lookupMethod(declaringType, name, signature);
     }
     finally {
       popGenericContext();
     }
   }
   
 
   public TypeReference lookupType(String packageName, String typeName)
   {
     String dottedName;
     if (StringUtilities.isNullOrEmpty(packageName)) {
       dottedName = typeName;
     }
     else {
       dottedName = packageName + "." + typeName;
     }
     
     TypeReference reference = this._factory.makeNamedType(dottedName);
     
     if (this._suppressResolveDepth.get() > 0) {
       return reference;
     }
     
 
 
 
     return reference;
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   protected TypeReference lookupTypeVariable(String name)
   {
     int i = 0; for (int n = this._genericContexts.size(); i < n; i++) {
       IGenericContext context = (IGenericContext)this._genericContexts.get(i);
       TypeReference typeVariable = context.findTypeVariable(name);
       
       if (typeVariable != null) {
         return typeVariable;
       }
     }
     
     if ((this._resolver instanceof IGenericContext)) {
       return ((IGenericContext)this._resolver).findTypeVariable(name);
     }
     
     return null;
   }
   
   public IMethodSignature parseMethodSignature(String signature)
   {
     VerifyArgument.notNull(signature, "signature");
     
     MethodTypeSignature methodTypeSignature = this._signatureParser.parseMethodSignature(signature);
     Reifier reifier = Reifier.make(this._factory);
     
 
 
 
 
 
     ReturnType returnTypeSignature = methodTypeSignature.getReturnType();
     TypeSignature[] parameterTypeSignatures = methodTypeSignature.getParameterTypes();
     FormalTypeParameter[] genericParameterSignatures = methodTypeSignature.getFormalTypeParameters();
     FieldTypeSignature[] thrownTypeSignatures = methodTypeSignature.getExceptionTypes();
     
     boolean needPopGenericContext = false;
     try {
       List<GenericParameter> genericParameters;
       if (ArrayUtilities.isNullOrEmpty(genericParameterSignatures)) {
         genericParameters = Collections.emptyList();
       }
       else {
         final GenericParameter[] gp = new GenericParameter[genericParameterSignatures.length];
         
         pushGenericContext(new IGenericContext()
         {
           public GenericParameter findTypeVariable(String name)
           {
             for (GenericParameter g : gp) {
               if (g == null) {
                 break;
               }
               
               if (StringUtilities.equals(g.getName(), name)) {
                 return g;
               }
             }
             return null;
           }
           
 
         });
         needPopGenericContext = true;
         
 
 
 
 
 
         for (int i = 0; i < gp.length; i++) {
           gp[i] = this._factory.makeTypeVariable(genericParameterSignatures[i].getName(), (FieldTypeSignature[])EmptyArrayCache.fromElementType(FieldTypeSignature.class));
         }
         
 
 
 
         genericParameters = ArrayUtilities.asUnmodifiableList(gp);
         
         for (int i = 0; i < gp.length; i++) {
           FieldTypeSignature[] bounds = genericParameterSignatures[i].getBounds();
           
           if (!ArrayUtilities.isNullOrEmpty(bounds)) {
             gp[i].setExtendsBound(this._factory.makeTypeBound(bounds));
           }
         }
       }
       
       returnTypeSignature.accept(reifier);
       TypeReference returnType = reifier.getResult();
       List<TypeReference> parameterTypes;
       if (ArrayUtilities.isNullOrEmpty(parameterTypeSignatures)) {
         parameterTypes = Collections.emptyList();
       }
       else {
         TypeReference[] pt = new TypeReference[parameterTypeSignatures.length];
         
         for (int i = 0; i < pt.length; i++) {
           parameterTypeSignatures[i].accept(reifier);
           pt[i] = reifier.getResult();
         }
         
         parameterTypes = ArrayUtilities.asUnmodifiableList(pt); }
       List<TypeReference> thrownTypes;
       TypeReference[] tt;
      if (ArrayUtilities.isNullOrEmpty(thrownTypeSignatures)) {
         thrownTypes = Collections.emptyList();
       }
       else {
         tt = new TypeReference[thrownTypeSignatures.length];
         
         for (int i = 0; i < tt.length; i++) {
           thrownTypeSignatures[i].accept(reifier);
           tt[i] = reifier.getResult();
         }
         
         thrownTypes = ArrayUtilities.asUnmodifiableList(tt);
       }
       
       return this._factory.makeMethodSignature(returnType, parameterTypes, genericParameters, thrownTypes);
     }
     finally {
       if (needPopGenericContext) {
         popGenericContext();
       }
     }
   }
   
   public IClassSignature parseClassSignature(String signature) {
     VerifyArgument.notNull(signature, "signature");
     
     ClassSignature classSignature = this._signatureParser.parseClassSignature(signature);
     Reifier reifier = Reifier.make(this._factory);
     
 
 
 
 
     ClassTypeSignature baseTypeSignature = classSignature.getSuperType();
     ClassTypeSignature[] interfaceTypeSignatures = classSignature.getInterfaces();
     FormalTypeParameter[] genericParameterSignatures = classSignature.getFormalTypeParameters();
     
     boolean needPopGenericContext = false;
     try {
       List<GenericParameter> genericParameters;
       if (ArrayUtilities.isNullOrEmpty(genericParameterSignatures)) {
         genericParameters = Collections.emptyList();
       }
       else {
         final GenericParameter[] gp = new GenericParameter[genericParameterSignatures.length];
         
         pushGenericContext(new IGenericContext()
         {
           public GenericParameter findTypeVariable(String name)
           {
             for (GenericParameter g : gp) {
               if (g == null) {
                 break;
               }
               
               if (StringUtilities.equals(g.getName(), name)) {
                 return g;
               }
             }
             return null;
           }
           
 
         });
         needPopGenericContext = true;
         
 
 
 
 
 
         for (int i = 0; i < gp.length; i++) {
           gp[i] = this._factory.makeTypeVariable(genericParameterSignatures[i].getName(), (FieldTypeSignature[])EmptyArrayCache.fromElementType(FieldTypeSignature.class));
         }
         
 
 
 
         genericParameters = ArrayUtilities.asUnmodifiableList(gp);
         
         for (int i = 0; i < gp.length; i++) {
           FieldTypeSignature[] bounds = genericParameterSignatures[i].getBounds();
           
           if (!ArrayUtilities.isNullOrEmpty(bounds)) {
             gp[i].setExtendsBound(this._factory.makeTypeBound(bounds));
           }
         }
       }
       
       baseTypeSignature.accept(reifier);
       TypeReference baseType = reifier.getResult();
       List<TypeReference> interfaceTypes;
       TypeReference[] it; 
       if (ArrayUtilities.isNullOrEmpty(interfaceTypeSignatures)) {
         interfaceTypes = Collections.emptyList();
       }
       else {
         it = new TypeReference[interfaceTypeSignatures.length];
         
         for (int i = 0; i < it.length; i++) {
           interfaceTypeSignatures[i].accept(reifier);
           it[i] = reifier.getResult();
         }
         
         interfaceTypes = ArrayUtilities.asUnmodifiableList(it);
       }
       
       return this._factory.makeClassSignature(baseType, interfaceTypes, genericParameters);
     }
     finally {
       if (needPopGenericContext) {
         popGenericContext();
       }
     }
   }
   
   protected MethodReference lookupMethod(TypeReference declaringType, String name, IMethodSignature signature) {
     MethodReference reference = new UnresolvedMethod(declaringType, name, signature);
     
 
 
 
 
     if (this._suppressResolveDepth.get() > 0) {
       return reference;
     }
     
 
 
 
     return reference;
   }
   
 
 
 
   private static final TypeReference[] PRIMITIVE_TYPES = new TypeReference[16];
   
   static {
     RuntimeHelpers.ensureClassInitialized(MetadataSystem.class);
     
     TypeReference[] allPrimitives = { BuiltinTypes.Boolean, BuiltinTypes.Byte, BuiltinTypes.Character, BuiltinTypes.Short, BuiltinTypes.Integer, BuiltinTypes.Long, BuiltinTypes.Float, BuiltinTypes.Double, BuiltinTypes.Void };
     
 
 
 
 
 
 
 
 
 
 
     for (TypeReference t : allPrimitives) {
       PRIMITIVE_TYPES[hashPrimitiveName(t.getName())] = t;
     }
   }
   
   private static int hashPrimitiveName(String name) {
     if (name.length() < 3) {
       return 0;
     }
     return (name.charAt(0) + name.charAt(2)) % 16;
   }
   
 
   private final class UnresolvedMethod
     extends MethodReference
   {
     private final TypeReference _declaringType;
     private final String _name;
     private final IMethodSignature _signature;
     private final List<GenericParameter> _genericParameters;
     
     UnresolvedMethod(TypeReference declaringType, String name, IMethodSignature signature)
     {
       this._declaringType = ((TypeReference)VerifyArgument.notNull(declaringType, "declaringType"));
       this._name = ((String)VerifyArgument.notNull(name, "name"));
       this._signature = ((IMethodSignature)VerifyArgument.notNull(signature, "signature"));
       GenericParameterCollection genericParameters;
       if (this._signature.hasGenericParameters()) {
         genericParameters = new GenericParameterCollection(this);
         
         for (GenericParameter genericParameter : this._signature.getGenericParameters()) {
           genericParameters.add(genericParameter);
         }
         
         genericParameters.freeze(false);
         
         this._genericParameters = genericParameters;
       }
       else {
         this._genericParameters = Collections.emptyList();
       }
     }
     
     public String getName()
     {
       return this._name;
     }
     
     public TypeReference getReturnType()
     {
       return this._signature.getReturnType();
     }
     
     public List<ParameterDefinition> getParameters()
     {
       return this._signature.getParameters();
     }
     
     public TypeReference getDeclaringType()
     {
       return this._declaringType;
     }
     
     public List<GenericParameter> getGenericParameters()
     {
       return this._genericParameters;
     }
     
     public List<TypeReference> getThrownTypes()
     {
       return this._signature.getThrownTypes();
     }
   }
   
 
   private final class UnresolvedField
     extends FieldReference
   {
     private final TypeReference _declaringType;
     private final String _name;
     private final TypeReference _fieldType;
     
     UnresolvedField(TypeReference declaringType, String name, TypeReference fieldType)
     {
       this._declaringType = ((TypeReference)VerifyArgument.notNull(declaringType, "declaringType"));
       this._name = ((String)VerifyArgument.notNull(name, "name"));
       this._fieldType = ((TypeReference)VerifyArgument.notNull(fieldType, "fieldType"));
     }
     
     public String getName()
     {
       return this._name;
     }
     
     public TypeReference getDeclaringType()
     {
       return this._declaringType;
     }
     
     public TypeReference getFieldType()
     {
       return this._fieldType;
     }
     
     protected StringBuilder appendName(StringBuilder sb, boolean fullName, boolean dottedName)
     {
       if (fullName) {
         TypeReference declaringType = getDeclaringType();
         
         if (declaringType != null) {
           return declaringType.appendName(sb, true, false).append('.').append(this._name);
         }
       }
       
       return sb.append(this._name);
     }
   }
 }


