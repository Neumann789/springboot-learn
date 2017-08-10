/* Decompiler - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler;
import com.strobel.assembler.InputTypeLoader;
import com.strobel.assembler.metadata.DeobfuscationUtilities;
import com.strobel.assembler.metadata.IMetadataResolver;
import com.strobel.assembler.metadata.MetadataParser;
import com.strobel.assembler.metadata.MetadataSystem;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.core.VerifyArgument;
import com.strobel.decompiler.languages.java.JavaFormattingOptions;

public final class Decompiler
{
    public static void decompile(String internalName, ITextOutput output) {
	decompile(internalName, output, new DecompilerSettings());
    }
    
    public static void decompile(String internalName, ITextOutput output,
				 DecompilerSettings settings) {
	VerifyArgument.notNull(internalName, "internalName");
    label_1450:
	{
	    VerifyArgument.notNull(settings, "settings");
	    if (settings.getTypeLoader() == null)
		PUSH new InputTypeLoader();
	    else
		PUSH settings.getTypeLoader();
	    break label_1450;
	}
	com.strobel.assembler.metadata.ITypeLoader typeLoader = POP;
	TypeReference type;
    label_1451:
	{
	    MetadataSystem metadataSystem = new MetadataSystem(typeLoader);
	    if (internalName.length() != 1)
		type = metadataSystem.lookupType(internalName);
	    else {
		MetadataParser parser
		    = new MetadataParser(IMetadataResolver.EMPTY);
		TypeReference reference
		    = parser.parseTypeDescriptor(internalName);
		type = metadataSystem.resolve(reference);
	    }
	    break label_1451;
	}
	TypeDefinition resolvedType;
	DecompilationOptions options;
    label_1452:
	{
	    if (type != null && (resolvedType = type.resolve()) != null) {
		DeobfuscationUtilities.processType(resolvedType);
		options = new DecompilationOptions();
		options.setSettings(settings);
		options.setFullDecompilation(true);
		if (settings.getFormattingOptions() == null)
		    settings.setFormattingOptions(JavaFormattingOptions
						      .createDefault());
	    } else {
		output.writeLine("!!! ERROR: Failed to load class %s.",
				 new Object[] { internalName });
		return;
	    }
	}
	settings.getLanguage().decompileType(resolvedType, output, options);
	break label_1452;
    }
}
