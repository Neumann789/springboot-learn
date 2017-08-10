 package com.strobel.assembler.ir.attributes;
 
 import com.strobel.assembler.ir.AnnotationReader;
 import com.strobel.assembler.metadata.Buffer;
 import com.strobel.assembler.metadata.IMetadataResolver;
 import com.strobel.assembler.metadata.IMetadataScope;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.assembler.metadata.annotations.AnnotationElement;
 import com.strobel.assembler.metadata.annotations.CustomAnnotation;
 import com.strobel.core.VerifyArgument;
 import com.strobel.util.ContractUtils;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class SourceAttribute
 {
   private final String _name;
   private final int _length;
   
   public final String getName()
   {
     return this._name;
   }
   
   public final int getLength() {
     return this._length;
   }
   
   protected SourceAttribute(String name, int length) {
     this._name = name;
     this._length = length;
   }
   
   public static SourceAttribute create(String name) {
     return new SourceAttribute((String)VerifyArgument.notNull(name, "name"), 0);
   }
   
   public static <T extends SourceAttribute> T find(String name, SourceAttribute... attributes)
   {
     VerifyArgument.notNull(name, "name");
     VerifyArgument.noNullElements(attributes, "attributes");
     
     for (SourceAttribute attribute : attributes) {
       if (name.equals(attribute.getName())) {
         return attribute;
       }
     }
     
     return null;
   }
   
   public static <T extends SourceAttribute> T find(String name, List<SourceAttribute> attributes)
   {
     VerifyArgument.notNull(name, "name");
     VerifyArgument.noNullElements(attributes, "attributes");
     
     for (SourceAttribute attribute : attributes) {
       if (name.equals(attribute.getName())) {
         return attribute;
       }
     }
     
     return null;
   }
   
   public static void readAttributes(IMetadataResolver resolver, IMetadataScope scope, Buffer input, SourceAttribute[] attributes) {
     for (int i = 0; i < attributes.length; i++) {
       attributes[i] = readAttribute(resolver, scope, input);
     }
   }
   
   public static SourceAttribute readAttribute(IMetadataResolver resolver, IMetadataScope scope, Buffer buffer) {
     int nameIndex = buffer.readUnsignedShort();
     int length = buffer.readInt();
     String name = (String)scope.lookupConstant(nameIndex);
     
     if (length == 0) {
       return create(name);
     }
     
     switch (name) {
     case "SourceFile": 
       int token = buffer.readUnsignedShort();
       String sourceFile = (String)scope.lookupConstant(token);
       return new SourceFileAttribute(sourceFile);
     
 
     case "ConstantValue": 
       int token = buffer.readUnsignedShort();
       Object constantValue = scope.lookupConstant(token);
       return new ConstantValueAttribute(constantValue);
     
 
     case "Code": 
       int maxStack = buffer.readUnsignedShort();
       int maxLocals = buffer.readUnsignedShort();
       int codeOffset = buffer.position();
       int codeLength = buffer.readInt();
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
         TypeReference catchType;
         if (catchTypeToken == 0) {
           catchType = null;
         }
         else {
           catchType = resolver.lookupType((String)scope.lookupConstant(catchTypeToken));
         }
         
         exceptionTable[k] = new ExceptionTableEntry(startOffset, endOffset, handlerOffset, catchType);
       }
       
 
 
 
 
 
       int attributeCount = buffer.readUnsignedShort();
       SourceAttribute[] attributes = new SourceAttribute[attributeCount];
       
       readAttributes(resolver, scope, buffer, attributes);
       
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
       
 
 
 
       return new LineNumberTableAttribute(entries);
     
 
     case "LocalVariableTable": 
     case "LocalVariableTypeTable": 
       int entryCount = buffer.readUnsignedShort();
       LocalVariableTableEntry[] entries = new LocalVariableTableEntry[entryCount];
       
       for (int i = 0; i < entries.length; i++) {
         int scopeOffset = buffer.readUnsignedShort();
         int scopeLength = buffer.readUnsignedShort();
         String variableName = (String)scope.lookupConstant(buffer.readUnsignedShort());
         String descriptor = (String)scope.lookupConstant(buffer.readUnsignedShort());
         int variableIndex = buffer.readUnsignedShort();
         
         entries[i] = new LocalVariableTableEntry(variableIndex, variableName, resolver.lookupType(descriptor), scopeOffset, scopeLength);
       }
       
 
 
 
 
 
 
       return new LocalVariableTableAttribute(name, entries);
     
 
     case "EnclosingMethod": 
       int typeToken = buffer.readUnsignedShort();
       int methodToken = buffer.readUnsignedShort();
       
       return new EnclosingMethodAttribute(scope.lookupType(typeToken), methodToken > 0 ? scope.lookupMethod(typeToken, methodToken) : null);
     
 
 
 
 
 
     case "InnerClasses": 
       throw ContractUtils.unreachable();
     
 
     case "RuntimeVisibleAnnotations": 
     case "RuntimeInvisibleAnnotations": 
       CustomAnnotation[] annotations = new CustomAnnotation[buffer.readUnsignedShort()];
       
       for (int i = 0; i < annotations.length; i++) {
         annotations[i] = AnnotationReader.read(scope, buffer);
       }
       
       return new AnnotationsAttribute(name, length, annotations);
     
 
     case "RuntimeVisibleParameterAnnotations": 
     case "RuntimeInvisibleParameterAnnotations": 
       CustomAnnotation[][] annotations = new CustomAnnotation[buffer.readUnsignedShort()][];
       
       for (int i = 0; i < annotations.length; i++) {
         CustomAnnotation[] parameterAnnotations = new CustomAnnotation[buffer.readUnsignedShort()];
         
         for (int j = 0; j < parameterAnnotations.length; j++) {
           parameterAnnotations[j] = AnnotationReader.read(scope, buffer);
         }
         
         annotations[i] = parameterAnnotations;
       }
       
       return new ParameterAnnotationsAttribute(name, length, annotations);
     
 
     case "AnnotationDefault": 
       AnnotationElement defaultValue = AnnotationReader.readElement(scope, buffer);
       return new AnnotationDefaultAttribute(length, defaultValue);
     
 
     case "Signature": 
       int token = buffer.readUnsignedShort();
       String signature = (String)scope.lookupConstant(token);
       return new SignatureAttribute(signature);
     }
     
     
     int offset = buffer.position();
     byte[] blob = new byte[length];
     buffer.read(blob, 0, blob.length);
     return new BlobAttribute(name, blob, offset);
   }
 }


