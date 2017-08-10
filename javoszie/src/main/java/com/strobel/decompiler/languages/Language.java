 package com.strobel.decompiler.languages;
 
 import com.strobel.assembler.metadata.FieldDefinition;
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.DecompilationOptions;
 import com.strobel.decompiler.ITextOutput;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class Language
 {
   public abstract String getName();
   
   public abstract String getFileExtension();
   
   public void decompilePackage(String packageName, Iterable<TypeDefinition> types, ITextOutput output, DecompilationOptions options)
   {
     writeCommentLine(output, packageName);
   }
   
   public TypeDecompilationResults decompileType(TypeDefinition type, ITextOutput output, DecompilationOptions options) {
     writeCommentLine(output, typeToString(type, true));
     return new TypeDecompilationResults(null);
   }
   
   public void decompileMethod(MethodDefinition method, ITextOutput output, DecompilationOptions options) {
     writeCommentLine(output, typeToString(method.getDeclaringType(), true) + "." + method.getName());
   }
   
   public void decompileField(FieldDefinition field, ITextOutput output, DecompilationOptions options) {
     writeCommentLine(output, typeToString(field.getDeclaringType(), true) + "." + field.getName());
   }
   
   public void writeCommentLine(ITextOutput output, String comment) {
     output.writeComment("// " + comment);
     output.writeLine();
   }
   
   public String typeToString(TypeReference type, boolean includePackage) {
     VerifyArgument.notNull(type, "type");
     return includePackage ? type.getFullName() : type.getName();
   }
   
   public String formatTypeName(TypeReference type) {
     return ((TypeReference)VerifyArgument.notNull(type, "type")).getName();
   }
   
   public boolean isMemberBrowsable(MemberReference member) {
     return true;
   }
   
   public String getHint(MemberReference member) {
     if ((member instanceof TypeReference)) {
       return typeToString((TypeReference)member, true);
     }
     
     return member.toString();
   }
 }


