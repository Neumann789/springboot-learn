package com.strobel.assembler.metadata;
 
 import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.strobel.assembler.Collection;
import com.strobel.assembler.ir.ConstantPool;
import com.strobel.assembler.ir.MetadataReader;
import com.strobel.assembler.ir.attributes.AnnotationsAttribute;
import com.strobel.assembler.ir.attributes.BlobAttribute;
import com.strobel.assembler.ir.attributes.BootstrapMethodsAttribute;
import com.strobel.assembler.ir.attributes.BootstrapMethodsTableEntry;
import com.strobel.assembler.ir.attributes.CodeAttribute;
import com.strobel.assembler.ir.attributes.ConstantValueAttribute;
import com.strobel.assembler.ir.attributes.EnclosingMethodAttribute;
import com.strobel.assembler.ir.attributes.ExceptionTableEntry;
import com.strobel.assembler.ir.attributes.ExceptionsAttribute;
import com.strobel.assembler.ir.attributes.InnerClassEntry;
import com.strobel.assembler.ir.attributes.InnerClassesAttribute;
import com.strobel.assembler.ir.attributes.LineNumberTableAttribute;
import com.strobel.assembler.ir.attributes.LineNumberTableEntry;
import com.strobel.assembler.ir.attributes.LocalVariableTableAttribute;
import com.strobel.assembler.ir.attributes.LocalVariableTableEntry;
import com.strobel.assembler.ir.attributes.MethodParameterEntry;
import com.strobel.assembler.ir.attributes.MethodParametersAttribute;
import com.strobel.assembler.ir.attributes.ParameterAnnotationsAttribute;
import com.strobel.assembler.ir.attributes.SignatureAttribute;
import com.strobel.assembler.ir.attributes.SourceAttribute;
import com.strobel.assembler.ir.attributes.SourceFileAttribute;
import com.strobel.assembler.metadata.annotations.CustomAnnotation;
import com.strobel.core.ArrayUtilities;
import com.strobel.core.Comparer;
import com.strobel.core.ExceptionUtilities;
import com.strobel.core.StringUtilities;
import com.strobel.core.VerifyArgument;
import com.strobel.util.EmptyArrayCache;
 
 
 
 
 
 
 
 
 public final class ClassFileReader
   extends MetadataReader
 {
   public static final int OPTION_PROCESS_ANNOTATIONS = 1;
   public static final int OPTION_PROCESS_CODE = 2;
   public static final int OPTIONS_DEFAULT = 1;
   static final long MAGIC = 3405691582L;
   private final int _options;
   private final IMetadataResolver _resolver;
   private final Buffer _buffer;
   private final ConstantPool _constantPool;
   private final ConstantPool.TypeInfoEntry _baseClassEntry;
   private final ConstantPool.TypeInfoEntry[] _interfaceEntries;
   private final List<FieldInfo> _fields;
   private final List<MethodInfo> _methods;
   private final List<SourceAttribute> _attributes;
   private final String _internalName;
   private final TypeDefinition _typeDefinition;
   private final MetadataParser _parser;
   private final ResolverFrame _resolverFrame;
   private final Scope _scope;
   
   private ClassFileReader(int options, IMetadataResolver resolver, int majorVersion, int minorVersion, Buffer buffer, ConstantPool constantPool, int accessFlags, ConstantPool.TypeInfoEntry thisClassEntry, ConstantPool.TypeInfoEntry baseClassEntry, ConstantPool.TypeInfoEntry[] interfaceEntries)
   {
     this._typeDefinition = new TypeDefinition();
     this._options = options;
     this._resolver = resolver;
     this._resolverFrame = new ResolverFrame();
     this._internalName = thisClassEntry.getName();
     this._buffer = buffer;
     this._constantPool = constantPool;
     this._baseClassEntry = baseClassEntry;
     this._interfaceEntries = ((ConstantPool.TypeInfoEntry[])VerifyArgument.notNull(interfaceEntries, "interfaceEntries"));
     this._fields = new ArrayList();
     this._methods = new ArrayList();
     
     int delimiter = this._internalName.lastIndexOf('/');
     
     if (delimiter < 0) {
       this._typeDefinition.setPackageName("");
       this._typeDefinition.setName(this._internalName);
     }
     else {
       this._typeDefinition.setPackageName(this._internalName.substring(0, delimiter).replace('/', '.'));
       this._typeDefinition.setName(this._internalName.substring(delimiter + 1));
     }
     
     this._attributes = this._typeDefinition.getSourceAttributesInternal();
     
     int delimiterIndex = this._internalName.lastIndexOf('/');
     
     if (delimiterIndex < 0) {
       this._typeDefinition.setName(this._internalName);
     }
     else {
       this._typeDefinition.setPackageName(this._internalName.substring(0, delimiterIndex).replace('/', '.'));
       this._typeDefinition.setName(this._internalName.substring(delimiterIndex + 1));
     }
     
     this._typeDefinition.setResolver(this._resolver);
     this._typeDefinition.setFlags(accessFlags);
     this._typeDefinition.setCompilerVersion(majorVersion, minorVersion);
     this._resolverFrame.addType(this._typeDefinition);
     this._parser = new MetadataParser(this._typeDefinition);
     this._scope = new Scope(this._parser, this._typeDefinition, constantPool);
     
     this._constantPool.freezeIfUnfrozen();
     this._typeDefinition.setConstantPool(this._constantPool);
   }
   
   protected boolean shouldProcessAnnotations() {
     return (this._options & 0x1) == 1;
   }
   
   protected boolean shouldProcessCode() {
     return (this._options & 0x2) == 2;
   }
   
   protected IMetadataScope getScope()
   {
     return this._scope;
   }
   
   public MetadataParser getParser()
   {
     return this._parser;
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   protected SourceAttribute readAttributeCore(String name, Buffer buffer, int originalOffset, int length)
   {
     VerifyArgument.notNull(name, "name");
     VerifyArgument.notNull(buffer, "buffer");
     VerifyArgument.isNonNegative(length, "length");
     
     switch (name) {
     case "Code": 
       int maxStack = buffer.readUnsignedShort();
       int maxLocals = buffer.readUnsignedShort();
       int codeLength = buffer.readInt();
       int codeOffset = buffer.position();
       byte[] code = new byte[codeLength];
       
       buffer.read(code, 0, codeLength);
       
       int exceptionTableLength = buffer.readUnsignedShort();
       ExceptionTableEntry[] exceptionTable = new ExceptionTableEntry[exceptionTableLength];
       
       for (int k = 0; k < exceptionTableLength; k++) {
         int startOffset = buffer.readUnsignedShort();
         int endOffset = buffer.readUnsignedShort();
         int handlerOffset = buffer.readUnsignedShort();
         int catchTypeToken = buffer.readUnsignedShort();
         TypeReference catchType;
         if (catchTypeToken == 0) {
           catchType = null;
         }
         else {
           catchType = this._scope.lookupType(catchTypeToken);
         }
         
         exceptionTable[k] = new ExceptionTableEntry(startOffset, endOffset, handlerOffset, catchType);
       }
       
 
 
 
 
 
       int attributeCount = buffer.readUnsignedShort();
       SourceAttribute[] attributes = new SourceAttribute[attributeCount];
       
       readAttributes(buffer, attributes);
       
       if (shouldProcessCode()) {
         return new CodeAttribute(length, maxStack, maxLocals, codeOffset, codeLength, buffer, exceptionTable, attributes);
       }
       
 
 
 
 
 
 
 
 
 
       return new CodeAttribute(length, originalOffset + codeOffset, codeLength, maxStack, maxLocals, exceptionTable, attributes);
     
 
 
 
 
 
 
 
 
 
 
     case "InnerClasses": 
       InnerClassEntry[] entries = new InnerClassEntry[buffer.readUnsignedShort()];
       
       for (int i = 0; i < entries.length; i++) {
         int innerClassIndex = buffer.readUnsignedShort();
         int outerClassIndex = buffer.readUnsignedShort();
         int shortNameIndex = buffer.readUnsignedShort();
         int accessFlags = buffer.readUnsignedShort();
         
         ConstantPool.TypeInfoEntry innerClass = (ConstantPool.TypeInfoEntry)this._constantPool.getEntry(innerClassIndex);
         ConstantPool.TypeInfoEntry outerClass;
         if (outerClassIndex != 0) {
           outerClass = (ConstantPool.TypeInfoEntry)this._constantPool.getEntry(outerClassIndex);
         }
         else {
           outerClass = null;
         }
         
         entries[i] = new InnerClassEntry(innerClass.getName(), outerClass != null ? outerClass.getName() : null, shortNameIndex != 0 ? (String)this._constantPool.lookupConstant(shortNameIndex) : null, accessFlags);
       }
       
 
 
 
 
 
       return new InnerClassesAttribute(length, ArrayUtilities.asUnmodifiableList(entries));
     }
     
     
     return super.readAttributeCore(name, buffer, originalOffset, length);
   }
   
   private void readAttributesPhaseOne(Buffer buffer, SourceAttribute[] attributes)
   {
     for (int i = 0; i < attributes.length; i++) {
       int nameIndex = buffer.readUnsignedShort();
       int length = buffer.readInt();
       IMetadataScope scope = getScope();
       String name = (String)scope.lookupConstant(nameIndex);
       int token ;
       switch (name) {
       case "SourceFile": 
         token = buffer.readUnsignedShort();
         String sourceFile = (String)scope.lookupConstant(token);
         attributes[i] = new SourceFileAttribute(sourceFile);
         break;
       
 
       case "ConstantValue": 
         token = buffer.readUnsignedShort();
         Object constantValue = scope.lookupConstant(token);
         attributes[i] = new ConstantValueAttribute(constantValue);
         break;
       
 
       case "LineNumberTable": 
         int entryCount = buffer.readUnsignedShort();
         LineNumberTableEntry[] entries = new LineNumberTableEntry[entryCount];
         
         for (int j = 0; j < entries.length; j++) {
           entries[j] = new LineNumberTableEntry(buffer.readUnsignedShort(), buffer.readUnsignedShort());
         }
         
 
 
 
         attributes[i] = new LineNumberTableAttribute(entries);
         break;
       
 
       case "Signature": 
         token = buffer.readUnsignedShort();
         String signature = (String)scope.lookupConstant(token);
         attributes[i] = new SignatureAttribute(signature);
         break;
       
 
       case "MethodParameters": 
         attributes[i] = readAttributeCore(name, buffer, buffer.position(), length);
         break;
       
 
       case "InnerClasses": 
         attributes[i] = readAttributeCore(name, buffer, buffer.position(), length);
         break;
       
 
       default: 
         int offset = buffer.position();
         byte[] blob = new byte[length];
         buffer.read(blob, 0, blob.length);
         attributes[i] = new BlobAttribute(name, blob, offset);
       }
       
     }
   }
   
   public static TypeDefinition readClass(IMetadataResolver resolver, Buffer b)
   {
     return readClass(1, resolver, b);
   }
   
   public static TypeDefinition readClass(int options, IMetadataResolver resolver, Buffer b) {
     long magic = b.readInt() & 0xFFFFFFFF;
     
     if (magic != 3405691582L) {
       throw new IllegalStateException("Wrong magic number: " + magic);
     }
     
     int minorVersion = b.readUnsignedShort();
     int majorVersion = b.readUnsignedShort();
     
     ConstantPool constantPool = ConstantPool.read(b);
     
     int accessFlags = b.readUnsignedShort();
     
     ConstantPool.TypeInfoEntry thisClass = (ConstantPool.TypeInfoEntry)constantPool.get(b.readUnsignedShort(), ConstantPool.Tag.TypeInfo);
     
 
     int baseClassToken = b.readUnsignedShort();
     ConstantPool.TypeInfoEntry baseClass;
     if (baseClassToken == 0) {
       baseClass = null;
     }
     else {
       baseClass = (ConstantPool.TypeInfoEntry)constantPool.getEntry(baseClassToken);
     }
     
     ConstantPool.TypeInfoEntry[] interfaces = new ConstantPool.TypeInfoEntry[b.readUnsignedShort()];
     
     for (int i = 0; i < interfaces.length; i++) {
       interfaces[i] = ((ConstantPool.TypeInfoEntry)constantPool.get(b.readUnsignedShort(), ConstantPool.Tag.TypeInfo));
     }
     
     return new ClassFileReader(options, resolver, majorVersion, minorVersion, b, constantPool, accessFlags, thisClass, baseClass, interfaces).readClass();
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
   final TypeDefinition readClass()
   {
     this._parser.pushGenericContext(this._typeDefinition);
     try
     {
       this._resolver.pushFrame(this._resolverFrame);
       SourceAttribute enclosingMethod;
       try {
         populateMemberInfo();
         
         enclosingMethod = SourceAttribute.find("EnclosingMethod", this._attributes);
         
         MethodReference declaringMethod;
         
         try
         {
           if ((enclosingMethod instanceof BlobAttribute)) {
             enclosingMethod = inflateAttribute(enclosingMethod);
           }
           if ((enclosingMethod instanceof EnclosingMethodAttribute)) {
             MethodReference method = ((EnclosingMethodAttribute)enclosingMethod).getEnclosingMethod();
             
             if (method != null) {
               MethodDefinition resolvedMethod = method.resolve();
               
               if (resolvedMethod != null) {
                 method = resolvedMethod;
                 
                 AnonymousLocalTypeCollection enclosedTypes = resolvedMethod.getDeclaredTypesInternal();
                 
                 if (!enclosedTypes.contains(this._typeDefinition)) {
                   enclosedTypes.add(this._typeDefinition);
                 }
               }
               
               this._typeDefinition.setDeclaringMethod(method);
             }
             
             declaringMethod = method;
           }
           else {
             declaringMethod = null;
           }
         }
         catch (Throwable t) {
           throw ExceptionUtilities.asRuntimeException(t);
         }
         
         if (declaringMethod != null) {
           this._parser.popGenericContext();
           this._parser.pushGenericContext(declaringMethod);
           this._parser.pushGenericContext(this._typeDefinition);
         }
         try
         {
           populateDeclaringType();
           populateBaseTypes();
           visitAttributes();
           visitFields();
           defineMethods();
           populateNamedInnerTypes();
           populateAnonymousInnerTypes();
           checkEnclosingMethodAttributes();
         }
         finally {
           if (declaringMethod == null) {}
         }
         
       }
       finally
       {
         this._resolver.popFrame();
       }
       
       return this._typeDefinition;
     }
     finally {
       this._parser.popGenericContext();
     }
   }
   
   private void checkEnclosingMethodAttributes() {
     InnerClassesAttribute innerClasses = (InnerClassesAttribute)SourceAttribute.find("InnerClasses", this._attributes);
     
     if (innerClasses == null) {
       return;
     }
     
     for (InnerClassEntry entry : innerClasses.getEntries()) {
       String outerClassName = entry.getOuterClassName();
       String innerClassName = entry.getInnerClassName();
       
       if ((outerClassName == null) && 
       
 
 
         (StringUtilities.startsWith(innerClassName, this._internalName + "$")))
       {
 
 
         TypeReference innerType = this._parser.parseTypeDescriptor(innerClassName);
         TypeDefinition resolvedInnerType = innerType.resolve();
         
         if ((resolvedInnerType != null) && (resolvedInnerType.getDeclaringMethod() == null))
         {
 
           SourceAttribute rawEnclosingMethodAttribute = SourceAttribute.find("EnclosingMethod", resolvedInnerType.getSourceAttributes());
           
           EnclosingMethodAttribute enclosingMethodAttribute;
           
           if ((rawEnclosingMethodAttribute instanceof EnclosingMethodAttribute)) {
             enclosingMethodAttribute = (EnclosingMethodAttribute)rawEnclosingMethodAttribute;
           }
           else {
             enclosingMethodAttribute = null;
           }
           
           MethodReference method;
           
           if ((enclosingMethodAttribute != null) && ((method = enclosingMethodAttribute.getEnclosingMethod()) != null))
           {
 
             MethodDefinition resolvedMethod = method.resolve();
             
             if (resolvedMethod != null) {
               method = resolvedMethod;
               
               AnonymousLocalTypeCollection enclosedTypes = resolvedMethod.getDeclaredTypesInternal();
               
               if (!enclosedTypes.contains(this._typeDefinition)) {
                 enclosedTypes.add(this._typeDefinition);
               }
             }
             
             resolvedInnerType.setDeclaringMethod(method);
           }
         }
       }
     }
   }
   
   private void populateMemberInfo() { int fieldCount = this._buffer.readUnsignedShort();
     
     for (int i = 0; i < fieldCount; i++) {
       int accessFlags = this._buffer.readUnsignedShort();
       
       String name = this._constantPool.lookupUtf8Constant(this._buffer.readUnsignedShort());
       String descriptor = this._constantPool.lookupUtf8Constant(this._buffer.readUnsignedShort());
       
 
       int attributeCount = this._buffer.readUnsignedShort();
       SourceAttribute[] attributes;
       if (attributeCount > 0) {
         attributes = new SourceAttribute[attributeCount];
         readAttributesPhaseOne(this._buffer, attributes);
       }
       else {
         attributes = (SourceAttribute[])EmptyArrayCache.fromElementType(SourceAttribute.class);
       }
       
       FieldInfo field = new FieldInfo(accessFlags, name, descriptor, attributes);
       
       this._fields.add(field);
     }
     
     int methodCount = this._buffer.readUnsignedShort();
     
     for (int i = 0; i < methodCount; i++) {
       int accessFlags = this._buffer.readUnsignedShort();
       
       String name = this._constantPool.lookupUtf8Constant(this._buffer.readUnsignedShort());
       String descriptor = this._constantPool.lookupUtf8Constant(this._buffer.readUnsignedShort());
       
 
       int attributeCount = this._buffer.readUnsignedShort();
       SourceAttribute[] attributes;
       if (attributeCount > 0) {
         attributes = new SourceAttribute[attributeCount];
         readAttributesPhaseOne(this._buffer, attributes);
       }
       else {
         attributes = (SourceAttribute[])EmptyArrayCache.fromElementType(SourceAttribute.class);
       }
       
       MethodInfo method = new MethodInfo(accessFlags, name, descriptor, attributes);
       
       this._methods.add(method);
     }
     
     int typeAttributeCount = this._buffer.readUnsignedShort();
     
     if (typeAttributeCount > 0) {
       SourceAttribute[] typeAttributes = new SourceAttribute[typeAttributeCount];
       
       readAttributesPhaseOne(this._buffer, typeAttributes);
       
       Collections.addAll(this._attributes, typeAttributes);
     }
   }
   
   private void populateDeclaringType() {
     InnerClassesAttribute innerClasses = (InnerClassesAttribute)SourceAttribute.find("InnerClasses", this._attributes);
     
     if (innerClasses == null) {
       return;
     }
     
     for (InnerClassEntry entry : innerClasses.getEntries()) {
       String innerClassName = entry.getInnerClassName();
       String shortName = entry.getShortName();
       
       String outerClassName = entry.getOuterClassName();
       
       if (Comparer.equals(innerClassName, this._internalName))
       {
 
 
         if (outerClassName == null) {
           int delimiterIndex = innerClassName.lastIndexOf('$');
           
           if (delimiterIndex >= 0) {
             outerClassName = innerClassName.substring(0, delimiterIndex);
           }
           
 
         }
         else
         {
           if (StringUtilities.isNullOrEmpty(shortName)) {
             this._typeDefinition.setFlags(this._typeDefinition.getFlags() | 0x100000000000L);
           }
           else {
             this._typeDefinition.setSimpleName(shortName);
           }
           
           this._typeDefinition.setFlags(this._typeDefinition.getFlags() & 0xFFFFFFFFFFFFFFF8L | entry.getAccessFlags());
           
 
 
           TypeReference outerType = this._parser.parseTypeDescriptor(outerClassName);
           TypeDefinition resolvedOuterType = outerType.resolve();
           
           if (resolvedOuterType != null) {
             if (this._typeDefinition.getDeclaringType() == null) {
               this._typeDefinition.setDeclaringType(resolvedOuterType);
               
               Collection<TypeDefinition> declaredTypes = resolvedOuterType.getDeclaredTypesInternal();
               
               if (!declaredTypes.contains(this._typeDefinition)) {
                 declaredTypes.add(this._typeDefinition);
               }
             }
           }
           else if (this._typeDefinition.getDeclaringType() == null) {
             this._typeDefinition.setDeclaringType(outerType);
           }
           
           return;
         } }
     }
   }
   
   private void populateBaseTypes() {
     SignatureAttribute signature = (SignatureAttribute)SourceAttribute.find("Signature", this._attributes);
     String[] interfaceNames = new String[this._interfaceEntries.length];
     
     for (int i = 0; i < this._interfaceEntries.length; i++) {
       interfaceNames[i] = this._interfaceEntries[i].getName();
     }
     
 
     Collection<TypeReference> explicitInterfaces = this._typeDefinition.getExplicitInterfacesInternal();
     String genericSignature = signature != null ? signature.getSignature() : null;
     TypeReference baseType;
     if (StringUtilities.isNullOrEmpty(genericSignature)) {
       baseType = this._baseClassEntry != null ? this._parser.parseTypeDescriptor(this._baseClassEntry.getName()) : null;
       
       for (String interfaceName : interfaceNames) {
         explicitInterfaces.add(this._parser.parseTypeDescriptor(interfaceName));
       }
     }
     else {
       IClassSignature classSignature = this._parser.parseClassSignature(genericSignature);
       
       baseType = classSignature.getBaseType();
       explicitInterfaces.addAll(classSignature.getExplicitInterfaces());
       this._typeDefinition.getGenericParametersInternal().addAll(classSignature.getGenericParameters());
     }
     
     this._typeDefinition.setBaseType(baseType);
   }
   
   private void populateNamedInnerTypes() {
     InnerClassesAttribute innerClasses = (InnerClassesAttribute)SourceAttribute.find("InnerClasses", this._attributes);
     
     if (innerClasses == null) {
       return;
     }
     
     Collection<TypeDefinition> declaredTypes = this._typeDefinition.getDeclaredTypesInternal();
     
     for (InnerClassEntry entry : innerClasses.getEntries()) {
       String outerClassName = entry.getOuterClassName();
       
       if (outerClassName != null)
       {
 
 
         String innerClassName = entry.getInnerClassName();
         
         if (!Comparer.equals(this._internalName, innerClassName))
         {
 
 
           TypeReference innerType = this._parser.parseTypeDescriptor(innerClassName);
           TypeDefinition resolvedInnerType = innerType.resolve();
           
           if ((resolvedInnerType != null) && (Comparer.equals(this._internalName, outerClassName)) && (!declaredTypes.contains(resolvedInnerType)))
           {
 
 
             declaredTypes.add(resolvedInnerType);
             resolvedInnerType.setFlags(resolvedInnerType.getFlags() | entry.getAccessFlags());
           }
         }
       }
     } }
   
   private void populateAnonymousInnerTypes() { InnerClassesAttribute innerClasses = (InnerClassesAttribute)SourceAttribute.find("InnerClasses", this._attributes);
     
     if (innerClasses == null) {
       return;
     }
     
     Collection<TypeDefinition> declaredTypes = this._typeDefinition.getDeclaredTypesInternal();
     
     for (InnerClassEntry entry : innerClasses.getEntries()) {
       String simpleName = entry.getShortName();
       
       if (StringUtilities.isNullOrEmpty(simpleName))
       {
 
 
         String outerClassName = entry.getOuterClassName();
         String innerClassName = entry.getInnerClassName();
         
         if ((outerClassName != null) && (!Comparer.equals(innerClassName, this._internalName)))
         {
 
 
           TypeReference innerType = this._parser.parseTypeDescriptor(innerClassName);
           TypeDefinition resolvedInnerType = innerType.resolve();
           
           if (((resolvedInnerType instanceof TypeDefinition)) && (Comparer.equals(this._internalName, outerClassName)) && (!declaredTypes.contains(resolvedInnerType)))
           {
 
 
             declaredTypes.add(resolvedInnerType); }
         }
       }
     }
     TypeReference self = this._parser.getResolver().lookupType(this._internalName);
     
     if ((self != null) && (self.isNested())) {
       return;
     }
     
     for (InnerClassEntry entry : innerClasses.getEntries()) {
       String outerClassName = entry.getOuterClassName();
       
       if (outerClassName == null)
       {
 
 
         String innerClassName = entry.getInnerClassName();
         
         if (!Comparer.equals(innerClassName, this._internalName))
         {
 
 
           TypeReference innerType = this._parser.parseTypeDescriptor(innerClassName);
           TypeDefinition resolvedInnerType = innerType.resolve();
           
           if ((resolvedInnerType != null) && (Comparer.equals(this._internalName, outerClassName)) && (!declaredTypes.contains(resolvedInnerType)))
           {
 
 
             declaredTypes.add(resolvedInnerType); }
         }
       }
     }
   }
   
   private void visitFields() {
     Collection<FieldDefinition> declaredFields = this._typeDefinition.getDeclaredFieldsInternal();
     
     for (FieldInfo field : this._fields)
     {
       SignatureAttribute signature = (SignatureAttribute)SourceAttribute.find("Signature", field.attributes);
       
       TypeReference fieldType = tryParseTypeSignature(signature != null ? signature.getSignature() : null, field.descriptor);
       
 
 
 
       FieldDefinition fieldDefinition = new FieldDefinition(this._resolver);
       
       fieldDefinition.setDeclaringType(this._typeDefinition);
       fieldDefinition.setFlags(Flags.fromStandardFlags(field.accessFlags, Flags.Kind.Field));
       fieldDefinition.setName(field.name);
       fieldDefinition.setFieldType(fieldType);
       
       declaredFields.add(fieldDefinition);
       
       inflateAttributes(field.attributes);
       
       ConstantValueAttribute constantValueAttribute = (ConstantValueAttribute)SourceAttribute.find("ConstantValue", field.attributes);
       
       if (constantValueAttribute != null) {
         Object constantValue = constantValueAttribute.getValue();
         
         if ((constantValue instanceof Number)) {
           Number number = (Number)constantValue;
           JvmType jvmType = fieldDefinition.getFieldType().getSimpleType();
           
           switch (jvmType) {
           case Boolean: 
             fieldDefinition.setConstantValue(Boolean.valueOf(number.longValue() != 0L));
             break;
           case Byte: 
             fieldDefinition.setConstantValue(Byte.valueOf(number.byteValue()));
             break;
           case Character: 
             fieldDefinition.setConstantValue(Character.valueOf((char)(int)number.longValue()));
             break;
           case Short: 
             fieldDefinition.setConstantValue(Short.valueOf(number.shortValue()));
             break;
           case Integer: 
             fieldDefinition.setConstantValue(Integer.valueOf(number.intValue()));
             break;
           case Long: 
             fieldDefinition.setConstantValue(Long.valueOf(number.longValue()));
             break;
           case Float: 
             fieldDefinition.setConstantValue(Float.valueOf(number.floatValue()));
             break;
           case Double: 
             fieldDefinition.setConstantValue(Double.valueOf(number.doubleValue()));
             break;
           default: 
             fieldDefinition.setConstantValue(constantValue);
           }
         }
         else
         {
           fieldDefinition.setConstantValue(constantValue);
         }
       }
       
       if (SourceAttribute.find("Synthetic", field.attributes) != null) {
         fieldDefinition.setFlags(fieldDefinition.getFlags() | 0x1000);
       }
       
       if (SourceAttribute.find("Deprecated", field.attributes) != null) {
         fieldDefinition.setFlags(fieldDefinition.getFlags() | 0x20000);
       }
       
       for (SourceAttribute attribute : field.attributes) {
         fieldDefinition.getSourceAttributesInternal().add(attribute);
       }
       
       if (shouldProcessAnnotations()) {
         Collection<CustomAnnotation> annotations = fieldDefinition.getAnnotationsInternal();
         
         AnnotationsAttribute visibleAnnotations = (AnnotationsAttribute)SourceAttribute.find("RuntimeVisibleAnnotations", field.attributes);
         
 
 
 
         AnnotationsAttribute invisibleAnnotations = (AnnotationsAttribute)SourceAttribute.find("RuntimeInvisibleAnnotations", field.attributes);
         
 
 
 
         if (visibleAnnotations != null) {
           Collections.addAll(annotations, visibleAnnotations.getAnnotations());
         }
         
         if (invisibleAnnotations != null) {
           Collections.addAll(annotations, invisibleAnnotations.getAnnotations());
         }
       }
     }
   }
   
   private TypeReference tryParseTypeSignature(String signature, String fallback) {
     try {
       if (signature != null) {
         return this._parser.parseTypeSignature(signature);
       }
     }
     catch (Throwable ignored) {}
     
 
     return this._parser.parseTypeSignature(fallback);
   }
   
   private void defineMethods() {
     try {
       AutoCloseable ignored = this._parser.suppressTypeResolution();Throwable localThrowable2 = null;
       try { for (MethodInfo method : this._methods)
         {
           IMethodSignature methodDescriptor = this._parser.parseMethodSignature(method.descriptor);
           MethodDefinition methodDefinition = new MethodDefinition();
           
           methodDefinition.setName(method.name);
           methodDefinition.setFlags(Flags.fromStandardFlags(method.accessFlags, Flags.Kind.Method));
           methodDefinition.setDeclaringType(this._typeDefinition);
           
           if ((this._typeDefinition.isInterface()) && (!Flags.testAny(method.accessFlags, 1024))) {
             methodDefinition.setFlags(methodDefinition.getFlags() | 0x80000000000L);
           }
           
           this._typeDefinition.getDeclaredMethodsInternal().add(methodDefinition);
           this._parser.pushGenericContext(methodDefinition);
           try
           {
             SignatureAttribute signature = (SignatureAttribute)SourceAttribute.find("Signature", method.attributes);
             
             IMethodSignature methodSignature = tryParseMethodSignature(signature != null ? signature.getSignature() : null, methodDescriptor);
             
 
 
 
             List<ParameterDefinition> signatureParameters = methodSignature.getParameters();
             List<ParameterDefinition> descriptorParameters = methodDescriptor.getParameters();
             ParameterDefinitionCollection parameters = methodDefinition.getParametersInternal();
             
             methodDefinition.setReturnType(methodSignature.getReturnType());
             parameters.addAll(signatureParameters);
             methodDefinition.getGenericParametersInternal().addAll(methodSignature.getGenericParameters());
             methodDefinition.getThrownTypesInternal().addAll(methodSignature.getThrownTypes());
             
             int missingParameters = descriptorParameters.size() - signatureParameters.size();
             
             for (int i = 0; i < missingParameters; i++) {
               ParameterDefinition parameter = (ParameterDefinition)descriptorParameters.get(i);
               parameter.setFlags(parameter.getFlags() | 0x1000);
               parameters.add(i, parameter);
             }
             
             int slot = 0;
             
             if (!Flags.testAny(methodDefinition.getFlags(), 8L)) {
               slot++;
             }
             
             MethodParametersAttribute methodParameters = (MethodParametersAttribute)SourceAttribute.find("MethodParameters", method.attributes);
             List<MethodParameterEntry> parameterEntries = methodParameters != null ? methodParameters.getEntries() : null;
             List<ParameterDefinition> parametersList = methodDefinition.getParameters();
             
             for (int i = 0; i < parametersList.size(); i++) {
               ParameterDefinition parameter = (ParameterDefinition)parametersList.get(i);
               
               parameter.setSlot(slot);
               slot += parameter.getSize();
               
               if ((parameterEntries != null) && (i < parameterEntries.size())) {
                 MethodParameterEntry entry = (MethodParameterEntry)parameterEntries.get(i);
                 String parameterName = entry.getName();
                 
                 if (!StringUtilities.isNullOrWhitespace(parameterName)) {
                   parameter.setName(parameterName);
                 }
                 
                 parameter.setFlags(entry.getFlags());
               }
             }
             
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
             inflateAttributes(method.attributes);
             
             Collections.addAll(methodDefinition.getSourceAttributesInternal(), method.attributes);
             
             method.codeAttribute = SourceAttribute.find("Code", method.attributes);
             
             if (method.codeAttribute != null) {
               methodDefinition.getSourceAttributesInternal().addAll(((CodeAttribute)method.codeAttribute).getAttributes());
             }
             
             ExceptionsAttribute exceptions = (ExceptionsAttribute)SourceAttribute.find("Exceptions", method.attributes);
             Collection<TypeReference> thrownTypes;
             if (exceptions != null) {
               thrownTypes = methodDefinition.getThrownTypesInternal();
               
               for (TypeReference thrownType : exceptions.getExceptionTypes()) {
                 if (!thrownTypes.contains(thrownType)) {
                   thrownTypes.add(thrownType);
                 }
               }
             }
             
             if ("<init>".equals(method.name)) {
               if (Flags.testAny(this._typeDefinition.getFlags(), 17592186044416L)) {
                 methodDefinition.setFlags(methodDefinition.getFlags() | 0x20000000 | 0x1000);
               }
               
               if (Flags.testAny(method.accessFlags, 2048)) {
                 this._typeDefinition.setFlags(this._typeDefinition.getFlags() | 0x800);
               }
             }
             
             readMethodBody(method, methodDefinition);
             
             if (SourceAttribute.find("Synthetic", method.attributes) != null) {
               methodDefinition.setFlags(methodDefinition.getFlags() | 0x1000);
             }
             
             if (SourceAttribute.find("Deprecated", method.attributes) != null) {
               methodDefinition.setFlags(methodDefinition.getFlags() | 0x20000);
             }
             
             if (shouldProcessAnnotations()) {
               AnnotationsAttribute visibleAnnotations = (AnnotationsAttribute)SourceAttribute.find("RuntimeVisibleAnnotations", method.attributes);
               
 
 
 
               AnnotationsAttribute invisibleAnnotations = (AnnotationsAttribute)SourceAttribute.find("RuntimeInvisibleAnnotations", method.attributes);
               
 
 
 
               Collection<CustomAnnotation> annotations = methodDefinition.getAnnotationsInternal();
               
               if (visibleAnnotations != null) {
                 Collections.addAll(annotations, visibleAnnotations.getAnnotations());
               }
               
               if (invisibleAnnotations != null) {
                 Collections.addAll(annotations, invisibleAnnotations.getAnnotations());
               }
               
               ParameterAnnotationsAttribute visibleParameterAnnotations = (ParameterAnnotationsAttribute)SourceAttribute.find("RuntimeVisibleParameterAnnotations", method.attributes);
               
 
 
 
               ParameterAnnotationsAttribute invisibleParameterAnnotations = (ParameterAnnotationsAttribute)SourceAttribute.find("RuntimeInvisibleParameterAnnotations", method.attributes);
               
 
 
 
               if (visibleParameterAnnotations != null) {
                 for (int i = 0; (i < visibleParameterAnnotations.getAnnotations().length) && (i < parameters.size()); i++) {
                   Collections.addAll(((ParameterDefinition)parameters.get(i)).getAnnotationsInternal(), visibleParameterAnnotations.getAnnotations()[i]);
                 }
               }
               
 
 
 
               if (invisibleParameterAnnotations != null) {
                 for (int i = 0; (i < invisibleParameterAnnotations.getAnnotations().length) && (i < parameters.size()); i++) {
                   Collections.addAll(((ParameterDefinition)parameters.get(i)).getAnnotationsInternal(), invisibleParameterAnnotations.getAnnotations()[i]);
                 }
                 
               }
               
             }
           }
           finally
           {
             this._parser.popGenericContext();
           }
         }
       }
       catch (Throwable localThrowable1)
       {
         localThrowable2 = localThrowable1;throw localThrowable1;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
       }
       finally
       {
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
         if (ignored != null) if (localThrowable2 != null) try { ignored.close(); } catch (Throwable x2) { localThrowable2.addSuppressed(x2); } else ignored.close();
       }
     } catch (Throwable t) { throw ExceptionUtilities.asRuntimeException(t);
     }
   }
   
   private IMethodSignature tryParseMethodSignature(String signature, IMethodSignature fallback) {
     try {
       if (signature != null) {
         return this._parser.parseMethodSignature(signature);
       }
     }
     catch (Throwable ignored) {}
     
 
     return fallback;
   }
   
   private void readMethodBody(MethodInfo methodInfo, MethodDefinition methodDefinition) { List<ParameterDefinition> parameters;
     if ((methodInfo.codeAttribute instanceof CodeAttribute)) {
       if (Flags.testAny(this._options, 2)) {
         MethodReader reader = new MethodReader(methodDefinition, this._scope);
         MethodBody body = reader.readBody();
         
         methodDefinition.setBody(body);
         body.freeze();
       }
       else {
         CodeAttribute codeAttribute = (CodeAttribute)methodInfo.codeAttribute;
         
         LocalVariableTableAttribute localVariables = (LocalVariableTableAttribute)SourceAttribute.find("LocalVariableTable", codeAttribute.getAttributes());
         
 
 
 
         if (localVariables == null) {
           return;
         }
         
         parameters = methodDefinition.getParameters();
         
         for (LocalVariableTableEntry entry : localVariables.getEntries()) {
           ParameterDefinition parameter = null;
           
           for (int j = 0; j < parameters.size(); j++) {
             if (((ParameterDefinition)parameters.get(j)).getSlot() == entry.getIndex()) {
               parameter = (ParameterDefinition)parameters.get(j);
               break;
             }
           }
           
           if ((parameter != null) && (!parameter.hasName())) {
             parameter.setName(entry.getName());
           }
         }
       }
     }
   }
   
   private void visitAttributes() {
     inflateAttributes(this._attributes);
     
     if (shouldProcessAnnotations()) {
       AnnotationsAttribute visibleAnnotations = (AnnotationsAttribute)SourceAttribute.find("RuntimeVisibleAnnotations", this._attributes);
       
 
 
 
       AnnotationsAttribute invisibleAnnotations = (AnnotationsAttribute)SourceAttribute.find("RuntimeInvisibleAnnotations", this._attributes);
       
 
 
 
       Collection<CustomAnnotation> annotations = this._typeDefinition.getAnnotationsInternal();
       
       if (visibleAnnotations != null) {
         Collections.addAll(annotations, visibleAnnotations.getAnnotations());
       }
       
       if (invisibleAnnotations != null) {
         Collections.addAll(annotations, invisibleAnnotations.getAnnotations());
       }
     }
   }
   
 
   final class FieldInfo
   {
     final int accessFlags;
     
     final String name;
     final String descriptor;
     final SourceAttribute[] attributes;
     
     FieldInfo(int accessFlags, String name, String descriptor, SourceAttribute[] attributes)
     {
       this.accessFlags = accessFlags;
       this.name = name;
       this.descriptor = descriptor;
       this.attributes = attributes;
     }
   }
   
 
   final class MethodInfo
   {
     final int accessFlags;
     
     final String name;
     
     final String descriptor;
     final SourceAttribute[] attributes;
     SourceAttribute codeAttribute;
     
     MethodInfo(int accessFlags, String name, String descriptor, SourceAttribute[] attributes)
     {
       this.accessFlags = accessFlags;
       this.name = name;
       this.descriptor = descriptor;
       this.attributes = attributes;
       this.codeAttribute = SourceAttribute.find("Code", attributes);
     }
   }
   
 
 
 
 
   private static final MethodHandleType[] METHOD_HANDLE_TYPES = MethodHandleType.values();;
   
   static class Scope implements IMetadataScope {
     private final MetadataParser _parser;
     private final TypeDefinition _typeDefinition;
     private final ConstantPool _constantPool;
     
     Scope(MetadataParser parser, TypeDefinition typeDefinition, ConstantPool constantPool) {
       this._parser = parser;
       this._typeDefinition = typeDefinition;
       this._constantPool = constantPool;
     }
     
     public TypeReference lookupType(int token)
     {
       ConstantPool.Entry entry = this._constantPool.get(token);
       
       if ((entry instanceof ConstantPool.TypeInfoEntry)) {
         ConstantPool.TypeInfoEntry typeInfo = (ConstantPool.TypeInfoEntry)entry;
         
         return this._parser.parseTypeDescriptor(typeInfo.getName());
       }
       
       String typeName = (String)this._constantPool.lookupConstant(token);
       
       return this._parser.parseTypeSignature(typeName);
     }
     
     public FieldReference lookupField(int token)
     {
       ConstantPool.FieldReferenceEntry entry = (ConstantPool.FieldReferenceEntry)this._constantPool.getEntry(token);
       return lookupField(entry.typeInfoIndex, entry.nameAndTypeDescriptorIndex);
     }
     
     public MethodReference lookupMethod(int token)
     {
       ConstantPool.Entry entry = this._constantPool.getEntry(token);
       ConstantPool.ReferenceEntry reference;
       if ((entry instanceof ConstantPool.MethodHandleEntry)) {
         ConstantPool.MethodHandleEntry methodHandle = (ConstantPool.MethodHandleEntry)entry;
         reference = (ConstantPool.ReferenceEntry)this._constantPool.getEntry(methodHandle.referenceIndex);
       }
       else {
         reference = (ConstantPool.ReferenceEntry)entry;
       }
       
       return lookupMethod(reference.typeInfoIndex, reference.nameAndTypeDescriptorIndex);
     }
     
     public MethodHandle lookupMethodHandle(int token)
     {
       ConstantPool.MethodHandleEntry entry = (ConstantPool.MethodHandleEntry)this._constantPool.getEntry(token);
       ConstantPool.ReferenceEntry reference = (ConstantPool.ReferenceEntry)this._constantPool.getEntry(entry.referenceIndex);
       
       return new MethodHandle(lookupMethod(reference.typeInfoIndex, reference.nameAndTypeDescriptorIndex), ClassFileReader.METHOD_HANDLE_TYPES[entry.referenceKind.ordinal()]);
     }
     
 
 
 
     public IMethodSignature lookupMethodType(int token)
     {
       ConstantPool.MethodTypeEntry entry = (ConstantPool.MethodTypeEntry)this._constantPool.getEntry(token);
       return this._parser.parseMethodSignature(entry.getType());
     }
     
     public DynamicCallSite lookupDynamicCallSite(int token)
     {
       ConstantPool.InvokeDynamicInfoEntry entry = (ConstantPool.InvokeDynamicInfoEntry)this._constantPool.getEntry(token);
       BootstrapMethodsAttribute attribute = (BootstrapMethodsAttribute)SourceAttribute.find("BootstrapMethods", this._typeDefinition.getSourceAttributes());
       
       BootstrapMethodsTableEntry bootstrapMethod = (BootstrapMethodsTableEntry)attribute.getBootstrapMethods().get(entry.bootstrapMethodAttributeIndex);
       
 
       ConstantPool.NameAndTypeDescriptorEntry nameAndType = (ConstantPool.NameAndTypeDescriptorEntry)this._constantPool.getEntry(entry.nameAndTypeDescriptorIndex);
       
       return new DynamicCallSite(bootstrapMethod.getMethod(), bootstrapMethod.getArguments(), nameAndType.getName(), this._parser.parseMethodSignature(nameAndType.getType()));
     }
     
 
 
 
 
 
     public FieldReference lookupField(int typeToken, int nameAndTypeToken)
     {
       ConstantPool.NameAndTypeDescriptorEntry nameAndDescriptor = (ConstantPool.NameAndTypeDescriptorEntry)this._constantPool.getEntry(nameAndTypeToken);
       
       return this._parser.parseField(lookupType(typeToken), nameAndDescriptor.getName(), nameAndDescriptor.getType());
     }
     
 
 
 
 
     public MethodReference lookupMethod(int typeToken, int nameAndTypeToken)
     {
       ConstantPool.NameAndTypeDescriptorEntry nameAndDescriptor = (ConstantPool.NameAndTypeDescriptorEntry)this._constantPool.getEntry(nameAndTypeToken);
       
       return this._parser.parseMethod(lookupType(typeToken), nameAndDescriptor.getName(), nameAndDescriptor.getType());
     }
     
 
 
 
 
 
     public <T> T lookupConstant(int token)
     {
       ConstantPool.Entry entry = this._constantPool.get(token);
       
       if (entry.getTag() == ConstantPool.Tag.TypeInfo) {
         return (T)lookupType(token);
       }
       
       return (T)this._constantPool.lookupConstant(token);
     }
     
     public Object lookup(int token)
     {
       ConstantPool.Entry entry = this._constantPool.get(token);
       
       if (entry == null) {
         return null;
       }
       
       switch (entry.getTag()) {//TODO 反编译问题 此处为猜测
       case Utf8StringConstant: 
       case IntegerConstant: 
       case FloatConstant: 
       case LongConstant: 
       case DoubleConstant: 
       case StringConstant: 
         return lookupConstant(token);
       
       case TypeInfo: 
         return lookupType(token);
       
       case FieldReference: 
         return lookupField(token);
       
       case MethodReference: 
         return lookupMethod(token);
       
       case InterfaceMethodReference: 
         return lookupMethod(token);
       
       case MethodHandle: 
         return lookupMethodHandle(token);
       
       case MethodType: 
         return lookupMethodType(token);
       
       case InvokeDynamicInfo: 
         return lookupDynamicCallSite(token);
       }
       
       return null;
     }
   }
   
 
   private final class ResolverFrame
     implements IResolverFrame
   {
     private ResolverFrame() {}
     
     final HashMap<String, TypeReference> types = new HashMap();
     final HashMap<String, GenericParameter> typeVariables = new HashMap();
     
     public void addType(TypeReference type) {
       VerifyArgument.notNull(type, "type");
       this.types.put(type.getInternalName(), type);
     }
     
     public void addTypeVariable(GenericParameter type) {
       VerifyArgument.notNull(type, "type");
       this.typeVariables.put(type.getName(), type);
     }
     
     public void removeType(TypeReference type) {
       VerifyArgument.notNull(type, "type");
       this.types.remove(type.getInternalName());
     }
     
     public void removeTypeVariable(GenericParameter type) {
       VerifyArgument.notNull(type, "type");
       this.typeVariables.remove(type.getName());
     }
     
     public TypeReference findType(String descriptor)
     {
       TypeReference type = (TypeReference)this.types.get(descriptor);
       
       if (type != null) {
         return type;
       }
       
       return null;
     }
     
     public GenericParameter findTypeVariable(String name)
     {
       GenericParameter typeVariable = (GenericParameter)this.typeVariables.get(name);
       
       if (typeVariable != null) {
         return typeVariable;
       }
       
       for (String typeName : this.types.keySet()) {
         TypeReference t = (TypeReference)this.types.get(typeName);
         
         if (t.containsGenericParameters()) {
           for (GenericParameter p : t.getGenericParameters()) {
             if (StringUtilities.equals(p.getName(), name)) {
               return p;
             }
           }
         }
       }
       
       return null;
     }
   }
 }


