/* BytecodeAstLanguage - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import javax.lang.model.element.Modifier;

import com.strobel.assembler.metadata.Flags;
import com.strobel.assembler.metadata.MethodBody;
import com.strobel.assembler.metadata.MethodDefinition;
import com.strobel.assembler.metadata.ParameterDefinition;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.core.ArrayUtilities;
import com.strobel.core.ExceptionUtilities;
import com.strobel.core.StringUtilities;
import com.strobel.core.VerifyArgument;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.DecompilerHelpers;
import com.strobel.decompiler.ITextOutput;
import com.strobel.decompiler.NameSyntax;
import com.strobel.decompiler.PlainTextOutput;
import com.strobel.decompiler.ast.AstBuilder;
import com.strobel.decompiler.ast.AstOptimizationStep;
import com.strobel.decompiler.ast.AstOptimizer;
import com.strobel.decompiler.ast.Block;
import com.strobel.decompiler.ast.Expression;
import com.strobel.decompiler.ast.Variable;

public class BytecodeAstLanguage extends Language
{
    private final String _name;
    private final boolean _inlineVariables;
    private final AstOptimizationStep _abortBeforeStep;
    
    public BytecodeAstLanguage() {
	this("Bytecode AST", true, AstOptimizationStep.None);
    }
    
    private BytecodeAstLanguage(String name, boolean inlineVariables,
				AstOptimizationStep abortBeforeStep) {
	_name = name;
	_inlineVariables = inlineVariables;
	_abortBeforeStep = abortBeforeStep;
    }
    
    public String getName() {
	return _name;
    }
    
    public String getFileExtension() {
	return ".jvm";
    }
    
    public TypeDecompilationResults decompileType
	(TypeDefinition type, ITextOutput output,
	 DecompilationOptions options) {
	writeTypeHeader(type, output);
	output.writeLine(" {");
	output.indent();
	try {
	    boolean first = true;
	    Iterator i$ = type.getDeclaredMethods().iterator();
	    GOTO flow_2_83_
	} finally {
	    GOTO flow_12_84_
	}
    flow_2_83_:
	Iterator i$;
	if (!i$.hasNext()) {
	    IF (options.getSettings().getExcludeNestedTypes())
		GOTO flow_11_85_
	    i$ = type.getDeclaredTypes().iterator();
	    GOTO flow_9_86_
	}
	MethodDefinition method;
    label_1519:
	{
	    method = (MethodDefinition) i$.next();
	    boolean first;
	    if (first)
		first = false;
	    else
		output.writeLine();
	    break label_1519;
	}
	decompileMethod(method, output, options);
	GOTO flow_2_83_
    flow_9_86_:
	Iterator i$;
	IF (!i$.hasNext())
	    GOTO flow_11_85_
	TypeDefinition innerType = (TypeDefinition) i$.next();
	output.writeLine();
	decompileType(innerType, output, options);
	GOTO flow_9_86_
    flow_11_85_:
	output.unindent();
	output.writeLine("}");
	GOTO flow_13_87_
    flow_12_84_:
	Object object = POP;
	output.unindent();
	output.writeLine("}");
	throw object;
    flow_13_87_:
	return new TypeDecompilationResults(null);
	GOTO END_OF_METHOD
    }
    
    public void decompileMethod(MethodDefinition method, ITextOutput output,
				DecompilationOptions options) {
	VerifyArgument.notNull(method, "method");
	VerifyArgument.notNull(output, "output");
	VerifyArgument.notNull(options, "options");
	writeMethodHeader(method, output);
	MethodBody body = method.getBody();
	if (body != null) {
	    DecompilerContext context = new DecompilerContext();
	    context.setCurrentMethod(method);
	    context.setCurrentType(method.getDeclaringType());
	    Block methodAst = new Block();
	    output.writeLine(" {");
	    output.indent();
	label_1520:
	    {
		try {
		    methodAst.getBody().addAll
			(AstBuilder.build(body, _inlineVariables, context));
		    if (_abortBeforeStep == null)
			break label_1520;
		} catch (Throwable PUSH) {
		    GOTO flow_21_88_
		} finally {
		    GOTO flow_23_89_
		}
		try {
		    AstOptimizer.optimize(context, methodAst,
					  _abortBeforeStep);
		} catch (Throwable PUSH) {
		    GOTO flow_21_88_
		} finally {
		    GOTO flow_23_89_
		}
	    }
	    try {
		java.util.Set allVariables = new LinkedHashSet();
		Iterator i$ = methodAst.getSelfAndChildrenRecursive
				  (Expression.class).iterator();
		GOTO flow_6_90_
	    } catch (Throwable PUSH) {
		GOTO flow_21_88_
	    } finally {
		GOTO flow_23_89_
	    }
	}
	output.writeDelimiter(";");
	output.writeLine();
    flow_6_90_:
	Iterator i$;
	java.util.Set allVariables;
	if (!i$.hasNext()) {
	    IF (allVariables.isEmpty())
		GOTO flow_19_91_
	    i$ = allVariables.iterator();
	    GOTO flow_12_92_
	}
	Expression e = (Expression) i$.next();
	Object operand = e.getOperand();
	IF (!(operand instanceof Variable)
	    || ((Variable) operand).isParameter())
	    GOTO flow_6_90_
	allVariables.add((Variable) operand);
	GOTO flow_6_90_
    flow_12_92_:
	Iterator i$;
	if (!i$.hasNext())
	    output.writeLine();
	    GOTO flow_19_91_
	Variable variable = (Variable) i$.next();
	output.writeDefinition(variable.getName(), variable);
    label_1522:
	{
	label_1521:
	    {
		TypeReference type = variable.getType();
		if (type != null) {
		    output.write(" : ");
		    DecompilerHelpers.writeType(output, type,
						NameSyntax.SHORT_TYPE_NAME);
		}
		break label_1521;
	    }
	    if (variable.isGenerated())
		output.write(" [generated]");
	    break label_1522;
	}
	output.writeLine();
	GOTO flow_12_92_
    flow_19_91_:
	try {
	    Block methodAst;
	    methodAst.writeTo(output);
	    output.unindent();
	    output.writeLine("}");
	    GOTO END_OF_METHOD
	} catch (Throwable PUSH) {
	    GOTO flow_21_88_
	} finally {
	    GOTO flow_23_89_
	}
    flow_21_88_:
	try {
	    Throwable t = POP;
	    writeError(output, t);
	} finally {
	    GOTO flow_23_89_
	}
	output.unindent();
	output.writeLine("}");
	GOTO END_OF_METHOD
    flow_23_89_:
	Object object = POP;
	output.unindent();
	output.writeLine("}");
	throw object;
    }
    
    private static void writeError(ITextOutput output, Throwable t) {
	List lines
	    = StringUtilities.split(ExceptionUtilities.getStackTraceString(t),
				    true, '\r', new char[] { '\n' });
	Iterator i$ = lines.iterator();
	for (;;) {
	    IF (!i$.hasNext())
		/* empty */
	    String line = (String) i$.next();
	    output.writeComment("// " + line.replace("\t", "    "));
	    output.writeLine();
	}
    }
    
    private void writeTypeHeader(TypeDefinition type, ITextOutput output) {
	long flags;
    label_1523:
	{
	    flags = type.getFlags() & 0x7e19L;
	    if (!type.isInterface()) {
		if (type.isEnum())
		    flags &= 0x7L;
	    } else
		flags &= ~0x400L;
	    break label_1523;
	}
	Iterator i$ = Flags.asModifierSet(flags).iterator();
    label_1524:
	{
	    for (;;) {
		if (!i$.hasNext()) {
		    if (!type.isInterface()) {
			if (!type.isEnum())
			    output.writeKeyword("class");
			else
			    output.writeKeyword("enum");
		    } else if (!type.isAnnotation())
			output.writeKeyword("interface");
		    else
			output.writeKeyword("@interface");
		    break;
		}
		Modifier modifier = (Modifier) i$.next();
		output.writeKeyword(modifier.toString());
		output.write(' ');
	    }
	}
	output.write(' ');
	DecompilerHelpers.writeType(output, type, NameSyntax.TYPE_NAME, true);
	break label_1524;
    }
    
    private void writeMethodHeader(MethodDefinition method,
				   ITextOutput output) {
    label_1525:
	{
	    if (!method.isTypeInitializer()) {
		if (!method.getDeclaringType().isInterface()) {
		    Iterator i$ = Flags.asModifierSet
				      (method.getFlags() & 0xd3fL).iterator();
		    while (i$.hasNext()) {
			Modifier modifier = (Modifier) i$.next();
			output.writeKeyword(modifier.toString());
			output.write(' ');
		    }
		}
	    } else {
		output.writeKeyword("static");
		return;
	    }
	}
	if (!method.isTypeInitializer()) {
	    DecompilerHelpers.writeType(output, method.getReturnType(),
					NameSyntax.TYPE_NAME);
	label_1526:
	    {
		output.write(' ');
		if (!method.isConstructor())
		    output.writeReference(method.getName(), method);
		else
		    output.writeReference(method.getDeclaringType().getName(),
					  method.getDeclaringType());
		break label_1526;
	    }
	    output.write("(");
	    List parameters = method.getParameters();
	    int i = 0;
	    for (;;) {
		if (i >= parameters.size()) {
		    output.write(")");
		    break;
		}
		ParameterDefinition parameter;
	    label_1527:
		{
		    parameter = (ParameterDefinition) parameters.get(i);
		    if (i != 0)
			output.write(", ");
		    break label_1527;
		}
		DecompilerHelpers.writeType(output,
					    parameter.getParameterType(),
					    NameSyntax.TYPE_NAME);
		output.write(' ');
		output.writeReference(parameter.getName(), parameter);
		i++;
	    }
	}
	break label_1525;
    }
    
    public String typeToString(TypeReference type, boolean includePackage) {
	ITextOutput output = new PlainTextOutput();
	PUSH output;
    label_1528:
	{
	    PUSH type;
	    if (!includePackage)
		PUSH NameSyntax.SHORT_TYPE_NAME;
	    else
		PUSH NameSyntax.TYPE_NAME;
	    break label_1528;
	}
	DecompilerHelpers.writeType(POP, POP, POP);
	return output.toString();
    }
    
    public static List getDebugLanguages() {
	AstOptimizationStep[] steps = AstOptimizationStep.values();
	BytecodeAstLanguage[] languages
	    = new BytecodeAstLanguage[steps.length];
	languages[0] = new BytecodeAstLanguage("Bytecode AST (Unoptimized)",
					       false, steps[0]);
	String nextName = "Bytecode AST (Variable Splitting)";
	int i = 1;
	for (;;) {
	    if (i >= languages.length)
		return ArrayUtilities.asUnmodifiableList(languages);
	    languages[i]
		= new BytecodeAstLanguage(nextName, true, steps[i - 1]);
	    nextName = "Bytecode AST (After " + steps[i - 1].name() + ")";
	    i++;
	}
    }
}
