 package com.strobel.decompiler.ast;
 
 import com.strobel.assembler.metadata.ArrayType;
 import com.strobel.assembler.metadata.BuiltinTypes;
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.GenericParameter;
 import com.strobel.assembler.metadata.IGenericInstance;
 import com.strobel.assembler.metadata.MetadataHelper;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.assembler.metadata.TypeSubstitutionVisitor;
 import com.strobel.core.StrongBox;
 import com.strobel.decompiler.DecompilerContext;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 public final class TypeAnalysis
 {
   private static final int FLAG_BOOLEAN_PROHIBITED = 1;
   private final List<ExpressionToInfer> _allExpressions;
   private final Set<Variable> _singleStoreVariables;
   private final Set<Variable> _singleLoadVariables;
   private final Set<Variable> _allVariables;
   private final Map<Variable, List<ExpressionToInfer>> _assignmentExpressions;
   private final Map<Variable, Set<TypeReference>> _previouslyInferred;
   private final java.util.IdentityHashMap<Variable, TypeReference> _inferredVariableTypes;
   private final java.util.Stack<Expression> _stack;
   private DecompilerContext _context;
   private com.strobel.assembler.metadata.CoreMetadataFactory _factory;
   private boolean _preserveMetadataTypes;
   private boolean _preserveMetadataGenericTypes;
   private boolean _doneInitializing;
   
   public TypeAnalysis()
   {
     this._allExpressions = new java.util.ArrayList();
     this._singleStoreVariables = new java.util.LinkedHashSet();
     this._singleLoadVariables = new java.util.LinkedHashSet();
     this._allVariables = new java.util.LinkedHashSet();
     
     this._assignmentExpressions = new java.util.LinkedHashMap()
     {
 
       public List<TypeAnalysis.ExpressionToInfer> get(Object key)
       {
         List<TypeAnalysis.ExpressionToInfer> value = (List)super.get(key);
         
         if (value == null) {
           if (TypeAnalysis.this._doneInitializing) {
             return java.util.Collections.emptyList();
           }
           
           put((Variable)key, value = new java.util.ArrayList());
         }
         
         return value;
       }
       
     };
     this._previouslyInferred = new DefaultMap(com.strobel.core.CollectionUtilities.setFactory());
     this._inferredVariableTypes = new java.util.IdentityHashMap();
     this._stack = new java.util.Stack();
   }
   
 
 
 
 
   public static void run(DecompilerContext context, Block method)
   {
     TypeAnalysis ta = new TypeAnalysis();
     
     com.strobel.assembler.ir.attributes.SourceAttribute localVariableTable = com.strobel.assembler.ir.attributes.SourceAttribute.find("LocalVariableTable", context.getCurrentMethod().getSourceAttributes());
     
 
 
 
     com.strobel.assembler.ir.attributes.SourceAttribute localVariableTypeTable = com.strobel.assembler.ir.attributes.SourceAttribute.find("LocalVariableTypeTable", context.getCurrentMethod().getSourceAttributes());
     
 
 
 
     ta._context = context;
     ta._factory = com.strobel.assembler.metadata.CoreMetadataFactory.make(context.getCurrentType(), context.getCurrentMethod());
     ta._preserveMetadataTypes = (localVariableTable != null);
     ta._preserveMetadataGenericTypes = (localVariableTypeTable != null);
     
     ta.createDependencyGraph(method);
     ta.identifySingleLoadVariables();
     ta._doneInitializing = true;
     ta.runInference();
   }
   
   public static void reset(DecompilerContext context, Block method) {
     com.strobel.assembler.ir.attributes.SourceAttribute localVariableTable = com.strobel.assembler.ir.attributes.SourceAttribute.find("LocalVariableTable", context.getCurrentMethod().getSourceAttributes());
     
 
 
 
     com.strobel.assembler.ir.attributes.SourceAttribute localVariableTypeTable = com.strobel.assembler.ir.attributes.SourceAttribute.find("LocalVariableTypeTable", context.getCurrentMethod().getSourceAttributes());
     
 
 
 
     boolean preserveTypesFromMetadata = localVariableTable != null;
     boolean preserveGenericTypesFromMetadata = localVariableTypeTable != null;
     
     for (Expression e : method.getSelfAndChildrenRecursive(Expression.class)) {
       e.setInferredType(null);
       e.setExpectedType(null);
       
       Object operand = e.getOperand();
       
       if ((operand instanceof Variable)) {
         Variable variable = (Variable)operand;
         
         if (shouldResetVariableType(variable, preserveTypesFromMetadata, preserveGenericTypesFromMetadata)) {
           variable.setType(null);
         }
       }
     }
   }
   
 
   private void createDependencyGraph(Node node)
   {
     if ((node instanceof Condition)) {
       ((Condition)node).getCondition().setExpectedType(BuiltinTypes.Boolean);
     }
     else if (((node instanceof Loop)) && (((Loop)node).getCondition() != null))
     {
 
       ((Loop)node).getCondition().setExpectedType(BuiltinTypes.Boolean);
     }
     else if ((node instanceof CatchBlock)) {
       CatchBlock catchBlock = (CatchBlock)node;
       
       if ((catchBlock.getExceptionVariable() != null) && (catchBlock.getExceptionType() != null) && (catchBlock.getExceptionVariable().getType() == null))
       {
 
 
         catchBlock.getExceptionVariable().setType(catchBlock.getExceptionType());
       }
     }
     else if ((node instanceof Expression)) {
       Expression expression = (Expression)node;
       ExpressionToInfer expressionToInfer = new ExpressionToInfer();
       
       expressionToInfer.expression = expression;
       
       this._allExpressions.add(expressionToInfer);
       
       findNestedAssignments(expression, expressionToInfer);
       
       if (expression.getCode().isStore()) {
         if (((expression.getOperand() instanceof Variable)) && (shouldInferVariableType((Variable)expression.getOperand())))
         {
 
           ((List)this._assignmentExpressions.get(expression.getOperand())).add(expressionToInfer);
           this._allVariables.add((Variable)expression.getOperand());
         } else { StrongBox<Variable> v;
           if ((PatternMatching.matchLoad((Node)expression.getArguments().get(0), v = new StrongBox())) && (shouldInferVariableType((Variable)v.value)))
           {
 
             ((List)this._assignmentExpressions.get(v.value)).add(expressionToInfer);
             this._allVariables.add(v.value);
           }
         }
       }
     } else if ((node instanceof Lambda)) {
       Lambda lambda = (Lambda)node;
       List<Variable> parameters = lambda.getParameters();
       
       for (Variable parameter : parameters) {
         this._assignmentExpressions.get(parameter);
       }
     }
     
     for (Node child : node.getChildren()) {
       createDependencyGraph(child);
     }
   }
   
   private void findNestedAssignments(Expression expression, ExpressionToInfer parent)
   {
     for (Expression argument : expression.getArguments()) {
       Object operand = argument.getOperand();
       
       if ((operand instanceof Variable)) {
         this._allVariables.add((Variable)operand);
       }
       
       if (argument.getCode() == AstCode.Store) {
         ExpressionToInfer expressionToInfer = new ExpressionToInfer();
         
         expressionToInfer.expression = argument;
         
         this._allExpressions.add(expressionToInfer);
         
         Variable variable = (Variable)operand;
         
         if (shouldInferVariableType(variable)) {
           ((List)this._assignmentExpressions.get(variable)).add(expressionToInfer);
           this._allVariables.add(variable);
           
 
 
 
           parent.dependencies.add(variable);
         }
       }
       else if (argument.getCode() == AstCode.Inc) {
         ExpressionToInfer expressionToInfer = new ExpressionToInfer();
         
         expressionToInfer.expression = argument;
         
         this._allExpressions.add(expressionToInfer);
         
         Variable variable = (Variable)operand;
         
         if (shouldInferVariableType(variable)) {
           ((List)this._assignmentExpressions.get(variable)).add(expressionToInfer);
           this._allVariables.add(variable);
           
 
 
 
           parent.dependencies.add(variable);
         }
       }
       else if ((argument.getCode() == AstCode.PreIncrement) || (argument.getCode() == AstCode.PostIncrement))
       {
 
         ExpressionToInfer expressionToInfer = new ExpressionToInfer();
         
         expressionToInfer.expression = argument;
         
         this._allExpressions.add(expressionToInfer);
         
         Expression load = (Expression)com.strobel.core.CollectionUtilities.firstOrDefault(argument.getArguments());
         StrongBox<Variable> variable = new StrongBox();
         
         if ((load != null) && (PatternMatching.matchLoadOrRet(load, variable)) && (shouldInferVariableType((Variable)variable.value)))
         {
 
 
           ((List)this._assignmentExpressions.get(variable.value)).add(expressionToInfer);
           this._allVariables.add(variable.value);
           
 
 
 
           parent.dependencies.add(variable.value);
         }
       }
       else {
         StrongBox<Variable> variable = new StrongBox();
         
         if ((PatternMatching.matchLoadOrRet(argument, variable)) && (shouldInferVariableType((Variable)variable.value)))
         {
 
           parent.dependencies.add(variable.value);
           this._allVariables.add(variable.value);
         }
       }
       
       findNestedAssignments(argument, parent);
     }
   }
   
   private boolean isSingleStoreBoolean(Variable variable) {
     if (this._singleStoreVariables.contains(variable)) {
       List<ExpressionToInfer> assignments = (List)this._assignmentExpressions.get(variable);
       ExpressionToInfer e = (ExpressionToInfer)com.strobel.core.CollectionUtilities.single(assignments);
       return PatternMatching.matchBooleanConstant((Node)com.strobel.core.CollectionUtilities.last(e.expression.getArguments())) != null;
     }
     return false;
   }
   
   private void identifySingleLoadVariables()
   {
     Map<Variable, List<ExpressionToInfer>> groupedExpressions = new DefaultMap(new com.strobel.functions.Supplier()
     {
       public List<TypeAnalysis.ExpressionToInfer> get()
       {
         return new java.util.ArrayList();
       }
     });
     
 
     for (Iterator i$ = this._allExpressions.iterator(); i$.hasNext();) { expressionToInfer = (ExpressionToInfer)i$.next();
       for (Variable variable : expressionToInfer.dependencies) {
         ((List)groupedExpressions.get(variable)).add(expressionToInfer);
       }
     }
     ExpressionToInfer expressionToInfer;
     for (Iterator i$ = groupedExpressions.keySet().iterator(); i$.hasNext();) { variable = (Variable)i$.next();
       List<ExpressionToInfer> expressions = (List)groupedExpressions.get(variable);
       
       if (expressions.size() == 1) {
         int references = 0;
         
         for (Expression expression : ((ExpressionToInfer)expressions.get(0)).expression.getSelfAndChildrenRecursive(Expression.class)) {
           if (expression.getOperand() == variable) { references++; if (references > 1) {
               break;
             }
           }
         }
         
 
         if (references == 1) {
           this._singleLoadVariables.add(variable);
           
 
 
 
           for (ExpressionToInfer assignment : (List)this._assignmentExpressions.get(variable)) {
             assignment.dependsOnSingleLoad = variable;
           }
         }
       }
     }
     Variable variable;
     for (Variable variable : this._assignmentExpressions.keySet()) {
       if (((List)this._assignmentExpressions.get(variable)).size() == 1) {
         this._singleStoreVariables.add(variable);
       }
     }
   }
   
   private void runInference()
   {
     this._previouslyInferred.clear();
     this._inferredVariableTypes.clear();
     
     int numberOfExpressionsAlreadyInferred = 0;
     
 
 
 
 
     boolean ignoreSingleLoadDependencies = false;
     boolean assignVariableTypesBasedOnPartialInformation = false;
     
     com.strobel.core.Predicate<Variable> dependentVariableTypesKnown = new com.strobel.core.Predicate()
     {
       public boolean test(Variable v) {
         return (TypeAnalysis.this.inferTypeForVariable(v, null) != null) || (TypeAnalysis.this._singleLoadVariables.contains(v));
       }
     };
     
     while (numberOfExpressionsAlreadyInferred < this._allExpressions.size()) {
       int oldCount = numberOfExpressionsAlreadyInferred;
       
       for (ExpressionToInfer e : this._allExpressions) {
         if ((!e.done) && (trueForAll(e.dependencies, dependentVariableTypesKnown)) && ((e.dependsOnSingleLoad == null) || (e.dependsOnSingleLoad.getType() != null) || (ignoreSingleLoadDependencies)))
         {
 
 
           runInference(e.expression);
           e.done = true;
           numberOfExpressionsAlreadyInferred++;
         }
       }
       
       if (numberOfExpressionsAlreadyInferred == oldCount) {
         if (ignoreSingleLoadDependencies) {
           if (assignVariableTypesBasedOnPartialInformation) {
             throw new IllegalStateException("Could not infer any expression.");
           }
           
           assignVariableTypesBasedOnPartialInformation = true;
 
 
         }
         else
         {
 
 
           ignoreSingleLoadDependencies = true;
         }
       }
       else
       {
         assignVariableTypesBasedOnPartialInformation = false;
         ignoreSingleLoadDependencies = false;
       }
       
 
 
 
       inferTypesForVariables(assignVariableTypesBasedOnPartialInformation);
     }
     
     verifyResults();
   }
   
   private void verifyResults() {
     StrongBox<Expression> a = new StrongBox();
     
     for (Iterator i$ = this._allVariables.iterator(); i$.hasNext();) { variable = (Variable)i$.next();
       TypeReference type = variable.getType();
       
       if ((type == null) || (type == BuiltinTypes.Null)) {
         TypeReference inferredType = inferTypeForVariable(variable, BuiltinTypes.Object);
         
         if ((inferredType == null) || (inferredType == BuiltinTypes.Null)) {
           variable.setType(BuiltinTypes.Object);
         }
         else {
           variable.setType(inferredType);
         }
       }
       else if (type.isWildcardType()) {
         variable.setType(MetadataHelper.getUpperBound(type));
       }
       else if (type.getSimpleType() == com.strobel.assembler.metadata.JvmType.Boolean)
       {
 
 
 
 
         for (ExpressionToInfer e : (List)this._assignmentExpressions.get(variable)) {
           if (PatternMatching.matchStore(e.expression, variable, a)) {
             Boolean booleanConstant = PatternMatching.matchBooleanConstant((Node)a.value);
             
             if (booleanConstant != null) {
               e.expression.setExpectedType(BuiltinTypes.Boolean);
               e.expression.setInferredType(BuiltinTypes.Boolean);
               ((Expression)a.value).setExpectedType(BuiltinTypes.Boolean);
               ((Expression)a.value).setInferredType(BuiltinTypes.Boolean);
             }
             
           }
         }
       }
       else if (type.getSimpleType() == com.strobel.assembler.metadata.JvmType.Character)
       {
 
 
 
 
         for (ExpressionToInfer e : (List)this._assignmentExpressions.get(variable)) {
           if (PatternMatching.matchStore(e.expression, variable, a)) {
             Character characterConstant = PatternMatching.matchCharacterConstant((Node)a.value);
             
             if (characterConstant != null) {
               e.expression.setExpectedType(BuiltinTypes.Character);
               e.expression.setInferredType(BuiltinTypes.Character);
               ((Expression)a.value).setExpectedType(BuiltinTypes.Character);
               ((Expression)a.value).setInferredType(BuiltinTypes.Character);
             }
           }
         }
       }
     }
     Variable variable;
   }
   
   private void inferTypesForVariables(boolean assignVariableTypesBasedOnPartialInformation)
   {
     for (Iterator i$ = this._allVariables.iterator(); i$.hasNext();) { variable = (Variable)i$.next();
       expressionsToInfer = (List)this._assignmentExpressions.get(variable);
       
       boolean inferredFromNull = false;
       inferredType = null;
       
       if (variable.isLambdaParameter()) {
         inferredType = (TypeReference)this._inferredVariableTypes.get(variable);
         
         if (inferredType != null) {}
       }
       else
       {
         if ((expressionsToInfer.isEmpty()) || 
         
 
           (assignVariableTypesBasedOnPartialInformation ? !anyDone(expressionsToInfer) : !allDone(expressionsToInfer))) {
           continue;
         }
         for (ExpressionToInfer e : expressionsToInfer) {
           List<Expression> arguments = e.expression.getArguments();
           
           assert (((e.expression.getCode().isStore()) && (arguments.size() == 1)) || (e.expression.getCode() == AstCode.Inc) || (e.expression.getCode() == AstCode.PreIncrement) || (e.expression.getCode() == AstCode.PostIncrement));
           
 
 
 
           Expression assignedValue = (Expression)arguments.get(0);
           
           if (assignedValue.getInferredType() != null) {
             if (inferredType == null) {
               inferredType = adjustType(assignedValue.getInferredType(), e.flags);
               inferredFromNull = PatternMatching.match(assignedValue, AstCode.AConstNull);
             }
             else {
               TypeReference assigned = cleanTypeArguments(assignedValue.getInferredType(), inferredType);
               TypeReference commonSuper = adjustType(typeWithMoreInformation(inferredType, assigned), e.flags);
               
               if ((inferredFromNull) && (assigned != BuiltinTypes.Null) && (!MetadataHelper.isAssignableFrom(commonSuper, assigned)))
               {
 
 
 
 
 
 
 
 
                 TypeReference asSubType = MetadataHelper.asSubType(commonSuper, assigned);
                 
                 inferredType = asSubType != null ? asSubType : assigned;
                 inferredFromNull = false;
 
               }
               else
               {
 
                 inferredType = commonSuper;
               }
             }
           }
         }
       }
       
 
 
 
       if (inferredType == null) {
         inferredType = variable.getType();
       }
       else if (!inferredType.isUnbounded()) {
         inferredType = inferredType.hasSuperBound() ? inferredType.getSuperBound() : inferredType.getExtendsBound();
       }
       
 
       if ((shouldInferVariableType(variable)) && (inferredType != null)) {
         variable.setType(inferredType);
         this._inferredVariableTypes.put(variable, inferredType);
         
 
 
 
 
 
 
 
 
 
 
 
         for (ExpressionToInfer e : this._allExpressions)
           if ((e.dependencies.contains(variable)) || (expressionsToInfer.contains(e)))
           {
 
             if (!this._stack.contains(e.expression))
             {
 
 
               boolean invalidate = false;
               
               for (Expression c : e.expression.getSelfAndChildrenRecursive(Expression.class)) {
                 if (!this._stack.contains(c))
                 {
 
 
                   c.setExpectedType(null);
                   
                   if (((PatternMatching.matchLoad(c, variable)) || (PatternMatching.matchStore(c, variable))) && (!MetadataHelper.isSameType(c.getInferredType(), inferredType)))
                   {
 
                     c.setExpectedType(inferredType);
                   }
                   
                   c.setInferredType(null);
                   
                   invalidate = true;
                 }
               }
               if (invalidate)
                 runInference(e.expression, e.flags);
             } }
       }
     }
     Variable variable;
     List<ExpressionToInfer> expressionsToInfer;
     TypeReference inferredType;
   }
   
   private boolean shouldInferVariableType(Variable variable) { com.strobel.assembler.metadata.VariableDefinition variableDefinition = variable.getOriginalVariable();
     
     if ((variable.isGenerated()) || (variable.isLambdaParameter()))
     {
 
       return true;
     }
     
     if (variable.isParameter()) {
       ParameterDefinition parameter = variable.getOriginalParameter();
       
       if (parameter == this._context.getCurrentMethod().getBody().getThisParameter()) {
         return false;
       }
       
       TypeReference parameterType = parameter.getParameterType();
       
       return (!this._preserveMetadataGenericTypes) && ((parameterType.isGenericType()) || (MetadataHelper.isRawType(parameterType)));
     }
     
 
 
     if ((variableDefinition != null) && (variableDefinition.isFromMetadata()) && (variableDefinition.getVariableType().isGenericType() ? this._preserveMetadataGenericTypes : this._preserveMetadataTypes))
     {
 
 
 
       return false;
     }
     
     return true;
   }
   
 
 
 
   private static boolean shouldResetVariableType(Variable variable, boolean preserveTypesFromMetadata, boolean preserveGenericTypesFromMetadata)
   {
     if ((variable.isGenerated()) || (variable.isLambdaParameter()))
     {
 
       return true;
     }
     
     com.strobel.assembler.metadata.VariableDefinition variableDefinition = variable.getOriginalVariable();
     
 
     if ((variableDefinition != null) && (variableDefinition.isFromMetadata()) && (variableDefinition.getVariableType().isGenericType() ? preserveGenericTypesFromMetadata : preserveTypesFromMetadata))
     {
 
 
 
       return false;
     }
     
     return ((variableDefinition != null) && (variableDefinition.getVariableType() == BuiltinTypes.Integer)) || ((variableDefinition != null) && (!variableDefinition.isTypeKnown()));
   }
   
   private void runInference(Expression expression)
   {
     runInference(expression, 0);
   }
   
   private void runInference(Expression expression, int flags) {
     List<Expression> arguments = expression.getArguments();
     
     Variable changedVariable = null;
     boolean anyArgumentIsMissingExpectedType = false;
     
     for (Expression argument : arguments) {
       if (argument.getExpectedType() == null) {
         anyArgumentIsMissingExpectedType = true;
         break;
       }
     }
     
     if ((expression.getInferredType() == null) || (anyArgumentIsMissingExpectedType)) {
       inferTypeForExpression(expression, expression.getExpectedType(), anyArgumentIsMissingExpectedType, flags);
     }
     else if ((expression.getInferredType() == BuiltinTypes.Integer) && (expression.getExpectedType() == BuiltinTypes.Boolean))
     {
 
       if ((expression.getCode() == AstCode.Load) || (expression.getCode() == AstCode.Store)) {
         Variable variable = (Variable)expression.getOperand();
         
         expression.setInferredType(BuiltinTypes.Boolean);
         
         if ((variable.getType() == BuiltinTypes.Integer) && (shouldInferVariableType(variable)))
         {
 
           variable.setType(BuiltinTypes.Boolean);
           changedVariable = variable;
         }
       }
     }
     else if ((expression.getInferredType() == BuiltinTypes.Integer) && (expression.getExpectedType() == BuiltinTypes.Character))
     {
 
       if ((expression.getCode() == AstCode.Load) || (expression.getCode() == AstCode.Store)) {
         Variable variable = (Variable)expression.getOperand();
         
         expression.setInferredType(BuiltinTypes.Character);
         
         if ((variable.getType() == BuiltinTypes.Integer) && (shouldInferVariableType(variable)) && (this._singleLoadVariables.contains(variable)))
         {
 
 
           variable.setType(BuiltinTypes.Character);
           changedVariable = variable;
         }
       }
     }
     
     for (Expression argument : arguments) {
       if (!argument.getCode().isStore()) {
         runInference(argument, flags);
       }
     }
     
     if ((changedVariable != null) && 
       (((Set)this._previouslyInferred.get(changedVariable)).add(changedVariable.getType()))) {
       invalidateDependentExpressions(expression, changedVariable);
     }
   }
   
   private void invalidateDependentExpressions(Expression expression, Variable variable)
   {
     List<ExpressionToInfer> assignments = (List)this._assignmentExpressions.get(variable);
     TypeReference inferredType = (TypeReference)this._inferredVariableTypes.get(variable);
     
     for (ExpressionToInfer e : this._allExpressions) {
       if ((e.expression != expression) && ((e.dependencies.contains(variable)) || (assignments.contains(e))))
       {
 
 
         if (!this._stack.contains(e.expression))
         {
 
 
           boolean invalidate = false;
           
           for (Expression c : e.expression.getSelfAndChildrenRecursive(Expression.class)) {
             if (!this._stack.contains(c))
             {
 
 
               c.setExpectedType(null);
               
               if (((PatternMatching.matchLoad(c, variable)) || (PatternMatching.matchStore(c, variable))) && (!MetadataHelper.isSameType(c.getInferredType(), inferredType)))
               {
 
                 c.setExpectedType(inferredType);
               }
               
               c.setInferredType(null);
               
               invalidate = true;
             }
           }
           if (invalidate)
           {
 
 
 
             runInference(e.expression, e.flags); }
         }
       }
     }
   }
   
   private TypeReference inferTypeForExpression(Expression expression, TypeReference expectedType) {
     return inferTypeForExpression(expression, expectedType, 0);
   }
   
   private TypeReference inferTypeForExpression(Expression expression, TypeReference expectedType, int flags) {
     return inferTypeForExpression(expression, expectedType, false, flags);
   }
   
 
 
 
   private TypeReference inferTypeForExpression(Expression expression, TypeReference expectedType, boolean forceInferChildren)
   {
     return inferTypeForExpression(expression, expectedType, forceInferChildren, 0);
   }
   
 
 
 
 
   private TypeReference inferTypeForExpression(Expression expression, TypeReference expectedType, boolean forceInferChildren, int flags)
   {
     boolean actualForceInferChildren = forceInferChildren;
     
     if ((expectedType != null) && (!isSameType(expression.getExpectedType(), expectedType)))
     {
 
       expression.setExpectedType(expectedType);
       
 
 
 
       if (!expression.getCode().isStore()) {
         actualForceInferChildren = true;
       }
     }
     
     if ((actualForceInferChildren) || (expression.getInferredType() == null)) {
       expression.setInferredType(doInferTypeForExpression(expression, expectedType, actualForceInferChildren, flags));
     }
     
     return expression.getInferredType();
   }
   
 
 
 
 
 
   private TypeReference doInferTypeForExpression(Expression expression, TypeReference expectedType, boolean forceInferChildren, int flags)
   {
     if ((this._stack.contains(expression)) && (!PatternMatching.match(expression, AstCode.LdC))) {
       return expectedType;
     }
     
     this._stack.push(expression);
     try
     {
       AstCode code = expression.getCode();
       Object operand = expression.getOperand();
       List<Expression> arguments = expression.getArguments();
       Object localObject1;
       Object localObject2; Object v; int i; Object field; Object effectiveField; TypeReference inferredType; Number n; Object op; TypeReference targetType; Object number; int i; TypeReference localTypeReference1; Object arrayType; TypeReference elementType; Object i$; Expression argument; TypeReference expectedElementType; TypeReference inferredType; Object type; TypeReference returnType; Object lambda; Object parameters; Object argument; TypeReference result; TypeReference inferredType; switch (code) {
       case LogicalNot: 
         if (forceInferChildren) {
           inferTypeForExpression((Expression)arguments.get(0), BuiltinTypes.Boolean);
         }
         
         return BuiltinTypes.Boolean;
       
 
       case LogicalAnd: 
       case LogicalOr: 
         if (forceInferChildren) {
           inferTypeForExpression((Expression)arguments.get(0), BuiltinTypes.Boolean);
           inferTypeForExpression((Expression)arguments.get(1), BuiltinTypes.Boolean);
         }
         
         return BuiltinTypes.Boolean;
       
 
       case TernaryOp: 
         if (forceInferChildren) {
           inferTypeForExpression((Expression)arguments.get(0), BuiltinTypes.Boolean);
         }
         
         return inferBinaryArguments((Expression)arguments.get(1), (Expression)arguments.get(2), expectedType, forceInferChildren, null, null, 0);
       
 
 
 
 
 
 
 
 
 
       case MonitorEnter: 
       case MonitorExit: 
         return null;
       
       case Store: 
         Variable v = (Variable)operand;
         TypeReference lastInferredType = (TypeReference)this._inferredVariableTypes.get(v);
         
         if ((PatternMatching.matchBooleanConstant((Node)expression.getArguments().get(0)) != null) && (shouldInferVariableType(v))) if (isBoolean(inferTypeForVariable(v, expectedType != null ? expectedType : BuiltinTypes.Boolean, true, flags)))
           {
 
 
             return BuiltinTypes.Boolean;
           }
         TypeReference inferredType;
         if ((forceInferChildren) || ((lastInferredType == null) && (v.getType() == null)))
         {
 
 
           inferredType = inferTypeForExpression((Expression)expression.getArguments().get(0), inferTypeForVariable(v, null, flags), flags);
           
 
 
 
 
           if ((inferredType != null) && (inferredType.isWildcardType())) {
             inferredType = MetadataHelper.getUpperBound(inferredType);
           }
           
           if (inferredType != null) {
             return adjustType(inferredType, flags);
           }
         }
         
         return adjustType(lastInferredType != null ? lastInferredType : v.getType(), flags);
       
 
       case Load: 
         v = (Variable)expression.getOperand();
         TypeReference inferredType = inferTypeForVariable((Variable)v, expectedType, flags);
         com.strobel.assembler.metadata.TypeDefinition thisType = this._context.getCurrentType();
         
         if ((((Variable)v).isParameter()) && (((Variable)v).getOriginalParameter() == this._context.getCurrentMethod().getBody().getThisParameter()))
         {
 
           if ((this._singleLoadVariables.contains(v)) && (((Variable)v).getType() == null)) {
             ((Variable)v).setType(thisType);
           }
           
           return thisType;
         }
         
         Object result = inferredType;
         
         if ((expectedType != null) && (expectedType != BuiltinTypes.Null) && (shouldInferVariableType((Variable)v)))
         {
           TypeReference tempResult;
           
           TypeReference tempResult;
           
           if (MetadataHelper.isSubType(inferredType, expectedType)) {
             tempResult = inferredType;
           }
           else {
             tempResult = MetadataHelper.asSubType(inferredType, expectedType);
           }
           
           if ((tempResult != null) && (tempResult.containsGenericParameters()))
           {
 
             Map<TypeReference, TypeReference> mappings = MetadataHelper.adapt(tempResult, inferredType);
             
             List<TypeReference> mappingsToRemove = null;
             
             for (TypeReference key : mappings.keySet()) {
               GenericParameter gp = this._context.getCurrentMethod().findTypeVariable(key.getSimpleName());
               
               if (MetadataHelper.isSameType(gp, key, true)) {
                 if (mappingsToRemove == null) {
                   mappingsToRemove = new java.util.ArrayList();
                 }
                 mappingsToRemove.add(key);
               }
             }
             
             if (mappingsToRemove != null) {
               mappings.keySet().removeAll(mappingsToRemove);
             }
             
             if (!mappings.isEmpty()) {
               tempResult = TypeSubstitutionVisitor.instance().visit(tempResult, mappings);
             }
           }
           
           if ((tempResult == null) && (((Variable)v).getType() != null)) {
             tempResult = MetadataHelper.asSubType(((Variable)v).getType(), expectedType);
             
             if (tempResult == null) {
               tempResult = MetadataHelper.asSubType(MetadataHelper.eraseRecursive(((Variable)v).getType()), expectedType);
             }
           }
           
           if (tempResult == null) {
             tempResult = expectedType;
           }
           
           result = tempResult;
           
           if (((TypeReference)result).isGenericType()) {
             if ((expectedType.isGenericDefinition()) && (!((TypeReference)result).isGenericDefinition())) {
               result = ((TypeReference)result).getUnderlyingType();
             }
             if ((MetadataHelper.areGenericsSupported(thisType)) && 
               (MetadataHelper.getUnboundGenericParameterCount((TypeReference)result) > 0)) {
               result = MetadataHelper.substituteGenericArguments((TypeReference)result, inferredType);
             }
           }
           
 
           if ((((TypeReference)result).isGenericDefinition()) && (!MetadataHelper.canReferenceTypeVariablesOf((TypeReference)result, this._context.getCurrentType()))) {
             result = new com.strobel.assembler.metadata.RawType(((TypeReference)result).getUnderlyingType());
           }
         }
         
         List<ExpressionToInfer> assignments = (List)this._assignmentExpressions.get(v);
         
         if ((result == null) && (assignments.isEmpty())) {
           result = BuiltinTypes.Object;
         }
         
         if ((result != null) && (((TypeReference)result).isWildcardType())) {
           result = MetadataHelper.getUpperBound((TypeReference)result);
         }
         
         result = adjustType((TypeReference)result, flags);
         
         if (flags != 0) {
           for (i = 0; i < assignments.size(); i++) {
             ((ExpressionToInfer)assignments.get(i)).flags |= flags;
           }
         }
         
         this._inferredVariableTypes.put(v, result);
         
         if ((result != null) && (!MetadataHelper.isSameType((TypeReference)result, inferredType)) && (((Set)this._previouslyInferred.get(v)).add(result)))
         {
 
 
           expression.setInferredType((TypeReference)result);
           invalidateDependentExpressions(expression, (Variable)v);
         }
         
         if ((this._singleLoadVariables.contains(v)) && (((Variable)v).getType() == null)) {
           ((Variable)v).setType((TypeReference)result);
         }
         
         return (int)result;
       
 
       case InvokeDynamic: 
         return inferDynamicCall(expression, expectedType, forceInferChildren);
       
 
       case InvokeVirtual: 
       case InvokeSpecial: 
       case InvokeStatic: 
       case InvokeInterface: 
         return inferCall(expression, expectedType, forceInferChildren);
       
 
       case GetField: 
         field = (FieldReference)operand;
         com.strobel.assembler.metadata.FieldDefinition resolvedField;
         if (forceInferChildren) {
           resolvedField = ((FieldReference)field).resolve();
           effectiveField = resolvedField != null ? resolvedField : field;
           TypeReference targetType = inferTypeForExpression((Expression)arguments.get(0), ((FieldReference)field).getDeclaringType());
           
           if (targetType != null) {
             FieldReference asMember = MetadataHelper.asMemberOf((FieldReference)effectiveField, targetType);
             
             return asMember.getFieldType();
           }
         }
         
         return getFieldType((FieldReference)operand);
       
 
       case GetStatic: 
         return getFieldType((FieldReference)operand);
       
 
       case PutField: 
         if (forceInferChildren) {
           inferTypeForExpression((Expression)arguments.get(0), ((FieldReference)operand).getDeclaringType());
           
 
 
 
           inferTypeForExpression((Expression)arguments.get(1), getFieldType((FieldReference)operand));
         }
         
 
 
 
         return getFieldType((FieldReference)operand);
       
 
       case PutStatic: 
         if (forceInferChildren) {
           inferTypeForExpression((Expression)arguments.get(0), getFieldType((FieldReference)operand));
         }
         
 
 
 
         return getFieldType((FieldReference)operand);
       
 
       case __New: 
         return (TypeReference)operand;
       
 
       case PreIncrement: 
       case PostIncrement: 
         inferredType = inferTypeForExpression((Expression)arguments.get(0), null, flags | 0x1);
         
 
 
 
 
         if ((inferredType == null) || (inferredType == BuiltinTypes.Boolean)) {
           n = (Number)operand;
           
           if ((n instanceof Long)) {
             return BuiltinTypes.Long;
           }
           
           return BuiltinTypes.Integer;
         }
         
         return inferredType;
       
 
       case Not: 
       case Neg: 
         return inferTypeForExpression((Expression)arguments.get(0), expectedType);
       
 
       case Add: 
       case Sub: 
       case Mul: 
       case Or: 
       case And: 
       case Xor: 
       case Div: 
       case Rem: 
         return inferBinaryExpression(code, arguments, flags);
       
 
       case Shl: 
         if (forceInferChildren) {
           inferTypeForExpression((Expression)arguments.get(1), BuiltinTypes.Integer, flags | 0x1);
         }
         
 
 
 
 
         if ((expectedType != null) && ((expectedType.getSimpleType() == com.strobel.assembler.metadata.JvmType.Integer) || (expectedType.getSimpleType() == com.strobel.assembler.metadata.JvmType.Long)))
         {
 
 
           return numericPromotion(inferTypeForExpression((Expression)arguments.get(0), expectedType, flags | 0x1));
         }
         
 
 
 
 
 
 
         return numericPromotion(inferTypeForExpression((Expression)arguments.get(0), null, flags | 0x1));
       
 
 
 
 
 
 
 
       case Shr: 
       case UShr: 
         if (forceInferChildren) {
           inferTypeForExpression((Expression)arguments.get(1), BuiltinTypes.Integer, flags | 0x1);
         }
         
         TypeReference type = numericPromotion(inferTypeForExpression((Expression)arguments.get(0), null, flags | 0x1));
         
 
 
 
 
 
 
         if (type == null) {
           return null;
         }
         
         TypeReference expectedInputType = null;
         
         switch (type.getSimpleType()) {
         case Integer: 
           expectedInputType = BuiltinTypes.Integer;
           break;
         case Long: 
           expectedInputType = BuiltinTypes.Long;
         }
         
         
         if (expectedInputType != null) {
           inferTypeForExpression((Expression)arguments.get(0), expectedInputType);
           return expectedInputType;
         }
         
         return type;
       
 
       case CompoundAssignment: 
         op = (Expression)arguments.get(0);
         targetType = inferTypeForExpression((Expression)((Expression)op).getArguments().get(0), null);
         
         if (forceInferChildren) {
           inferTypeForExpression((Expression)arguments.get(0), targetType);
         }
         
         return targetType;
       
 
       case AConstNull: 
         if ((expectedType != null) && (!expectedType.isPrimitive())) {
           return expectedType;
         }
         
         return BuiltinTypes.Null;
       
 
       case LdC: 
         if (((operand instanceof Boolean)) && (PatternMatching.matchBooleanConstant(expression) != null) && (!com.strobel.assembler.metadata.Flags.testAny(flags, 1)))
         {
 
 
           return BuiltinTypes.Boolean;
         }
         
         if (((operand instanceof Character)) && (PatternMatching.matchCharacterConstant(expression) != null)) {
           return BuiltinTypes.Character;
         }
         
         if ((operand instanceof Number)) {
           number = (Number)operand;
           
           if ((number instanceof Integer)) {
             if (expectedType != null) {
               switch (expectedType.getSimpleType()) {
               case Boolean: 
                 if ((((Number)number).intValue() == 0) || (((Number)number).intValue() == 1)) {
                   return adjustType(BuiltinTypes.Boolean, flags);
                 }
                 return BuiltinTypes.Integer;
               
               case Byte: 
                 if ((((Number)number).intValue() >= -128) && (((Number)number).intValue() <= 127))
                 {
 
                   return BuiltinTypes.Byte;
                 }
                 return BuiltinTypes.Integer;
               
               case Character: 
                 if ((((Number)number).intValue() >= 0) && (((Number)number).intValue() <= 65535))
                 {
 
                   return BuiltinTypes.Character;
                 }
                 return BuiltinTypes.Integer;
               
               case Short: 
                 if ((((Number)number).intValue() >= 32768) && (((Number)number).intValue() <= 32767))
                 {
 
                   return BuiltinTypes.Short;
                 }
                 return BuiltinTypes.Integer;
               }
               
             } else if (PatternMatching.matchBooleanConstant(expression) != null) {
               return adjustType(BuiltinTypes.Boolean, flags);
             }
             
             return BuiltinTypes.Integer;
           }
           
           if ((number instanceof Long)) {
             return BuiltinTypes.Long;
           }
           
           if ((number instanceof Float)) {
             return BuiltinTypes.Float;
           }
           
           return BuiltinTypes.Double;
         }
         
         if ((operand instanceof TypeReference)) {
           return this._factory.makeParameterizedType(this._factory.makeNamedType("java.lang.Class"), null, new TypeReference[] { (TypeReference)operand });
         }
         
 
 
 
 
         return this._factory.makeNamedType("java.lang.String");
       
 
       case NewArray: 
       case __NewArray: 
       case __ANewArray: 
         if (forceInferChildren) {
           inferTypeForExpression((Expression)arguments.get(0), BuiltinTypes.Integer, flags | 0x1);
         }
         return ((TypeReference)operand).makeArrayType();
       
 
       case MultiANewArray: 
         if (forceInferChildren) {
           for (i = 0; i < arguments.size(); i++) {
             inferTypeForExpression((Expression)arguments.get(i), BuiltinTypes.Integer, flags | 0x1);
           }
         }
         return (TypeReference)operand;
       
 
       case InitObject: 
         return inferInitObject(expression, expectedType, forceInferChildren, (MethodReference)operand, arguments);
       
 
       case InitArray: 
         arrayType = (TypeReference)operand;
         elementType = ((TypeReference)arrayType).getElementType();
         
         if (forceInferChildren) {
           for (i$ = arguments.iterator(); ((Iterator)i$).hasNext();) { argument = (Expression)((Iterator)i$).next();
             inferTypeForExpression(argument, elementType);
           }
         }
         
         return (TypeReference)arrayType;
       
 
       case ArrayLength: 
         return BuiltinTypes.Integer;
       
 
 
       case LoadElement: 
         TypeReference arrayType = inferTypeForExpression((Expression)arguments.get(0), null);
         
         inferTypeForExpression((Expression)arguments.get(1), BuiltinTypes.Integer, flags | 0x1);
         
         if ((arrayType != null) && (arrayType.isArray())) {
           return arrayType.getElementType();
         }
         
 
 
 
 
         return null;
       
 
 
       case StoreElement: 
         TypeReference arrayType = inferTypeForExpression((Expression)arguments.get(0), null);
         
         inferTypeForExpression((Expression)arguments.get(1), BuiltinTypes.Integer, flags | 0x1);
         
         TypeReference expectedElementType;
         
         if ((arrayType != null) && (arrayType.isArray())) {
           expectedElementType = arrayType.getElementType();
 
         }
         else
         {
 
           expectedElementType = null;
         }
         
         if (forceInferChildren) {
           inferTypeForExpression((Expression)arguments.get(2), expectedElementType);
         }
         
         return expectedElementType;
       
 
       case __BIPush: 
       case __SIPush: 
         Number number = (Number)operand;
         
         if (expectedType != null) {
           if ((expectedType.getSimpleType() == com.strobel.assembler.metadata.JvmType.Boolean) && ((number.intValue() == 0) || (number.intValue() == 1)))
           {
 
             return BuiltinTypes.Boolean;
           }
           
           if ((expectedType.getSimpleType() == com.strobel.assembler.metadata.JvmType.Byte) && (number.intValue() >= -128) && (number.intValue() <= 127))
           {
 
 
             return BuiltinTypes.Byte;
           }
           
           if ((expectedType.getSimpleType() == com.strobel.assembler.metadata.JvmType.Character) && (number.intValue() >= 0) && (number.intValue() <= 65535))
           {
 
 
             return BuiltinTypes.Character;
           }
           
           if (expectedType.getSimpleType().isIntegral()) {
             return expectedType;
           }
         }
         else if (code == AstCode.__BIPush) {
           return BuiltinTypes.Byte;
         }
         
         return BuiltinTypes.Short;
       case I2L: 
       case I2F: 
       case I2D: 
       case L2I: 
       case L2F: 
       case L2D: 
       case F2I: 
       case F2L: 
       case F2D: 
       case D2I: 
       case D2L: 
       case D2F: 
       case I2B: 
       case I2C: 
       case I2S: 
         TypeReference conversionResult;
         
 
         Object expectedArgumentType;
         
         switch (code) {
         case I2L: 
           conversionResult = BuiltinTypes.Long;
           expectedArgumentType = BuiltinTypes.Integer;
           break;
         case I2F: 
           conversionResult = BuiltinTypes.Float;
           expectedArgumentType = BuiltinTypes.Integer;
           break;
         case I2D: 
           conversionResult = BuiltinTypes.Double;
           expectedArgumentType = BuiltinTypes.Integer;
           break;
         case L2I: 
           conversionResult = BuiltinTypes.Integer;
           expectedArgumentType = BuiltinTypes.Long;
           break;
         case L2F: 
           conversionResult = BuiltinTypes.Float;
           expectedArgumentType = BuiltinTypes.Long;
           break;
         case L2D: 
           conversionResult = BuiltinTypes.Double;
           expectedArgumentType = BuiltinTypes.Long;
           break;
         case F2I: 
           conversionResult = BuiltinTypes.Integer;
           expectedArgumentType = BuiltinTypes.Float;
           break;
         case F2L: 
           conversionResult = BuiltinTypes.Long;
           expectedArgumentType = BuiltinTypes.Float;
           break;
         case F2D: 
           conversionResult = BuiltinTypes.Double;
           expectedArgumentType = BuiltinTypes.Float;
           break;
         case D2I: 
           conversionResult = BuiltinTypes.Integer;
           expectedArgumentType = BuiltinTypes.Double;
           break;
         case D2L: 
           conversionResult = BuiltinTypes.Long;
           expectedArgumentType = BuiltinTypes.Double;
           break;
         case D2F: 
           conversionResult = BuiltinTypes.Float;
           expectedArgumentType = BuiltinTypes.Double;
           break;
         case I2B: 
           conversionResult = BuiltinTypes.Byte;
           expectedArgumentType = BuiltinTypes.Integer;
           break;
         case I2C: 
           conversionResult = BuiltinTypes.Character;
           expectedArgumentType = BuiltinTypes.Integer;
           break;
         case I2S: 
           conversionResult = BuiltinTypes.Short;
           expectedArgumentType = BuiltinTypes.Integer;
           break;
         default: 
           throw com.strobel.util.ContractUtils.unsupported();
         }
         
         ((Expression)arguments.get(0)).setExpectedType((TypeReference)expectedArgumentType);
         return conversionResult;
       case CheckCast: 
       case Unbox: 
         TypeReference castType;
         
         if (expectedType != null) {
           castType = (TypeReference)operand;
           
           inferredType = MetadataHelper.asSubType(castType, expectedType);
           
           if (forceInferChildren) {
             inferredType = inferTypeForExpression((Expression)arguments.get(0), inferredType != null ? inferredType : (TypeReference)operand);
           }
           
 
 
 
 
           if ((inferredType != null) && (MetadataHelper.isSubType(inferredType, MetadataHelper.eraseRecursive(castType)))) {
             expression.setOperand(inferredType);
             return inferredType;
           }
         }
         return (TypeReference)operand;
       
 
       case Box: 
         type = (TypeReference)operand;
         
         if (forceInferChildren) {
           inferTypeForExpression((Expression)arguments.get(0), (TypeReference)type);
         }
         
         return ((TypeReference)type).isPrimitive() ? BuiltinTypes.Object : (TypeReference)type;
       
 
       case CmpEq: 
       case CmpNe: 
       case CmpLt: 
       case CmpGe: 
       case CmpGt: 
       case CmpLe: 
         if (forceInferChildren) {
           return inferBinaryExpression(code, arguments, flags);
         }
         
         return BuiltinTypes.Boolean;
       
 
       case __DCmpG: 
       case __DCmpL: 
       case __FCmpG: 
       case __FCmpL: 
       case __LCmp: 
         if (forceInferChildren) {
           return inferBinaryExpression(code, arguments, flags);
         }
         
         return BuiltinTypes.Integer;
       
 
       case IfTrue: 
         if (forceInferChildren) {
           inferTypeForExpression((Expression)arguments.get(0), BuiltinTypes.Boolean, true);
         }
         return null;
       
 
       case Goto: 
       case Switch: 
       case AThrow: 
       case LoopOrSwitchBreak: 
       case LoopContinue: 
       case __Return: 
         return null;
       
 
       case __IReturn: 
       case __LReturn: 
       case __FReturn: 
       case __DReturn: 
       case __AReturn: 
       case Return: 
         Expression lambdaBinding = (Expression)expression.getUserData(AstKeys.PARENT_LAMBDA_BINDING);
         Object method;
         if (lambdaBinding != null) {
           Lambda lambda = (Lambda)lambdaBinding.getOperand();
           method = lambda.getMethod();
           
           if (method == null) {
             return null;
           }
           
           TypeReference oldInferredType = lambda.getInferredReturnType();
           
           TypeReference inferredType = expectedType;
           
           TypeReference returnType = oldInferredType != null ? oldInferredType : expectedType;
           
           TypeReference newInferredType;
           if (forceInferChildren) {
             if (returnType == null) {
               returnType = lambda.getMethod().getReturnType();
             }
             
             if (returnType.containsGenericParameters()) {
               Map<TypeReference, TypeReference> mappings = null;
               TypeReference declaringType = ((MethodReference)method).getDeclaringType();
               
               if (declaringType.isGenericType()) {
                 for (GenericParameter gp : declaringType.getGenericParameters()) {
                   GenericParameter inScope = this._context.getCurrentMethod().findTypeVariable(gp.getName());
                   
                   if ((inScope == null) || (!MetadataHelper.isSameType(gp, inScope)))
                   {
 
 
                     if (mappings == null) {
                       mappings = new HashMap();
                     }
                     
                     if (!mappings.containsKey(gp)) {
                       mappings.put(gp, MetadataHelper.eraseRecursive(gp));
                     }
                   }
                 }
                 if (mappings != null) {
                   declaringType = TypeSubstitutionVisitor.instance().visit(declaringType, mappings);
                   
                   if (declaringType != null) {
                     MethodReference boundMethod = MetadataHelper.asMemberOf((MethodReference)method, declaringType);
                     
 
 
 
                     if (boundMethod != null) {
                       returnType = boundMethod.getReturnType();
                     }
                   }
                 }
               }
             }
             
             if ((!arguments.isEmpty()) && (returnType != BuiltinTypes.Void)) {
               inferredType = inferTypeForExpression((Expression)arguments.get(0), returnType);
             }
             
             if ((oldInferredType != null) && (inferredType != BuiltinTypes.Void)) {
               newInferredType = MetadataHelper.asSuper(inferredType, oldInferredType);
               
 
 
 
               if (newInferredType != null) {
                 inferredType = newInferredType;
               }
             }
           }
           
           lambda.setExpectedReturnType(returnType);
           lambda.setInferredReturnType(inferredType);
           
           return inferredType;
         }
         
         returnType = this._context.getCurrentMethod().getReturnType();
         
         if ((forceInferChildren) && (arguments.size() == 1)) {
           inferTypeForExpression((Expression)arguments.get(0), returnType, true);
         }
         
         return returnType;
       
 
       case Bind: 
         lambda = (Lambda)expression.getOperand();
         
         if (lambda == null) {
           return null;
         }
         
         MethodReference method = ((Lambda)lambda).getMethod();
         parameters = ((Lambda)lambda).getParameters();
         
         TypeReference functionType = ((Lambda)lambda).getFunctionType();
         
         if ((functionType != null) && (expectedType != null)) {
           TypeReference asSubType = MetadataHelper.asSubType(functionType, expectedType);
           
           if (asSubType != null) {
             functionType = asSubType;
           }
         }
         
         MethodReference boundMethod = MetadataHelper.asMemberOf(method, functionType);
         
         if (boundMethod == null) {
           boundMethod = method;
         }
         
         List<ParameterDefinition> methodParameters = boundMethod.getParameters();
         
         int argumentCount = Math.min(arguments.size(), methodParameters.size());
         
         TypeReference inferredReturnType = null;
         
         if (forceInferChildren) {
           for (int i = 0; i < argumentCount; i++) {
             Expression argument = (Expression)arguments.get(i);
             
             inferTypeForExpression(argument, ((ParameterDefinition)methodParameters.get(i)).getParameterType());
           }
           
 
 
 
           List<Variable> lambdaParameters = ((Lambda)lambda).getParameters();
           
           int i = 0; for (int n = lambdaParameters.size(); i < n; i++) {
             invalidateDependentExpressions(expression, (Variable)lambdaParameters.get(i));
           }
           
           for (Expression e : ((Lambda)lambda).getChildrenAndSelfRecursive(Expression.class)) {
             if (PatternMatching.match(e, AstCode.Return)) {
               runInference(e);
               
               if (e.getInferredType() != null) {
                 if (inferredReturnType != null) {
                   inferredReturnType = MetadataHelper.asSuper(e.getInferredType(), inferredReturnType);
                 }
                 else {
                   inferredReturnType = e.getInferredType();
                 }
               }
             }
           }
         }
         
         MethodDefinition r = boundMethod.resolve();
         Map<TypeReference, TypeReference> mappings;
         if (((functionType.containsGenericParameters()) && (boundMethod.containsGenericParameters())) || ((r != null) && (r.getDeclaringType().containsGenericParameters()) && (r.containsGenericParameters())))
         {
 
 
           Map<TypeReference, TypeReference> oldMappings = new HashMap();
           Map<TypeReference, TypeReference> newMappings = new HashMap();
           
           List<ParameterDefinition> p = boundMethod.getParameters();
           List<ParameterDefinition> rp = r != null ? r.getParameters() : method.getParameters();
           
           TypeReference returnType = r != null ? r.getReturnType() : method.getReturnType();
           
 
           if (inferredReturnType != null) {
             if (returnType.isGenericParameter()) {
               TypeReference boundReturnType = ensureReferenceType(inferredReturnType);
               
               if (!MetadataHelper.isSameType(boundReturnType, returnType)) {
                 newMappings.put(returnType, boundReturnType);
               }
             }
             else if (returnType.containsGenericParameters()) {
               Map<TypeReference, TypeReference> returnMappings = new HashMap();
               
               new AddMappingsForArgumentVisitor(returnType).visit(inferredReturnType, returnMappings);
               
 
 
 
               newMappings.putAll(returnMappings);
             }
           }
           
           int i = 0; for (int j = Math.max(0, ((List)parameters).size() - arguments.size()); i < arguments.size(); j++) {
             Expression argument = (Expression)arguments.get(i);
             TypeReference rType = ((ParameterDefinition)rp.get(j)).getParameterType();
             TypeReference pType = ((ParameterDefinition)p.get(j)).getParameterType();
             TypeReference aType = argument.getInferredType();
             
             if ((pType != null) && (rType.containsGenericParameters())) {
               new AddMappingsForArgumentVisitor(pType).visit(rType, oldMappings);
             }
             
             if ((aType != null) && (rType.containsGenericParameters())) {
               new AddMappingsForArgumentVisitor(aType).visit(rType, newMappings);
             }
             i++;
           }
           
 
 
 
 
 
 
 
 
 
 
 
 
           mappings = oldMappings;
           
           if (!newMappings.isEmpty()) {
             for (TypeReference t : newMappings.keySet()) {
               TypeReference oldMapping = (TypeReference)oldMappings.get(t);
               TypeReference newMapping = (TypeReference)newMappings.get(t);
               
               if ((oldMapping == null) || (MetadataHelper.isSubType(newMapping, oldMapping))) {
                 mappings.put(t, newMapping);
               }
             }
           }
           
           if (!mappings.isEmpty()) {
             TypeReference declaringType = (r != null ? r : method).getDeclaringType();
             
             TypeReference boundDeclaringType = TypeSubstitutionVisitor.instance().visit(declaringType, mappings);
             
             if ((boundDeclaringType != null) && (boundDeclaringType.isGenericType())) {
               for (GenericParameter gp : boundDeclaringType.getGenericParameters()) {
                 GenericParameter inScope = this._context.getCurrentMethod().findTypeVariable(gp.getName());
                 
                 if ((inScope == null) || (!MetadataHelper.isSameType(gp, inScope)))
                 {
 
 
                   if (!mappings.containsKey(gp)) {
                     mappings.put(gp, MetadataHelper.eraseRecursive(gp));
                   }
                 }
               }
               boundDeclaringType = TypeSubstitutionVisitor.instance().visit(boundDeclaringType, mappings);
             }
             
             if (boundDeclaringType != null) {
               functionType = boundDeclaringType;
             }
             
             MethodReference newBoundMethod = MetadataHelper.asMemberOf(boundMethod, boundDeclaringType);
             
             if (newBoundMethod != null) {
               boundMethod = newBoundMethod;
               ((Lambda)lambda).setMethod(boundMethod);
               methodParameters = boundMethod.getParameters();
             }
           }
           
           for (int i = 0; i < methodParameters.size(); i++) {
             Variable variable = (Variable)((List)parameters).get(i);
             TypeReference variableType = ((ParameterDefinition)methodParameters.get(i)).getParameterType();
             TypeReference oldVariableType = variable.getType();
             
             if ((oldVariableType == null) || (!MetadataHelper.isSameType(variableType, oldVariableType))) {
               invalidateDependentExpressions(expression, variable);
             }
           }
         }
         
         return functionType;
       
 
       case Jsr: 
         return BuiltinTypes.Integer;
       
 
       case Ret: 
         if (forceInferChildren) {
           inferTypeForExpression((Expression)arguments.get(0), BuiltinTypes.Integer);
         }
         return null;
       
 
       case Pop: 
       case Pop2: 
         return null;
       
 
 
 
 
 
       case Dup: 
       case Dup2: 
         argument = (Expression)arguments.get(0);
         result = inferTypeForExpression((Expression)argument, expectedType);
         
         ((Expression)argument).setExpectedType(result);
         
         return result;
       
 
       case InstanceOf: 
         return BuiltinTypes.Boolean;
       
 
       case __IInc: 
       case __IIncW: 
       case Inc: 
         inferredType = inferTypeForVariable((Variable)operand, BuiltinTypes.Integer, flags | 0x1);
         
 
 
 
 
         if (forceInferChildren) {
           inferTypeForExpression((Expression)arguments.get(0), inferredType, true);
         }
         
         return inferredType;
       
 
       case Leave: 
       case EndFinally: 
       case Nop: 
         return null;
       
 
       case DefaultValue: 
         return (TypeReference)expression.getOperand();
       }
       
       
       System.err.printf("Type inference can't handle opcode '%s'.\n", new Object[] { code.getName() });
       return null;
 
     }
     finally
     {
       this._stack.pop();
     }
   }
   
 
 
 
 
 
 
   private TypeReference inferInitObject(Expression expression, TypeReference expectedType, boolean forceInferChildren, MethodReference operand, List<Expression> arguments)
   {
     MethodReference resolvedCtor = (operand instanceof IGenericInstance) ? operand.resolve() : operand;
     MethodReference constructor = resolvedCtor != null ? resolvedCtor : operand;
     TypeReference type = constructor.getDeclaringType();
     
     TypeReference inferredType;
     TypeReference inferredType;
     if ((expectedType != null) && (!MetadataHelper.isSameType(expectedType, BuiltinTypes.Object))) {
       TypeReference asSubType = MetadataHelper.asSubType(type, expectedType);
       inferredType = asSubType != null ? asSubType : type;
     }
     else {
       inferredType = type;
     }
     
     Map<TypeReference, TypeReference> mappings;
     Map<TypeReference, TypeReference> mappings;
     if (inferredType.isGenericDefinition()) {
       mappings = new HashMap();
       
       for (GenericParameter gp : inferredType.getGenericParameters()) {
         mappings.put(gp, MetadataHelper.eraseRecursive(gp));
       }
     }
     else {
       mappings = java.util.Collections.emptyMap();
     }
     
     if (forceInferChildren) {
       MethodReference asMember = MetadataHelper.asMemberOf(constructor, TypeSubstitutionVisitor.instance().visit(inferredType, mappings));
       
 
 
 
       List<ParameterDefinition> parameters = asMember.getParameters();
       
       for (int i = 0; (i < arguments.size()) && (i < parameters.size()); i++) {
         inferTypeForExpression((Expression)arguments.get(i), ((ParameterDefinition)parameters.get(i)).getParameterType());
       }
       
 
 
 
       expression.setOperand(asMember);
     }
     
     if (inferredType == null) {
       return type;
     }
     
     List<TypeReference> oldTypeArguments = (List)expression.getUserData(AstKeys.TYPE_ARGUMENTS);
     
     if ((inferredType instanceof IGenericInstance)) {
       boolean typeArgumentsChanged = false;
       List<TypeReference> typeArguments = ((IGenericInstance)inferredType).getTypeArguments();
       
       for (int i = 0; i < typeArguments.size(); i++) {
         TypeReference t = (TypeReference)typeArguments.get(i);
         
         while (t.isWildcardType()) {
           t = t.hasExtendsBound() ? t.getExtendsBound() : MetadataHelper.getUpperBound(t);
           
           if (!typeArgumentsChanged) {
             typeArguments = com.strobel.core.CollectionUtilities.toList(typeArguments);
             typeArgumentsChanged = true;
           }
           
           typeArguments.set(i, t);
         }
         
         while (t.isGenericParameter()) {
           GenericParameter inScope = this._context.getCurrentMethod().findTypeVariable(t.getName());
           
           if ((inScope != null) && (MetadataHelper.isSameType(t, inScope))) {
             break;
           }
           
           if ((oldTypeArguments != null) && (oldTypeArguments.size() == typeArguments.size()))
           {
 
             TypeReference o = (TypeReference)oldTypeArguments.get(i);
             
             if (!MetadataHelper.isSameType(o, t)) {
               t = o;
               
               if (!typeArgumentsChanged) {
                 typeArguments = com.strobel.core.CollectionUtilities.toList(typeArguments);
                 typeArgumentsChanged = true;
               }
               
               typeArguments.set(i, t);
               continue;
             }
           }
           
           t = t.hasExtendsBound() ? t.getExtendsBound() : MetadataHelper.getUpperBound(t);
           
           if (!typeArgumentsChanged) {
             typeArguments = com.strobel.core.CollectionUtilities.toList(typeArguments);
             typeArgumentsChanged = true;
           }
           
           typeArguments.set(i, t);
         }
       }
       
       expression.putUserData(AstKeys.TYPE_ARGUMENTS, typeArguments);
       
       if (typeArgumentsChanged) {
         inferredType = inferredType.makeGenericType(typeArguments);
       }
     }
     
     return inferredType;
   }
   
   private TypeReference cleanTypeArguments(TypeReference newType, TypeReference alternateType)
   {
     if (!(alternateType instanceof IGenericInstance)) {
       return newType;
     }
     
     if (!com.strobel.core.StringUtilities.equals(newType.getInternalName(), alternateType.getInternalName())) {
       return newType;
     }
     
     List<TypeReference> alternateTypeArguments = ((IGenericInstance)alternateType).getTypeArguments();
     
     boolean typeArgumentsChanged = false;
     List<TypeReference> typeArguments;
     List<TypeReference> typeArguments;
     if ((newType instanceof IGenericInstance)) {
       typeArguments = ((IGenericInstance)newType).getTypeArguments();
     }
     else {
       typeArguments = new java.util.ArrayList();
       typeArguments.addAll(newType.getGenericParameters());
     }
     
     for (int i = 0; i < typeArguments.size(); i++) {
       TypeReference t = (TypeReference)typeArguments.get(i);
       
       while (t.isGenericParameter()) {
         GenericParameter inScope = this._context.getCurrentMethod().findTypeVariable(t.getName());
         
         if ((inScope != null) && (MetadataHelper.isSameType(t, inScope))) {
           break;
         }
         
         if ((alternateTypeArguments != null) && (alternateTypeArguments.size() == typeArguments.size()))
         {
 
           TypeReference o = (TypeReference)alternateTypeArguments.get(i);
           
           if (!MetadataHelper.isSameType(o, t)) {
             t = o;
             
             if (!typeArgumentsChanged) {
               typeArguments = com.strobel.core.CollectionUtilities.toList(typeArguments);
               typeArgumentsChanged = true;
             }
             
             typeArguments.set(i, t);
             continue;
           }
         }
         
         t = t.hasExtendsBound() ? t.getExtendsBound() : MetadataHelper.getUpperBound(t);
         
         if (!typeArgumentsChanged) {
           typeArguments = com.strobel.core.CollectionUtilities.toList(typeArguments);
           typeArgumentsChanged = true;
         }
         
         typeArguments.set(i, t);
       }
     }
     
     if (typeArgumentsChanged) {
       return newType.makeGenericType(typeArguments);
     }
     
     return newType;
   }
   
   private TypeReference inferBinaryExpression(AstCode code, List<Expression> arguments, int flags) {
     Expression left = (Expression)arguments.get(0);
     Expression right = (Expression)arguments.get(1);
     
     runInference(left);
     runInference(right);
     
     left.setExpectedType(left.getInferredType());
     right.setExpectedType(left.getInferredType());
     left.setInferredType(null);
     right.setInferredType(null);
     
     int operandFlags = 0;
     
     switch (code) {
     case Or: 
     case And: 
     case Xor: 
     case CmpEq: 
     case CmpNe: 
       if (left.getExpectedType() == BuiltinTypes.Boolean) {
         if (right.getExpectedType() == BuiltinTypes.Integer) {
           if (PatternMatching.matchBooleanConstant(right) != null) {
             right.setExpectedType(BuiltinTypes.Boolean);
           }
           else {
             left.setExpectedType(BuiltinTypes.Integer);
             operandFlags |= 0x1;
           }
         }
         else if (right.getExpectedType() != BuiltinTypes.Boolean) {
           left.setExpectedType(BuiltinTypes.Integer);
           operandFlags |= 0x1;
         }
       }
       else if (right.getExpectedType() == BuiltinTypes.Boolean) {
         if (left.getExpectedType() == BuiltinTypes.Integer) {
           if (PatternMatching.matchBooleanConstant(left) != null) {
             left.setExpectedType(BuiltinTypes.Boolean);
           }
           else {
             right.setExpectedType(BuiltinTypes.Integer);
             operandFlags |= 0x1;
           }
         }
         else if (left.getExpectedType() != BuiltinTypes.Boolean) {
           right.setExpectedType(BuiltinTypes.Integer);
           operandFlags |= 0x1;
         }
       }
       
 
 
       break;
     default: 
       operandFlags |= 0x1;
       
       if ((left.getExpectedType() == BuiltinTypes.Boolean) || ((left.getExpectedType() == null) && (PatternMatching.matchBooleanConstant(left) != null)))
       {
 
         left.setExpectedType(BuiltinTypes.Integer);
       }
       
       if ((right.getExpectedType() == BuiltinTypes.Boolean) || ((right.getExpectedType() == null) && (PatternMatching.matchBooleanConstant(right) != null)))
       {
 
         right.setExpectedType(BuiltinTypes.Integer);
       }
       
       break;
     }
     
     
     if (left.getExpectedType() == BuiltinTypes.Character) {
       if ((right.getExpectedType() == BuiltinTypes.Integer) && (PatternMatching.matchCharacterConstant(right) != null)) {
         right.setExpectedType(BuiltinTypes.Character);
       }
     }
     else if ((right.getExpectedType() == BuiltinTypes.Character) && 
       (left.getExpectedType() == BuiltinTypes.Integer) && (PatternMatching.matchCharacterConstant(left) != null)) {
       left.setExpectedType(BuiltinTypes.Character);
     }
     
 
     TypeReference operandType = inferBinaryArguments(left, right, typeWithMoreInformation(doInferTypeForExpression(left, left.getExpectedType(), true, operandFlags), doInferTypeForExpression(right, right.getExpectedType(), true, operandFlags)), false, null, null, operandFlags);
     
 
 
 
 
 
 
 
 
 
 
 
     switch (code) {
     case CmpEq: 
     case CmpNe: 
     case CmpLt: 
     case CmpGe: 
     case CmpGt: 
     case CmpLe: 
       return BuiltinTypes.Boolean;
     }
     
     return adjustType(operandType, flags);
   }
   
 
   private TypeReference inferDynamicCall(Expression expression, TypeReference expectedType, boolean forceInferChildren)
   {
     List<Expression> arguments = expression.getArguments();
     com.strobel.assembler.metadata.DynamicCallSite callSite = (com.strobel.assembler.metadata.DynamicCallSite)expression.getOperand();
     
     TypeReference inferredType = expression.getInferredType();
     
     if (inferredType == null) {
       inferredType = callSite.getMethodType().getReturnType();
     }
     
     TypeReference result = expectedType == null ? inferredType : MetadataHelper.asSubType(inferredType, expectedType);
     
 
     if (result == null) {
       result = inferredType;
     }
     
     if ((result.isGenericType()) || (MetadataHelper.isRawType(result))) {
       MethodReference bootstrapMethod = callSite.getBootstrapMethod();
       
       if (("java/lang/invoke/LambdaMetafactory".equals(bootstrapMethod.getDeclaringType().getInternalName())) && (com.strobel.core.StringUtilities.equals("metafactory", bootstrapMethod.getName(), com.strobel.core.StringComparison.OrdinalIgnoreCase)) && (callSite.getBootstrapArguments().size() == 3) && ((callSite.getBootstrapArguments().get(1) instanceof com.strobel.assembler.metadata.MethodHandle)))
       {
 
 
 
         com.strobel.assembler.metadata.MethodHandle targetHandle = (com.strobel.assembler.metadata.MethodHandle)callSite.getBootstrapArguments().get(1);
         MethodReference targetMethod = targetHandle.getMethod();
         Map<TypeReference, TypeReference> expectedMappings = new HashMap();
         Map<TypeReference, TypeReference> inferredMappings = new HashMap();
         
         MethodReference functionMethod = null;
         
         com.strobel.assembler.metadata.TypeDefinition resolvedType = result.resolve();
         
         List<MethodReference> methods = MetadataHelper.findMethods(resolvedType != null ? resolvedType : result, com.strobel.assembler.metadata.MetadataFilters.matchName(callSite.getMethodName()));
         
 
 
 
         for (MethodReference m : methods) {
           MethodDefinition r = m.resolve();
           
           if ((r != null) && (r.isAbstract()) && (!r.isStatic()) && (!r.isDefault())) {
             functionMethod = r;
             break;
           }
         }
         
         if (functionMethod == null) {
           return null;
         }
         
         boolean firstArgIsTarget = false;
         MethodReference actualMethod = targetMethod;
         
         switch (targetHandle.getHandleType()) {
         case GetField: 
         case PutField: 
         case InvokeVirtual: 
         case InvokeSpecial: 
         case InvokeInterface: 
           if (arguments.size() > 0) {
             Expression arg = (Expression)arguments.get(0);
             TypeReference expectedArgType = targetMethod.getDeclaringType();
             
             if (forceInferChildren) {
               inferTypeForExpression(arg, expectedArgType, true);
             }
             
             TypeReference targetType = arg.getInferredType();
             
             if ((targetType != null) && (MetadataHelper.isSubType(targetType, expectedArgType)))
             {
 
               firstArgIsTarget = true;
               
               MethodReference asMember = MetadataHelper.asMemberOf(actualMethod, targetType);
               
               if (asMember != null) {
                 actualMethod = asMember;
               }
             }
           }
           break;
         }
         
         if ((expectedType != null) && (expectedType.isGenericType()) && (!expectedType.isGenericDefinition())) {
           List<GenericParameter> genericParameters;
           List<GenericParameter> genericParameters;
           if (resolvedType != null) {
             genericParameters = resolvedType.getGenericParameters();
           }
           else {
             genericParameters = expectedType.getGenericParameters();
           }
           
           List<TypeReference> typeArguments = ((IGenericInstance)expectedType).getTypeArguments();
           
           if (typeArguments.size() == genericParameters.size()) {
             for (int i = 0; i < genericParameters.size(); i++) {
               TypeReference typeArgument = (TypeReference)typeArguments.get(i);
               GenericParameter genericParameter = (GenericParameter)genericParameters.get(i);
               
               if (!MetadataHelper.isSameType(typeArgument, genericParameter, true)) {
                 expectedMappings.put(genericParameter, typeArgument);
               }
             }
           }
         }
         
         new AddMappingsForArgumentVisitor(actualMethod.isConstructor() ? actualMethod.getDeclaringType() : actualMethod.getReturnType()).visit(functionMethod.getReturnType(), inferredMappings);
         
 
 
 
         List<ParameterDefinition> tp = actualMethod.getParameters();
         List<ParameterDefinition> fp = functionMethod.getParameters();
         
         if (tp.size() == fp.size()) {
           for (int i = 0; i < fp.size(); i++) {
             new AddMappingsForArgumentVisitor(((ParameterDefinition)tp.get(i)).getParameterType()).visit(((ParameterDefinition)fp.get(i)).getParameterType(), inferredMappings);
           }
         }
         
         for (TypeReference key : expectedMappings.keySet()) {
           TypeReference expectedMapping = (TypeReference)expectedMappings.get(key);
           TypeReference inferredMapping = (TypeReference)inferredMappings.get(key);
           
           if ((inferredMapping == null) || (MetadataHelper.isSubType(expectedMapping, inferredMapping))) {
             inferredMappings.put(key, expectedMapping);
           }
         }
         
         result = TypeSubstitutionVisitor.instance().visit(resolvedType != null ? resolvedType : result, inferredMappings);
         
 
 
 
         if ((!firstArgIsTarget) || (expectedType == null)) {
           return result;
         }
         
 
 
 
 
 
 
         TypeReference declaringType = actualMethod.getDeclaringType();
         
         if ((!declaringType.isGenericDefinition()) && (!MetadataHelper.isRawType(actualMethod.getDeclaringType()))) {
           return result;
         }
         
         declaringType = declaringType.isGenericDefinition() ? declaringType : declaringType.resolve();
         
         if (declaringType == null) {
           return result;
         }
         
         MethodReference resultMethod = MetadataHelper.asMemberOf(functionMethod, result);
         
         actualMethod = actualMethod.resolve();
         
         if ((resultMethod == null) || (actualMethod == null)) {
           return result;
         }
         
         inferredMappings.clear();
         
         new AddMappingsForArgumentVisitor(resultMethod.getReturnType()).visit(actualMethod.getReturnType(), inferredMappings);
         
         List<ParameterDefinition> ap = actualMethod.getParameters();
         List<ParameterDefinition> rp = resultMethod.getParameters();
         
         if (ap.size() == rp.size()) {
           int i = 0; for (int n = ap.size(); i < n; i++) {
             new AddMappingsForArgumentVisitor(((ParameterDefinition)rp.get(i)).getParameterType()).visit(((ParameterDefinition)ap.get(i)).getParameterType(), inferredMappings);
           }
         }
         
         TypeReference resolvedTargetType = TypeSubstitutionVisitor.instance().visit(declaringType, inferredMappings);
         
         if (resolvedTargetType != null) {
           inferTypeForExpression((Expression)arguments.get(0), resolvedTargetType, true);
         }
       }
     }
     
 
 
 
 
     return result;
   }
   
   private TypeReference inferCall(Expression expression, TypeReference expectedType, boolean forceInferChildren)
   {
     AstCode code = expression.getCode();
     List<Expression> arguments = expression.getArguments();
     MethodReference method = (MethodReference)expression.getOperand();
     List<ParameterDefinition> parameters = method.getParameters();
     boolean hasThis = (code != AstCode.InvokeStatic) && (code != AstCode.InvokeDynamic);
     
     TypeReference targetType = null;
     MethodReference boundMethod = method;
     
     if (forceInferChildren) {
       MethodDefinition r = method.resolve();
       
       MethodReference actualMethod;
       MethodReference actualMethod;
       if (hasThis) {
         Expression thisArg = (Expression)arguments.get(0);
         
         TypeReference expectedTargetType = thisArg.getInferredType() != null ? thisArg.getInferredType() : thisArg.getExpectedType();
         
 
         if ((expectedTargetType != null) && (expectedTargetType.isGenericType()) && (!expectedTargetType.isGenericDefinition()))
         {
 
 
           boundMethod = MetadataHelper.asMemberOf(method, expectedTargetType);
           
           targetType = inferTypeForExpression((Expression)arguments.get(0), expectedTargetType);
 
 
 
         }
         else if (method.isConstructor()) {
           targetType = method.getDeclaringType();
         }
         else {
           targetType = inferTypeForExpression((Expression)arguments.get(0), method.getDeclaringType());
         }
         
 
 
 
         if ((!(targetType instanceof com.strobel.assembler.metadata.RawType)) && (MetadataHelper.isRawType(targetType)) && (!MetadataHelper.canReferenceTypeVariablesOf(targetType, this._context.getCurrentType())))
         {
 
 
           targetType = MetadataHelper.erase(targetType);
         }
         
         MethodReference m = targetType != null ? MetadataHelper.asMemberOf(r != null ? r : method, targetType) : method;
         
         MethodReference actualMethod;
         if (m != null) {
           actualMethod = m;
         }
         else {
           actualMethod = r != null ? r : boundMethod;
         }
       }
       else {
         actualMethod = r != null ? r : boundMethod;
       }
       
       boundMethod = actualMethod;
       expression.setOperand(boundMethod);
       
       List<ParameterDefinition> p = method.getParameters();
       
       Map<TypeReference, TypeReference> mappings = null;
       
       if ((actualMethod.containsGenericParameters()) || ((r != null) && (r.containsGenericParameters()))) {
         Map<TypeReference, TypeReference> oldMappings = new HashMap();
         Map<TypeReference, TypeReference> newMappings = new HashMap();
         Map<TypeReference, TypeReference> inferredMappings = new HashMap();
         
         if ((targetType != null) && (targetType.isGenericType())) {
           oldMappings.putAll(MetadataHelper.getGenericSubTypeMappings(targetType.getUnderlyingType(), targetType));
         }
         
         List<ParameterDefinition> rp = r != null ? r.getParameters() : actualMethod.getParameters();
         List<ParameterDefinition> cp = boundMethod.getParameters();
         
         boolean mapOld = method instanceof IGenericInstance;
         
         for (int i = 0; i < parameters.size(); i++) {
           TypeReference rType = ((ParameterDefinition)rp.get(i)).getParameterType();
           TypeReference pType = ((ParameterDefinition)p.get(i)).getParameterType();
           TypeReference cType = ((ParameterDefinition)cp.get(i)).getParameterType();
           TypeReference aType = inferTypeForExpression((Expression)arguments.get(hasThis ? i + 1 : i), cType);
           
           if ((mapOld) && (rType != null) && (rType.containsGenericParameters())) {
             new AddMappingsForArgumentVisitor(pType).visit(rType, oldMappings);
           }
           
           if ((cType != null) && (rType.containsGenericParameters())) {
             new AddMappingsForArgumentVisitor(cType).visit(rType, newMappings);
           }
           
           if ((aType != null) && (rType.containsGenericParameters())) {
             new AddMappingsForArgumentVisitor(aType).visit(rType, inferredMappings);
           }
         }
         
         if (expectedType != null) {
           TypeReference returnType = r != null ? r.getReturnType() : actualMethod.getReturnType();
           
 
           if (returnType.containsGenericParameters()) {
             Map<TypeReference, TypeReference> returnMappings = new HashMap();
             
             new AddMappingsForArgumentVisitor(expectedType).visit(returnType, returnMappings);
             
             newMappings.putAll(returnMappings);
           }
         }
         
         if ((!oldMappings.isEmpty()) || (!newMappings.isEmpty()) || (!inferredMappings.isEmpty())) {
           mappings = oldMappings;
           
           for (TypeReference t : newMappings.keySet()) {
             TypeReference oldMapping = (TypeReference)mappings.get(t);
             TypeReference newMapping = (TypeReference)newMappings.get(t);
             
             if ((oldMapping == null) || (MetadataHelper.isSubType(newMapping, oldMapping))) {
               mappings.put(t, newMapping);
             }
           }
           
           for (TypeReference t : inferredMappings.keySet()) {
             TypeReference oldMapping = (TypeReference)mappings.get(t);
             TypeReference newMapping = (TypeReference)inferredMappings.get(t);
             
             if ((oldMapping == null) || (MetadataHelper.isSubType(newMapping, oldMapping))) {
               mappings.put(t, newMapping);
             }
           }
         }
         
         if (mappings != null) {
           boundMethod = TypeSubstitutionVisitor.instance().visitMethod(r != null ? r : actualMethod, mappings);
           actualMethod = boundMethod;
           expression.setOperand(boundMethod);
           p = boundMethod.getParameters();
         }
         
         TypeReference boundDeclaringType = boundMethod.getDeclaringType();
         
         if (boundDeclaringType.isGenericType()) {
           if (mappings == null) {
             mappings = new HashMap();
           }
           
           for (GenericParameter gp : boundDeclaringType.getGenericParameters()) {
             GenericParameter inScope = this._context.getCurrentMethod().findTypeVariable(gp.getName());
             
             if ((inScope == null) || (!MetadataHelper.isSameType(gp, inScope)))
             {
 
 
               if (!mappings.containsKey(gp)) {
                 mappings.put(gp, MetadataHelper.eraseRecursive(gp));
               }
             }
           }
           boundMethod = TypeSubstitutionVisitor.instance().visitMethod(actualMethod, mappings);
           expression.setOperand(boundMethod);
           p = boundMethod.getParameters();
         }
         
         if (boundMethod.isGenericMethod()) {
           if (mappings == null) {
             mappings = new HashMap();
           }
           
           for (GenericParameter gp : boundMethod.getGenericParameters()) {
             if (!mappings.containsKey(gp)) {
               mappings.put(gp, MetadataHelper.eraseRecursive(gp));
             }
           }
           
           boundMethod = TypeSubstitutionVisitor.instance().visitMethod(actualMethod, mappings);
           expression.setOperand(boundMethod);
           p = boundMethod.getParameters();
         }
         
         if ((r != null) && (method.isGenericMethod())) {
           HashMap<TypeReference, TypeReference> tempMappings = new HashMap();
           List<ParameterDefinition> bp = method.getParameters();
           
           int i = 0; for (int n = bp.size(); i < n; i++) {
             new AddMappingsForArgumentVisitor(((ParameterDefinition)bp.get(i)).getParameterType()).visit(((ParameterDefinition)rp.get(i)).getParameterType(), tempMappings);
           }
           
 
 
 
           boolean changed = false;
           
           if (mappings == null) {
             mappings = tempMappings;
             changed = true;
           }
           else {
             for (TypeReference key : tempMappings.keySet()) {
               if (!mappings.containsKey(key)) {
                 mappings.put(key, tempMappings.get(key));
                 changed = true;
               }
             }
           }
           
           if (changed) {
             boundMethod = TypeSubstitutionVisitor.instance().visitMethod(actualMethod, mappings);
             expression.setOperand(boundMethod);
             p = boundMethod.getParameters();
           }
         }
       }
       else {
         boundMethod = actualMethod;
       }
       
       if ((hasThis) && (mappings != null)) {
         TypeReference expectedTargetType;
         TypeReference expectedTargetType;
         if (boundMethod.isConstructor()) {
           expectedTargetType = MetadataHelper.substituteGenericArguments(boundMethod.getDeclaringType(), mappings);
         }
         else {
           expectedTargetType = boundMethod.getDeclaringType();
         }
         
         if ((expectedTargetType != null) && (expectedTargetType.isGenericDefinition()) && (((Expression)arguments.get(0)).getInferredType() != null))
         {
 
 
           expectedTargetType = MetadataHelper.asSuper(expectedTargetType, ((Expression)arguments.get(0)).getInferredType());
         }
         
 
 
 
         TypeReference inferredTargetType = inferTypeForExpression((Expression)arguments.get(0), expectedTargetType, forceInferChildren);
         
 
 
 
 
         if (inferredTargetType != null) {
           targetType = MetadataHelper.substituteGenericArguments(inferredTargetType, mappings);
           
           if ((MetadataHelper.isRawType(targetType)) && (!MetadataHelper.canReferenceTypeVariablesOf(targetType, this._context.getCurrentType())))
           {
 
             targetType = MetadataHelper.erase(targetType);
           }
           
           boundMethod = MetadataHelper.asMemberOf(boundMethod, targetType);
           p = boundMethod.getParameters();
           expression.setOperand(boundMethod);
         }
       }
       
       for (int i = 0; i < parameters.size(); i++) {
         TypeReference pType = ((ParameterDefinition)p.get(i)).getParameterType();
         
         Expression argument = (Expression)arguments.get(hasThis ? i + 1 : i);
         
         inferTypeForExpression(argument, pType, forceInferChildren, (PatternMatching.match(argument, AstCode.Load)) && (pType != BuiltinTypes.Boolean) ? 1 : 0);
       }
     }
     
 
 
 
 
 
     if ((hasThis) && 
       (boundMethod.isConstructor())) {
       return boundMethod.getDeclaringType();
     }
     
 
     return boundMethod.getReturnType();
   }
   
   private TypeReference inferTypeForVariable(Variable v, TypeReference expectedType) {
     return inferTypeForVariable(v, expectedType, false, 0);
   }
   
   private TypeReference inferTypeForVariable(Variable v, TypeReference expectedType, int flags) {
     return inferTypeForVariable(v, expectedType, false, flags);
   }
   
 
 
 
 
   private TypeReference inferTypeForVariable(Variable v, TypeReference expectedType, boolean favorExpectedOverActual, int flags)
   {
     TypeReference lastInferredType = (TypeReference)this._inferredVariableTypes.get(v);
     
     if (lastInferredType != null) {
       return adjustType(lastInferredType, flags);
     }
     
     if (isSingleStoreBoolean(v)) {
       return adjustType(BuiltinTypes.Boolean, flags);
     }
     
     if ((favorExpectedOverActual) && (expectedType != null)) {
       return adjustType(expectedType, flags);
     }
     
     TypeReference variableType = v.getType();
     
     if (variableType != null) {
       return adjustType(variableType, flags);
     }
     
     if (v.isGenerated()) {
       return adjustType(expectedType, flags);
     }
     
     return adjustType(v.isParameter() ? v.getOriginalParameter().getParameterType() : v.getOriginalVariable().getVariableType(), flags);
   }
   
 
 
 
   private static TypeReference adjustType(TypeReference type, int flags)
   {
     if ((com.strobel.assembler.metadata.Flags.testAny(flags, 1)) && (type == BuiltinTypes.Boolean)) {
       return BuiltinTypes.Integer;
     }
     return type;
   }
   
   private TypeReference numericPromotion(TypeReference type) {
     if (type == null) {
       return null;
     }
     
     switch (type.getSimpleType()) {
     case Byte: 
     case Short: 
       return BuiltinTypes.Integer;
     }
     
     return type;
   }
   
 
 
 
 
 
 
 
 
   private TypeReference inferBinaryArguments(Expression left, Expression right, TypeReference expectedType, boolean forceInferChildren, TypeReference leftPreferred, TypeReference rightPreferred, int operandFlags)
   {
     TypeReference actualLeftPreferred = leftPreferred;
     TypeReference actualRightPreferred = rightPreferred;
     
     if (actualLeftPreferred == null) {
       actualLeftPreferred = doInferTypeForExpression(left, expectedType, forceInferChildren, operandFlags);
     }
     
     if (actualRightPreferred == null) {
       actualRightPreferred = doInferTypeForExpression(right, expectedType, forceInferChildren, operandFlags);
     }
     
     if (actualLeftPreferred == BuiltinTypes.Null) {
       if ((actualRightPreferred != null) && (!actualRightPreferred.isPrimitive())) {
         actualLeftPreferred = actualRightPreferred;
       }
     }
     else if ((actualRightPreferred == BuiltinTypes.Null) && 
       (actualLeftPreferred != null) && (!actualLeftPreferred.isPrimitive())) {
       actualRightPreferred = actualLeftPreferred;
     }
     
 
     if (actualLeftPreferred == BuiltinTypes.Character) {
       if ((actualRightPreferred == BuiltinTypes.Integer) && (PatternMatching.matchCharacterConstant(right) != null)) {
         actualRightPreferred = BuiltinTypes.Character;
       }
     }
     else if ((actualRightPreferred == BuiltinTypes.Character) && 
       (actualLeftPreferred == BuiltinTypes.Integer) && (PatternMatching.matchCharacterConstant(left) != null)) {
       actualLeftPreferred = BuiltinTypes.Character;
     }
     
 
     if (isSameType(actualLeftPreferred, actualRightPreferred)) {
       left.setInferredType(actualLeftPreferred);
       left.setExpectedType(actualLeftPreferred);
       right.setInferredType(actualLeftPreferred);
       right.setExpectedType(actualLeftPreferred);
       
       return actualLeftPreferred;
     }
     
     if (isSameType(actualRightPreferred, doInferTypeForExpression(left, actualRightPreferred, forceInferChildren, operandFlags))) {
       left.setInferredType(actualRightPreferred);
       left.setExpectedType(actualRightPreferred);
       right.setInferredType(actualRightPreferred);
       right.setExpectedType(actualRightPreferred);
       
       return actualRightPreferred;
     }
     
     if (isSameType(actualLeftPreferred, doInferTypeForExpression(right, actualLeftPreferred, forceInferChildren, operandFlags))) {
       left.setInferredType(actualLeftPreferred);
       left.setExpectedType(actualLeftPreferred);
       right.setInferredType(actualLeftPreferred);
       right.setExpectedType(actualLeftPreferred);
       
       return actualLeftPreferred;
     }
     
     TypeReference result = typeWithMoreInformation(actualLeftPreferred, actualRightPreferred);
     
     left.setExpectedType(result);
     right.setExpectedType(result);
     left.setInferredType(doInferTypeForExpression(left, result, forceInferChildren, operandFlags));
     right.setInferredType(doInferTypeForExpression(right, result, forceInferChildren, operandFlags));
     
     return result;
   }
   
   private TypeReference typeWithMoreInformation(TypeReference leftPreferred, TypeReference rightPreferred) {
     int left = getInformationAmount(leftPreferred);
     int right = getInformationAmount(rightPreferred);
     
     if (left < right) {
       return rightPreferred;
     }
     
     if (left > right) {
       return leftPreferred;
     }
     
     if ((leftPreferred != null) && (rightPreferred != null)) {
       return MetadataHelper.findCommonSuperType(leftPreferred.isGenericDefinition() ? new com.strobel.assembler.metadata.RawType(leftPreferred) : leftPreferred, rightPreferred.isGenericDefinition() ? new com.strobel.assembler.metadata.RawType(rightPreferred) : rightPreferred);
     }
     
 
 
 
 
 
     return leftPreferred;
   }
   
   private static int getInformationAmount(TypeReference type) {
     if ((type == null) || (type == BuiltinTypes.Null)) {
       return 0;
     }
     
     switch (type.getSimpleType()) {
     case Boolean: 
       return 1;
     
     case Byte: 
       return 8;
     
     case Character: 
     case Short: 
       return 16;
     
     case Integer: 
     case Float: 
       return 32;
     
     case Long: 
     case Double: 
       return 64;
     }
     
     return 100;
   }
   
   static TypeReference getFieldType(FieldReference field)
   {
     com.strobel.assembler.metadata.FieldDefinition resolvedField = field.resolve();
     
     if (resolvedField != null) {
       FieldReference asMember = MetadataHelper.asMemberOf(resolvedField, field.getDeclaringType());
       
       return asMember.getFieldType();
     }
     
     return substituteTypeArguments(field.getFieldType(), field);
   }
   
   static TypeReference substituteTypeArguments(TypeReference type, com.strobel.assembler.metadata.MemberReference member) {
     if ((type instanceof ArrayType)) {
       ArrayType arrayType = (ArrayType)type;
       
       TypeReference elementType = substituteTypeArguments(arrayType.getElementType(), member);
       
 
 
 
       if (!com.strobel.assembler.metadata.MetadataResolver.areEquivalent(elementType, arrayType.getElementType())) {
         return elementType.makeArrayType();
       }
       
       return type;
     }
     
     if ((type instanceof IGenericInstance)) {
       IGenericInstance genericInstance = (IGenericInstance)type;
       List<TypeReference> newTypeArguments = new java.util.ArrayList();
       
       boolean isChanged = false;
       
       for (TypeReference typeArgument : genericInstance.getTypeArguments()) {
         TypeReference newTypeArgument = substituteTypeArguments(typeArgument, member);
         
         newTypeArguments.add(newTypeArgument);
         isChanged |= newTypeArgument != typeArgument;
       }
       
       return isChanged ? type.makeGenericType(newTypeArguments) : type;
     }
     
 
     if ((type instanceof GenericParameter)) {
       GenericParameter genericParameter = (GenericParameter)type;
       com.strobel.assembler.metadata.IGenericParameterProvider owner = genericParameter.getOwner();
       
       if ((member.getDeclaringType() instanceof ArrayType)) {
         return member.getDeclaringType().getElementType();
       }
       if (((owner instanceof MethodReference)) && ((member instanceof MethodReference))) {
         MethodReference method = (MethodReference)member;
         MethodReference ownerMethod = (MethodReference)owner;
         
         if ((method.isGenericMethod()) && (com.strobel.assembler.metadata.MetadataResolver.areEquivalent(ownerMethod.getDeclaringType(), method.getDeclaringType())) && (com.strobel.core.StringUtilities.equals(ownerMethod.getName(), method.getName())) && (com.strobel.core.StringUtilities.equals(ownerMethod.getErasedSignature(), method.getErasedSignature())))
         {
 
 
 
           if ((method instanceof IGenericInstance)) {
             List<TypeReference> typeArguments = ((IGenericInstance)member).getTypeArguments();
             return (TypeReference)typeArguments.get(genericParameter.getPosition());
           }
           
           return (TypeReference)method.getGenericParameters().get(genericParameter.getPosition());
         }
         
       }
       else if ((owner instanceof TypeReference)) {
         TypeReference declaringType;
         TypeReference declaringType;
         if ((member instanceof TypeReference)) {
           declaringType = (TypeReference)member;
         }
         else {
           declaringType = member.getDeclaringType();
         }
         
         if (com.strobel.assembler.metadata.MetadataResolver.areEquivalent((TypeReference)owner, declaringType)) {
           if ((declaringType instanceof IGenericInstance)) {
             List<TypeReference> typeArguments = ((IGenericInstance)declaringType).getTypeArguments();
             return (TypeReference)typeArguments.get(genericParameter.getPosition());
           }
           
           if (!declaringType.isGenericDefinition()) {
             declaringType = declaringType.getUnderlyingType();
           }
           
           if ((declaringType != null) && (declaringType.isGenericDefinition())) {
             return (TypeReference)declaringType.getGenericParameters().get(genericParameter.getPosition());
           }
         }
       }
     }
     
     return type;
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   private boolean isSameType(TypeReference t1, TypeReference t2)
   {
     return MetadataHelper.isSameType(t1, t2, true);
   }
   
   private boolean anyDone(List<ExpressionToInfer> expressions) {
     for (ExpressionToInfer expression : expressions) {
       if (expression.done) {
         return true;
       }
     }
     return false;
   }
   
   private boolean allDone(List<ExpressionToInfer> expressions) {
     for (ExpressionToInfer expression : expressions) {
       if (!expression.done) {
         return false;
       }
     }
     return true;
   }
   
   public static <T> boolean trueForAll(Iterable<T> sequence, com.strobel.core.Predicate<T> condition) {
     for (T item : sequence) {
       if (!condition.test(item)) {
         return false;
       }
     }
     return true;
   }
   
   public static boolean isBoolean(TypeReference type) {
     return (type != null) && (type.getSimpleType() == com.strobel.assembler.metadata.JvmType.Boolean);
   }
   
 
   static final class ExpressionToInfer
   {
     private final List<Variable> dependencies = new java.util.ArrayList();
     
     Expression expression;
     boolean done;
     Variable dependsOnSingleLoad;
     int flags;
     
     public String toString()
     {
       if (this.done) {
         return "[Done] " + this.expression;
       }
       return this.expression.toString();
     }
   }
   
   private static final class AddMappingsForArgumentVisitor extends com.strobel.assembler.metadata.DefaultTypeVisitor<Map<TypeReference, TypeReference>, Void>
   {
     private TypeReference argumentType;
     
     AddMappingsForArgumentVisitor(TypeReference argumentType)
     {
       this.argumentType = ((TypeReference)com.strobel.core.VerifyArgument.notNull(argumentType, "argumentType"));
     }
     
     public Void visit(TypeReference t, Map<TypeReference, TypeReference> map) {
       TypeReference a = this.argumentType;
       t.accept(this, map);
       this.argumentType = a;
       return null;
     }
     
     public Void visitArrayType(ArrayType t, Map<TypeReference, TypeReference> map)
     {
       TypeReference a = this.argumentType;
       
       if ((a.isArray()) && (t.isArray())) {
         this.argumentType = a.getElementType();
         visit(t.getElementType(), map);
       }
       
       return null;
     }
     
 
     public Void visitGenericParameter(GenericParameter t, Map<TypeReference, TypeReference> map)
     {
       if (com.strobel.assembler.metadata.MetadataResolver.areEquivalent(this.argumentType, t)) {
         return null;
       }
       
       TypeReference existingMapping = (TypeReference)map.get(t);
       
       TypeReference mappedType = this.argumentType;
       
       mappedType = TypeAnalysis.ensureReferenceType(mappedType);
       
       if (existingMapping == null) {
         if ((!(mappedType instanceof com.strobel.assembler.metadata.RawType)) && (MetadataHelper.isRawType(mappedType))) {
           TypeReference bound = MetadataHelper.getUpperBound(t);
           TypeReference asSuper = MetadataHelper.asSuper(mappedType, bound);
           
           if (asSuper != null) {
             if (MetadataHelper.isSameType(MetadataHelper.getUpperBound(t), asSuper)) {
               return null;
             }
             mappedType = asSuper;
           }
           else {
             mappedType = MetadataHelper.erase(mappedType);
           }
         }
         map.put(t, mappedType);
       }
       else if (!MetadataHelper.isSubType(this.argumentType, existingMapping))
       {
 
 
         TypeReference commonSuperType = MetadataHelper.asSuper(mappedType, existingMapping);
         
         if (commonSuperType == null) {
           commonSuperType = MetadataHelper.asSuper(existingMapping, mappedType);
         }
         
         if (commonSuperType == null) {
           commonSuperType = MetadataHelper.findCommonSuperType(existingMapping, mappedType);
         }
         
         map.put(t, commonSuperType);
       }
       
       return null;
     }
     
     public Void visitWildcard(com.strobel.assembler.metadata.WildcardType t, Map<TypeReference, TypeReference> map)
     {
       return null;
     }
     
     public Void visitCompoundType(com.strobel.assembler.metadata.CompoundTypeReference t, Map<TypeReference, TypeReference> map)
     {
       return null;
     }
     
     public Void visitParameterizedType(TypeReference t, Map<TypeReference, TypeReference> map)
     {
       TypeReference r = MetadataHelper.asSuper(t.getUnderlyingType(), this.argumentType);
       TypeReference s = MetadataHelper.asSubType(this.argumentType, r != null ? r : t.getUnderlyingType());
       
       if ((s != null) && ((s instanceof IGenericInstance))) {
         List<TypeReference> tArgs = ((IGenericInstance)t).getTypeArguments();
         List<TypeReference> sArgs = ((IGenericInstance)s).getTypeArguments();
         
         if (tArgs.size() == sArgs.size()) {
           int i = 0; for (int n = tArgs.size(); i < n; i++) {
             this.argumentType = ((TypeReference)sArgs.get(i));
             visit((TypeReference)tArgs.get(i), map);
           }
         }
       }
       
       return null;
     }
     
     public Void visitPrimitiveType(com.strobel.assembler.metadata.PrimitiveType t, Map<TypeReference, TypeReference> map)
     {
       return null;
     }
     
     public Void visitClassType(TypeReference t, Map<TypeReference, TypeReference> map)
     {
       return null;
     }
     
     public Void visitNullType(TypeReference t, Map<TypeReference, TypeReference> map)
     {
       return null;
     }
     
     public Void visitBottomType(TypeReference t, Map<TypeReference, TypeReference> map)
     {
       return null;
     }
     
     public Void visitRawType(com.strobel.assembler.metadata.RawType t, Map<TypeReference, TypeReference> map)
     {
       return null;
     }
   }
   
   private static TypeReference ensureReferenceType(TypeReference mappedType) {
     if (mappedType == null) {
       return null;
     }
     
     if (mappedType.isPrimitive()) {
       switch (mappedType.getSimpleType()) {
       case Boolean: 
         return com.strobel.assembler.metadata.CommonTypeReferences.Boolean;
       case Byte: 
         return com.strobel.assembler.metadata.CommonTypeReferences.Byte;
       case Character: 
         return com.strobel.assembler.metadata.CommonTypeReferences.Character;
       case Short: 
         return com.strobel.assembler.metadata.CommonTypeReferences.Short;
       case Integer: 
         return com.strobel.assembler.metadata.CommonTypeReferences.Integer;
       case Long: 
         return com.strobel.assembler.metadata.CommonTypeReferences.Long;
       case Float: 
         return com.strobel.assembler.metadata.CommonTypeReferences.Float;
       case Double: 
         return com.strobel.assembler.metadata.CommonTypeReferences.Double;
       }
       
     }
     return mappedType;
   }
 }


