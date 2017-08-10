 package com.strobel.decompiler.languages;
 
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
 import com.strobel.decompiler.DecompilerSettings;
 import com.strobel.decompiler.ITextOutput;
 import com.strobel.decompiler.NameSyntax;
 import com.strobel.decompiler.PlainTextOutput;
 import com.strobel.decompiler.ast.AstBuilder;
 import com.strobel.decompiler.ast.AstOptimizationStep;
 import com.strobel.decompiler.ast.AstOptimizer;
 import com.strobel.decompiler.ast.Block;
 import com.strobel.decompiler.ast.Expression;
 import com.strobel.decompiler.ast.Variable;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Set;
 import javax.lang.model.element.Modifier;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class BytecodeAstLanguage
   extends Language
 {
   private final String _name;
   private final boolean _inlineVariables;
   private final AstOptimizationStep _abortBeforeStep;
   
   public BytecodeAstLanguage()
   {
     this("Bytecode AST", true, AstOptimizationStep.None);
   }
   
   private BytecodeAstLanguage(String name, boolean inlineVariables, AstOptimizationStep abortBeforeStep) {
     this._name = name;
     this._inlineVariables = inlineVariables;
     this._abortBeforeStep = abortBeforeStep;
   }
   
   public String getName()
   {
     return this._name;
   }
   
   public String getFileExtension()
   {
     return ".jvm";
   }
   
   public TypeDecompilationResults decompileType(TypeDefinition type, ITextOutput output, DecompilationOptions options)
   {
     writeTypeHeader(type, output);
     
     output.writeLine(" {");
     output.indent();
     try
     {
       boolean first = true;
       
       for (MethodDefinition method : type.getDeclaredMethods()) {
         if (!first) {
           output.writeLine();
         }
         else {
           first = false;
         }
         
         decompileMethod(method, output, options);
       }
       
       if (!options.getSettings().getExcludeNestedTypes()) {
         for (TypeDefinition innerType : type.getDeclaredTypes()) {
           output.writeLine();
           decompileType(innerType, output, options);
         }
       }
     }
     finally {
       output.unindent();
       output.writeLine("}");
     }
     
     return new TypeDecompilationResults(null);
   }
   
 
   public void decompileMethod(MethodDefinition method, ITextOutput output, DecompilationOptions options)
   {
     VerifyArgument.notNull(method, "method");
     VerifyArgument.notNull(output, "output");
     VerifyArgument.notNull(options, "options");
     
     writeMethodHeader(method, output);
     
     MethodBody body = method.getBody();
     
     if (body == null) {
       output.writeDelimiter(";");
       output.writeLine();
       return;
     }
     
     DecompilerContext context = new DecompilerContext();
     
     context.setCurrentMethod(method);
     context.setCurrentType(method.getDeclaringType());
     
     Block methodAst = new Block();
     
     output.writeLine(" {");
     output.indent();
     try
     {
       methodAst.getBody().addAll(AstBuilder.build(body, this._inlineVariables, context));
       
       if (this._abortBeforeStep != null) {
         AstOptimizer.optimize(context, methodAst, this._abortBeforeStep);
       }
       
       Set<Variable> allVariables = new LinkedHashSet();
       
       for (Expression e : methodAst.getSelfAndChildrenRecursive(Expression.class)) {
         Object operand = e.getOperand();
         
         if (((operand instanceof Variable)) && (!((Variable)operand).isParameter())) {
           allVariables.add((Variable)operand);
         }
       }
       
       if (!allVariables.isEmpty()) {
         for (Variable variable : allVariables) {
           output.writeDefinition(variable.getName(), variable);
           
           TypeReference type = variable.getType();
           
           if (type != null) {
             output.write(" : ");
             DecompilerHelpers.writeType(output, type, NameSyntax.SHORT_TYPE_NAME);
           }
           
           if (variable.isGenerated()) {
             output.write(" [generated]");
           }
           
           output.writeLine();
         }
         
         output.writeLine();
       }
       
       methodAst.writeTo(output);
     }
     catch (Throwable t) {
       writeError(output, t);
     }
     finally {
       output.unindent();
       output.writeLine("}");
     }
   }
   
   private static void writeError(ITextOutput output, Throwable t) {
     List<String> lines = StringUtilities.split(ExceptionUtilities.getStackTraceString(t), true, '\r', new char[] { '\n' });
     
 
 
 
 
 
     for (String line : lines) {
       output.writeComment("// " + line.replace("\t", "    "));
       output.writeLine();
     }
   }
   
   private void writeTypeHeader(TypeDefinition type, ITextOutput output) {
     long flags = type.getFlags() & 0x7E19;
     
     if (type.isInterface()) {
       flags &= 0xFFFFFFFFFFFFFBFF;
     }
     else if (type.isEnum()) {
       flags &= 0x7;
     }
     
     for (Modifier modifier : Flags.asModifierSet(flags)) {
       output.writeKeyword(modifier.toString());
       output.write(' ');
     }
     
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
   }
   
   private void writeMethodHeader(MethodDefinition method, ITextOutput output) {
     if (method.isTypeInitializer()) {
       output.writeKeyword("static");
       return;
     }
     
     if (!method.getDeclaringType().isInterface()) {
       for (Modifier modifier : Flags.asModifierSet(method.getFlags() & 0xD3F)) {
         output.writeKeyword(modifier.toString());
         output.write(' ');
       }
     }
     
     if (!method.isTypeInitializer()) {
       DecompilerHelpers.writeType(output, method.getReturnType(), NameSyntax.TYPE_NAME);
       output.write(' ');
       
       if (method.isConstructor()) {
         output.writeReference(method.getDeclaringType().getName(), method.getDeclaringType());
       }
       else {
         output.writeReference(method.getName(), method);
       }
       
       output.write("(");
       
       List<ParameterDefinition> parameters = method.getParameters();
       
       for (int i = 0; i < parameters.size(); i++) {
         ParameterDefinition parameter = (ParameterDefinition)parameters.get(i);
         
         if (i != 0) {
           output.write(", ");
         }
         
         DecompilerHelpers.writeType(output, parameter.getParameterType(), NameSyntax.TYPE_NAME);
         output.write(' ');
         output.writeReference(parameter.getName(), parameter);
       }
       
       output.write(")");
     }
   }
   
   public String typeToString(TypeReference type, boolean includePackage)
   {
     ITextOutput output = new PlainTextOutput();
     DecompilerHelpers.writeType(output, type, includePackage ? NameSyntax.TYPE_NAME : NameSyntax.SHORT_TYPE_NAME);
     return output.toString();
   }
   
   public static List<BytecodeAstLanguage> getDebugLanguages() {
     AstOptimizationStep[] steps = AstOptimizationStep.values();
     BytecodeAstLanguage[] languages = new BytecodeAstLanguage[steps.length];
     
     languages[0] = new BytecodeAstLanguage("Bytecode AST (Unoptimized)", false, steps[0]);
     
     String nextName = "Bytecode AST (Variable Splitting)";
     
     for (int i = 1; i < languages.length; i++) {
       languages[i] = new BytecodeAstLanguage(nextName, true, steps[(i - 1)]);
       nextName = "Bytecode AST (After " + steps[(i - 1)].name() + ")";
     }
     
     return ArrayUtilities.asUnmodifiableList(languages);
   }
 }


