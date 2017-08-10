 package com.strobel.decompiler;
 
 import com.beust.jcommander.JCommander;
 import com.beust.jcommander.internal.Console;
 import com.strobel.Procyon;
 import com.strobel.assembler.metadata.CompositeTypeLoader;
 import com.strobel.assembler.metadata.DeobfuscationUtilities;
 import com.strobel.assembler.metadata.IMetadataResolver;
 import com.strobel.assembler.metadata.ITypeLoader;
 import com.strobel.assembler.metadata.JarTypeLoader;
 import com.strobel.assembler.metadata.MetadataParser;
 import com.strobel.assembler.metadata.MetadataSystem;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.ExceptionUtilities;
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.languages.BytecodeLanguage;
 import com.strobel.decompiler.languages.Language;
 import com.strobel.decompiler.languages.Languages;
 import com.strobel.decompiler.languages.LineNumberPosition;
 import com.strobel.decompiler.languages.TypeDecompilationResults;
 import com.strobel.decompiler.languages.java.JavaFormattingOptions;
 import com.strobel.io.PathHelper;
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.io.Writer;
 import java.nio.charset.Charset;
 import java.util.EnumSet;
 import java.util.Enumeration;
 import java.util.List;
 import java.util.jar.JarEntry;
 import java.util.jar.JarFile;
 import java.util.logging.ConsoleHandler;
 import java.util.logging.Handler;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 
 public class DecompilerDriver
 {
   public static void main(String[] args)
   {
     CommandLineOptions options = new CommandLineOptions();
     JCommander jCommander;
     List<String> typeNames;
     try
     {
       jCommander = new JCommander(options);
       jCommander.setAllowAbbreviatedOptions(true);
       jCommander.parse(args);
       typeNames = options.getInputs();
     }
     catch (Throwable t) {
       System.err.println(ExceptionUtilities.getMessage(t));
       System.exit(-1);
       return;
     }
     
     configureLogging(options);
     
     String jarFile = options.getJarFile();
     boolean decompileJar = !StringUtilities.isNullOrWhitespace(jarFile);
     
     if (options.getPrintVersion()) {
       JCommander.getConsole().println(Procyon.version());
       if (options.getPrintUsage()) {
         jCommander.usage();
       }
       return;
     }
     
     if ((options.getPrintUsage()) || ((typeNames.isEmpty()) && (!decompileJar)))
     {
 
       jCommander.usage();
       return;
     }
     
     DecompilerSettings settings = new DecompilerSettings();
     
     settings.setFlattenSwitchBlocks(options.getFlattenSwitchBlocks());
     settings.setForceExplicitImports(!options.getCollapseImports());
     settings.setForceExplicitTypeArguments(options.getForceExplicitTypeArguments());
     settings.setRetainRedundantCasts(options.getRetainRedundantCasts());
     settings.setShowSyntheticMembers(options.getShowSyntheticMembers());
     settings.setExcludeNestedTypes(options.getExcludeNestedTypes());
     settings.setOutputDirectory(options.getOutputDirectory());
     settings.setIncludeLineNumbersInBytecode(options.getIncludeLineNumbers());
     settings.setRetainPointlessSwitches(options.getRetainPointlessSwitches());
     settings.setUnicodeOutputEnabled(options.isUnicodeOutputEnabled());
     settings.setMergeVariables(options.getMergeVariables());
     settings.setShowDebugLineNumbers(options.getShowDebugLineNumbers());
     settings.setSimplifyMemberReferences(options.getSimplifyMemberReferences());
     settings.setDisableForEachTransforms(options.getDisableForEachTransforms());
     settings.setTypeLoader(new com.strobel.assembler.InputTypeLoader());
     settings.setOutputFileHeaderText("\nDecompiled by Procyon v" + Procyon.version() + "\n");
     
     if (options.isRawBytecode()) {
       settings.setLanguage(Languages.bytecode());
     }
     else if (options.isBytecodeAst()) {
       settings.setLanguage(options.isUnoptimized() ? Languages.bytecodeAstUnoptimized() : Languages.bytecodeAst());
     }
     
 
 
 
     DecompilationOptions decompilationOptions = new DecompilationOptions();
     
     decompilationOptions.setSettings(settings);
     decompilationOptions.setFullDecompilation(true);
     
     if (settings.getFormattingOptions() == null) {
       settings.setFormattingOptions(JavaFormattingOptions.createDefault());
     }
     MetadataSystem metadataSystem;
     if (decompileJar) {
       try {
         decompileJar(jarFile, options, decompilationOptions);
       }
       catch (Throwable t) {
         System.err.println(ExceptionUtilities.getMessage(t));
         System.exit(-1);
       }
     }
     else {
       metadataSystem = new NoRetryMetadataSystem(settings.getTypeLoader());
       
       metadataSystem.setEagerMethodLoadingEnabled(options.isEagerMethodLoadingEnabled());
       
       for (String typeName : typeNames) {
         try {
           if (typeName.endsWith(".jar")) {
             decompileJar(typeName, options, decompilationOptions);
           }
           else {
             decompileType(metadataSystem, typeName, options, decompilationOptions, true);
           }
         }
         catch (Throwable t) {
           t.printStackTrace();
         }
       }
     }
   }
   
   private static void configureLogging(CommandLineOptions options) {
     Logger globalLogger = Logger.getGlobal();
     Logger rootLogger = Logger.getAnonymousLogger().getParent();
     
     for (Handler handler : globalLogger.getHandlers()) {
       globalLogger.removeHandler(handler);
     }
     
     for (Handler handler : rootLogger.getHandlers()) {
       rootLogger.removeHandler(handler);
     }
     
     Level verboseLevel;
     
     switch (options.getVerboseLevel()) {
     case 0: 
       verboseLevel = Level.SEVERE;
       break;
     case 1: 
       verboseLevel = Level.FINE;
       break;
     case 2: 
       verboseLevel = Level.FINER;
       break;
     case 3: 
     default: 
       verboseLevel = Level.FINEST;
     }
     
     
     globalLogger.setLevel(verboseLevel);
     rootLogger.setLevel(verboseLevel);
     
     ConsoleHandler handler = new ConsoleHandler();
     
     handler.setLevel(verboseLevel);
     handler.setFormatter(new BriefLogFormatter());
     
     globalLogger.addHandler(handler);
     rootLogger.addHandler(handler);
   }
   
 
 
   private static void decompileJar(String jarFilePath, CommandLineOptions options, DecompilationOptions decompilationOptions)
     throws IOException
   {
     File jarFile = new File(jarFilePath);
     
     if (!jarFile.exists()) {
       throw new FileNotFoundException("File not found: " + jarFilePath);
     }
     
     DecompilerSettings settings = decompilationOptions.getSettings();
     JarFile jar = new JarFile(jarFile);
     Enumeration<JarEntry> entries = jar.entries();
     
     boolean oldShowSyntheticMembers = settings.getShowSyntheticMembers();
     ITypeLoader oldTypeLoader = settings.getTypeLoader();
     
     settings.setShowSyntheticMembers(false);
     
     settings.setTypeLoader(new CompositeTypeLoader(new ITypeLoader[] { new JarTypeLoader(jar), settings.getTypeLoader() }));
     
 
 
 
 
     try
     {
       MetadataSystem metadataSystem = new NoRetryMetadataSystem(settings.getTypeLoader());
       
       metadataSystem.setEagerMethodLoadingEnabled(options.isEagerMethodLoadingEnabled());
       
       int classesDecompiled = 0;
       
       while (entries.hasMoreElements()) {
         JarEntry entry = (JarEntry)entries.nextElement();
         String name = entry.getName();
         
         if (name.endsWith(".class"))
         {
 
 
           String internalName = StringUtilities.removeRight(name, ".class");
           try
           {
             decompileType(metadataSystem, internalName, options, decompilationOptions, false);
             
             classesDecompiled++; if (classesDecompiled % 100 == 0) {
               metadataSystem = new NoRetryMetadataSystem(settings.getTypeLoader());
             }
           }
           catch (Throwable t) {
             t.printStackTrace();
           }
         }
       }
     } finally {
       settings.setShowSyntheticMembers(oldShowSyntheticMembers);
       settings.setTypeLoader(oldTypeLoader);
     }
   }
   
 
 
 
 
 
   private static void decompileType(MetadataSystem metadataSystem, String typeName, CommandLineOptions commandLineOptions, DecompilationOptions options, boolean includeNested)
     throws IOException
   {
     DecompilerSettings settings = options.getSettings();
     TypeReference type;
     if (typeName.length() == 1)
     {
 
 
 
       MetadataParser parser = new MetadataParser(IMetadataResolver.EMPTY);
       TypeReference reference = parser.parseTypeDescriptor(typeName);
       
       type = metadataSystem.resolve(reference);
     }
     else {
       type = metadataSystem.lookupType(typeName);
     }
     
     TypeDefinition resolvedType;
     
     if ((type == null) || ((resolvedType = type.resolve()) == null)) {
       System.err.printf("!!! ERROR: Failed to load class %s.\n", new Object[] { typeName }); return;
     }
     
     DeobfuscationUtilities.processType(resolvedType);
     
     if ((!includeNested) && ((resolvedType.isNested()) || (resolvedType.isAnonymous()) || (resolvedType.isSynthetic()))) {
       return;
     }
     
     Writer writer = createWriter(resolvedType, settings);
     boolean writeToFile = writer instanceof FileOutputWriter;
     PlainTextOutput output;
     if (writeToFile) {
       output = new PlainTextOutput(writer);
     }
     else {
       output = new AnsiTextOutput(writer, commandLineOptions.getUseLightColorScheme() ? AnsiTextOutput.ColorScheme.LIGHT : AnsiTextOutput.ColorScheme.DARK);
     }
     
 
 
 
 
     output.setUnicodeOutputEnabled(settings.isUnicodeOutputEnabled());
     
     if ((settings.getLanguage() instanceof BytecodeLanguage)) {
       output.setIndentToken("  ");
     }
     
     if (writeToFile) {
       System.out.printf("Decompiling %s...\n", new Object[] { typeName });
     }
     
     TypeDecompilationResults results = settings.getLanguage().decompileType(resolvedType, output, options);
     
     writer.flush();
     
     if (writeToFile) {
       writer.close();
     }
     
 
 
     List<LineNumberPosition> lineNumberPositions = results.getLineNumberPositions();
     
     if (((commandLineOptions.getIncludeLineNumbers()) || (commandLineOptions.getStretchLines())) && ((writer instanceof FileOutputWriter))) {
       EnumSet<LineNumberFormatter.LineNumberOption> lineNumberOptions = EnumSet.noneOf(LineNumberFormatter.LineNumberOption.class);
       
       if (commandLineOptions.getIncludeLineNumbers()) {
         lineNumberOptions.add(LineNumberFormatter.LineNumberOption.LEADING_COMMENTS);
       }
       
       if (commandLineOptions.getStretchLines()) {
         lineNumberOptions.add(LineNumberFormatter.LineNumberOption.STRETCHED);
       }
       
       LineNumberFormatter lineFormatter = new LineNumberFormatter(((FileOutputWriter)writer).getFile(), lineNumberPositions, lineNumberOptions);
       
 
 
 
 
       lineFormatter.reformatFile();
     }
   }
   
   private static Writer createWriter(TypeDefinition type, DecompilerSettings settings) throws IOException {
     String outputDirectory = settings.getOutputDirectory();
     
     if (StringUtilities.isNullOrWhitespace(outputDirectory)) {
       return new java.io.OutputStreamWriter(System.out, settings.isUnicodeOutputEnabled() ? Charset.forName("UTF-8") : Charset.defaultCharset());
     }
     
 
 
 
 
 
     String fileName = type.getName() + settings.getLanguage().getFileExtension();
     String packageName = type.getPackageName();
     String outputPath;
     if (StringUtilities.isNullOrWhitespace(packageName)) {
       outputPath = PathHelper.combine(outputDirectory, fileName);
     }
     else {
       outputPath = PathHelper.combine(outputDirectory, packageName.replace('.', PathHelper.DirectorySeparator), fileName);
     }
     
 
 
 
 
     File outputFile = new File(outputPath);
     File parentFile = outputFile.getParentFile();
     
     if ((parentFile != null) && (!parentFile.exists()) && (!parentFile.mkdirs())) {
       throw new IllegalStateException(String.format("Could not create output directory for file \"%s\".", new Object[] { outputPath }));
     }
     
 
 
 
 
 
     if ((!outputFile.exists()) && (!outputFile.createNewFile())) {
       throw new IllegalStateException(String.format("Could not create output file \"%s\".", new Object[] { outputPath }));
     }
     
 
 
 
 
 
     return new FileOutputWriter(outputFile, settings);
   }
 }


