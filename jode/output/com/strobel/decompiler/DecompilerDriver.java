/* DecompilerDriver - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.beust.jcommander.JCommander;
import com.strobel.Procyon;
import com.strobel.assembler.InputTypeLoader;
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
import com.strobel.decompiler.languages.Languages;
import com.strobel.decompiler.languages.TypeDecompilationResults;
import com.strobel.decompiler.languages.java.JavaFormattingOptions;
import com.strobel.io.PathHelper;

public class DecompilerDriver
{
    public static void main(String[] args) {
	CommandLineOptions options = new CommandLineOptions();
	JCommander jCommander;
	List typeNames;
	try {
	    jCommander = new JCommander(options);
	    jCommander.setAllowAbbreviatedOptions(true);
	    jCommander.parse(args);
	    typeNames = options.getInputs();
	} catch (Throwable PUSH) {
	    Throwable t = POP;
	    System.err.println(ExceptionUtilities.getMessage(t));
	    System.exit(-1);
	    return;
	}
	configureLogging(options);
	String jarFile;
    label_1453:
	{
	    jarFile = options.getJarFile();
	    if (StringUtilities.isNullOrWhitespace(jarFile))
		PUSH false;
	    else
		PUSH true;
	    break label_1453;
	}
	boolean decompileJar = POP;
	DecompilerSettings settings;
    label_1454:
	{
	    if (!options.getPrintVersion()) {
		if (!options.getPrintUsage()
		    && (!typeNames.isEmpty() || decompileJar)) {
		    settings = new DecompilerSettings();
		    settings.setFlattenSwitchBlocks
			(options.getFlattenSwitchBlocks());
		    PUSH settings;
		    if (options.getCollapseImports())
			PUSH false;
		    else
			PUSH true;
		} else {
		    jCommander.usage();
		    return;
		}
	    } else {
		JCommander.getConsole().println(Procyon.version());
		if (options.getPrintUsage())
		    jCommander.usage();
		return;
	    }
	}
	((DecompilerSettings) POP).setForceExplicitImports(POP);
	settings.setForceExplicitTypeArguments
	    (options.getForceExplicitTypeArguments());
	settings.setRetainRedundantCasts(options.getRetainRedundantCasts());
	settings.setShowSyntheticMembers(options.getShowSyntheticMembers());
	settings.setExcludeNestedTypes(options.getExcludeNestedTypes());
	settings.setOutputDirectory(options.getOutputDirectory());
	settings
	    .setIncludeLineNumbersInBytecode(options.getIncludeLineNumbers());
	settings
	    .setRetainPointlessSwitches(options.getRetainPointlessSwitches());
	settings.setUnicodeOutputEnabled(options.isUnicodeOutputEnabled());
	settings.setMergeVariables(options.getMergeVariables());
	settings.setShowDebugLineNumbers(options.getShowDebugLineNumbers());
	settings.setSimplifyMemberReferences
	    (options.getSimplifyMemberReferences());
	settings.setDisableForEachTransforms
	    (options.getDisableForEachTransforms());
	settings.setTypeLoader(new InputTypeLoader());
	settings.setOutputFileHeaderText("\nDecompiled by Procyon v"
					 + Procyon.version() + "\n");
    label_1456:
	{
	label_1455:
	    {
		if (!options.isRawBytecode()) {
		    if (!options.isBytecodeAst())
			break label_1456;
		    PUSH settings;
		    if (!options.isUnoptimized())
			PUSH Languages.bytecodeAst();
		    else
			PUSH Languages.bytecodeAstUnoptimized();
		} else {
		    settings.setLanguage(Languages.bytecode());
		    break label_1456;
		}
	    }
	    ((DecompilerSettings) POP).setLanguage(POP);
	}
	DecompilationOptions decompilationOptions = new DecompilationOptions();
	decompilationOptions.setSettings(settings);
    label_1457:
	{
	    decompilationOptions.setFullDecompilation(true);
	    if (settings.getFormattingOptions() == null)
		settings.setFormattingOptions(JavaFormattingOptions
						  .createDefault());
	    break label_1457;
	}
	if (!decompileJar) {
	    MetadataSystem metadataSystem
		= new NoRetryMetadataSystem(settings.getTypeLoader());
	    metadataSystem.setEagerMethodLoadingEnabled
		(options.isEagerMethodLoadingEnabled());
	    Iterator i$ = typeNames.iterator();
	    while (i$.hasNext()) {
		String typeName = (String) i$.next();
	    label_1458:
		{
		    try {
			if (typeName.endsWith(".jar")) {
			    try {
				decompileJar(typeName, options,
					     decompilationOptions);
				continue;
			    } catch (Throwable PUSH) {
				/* empty */
			    }
			    break label_1458;
			}
		    } catch (Throwable PUSH) {
			break label_1458;
		    }
		    try {
			decompileType(metadataSystem, typeName, options,
				      decompilationOptions, true);
			continue;
		    } catch (Throwable PUSH) {
			/* empty */
		    }
		}
		Throwable t = POP;
		t.printStackTrace();
	    }
	} else {
	    try {
		decompileJar(jarFile, options, decompilationOptions);
	    } catch (Throwable PUSH) {
		Throwable t = POP;
		System.err.println(ExceptionUtilities.getMessage(t));
		System.exit(-1);
	    }
	}
	return;
	break label_1455;
	break label_1454;
    }
    
    private static void configureLogging(CommandLineOptions options) {
	Logger globalLogger = Logger.getGlobal();
	Logger rootLogger = Logger.getAnonymousLogger().getParent();
	Handler[] arr$ = globalLogger.getHandlers();
	int len$ = arr$.length;
	int i$ = 0;
	for (;;) {
	    if (i$ >= len$) {
		arr$ = rootLogger.getHandlers();
		len$ = arr$.length;
		i$ = 0;
		for (;;) {
		    if (i$ >= len$) {
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
		    Handler handler = arr$[i$];
		    rootLogger.removeHandler(handler);
		    i$++;
		}
	    }
	    Handler handler = arr$[i$];
	    globalLogger.removeHandler(handler);
	    i$++;
	}
    }
    
    private static void decompileJar(String jarFilePath,
				     CommandLineOptions options,
				     DecompilationOptions decompilationOptions)
	throws IOException {
	File jarFile = new File(jarFilePath);
	if (jarFile.exists()) {
	    DecompilerSettings settings = decompilationOptions.getSettings();
	    JarFile jar = new JarFile(jarFile);
	    Enumeration entries = jar.entries();
	    boolean oldShowSyntheticMembers
		= settings.getShowSyntheticMembers();
	    ITypeLoader oldTypeLoader = settings.getTypeLoader();
	    settings.setShowSyntheticMembers(false);
	    settings.setTypeLoader
		(new CompositeTypeLoader(new ITypeLoader[]
					 { new JarTypeLoader(jar),
					   settings.getTypeLoader() }));
	label_1460:
	    {
		MetadataSystem metadataSystem;
		int classesDecompiled;
		try {
		    metadataSystem
			= new NoRetryMetadataSystem(settings.getTypeLoader());
		    metadataSystem.setEagerMethodLoadingEnabled
			(options.isEagerMethodLoadingEnabled());
		    classesDecompiled = 0;
		} finally {
		    break label_1460;
		}
		for (;;) {
		    try {
			if (entries.hasMoreElements()) {
			    JarEntry entry = (JarEntry) entries.nextElement();
			    String name = entry.getName();
			    if (name.endsWith(".class")) {
				String internalName
				    = StringUtilities.removeRight(name,
								  ".class");
			    label_1459:
				{
				    try {
					decompileType(metadataSystem,
						      internalName, options,
						      decompilationOptions,
						      false);
					if (++classesDecompiled % 100 != 0)
					    continue;
				    } catch (Throwable PUSH) {
					break label_1459;
				    }
				    try {
					metadataSystem
					    = (new NoRetryMetadataSystem
					       (settings.getTypeLoader()));
					continue;
				    } catch (Throwable PUSH) {
					/* empty */
				    }
				}
				Throwable t = POP;
				t.printStackTrace();
			    }
			}
		    } finally {
			break;
		    }
		    settings.setShowSyntheticMembers(oldShowSyntheticMembers);
		    settings.setTypeLoader(oldTypeLoader);
		}
	    }
	    Object object = POP;
	    settings.setShowSyntheticMembers(oldShowSyntheticMembers);
	    settings.setTypeLoader(oldTypeLoader);
	    throw object;
	}
	throw new FileNotFoundException("File not found: " + jarFilePath);
    }
    
    private static void decompileType(MetadataSystem metadataSystem,
				      String typeName,
				      CommandLineOptions commandLineOptions,
				      DecompilationOptions options,
				      boolean includeNested)
	throws IOException {
	DecompilerSettings settings;
	TypeReference type;
    label_1461:
	{
	    settings = options.getSettings();
	    if (typeName.length() != 1)
		type = metadataSystem.lookupType(typeName);
	    else {
		MetadataParser parser
		    = new MetadataParser(IMetadataResolver.EMPTY);
		TypeReference reference = parser.parseTypeDescriptor(typeName);
		type = metadataSystem.resolve(reference);
	    }
	    break label_1461;
	}
	TypeDefinition resolvedType;
	Writer writer;
	boolean writeToFile;
	PlainTextOutput output;
    label_1463:
	{
	label_1462:
	    {
		if (type != null && (resolvedType = type.resolve()) != null) {
		    DeobfuscationUtilities.processType(resolvedType);
		    if (!includeNested && (resolvedType.isNested()
					   || resolvedType.isAnonymous()
					   || resolvedType.isSynthetic()))
			return;
		    writer = createWriter(resolvedType, settings);
		    writeToFile = writer instanceof FileOutputWriter;
		    if (!writeToFile) {
			PUSH new AnsiTextOutput;
			DUP
			PUSH writer;
			if (!commandLineOptions.getUseLightColorScheme())
			    PUSH AnsiTextOutput.ColorScheme.DARK;
			else
			    PUSH AnsiTextOutput.ColorScheme.LIGHT;
		    } else {
			output = new PlainTextOutput(writer);
			break label_1463;
		    }
		} else {
		    System.err.printf("!!! ERROR: Failed to load class %s.\n",
				      new Object[] { typeName });
		    return;
		}
	    }
	    ((UNCONSTRUCTED)POP).AnsiTextOutput(POP, POP);
	    output = POP;
	}
    label_1465:
	{
	label_1464:
	    {
		output.setUnicodeOutputEnabled(settings
						   .isUnicodeOutputEnabled());
		if (settings.getLanguage() instanceof BytecodeLanguage)
		    output.setIndentToken("  ");
		break label_1464;
	    }
	    if (writeToFile)
		System.out.printf("Decompiling %s...\n",
				  new Object[] { typeName });
	    break label_1465;
	}
	TypeDecompilationResults results
	    = settings.getLanguage().decompileType(resolvedType, output,
						   options);
    label_1466:
	{
	    writer.flush();
	    if (writeToFile)
		writer.close();
	    break label_1466;
	}
	List lineNumberPositions = results.getLineNumberPositions();
	if ((commandLineOptions.getIncludeLineNumbers()
	     || commandLineOptions.getStretchLines())
	    && writer instanceof FileOutputWriter) {
	    EnumSet lineNumberOptions;
	label_1468:
	    {
	    label_1467:
		{
		    lineNumberOptions
			= (EnumSet.noneOf
			   (LineNumberFormatter.LineNumberOption.class));
		    if (commandLineOptions.getIncludeLineNumbers())
			lineNumberOptions.add
			    (LineNumberFormatter.LineNumberOption
			     .LEADING_COMMENTS);
		    break label_1467;
		}
		if (commandLineOptions.getStretchLines())
		    lineNumberOptions
			.add(LineNumberFormatter.LineNumberOption.STRETCHED);
		break label_1468;
	    }
	    LineNumberFormatter lineFormatter
		= new LineNumberFormatter(((FileOutputWriter) writer)
					      .getFile(),
					  lineNumberPositions,
					  lineNumberOptions);
	    lineFormatter.reformatFile();
	}
	break label_1462;
    }
    
    private static Writer createWriter(TypeDefinition type,
				       DecompilerSettings settings)
	throws IOException {
	String outputDirectory = settings.getOutputDirectory();
	String outputPath;
    label_1470:
	{
	    if (!StringUtilities.isNullOrWhitespace(outputDirectory)) {
		String fileName
		    = (type.getName()
		       + settings.getLanguage().getFileExtension());
		String packageName = type.getPackageName();
		if (!StringUtilities.isNullOrWhitespace(packageName))
		    outputPath
			= (PathHelper.combine
			   (outputDirectory,
			    packageName.replace('.',
						PathHelper.DirectorySeparator),
			    fileName));
		else
		    outputPath = PathHelper.combine(outputDirectory, fileName);
	    } else {
		PUSH new OutputStreamWriter;
		DUP
	    label_1469:
		{
		    PUSH System.out;
		    if (!settings.isUnicodeOutputEnabled())
			PUSH Charset.defaultCharset();
		    else
			PUSH Charset.forName("UTF-8");
		    break label_1469;
		}
		((UNCONSTRUCTED)POP).OutputStreamWriter(POP, POP);
		return POP;
	    }
	}
	File outputFile = new File(outputPath);
	File parentFile = outputFile.getParentFile();
	if (parentFile == null || parentFile.exists() || parentFile.mkdirs()) {
	    if (outputFile.exists() || outputFile.createNewFile())
		return new FileOutputWriter(outputFile, settings);
	    throw new IllegalStateException
		      (String.format("Could not create output file \"%s\".",
				     new Object[] { outputPath }));
	}
	throw new IllegalStateException
		  (String.format
		   ("Could not create output directory for file \"%s\".",
		    new Object[] { outputPath }));
	break label_1470;
    }
}
