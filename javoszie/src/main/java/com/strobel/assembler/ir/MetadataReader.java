 package com.strobel.assembler.ir;
 
 import com.strobel.assembler.ir.attributes.AnnotationDefaultAttribute;
 import com.strobel.assembler.ir.attributes.AnnotationsAttribute;
 import com.strobel.assembler.ir.attributes.BlobAttribute;
 import com.strobel.assembler.ir.attributes.BootstrapMethodsAttribute;
 import com.strobel.assembler.ir.attributes.BootstrapMethodsTableEntry;
 import com.strobel.assembler.ir.attributes.CodeAttribute;
 import com.strobel.assembler.ir.attributes.ExceptionTableEntry;
 import com.strobel.assembler.ir.attributes.ExceptionsAttribute;
 import com.strobel.assembler.ir.attributes.LineNumberTableEntry;
 import com.strobel.assembler.ir.attributes.LocalVariableTableEntry;
 import com.strobel.assembler.ir.attributes.MethodParameterEntry;
 import com.strobel.assembler.ir.attributes.MethodParametersAttribute;
 import com.strobel.assembler.ir.attributes.ParameterAnnotationsAttribute;
 import com.strobel.assembler.ir.attributes.SignatureAttribute;
 import com.strobel.assembler.ir.attributes.SourceAttribute;
 import com.strobel.assembler.ir.attributes.SourceFileAttribute;
 import com.strobel.assembler.metadata.Buffer;
 import com.strobel.assembler.metadata.IMetadataScope;
 import com.strobel.assembler.metadata.MetadataParser;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.assembler.metadata.annotations.AnnotationElement;
 import com.strobel.assembler.metadata.annotations.CustomAnnotation;
 import com.strobel.core.VerifyArgument;
 import java.util.List;
 
 public abstract class MetadataReader
 {
   protected abstract IMetadataScope getScope();
   
   protected abstract MetadataParser getParser();
   
   public void readAttributes(Buffer input, SourceAttribute[] attributes)
   {
     for (int i = 0; i < attributes.length; i++) {
       attributes[i] = readAttribute(input);
     }
   }
   
   public SourceAttribute readAttribute(Buffer buffer) {
     int nameIndex = buffer.readUnsignedShort();
     int length = buffer.readInt();
     IMetadataScope scope = getScope();
     String name = (String)scope.lookupConstant(nameIndex);
     
     return readAttributeCore(name, buffer, -1, length);
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   protected SourceAttribute readAttributeCore(String name, Buffer buffer, int originalOffset, int length)
   {
     IMetadataScope scope = getScope();
     
     if (length == 0) {
       return SourceAttribute.create(name);
     }
     int token;
     switch (name) {
     case "SourceFile": 
       token = buffer.readUnsignedShort();
       String sourceFile = (String)scope.lookupConstant(token);
       return new SourceFileAttribute(sourceFile);
     
 
     case "ConstantValue": 
       token = buffer.readUnsignedShort();
       Object constantValue = scope.lookupConstant(token);
       return new com.strobel.assembler.ir.attributes.ConstantValueAttribute(constantValue);
     
 
     case "Code": 
       int maxStack = buffer.readUnsignedShort();
       int maxLocals = buffer.readUnsignedShort();
       int codeLength = buffer.readInt();
       int relativeOffset = buffer.position();
       int codeOffset = originalOffset >= 0 ? originalOffset - 2 + relativeOffset : relativeOffset;
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
           catchType = scope.lookupType(catchTypeToken);
         }
         
         exceptionTable[k] = new ExceptionTableEntry(startOffset, endOffset, handlerOffset, catchType);
       }
       
 
 
 
 
 
       int attributeCount = buffer.readUnsignedShort();
       SourceAttribute[] attributes = new SourceAttribute[attributeCount];
       
       readAttributes(buffer, attributes);
       
       return new CodeAttribute(length, maxStack, maxLocals, codeOffset, codeLength, buffer, exceptionTable, attributes);
     
 
 
 
 
 
 
 
 
 
 
     case "Exceptions": 
       int exceptionCount = buffer.readUnsignedShort();
       TypeReference[] exceptionTypes = new TypeReference[exceptionCount];
       
       for (int i = 0; i < exceptionTypes.length; i++) {
         exceptionTypes[i] = scope.lookupType(buffer.readUnsignedShort());
       }
       
       return new ExceptionsAttribute(exceptionTypes);
     
 
     case "LineNumberTable": 
       int entryCount = buffer.readUnsignedShort();
       LineNumberTableEntry[] entries = new LineNumberTableEntry[entryCount];
       
       for (int i = 0; i < entries.length; i++) {
         entries[i] = new LineNumberTableEntry(buffer.readUnsignedShort(), buffer.readUnsignedShort());
       }
       
 
 
 
       return new com.strobel.assembler.ir.attributes.LineNumberTableAttribute(entries);
     
 
     case "LocalVariableTable": 
     case "LocalVariableTypeTable": 
       int entryCount2 = buffer.readUnsignedShort();
       LocalVariableTableEntry[] localVariableTableEntry = new LocalVariableTableEntry[entryCount2];
       
       for (int i = 0; i < localVariableTableEntry.length; i++) {
         int scopeOffset = buffer.readUnsignedShort();
         int scopeLength = buffer.readUnsignedShort();
         int nameToken = buffer.readUnsignedShort();
         int typeToken = buffer.readUnsignedShort();
         int variableIndex = buffer.readUnsignedShort();
         String variableName = (String)scope.lookupConstant(nameToken);
         String descriptor = (String)scope.lookupConstant(typeToken);
         
         localVariableTableEntry[i] = new LocalVariableTableEntry(variableIndex, variableName, getParser().parseTypeSignature(descriptor), scopeOffset, scopeLength);
       }
 
       return new com.strobel.assembler.ir.attributes.LocalVariableTableAttribute(name, localVariableTableEntry);
     
 
     case "EnclosingMethod": 
       int typeToken = buffer.readUnsignedShort();
       int methodToken = buffer.readUnsignedShort();
       
       return new com.strobel.assembler.ir.attributes.EnclosingMethodAttribute(scope.lookupType(typeToken), methodToken > 0 ? scope.lookupMethod(typeToken, methodToken) : null);
     
 
 
 
 
 
     case "RuntimeVisibleAnnotations": 
     case "RuntimeInvisibleAnnotations": 
       CustomAnnotation[] annotations = new CustomAnnotation[buffer.readUnsignedShort()];
       
       for (int i = 0; i < annotations.length; i++) {
         annotations[i] = AnnotationReader.read(scope, buffer);
       }
       
       return new AnnotationsAttribute(name, length, annotations);
     
 
     case "RuntimeVisibleParameterAnnotations": 
     case "RuntimeInvisibleParameterAnnotations": 
       CustomAnnotation[][] annotations2 = new CustomAnnotation[buffer.readUnsignedByte()][];
       
       for (int i = 0; i < annotations2.length; i++) {
         CustomAnnotation[] parameterAnnotations = new CustomAnnotation[buffer.readUnsignedShort()];
         
         for (int j = 0; j < parameterAnnotations.length; j++) {
           parameterAnnotations[j] = AnnotationReader.read(scope, buffer);
         }
         
         annotations2[i] = parameterAnnotations;
       }
       
       return new ParameterAnnotationsAttribute(name, length, annotations2);
     
 
     case "AnnotationDefault": 
       AnnotationElement defaultValue = AnnotationReader.readElement(scope, buffer);
       return new AnnotationDefaultAttribute(length, defaultValue);
     
 
     case "Signature": 
       int token2 = buffer.readUnsignedShort();
       String signature = (String)scope.lookupConstant(token2);
       return new SignatureAttribute(signature);
     
 
     case "BootstrapMethods": 
       BootstrapMethodsTableEntry[] methods = new BootstrapMethodsTableEntry[buffer.readUnsignedShort()];
       
       for (int i = 0; i < methods.length; i++) {
         MethodReference bootstrapMethod = scope.lookupMethod(buffer.readUnsignedShort());
         Object[] arguments = new Object[buffer.readUnsignedShort()];
         List<ParameterDefinition> parameters = bootstrapMethod.getParameters();
         
         if (parameters.size() != arguments.length + 3) {
           MethodDefinition resolved = bootstrapMethod.resolve();
           
           if ((resolved == null) || (!resolved.isVarArgs()) || (parameters.size() >= arguments.length + 3)) {
             throw Error.invalidBootstrapMethodEntry(bootstrapMethod, parameters.size(), arguments.length);
           }
         }
         
         for (int j = 0; j < arguments.length; j++)
         {
           int token3= buffer.readUnsignedShort();
           int parameterIndex = j + 3;
           TypeReference parameterType;
           if (parameterIndex < parameters.size()) {
             parameterType = ((ParameterDefinition)parameters.get(parameterIndex)).getParameterType();
           }
           else {
             parameterType = com.strobel.assembler.metadata.BuiltinTypes.Object;
           }
           
           switch (parameterType.getInternalName()) {
           case "java/lang/invoke/MethodHandle": 
             arguments[j] = scope.lookupMethodHandle(token3);
             break;
           
           case "java/lang/invoke/MethodType": 
             arguments[j] = scope.lookupMethodType(token3);
             break;
           
           default: 
             arguments[j] = scope.lookup(token3);
           }
           
         }
         
         methods[i] = new BootstrapMethodsTableEntry(bootstrapMethod, arguments);
       }
       
       return new BootstrapMethodsAttribute(methods);
     
 
     case "MethodParameters": 
       int methodParameterCount = buffer.readUnsignedByte();
       int computedCount = (length - 1) / 4;
       MethodParameterEntry[] methodParameterEntry = new MethodParameterEntry[methodParameterCount];
       
       for (int i = 0; i < methodParameterEntry.length; i++) {
         int flags;
         int nameIndex;
         if (i < computedCount) {
           nameIndex = buffer.readUnsignedShort();
           flags = buffer.readUnsignedShort();
         }
         else {
           nameIndex = 0;
           flags = 0;
         }
         
         methodParameterEntry[i] = new MethodParameterEntry(nameIndex != 0 ? (String)getScope().lookupConstant(nameIndex) : null, flags);
       }
       
       return new MethodParametersAttribute(com.strobel.core.ArrayUtilities.asUnmodifiableList(methodParameterEntry));
     }
     
     
     byte[] blob = new byte[length];
     int offset = buffer.position();
     buffer.read(blob, 0, blob.length);
     return new BlobAttribute(name, blob, offset);
   }
   
 
   protected void inflateAttributes(SourceAttribute[] attributes)
   {
     VerifyArgument.noNullElements(attributes, "attributes");
     
     if (attributes.length == 0) {
       return;
     }
     
     Buffer buffer = null;
     
     for (int i = 0; i < attributes.length; i++) {
       SourceAttribute attribute = attributes[i];
       
       if ((attribute instanceof BlobAttribute)) {
         if (buffer == null) {
           buffer = new Buffer(attribute.getLength());
         }
         
         attributes[i] = inflateAttribute(buffer, attribute);
       }
     }
   }
   
   protected final SourceAttribute inflateAttribute(SourceAttribute attribute) {
     return inflateAttribute(new Buffer(0), attribute);
   }
   
   protected final SourceAttribute inflateAttribute(Buffer buffer, SourceAttribute attribute) {
     if ((attribute instanceof BlobAttribute)) {
       buffer.reset(attribute.getLength());
       
       BlobAttribute blobAttribute = (BlobAttribute)attribute;
       
       System.arraycopy(blobAttribute.getData(), 0, buffer.array(), 0, attribute.getLength());
       
 
 
 
 
 
 
       return readAttributeCore(attribute.getName(), buffer, blobAttribute.getDataOffset(), attribute.getLength());
     }
     
 
 
 
 
 
     return attribute;
   }
   
   protected void inflateAttributes(List<SourceAttribute> attributes) {
     VerifyArgument.noNullElements(attributes, "attributes");
     
     if (attributes.isEmpty()) {
       return;
     }
     
     Buffer buffer = null;
     
     for (int i = 0; i < attributes.size(); i++) {
       SourceAttribute attribute = (SourceAttribute)attributes.get(i);
       
       if ((attribute instanceof BlobAttribute)) {
         if (buffer == null) {
           buffer = new Buffer(attribute.getLength());
         }
         else if (buffer.size() < attribute.getLength()) {
           buffer.reset(attribute.getLength());
         }
         else {
           buffer.position(0);
         }
         
         BlobAttribute blobAttribute = (BlobAttribute)attribute;
         
         System.arraycopy(blobAttribute.getData(), 0, buffer.array(), 0, attribute.getLength());
         
 
 
 
 
 
 
         attributes.set(i, readAttributeCore(attribute.getName(), buffer, blobAttribute.getDataOffset(), attribute.getLength()));
       }
     }
   }
 }


