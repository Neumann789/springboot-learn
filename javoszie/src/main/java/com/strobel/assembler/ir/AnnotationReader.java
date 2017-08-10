 package com.strobel.assembler.ir;
 
 import com.strobel.assembler.metadata.Buffer;
 import com.strobel.assembler.metadata.IMetadataScope;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.assembler.metadata.annotations.AnnotationAnnotationElement;
 import com.strobel.assembler.metadata.annotations.AnnotationElement;
 import com.strobel.assembler.metadata.annotations.AnnotationElementType;
 import com.strobel.assembler.metadata.annotations.AnnotationParameter;
 import com.strobel.assembler.metadata.annotations.ArrayAnnotationElement;
 import com.strobel.assembler.metadata.annotations.ClassAnnotationElement;
 import com.strobel.assembler.metadata.annotations.ConstantAnnotationElement;
 import com.strobel.assembler.metadata.annotations.CustomAnnotation;
 import com.strobel.assembler.metadata.annotations.EnumAnnotationElement;
 import com.strobel.core.ArrayUtilities;
 import com.strobel.util.ContractUtils;
 
 
 
 
 
 
 
 
 
 
 public final class AnnotationReader
 {
   public static CustomAnnotation read(IMetadataScope scope, Buffer input)
   {
     int typeToken = input.readUnsignedShort();
     int parameterCount = input.readUnsignedShort();
     
     TypeReference annotationType = scope.lookupType(typeToken);
     AnnotationParameter[] parameters = new AnnotationParameter[parameterCount];
     
     readParameters(parameters, scope, input, true);
     
     return new CustomAnnotation(annotationType, ArrayUtilities.asUnmodifiableList(parameters));
   }
   
 
 
 
 
   private static void readParameters(AnnotationParameter[] parameters, IMetadataScope scope, Buffer input, boolean namedParameter)
   {
     for (int i = 0; i < parameters.length; i++)
     {
       parameters[i] = new AnnotationParameter(namedParameter ? (String)scope.lookupConstant(input.readUnsignedShort()) : "value", readElement(scope, input));
     }
   }
   
 
 
 
   public static AnnotationElement readElement(IMetadataScope scope, Buffer input)
   {
     char tag = (char)input.readUnsignedByte();
     AnnotationElementType elementType = AnnotationElementType.forTag(tag);
     
     switch (elementType) {
     case Constant: 
       Object constantValue = scope.lookupConstant(input.readUnsignedShort());
       
       switch (tag) {
       case 'B': 
         constantValue = Byte.valueOf(((Number)constantValue).byteValue());
         break;
       
       case 'C': 
         constantValue = Character.valueOf((char)((Number)constantValue).intValue());
         break;
       
       case 'S': 
         constantValue = Short.valueOf(((Number)constantValue).shortValue());
         break;
       
       case 'Z': 
         constantValue = ((Number)constantValue).intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
       }
       
       
       return new ConstantAnnotationElement(constantValue);
     
 
     case Enum: 
       TypeReference enumType = scope.lookupType(input.readUnsignedShort());
       String constantName = (String)scope.lookupConstant(input.readUnsignedShort());
       return new EnumAnnotationElement(enumType, constantName);
     
 
     case Array: 
       AnnotationElement[] elements = new AnnotationElement[input.readUnsignedShort()];
       
       for (int i = 0; i < elements.length; i++) {
         elements[i] = readElement(scope, input);
       }
       
       return new ArrayAnnotationElement(elements);
     
 
     case Class: 
       TypeReference type = scope.lookupType(input.readUnsignedShort());
       return new ClassAnnotationElement(type);
     
 
     case Annotation: 
       CustomAnnotation annotation = read(scope, input);
       return new AnnotationAnnotationElement(annotation);
     }
     
     
     throw ContractUtils.unreachable();
   }
 }


