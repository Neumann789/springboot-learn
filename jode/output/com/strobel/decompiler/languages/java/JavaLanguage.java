/* JavaLanguage - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java;
import java.util.List;

import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.core.Predicate;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.ITextOutput;
import com.strobel.decompiler.languages.Language;
import com.strobel.decompiler.languages.TypeDecompilationResults;
import com.strobel.decompiler.languages.java.ast.AstBuilder;
import com.strobel.decompiler.languages.java.ast.CompilationUnit;
import com.strobel.decompiler.languages.java.ast.transforms.IAstTransform;

public class JavaLanguage extends Language
{
    private final String _name;
    private final Predicate _transformAbortCondition;
    
    public JavaLanguage() {
	this("Java", null);
    }
    
    private JavaLanguage(String name, Predicate transformAbortCondition) {
	_name = name;
	_transformAbortCondition = transformAbortCondition;
    }
    
    public final String getName() {
	return _name;
    }
    
    public final String getFileExtension() {
	return ".java";
    }
    
    public TypeDecompilationResults decompileType
	(TypeDefinition type, ITextOutput output,
	 DecompilationOptions options) {
	AstBuilder astBuilder = buildAst(type, options);
	List lineNumberPositions = astBuilder.generateCode(output);
	return new TypeDecompilationResults(lineNumberPositions);
    }
    
    public CompilationUnit decompileTypeToAst(TypeDefinition type,
					      DecompilationOptions options) {
	return buildAst(type, options).getCompilationUnit();
    }
    
    private AstBuilder buildAst(TypeDefinition type,
				DecompilationOptions options) {
	AstBuilder builder = createAstBuilder(options, type, false);
	builder.addType(type);
	runTransforms(builder, options, null);
	return builder;
    }
    
    private AstBuilder createAstBuilder(DecompilationOptions options,
					TypeDefinition currentType,
					boolean isSingleMember) {
	DecompilerSettings settings = options.getSettings();
	DecompilerContext context = new DecompilerContext();
	context.setCurrentType(currentType);
	context.setSettings(settings);
	return new AstBuilder(context);
    }
    
    private void runTransforms(AstBuilder astBuilder,
			       DecompilationOptions options,
			       IAstTransform additionalTransform) {
	astBuilder.runTransformations(_transformAbortCondition);
	if (additionalTransform != null)
	    additionalTransform.run(astBuilder.getCompilationUnit());
	return;
    }
}
