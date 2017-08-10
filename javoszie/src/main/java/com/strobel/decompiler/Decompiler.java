 package com.strobel.decompiler;
 
 import com.strobel.assembler.InputTypeLoader;
 import com.strobel.assembler.metadata.DeobfuscationUtilities;
 import com.strobel.assembler.metadata.IMetadataResolver;
 import com.strobel.assembler.metadata.ITypeLoader;
 import com.strobel.assembler.metadata.MetadataParser;
 import com.strobel.assembler.metadata.MetadataSystem;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.Language;
 import com.strobel.decompiler.languages.java.JavaFormattingOptions;
 
 
 
 
 
 
 
 
 public final class Decompiler
 {
   public static void decompile(String internalName, ITextOutput output)
   {
     decompile(internalName, output, new DecompilerSettings());
   }
   
   public static void decompile(String internalName, ITextOutput output, DecompilerSettings settings) {
     VerifyArgument.notNull(internalName, "internalName");
     VerifyArgument.notNull(settings, "settings");
     
     ITypeLoader typeLoader = settings.getTypeLoader() != null ? settings.getTypeLoader() : new InputTypeLoader();
     MetadataSystem metadataSystem = new MetadataSystem(typeLoader);
     
     TypeReference type;
     TypeReference type;
     if (internalName.length() == 1)
     {
 
 
 
       MetadataParser parser = new MetadataParser(IMetadataResolver.EMPTY);
       TypeReference reference = parser.parseTypeDescriptor(internalName);
       
       type = metadataSystem.resolve(reference);
     }
     else {
       type = metadataSystem.lookupType(internalName);
     }
     
     TypeDefinition resolvedType;
     
     if ((type == null) || ((resolvedType = type.resolve()) == null)) {
       output.writeLine("!!! ERROR: Failed to load class %s.", new Object[] { internalName }); return;
     }
     
     TypeDefinition resolvedType;
     DeobfuscationUtilities.processType(resolvedType);
     
     DecompilationOptions options = new DecompilationOptions();
     
     options.setSettings(settings);
     options.setFullDecompilation(true);
     
     if (settings.getFormattingOptions() == null) {
       settings.setFormattingOptions(JavaFormattingOptions.createDefault());
     }
     
     settings.getLanguage().decompileType(resolvedType, output, options);
   }
 }


