 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.assembler.ir.attributes.AnnotationDefaultAttribute;
 import com.strobel.assembler.ir.attributes.LineNumberTableAttribute;
 import com.strobel.assembler.ir.attributes.LineNumberTableEntry;
 import com.strobel.assembler.ir.attributes.SourceAttribute;
 import com.strobel.assembler.metadata.FieldDefinition;
 import com.strobel.assembler.metadata.Flags;
 import com.strobel.assembler.metadata.GenericParameter;
 import com.strobel.assembler.metadata.IGenericInstance;
 import com.strobel.assembler.metadata.IMemberDefinition;
 import com.strobel.assembler.metadata.MetadataResolver;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.assembler.metadata.annotations.AnnotationAnnotationElement;
 import com.strobel.assembler.metadata.annotations.AnnotationElement;
 import com.strobel.assembler.metadata.annotations.AnnotationParameter;
 import com.strobel.assembler.metadata.annotations.ArrayAnnotationElement;
 import com.strobel.assembler.metadata.annotations.ClassAnnotationElement;
 import com.strobel.assembler.metadata.annotations.ConstantAnnotationElement;
 import com.strobel.assembler.metadata.annotations.CustomAnnotation;
 import com.strobel.assembler.metadata.annotations.EnumAnnotationElement;
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.MutableInteger;
 import com.strobel.core.Predicate;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.DecompilerSettings;
 import com.strobel.decompiler.languages.java.JavaOutputVisitor;
 import com.strobel.decompiler.languages.java.ast.transforms.IAstTransform;
 import java.lang.ref.Reference;
 import java.lang.ref.SoftReference;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.IdentityHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import javax.lang.model.element.Modifier;
 
 public final class AstBuilder
 {
   private final DecompilerContext _context;
   private final CompilationUnit _compileUnit = new CompilationUnit();
   private final Map<String, Reference<TypeDeclaration>> _typeDeclarations = new java.util.LinkedHashMap();
   private final Map<String, String> _unqualifiedTypeNames = new java.util.LinkedHashMap();
   
   private final TextNode _packagePlaceholder;
   private boolean _decompileMethodBodies = true;
   private boolean _haveTransformationsRun;
   
   public AstBuilder(DecompilerContext context) {
     this._context = ((DecompilerContext)VerifyArgument.notNull(context, "context"));
     
     String headerText = context.getSettings().getOutputFileHeaderText();
     
     if (!StringUtilities.isNullOrWhitespace(headerText)) {
       List<String> lines = StringUtilities.split(headerText, false, '\n', new char[0]);
       
       for (String line : lines) {
         this._compileUnit.addChild(new Comment(" " + line.trim(), CommentType.SingleLine), Roles.COMMENT);
       }
       
       this._compileUnit.addChild(new UnixNewLine(), Roles.NEW_LINE);
     }
     
     this._packagePlaceholder = new TextNode();
     this._compileUnit.addChild(this._packagePlaceholder, Roles.TEXT);
     
     if (this._context.getUserData(Keys.AST_BUILDER) == null) {
       this._context.putUserData(Keys.AST_BUILDER, this);
     }
   }
   
   final DecompilerContext getContext() {
     return this._context;
   }
   
   public final boolean getDecompileMethodBodies() {
     return this._decompileMethodBodies;
   }
   
   public final void setDecompileMethodBodies(boolean decompileMethodBodies) {
     this._decompileMethodBodies = decompileMethodBodies;
   }
   
   public final CompilationUnit getCompilationUnit() {
     return this._compileUnit;
   }
   
   public final void runTransformations() {
     runTransformations(null);
   }
   
   public final void runTransformations(Predicate<IAstTransform> transformAbortCondition) {
     com.strobel.decompiler.languages.java.ast.transforms.TransformationPipeline.runTransformationsUntil(this._compileUnit, transformAbortCondition, this._context);
     this._compileUnit.acceptVisitor(new InsertParenthesesVisitor(), null);
     this._haveTransformationsRun = true;
   }
   
   public final void addType(TypeDefinition type) {
     TypeDeclaration astType = createType(type);
     String packageName = type.getPackageName();
     
     if ((this._compileUnit.getPackage().isNull()) && (!StringUtilities.isNullOrWhitespace(packageName))) {
       this._compileUnit.insertChildBefore(this._packagePlaceholder, new PackageDeclaration(packageName), Roles.PACKAGE);
       
 
 
 
       this._packagePlaceholder.remove();
     }
     
     this._compileUnit.addChild(astType, CompilationUnit.MEMBER_ROLE);
   }
   
   public final TypeDeclaration createType(TypeDefinition type) {
     VerifyArgument.notNull(type, "type");
     
     Reference<TypeDeclaration> existingDeclaration = (Reference)this._typeDeclarations.get(type.getInternalName());
     
     TypeDeclaration d;
     if ((existingDeclaration != null) && ((d = (TypeDeclaration)existingDeclaration.get()) != null)) {
       return d;
     }
     
     return createTypeNoCache(type);
   }
   
   protected final TypeDeclaration createTypeNoCache(TypeDefinition type) {
     VerifyArgument.notNull(type, "type");
     
     TypeDefinition oldCurrentType = this._context.getCurrentType();
     
     this._context.setCurrentType(type);
     try
     {
       return createTypeCore(type);
     }
     finally {
       this._context.setCurrentType(oldCurrentType);
     }
   }
   
   public AstType convertType(TypeReference type) {
     return convertType(type, new ConvertTypeOptions());
   }
   
   public AstType convertType(TypeReference type, ConvertTypeOptions options) {
     return convertType(type, new MutableInteger(0), options);
   }
   
   public final List<ParameterDeclaration> createParameters(Iterable<ParameterDefinition> parameters) {
     List<ParameterDeclaration> declarations = new ArrayList();
     
     for (ParameterDefinition p : parameters) {
       TypeReference type = p.getParameterType();
       AstType astType = convertType(type);
       ParameterDeclaration d = new ParameterDeclaration(p.getName(), astType);
       
       d.putUserData(Keys.PARAMETER_DEFINITION, p);
       
       for (CustomAnnotation annotation : p.getAnnotations()) {
         d.getAnnotations().add(createAnnotation(annotation));
       }
       
       declarations.add(d);
       
       if (p.isFinal()) {
         EntityDeclaration.addModifier(d, Modifier.FINAL);
       }
     }
     
     return Collections.unmodifiableList(declarations);
   }
   
   final AstType convertType(TypeReference type, MutableInteger typeIndex, ConvertTypeOptions options) {
     if (type == null) {
       return AstType.NULL;
     }
     
     if (type.isArray()) {
       return convertType(type.getElementType(), typeIndex.increment(), options).makeArrayType();
     }
     
     if (type.isGenericParameter()) {
       SimpleType simpleType = new SimpleType(type.getSimpleName());
       simpleType.putUserData(Keys.TYPE_REFERENCE, type);
       return simpleType;
     }
     
     if (type.isPrimitive()) {
       SimpleType simpleType = new SimpleType(type.getSimpleName());
       simpleType.putUserData(Keys.TYPE_REFERENCE, type.resolve());
       return simpleType;
     }
     
     if (type.isWildcardType()) {
       if (!options.getAllowWildcards()) {
         if (type.hasExtendsBound()) {
           return convertType(type.getExtendsBound(), options);
         }
         return convertType(com.strobel.assembler.metadata.BuiltinTypes.Object, options);
       }
       
       WildcardType wildcardType = new WildcardType();
       
       if (type.hasExtendsBound()) {
         wildcardType.addChild(convertType(type.getExtendsBound()), Roles.EXTENDS_BOUND);
       }
       else if (type.hasSuperBound()) {
         wildcardType.addChild(convertType(type.getSuperBound()), Roles.SUPER_BOUND);
       }
       
       wildcardType.putUserData(Keys.TYPE_REFERENCE, type);
       return wildcardType;
     }
     
     boolean includeTypeArguments = (options == null) || (options.getIncludeTypeArguments());
     boolean includeTypeParameterDefinitions = (options == null) || (options.getIncludeTypeParameterDefinitions());
     boolean allowWildcards = (options == null) || (options.getAllowWildcards());
     
     if (((type instanceof IGenericInstance)) && (includeTypeArguments))
     {
       IGenericInstance genericInstance = (IGenericInstance)type;
       
       if (options != null) {
         options.setIncludeTypeParameterDefinitions(false);
       }
       AstType baseType;
       try {
         baseType = convertType((TypeReference)genericInstance.getGenericDefinition(), typeIndex.increment(), options);
 
 
       }
       finally
       {
 
         if (options != null) {
           options.setIncludeTypeParameterDefinitions(includeTypeParameterDefinitions);
         }
       }
       
       if (options != null) {
         options.setAllowWildcards(true);
       }
       
       Object typeArguments = new ArrayList();
       try
       {
         for (TypeReference typeArgument : genericInstance.getTypeArguments()) {
           ((List)typeArguments).add(convertType(typeArgument, typeIndex.increment(), options));
         }
       }
       finally {
         if (options != null) {
           options.setAllowWildcards(allowWildcards);
         }
       }
       
       applyTypeArguments(baseType, (List)typeArguments);
       baseType.putUserData(Keys.TYPE_REFERENCE, type);
       
       return baseType;
     }
     
     String name = null;
     
     PackageDeclaration packageDeclaration = this._compileUnit.getPackage();
     
     TypeDefinition resolvedType = type.resolve();
     TypeReference nameSource = resolvedType != null ? resolvedType : type;
     
     if ((options == null) || (options.getIncludePackage())) {
       String packageName = nameSource.getPackageName();
       name = packageName + "." + nameSource.getSimpleName();
 
 
 
 
 
 
 
 
 
 
 
     }
     else
     {
 
 
 
 
 
 
 
 
 
 
 
       if ((packageDeclaration != null) && (StringUtilities.equals(packageDeclaration.getName(), nameSource.getPackageName())))
       {
 
         String unqualifiedName = nameSource.getSimpleName();
         name = unqualifiedName; }
       TypeReference typeToImport;
       TypeReference typeToImport;
       String unqualifiedName; if (nameSource.isNested()) {
         String unqualifiedName = nameSource.getSimpleName();
         
         TypeReference current = nameSource;
         
         while (current.isNested()) {
           current = current.getDeclaringType();
           
           if (isContextWithinType(current)) {
             break;
           }
           
           unqualifiedName = current.getSimpleName() + "." + unqualifiedName;
         }
         
         name = unqualifiedName;
         typeToImport = current;
       }
       else {
         typeToImport = nameSource;
         unqualifiedName = nameSource.getSimpleName();
       }
       
       if ((options.getAddImports()) && (!this._typeDeclarations.containsKey(typeToImport.getInternalName()))) {
         String importedName = (String)this._unqualifiedTypeNames.get(typeToImport.getSimpleName());
         
         if (importedName == null) {
           SimpleType importedType = new SimpleType(typeToImport.getFullName());
           
           importedType.putUserData(Keys.TYPE_REFERENCE, typeToImport);
           
           if (packageDeclaration != null) {
             this._compileUnit.insertChildAfter(packageDeclaration, new ImportDeclaration(importedType), CompilationUnit.IMPORT_ROLE);
 
 
           }
           else
           {
 
             this._compileUnit.getImports().add(new ImportDeclaration(importedType));
           }
           
           this._unqualifiedTypeNames.put(typeToImport.getSimpleName(), typeToImport.getFullName());
           importedName = typeToImport.getFullName();
         }
         
         if (name == null) {
           if (importedName.equals(typeToImport.getFullName())) {
             name = unqualifiedName;
           }
           else {
             String packageName = nameSource.getPackageName();
             name = packageName + "." + nameSource.getSimpleName();
           }
           
         }
       }
       else if (name != null) {
         name = nameSource.getSimpleName();
       }
     }
     
     SimpleType astType = new SimpleType(name);
     
     astType.putUserData(Keys.TYPE_REFERENCE, type);
     
 
 
 
 
 
 
     return astType;
   }
   
   private boolean isContextWithinType(TypeReference type) {
     TypeReference scope = this._context.getCurrentType();
     
     for (TypeReference current = scope; 
         current != null; 
         current = current.getDeclaringType())
     {
       if (MetadataResolver.areEquivalent(current, type)) {
         return true;
       }
       
       TypeDefinition resolved = current.resolve();
       
       if (resolved != null) {
         TypeReference baseType = resolved.getBaseType();
         
         while (baseType != null) {
           if (MetadataResolver.areEquivalent(baseType, type)) {
             return true;
           }
           
           TypeDefinition resolvedBaseType = baseType.resolve();
           
           baseType = resolvedBaseType != null ? resolvedBaseType.getBaseType() : null;
         }
         
 
         for (TypeReference ifType : com.strobel.assembler.metadata.MetadataHelper.getInterfaces(current)) {
           if (MetadataResolver.areEquivalent(ifType, type)) {
             return true;
           }
         }
       }
     }
     
     return false;
   }
   
   private TypeDeclaration createTypeCore(TypeDefinition type) {
     TypeDeclaration astType = new TypeDeclaration();
     String packageName = type.getPackageName();
     
     if ((this._compileUnit.getPackage().isNull()) && (!StringUtilities.isNullOrWhitespace(packageName))) {
       PackageDeclaration packageDeclaration = new PackageDeclaration(packageName);
       
       packageDeclaration.putUserData(Keys.PACKAGE_REFERENCE, com.strobel.assembler.metadata.PackageReference.parse(packageName));
       
       this._compileUnit.insertChildBefore(this._packagePlaceholder, packageDeclaration, Roles.PACKAGE);
       
 
 
 
 
       this._packagePlaceholder.remove();
     }
     
     this._typeDeclarations.put(type.getInternalName(), new SoftReference(astType));
     
     long flags = type.getFlags();
     
     if ((type.isInterface()) || (type.isEnum())) {
       flags &= 0x7;
     }
     else {
       flags &= 0x7E1F;
     }
     
     EntityDeclaration.setModifiers(astType, Flags.asModifierSet(scrubAccessModifiers(flags)));
     
 
 
 
     astType.setName(type.getSimpleName());
     astType.putUserData(Keys.TYPE_DEFINITION, type);
     astType.putUserData(Keys.TYPE_REFERENCE, type);
     
     if (type.isEnum()) {
       astType.setClassType(ClassType.ENUM);
     }
     else if (type.isAnnotation()) {
       astType.setClassType(ClassType.ANNOTATION);
     }
     else if (type.isInterface()) {
       astType.setClassType(ClassType.INTERFACE);
     }
     else {
       astType.setClassType(ClassType.CLASS);
     }
     
     List<TypeParameterDeclaration> typeParameters = createTypeParameters(type.getGenericParameters());
     
     if (!typeParameters.isEmpty()) {
       astType.getTypeParameters().addAll(typeParameters);
     }
     
     TypeReference baseType = type.getBaseType();
     
     if ((baseType != null) && (!type.isEnum()) && (!com.strobel.assembler.metadata.BuiltinTypes.Object.equals(baseType))) {
       astType.addChild(convertType(baseType), Roles.BASE_TYPE);
     }
     
     for (TypeReference interfaceType : type.getExplicitInterfaces()) {
       if ((!type.isAnnotation()) || (!"java/lang/annotations/Annotation".equals(interfaceType.getInternalName())))
       {
 
         astType.addChild(convertType(interfaceType), Roles.IMPLEMENTED_INTERFACE);
       }
     }
     for (CustomAnnotation annotation : type.getAnnotations()) {
       astType.getAnnotations().add(createAnnotation(annotation));
     }
     
     addTypeMembers(astType, type);
     
     return astType;
   }
   
   private long scrubAccessModifiers(long flags) {
     long result = flags & 0xFFFFFFFFFFFFFFF8;
     
     if ((flags & 0x2) != 0L) {
       return result | 0x2;
     }
     
     if ((flags & 0x4) != 0L) {
       return result | 0x4;
     }
     
     if ((flags & 1L) != 0L) {
       return result | 1L;
     }
     
     return result;
   }
   
   private void addTypeMembers(TypeDeclaration astType, TypeDefinition type) {
     for (FieldDefinition field : type.getDeclaredFields()) {
       astType.addChild(createField(field), Roles.TYPE_MEMBER);
     }
     
     for (MethodDefinition method : type.getDeclaredMethods()) {
       if (method.isConstructor()) {
         astType.addChild(createConstructor(method), Roles.TYPE_MEMBER);
       }
       else {
         astType.addChild(createMethod(method), Roles.TYPE_MEMBER);
       }
     }
     
     List<TypeDefinition> nestedTypes = new ArrayList();
     
     for (TypeDefinition nestedType : type.getDeclaredTypes()) {
       TypeReference declaringType = nestedType.getDeclaringType();
       
       if ((!nestedType.isLocalClass()) && (type.isEquivalentTo(declaringType)))
       {
 
         if (nestedType.isAnonymous()) {
           this._typeDeclarations.put(type.getInternalName(), new SoftReference(astType));
         }
         else {
           nestedTypes.add(nestedType);
         }
       }
     }
     
     sortNestedTypes(nestedTypes);
     
     for (TypeDefinition nestedType : nestedTypes) {
       astType.addChild(createTypeNoCache(nestedType), Roles.TYPE_MEMBER);
     }
   }
   
   private static void sortNestedTypes(List<TypeDefinition> types) {
     IdentityHashMap<TypeDefinition, Integer> minOffsets = new IdentityHashMap();
     
     for (TypeDefinition type : types) {
       minOffsets.put(type, findFirstLineNumber(type));
     }
     
     Collections.sort(types, new java.util.Comparator()
     {
 
       public int compare(TypeDefinition o1, TypeDefinition o2)
       {
         return Integer.compare(((Integer)this.val$minOffsets.get(o1)).intValue(), ((Integer)this.val$minOffsets.get(o2)).intValue());
       }
     });
   }
   
   private static Integer findFirstLineNumber(TypeDefinition type)
   {
     int minLineNumber = Integer.MAX_VALUE;
     
     for (MethodDefinition method : type.getDeclaredMethods()) {
       LineNumberTableAttribute attribute = (LineNumberTableAttribute)SourceAttribute.find("LineNumberTable", method.getSourceAttributes());
       
       if ((attribute != null) && (!attribute.getEntries().isEmpty())) {
         int firstLineNumber = ((LineNumberTableEntry)attribute.getEntries().get(0)).getLineNumber();
         
         if (firstLineNumber < minLineNumber) {
           minLineNumber = firstLineNumber;
         }
       }
     }
     
     return Integer.valueOf(minLineNumber);
   }
   
   private FieldDeclaration createField(FieldDefinition field) {
     FieldDeclaration astField = new FieldDeclaration();
     VariableInitializer initializer = new VariableInitializer(field.getName());
     
     astField.setName(field.getName());
     astField.addChild(initializer, Roles.VARIABLE);
     astField.setReturnType(convertType(field.getFieldType()));
     astField.putUserData(Keys.FIELD_DEFINITION, field);
     astField.putUserData(Keys.MEMBER_REFERENCE, field);
     
     EntityDeclaration.setModifiers(astField, Flags.asModifierSet(scrubAccessModifiers(field.getFlags() & 0x40DF)));
     
 
 
 
     if (field.hasConstantValue()) {
       initializer.setInitializer(new PrimitiveExpression(-34, field.getConstantValue()));
       initializer.putUserData(Keys.FIELD_DEFINITION, field);
       initializer.putUserData(Keys.MEMBER_REFERENCE, field);
     }
     
     for (CustomAnnotation annotation : field.getAnnotations()) {
       astField.getAnnotations().add(createAnnotation(annotation));
     }
     
     return astField;
   }
   
   private MethodDeclaration createMethod(MethodDefinition method) {
     MethodDeclaration astMethod = new MethodDeclaration();
     
     Set<Modifier> modifiers;
     Set<Modifier> modifiers;
     if (method.isTypeInitializer()) {
       modifiers = Collections.singleton(Modifier.STATIC);
     } else { Set<Modifier> modifiers;
       if (method.getDeclaringType().isInterface()) {
         modifiers = Collections.emptySet();
       }
       else {
         modifiers = Flags.asModifierSet(scrubAccessModifiers(method.getFlags() & 0xD3F));
       }
     }
     EntityDeclaration.setModifiers(astMethod, modifiers);
     
     astMethod.setName(method.getName());
     astMethod.getParameters().addAll(createParameters(method.getParameters()));
     astMethod.getTypeParameters().addAll(createTypeParameters(method.getGenericParameters()));
     astMethod.setReturnType(convertType(method.getReturnType()));
     astMethod.putUserData(Keys.METHOD_DEFINITION, method);
     astMethod.putUserData(Keys.MEMBER_REFERENCE, method);
     
     for (TypeDefinition declaredType : method.getDeclaredTypes()) {
       if (!declaredType.isAnonymous()) {
         astMethod.getDeclaredTypes().add(createType(declaredType));
       }
     }
     
     if ((!method.getDeclaringType().isInterface()) || (method.isTypeInitializer()) || (method.isDefault())) {
       astMethod.setBody(createMethodBody(method, astMethod.getParameters()));
     }
     
     for (TypeReference thrownType : method.getThrownTypes()) {
       astMethod.addChild(convertType(thrownType), Roles.THROWN_TYPE);
     }
     
     for (CustomAnnotation annotation : method.getAnnotations()) {
       astMethod.getAnnotations().add(createAnnotation(annotation));
     }
     
     AnnotationDefaultAttribute defaultAttribute = (AnnotationDefaultAttribute)SourceAttribute.find("AnnotationDefault", method.getSourceAttributes());
     
 
 
 
     if (defaultAttribute != null) {
       Expression defaultValue = createAnnotationElement(defaultAttribute.getDefaultValue());
       
       if ((defaultValue != null) && (!defaultValue.isNull())) {
         astMethod.setDefaultValue(defaultValue);
       }
     }
     
     return astMethod;
   }
   
   private ConstructorDeclaration createConstructor(MethodDefinition method) {
     ConstructorDeclaration astMethod = new ConstructorDeclaration();
     
     EntityDeclaration.setModifiers(astMethod, Flags.asModifierSet(scrubAccessModifiers(method.getFlags() & 0x7)));
     
 
 
 
     astMethod.setName(method.getDeclaringType().getName());
     astMethod.getParameters().addAll(createParameters(method.getParameters()));
     astMethod.setBody(createMethodBody(method, astMethod.getParameters()));
     astMethod.putUserData(Keys.METHOD_DEFINITION, method);
     astMethod.putUserData(Keys.MEMBER_REFERENCE, method);
     
     for (TypeReference thrownType : method.getThrownTypes()) {
       astMethod.addChild(convertType(thrownType), Roles.THROWN_TYPE);
     }
     
     return astMethod;
   }
   
   final List<TypeParameterDeclaration> createTypeParameters(List<GenericParameter> genericParameters) {
     if (genericParameters.isEmpty()) {
       return Collections.emptyList();
     }
     
     int count = genericParameters.size();
     TypeParameterDeclaration[] typeParameters = new TypeParameterDeclaration[genericParameters.size()];
     
     for (int i = 0; i < count; i++) {
       GenericParameter genericParameter = (GenericParameter)genericParameters.get(i);
       TypeParameterDeclaration typeParameter = new TypeParameterDeclaration(genericParameter.getName());
       
       if (genericParameter.hasExtendsBound()) {
         typeParameter.setExtendsBound(convertType(genericParameter.getExtendsBound()));
       }
       
       typeParameter.putUserData(Keys.TYPE_REFERENCE, genericParameter);
       typeParameter.putUserData(Keys.TYPE_DEFINITION, genericParameter);
       typeParameters[i] = typeParameter;
     }
     
     return ArrayUtilities.asUnmodifiableList(typeParameters);
   }
   
   static void addTypeArguments(TypeReference type, AstType astType) {
     if (type.hasGenericParameters()) {
       List<GenericParameter> genericParameters = type.getGenericParameters();
       int count = genericParameters.size();
       AstType[] typeArguments = new AstType[count];
       
       for (int i = 0; i < count; i++) {
         GenericParameter genericParameter = (GenericParameter)genericParameters.get(i);
         SimpleType typeParameter = new SimpleType(genericParameter.getName());
         
         typeParameter.putUserData(Keys.TYPE_REFERENCE, genericParameter);
         typeArguments[i] = typeParameter;
       }
       
       applyTypeArguments(astType, ArrayUtilities.asUnmodifiableList(typeArguments));
     }
   }
   
   static void applyTypeArguments(AstType baseType, List<AstType> typeArguments) {
     if ((baseType instanceof SimpleType)) {
       SimpleType st = (SimpleType)baseType;
       st.getTypeArguments().addAll(typeArguments);
     }
   }
   
 
 
   private BlockStatement createMethodBody(MethodDefinition method, Iterable<ParameterDeclaration> parameters)
   {
     if (this._decompileMethodBodies) {
       return AstMethodBodyBuilder.createMethodBody(this, method, this._context, parameters);
     }
     
     return null;
   }
   
   public static Expression makePrimitive(long val, TypeReference type) {
     if (com.strobel.decompiler.ast.TypeAnalysis.isBoolean(type)) {
       if (val == 0L) {
         return new PrimitiveExpression(-34, Boolean.FALSE);
       }
       return new PrimitiveExpression(-34, Boolean.TRUE);
     }
     
     if (type != null) {
       return new PrimitiveExpression(-34, JavaPrimitiveCast.cast(type.getSimpleType(), Long.valueOf(val)));
     }
     
     return new PrimitiveExpression(-34, JavaPrimitiveCast.cast(com.strobel.assembler.metadata.JvmType.Integer, Long.valueOf(val)));
   }
   
   public static Expression makeDefaultValue(TypeReference type) {
     if (type == null) {
       return new NullReferenceExpression(-34);
     }
     
     switch (type.getSimpleType()) {
     case Boolean: 
       return new PrimitiveExpression(-34, Boolean.FALSE);
     
     case Byte: 
       return new PrimitiveExpression(-34, Byte.valueOf((byte)0));
     
     case Character: 
       return new PrimitiveExpression(-34, Character.valueOf('\000'));
     
     case Short: 
       return new PrimitiveExpression(-34, Short.valueOf((short)0));
     
     case Integer: 
       return new PrimitiveExpression(-34, Integer.valueOf(0));
     
     case Long: 
       return new PrimitiveExpression(-34, Long.valueOf(0L));
     
     case Float: 
       return new PrimitiveExpression(-34, Float.valueOf(0.0F));
     
     case Double: 
       return new PrimitiveExpression(-34, Double.valueOf(0.0D));
     }
     
     return new NullReferenceExpression(-34);
   }
   
   public List<com.strobel.decompiler.languages.LineNumberPosition> generateCode(com.strobel.decompiler.ITextOutput output)
   {
     if (!this._haveTransformationsRun) {
       runTransformations();
     }
     
     JavaOutputVisitor visitor = new JavaOutputVisitor(output, this._context.getSettings());
     this._compileUnit.acceptVisitor(visitor, null);
     return visitor.getLineNumberPositions();
   }
   
   public static boolean isMemberHidden(IMemberDefinition member, DecompilerContext context) {
     DecompilerSettings settings = context.getSettings();
     
     if ((member.isSynthetic()) && (!settings.getShowSyntheticMembers())) {
       return !context.getForcedVisibleMembers().contains(member);
     }
     
     if (((member instanceof TypeReference)) && (((TypeReference)member).isNested()) && (settings.getExcludeNestedTypes()))
     {
 
 
       TypeDefinition resolvedType = ((TypeReference)member).resolve();
       
       return (resolvedType == null) || ((!resolvedType.isAnonymous()) && (findLocalType(resolvedType) == null));
     }
     
 
     return false;
   }
   
   private static TypeReference findLocalType(TypeReference type) {
     if (type != null) {
       TypeDefinition resolvedType = type.resolve();
       
       if ((resolvedType != null) && (resolvedType.isLocalClass())) {
         return resolvedType;
       }
       
       TypeReference declaringType = type.getDeclaringType();
       
       if (declaringType != null) {
         return findLocalType(declaringType);
       }
     }
     
     return null;
   }
   
   public Annotation createAnnotation(CustomAnnotation annotation) {
     Annotation a = new Annotation();
     AstNodeCollection<Expression> arguments = a.getArguments();
     
     a.setType(convertType(annotation.getAnnotationType()));
     
     List<AnnotationParameter> parameters = annotation.getParameters();
     
     for (AnnotationParameter p : parameters) {
       String member = p.getMember();
       Expression value = createAnnotationElement(p.getValue());
       
       if ((StringUtilities.isNullOrEmpty(member)) || ((parameters.size() == 1) && ("value".equals(member))))
       {
 
         arguments.add(value);
       }
       else {
         arguments.add(new AssignmentExpression(new IdentifierExpression(value.getOffset(), member), value));
       }
     }
     
     return a;
   }
   
   public Expression createAnnotationElement(AnnotationElement element) {
     switch (element.getElementType()) {
     case Constant: 
       ConstantAnnotationElement constant = (ConstantAnnotationElement)element;
       return new PrimitiveExpression(-34, constant.getConstantValue());
     
 
     case Enum: 
       EnumAnnotationElement enumElement = (EnumAnnotationElement)element;
       return new TypeReferenceExpression(-34, convertType(enumElement.getEnumType())).member(enumElement.getEnumConstantName());
     
 
     case Array: 
       ArrayAnnotationElement arrayElement = (ArrayAnnotationElement)element;
       ArrayInitializerExpression initializer = new ArrayInitializerExpression();
       AstNodeCollection<Expression> elements = initializer.getElements();
       
       for (AnnotationElement e : arrayElement.getElements()) {
         elements.add(createAnnotationElement(e));
       }
       
       return initializer;
     
 
     case Class: 
       return new ClassOfExpression(-34, convertType(((ClassAnnotationElement)element).getClassType()));
     
 
 
 
 
     case Annotation: 
       return createAnnotation(((AnnotationAnnotationElement)element).getAnnotation());
     }
     
     
     throw com.strobel.util.ContractUtils.unreachable();
   }
 }


