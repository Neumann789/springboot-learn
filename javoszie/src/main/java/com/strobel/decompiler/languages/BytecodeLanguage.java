 package com.strobel.decompiler.languages;
 
 import com.strobel.assembler.ir.ExceptionHandler;
 import com.strobel.assembler.ir.Instruction;
 import com.strobel.assembler.ir.InstructionBlock;
 import com.strobel.assembler.ir.OpCode;
 import com.strobel.assembler.ir.attributes.EnclosingMethodAttribute;
 import com.strobel.assembler.ir.attributes.InnerClassEntry;
 import com.strobel.assembler.ir.attributes.LineNumberTableEntry;
 import com.strobel.assembler.ir.attributes.LocalVariableTableEntry;
 import com.strobel.assembler.ir.attributes.MethodParameterEntry;
 import com.strobel.assembler.ir.attributes.SourceAttribute;
 import com.strobel.assembler.metadata.DynamicCallSite;
 import com.strobel.assembler.metadata.FieldDefinition;
 import com.strobel.assembler.metadata.Flags;
 import com.strobel.assembler.metadata.Flags.Flag;
 import com.strobel.assembler.metadata.Flags.Kind;
 import com.strobel.assembler.metadata.MetadataParser;
 import com.strobel.assembler.metadata.MethodBody;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.SwitchInfo;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.assembler.metadata.VariableDefinition;
 import com.strobel.assembler.metadata.VariableDefinitionCollection;
 import com.strobel.assembler.metadata.VariableReference;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.DecompilationOptions;
 import com.strobel.decompiler.DecompilerHelpers;
 import com.strobel.decompiler.DecompilerSettings;
 import com.strobel.decompiler.ITextOutput;
 import com.strobel.decompiler.NameSyntax;
 import java.util.EnumSet;
 import java.util.List;
 
 public class BytecodeLanguage extends Language
 {
   public String getName()
   {
     return "Bytecode";
   }
   
   public String getFileExtension()
   {
     return ".class";
   }
   
   public TypeDecompilationResults decompileType(TypeDefinition type, ITextOutput output, DecompilationOptions options)
   {
     VerifyArgument.notNull(type, "type");
     VerifyArgument.notNull(output, "output");
     VerifyArgument.notNull(options, "options");
     
     if (type.isInterface()) {
       if (type.isAnnotation()) {
         output.writeKeyword("@interface");
       }
       else {
         output.writeKeyword("interface");
       }
     }
     else if (type.isEnum()) {
       output.writeKeyword("enum");
     }
     else {
       output.writeKeyword("class");
     }
     
     output.write(' ');
     DecompilerHelpers.writeType(output, type, NameSyntax.TYPE_NAME, true);
     output.writeLine();
     output.indent();
     try
     {
       writeTypeHeader(output, type);
       
       for (SourceAttribute attribute : type.getSourceAttributes()) {
         writeTypeAttribute(output, type, attribute);
       }
       
       com.strobel.assembler.ir.ConstantPool constantPool = type.getConstantPool();
       
       if (constantPool != null) {
         constantPool.accept(new com.strobel.assembler.metadata.ConstantPoolPrinter(output, options.getSettings()));
       }
       
       for (FieldDefinition field : type.getDeclaredFields()) {
         output.writeLine();
         decompileField(field, output, options);
       }
       
       for (MethodDefinition method : type.getDeclaredMethods()) {
         output.writeLine();
         try
         {
           decompileMethod(method, output, options);
         }
         catch (com.strobel.assembler.metadata.MethodBodyParseException e) {
           writeMethodBodyParseError(output, e);
         }
       }
     }
     finally {
       output.unindent();
     }
     
     if (!options.getSettings().getExcludeNestedTypes()) {
       for (TypeDefinition innerType : type.getDeclaredTypes()) {
         output.writeLine();
         decompileType(innerType, output, options);
       }
     }
     
     return new TypeDecompilationResults(null);
   }
   
   private void writeMethodBodyParseError(ITextOutput output, Throwable error) {
     output.indent();
     try
     {
       output.writeError("Method could not be disassembled because an error occurred.");
       output.writeLine();
       for (String line : StringUtilities.split(com.strobel.core.ExceptionUtilities.getStackTraceString(error), true, '\r', new char[] { '\n' })) {
         output.writeError(line);
         output.writeLine();
       }
     }
     finally {
       output.unindent();
     }
   }
   
   private void writeTypeAttribute(ITextOutput output, TypeDefinition type, SourceAttribute attribute) {
     if ((attribute instanceof com.strobel.assembler.ir.attributes.BlobAttribute)) {
       return;
     }
     
     switch (attribute.getName()) {
     case "SourceFile": 
       output.writeAttribute("SourceFile");
       output.write(": ");
       output.writeTextLiteral(((com.strobel.assembler.ir.attributes.SourceFileAttribute)attribute).getSourceFile());
       output.writeLine();
       break;
     
 
     case "Deprecated": 
       output.writeAttribute("Deprecated");
       output.writeLine();
       break;
     
 
     case "EnclosingMethod": 
       TypeReference enclosingType = ((EnclosingMethodAttribute)attribute).getEnclosingType();
       MethodReference enclosingMethod = ((EnclosingMethodAttribute)attribute).getEnclosingMethod();
       
       if (enclosingType != null) {
         output.writeAttribute("EnclosingType");
         output.write(": ");
         output.writeReference(enclosingType.getInternalName(), enclosingType);
         output.writeLine();
       }
       
       if (enclosingMethod != null) {
         TypeReference declaringType = enclosingMethod.getDeclaringType();
         
         output.writeAttribute("EnclosingMethod");
         output.write(": ");
         output.writeReference(declaringType.getInternalName(), declaringType);
         output.writeDelimiter(".");
         output.writeReference(enclosingMethod.getName(), enclosingMethod);
         output.writeDelimiter(":");
         DecompilerHelpers.writeMethodSignature(output, enclosingMethod);
         output.writeLine(); }
       break;
     
 
 
 
     case "InnerClasses": 
       com.strobel.assembler.ir.attributes.InnerClassesAttribute innerClasses = (com.strobel.assembler.ir.attributes.InnerClassesAttribute)attribute;
       List<InnerClassEntry> entries = innerClasses.getEntries();
       
       output.writeAttribute("InnerClasses");
       output.writeLine(": ");
       output.indent();
       try
       {
         for (InnerClassEntry entry : entries) {
           writeInnerClassEntry(output, type, entry);
         }
       }
       finally {
         output.unindent();
       }
     
 
     case "Signature": 
       output.writeAttribute("Signature");
       output.write(": ");
       DecompilerHelpers.writeGenericSignature(output, type);
       output.writeLine();
     }
   }
   
   private void writeInnerClassEntry(ITextOutput output, TypeDefinition type, InnerClassEntry entry)
   {
     String shortName = entry.getShortName();
     String innerClassName = entry.getInnerClassName();
     String outerClassName = entry.getOuterClassName();
     EnumSet<Flags.Flag> flagsSet = Flags.asFlagSet(entry.getAccessFlags(), Flags.Kind.InnerClass);
     
     for (Flags.Flag flag : flagsSet) {
       output.writeKeyword(flag.toString());
       output.write(' ');
     }
     
     MetadataParser parser = new MetadataParser(type);
     
     if (tryWriteType(output, parser, shortName, innerClassName)) {
       output.writeDelimiter(" = ");
     }
     
     if (!tryWriteType(output, parser, innerClassName, innerClassName)) {
       output.writeError("?");
     }
     
     if (!StringUtilities.isNullOrEmpty(outerClassName)) {
       output.writeDelimiter(" of ");
       
       if (!tryWriteType(output, parser, outerClassName, outerClassName)) {
         output.writeError("?");
       }
     }
     
     output.writeLine();
   }
   
 
 
 
 
   private boolean tryWriteType(@com.strobel.annotations.NotNull ITextOutput output, @com.strobel.annotations.NotNull MetadataParser parser, String text, String descriptor)
   {
     if (StringUtilities.isNullOrEmpty(text)) {
       return false;
     }
     
     if (StringUtilities.isNullOrEmpty(descriptor)) {
       output.writeError(text);
       return true;
     }
     try
     {
       TypeReference type = parser.parseTypeDescriptor(descriptor);
       output.writeReference(text, type);
       return true;
     }
     catch (Throwable ignored)
     {
       try
       {
         output.writeReference(text, new DummyTypeReference(descriptor));
         return true;
 
       }
       catch (Throwable ignored)
       {
         output.writeError(text); } }
     return true;
   }
   
   private void writeTypeHeader(ITextOutput output, TypeDefinition type) {
     output.writeAttribute("Minor version");
     output.write(": ");
     output.writeLiteral(Integer.valueOf(type.getCompilerMinorVersion()));
     output.writeLine();
     
     output.writeAttribute("Major version");
     output.write(": ");
     output.writeLiteral(Integer.valueOf(type.getCompilerMajorVersion()));
     output.writeLine();
     
     long flags = type.getFlags();
     List<String> flagStrings = new java.util.ArrayList();
     
     EnumSet<Flags.Flag> flagsSet = Flags.asFlagSet(flags, type.isInnerClass() ? Flags.Kind.InnerClass : Flags.Kind.Class);
     
 
 
 
     for (Flags.Flag flag : flagsSet) {
       flagStrings.add(flag.name());
     }
     
     if (!flagStrings.isEmpty()) {
       output.writeAttribute("Flags");
       output.write(": ");
       
       for (int i = 0; i < flagStrings.size(); i++) {
         if (i != 0) {
           output.write(", ");
         }
         
         output.writeLiteral(flagStrings.get(i));
       }
       
       output.writeLine();
     }
   }
   
   public void decompileField(FieldDefinition field, ITextOutput output, DecompilationOptions options)
   {
     long flags = field.getFlags();
     EnumSet<Flags.Flag> flagSet = Flags.asFlagSet(flags & 0x40DF & 0xFFFFFFFFFFFFBFFF, Flags.Kind.Field);
     List<String> flagStrings = new java.util.ArrayList();
     
     for (Flags.Flag flag : flagSet) {
       flagStrings.add(flag.toString());
     }
     
     if (flagSet.size() > 0) {
       for (int i = 0; i < flagStrings.size(); i++) {
         output.writeKeyword((String)flagStrings.get(i));
         output.write(' ');
       }
     }
     
     DecompilerHelpers.writeType(output, field.getFieldType(), NameSyntax.TYPE_NAME);
     
     output.write(' ');
     output.writeDefinition(field.getName(), field);
     output.writeDelimiter(";");
     output.writeLine();
     
     flagStrings.clear();
     
     for (Flags.Flag flag : Flags.asFlagSet(flags & 0xFFFFFFFFFFFFF0DF, Flags.Kind.Field)) {
       flagStrings.add(flag.name());
     }
     
     if (flagStrings.isEmpty()) {
       return;
     }
     
     output.indent();
     try
     {
       output.writeAttribute("Flags");
       output.write(": ");
       
       for (int i = 0; i < flagStrings.size(); i++) {
         if (i != 0) {
           output.write(", ");
         }
         
         output.writeLiteral(flagStrings.get(i));
       }
       
       output.writeLine();
       
       for (SourceAttribute attribute : field.getSourceAttributes()) {
         writeFieldAttribute(output, field, attribute);
       }
     }
     finally {
       output.unindent();
     }
   }
   
   private void writeFieldAttribute(ITextOutput output, FieldDefinition field, SourceAttribute attribute) {
     switch (attribute.getName()) {
     case "ConstantValue": 
       Object constantValue = ((com.strobel.assembler.ir.attributes.ConstantValueAttribute)attribute).getValue();
       
       output.writeAttribute("ConstantValue");
       output.write(": ");
       
       if (constantValue != null) {
         String typeDescriptor = constantValue.getClass().getName().replace('.', '/');
         TypeReference valueType = field.getDeclaringType().getResolver().lookupType(typeDescriptor);
         
         if (valueType != null) {
           DecompilerHelpers.writeType(output, com.strobel.assembler.metadata.MetadataHelper.getUnderlyingPrimitiveTypeOrSelf(valueType), NameSyntax.TYPE_NAME);
           
 
 
 
 
           output.write(' ');
         }
       }
       
       DecompilerHelpers.writeOperand(output, constantValue);
       output.writeLine();
       break;
     
 
     case "Signature": 
       output.writeAttribute("Signature");
       output.write(": ");
       DecompilerHelpers.writeType(output, field.getFieldType(), NameSyntax.SIGNATURE, false);
       output.writeLine();
     }
     
   }
   
 
   public void decompileMethod(MethodDefinition method, ITextOutput output, DecompilationOptions options)
   {
     writeMethodHeader(output, method);
     writeMethodBody(output, method, options);
     
     for (SourceAttribute attribute : method.getSourceAttributes()) {
       writeMethodAttribute(output, method, attribute);
     }
     
     writeMethodEnd(output, method, options);
   }
   
   private void writeMethodHeader(ITextOutput output, MethodDefinition method) {
     String name = method.getName();
     long flags = Flags.fromStandardFlags(method.getFlags(), Flags.Kind.Method);
     List<String> flagStrings = new java.util.ArrayList();
     
     if ("<clinit>".equals(name)) {
       output.writeKeyword("static");
       output.write(" {}");
     }
     else {
       EnumSet<Flags.Flag> flagSet = Flags.asFlagSet(flags & 0xD3F, Flags.Kind.Method);
       
       for (Flags.Flag flag : flagSet) {
         flagStrings.add(flag.toString());
       }
       
       if (flagSet.size() > 0) {
         for (int i = 0; i < flagStrings.size(); i++) {
           output.writeKeyword((String)flagStrings.get(i));
           output.write(' ');
         }
       }
       
       List<com.strobel.assembler.metadata.GenericParameter> genericParameters = method.getGenericParameters();
       
       if (!genericParameters.isEmpty()) {
         output.writeDelimiter("<");
         
         for (int i = 0; i < genericParameters.size(); i++) {
           if (i != 0) {
             output.writeDelimiter(", ");
           }
           
           DecompilerHelpers.writeType(output, (TypeReference)genericParameters.get(i), NameSyntax.TYPE_NAME, true);
         }
         
         output.writeDelimiter(">");
         output.write(' ');
       }
       
       DecompilerHelpers.writeType(output, method.getReturnType(), NameSyntax.TYPE_NAME, false);
       
       output.write(' ');
       output.writeDefinition(name, method);
       output.writeDelimiter("(");
       
       List<ParameterDefinition> parameters = method.getParameters();
       
       for (int i = 0; i < parameters.size(); i++) {
         if (i != 0) {
           output.writeDelimiter(", ");
         }
         
         ParameterDefinition parameter = (ParameterDefinition)parameters.get(i);
         
         if ((Flags.testAny(flags, 17179869312L)) && (i == parameters.size() - 1)) {
           DecompilerHelpers.writeType(output, parameter.getParameterType().getElementType(), NameSyntax.TYPE_NAME, false);
           output.writeDelimiter("...");
         }
         else {
           DecompilerHelpers.writeType(output, parameter.getParameterType(), NameSyntax.TYPE_NAME, false);
         }
         
         output.write(' ');
         
         String parameterName = parameter.getName();
         
         if (StringUtilities.isNullOrEmpty(parameterName)) {
           output.write("p%d", new Object[] { Integer.valueOf(i) });
         }
         else {
           output.write(parameterName);
         }
       }
       
       output.writeDelimiter(")");
       
       List<TypeReference> thrownTypes = method.getThrownTypes();
       
       if (!thrownTypes.isEmpty()) {
         output.writeKeyword(" throws ");
         
         for (int i = 0; i < thrownTypes.size(); i++) {
           if (i != 0) {
             output.writeDelimiter(", ");
           }
           
           DecompilerHelpers.writeType(output, (TypeReference)thrownTypes.get(i), NameSyntax.TYPE_NAME, false);
         }
       }
     }
     
     output.writeDelimiter(";");
     output.writeLine();
     
     flagStrings.clear();
     
     for (Flags.Flag flag : Flags.asFlagSet(flags & 0xFFFFFFFFFFFFFD3F, Flags.Kind.Method)) {
       flagStrings.add(flag.name());
     }
     
     if (flagStrings.isEmpty()) {
       return;
     }
     
     output.indent();
     try
     {
       output.writeAttribute("Flags");
       output.write(": ");
       
       for (int i = 0; i < flagStrings.size(); i++) {
         if (i != 0) {
           output.write(", ");
         }
         
         output.writeLiteral(flagStrings.get(i));
       }
       
       output.writeLine();
     }
     finally {
       output.unindent();
     }
   }
   
   private void writeMethodAttribute(ITextOutput output, MethodDefinition method, SourceAttribute attribute)
   {
     switch (attribute.getName()) {
     case "Exceptions": 
       com.strobel.assembler.ir.attributes.ExceptionsAttribute exceptionsAttribute = (com.strobel.assembler.ir.attributes.ExceptionsAttribute)attribute;
       List<TypeReference> exceptionTypes = exceptionsAttribute.getExceptionTypes();
       
       if (!exceptionTypes.isEmpty()) {
         output.indent();
         try
         {
           output.writeAttribute("Exceptions");
           output.writeLine(":");
           
           output.indent();
           try
           {
             for (TypeReference exceptionType : exceptionTypes) {
               output.writeKeyword("throws");
               output.write(' ');
               DecompilerHelpers.writeType(output, exceptionType, NameSyntax.TYPE_NAME);
               output.writeLine();
             }
             
           }
           finally {}
         }
         finally
         {
           output.unindent(); } } break;
     
 
 
 
 
 
     case "LocalVariableTable": 
     case "LocalVariableTypeTable": 
       com.strobel.assembler.ir.attributes.LocalVariableTableAttribute localVariables = (com.strobel.assembler.ir.attributes.LocalVariableTableAttribute)attribute;
       List<LocalVariableTableEntry> entries = localVariables.getEntries();
       
       int longestName = "Name".length();
       int longestSignature = "Signature".length();
       
       for (LocalVariableTableEntry entry : entries) {
         String name = entry.getName();
         
         TypeReference type = entry.getType();
         
         if (type != null) { String signature;
           String signature; if (attribute.getName().equals("LocalVariableTypeTable")) {
             signature = type.getSignature();
           }
           else {
             signature = type.getErasedSignature();
           }
           
           if (signature.length() > longestSignature) {
             longestSignature = signature.length();
           }
         }
         
         if ((name != null) && (name.length() > longestName)) {
           longestName = name.length();
         }
       }
       
       output.indent();
       try
       {
         output.writeAttribute(attribute.getName());
         output.writeLine(":");
         
         output.indent();
         MethodBody body;
         try {
           output.write("Start  Length  Slot  %1$-" + longestName + "s  Signature", new Object[] { "Name" });
           output.writeLine();
           
           output.write("-----  ------  ----  %1$-" + longestName + "s  %2$-" + longestSignature + "s", new Object[] { StringUtilities.repeat('-', longestName), StringUtilities.repeat('-', longestSignature) });
           
 
 
 
 
           output.writeLine();
           
           body = method.getBody();
           
           for (LocalVariableTableEntry entry : entries)
           {
             VariableDefinitionCollection variables = body != null ? body.getVariables() : null;
             NameSyntax nameSyntax;
             NameSyntax nameSyntax; if (attribute.getName().equals("LocalVariableTypeTable")) {
               nameSyntax = NameSyntax.SIGNATURE;
             }
             else {
               nameSyntax = NameSyntax.ERASED_SIGNATURE;
             }
             
             output.writeLiteral(String.format("%1$-5d", new Object[] { Integer.valueOf(entry.getScopeOffset()) }));
             output.write("  ");
             output.writeLiteral(String.format("%1$-6d", new Object[] { Integer.valueOf(entry.getScopeLength()) }));
             output.write("  ");
             output.writeLiteral(String.format("%1$-4d", new Object[] { Integer.valueOf(entry.getIndex()) }));
             
             output.writeReference(String.format("  %1$-" + longestName + "s  ", new Object[] { entry.getName() }), variables != null ? variables.tryFind(entry.getIndex(), entry.getScopeOffset()) : null);
             
 
 
 
             DecompilerHelpers.writeType(output, entry.getType(), nameSyntax);
             
             output.writeLine();
           }
           
         }
         finally {}
       }
       finally
       {
         output.unindent();
       }
       
       break;
     
 
     case "MethodParameters": 
       com.strobel.assembler.ir.attributes.MethodParametersAttribute parameters = (com.strobel.assembler.ir.attributes.MethodParametersAttribute)attribute;
       List<MethodParameterEntry> entries = parameters.getEntries();
       
       int longestName = "Name".length();
       int longestFlags = "Flags".length();
       
       for (MethodParameterEntry entry : entries) {
         String name = entry.getName();
         String flags = Flags.toString(entry.getFlags());
         
         if ((name != null) && (name.length() > longestName)) {
           longestName = name.length();
         }
         
         if ((flags != null) && (flags.length() > longestFlags)) {
           longestFlags = flags.length();
         }
       }
       
       output.indent();
       try
       {
         output.writeAttribute(attribute.getName());
         output.writeLine(":");
         
         output.indent();
         try
         {
           output.write("%1$-" + longestName + "s  %2$-" + longestFlags + "s  ", new Object[] { "Name", "Flags" });
           output.writeLine();
           
           output.write("%1$-" + longestName + "s  %2$-" + longestFlags + "s", new Object[] { StringUtilities.repeat('-', longestName), StringUtilities.repeat('-', longestFlags) });
           
 
 
 
 
           output.writeLine();
           
           for (int i = 0; i < entries.size(); i++) {
             MethodParameterEntry entry = (MethodParameterEntry)entries.get(i);
             List<ParameterDefinition> parameterDefinitions = method.getParameters();
             
             output.writeReference(String.format("%1$-" + longestName + "s  ", new Object[] { entry.getName() }), i < parameterDefinitions.size() ? (ParameterDefinition)parameterDefinitions.get(i) : null);
             
 
 
 
             EnumSet<Flags.Flag> flags = Flags.asFlagSet(entry.getFlags());
             
             boolean firstFlag = true;
             
             for (Flags.Flag flag : flags) {
               if (!firstFlag) {
                 output.writeDelimiter(", ");
               }
               
               output.writeLiteral(flag.name());
               firstFlag = false;
             }
             
             output.writeLine();
           }
           
         }
         finally {}
       }
       finally
       {
         output.unindent();
       }
       
       break;
     
 
     case "Signature": 
       output.indent();
       try
       {
         String signature = ((com.strobel.assembler.ir.attributes.SignatureAttribute)attribute).getSignature();
         
         output.writeAttribute(attribute.getName());
         output.writeLine(":");
         output.indent();
         
         com.strobel.decompiler.PlainTextOutput temp = new com.strobel.decompiler.PlainTextOutput();
         
         DecompilerHelpers.writeMethodSignature(temp, method);
         DecompilerHelpers.writeMethodSignature(output, method);
         
         if (!StringUtilities.equals(temp.toString(), signature)) {
           output.write(' ');
           output.writeDelimiter("[");
           output.write("from metadata: ");
           output.writeError(signature);
           output.writeDelimiter("]");
           output.writeLine();
         }
         
         output.writeLine();
         output.unindent();
       }
       finally {
         output.unindent();
       }
     }
     
   }
   
 
   private void writeMethodBody(ITextOutput output, MethodDefinition method, DecompilationOptions options)
   {
     MethodBody body = method.getBody();
     
     if (body == null) {
       return;
     }
     
     output.indent();
     try
     {
       output.writeAttribute("Code");
       output.writeLine(":");
       
       output.indent();
       try
       {
         output.write("stack=");
         output.writeLiteral(Integer.valueOf(body.getMaxStackSize()));
         output.write(", locals=");
         output.writeLiteral(Integer.valueOf(body.getMaxLocals()));
         output.write(", arguments=");
         output.writeLiteral(Integer.valueOf(method.getParameters().size()));
       }
       finally {}
       
 
 
 
       com.strobel.assembler.ir.InstructionCollection instructions = body.getInstructions();
       
       if (!instructions.isEmpty()) {
         int[] lineNumbers;
         int[] lineNumbers;
         if (options.getSettings().getIncludeLineNumbersInBytecode()) {
           com.strobel.assembler.ir.attributes.LineNumberTableAttribute lineNumbersAttribute = (com.strobel.assembler.ir.attributes.LineNumberTableAttribute)SourceAttribute.find("LineNumberTable", method.getSourceAttributes());
           
 
           int[] lineNumbers;
           
           if (lineNumbersAttribute != null) {
             lineNumbers = new int[body.getCodeSize()];
             
             java.util.Arrays.fill(lineNumbers, -1);
             
             for (LineNumberTableEntry entry : lineNumbersAttribute.getEntries()) {
               if (entry.getOffset() >= lineNumbers.length)
               {
 
 
 
                 lineNumbers = java.util.Arrays.copyOf(lineNumbers, entry.getOffset() + 1);
               }
               lineNumbers[entry.getOffset()] = entry.getLineNumber();
             }
           }
           else {
             lineNumbers = null;
           }
         }
         else {
           lineNumbers = null;
         }
         
         printer = new InstructionPrinter(output, method, options.getSettings(), lineNumbers, null);
         
         for (Instruction instruction : instructions) {
           printer.visit(instruction);
         }
       }
     } finally {
       InstructionPrinter printer;
       output.unindent();
     }
   }
   
   private void writeMethodEnd(ITextOutput output, MethodDefinition method, DecompilationOptions options)
   {
     MethodBody body = method.getBody();
     
     if (body == null) {
       return;
     }
     
     List<ExceptionHandler> handlers = body.getExceptionHandlers();
     List<com.strobel.assembler.ir.StackMapFrame> stackMapFrames = body.getStackMapFrames();
     
     if (!handlers.isEmpty()) {
       output.indent();
       try
       {
         int longestType = "Type".length();
         
         for (ExceptionHandler handler : handlers) {
           TypeReference catchType = handler.getCatchType();
           
           if (catchType != null) {
             String signature = catchType.getSignature();
             
             if (signature.length() > longestType) {
               longestType = signature.length();
             }
           }
         }
         
         output.writeAttribute("Exceptions");
         output.writeLine(":");
         
         output.indent();
         try
         {
           output.write("Try           Handler");
           output.writeLine();
           output.write("Start  End    Start  End    %1$-" + longestType + "s", new Object[] { "Type" });
           output.writeLine();
           
           output.write("-----  -----  -----  -----  %1$-" + longestType + "s", new Object[] { StringUtilities.repeat('-', longestType) });
           
 
 
 
           output.writeLine();
           
           for (ExceptionHandler handler : handlers)
           {
 
             TypeReference catchType = handler.getCatchType();
             boolean isFinally;
             boolean isFinally; if (catchType != null) {
               isFinally = false;
             }
             else {
               catchType = getResolver(body).lookupType("java/lang/Throwable");
               isFinally = true;
             }
             
             output.writeLiteral(String.format("%1$-5d", new Object[] { Integer.valueOf(handler.getTryBlock().getFirstInstruction().getOffset()) }));
             output.write("  ");
             output.writeLiteral(String.format("%1$-5d", new Object[] { Integer.valueOf(handler.getTryBlock().getLastInstruction().getEndOffset()) }));
             output.write("  ");
             output.writeLiteral(String.format("%1$-5d", new Object[] { Integer.valueOf(handler.getHandlerBlock().getFirstInstruction().getOffset()) }));
             output.write("  ");
             output.writeLiteral(String.format("%1$-5d", new Object[] { Integer.valueOf(handler.getHandlerBlock().getLastInstruction().getEndOffset()) }));
             output.write("  ");
             
             if (isFinally) {
               output.writeReference("Any", catchType);
             }
             else {
               DecompilerHelpers.writeType(output, catchType, NameSyntax.SIGNATURE);
             }
             
             output.writeLine();
           }
           
         }
         finally {}
       }
       finally
       {
         output.unindent();
       }
     }
     
     if (!stackMapFrames.isEmpty()) {
       output.indent();
       try
       {
         output.writeAttribute("Stack Map Frames");
         output.writeLine(":");
         
         output.indent();
         try
         {
           for (com.strobel.assembler.ir.StackMapFrame frame : stackMapFrames) {
             DecompilerHelpers.writeOffsetReference(output, frame.getStartInstruction());
             output.write(' ');
             DecompilerHelpers.writeFrame(output, frame.getFrame());
             output.writeLine();
           }
           
         }
         finally {}
       }
       finally
       {
         output.unindent();
       }
     }
   }
   
   private static com.strobel.assembler.metadata.IMetadataResolver getResolver(MethodBody body) {
     MethodReference method = body.getMethod();
     
     if (method != null) {
       MethodDefinition resolvedMethod = method.resolve();
       
       if (resolvedMethod != null) {
         TypeDefinition declaringType = resolvedMethod.getDeclaringType();
         
         if (declaringType != null) {
           return declaringType.getResolver();
         }
       }
     }
     
     return com.strobel.assembler.metadata.MetadataSystem.instance();
   }
   
   private static final class InstructionPrinter implements com.strobel.assembler.ir.InstructionVisitor {
     private static final int MAX_OPCODE_LENGTH;
     private static final String[] OPCODE_NAMES;
     private static final String LINE_NUMBER_CODE = "linenumber";
     
     static {
       int maxLength = "linenumber".length();
       
       OpCode[] values = OpCode.values();
       String[] names = new String[values.length];
       
       for (int i = 0; i < values.length; i++) {
         OpCode op = values[i];
         int length = op.name().length();
         
         if (length > maxLength) {
           maxLength = length;
         }
         
         names[i] = op.name().toLowerCase();
       }
       
       MAX_OPCODE_LENGTH = maxLength;
       OPCODE_NAMES = names;
     }
     
 
     private final DecompilerSettings _settings;
     private final ITextOutput _output;
     private final MethodBody _body;
     private final int[] _lineNumbers;
     private int _currentOffset = -1;
     
     private InstructionPrinter(ITextOutput output, MethodDefinition method, DecompilerSettings settings, int[] lineNumbers) {
       this._settings = settings;
       this._output = ((ITextOutput)VerifyArgument.notNull(output, "output"));
       this._body = ((MethodDefinition)VerifyArgument.notNull(method, "method")).getBody();
       this._lineNumbers = lineNumbers;
     }
     
     private void printOpCode(OpCode opCode) {
       switch (BytecodeLanguage.1.$SwitchMap$com$strobel$assembler$ir$OpCode[opCode.ordinal()]) {
       case 1: 
       case 2: 
         this._output.writeReference(OPCODE_NAMES[opCode.ordinal()], opCode);
         break;
       
       default: 
         this._output.writeReference(String.format("%1$-" + MAX_OPCODE_LENGTH + "s", new Object[] { OPCODE_NAMES[opCode.ordinal()] }), opCode);
       }
       
     }
     
     public void visit(Instruction instruction)
     {
       VerifyArgument.notNull(instruction, "instruction");
       
       if (this._lineNumbers != null) {
         int lineNumber = this._lineNumbers[instruction.getOffset()];
         
         if (lineNumber >= 0) {
           this._output.write("          ");
           this._output.write("%1$-" + MAX_OPCODE_LENGTH + "s", new Object[] { "linenumber" });
           this._output.write(' ');
           this._output.writeLiteral(Integer.valueOf(lineNumber));
           this._output.writeLine();
         }
       }
       
       this._currentOffset = instruction.getOffset();
       try
       {
         this._output.writeLabel(String.format("%1$8d", new Object[] { Integer.valueOf(instruction.getOffset()) }));
         this._output.write(": ");
         instruction.accept(this);
       }
       catch (Throwable t) {
         printOpCode(instruction.getOpCode());
         
         boolean foundError = false;
         
         for (int i = 0; i < instruction.getOperandCount(); i++) {
           Object operand = instruction.getOperand(i);
           
           if ((operand instanceof com.strobel.assembler.ir.ErrorOperand)) {
             this._output.write(String.valueOf(operand));
             foundError = true;
             break;
           }
         }
         
         if (!foundError) {
           this._output.write("!!! ERROR");
         }
         
         this._output.writeLine();
       }
       finally {
         this._currentOffset = -1;
       }
     }
     
     public void visit(OpCode op)
     {
       printOpCode(op);
       
       int slot = com.strobel.assembler.ir.OpCodeHelpers.getLoadStoreMacroArgumentIndex(op);
       
       if (slot >= 0) {
         VariableDefinitionCollection variables = this._body.getVariables();
         
         if (slot < variables.size()) {
           VariableDefinition variable = findVariable(op, slot, this._currentOffset);
           
           if ((variable != null) && (variable.hasName()) && (variable.isFromMetadata()))
           {
 
 
             this._output.writeComment(" /* %s */", new Object[] { StringUtilities.escape(variable.getName(), false, this._settings.isUnicodeOutputEnabled()) });
           }
         }
       }
       
 
 
 
       this._output.writeLine();
     }
     
     private VariableDefinition findVariable(OpCode op, int slot, int offset) {
       VariableDefinition variable = this._body.getVariables().tryFind(slot, offset);
       
       if ((variable == null) && (op.isStore())) {
         variable = this._body.getVariables().tryFind(slot, offset + op.getSize() + op.getOperandType().getBaseSize());
       }
       
       return variable;
     }
     
     public void visitConstant(OpCode op, TypeReference value)
     {
       printOpCode(op);
       
       this._output.write(' ');
       DecompilerHelpers.writeType(this._output, value, NameSyntax.ERASED_SIGNATURE);
       this._output.write(".class");
       
       this._output.writeLine();
     }
     
     public void visitConstant(OpCode op, int value)
     {
       printOpCode(op);
       
       this._output.write(' ');
       this._output.writeLiteral(Integer.valueOf(value));
       
       this._output.writeLine();
     }
     
     public void visitConstant(OpCode op, long value)
     {
       printOpCode(op);
       
       this._output.write(' ');
       this._output.writeLiteral(Long.valueOf(value));
       
       this._output.writeLine();
     }
     
     public void visitConstant(OpCode op, float value)
     {
       printOpCode(op);
       
       this._output.write(' ');
       this._output.writeLiteral(Float.valueOf(value));
       
       this._output.writeLine();
     }
     
     public void visitConstant(OpCode op, double value)
     {
       printOpCode(op);
       
       this._output.write(' ');
       this._output.writeLiteral(Double.valueOf(value));
       
       this._output.writeLine();
     }
     
     public void visitConstant(OpCode op, String value)
     {
       printOpCode(op);
       
       this._output.write(' ');
       this._output.writeTextLiteral(StringUtilities.escape(value, true, this._settings.isUnicodeOutputEnabled()));
       
       this._output.writeLine();
     }
     
     public void visitBranch(OpCode op, Instruction target)
     {
       printOpCode(op);
       
       this._output.write(' ');
       this._output.writeLabel(String.valueOf(target.getOffset()));
       
       this._output.writeLine();
     }
     
     public void visitVariable(OpCode op, VariableReference variable)
     {
       printOpCode(op);
       
       this._output.write(' ');
       
       VariableDefinition definition = findVariable(op, variable.getSlot(), this._currentOffset);
       
       if ((definition != null) && (definition.hasName()) && (definition.isFromMetadata())) {
         this._output.writeReference(variable.getName(), variable);
       }
       else {
         this._output.writeLiteral(Integer.valueOf(variable.getSlot()));
       }
       
       this._output.writeLine();
     }
     
     public void visitVariable(OpCode op, VariableReference variable, int operand)
     {
       printOpCode(op);
       this._output.write(' ');
       
       VariableDefinition definition;
       VariableDefinition definition;
       if ((variable instanceof VariableDefinition)) {
         definition = (VariableDefinition)variable;
       }
       else {
         definition = findVariable(op, variable.getSlot(), this._currentOffset);
       }
       
       if ((definition != null) && (definition.hasName()) && (definition.isFromMetadata())) {
         this._output.writeReference(variable.getName(), variable);
       }
       else {
         this._output.writeLiteral(Integer.valueOf(variable.getSlot()));
       }
       
       this._output.write(", ");
       this._output.writeLiteral(String.valueOf(operand));
       
       this._output.writeLine();
     }
     
     public void visitType(OpCode op, TypeReference type)
     {
       printOpCode(op);
       
       this._output.write(' ');
       
       DecompilerHelpers.writeType(this._output, type, NameSyntax.SIGNATURE);
       
       this._output.writeLine();
     }
     
     public void visitMethod(OpCode op, MethodReference method)
     {
       printOpCode(op);
       
       this._output.write(' ');
       
       DecompilerHelpers.writeMethod(this._output, method);
       
       this._output.writeLine();
     }
     
     public void visitDynamicCallSite(OpCode op, DynamicCallSite callSite)
     {
       printOpCode(op);
       
       this._output.write(' ');
       
       this._output.writeReference(callSite.getMethodName(), callSite.getMethodType());
       this._output.writeDelimiter(":");
       
       DecompilerHelpers.writeMethodSignature(this._output, callSite.getMethodType());
       
       this._output.writeLine();
     }
     
     public void visitField(OpCode op, com.strobel.assembler.metadata.FieldReference field)
     {
       printOpCode(op);
       
       this._output.write(' ');
       
       DecompilerHelpers.writeField(this._output, field);
       
       this._output.writeLine();
     }
     
 
 
 
 
     public void visitSwitch(OpCode op, SwitchInfo switchInfo)
     {
       printOpCode(op);
       this._output.write(" {");
       this._output.writeLine();
       
       switch (BytecodeLanguage.1.$SwitchMap$com$strobel$assembler$ir$OpCode[op.ordinal()]) {
       case 1: 
         Instruction[] targets = switchInfo.getTargets();
         
         int caseValue = switchInfo.getLowValue();
         
         for (Instruction target : targets) {
           this._output.write("            ");
           this._output.writeLiteral(String.format("%1$7d", new Object[] { Integer.valueOf(switchInfo.getLowValue() + caseValue++) }));
           this._output.write(": ");
           this._output.writeLabel(String.valueOf(target.getOffset()));
           this._output.writeLine();
         }
         
         this._output.write("            ");
         this._output.writeKeyword("default");
         this._output.write(": ");
         this._output.writeLabel(String.valueOf(switchInfo.getDefaultTarget().getOffset()));
         this._output.writeLine();
         
         break;
       
 
       case 2: 
         int[] keys = switchInfo.getKeys();
         Instruction[] targets = switchInfo.getTargets();
         
         for (int i = 0; i < keys.length; i++) {
           int key = keys[i];
           Instruction target = targets[i];
           
           this._output.write("            ");
           this._output.writeLiteral(String.format("%1$7d", new Object[] { Integer.valueOf(key) }));
           this._output.write(": ");
           this._output.writeLabel(String.valueOf(target.getOffset()));
           this._output.writeLine();
         }
         
         this._output.write("            ");
         this._output.writeKeyword("default");
         this._output.write(": ");
         this._output.writeLabel(String.valueOf(switchInfo.getDefaultTarget().getOffset()));
         this._output.writeLine();
         
         break;
       }
       
       
       this._output.write("          }");
       this._output.writeLine();
     }
     
     public void visitLabel(com.strobel.assembler.metadata.Label label) {}
     
     public void visitEnd() {}
   }
   
   private static final class DummyTypeReference extends TypeReference {
     private final String _descriptor;
     private final String _fullName;
     private final String _simpleName;
     
     public DummyTypeReference(String descriptor) {
       this._descriptor = ((String)VerifyArgument.notNull(descriptor, "descriptor"));
       this._fullName = descriptor.replace('/', '.');
       
       int delimiterIndex = this._fullName.lastIndexOf('.');
       
       if ((delimiterIndex < 0) || (delimiterIndex == this._fullName.length() - 1)) {
         this._simpleName = this._fullName;
       }
       else {
         this._simpleName = this._fullName.substring(delimiterIndex + 1);
       }
     }
     
     public final String getSimpleName()
     {
       return this._simpleName;
     }
     
     public final String getFullName()
     {
       return this._fullName;
     }
     
     public final String getInternalName()
     {
       return this._descriptor;
     }
     
     public final <R, P> R accept(com.strobel.assembler.metadata.TypeMetadataVisitor<P, R> visitor, P parameter)
     {
       return (R)visitor.visitClassType(this, parameter);
     }
   }
 }


