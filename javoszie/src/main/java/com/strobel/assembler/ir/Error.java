 package com.strobel.assembler.ir;
 
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.TypeReference;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class Error
 {
   public static RuntimeException notGenericParameter(TypeReference type)
   {
     return new UnsupportedOperationException(String.format("TypeReference '%s' is not a generic parameter.", new Object[] { type.getFullName() }));
   }
   
 
 
 
 
   public static RuntimeException notWildcard(TypeReference type)
   {
     throw new UnsupportedOperationException(String.format("TypeReference '%s' is not a wildcard or captured type.", new Object[] { type.getFullName() }));
   }
   
 
 
 
 
   public static RuntimeException notBoundedType(TypeReference type)
   {
     throw new UnsupportedOperationException(String.format("TypeReference '%s' is not a bounded type.", new Object[] { type.getFullName() }));
   }
   
 
 
 
 
   public static RuntimeException notGenericType(TypeReference type)
   {
     return new UnsupportedOperationException(String.format("TypeReference '%s' is not a generic type.", new Object[] { type.getFullName() }));
   }
   
 
 
 
 
   public static RuntimeException notGenericMethod(MethodReference method)
   {
     return new UnsupportedOperationException(String.format("TypeReference '%s' is not a generic method.", new Object[] { method.getName() }));
   }
   
 
 
 
 
   public static RuntimeException notGenericMethodDefinition(MethodReference method)
   {
     return new UnsupportedOperationException(String.format("TypeReference '%s' is not a generic method definition.", new Object[] { method.getName() }));
   }
   
 
 
 
 
   public static RuntimeException noElementType(TypeReference type)
   {
     return new UnsupportedOperationException(String.format("TypeReference '%s' does not have an element type.", new Object[] { type.getFullName() }));
   }
   
 
 
 
 
   public static RuntimeException notEnumType(TypeReference type)
   {
     return new UnsupportedOperationException(String.format("TypeReference '%s' is not an enum type.", new Object[] { type.getFullName() }));
   }
   
 
 
 
 
   public static RuntimeException notArrayType(TypeReference type)
   {
     return new UnsupportedOperationException(String.format("TypeReference '%s' is not an array type.", new Object[] { type.getFullName() }));
   }
   
 
 
 
 
   public static RuntimeException invalidSignatureTypeExpected(String signature, int position)
   {
     return new IllegalArgumentException(String.format("Invalid signature: type expected at position %d (%s).", new Object[] { Integer.valueOf(position), signature }));
   }
   
 
 
 
 
 
   public static RuntimeException invalidSignatureTopLevelGenericParameterUnexpected(String signature, int position)
   {
     return new IllegalArgumentException(String.format("Invalid signature: unexpected generic parameter at position %d.  (%s)", new Object[] { Integer.valueOf(position), signature }));
   }
   
 
 
 
 
 
   public static RuntimeException invalidSignatureNonGenericTypeTypeArguments(TypeReference type)
   {
     return new IllegalArgumentException(String.format("Invalid signature: unexpected type arguments specified for non-generic type '%s'.", new Object[] { type.getBriefDescription() }));
   }
   
 
 
 
 
   public static RuntimeException invalidSignatureUnexpectedToken(String signature, int position)
   {
     return new IllegalArgumentException(String.format("Invalid signature: unexpected token at position %d.  (%s)", new Object[] { Integer.valueOf(position), signature }));
   }
   
 
 
 
 
 
   public static RuntimeException invalidSignatureUnexpectedEnd(String signature, int position)
   {
     return new IllegalArgumentException(String.format("Invalid signature: unexpected end of signature at position %d.  (%s)", new Object[] { Integer.valueOf(position), signature }));
   }
   
 
 
 
 
 
   public static RuntimeException invalidSignatureExpectedEndOfTypeArguments(String signature, int position)
   {
     return new IllegalArgumentException(String.format("Invalid signature: expected end of type argument list at position %d.  (%s)", new Object[] { Integer.valueOf(position), signature }));
   }
   
 
 
 
 
 
   public static RuntimeException invalidSignatureExpectedEndOfTypeVariables(String signature, int position)
   {
     return new IllegalArgumentException(String.format("Invalid signature: expected end of type variable list at position %d.  (%s)", new Object[] { Integer.valueOf(position), signature }));
   }
   
 
 
 
 
 
   public static RuntimeException invalidSignatureExpectedTypeArgument(String signature, int position)
   {
     return new IllegalArgumentException(String.format("Invalid signature: expected type argument at position %d.  (%s)", new Object[] { Integer.valueOf(position), signature }));
   }
   
 
 
 
 
 
   public static RuntimeException invalidSignatureExpectedParameterList(String signature, int position)
   {
     return new IllegalArgumentException(String.format("Invalid signature: expected parameter type list at position %d.  (%s)", new Object[] { Integer.valueOf(position), signature }));
   }
   
 
 
 
 
 
   public static RuntimeException invalidSignatureExpectedReturnType(String signature, int position)
   {
     return new IllegalArgumentException(String.format("Invalid signature: expected return type at position %d.  (%s)", new Object[] { Integer.valueOf(position), signature }));
   }
   
 
 
 
 
 
   public static RuntimeException invalidSignatureExpectedTypeVariable(String signature, int position)
   {
     return new IllegalArgumentException(String.format("Invalid signature: expected type variable name at position %d.  (%s)", new Object[] { Integer.valueOf(position), signature }));
   }
   
 
 
 
 
 
   public static RuntimeException stackMapperCalledWithUnexpandedFrame(FrameType frameType)
   {
     throw new IllegalStateException(String.format("StackMappingVisitor.visitFrame() was called with an unexpanded frame (%s).", new Object[] { frameType.name() }));
   }
   
 
 
 
 
 
 
 
 
   public static RuntimeException invalidBootstrapMethodEntry(MethodReference bootstrapMethod, int parameterCount, int argumentCount)
   {
     if (argumentCount > parameterCount + 3) {
       return new IllegalStateException(String.format("Invalid BootstrapMethods attribute entry: %d too many arguments specifiedfor method %s.", new Object[] { Integer.valueOf(argumentCount - (parameterCount + 3)), bootstrapMethod.getFullName() }));
     }
     
 
 
 
 
 
 
 
     return new IllegalStateException(String.format("Invalid BootstrapMethods attribute entry: %d additional arguments required for method %s, but only %d specified.", new Object[] { Integer.valueOf(parameterCount - 3), bootstrapMethod.getFullName(), Integer.valueOf(argumentCount) }));
   }
 }


