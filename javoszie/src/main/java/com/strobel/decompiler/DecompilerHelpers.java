 package com.strobel.decompiler;
 
 import com.strobel.assembler.ir.ExceptionHandler;
 import com.strobel.assembler.ir.Frame;
 import com.strobel.assembler.ir.FrameType;
 import com.strobel.assembler.ir.FrameValue;
 import com.strobel.assembler.ir.FrameValueType;
 import com.strobel.assembler.ir.Instruction;
 import com.strobel.assembler.ir.InstructionBlock;
 import com.strobel.assembler.ir.OpCode;
 import com.strobel.assembler.metadata.BuiltinTypes;
 import com.strobel.assembler.metadata.CompoundTypeReference;
 import com.strobel.assembler.metadata.DynamicCallSite;
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.GenericParameter;
 import com.strobel.assembler.metadata.IGenericInstance;
 import com.strobel.assembler.metadata.IMethodSignature;
 import com.strobel.assembler.metadata.MetadataSystem;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.ParameterReference;
 import com.strobel.assembler.metadata.SwitchInfo;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.assembler.metadata.VariableReference;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.ast.Variable;
 import java.util.List;
 import java.util.Stack;
 
 
 
 public final class DecompilerHelpers
 {
   public static void writeType(ITextOutput writer, TypeReference type)
   {
     writeType(writer, type, NameSyntax.SIGNATURE);
   }
   
   public static void writeGenericSignature(ITextOutput writer, TypeReference type) {
     formatGenericSignature(writer, type, new Stack());
   }
   
   public static void writeType(ITextOutput writer, TypeReference type, NameSyntax syntax) {
     VerifyArgument.notNull(type, "type");
     VerifyArgument.notNull(writer, "writer");
     VerifyArgument.notNull(syntax, "syntax");
     
     formatType(writer, type, syntax, type.isDefinition(), new Stack());
   }
   
 
 
 
 
   public static void writeType(ITextOutput writer, TypeReference type, NameSyntax syntax, boolean isDefinition)
   {
     VerifyArgument.notNull(type, "type");
     VerifyArgument.notNull(writer, "writer");
     VerifyArgument.notNull(syntax, "syntax");
     
     formatType(writer, type, syntax, isDefinition, new Stack());
   }
   
   public static void writeMethod(ITextOutput writer, MethodReference method) {
     VerifyArgument.notNull(method, "method");
     VerifyArgument.notNull(writer, "writer");
     
     Stack<TypeReference> typeStack = new Stack();
     
     formatType(writer, method.getDeclaringType(), NameSyntax.DESCRIPTOR, false, typeStack);
     writer.writeDelimiter(".");
     writer.writeReference(method.getName(), method);
     writer.writeDelimiter(":");
     formatMethodSignature(writer, method, typeStack);
   }
   
   public static void writeMethodSignature(ITextOutput writer, IMethodSignature signature) {
     VerifyArgument.notNull(signature, "signature");
     VerifyArgument.notNull(writer, "writer");
     
     Stack<TypeReference> typeStack = new Stack();
     
     formatMethodSignature(writer, signature, typeStack);
   }
   
   public static void writeField(ITextOutput writer, FieldReference field) {
     VerifyArgument.notNull(field, "field");
     VerifyArgument.notNull(writer, "writer");
     
     Stack<TypeReference> typeStack = new Stack();
     
     formatType(writer, field.getDeclaringType(), NameSyntax.DESCRIPTOR, false, typeStack);
     writer.writeDelimiter(".");
     writer.writeReference(field.getName(), field);
     writer.writeDelimiter(":");
     formatType(writer, field.getFieldType(), NameSyntax.SIGNATURE, false, typeStack);
   }
   
   public static void writeOperand(ITextOutput writer, Object operand) {
     writeOperand(writer, operand, false);
   }
   
   public static void writeOperand(ITextOutput writer, Object operand, boolean isUnicodeSupported) {
     VerifyArgument.notNull(writer, "writer");
     VerifyArgument.notNull(operand, "operand");
     
     if ((operand instanceof Instruction)) {
       Instruction targetInstruction = (Instruction)operand;
       writeOffsetReference(writer, targetInstruction);
       return;
     }
     
     if ((operand instanceof Instruction[])) {
       Instruction[] targetInstructions = (Instruction[])operand;
       writeLabelList(writer, targetInstructions);
       return;
     }
     
     if ((operand instanceof SwitchInfo)) {
       SwitchInfo switchInfo = (SwitchInfo)operand;
       
       writer.write('[');
       writeOffsetReference(writer, switchInfo.getDefaultTarget());
       
       for (Instruction target : switchInfo.getTargets()) {
         writer.write(", ");
         writeOffsetReference(writer, target);
       }
       
       writer.write(']');
       return;
     }
     
     if ((operand instanceof VariableReference)) {
       VariableReference variable = (VariableReference)operand;
       
       if (variable.hasName()) {
         writer.writeReference(escapeIdentifier(variable.getName()), variable);
       }
       else {
         writer.writeReference("$" + String.valueOf(variable.getSlot()), variable);
       }
       
       return;
     }
     
     if ((operand instanceof ParameterReference)) {
       ParameterReference parameter = (ParameterReference)operand;
       String parameterName = parameter.getName();
       
       if (StringUtilities.isNullOrEmpty(parameterName)) {
         writer.writeReference(String.valueOf(parameter.getPosition()), parameter);
       }
       else {
         writer.writeReference(escapeIdentifier(parameterName), parameter);
       }
       
       return;
     }
     
     if ((operand instanceof Variable)) {
       Variable variable = (Variable)operand;
       
       if (variable.isParameter()) {
         writer.writeReference(variable.getName(), variable.getOriginalParameter());
       }
       else {
         writer.writeReference(variable.getName(), variable.getOriginalVariable());
       }
       
       return;
     }
     
     if ((operand instanceof MethodReference)) {
       writeMethod(writer, (MethodReference)operand);
       return;
     }
     
     if ((operand instanceof TypeReference)) {
       writeType(writer, (TypeReference)operand, NameSyntax.TYPE_NAME);
       writer.write('.');
       writer.writeKeyword("class");
       return;
     }
     
     if ((operand instanceof FieldReference)) {
       writeField(writer, (FieldReference)operand);
       return;
     }
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     if ((operand instanceof DynamicCallSite)) {
       writeDynamicCallSite(writer, (DynamicCallSite)operand);
       return;
     }
     
     writePrimitiveValue(writer, operand);
   }
   
   public static void writeDynamicCallSite(ITextOutput output, DynamicCallSite operand) {
     output.writeReference(operand.getMethodName(), operand.getMethodType());
     output.writeDelimiter(":");
     writeMethodSignature(output, operand.getMethodType());
   }
   
   public static String offsetToString(int offset) {
     return String.format("#%1$04d", new Object[] { Integer.valueOf(offset) });
   }
   
   public static void writeExceptionHandler(ITextOutput output, ExceptionHandler handler) {
     VerifyArgument.notNull(output, "output");
     VerifyArgument.notNull(handler, "handler");
     
     output.write("Try ");
     writeOffsetReference(output, handler.getTryBlock().getFirstInstruction());
     output.write(" - ");
     writeEndOffsetReference(output, handler.getTryBlock().getLastInstruction());
     output.write(' ');
     output.write(String.valueOf(handler.getHandlerType()));
     
     TypeReference catchType = handler.getCatchType();
     
     if (catchType != null) {
       output.write(' ');
       writeType(output, catchType);
     }
     
     InstructionBlock handlerBlock = handler.getHandlerBlock();
     
     output.write(' ');
     writeOffsetReference(output, handlerBlock.getFirstInstruction());
     
     if (handlerBlock.getLastInstruction() != null) {
       output.write(" - ");
       writeEndOffsetReference(output, handlerBlock.getLastInstruction());
     }
   }
   
   public static void writeInstruction(ITextOutput writer, Instruction instruction) {
     VerifyArgument.notNull(writer, "writer");
     VerifyArgument.notNull(instruction, "instruction");
     
     writer.writeDefinition(offsetToString(instruction.getOffset()), instruction);
     writer.write(": ");
     writer.writeReference(instruction.getOpCode().name(), instruction.getOpCode());
     
     if (instruction.hasOperand()) {
       writer.write(' ');
       writeOperandList(writer, instruction);
     }
   }
   
   public static void writeOffsetReference(ITextOutput writer, Instruction instruction) {
     VerifyArgument.notNull(writer, "writer");
     
     writer.writeLabel(offsetToString(instruction.getOffset()));
   }
   
   public static void writeEndOffsetReference(ITextOutput writer, Instruction instruction) {
     VerifyArgument.notNull(writer, "writer");
     
     writer.writeLabel(offsetToString(instruction.getEndOffset()));
   }
   
   public static String escapeIdentifier(String name) {
     VerifyArgument.notNull(name, "name");
     
     StringBuilder sb = null;
     
     int i = 0; for (int n = name.length(); i < n; i++) {
       char ch = name.charAt(i);
       
       if (i == 0) {
         if (!Character.isJavaIdentifierStart(ch))
         {
 
           sb = new StringBuilder(name.length() * 2);
           sb.append(String.format("\\u%1$04x", new Object[] { Integer.valueOf(ch) }));
         }
       } else if (Character.isJavaIdentifierPart(ch)) {
         if (sb != null) {
           sb.append(ch);
         }
       }
       else {
         if (sb == null) {
           sb = new StringBuilder(name.length() * 2);
         }
         sb.append(String.format("\\u%1$04x", new Object[] { Integer.valueOf(ch) }));
       }
     }
     
     if (sb != null) {
       return sb.toString();
     }
     
     return name;
   }
   
   public static void writeFrame(ITextOutput writer, Frame frame) {
     VerifyArgument.notNull(writer, "writer");
     VerifyArgument.notNull(frame, "frame");
     
     FrameType frameType = frame.getFrameType();
     
     writer.writeLiteral(String.valueOf(frameType));
     
     List<FrameValue> localValues = frame.getLocalValues();
     List<FrameValue> stackValues = frame.getStackValues();
     
     if (!localValues.isEmpty()) {
       writer.writeLine();
       writer.indent();
       writer.write("Locals: ");
       writer.writeDelimiter("[");
       
       for (int i = 0; i < localValues.size(); i++) {
         FrameValue value = (FrameValue)localValues.get(i);
         
         if (i != 0) {
           writer.writeDelimiter(", ");
         }
         
         if (value.getType() == FrameValueType.Reference) {
           writer.writeLiteral("Reference");
           writer.writeDelimiter("(");
           writeType(writer, (TypeReference)value.getParameter(), NameSyntax.SIGNATURE);
           writer.writeDelimiter(")");
         }
         else {
           writer.writeLiteral(String.valueOf(value.getType()));
         }
       }
       
       writer.writeDelimiter("]");
       writer.unindent();
     }
     
     if (!stackValues.isEmpty()) {
       writer.writeLine();
       writer.indent();
       writer.write("Stack: ");
       writer.writeDelimiter("[");
       
       for (int i = 0; i < stackValues.size(); i++) {
         FrameValue value = (FrameValue)stackValues.get(i);
         
         if (i != 0) {
           writer.writeDelimiter(", ");
         }
         
         if (value.getType() == FrameValueType.Reference) {
           writer.writeLiteral("Reference");
           writer.writeDelimiter("(");
           writeType(writer, (TypeReference)value.getParameter(), NameSyntax.SIGNATURE);
           writer.writeDelimiter(")");
         }
         else {
           writer.writeLiteral(String.valueOf(value.getType()));
         }
       }
       
       writer.writeDelimiter("]");
       writer.unindent();
     }
   }
   
   private static void writeLabelList(ITextOutput writer, Instruction[] instructions) {
     writer.write('(');
     
     for (int i = 0; i < instructions.length; i++) {
       if (i != 0) {
         writer.write(", ");
       }
       writeOffsetReference(writer, instructions[i]);
     }
     
     writer.write(')');
   }
   
   private static void writeOperandList(ITextOutput writer, Instruction instruction) {
     int i = 0; for (int n = instruction.getOperandCount(); i < n; i++) {
       if (i != 0) {
         writer.write(", ");
       }
       writeOperand(writer, instruction.getOperand(i));
     }
   }
   
 
 
 
   private static void formatMethodSignature(ITextOutput writer, IMethodSignature signature, Stack<TypeReference> typeStack)
   {
     if (signature.isGenericDefinition()) {
       List<GenericParameter> genericParameters = signature.getGenericParameters();
       int count = genericParameters.size();
       
       if (count > 0) {
         writer.writeDelimiter("<");
         for (int i = 0; i < count; i++) {
           formatGenericSignature(writer, (TypeReference)genericParameters.get(i), typeStack);
         }
         writer.writeDelimiter(">");
       }
     }
     
     List<ParameterDefinition> parameters = signature.getParameters();
     
     writer.writeDelimiter("(");
     
     int i = 0; for (int n = parameters.size(); i < n; i++) {
       ParameterDefinition p = (ParameterDefinition)parameters.get(i);
       if (!p.isSynthetic())
       {
 
         formatType(writer, p.getParameterType(), NameSyntax.SIGNATURE, false, typeStack);
       }
     }
     writer.writeDelimiter(")");
     
     formatType(writer, signature.getReturnType(), NameSyntax.SIGNATURE, false, typeStack);
   }
   
 
 
 
 
 
 
   private static void formatType(ITextOutput writer, TypeReference type, NameSyntax syntax, boolean isDefinition, Stack<TypeReference> stack)
   {
     if (type.isGenericParameter()) {
       switch (syntax) {
       case SIGNATURE: 
       case ERASED_SIGNATURE: 
       case DESCRIPTOR: 
         writer.writeDelimiter("T");
         writer.writeReference(type.getSimpleName(), type);
         writer.writeDelimiter(";");
         return;
       }
       
       
       writer.writeReference(type.getName(), type);
       
       if ((isDefinition) && (type.hasExtendsBound()) && (!stack.contains(type.getExtendsBound())) && (!BuiltinTypes.Object.equals(type.getExtendsBound())))
       {
 
 
 
         writer.writeKeyword(" extends ");
         stack.push(type);
         try
         {
           formatType(writer, type.getExtendsBound(), syntax, false, stack);
         }
         finally {
           stack.pop();
         }
       }
       
       return;
     }
     
 
 
     if (type.isWildcardType()) {
       switch (syntax) {
       case DESCRIPTOR: 
         formatType(writer, type.getExtendsBound(), syntax, false, stack);
         return;
       
 
       case SIGNATURE: 
       case ERASED_SIGNATURE: 
         if (type.hasSuperBound()) {
           writer.write('-');
           formatType(writer, type.getSuperBound(), syntax, false, stack);
         }
         else if (type.hasExtendsBound()) {
           writer.write('+');
           formatType(writer, type.getExtendsBound(), syntax, false, stack);
         }
         else {
           writer.write('*');
         }
         return;
       }
       
       
       writer.write("?");
       
       if (type.hasSuperBound()) {
         writer.writeKeyword(" super ");
         formatType(writer, type.getSuperBound(), syntax, false, stack);
       }
       else if (type.hasExtendsBound()) {
         writer.writeKeyword(" extends ");
         formatType(writer, type.getExtendsBound(), syntax, false, stack);
       }
       
       return;
     }
     
 
 
     if ((type instanceof CompoundTypeReference)) {
       CompoundTypeReference compoundType = (CompoundTypeReference)type;
       TypeReference baseType = compoundType.getBaseType();
       List<TypeReference> interfaces = compoundType.getInterfaces();
       boolean first;
       switch (syntax) {
       case SIGNATURE: 
         if (baseType != null) {
           formatType(writer, baseType, syntax, false, stack);
         }
         
         for (TypeReference interfaceType : interfaces) {
           writer.writeDelimiter(":");
           formatType(writer, interfaceType, syntax, false, stack);
         }
         
         break;
       case ERASED_SIGNATURE: 
       case DESCRIPTOR: 
         TypeReference erasedType;
         
         TypeReference erasedType;
         
         if (baseType != null) {
           erasedType = baseType;
         } else { TypeReference erasedType;
           if (!interfaces.isEmpty()) {
             erasedType = (TypeReference)interfaces.get(0);
           }
           else {
             erasedType = BuiltinTypes.Object;
           }
         }
         formatType(writer, erasedType, syntax, false, stack);
         break;
       
 
       case TYPE_NAME: 
       case SHORT_TYPE_NAME: 
         first = true;
         
         if (baseType != null) {
           formatType(writer, baseType, syntax, false, stack);
           first = false;
         }
         
         for (TypeReference interfaceType : interfaces) {
           if (!first) {
             writer.writeDelimiter(" & ");
           }
           
           formatType(writer, interfaceType, syntax, false, stack);
           first = false;
         }
         
         break;
       }
       
       
       return;
     }
     
     if (type.isArray()) {
       switch (syntax) {
       case SIGNATURE: 
       case ERASED_SIGNATURE: 
       case DESCRIPTOR: 
         writer.writeDelimiter("[");
         formatType(writer, type.getElementType(), syntax, false, stack);
         break;
       
 
       case TYPE_NAME: 
       case SHORT_TYPE_NAME: 
         formatType(writer, type.getElementType(), syntax, false, stack);
         writer.writeDelimiter("[]");
       }
       
       
       return;
     }
     
     stack.push(type);
     
     TypeDefinition resolvedType = type.resolve();
     TypeReference nameSource = resolvedType != null ? resolvedType : type;
     try
     {
       String name;
       String name;
       switch (syntax) {
       case TYPE_NAME: 
         name = nameSource.getFullName();
         break;
       case SHORT_TYPE_NAME: 
         name = nameSource.getSimpleName();
         break;
       case DESCRIPTOR: 
         name = nameSource.getInternalName();
         break;
       default: 
         if (nameSource.isPrimitive()) {
           name = nameSource.getInternalName();
         }
         else {
           writer.writeDelimiter("L");
           name = nameSource.getInternalName();
         }
         break;
       }
       
       if ((type.isPrimitive()) && ((syntax == NameSyntax.TYPE_NAME) || (syntax == NameSyntax.SHORT_TYPE_NAME))) {
         writer.writeKeyword(name);
       }
       else if (isDefinition) {
         writer.writeDefinition(name, type);
       }
       else {
         writer.writeReference(name, type);
       }
       
       if ((type.isGenericType()) && (syntax != NameSyntax.DESCRIPTOR) && (syntax != NameSyntax.ERASED_SIGNATURE))
       {
 
 
         stack.push(type);
         try
         {
           List<? extends TypeReference> typeArguments;
           List<? extends TypeReference> typeArguments;
           if ((type instanceof IGenericInstance)) {
             typeArguments = ((IGenericInstance)type).getTypeArguments();
           }
           else {
             typeArguments = type.getGenericParameters();
           }
           
           int count = typeArguments.size();
           
           if (count > 0) {
             writer.writeDelimiter("<");
             for (int i = 0; i < count; i++) {
               if ((syntax != NameSyntax.SIGNATURE) && 
                 (i != 0)) {
                 writer.writeDelimiter(", ");
               }
               
 
               TypeReference typeArgument = (TypeReference)typeArguments.get(i);
               
               formatType(writer, typeArgument, syntax, false, stack);
             }
             writer.writeDelimiter(">");
           }
         }
         finally {}
       }
       
 
 
       if ((!type.isPrimitive()) && ((syntax == NameSyntax.SIGNATURE) || (syntax == NameSyntax.ERASED_SIGNATURE))) {
         writer.writeDelimiter(";");
       }
     }
     finally {
       stack.pop();
     }
   }
   
 
 
 
   private static void formatGenericSignature(ITextOutput writer, TypeReference type, Stack<TypeReference> stack)
   {
     if (type.isGenericParameter()) {
       TypeReference extendsBound = type.getExtendsBound();
       TypeDefinition resolvedBound = extendsBound.resolve();
       
       writer.writeDefinition(type.getName(), type);
       
       if ((resolvedBound != null) && (resolvedBound.isInterface())) {
         writer.writeDelimiter(":");
       }
       
       writer.writeDelimiter(":");
       
       formatType(writer, extendsBound, NameSyntax.SIGNATURE, false, stack);
       
       return;
     }
     
     if (type.isGenericType()) {
       List<? extends TypeReference> typeArguments;
       List<? extends TypeReference> typeArguments;
       if ((type instanceof IGenericInstance)) {
         typeArguments = ((IGenericInstance)type).getTypeArguments();
       }
       else {
         typeArguments = type.getGenericParameters();
       }
       
       int count = typeArguments.size();
       
       if (count > 0) {
         writer.writeDelimiter("<");
         
         for (int i = 0; i < count; i++) {
           formatGenericSignature(writer, (TypeReference)typeArguments.get(i), stack);
         }
         writer.writeDelimiter(">");
       }
     }
     
     TypeDefinition definition = type.resolve();
     
     if (definition == null) {
       return;
     }
     
     TypeReference baseType = definition.getBaseType();
     List<TypeReference> interfaces = definition.getExplicitInterfaces();
     
     if (baseType == null) {
       formatType(writer, BuiltinTypes.Object, NameSyntax.SIGNATURE, false, stack);
     }
     else {
       formatType(writer, baseType, NameSyntax.SIGNATURE, false, stack);
     }
     
     for (TypeReference interfaceType : interfaces) {
       formatType(writer, interfaceType, NameSyntax.SIGNATURE, false, stack);
     }
   }
   
   public static void writePrimitiveValue(ITextOutput output, Object value) {
     if (value == null) {
       output.writeKeyword("null");
       return;
     }
     
     if ((value instanceof Boolean)) {
       if (((Boolean)value).booleanValue()) {
         output.writeKeyword("true");
       }
       else {
         output.writeKeyword("false");
       }
       return;
     }
     
     if ((value instanceof String)) {
       output.writeTextLiteral(StringUtilities.escape(value.toString(), true, true));
     }
     else if ((value instanceof Character)) {
       output.writeTextLiteral(StringUtilities.escape(((Character)value).charValue(), true, true));
     }
     else if ((value instanceof Float)) {
       float f = ((Float)value).floatValue();
       if ((Float.isInfinite(f)) || (Float.isNaN(f))) {
         output.writeReference("Float", MetadataSystem.instance().lookupType("java/lang/Float"));
         output.writeDelimiter(".");
         if (f == Float.POSITIVE_INFINITY) {
           output.write("POSITIVE_INFINITY");
         }
         else if (f == Float.NEGATIVE_INFINITY) {
           output.write("NEGATIVE_INFINITY");
         }
         else {
           output.write("NaN");
         }
         return;
       }
       output.writeLiteral(Float.toString(f) + "f");
     }
     else if ((value instanceof Double)) {
       double d = ((Double)value).doubleValue();
       if ((Double.isInfinite(d)) || (Double.isNaN(d))) {
         TypeReference doubleType = MetadataSystem.instance().lookupType("java/lang/Double");
         output.writeReference("Double", doubleType);
         output.writeDelimiter(".");
         if (d == Double.POSITIVE_INFINITY) {
           output.write("POSITIVE_INFINITY");
         }
         else if (d == Double.NEGATIVE_INFINITY) {
           output.write("NEGATIVE_INFINITY");
         }
         else {
           output.write("NaN");
         }
         return;
       }
       
       String number = Double.toString(d);
       
       if ((number.indexOf('.') < 0) && (number.indexOf('E') < 0)) {
         number = number + "d";
       }
       
       output.writeLiteral(number);
     }
     else if ((value instanceof Long)) {
       output.writeLiteral(String.valueOf(value) + "L");
     }
     else {
       output.writeLiteral(String.valueOf(value));
     }
   }
 }


