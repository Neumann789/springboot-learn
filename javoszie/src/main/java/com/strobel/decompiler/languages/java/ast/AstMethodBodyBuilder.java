 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.assembler.metadata.BuiltinTypes;
 import com.strobel.assembler.metadata.DynamicCallSite;
 import com.strobel.assembler.metadata.FieldDefinition;
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.IGenericInstance;
 import com.strobel.assembler.metadata.JvmType;
 import com.strobel.assembler.metadata.MetadataHelper;
 import com.strobel.assembler.metadata.MetadataParser;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodHandle;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.StringComparison;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.DecompilationOptions;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.DecompilerSettings;
 import com.strobel.decompiler.PlainTextOutput;
 import com.strobel.decompiler.ast.AstCode;
 import com.strobel.decompiler.ast.Block;
 import com.strobel.decompiler.ast.CaseBlock;
 import com.strobel.decompiler.ast.CatchBlock;
 import com.strobel.decompiler.ast.Condition;
 import com.strobel.decompiler.ast.Label;
 import com.strobel.decompiler.ast.Lambda;
 import com.strobel.decompiler.ast.Loop;
 import com.strobel.decompiler.ast.Node;
 import com.strobel.decompiler.ast.Range;
 import com.strobel.decompiler.ast.Switch;
 import com.strobel.decompiler.ast.TryCatchBlock;
 import com.strobel.decompiler.ast.Variable;
 import com.strobel.decompiler.patterns.AnyNode;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.OptionalNode;
 import com.strobel.decompiler.semantics.ResolveResult;
 import com.strobel.util.ContractUtils;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Set;
 
 public class AstMethodBodyBuilder
 {
   private final AstBuilder _astBuilder;
   private final MethodDefinition _method;
   private final MetadataParser _parser;
   private final DecompilerContext _context;
   private final Set<Variable> _localVariablesToDefine = new LinkedHashSet();
   
 
 
 
 
   private static final INode LAMBDA_BODY_PATTERN = new com.strobel.decompiler.patterns.Choice(new INode[] { new BlockStatement(new Statement[] { new ExpressionStatement(new AnyNode("body").toExpression()), new OptionalNode(new ReturnStatement(-34)).toStatement() }), new BlockStatement(new Statement[] { new ReturnStatement(-34, new AnyNode("body").toExpression()) }), new AnyNode("body").toBlockStatement() });
   
 
 
 
 
 
 
 
 
 
   private static final INode EMPTY_LAMBDA_BODY_PATTERN = new BlockStatement(new Statement[] { new ReturnStatement(-34) });
   
 
 
 
 
 
   public static BlockStatement createMethodBody(AstBuilder astBuilder, MethodDefinition method, DecompilerContext context, Iterable<ParameterDeclaration> parameters)
   {
     VerifyArgument.notNull(astBuilder, "astBuilder");
     VerifyArgument.notNull(method, "method");
     VerifyArgument.notNull(context, "context");
     
     MethodDefinition oldCurrentMethod = context.getCurrentMethod();
     
 
 
 
 
 
 
     context.setCurrentMethod(method);
     try
     {
       AstMethodBodyBuilder builder = new AstMethodBodyBuilder(astBuilder, method, context);
       return builder.createMethodBody(parameters);
     } catch (Throwable t) {
       BlockStatement localBlockStatement;
       return createErrorBlock(astBuilder, context, method, t);
     }
     finally {
       context.setCurrentMethod(oldCurrentMethod);
     }
   }
   
 
 
 
 
 
   private static BlockStatement createErrorBlock(AstBuilder astBuilder, DecompilerContext context, MethodDefinition method, Throwable t)
   {
     BlockStatement block = new BlockStatement();
     
     List<String> lines = StringUtilities.split(com.strobel.core.ExceptionUtilities.getStackTraceString(t), true, '\r', new char[] { '\n' });
     
 
 
 
 
 
     block.addChild(new Comment(" ", CommentType.SingleLine), Roles.COMMENT);
     block.addChild(new Comment(" This method could not be decompiled.", CommentType.SingleLine), Roles.COMMENT);
     block.addChild(new Comment(" ", CommentType.SingleLine), Roles.COMMENT);
     try
     {
       PlainTextOutput bytecodeOutput = new PlainTextOutput();
       DecompilationOptions bytecodeOptions = new DecompilationOptions();
       
       bytecodeOptions.getSettings().setIncludeLineNumbersInBytecode(false);
       
       com.strobel.decompiler.languages.Languages.bytecode().decompileMethod(method, bytecodeOutput, bytecodeOptions);
       
       List<String> bytecodeLines = StringUtilities.split(bytecodeOutput.toString(), true, '\r', new char[] { '\n' });
       
 
 
 
 
 
       block.addChild(new Comment(" Original Bytecode:", CommentType.SingleLine), Roles.COMMENT);
       block.addChild(new Comment(" ", CommentType.SingleLine), Roles.COMMENT);
       
       for (int i = 4; i < bytecodeLines.size(); i++) {
         String line = StringUtilities.removeLeft((String)bytecodeLines.get(i), "      ");
         
         block.addChild(new Comment(line.replace("\t", "  "), CommentType.SingleLine), Roles.COMMENT);
       }
       
       block.addChild(new Comment(" ", CommentType.SingleLine), Roles.COMMENT);
     }
     catch (Throwable ignored) {
       block.addChild(new Comment(" Could not show original bytecode, likely due to the same error.", CommentType.SingleLine), Roles.COMMENT);
       block.addChild(new Comment(" ", CommentType.SingleLine), Roles.COMMENT);
     }
     
     if (context.getSettings().getIncludeErrorDiagnostics()) {
       block.addChild(new Comment(" The error that occurred was:", CommentType.SingleLine), Roles.COMMENT);
       block.addChild(new Comment(" ", CommentType.SingleLine), Roles.COMMENT);
       
       for (String line : lines) {
         block.addChild(new Comment(" " + line.replace("\t", "    "), CommentType.SingleLine), Roles.COMMENT);
       }
       
       block.addChild(new Comment(" ", CommentType.SingleLine), Roles.COMMENT);
     }
     try
     {
       TypeDefinition currentType = astBuilder.getContext().getCurrentType();
       com.strobel.assembler.metadata.IMetadataResolver resolver = currentType != null ? currentType.getResolver() : com.strobel.assembler.metadata.MetadataSystem.instance();
       MetadataParser parser = new MetadataParser(resolver);
       
       block.add(new ThrowStatement(new ObjectCreationExpression(-34, astBuilder.convertType(parser.parseTypeDescriptor("java/lang/IllegalStateException")), new Expression[] { new PrimitiveExpression(-34, "An error occurred while decompiling this method.") })));
 
 
 
 
     }
     catch (Throwable ignored)
     {
 
 
 
       block.add(new EmptyStatement());
     }
     
     return block;
   }
   
   private AstMethodBodyBuilder(AstBuilder astBuilder, MethodDefinition method, DecompilerContext context) {
     this._astBuilder = astBuilder;
     this._method = method;
     this._context = context;
     this._parser = new MetadataParser(method.getDeclaringType());
   }
   
   private BlockStatement createMethodBody(Iterable<ParameterDeclaration> parameters)
   {
     com.strobel.assembler.metadata.MethodBody body = this._method.getBody();
     
     if (body == null) {
       return null;
     }
     
     Block method = new Block();
     
     method.getBody().addAll(com.strobel.decompiler.ast.AstBuilder.build(body, true, this._context));
     
 
 
     com.strobel.decompiler.ast.AstOptimizer.optimize(this._context, method);
     
     Set<ParameterDefinition> unmatchedParameters = new LinkedHashSet(this._method.getParameters());
     Set<Variable> methodParameters = new LinkedHashSet();
     Set<Variable> localVariables = new LinkedHashSet();
     
     List<com.strobel.decompiler.ast.Expression> expressions = method.getSelfAndChildrenRecursive(com.strobel.decompiler.ast.Expression.class);
     
 
 
     for (com.strobel.decompiler.ast.Expression e : expressions) {
       Object operand = e.getOperand();
       
       if ((operand instanceof Variable)) {
         Variable variable = (Variable)operand;
         
         if (variable.isParameter()) {
           methodParameters.add(variable);
           unmatchedParameters.remove(variable.getOriginalParameter());
         }
         else {
           localVariables.add(variable);
         }
       }
     }
     
     List<Variable> orderedParameters = new ArrayList();
     
     for (ParameterDefinition p : unmatchedParameters) {
       Variable v = new Variable();
       v.setName(p.getName());
       v.setOriginalParameter(p);
       v.setType(p.getParameterType());
       orderedParameters.add(v);
     }
     
     for (Variable parameter : methodParameters) {
       orderedParameters.add(parameter);
     }
     
     Collections.sort(orderedParameters, new java.util.Comparator()
     {
 
       public int compare(@NotNull Variable p1, @NotNull Variable p2)
       {
         return Integer.compare(p1.getOriginalParameter().getSlot(), p2.getOriginalParameter().getSlot());
       }
       
 
     });
     List<CatchBlock> catchBlocks = method.getSelfAndChildrenRecursive(CatchBlock.class);
     
 
 
     for (CatchBlock catchBlock : catchBlocks) {
       Variable exceptionVariable = catchBlock.getExceptionVariable();
       
       if (exceptionVariable != null) {
         localVariables.add(exceptionVariable);
       }
     }
     
     NameVariables.assignNamesToVariables(this._context, orderedParameters, localVariables, method);
     
     for (final Variable p : orderedParameters) {
       ParameterDeclaration declaration = (ParameterDeclaration)CollectionUtilities.firstOrDefault(parameters, new com.strobel.core.Predicate()
       {
 
         public boolean test(ParameterDeclaration pd)
         {
           return pd.getUserData(Keys.PARAMETER_DEFINITION) == p.getOriginalParameter();
         }
       });
       
 
       if (declaration != null) {
         declaration.setName(p.getName());
       }
     }
     
     BlockStatement astBlock = transformBlock(method);
     
     CommentStatement.replaceAll(astBlock);
     
     AstNodeCollection<Statement> statements = astBlock.getStatements();
     Statement insertionPoint = (Statement)CollectionUtilities.firstOrDefault(statements);
     
     for (Variable v : this._localVariablesToDefine) {
       TypeReference variableType = v.getType();
       
       TypeDefinition resolvedType = variableType.resolve();
       
       if ((resolvedType != null) && (resolvedType.isAnonymous())) {
         if (resolvedType.getExplicitInterfaces().isEmpty()) {
           variableType = resolvedType.getBaseType();
         }
         else {
           variableType = (TypeReference)resolvedType.getExplicitInterfaces().get(0);
         }
       }
       
       AstType type = this._astBuilder.convertType(variableType);
       VariableDeclarationStatement declaration = new VariableDeclarationStatement(type, v.getName(), -34);
       
       declaration.putUserData(Keys.VARIABLE, v);
       statements.insertBefore(insertionPoint, declaration);
     }
     
     return astBlock;
   }
   
   private BlockStatement transformBlock(Block block) {
     BlockStatement astBlock = new BlockStatement();
     
     if (block != null) {
       List<Node> children = block.getChildren();
       for (int i = 0; i < children.size(); i++) {
         Node node = (Node)children.get(i);
         
         Statement statement = transformNode(node, i < children.size() - 1 ? (Node)children.get(i + 1) : null);
         
 
 
 
         astBlock.getStatements().add(statement);
         
         if ((statement instanceof SynchronizedStatement)) {
           i++;
         }
       }
     }
     
     return astBlock;
   }
   
   private Statement transformNode(Node node, Node next) {
     if ((node instanceof Label)) {
       return new LabelStatement(-34, ((Label)node).getName());
     }
     
     if ((node instanceof Block)) {
       return transformBlock((Block)node);
     }
     
     if ((node instanceof com.strobel.decompiler.ast.Expression)) {
       com.strobel.decompiler.ast.Expression expression = (com.strobel.decompiler.ast.Expression)node;
       
       if ((expression.getCode() == AstCode.MonitorEnter) && ((next instanceof TryCatchBlock)))
       {
 
         TryCatchBlock tryCatch = (TryCatchBlock)next;
         Block finallyBlock = tryCatch.getFinallyBlock();
         
         if ((finallyBlock != null) && (finallyBlock.getBody().size() == 1))
         {
 
           Node finallyNode = (Node)finallyBlock.getBody().get(0);
           
           if (((finallyNode instanceof com.strobel.decompiler.ast.Expression)) && (((com.strobel.decompiler.ast.Expression)finallyNode).getCode() == AstCode.MonitorExit))
           {
 
             return transformSynchronized(expression, tryCatch);
           }
         }
       }
       
       List<Range> ranges = new ArrayList();
       
       List<com.strobel.decompiler.ast.Expression> childExpressions = node.getSelfAndChildrenRecursive(com.strobel.decompiler.ast.Expression.class);
       
 
 
       for (com.strobel.decompiler.ast.Expression e : childExpressions) {
         ranges.addAll(e.getRanges());
       }
       
 
       List<Range> orderedAndJoinedRanges = Range.orderAndJoint(ranges);
       AstNode codeExpression = transformExpression((com.strobel.decompiler.ast.Expression)node, true);
       
       if (codeExpression != null) {
         if ((codeExpression instanceof Expression)) {
           return new ExpressionStatement((Expression)codeExpression);
         }
         return (Statement)codeExpression;
       }
     }
     
     if ((node instanceof Loop)) {
       Loop loop = (Loop)node;
       
       com.strobel.decompiler.ast.Expression loopCondition = loop.getCondition();
       Statement loopStatement;
       Statement loopStatement; if (loopCondition != null) { Statement loopStatement;
         if (loop.getLoopType() == com.strobel.decompiler.ast.LoopType.PostCondition) {
           DoWhileStatement doWhileStatement = new DoWhileStatement(loopCondition.getOffset());
           doWhileStatement.setCondition((Expression)transformExpression(loopCondition, false));
           loopStatement = doWhileStatement;
         }
         else {
           WhileStatement whileStatement = new WhileStatement(loopCondition.getOffset());
           whileStatement.setCondition((Expression)transformExpression(loopCondition, false));
           loopStatement = whileStatement;
         }
       }
       else {
         WhileStatement whileStatement = new WhileStatement(-34);
         loopStatement = whileStatement;
         whileStatement.setCondition(new PrimitiveExpression(-34, Boolean.valueOf(true)));
       }
       
       loopStatement.setChildByRole(Roles.EMBEDDED_STATEMENT, transformBlock(loop.getBody()));
       
       return loopStatement;
     }
     
     if ((node instanceof Condition)) {
       Condition condition = (Condition)node;
       com.strobel.decompiler.ast.Expression testCondition = condition.getCondition();
       Block trueBlock = condition.getTrueBlock();
       Block falseBlock = condition.getFalseBlock();
       boolean hasFalseBlock = (falseBlock.getEntryGoto() != null) || (!falseBlock.getBody().isEmpty());
       
       return new IfElseStatement(testCondition.getOffset(), (Expression)transformExpression(testCondition, false), transformBlock(trueBlock), hasFalseBlock ? transformBlock(falseBlock) : null);
     }
     
 
 
 
 
 
     if ((node instanceof Switch)) {
       Switch switchNode = (Switch)node;
       com.strobel.decompiler.ast.Expression testCondition = switchNode.getCondition();
       
       if (com.strobel.decompiler.ast.TypeAnalysis.isBoolean(testCondition.getInferredType())) {
         testCondition.setExpectedType(BuiltinTypes.Integer);
       }
       
       List<CaseBlock> caseBlocks = switchNode.getCaseBlocks();
       SwitchStatement switchStatement = new SwitchStatement((Expression)transformExpression(testCondition, false));
       
       for (CaseBlock caseBlock : caseBlocks) {
         SwitchSection section = new SwitchSection();
         AstNodeCollection<CaseLabel> caseLabels = section.getCaseLabels();
         TypeReference referenceType;
         if (caseBlock.getValues().isEmpty()) {
           caseLabels.add(new CaseLabel());
         }
         else
         {
           TypeReference referenceType;
           if (testCondition.getExpectedType() != null) {
             referenceType = testCondition.getExpectedType();
           }
           else {
             referenceType = testCondition.getInferredType();
           }
           
           for (Integer value : caseBlock.getValues()) {
             CaseLabel caseLabel = new CaseLabel();
             caseLabel.setExpression(AstBuilder.makePrimitive(value.intValue(), referenceType));
             caseLabels.add(caseLabel);
           }
         }
         
         section.getStatements().add(transformBlock(caseBlock));
         switchStatement.getSwitchSections().add(section);
       }
       
       return switchStatement;
     }
     
     if ((node instanceof TryCatchBlock)) {
       TryCatchBlock tryCatchNode = (TryCatchBlock)node;
       Block finallyBlock = tryCatchNode.getFinallyBlock();
       List<CatchBlock> catchBlocks = tryCatchNode.getCatchBlocks();
       
       TryCatchStatement tryCatch = new TryCatchStatement(-34);
       
       tryCatch.setTryBlock(transformBlock(tryCatchNode.getTryBlock()));
       
       for (CatchBlock catchBlock : catchBlocks) {
         CatchClause catchClause = new CatchClause(transformBlock(catchBlock));
         
         for (TypeReference caughtType : catchBlock.getCaughtTypes()) {
           catchClause.getExceptionTypes().add(this._astBuilder.convertType(caughtType));
         }
         
         Variable exceptionVariable = catchBlock.getExceptionVariable();
         
         if (exceptionVariable != null) {
           catchClause.setVariableName(exceptionVariable.getName());
           catchClause.putUserData(Keys.VARIABLE, exceptionVariable);
         }
         
         tryCatch.getCatchClauses().add(catchClause);
       }
       
       if ((finallyBlock != null) && ((!finallyBlock.getBody().isEmpty()) || (catchBlocks.isEmpty()))) {
         tryCatch.setFinallyBlock(transformBlock(finallyBlock));
       }
       
       return tryCatch;
     }
     
     throw new IllegalArgumentException("Unknown node type: " + node);
   }
   
   private SynchronizedStatement transformSynchronized(com.strobel.decompiler.ast.Expression expression, TryCatchBlock tryCatch) {
     SynchronizedStatement s = new SynchronizedStatement(expression.getOffset());
     
     s.setExpression((Expression)transformExpression((com.strobel.decompiler.ast.Expression)expression.getArguments().get(0), false));
     
     if (tryCatch.getCatchBlocks().isEmpty()) {
       s.setEmbeddedStatement(transformBlock(tryCatch.getTryBlock()));
     }
     else {
       tryCatch.setFinallyBlock(null);
       s.setEmbeddedStatement(new BlockStatement(new Statement[] { transformNode(tryCatch, null) }));
     }
     
     return s;
   }
   
   private AstNode transformExpression(com.strobel.decompiler.ast.Expression e, boolean isTopLevel) {
     return transformByteCode(e, isTopLevel);
   }
   
   private AstNode transformByteCode(com.strobel.decompiler.ast.Expression byteCode, boolean isTopLevel)
   {
     Object operand = byteCode.getOperand();
     Label label = (operand instanceof Label) ? (Label)operand : null;
     AstType operandType = (operand instanceof TypeReference) ? this._astBuilder.convertType((TypeReference)operand) : AstType.NULL;
     Variable variableOperand = (operand instanceof Variable) ? (Variable)operand : null;
     FieldReference fieldOperand = (operand instanceof FieldReference) ? (FieldReference)operand : null;
     
     List<Expression> arguments = new ArrayList();
     
     for (com.strobel.decompiler.ast.Expression e : byteCode.getArguments()) {
       arguments.add((Expression)transformExpression(e, false));
     }
     
     Expression arg1 = arguments.size() >= 1 ? (Expression)arguments.get(0) : null;
     Expression arg2 = arguments.size() >= 2 ? (Expression)arguments.get(1) : null;
     Expression arg3 = arguments.size() >= 3 ? (Expression)arguments.get(2) : null;
     
     switch (byteCode.getCode()) {
     case Nop: 
       return null;
     
     case AConstNull: 
       return new NullReferenceExpression(byteCode.getOffset());
     
     case LdC: 
       if ((operand instanceof TypeReference)) {
         operandType.getChildrenByRole(Roles.TYPE_ARGUMENT).clear();
         return new ClassOfExpression(byteCode.getOffset(), operandType);
       }
       
       TypeReference type = byteCode.getInferredType() != null ? byteCode.getInferredType() : byteCode.getExpectedType();
       
 
       if (type != null) {
         switch (type.getSimpleType()) {
         case Byte: 
         case Short: 
           return new PrimitiveExpression(byteCode.getOffset(), JavaPrimitiveCast.cast(JvmType.Integer, operand));
         }
         
         
 
 
         return new PrimitiveExpression(byteCode.getOffset(), JavaPrimitiveCast.cast(type.getSimpleType(), operand));
       }
       
 
 
 
 
       return new PrimitiveExpression(byteCode.getOffset(), operand);
     
 
     case Pop: 
     case Pop2: 
     case Dup: 
     case DupX1: 
     case DupX2: 
     case Dup2: 
     case Dup2X1: 
     case Dup2X2: 
       return arg1;
     
     case Swap: 
       return arg1;
     
     case I2L: 
       return new CastExpression(this._astBuilder.convertType(BuiltinTypes.Long), arg1);
     case I2F: 
       return new CastExpression(this._astBuilder.convertType(BuiltinTypes.Float), arg1);
     case I2D: 
       return new CastExpression(this._astBuilder.convertType(BuiltinTypes.Double), arg1);
     case L2I: 
       return new CastExpression(this._astBuilder.convertType(BuiltinTypes.Integer), arg1);
     case L2F: 
       return new CastExpression(this._astBuilder.convertType(BuiltinTypes.Float), arg1);
     case L2D: 
       return new CastExpression(this._astBuilder.convertType(BuiltinTypes.Double), arg1);
     case F2I: 
       return new CastExpression(this._astBuilder.convertType(BuiltinTypes.Integer), arg1);
     case F2L: 
       return new CastExpression(this._astBuilder.convertType(BuiltinTypes.Long), arg1);
     case F2D: 
       return new CastExpression(this._astBuilder.convertType(BuiltinTypes.Double), arg1);
     case D2I: 
       return new CastExpression(this._astBuilder.convertType(BuiltinTypes.Integer), arg1);
     case D2L: 
       return new CastExpression(this._astBuilder.convertType(BuiltinTypes.Long), arg1);
     case D2F: 
       return new CastExpression(this._astBuilder.convertType(BuiltinTypes.Float), arg1);
     case I2B: 
       return new CastExpression(this._astBuilder.convertType(BuiltinTypes.Byte), arg1);
     case I2C: 
       return new CastExpression(this._astBuilder.convertType(BuiltinTypes.Character), arg1);
     case I2S: 
       return new CastExpression(this._astBuilder.convertType(BuiltinTypes.Short), arg1);
     
     case Goto: 
       return new GotoStatement(byteCode.getOffset(), ((Label)operand).getName());
     
     case GetStatic: 
       ConvertTypeOptions options = new ConvertTypeOptions();
       options.setIncludeTypeParameterDefinitions(false);
       MemberReferenceExpression fieldReference = this._astBuilder.convertType(fieldOperand.getDeclaringType(), options).member(fieldOperand.getName());
       
       fieldReference.putUserData(Keys.MEMBER_REFERENCE, fieldOperand);
       return fieldReference;
     
 
     case PutStatic: 
       ConvertTypeOptions options = new ConvertTypeOptions();
       options.setIncludeTypeParameterDefinitions(false);
       
       FieldDefinition resolvedField = fieldOperand.resolve();
       Expression fieldReference;
       Expression fieldReference;
       if ((resolvedField != null) && (resolvedField.isFinal()) && (StringUtilities.equals(resolvedField.getDeclaringType().getInternalName(), this._context.getCurrentType().getInternalName())))
       {
 
 
 
 
 
 
         fieldReference = new IdentifierExpression(byteCode.getOffset(), fieldOperand.getName());
       }
       else {
         fieldReference = this._astBuilder.convertType(fieldOperand.getDeclaringType(), options).member(fieldOperand.getName());
       }
       
 
       fieldReference.putUserData(Keys.MEMBER_REFERENCE, fieldOperand);
       return new AssignmentExpression(fieldReference, arg1);
     case GetField: 
       MemberReferenceExpression fieldReference;
       
       MemberReferenceExpression fieldReference;
       
       if (((arg1 instanceof ThisReferenceExpression)) && (MetadataHelper.isSubType(this._context.getCurrentType(), fieldOperand.getDeclaringType())) && (!StringUtilities.equals(fieldOperand.getDeclaringType().getInternalName(), this._context.getCurrentType().getInternalName())))
       {
 
 
         fieldReference = new SuperReferenceExpression(arg1.getOffset()).member(fieldOperand.getName());
       }
       else {
         fieldReference = arg1.member(fieldOperand.getName());
       }
       
       fieldReference.putUserData(Keys.MEMBER_REFERENCE, fieldOperand);
       return fieldReference;
     case PutField: 
       MemberReferenceExpression fieldReference;
       
       MemberReferenceExpression fieldReference;
       
       if (((arg1 instanceof ThisReferenceExpression)) && (MetadataHelper.isSubType(this._context.getCurrentType(), fieldOperand.getDeclaringType())) && (!StringUtilities.equals(fieldOperand.getDeclaringType().getInternalName(), this._context.getCurrentType().getInternalName())))
       {
 
 
         fieldReference = new SuperReferenceExpression(arg1.getOffset()).member(fieldOperand.getName());
       }
       else {
         fieldReference = arg1.member(fieldOperand.getName());
       }
       
       fieldReference.putUserData(Keys.MEMBER_REFERENCE, fieldOperand);
       return new AssignmentExpression(fieldReference, arg2);
     
 
     case InvokeVirtual: 
       return transformCall(true, byteCode, arguments);
     case InvokeSpecial: 
     case InvokeStatic: 
       return transformCall(false, byteCode, arguments);
     case InvokeInterface: 
       return transformCall(false, byteCode, arguments);
     
     case InvokeDynamic: 
       DynamicCallSite callSite = (DynamicCallSite)operand;
       MethodReference bootstrapMethod = callSite.getBootstrapMethod();
       
       if (("java/lang/invoke/LambdaMetafactory".equals(bootstrapMethod.getDeclaringType().getInternalName())) && ((StringUtilities.equals("metafactory", bootstrapMethod.getName(), StringComparison.OrdinalIgnoreCase)) || (StringUtilities.equals("altMetafactory", bootstrapMethod.getName(), StringComparison.OrdinalIgnoreCase))) && (callSite.getBootstrapArguments().size() >= 3) && ((callSite.getBootstrapArguments().get(1) instanceof MethodHandle)))
       {
 
 
 
 
         MethodHandle targetMethodHandle = (MethodHandle)callSite.getBootstrapArguments().get(1);
         MethodReference targetMethod = targetMethodHandle.getMethod();
         TypeReference declaringType = targetMethod.getDeclaringType();
         String methodName = targetMethod.isConstructor() ? "new" : targetMethod.getName();
         
         boolean hasInstanceArgument;
         
         switch (targetMethodHandle.getHandleType())
         {
         case GetField: 
         case PutField: 
         case InvokeVirtual: 
         case InvokeInterface: 
         case InvokeSpecial: 
           hasInstanceArgument = arg1 != null;
           break;
         
         default: 
           hasInstanceArgument = false;
         }
         
         
         MethodGroupExpression methodGroup = new MethodGroupExpression(byteCode.getOffset(), hasInstanceArgument ? arg1 : new TypeReferenceExpression(byteCode.getOffset(), this._astBuilder.convertType(declaringType)), methodName);
         
 
 
 
 
 
         methodGroup.getClosureArguments().addAll(hasInstanceArgument ? arguments.subList(1, arguments.size()) : arguments);
         
 
 
         methodGroup.putUserData(Keys.DYNAMIC_CALL_SITE, callSite);
         methodGroup.putUserData(Keys.MEMBER_REFERENCE, targetMethod);
         
         if (byteCode.getInferredType() != null) {
           methodGroup.putUserData(Keys.TYPE_REFERENCE, byteCode.getInferredType());
         }
         
         return methodGroup;
       }
       
 
 
       break;
     case Bind: 
       Lambda lambda = (Lambda)byteCode.getOperand();
       LambdaExpression lambdaExpression = new LambdaExpression(byteCode.getOffset());
       AstNodeCollection<ParameterDeclaration> declarations = lambdaExpression.getParameters();
       
       for (Variable v : lambda.getParameters()) {
         ParameterDefinition p = v.getOriginalParameter();
         ParameterDeclaration d = new ParameterDeclaration(v.getName(), null);
         
         d.putUserData(Keys.PARAMETER_DEFINITION, p);
         d.putUserData(Keys.VARIABLE, v);
         
         for (com.strobel.assembler.metadata.annotations.CustomAnnotation annotation : p.getAnnotations()) {
           d.getAnnotations().add(this._astBuilder.createAnnotation(annotation));
         }
         
         declarations.add(d);
         
         if (p.isFinal()) {
           EntityDeclaration.addModifier(d, javax.lang.model.element.Modifier.FINAL);
         }
       }
       
       BlockStatement body = transformBlock(lambda.getBody());
       Match m = LAMBDA_BODY_PATTERN.match(body);
       
       if (m.success()) {
         AstNode bodyNode = (AstNode)CollectionUtilities.first(m.get("body"));
         bodyNode.remove();
         lambdaExpression.setBody(bodyNode);
         
         if (EMPTY_LAMBDA_BODY_PATTERN.matches(bodyNode)) {
           bodyNode.getChildrenByRole(BlockStatement.STATEMENT_ROLE).clear();
         }
       }
       else {
         lambdaExpression.setBody(body);
       }
       
       lambdaExpression.putUserData(Keys.TYPE_REFERENCE, byteCode.getInferredType());
       
       DynamicCallSite callSite = lambda.getCallSite();
       
       if (callSite != null) {
         lambdaExpression.putUserData(Keys.DYNAMIC_CALL_SITE, callSite);
       }
       
       return lambdaExpression;
     
 
     case ArrayLength: 
       MemberReferenceExpression length = arg1.member("length");
       TypeReference arrayType = ((com.strobel.decompiler.ast.Expression)CollectionUtilities.single(byteCode.getArguments())).getInferredType();
       
       if (arrayType != null) {
         length.putUserData(Keys.MEMBER_REFERENCE, this._parser.parseField(arrayType, "length", "I"));
       }
       
 
 
 
       return length;
     
     case AThrow: 
       return new ThrowStatement(arg1);
     
     case CheckCast: 
       return new CastExpression(operandType, arg1);
     
     case InstanceOf: 
       return new InstanceOfExpression(byteCode.getOffset(), arg1, operandType);
     
     case MonitorEnter: 
     case MonitorExit: 
       break;
     
     case MultiANewArray: 
       ArrayCreationExpression arrayCreation = new ArrayCreationExpression(byteCode.getOffset());
       
 
       int rank = 0;
       AstType elementType = operandType;
       
       while ((elementType instanceof ComposedType)) {
         rank += ((ComposedType)elementType).getArraySpecifiers().size();
         elementType = ((ComposedType)elementType).getBaseType();
       }
       
       arrayCreation.setType(elementType.clone());
       
       for (int i = 0; i < arguments.size(); i++) {
         arrayCreation.getDimensions().add((AstNode)arguments.get(i));
         rank--;
       }
       
       for (int i = 0; i < rank; i++) {
         arrayCreation.getAdditionalArraySpecifiers().add(new ArraySpecifier());
       }
       
       return arrayCreation;
     
 
     case Breakpoint: 
       return null;
     
     case Load: 
       if (!variableOperand.isParameter()) {
         this._localVariablesToDefine.add(variableOperand);
       }
       if ((variableOperand.isParameter()) && (variableOperand.getOriginalParameter().getPosition() < 0)) {
         ThisReferenceExpression self = new ThisReferenceExpression(byteCode.getOffset());
         self.putUserData(Keys.TYPE_REFERENCE, this._context.getCurrentType());
         return self;
       }
       IdentifierExpression name = new IdentifierExpression(byteCode.getOffset(), variableOperand.getName());
       
       name.putUserData(Keys.VARIABLE, variableOperand);
       return name;
     
 
     case Store: 
       if (!variableOperand.isParameter()) {
         this._localVariablesToDefine.add(variableOperand);
       }
       IdentifierExpression name = new IdentifierExpression(byteCode.getOffset(), variableOperand.getName());
       
       name.putUserData(Keys.VARIABLE, variableOperand);
       return new AssignmentExpression(name, arg1);
     
 
     case LoadElement: 
       return new IndexerExpression(byteCode.getOffset(), arg1, arg2);
     
     case StoreElement: 
       return new AssignmentExpression(new IndexerExpression(byteCode.getOffset(), arg1, arg2), arg3);
     
 
 
 
 
     case Add: 
       return new BinaryOperatorExpression(arg1, BinaryOperatorType.ADD, arg2);
     case Sub: 
       return new BinaryOperatorExpression(arg1, BinaryOperatorType.SUBTRACT, arg2);
     case Mul: 
       return new BinaryOperatorExpression(arg1, BinaryOperatorType.MULTIPLY, arg2);
     case Div: 
       return new BinaryOperatorExpression(arg1, BinaryOperatorType.DIVIDE, arg2);
     case Rem: 
       return new BinaryOperatorExpression(arg1, BinaryOperatorType.MODULUS, arg2);
     case Neg: 
       return new UnaryOperatorExpression(UnaryOperatorType.MINUS, arg1);
     case Shl: 
       return new BinaryOperatorExpression(arg1, BinaryOperatorType.SHIFT_LEFT, arg2);
     case Shr: 
       return new BinaryOperatorExpression(arg1, BinaryOperatorType.SHIFT_RIGHT, arg2);
     case UShr: 
       return new BinaryOperatorExpression(arg1, BinaryOperatorType.UNSIGNED_SHIFT_RIGHT, arg2);
     case And: 
       return new BinaryOperatorExpression(arg1, BinaryOperatorType.BITWISE_AND, arg2);
     case Or: 
       return new BinaryOperatorExpression(arg1, BinaryOperatorType.BITWISE_OR, arg2);
     case Not: 
       return new UnaryOperatorExpression(UnaryOperatorType.NOT, arg1);
     case Xor: 
       return new BinaryOperatorExpression(arg1, BinaryOperatorType.EXCLUSIVE_OR, arg2);
     
     case Inc: 
       if (!variableOperand.isParameter()) {
         this._localVariablesToDefine.add(variableOperand);
       }
       
       IdentifierExpression name = new IdentifierExpression(byteCode.getOffset(), variableOperand.getName());
       
 
       name.getIdentifierToken().putUserData(Keys.VARIABLE, variableOperand);
       name.putUserData(Keys.VARIABLE, variableOperand);
       
       PrimitiveExpression deltaExpression = (PrimitiveExpression)arg1;
       int delta = ((Integer)JavaPrimitiveCast.cast(JvmType.Integer, deltaExpression.getValue())).intValue();
       
       switch (delta) {
       case -1: 
         return new UnaryOperatorExpression(UnaryOperatorType.DECREMENT, name);
       case 1: 
         return new UnaryOperatorExpression(UnaryOperatorType.INCREMENT, name);
       }
       return new AssignmentExpression(name, AssignmentOperatorType.ADD, arg1);
     
 
 
     case CmpEq: 
       return new BinaryOperatorExpression(arg1, BinaryOperatorType.EQUALITY, arg2);
     case CmpNe: 
       return new BinaryOperatorExpression(arg1, BinaryOperatorType.INEQUALITY, arg2);
     case CmpLt: 
       return new BinaryOperatorExpression(arg1, BinaryOperatorType.LESS_THAN, arg2);
     case CmpGe: 
       return new BinaryOperatorExpression(arg1, BinaryOperatorType.GREATER_THAN_OR_EQUAL, arg2);
     case CmpGt: 
       return new BinaryOperatorExpression(arg1, BinaryOperatorType.GREATER_THAN, arg2);
     case CmpLe: 
       return new BinaryOperatorExpression(arg1, BinaryOperatorType.LESS_THAN_OR_EQUAL, arg2);
     
     case Return: 
       return new ReturnStatement(byteCode.getOffset(), arg1);
     
     case NewArray: 
       ArrayCreationExpression arrayCreation = new ArrayCreationExpression(byteCode.getOffset());
       
 
       TypeReference elementType = (TypeReference)operandType.getUserData(Keys.TYPE_REFERENCE);
       
       while (elementType.isArray()) {
         arrayCreation.getAdditionalArraySpecifiers().add(new ArraySpecifier());
         elementType = elementType.getElementType();
       }
       
       arrayCreation.setType(this._astBuilder.convertType(elementType));
       arrayCreation.getDimensions().add(arg1);
       
       return arrayCreation;
     
 
     case LogicalNot: 
       return new UnaryOperatorExpression(UnaryOperatorType.NOT, arg1);
     
     case LogicalAnd: 
       return new BinaryOperatorExpression(arg1, BinaryOperatorType.LOGICAL_AND, arg2);
     case LogicalOr: 
       return new BinaryOperatorExpression(arg1, BinaryOperatorType.LOGICAL_OR, arg2);
     
     case InitObject: 
       return transformCall(false, byteCode, arguments);
     
     case InitArray: 
       ArrayCreationExpression arrayCreation = new ArrayCreationExpression(byteCode.getOffset());
       
 
       TypeReference elementType = (TypeReference)operandType.getUserData(Keys.TYPE_REFERENCE);
       
       while (elementType.isArray()) {
         arrayCreation.getAdditionalArraySpecifiers().add(new ArraySpecifier());
         elementType = elementType.getElementType();
       }
       
       arrayCreation.setType(this._astBuilder.convertType(elementType));
       arrayCreation.setInitializer(new ArrayInitializerExpression(arguments));
       
       return arrayCreation;
     
 
     case Wrap: 
       return null;
     
     case TernaryOp: 
       return new ConditionalExpression(arg1, arg2, arg3);
     
     case LoopOrSwitchBreak: 
       return label != null ? new GotoStatement(byteCode.getOffset(), label.getName()) : new BreakStatement(byteCode.getOffset());
     
     case LoopContinue: 
       return label != null ? new ContinueStatement(byteCode.getOffset(), label.getName()) : new ContinueStatement(byteCode.getOffset());
     
     case CompoundAssignment: 
       throw ContractUtils.unreachable();
     
     case PreIncrement: 
       Integer incrementAmount = (Integer)operand;
       if (incrementAmount.intValue() < 0) {
         return new UnaryOperatorExpression(UnaryOperatorType.DECREMENT, arg1);
       }
       return new UnaryOperatorExpression(UnaryOperatorType.INCREMENT, arg1);
     
 
     case PostIncrement: 
       Integer incrementAmount = (Integer)operand;
       if (incrementAmount.intValue() < 0) {
         return new UnaryOperatorExpression(UnaryOperatorType.POST_DECREMENT, arg1);
       }
       return new UnaryOperatorExpression(UnaryOperatorType.POST_INCREMENT, arg1);
     
 
     case Box: 
     case Unbox: 
       throw ContractUtils.unreachable();
     
     case Leave: 
     case EndFinally: 
       return null;
     
     case DefaultValue: 
       return AstBuilder.makeDefaultValue((TypeReference)operand);
     }
     
     Expression inlinedAssembly = inlineAssembly(byteCode, arguments);
     
     if (isTopLevel) {
       return new CommentStatement(" " + inlinedAssembly.toString());
     }
     
     return inlinedAssembly;
   }
   
 
 
 
 
   private Expression transformCall(boolean isVirtual, com.strobel.decompiler.ast.Expression byteCode, List<Expression> arguments)
   {
     MethodReference methodReference = (MethodReference)byteCode.getOperand();
     
     boolean hasThis = (byteCode.getCode() == AstCode.InvokeVirtual) || (byteCode.getCode() == AstCode.InvokeInterface) || (byteCode.getCode() == AstCode.InvokeSpecial);
     
 
 
 
 
     TypeReference declaringType = methodReference.getDeclaringType();
     Expression target;
     if (hasThis) {
       Expression target = (Expression)arguments.remove(0);
       
       if ((target instanceof NullReferenceExpression)) {
         target = new CastExpression(this._astBuilder.convertType(declaringType), target);
       }
     }
     else {
       MethodDefinition resolvedMethod;
       Expression target;
       if ((byteCode.getCode() == AstCode.InvokeStatic) && (declaringType.isEquivalentTo(this._context.getCurrentType())) && ((!this._context.getSettings().getForceExplicitTypeArguments()) || ((resolvedMethod = methodReference.resolve()) == null) || (!resolvedMethod.isGenericMethod())))
       {
 
 
 
 
         target = Expression.NULL;
       }
       else {
         ConvertTypeOptions options = new ConvertTypeOptions();
         options.setIncludeTypeArguments(false);
         options.setIncludeTypeParameterDefinitions(false);
         options.setAllowWildcards(false);
         target = new TypeReferenceExpression(byteCode.getOffset(), this._astBuilder.convertType(declaringType, options));
       }
     }
     
     if ((target instanceof ThisReferenceExpression)) {
       if ((!isVirtual) && (!declaringType.isEquivalentTo(this._method.getDeclaringType()))) {
         target = new SuperReferenceExpression(byteCode.getOffset());
         target.putUserData(Keys.TYPE_REFERENCE, declaringType);
       }
     }
     else if (methodReference.isConstructor())
     {
       TypeDefinition resolvedType = declaringType.resolve();
       ObjectCreationExpression creation;
       ObjectCreationExpression creation; if (resolvedType != null)
       {
         TypeReference instantiatedType;
         
         TypeReference instantiatedType;
         if (resolvedType.isAnonymous()) { TypeReference instantiatedType;
           if (resolvedType.getExplicitInterfaces().isEmpty()) {
             instantiatedType = resolvedType.getBaseType();
           }
           else {
             instantiatedType = (TypeReference)resolvedType.getExplicitInterfaces().get(0);
           }
         }
         else {
           instantiatedType = resolvedType;
         }
         
         List<TypeReference> typeArguments = (List)byteCode.getUserData(com.strobel.decompiler.ast.AstKeys.TYPE_ARGUMENTS);
         
         if ((typeArguments != null) && (resolvedType.isGenericDefinition()) && (typeArguments.size() == resolvedType.getGenericParameters().size()))
         {
 
 
           instantiatedType = instantiatedType.makeGenericType(typeArguments);
         }
         
         AstType declaredType = this._astBuilder.convertType(instantiatedType);
         ObjectCreationExpression creation;
         if (resolvedType.isAnonymous()) {
           creation = new AnonymousObjectCreationExpression(byteCode.getOffset(), this._astBuilder.createType(resolvedType).clone(), declaredType);
 
 
         }
         else
         {
 
           creation = new ObjectCreationExpression(byteCode.getOffset(), declaredType);
         }
       }
       else {
         ConvertTypeOptions options = new ConvertTypeOptions();
         options.setIncludeTypeParameterDefinitions(false);
         creation = new ObjectCreationExpression(byteCode.getOffset(), this._astBuilder.convertType(declaringType, options));
       }
       
       creation.getArguments().addAll(adjustArgumentsForMethodCall(methodReference, arguments));
       creation.putUserData(Keys.MEMBER_REFERENCE, methodReference);
       
       return creation;
     }
     
     InvocationExpression invocation;
     InvocationExpression invocation;
     if (methodReference.isConstructor()) {
       invocation = new InvocationExpression(byteCode.getOffset(), target, adjustArgumentsForMethodCall(methodReference, arguments));
 
 
     }
     else
     {
 
       invocation = target.invoke(methodReference.getName(), convertTypeArguments(methodReference), adjustArgumentsForMethodCall(methodReference, arguments));
     }
     
 
 
 
 
     invocation.putUserData(Keys.MEMBER_REFERENCE, methodReference);
     
     return invocation;
   }
   
   private List<AstType> convertTypeArguments(MethodReference methodReference)
   {
     if ((this._context.getSettings().getForceExplicitTypeArguments()) && ((methodReference instanceof IGenericInstance)))
     {
 
       List<TypeReference> typeArguments = ((IGenericInstance)methodReference).getTypeArguments();
       
       if (!typeArguments.isEmpty()) {
         List<AstType> astTypeArguments = new ArrayList();
         
         for (TypeReference type : typeArguments) {
           astTypeArguments.add(this._astBuilder.convertType(type));
         }
         
         return astTypeArguments;
       }
     }
     
     return Collections.emptyList();
   }
   
   private List<Expression> adjustArgumentsForMethodCall(MethodReference method, List<Expression> arguments)
   {
     if ((!arguments.isEmpty()) && (method.isConstructor())) {
       TypeReference declaringType = method.getDeclaringType();
       
       if (declaringType.isNested()) {
         TypeDefinition resolvedType = declaringType.resolve();
         
         if (resolvedType != null) {
           if (resolvedType.isLocalClass()) {
             return arguments;
           }
           
           if (resolvedType.isInnerClass()) {
             MethodDefinition resolvedMethod = method.resolve();
             
             if ((resolvedMethod != null) && (resolvedMethod.isSynthetic()) && ((resolvedMethod.getFlags() & 0x7) == 0L))
             {
 
 
               List<ParameterDefinition> parameters = resolvedMethod.getParameters();
               
               int start = 0;
               int end = arguments.size();
               
               for (int i = parameters.size() - 1; i >= 0; i--) {
                 TypeReference parameterType = ((ParameterDefinition)parameters.get(i)).getParameterType();
                 TypeDefinition resolvedParameterType = parameterType.resolve();
                 
                 if ((resolvedParameterType == null) || (!resolvedParameterType.isAnonymous())) break;
                 end--;
               }
               
 
 
 
 
               if ((!resolvedType.isStatic()) && (!this._context.getSettings().getShowSyntheticMembers())) {
                 start++;
               }
               
               if (start > end) {
                 return Collections.emptyList();
               }
               
               return adjustArgumentsForMethodCallCore(method.getParameters().subList(start, end), arguments.subList(start, end));
             }
           }
         }
       }
     }
     
 
 
 
     return adjustArgumentsForMethodCallCore(method.getParameters(), arguments);
   }
   
 
 
   private List<Expression> adjustArgumentsForMethodCallCore(List<ParameterDefinition> parameters, List<Expression> arguments)
   {
     int parameterCount = parameters.size();
     
     assert (parameterCount == arguments.size());
     
     JavaResolver resolver = new JavaResolver(this._context);
     ConvertTypeOptions options = new ConvertTypeOptions();
     
     options.setAllowWildcards(false);
     
     int i = 0; for (int n = arguments.size(); i < n; i++) {
       Expression argument = (Expression)arguments.get(i);
       ResolveResult resolvedArgument = resolver.apply(argument);
       
       if ((resolvedArgument != null) && (!(argument instanceof LambdaExpression)))
       {
 
 
 
 
 
         ParameterDefinition p = (ParameterDefinition)parameters.get(i);
         
         TypeReference aType = resolvedArgument.getType();
         TypeReference pType = p.getParameterType();
         
         if (isCastRequired(pType, aType, true)) {
           arguments.set(i, new CastExpression(this._astBuilder.convertType(pType, options), argument));
         }
       }
     }
     
 
 
     int first = 0;int last = parameterCount - 1;
     
     while ((first < parameterCount) && (((ParameterDefinition)parameters.get(first)).isSynthetic())) {
       first++;
     }
     
     while ((last >= 0) && (((ParameterDefinition)parameters.get(last)).isSynthetic())) {
       last--;
     }
     
     if ((first >= parameterCount) || (last < 0)) {
       return Collections.emptyList();
     }
     
     if ((first == 0) && (last == parameterCount - 1)) {
       return arguments;
     }
     
     return arguments.subList(first, last + 1);
   }
   
   private boolean isCastRequired(TypeReference targetType, TypeReference sourceType, boolean exactMatch) {
     if ((targetType == null) || (sourceType == null)) {
       return false;
     }
     
     if (targetType.isPrimitive()) {
       return sourceType.getSimpleType() != targetType.getSimpleType();
     }
     
     if (exactMatch) {
       return !MetadataHelper.isSameType(targetType, sourceType, true);
     }
     
     return !MetadataHelper.isAssignableFrom(targetType, sourceType);
   }
   
   private static Expression inlineAssembly(com.strobel.decompiler.ast.Expression byteCode, List<Expression> arguments) {
     if (byteCode.getOperand() != null) {
       arguments.add(0, new IdentifierExpression(byteCode.getOffset(), formatByteCodeOperand(byteCode.getOperand())));
     }
     
     return new IdentifierExpression(byteCode.getOffset(), byteCode.getCode().getName()).invoke(arguments);
   }
   
   private static String formatByteCodeOperand(Object operand) {
     if (operand == null) {
       return "";
     }
     
     PlainTextOutput output = new PlainTextOutput();
     com.strobel.decompiler.DecompilerHelpers.writeOperand(output, operand);
     return output.toString();
   }
 }


