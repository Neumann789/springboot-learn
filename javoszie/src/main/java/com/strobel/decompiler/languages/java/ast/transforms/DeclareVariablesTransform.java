 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.StrongBox;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.ast.Variable;
 import com.strobel.decompiler.languages.java.ast.AnonymousObjectCreationExpression;
 import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
 import com.strobel.decompiler.languages.java.ast.AssignmentOperatorType;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.AstType;
 import com.strobel.decompiler.languages.java.ast.BlockStatement;
 import com.strobel.decompiler.languages.java.ast.CatchClause;
 import com.strobel.decompiler.languages.java.ast.DefiniteAssignmentAnalysis;
 import com.strobel.decompiler.languages.java.ast.DepthFirstAstVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.ExpressionStatement;
 import com.strobel.decompiler.languages.java.ast.FieldDeclaration;
 import com.strobel.decompiler.languages.java.ast.ForEachStatement;
 import com.strobel.decompiler.languages.java.ast.ForStatement;
 import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
 import com.strobel.decompiler.languages.java.ast.ParameterDeclaration;
 import com.strobel.decompiler.languages.java.ast.Roles;
 import com.strobel.decompiler.languages.java.ast.Statement;
 import com.strobel.decompiler.languages.java.ast.TryCatchStatement;
 import com.strobel.decompiler.languages.java.ast.TypeDeclaration;
 import com.strobel.decompiler.languages.java.ast.UnaryOperatorExpression;
 import com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement;
 import com.strobel.decompiler.languages.java.ast.VariableInitializer;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import javax.lang.model.element.Modifier;
 
 public class DeclareVariablesTransform implements IAstTransform
 {
   protected final List<VariableToDeclare> variablesToDeclare = new java.util.ArrayList();
   protected final DecompilerContext context;
   
   public DeclareVariablesTransform(DecompilerContext context) {
     this.context = ((DecompilerContext)com.strobel.core.VerifyArgument.notNull(context, "context"));
   }
   
 
   public void run(AstNode node)
   {
     run(node, null);
     
     for (VariableToDeclare v : this.variablesToDeclare) {
       Variable variable = v.getVariable();
       AssignmentExpression replacedAssignment = v.getReplacedAssignment();
       
       if (replacedAssignment == null) {
         BlockStatement block = (BlockStatement)v.getInsertionPoint().getParent();
         AnalysisResult analysisResult = analyze(v, block);
         VariableDeclarationStatement declaration = new VariableDeclarationStatement(v.getType().clone(), v.getName(), -34);
         
         if (variable != null) {
           ((VariableInitializer)declaration.getVariables().firstOrNullObject()).putUserData(Keys.VARIABLE, variable);
         }
         
         if (analysisResult.isSingleAssignment) {
           declaration.addModifier(Modifier.FINAL);
         }
         else if ((analysisResult.needsInitializer) && (variable != null)) {
           ((VariableInitializer)declaration.getVariables().firstOrNullObject()).setInitializer(com.strobel.decompiler.languages.java.ast.AstBuilder.makeDefaultValue(variable.getType()));
         }
         
 
 
         Statement insertionPoint = v.getInsertionPoint();
         
         while ((insertionPoint.getPreviousSibling() instanceof com.strobel.decompiler.languages.java.ast.LabelStatement)) {
           insertionPoint = (Statement)insertionPoint.getPreviousSibling();
         }
         
         block.getStatements().insertBefore(insertionPoint, declaration);
       }
     }
     
 
 
 
 
 
     for (VariableToDeclare v : this.variablesToDeclare) {
       Variable variable = v.getVariable();
       AssignmentExpression replacedAssignment = v.getReplacedAssignment();
       
       if (replacedAssignment != null) {
         VariableInitializer initializer = new VariableInitializer(v.getName());
         Expression right = replacedAssignment.getRight();
         AstNode parent = replacedAssignment.getParent();
         
         if ((!parent.isNull()) && (parent.getParent() != null))
         {
 
           AnalysisResult analysisResult = analyze(v, parent.getParent());
           
           right.remove();
           right.putUserDataIfAbsent(Keys.MEMBER_REFERENCE, replacedAssignment.getUserData(Keys.MEMBER_REFERENCE));
           right.putUserDataIfAbsent(Keys.VARIABLE, variable);
           
           initializer.setInitializer(right);
           initializer.putUserData(Keys.VARIABLE, variable);
           
           VariableDeclarationStatement declaration = new VariableDeclarationStatement();
           
           declaration.setType(v.getType().clone());
           declaration.getVariables().add(initializer);
           
           if ((parent instanceof ExpressionStatement)) {
             if (analysisResult.isSingleAssignment) {
               declaration.addModifier(Modifier.FINAL);
             }
             
             declaration.putUserDataIfAbsent(Keys.MEMBER_REFERENCE, parent.getUserData(Keys.MEMBER_REFERENCE));
             declaration.putUserData(Keys.VARIABLE, variable);
             parent.replaceWith(declaration);
           }
           else {
             if (analysisResult.isSingleAssignment) {
               declaration.addModifier(Modifier.FINAL);
             }
             
             replacedAssignment.replaceWith(declaration);
           }
         }
       }
     }
     this.variablesToDeclare.clear();
   }
   
   private AnalysisResult analyze(VariableToDeclare v, AstNode scope) {
     BlockStatement block = v.getBlock();
     DefiniteAssignmentAnalysis analysis = new DefiniteAssignmentAnalysis(this.context, block);
     
     if (v.getInsertionPoint() != null) {
       Statement parentStatement = v.getInsertionPoint();
       analysis.setAnalyzedRange(parentStatement, block);
     }
     else {
       ExpressionStatement parentStatement = (ExpressionStatement)v.getReplacedAssignment().getParent();
       analysis.setAnalyzedRange(parentStatement, block);
     }
     
     analysis.analyze(v.getName());
     
     boolean needsInitializer = !analysis.getUnassignedVariableUses().isEmpty();
     IsSingleAssignmentVisitor isSingleAssignmentVisitor = new IsSingleAssignmentVisitor(v.getName(), v.getReplacedAssignment());
     
     scope.acceptVisitor(isSingleAssignmentVisitor, null);
     
     return new AnalysisResult(isSingleAssignmentVisitor.isSingleAssignment(), needsInitializer, null);
   }
   
   private static final class AnalysisResult {
     final boolean isSingleAssignment;
     final boolean needsInitializer;
     
     private AnalysisResult(boolean singleAssignment, boolean needsInitializer) {
       this.isSingleAssignment = singleAssignment;
       this.needsInitializer = needsInitializer;
     }
   }
   
   private void run(AstNode node, DefiniteAssignmentAnalysis daa) {
     DefiniteAssignmentAnalysis analysis = daa;
     BlockStatement block;
     if ((node instanceof BlockStatement)) {
       block = (BlockStatement)node;
       List<VariableDeclarationStatement> variables = new java.util.ArrayList();
       
       for (Statement statement : block.getStatements()) {
         if ((statement instanceof VariableDeclarationStatement)) {
           variables.add((VariableDeclarationStatement)statement);
         }
       }
       
       if (!variables.isEmpty())
       {
 
 
         for (VariableDeclarationStatement declaration : variables) {
           assert ((declaration.getVariables().size() == 1) && (((VariableInitializer)declaration.getVariables().firstOrNullObject()).getInitializer().isNull()));
           
 
           declaration.remove();
         }
       }
       
       if (analysis == null) {
         analysis = new DefiniteAssignmentAnalysis(block, new com.strobel.decompiler.languages.java.ast.JavaResolver(this.context));
       }
       
       for (VariableDeclarationStatement declaration : variables) {
         VariableInitializer initializer = (VariableInitializer)declaration.getVariables().firstOrNullObject();
         String variableName = initializer.getName();
         Variable variable = (Variable)declaration.getUserData(Keys.VARIABLE);
         
         declareVariableInBlock(analysis, block, declaration.getType(), variableName, variable, true);
       }
     }
     Map<ParameterDefinition, ParameterDeclaration> declarationMap;
     if (((node instanceof MethodDeclaration)) || ((node instanceof com.strobel.decompiler.languages.java.ast.ConstructorDeclaration)))
     {
 
       Set<ParameterDefinition> unassignedParameters = new java.util.HashSet();
       AstNodeCollection<ParameterDeclaration> parameters = node.getChildrenByRole(Roles.PARAMETER);
       declarationMap = new java.util.HashMap();
       Map<String, ParameterDefinition> parametersByName = new java.util.HashMap();
       
       for (ParameterDeclaration parameter : parameters) {
         ParameterDefinition definition = (ParameterDefinition)parameter.getUserData(Keys.PARAMETER_DEFINITION);
         
         if (definition != null) {
           unassignedParameters.add(definition);
           declarationMap.put(definition, parameter);
           parametersByName.put(parameter.getName(), definition);
         }
       }
       
       node.acceptVisitor(new ParameterAssignmentVisitor(unassignedParameters, parametersByName), null);
       
       for (ParameterDefinition definition : unassignedParameters) {
         ParameterDeclaration declaration = (ParameterDeclaration)declarationMap.get(definition);
         
         if ((declaration != null) && (!declaration.hasModifier(Modifier.FINAL))) {
           declaration.addChild(new com.strobel.decompiler.languages.java.ast.JavaModifierToken(Modifier.FINAL), com.strobel.decompiler.languages.java.ast.EntityDeclaration.MODIFIER_ROLE);
         }
       }
     }
     
     for (AstNode child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
       if ((child instanceof TypeDeclaration)) {
         TypeDefinition currentType = this.context.getCurrentType();
         com.strobel.assembler.metadata.MethodDefinition currentMethod = this.context.getCurrentMethod();
         
         this.context.setCurrentType(null);
         this.context.setCurrentMethod(null);
         try
         {
           TypeDefinition type = (TypeDefinition)child.getUserData(Keys.TYPE_DEFINITION);
           
           if ((type != null) && (type.isInterface()))
           {
 
 
 
 
 
             this.context.setCurrentType(currentType);
             this.context.setCurrentMethod(currentMethod); continue;
           }
           new DeclareVariablesTransform(this.context).run(child);
         }
         finally {
           this.context.setCurrentType(currentType);
           this.context.setCurrentMethod(currentMethod);
         }
       }
       else {
         run(child, analysis);
       }
     }
   }
   
 
 
 
 
 
 
 
 
 
 
   private void declareVariableInBlock(DefiniteAssignmentAnalysis analysis, BlockStatement block, AstType type, String variableName, Variable variable, boolean allowPassIntoLoops)
   {
     StrongBox<Statement> declarationPoint = new StrongBox();
     
     boolean canMoveVariableIntoSubBlocks = findDeclarationPoint(analysis, variableName, allowPassIntoLoops, block, declarationPoint, null);
     
 
 
 
 
 
 
 
     if (declarationPoint.get() == null)
     {
 
 
       return;
     }
     
     if (canMoveVariableIntoSubBlocks) {
       for (Statement statement : block.getStatements()) {
         if (usesVariable(statement, variableName))
         {
 
 
           boolean processChildren = true;
           
           if (((statement instanceof ForStatement)) && (statement == declarationPoint.get())) {
             ForStatement forStatement = (ForStatement)statement;
             AstNodeCollection<Statement> initializers = forStatement.getInitializers();
             
             for (Statement initializer : initializers) {
               if (tryConvertAssignmentExpressionIntoVariableDeclaration(block, initializer, type, variableName)) {
                 processChildren = false;
                 break;
               }
             }
           }
           
           if (processChildren) {
             for (AstNode child : statement.getChildren()) {
               if ((child instanceof BlockStatement)) {
                 declareVariableInBlock(analysis, (BlockStatement)child, type, variableName, variable, allowPassIntoLoops);
               }
               else if (hasNestedBlocks(child)) {
                 for (AstNode nestedChild : child.getChildren()) {
                   if ((nestedChild instanceof BlockStatement)) {
                     declareVariableInBlock(analysis, (BlockStatement)nestedChild, type, variableName, variable, allowPassIntoLoops);
                   }
                 }
               }
             }
           }
           
 
 
 
 
 
 
 
           boolean canStillMoveIntoSubBlocks = findDeclarationPoint(analysis, variableName, allowPassIntoLoops, block, declarationPoint, statement);
           
 
 
 
 
 
 
 
           if ((!canStillMoveIntoSubBlocks) && (declarationPoint.get() != null)) {
             if (!tryConvertAssignmentExpressionIntoVariableDeclaration(block, (Statement)declarationPoint.get(), type, variableName)) {
               VariableToDeclare vtd = new VariableToDeclare(type, variableName, variable, (Statement)declarationPoint.get(), block);
               this.variablesToDeclare.add(vtd);
             }
             return;
           }
         }
       }
     } else if (!tryConvertAssignmentExpressionIntoVariableDeclaration(block, (Statement)declarationPoint.get(), type, variableName)) {
       VariableToDeclare vtd = new VariableToDeclare(type, variableName, variable, (Statement)declarationPoint.get(), block);
       this.variablesToDeclare.add(vtd);
     }
   }
   
 
 
 
 
 
   public static boolean findDeclarationPoint(DefiniteAssignmentAnalysis analysis, VariableDeclarationStatement declaration, BlockStatement block, StrongBox<Statement> declarationPoint, Statement skipUpThrough)
   {
     String variableName = ((VariableInitializer)declaration.getVariables().firstOrNullObject()).getName();
     
     return findDeclarationPoint(analysis, variableName, true, block, declarationPoint, skipUpThrough);
   }
   
 
 
 
 
 
 
   static boolean findDeclarationPoint(DefiniteAssignmentAnalysis analysis, String variableName, boolean allowPassIntoLoops, BlockStatement block, StrongBox<Statement> declarationPoint, Statement skipUpThrough)
   {
     declarationPoint.set(null);
     
     Statement waitFor = skipUpThrough;
     
     if ((block.getParent() instanceof CatchClause)) {
       CatchClause catchClause = (CatchClause)block.getParent();
       
       if (StringUtilities.equals(catchClause.getVariableName(), variableName)) {
         return false;
       }
     }
     
     for (Statement statement : block.getStatements()) {
       if (waitFor != null) {
         if (statement == waitFor) {
           waitFor = null;
         }
         
 
       }
       else if (usesVariable(statement, variableName)) {
         if (declarationPoint.get() != null) {
           return canRedeclareVariable(analysis, block, statement, variableName);
         }
         
         declarationPoint.set(statement);
         
         if (!canMoveVariableIntoSubBlock(analysis, block, statement, variableName, allowPassIntoLoops))
         {
 
 
 
           return false;
         }
         
 
 
 
 
 
 
         Statement nextStatement = statement.getNextStatement();
         
         if (nextStatement != null) {
           analysis.setAnalyzedRange(nextStatement, block);
           analysis.analyze(variableName);
           
           if (!analysis.getUnassignedVariableUses().isEmpty()) {
             return false;
           }
         }
       }
     }
     
     return true;
   }
   
 
 
 
 
 
   private static boolean canMoveVariableIntoSubBlock(DefiniteAssignmentAnalysis analysis, BlockStatement block, Statement statement, String variableName, boolean allowPassIntoLoops)
   {
     if ((!allowPassIntoLoops) && (AstNode.isLoop(statement))) {
       return false;
     }
     
     if ((statement instanceof ForStatement)) {
       ForStatement forStatement = (ForStatement)statement;
       
 
 
 
 
       if (!forStatement.getInitializers().isEmpty()) {
         boolean result = false;
         com.strobel.assembler.metadata.TypeReference lastInitializerType = null;
         StrongBox<Statement> declarationPoint = null;
         
         Set<String> variableNames = new java.util.HashSet();
         
         for (Statement initializer : forStatement.getInitializers()) {
           if (((initializer instanceof ExpressionStatement)) && ((((ExpressionStatement)initializer).getExpression() instanceof AssignmentExpression)))
           {
 
             Expression e = ((ExpressionStatement)initializer).getExpression();
             
             if (((e instanceof AssignmentExpression)) && (((AssignmentExpression)e).getOperator() == AssignmentOperatorType.ASSIGN) && ((((AssignmentExpression)e).getLeft() instanceof IdentifierExpression)))
             {
 
 
               IdentifierExpression identifier = (IdentifierExpression)((AssignmentExpression)e).getLeft();
               boolean usedByInitializer = usesVariable(((AssignmentExpression)e).getRight(), variableName);
               
               if (usedByInitializer) {
                 return false;
               }
               
               Variable variable = (Variable)identifier.getUserData(Keys.VARIABLE);
               
               if ((variable == null) || (variable.isParameter())) {
                 return false;
               }
               
               com.strobel.assembler.metadata.TypeReference variableType = variable.getType();
               
               if (lastInitializerType == null) {
                 lastInitializerType = variableType;
               }
               else if (!com.strobel.assembler.metadata.MetadataHelper.isSameType(lastInitializerType, variableType)) {
                 return false;
               }
               
               if (!variableNames.add(identifier.getIdentifier()))
               {
 
 
                 return false;
               }
               
               if (result) {
                 if (declarationPoint == null) {
                   declarationPoint = new StrongBox();
                 }
                 
 
 
 
 
 
                 if ((!findDeclarationPoint(analysis, identifier.getIdentifier(), allowPassIntoLoops, block, declarationPoint, null)) || (declarationPoint.get() != statement))
                 {
 
                   return false;
                 }
               }
               else if (StringUtilities.equals(identifier.getIdentifier(), variableName)) {
                 result = true;
               }
             }
           }
         }
         
         if (result) {
           return true;
         }
       }
     }
     
     if ((statement instanceof TryCatchStatement)) {
       TryCatchStatement tryCatch = (TryCatchStatement)statement;
       
 
 
 
 
 
       if (!tryCatch.getResources().isEmpty()) {
         for (VariableDeclarationStatement resource : tryCatch.getResources()) {
           if (StringUtilities.equals(((VariableInitializer)CollectionUtilities.first(resource.getVariables())).getName(), variableName)) {
             return true;
           }
         }
       }
     }
     
 
 
 
 
 
     for (AstNode child = statement.getFirstChild(); child != null; child = child.getNextSibling()) {
       if ((!(child instanceof BlockStatement)) && (usesVariable(child, variableName))) {
         if (hasNestedBlocks(child))
         {
 
 
           for (AstNode grandChild = child.getFirstChild(); grandChild != null; grandChild = grandChild.getNextSibling()) {
             if ((!(grandChild instanceof BlockStatement)) && (usesVariable(grandChild, variableName))) {
               return false;
             }
             
           }
         } else {
           return false;
         }
       }
     }
     
     return true;
   }
   
   private static boolean usesVariable(AstNode node, String variableName) {
     if ((node instanceof AnonymousObjectCreationExpression)) {
       for (Expression argument : ((AnonymousObjectCreationExpression)node).getArguments()) {
         if (usesVariable(argument, variableName)) {
           return true;
         }
       }
       return false;
     }
     
     if ((node instanceof TypeDeclaration)) {
       TypeDeclaration type = (TypeDeclaration)node;
       
       for (FieldDeclaration field : CollectionUtilities.ofType(type.getMembers(), FieldDeclaration.class)) {
         if ((!field.getVariables().isEmpty()) && (usesVariable((AstNode)CollectionUtilities.first(field.getVariables()), variableName))) {
           return true;
         }
       }
       
       for (MethodDeclaration method : CollectionUtilities.ofType(type.getMembers(), MethodDeclaration.class)) {
         if (usesVariable(method.getBody(), variableName)) {
           return true;
         }
       }
       
       return false;
     }
     
     if (((node instanceof IdentifierExpression)) && 
       (StringUtilities.equals(((IdentifierExpression)node).getIdentifier(), variableName))) {
       return true;
     }
     
 
     if ((node instanceof ForStatement)) {
       ForStatement forLoop = (ForStatement)node;
       
       for (Statement statement : forLoop.getInitializers()) {
         if ((statement instanceof VariableDeclarationStatement)) {
           AstNodeCollection<VariableInitializer> variables = ((VariableDeclarationStatement)statement).getVariables();
           
           for (VariableInitializer variable : variables) {
             if (StringUtilities.equals(variable.getName(), variableName))
             {
 
 
               return false;
             }
           }
         }
       }
     }
     
     if ((node instanceof TryCatchStatement)) {
       TryCatchStatement tryCatch = (TryCatchStatement)node;
       
       for (VariableDeclarationStatement resource : tryCatch.getResources()) {
         if (StringUtilities.equals(((VariableInitializer)CollectionUtilities.first(resource.getVariables())).getName(), variableName))
         {
 
 
           return false;
         }
       }
     }
     
     if (((node instanceof ForEachStatement)) && 
       (StringUtilities.equals(((ForEachStatement)node).getVariableName(), variableName)))
     {
 
 
       return false;
     }
     
 
     if (((node instanceof CatchClause)) && 
       (StringUtilities.equals(((CatchClause)node).getVariableName(), variableName)))
     {
 
 
       return false;
     }
     
 
     for (AstNode child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
       if (usesVariable(child, variableName)) {
         return true;
       }
     }
     
     return false;
   }
   
 
 
 
 
   private static boolean canRedeclareVariable(DefiniteAssignmentAnalysis analysis, BlockStatement block, AstNode node, String variableName)
   {
     if ((node instanceof ForStatement)) {
       ForStatement forLoop = (ForStatement)node;
       
       for (Statement statement : forLoop.getInitializers()) {
         if ((statement instanceof VariableDeclarationStatement)) {
           AstNodeCollection<VariableInitializer> variables = ((VariableDeclarationStatement)statement).getVariables();
           
           for (VariableInitializer variable : variables) {
             if (StringUtilities.equals(variable.getName(), variableName)) {
               return true;
             }
           }
         }
         else if (((statement instanceof ExpressionStatement)) && ((((ExpressionStatement)statement).getExpression() instanceof AssignmentExpression)))
         {
 
           AssignmentExpression assignment = (AssignmentExpression)((ExpressionStatement)statement).getExpression();
           Expression left = assignment.getLeft();
           Expression right = assignment.getRight();
           
           if (((left instanceof IdentifierExpression)) && (StringUtilities.equals(((IdentifierExpression)left).getIdentifier(), variableName)) && (!usesVariable(right, variableName)))
           {
 
 
             return true;
           }
         }
       }
     }
     
     if (((node instanceof ForEachStatement)) && 
       (StringUtilities.equals(((ForEachStatement)node).getVariableName(), variableName))) {
       return true;
     }
     
 
     if ((node instanceof TryCatchStatement)) {
       TryCatchStatement tryCatch = (TryCatchStatement)node;
       
       for (VariableDeclarationStatement resource : tryCatch.getResources()) {
         if (StringUtilities.equals(((VariableInitializer)CollectionUtilities.first(resource.getVariables())).getName(), variableName)) {
           return true;
         }
       }
     }
     
     for (AstNode prev = node.getPreviousSibling(); 
         (prev != null) && (!prev.isNull()); 
         prev = prev.getPreviousSibling())
     {
       if (usesVariable(prev, variableName)) {
         Statement statement = (Statement)CollectionUtilities.firstOrDefault(CollectionUtilities.ofType(prev.getAncestorsAndSelf(), Statement.class));
         
         if (statement == null) {
           return false;
         }
         
         if (!canMoveVariableIntoSubBlock(analysis, block, statement, variableName, true)) {
           return false;
         }
       }
     }
     
     return true;
   }
   
   private static boolean hasNestedBlocks(AstNode node) {
     return ((node.getChildByRole(Roles.EMBEDDED_STATEMENT) instanceof BlockStatement)) || ((node instanceof TryCatchStatement)) || ((node instanceof CatchClause)) || ((node instanceof com.strobel.decompiler.languages.java.ast.SwitchSection));
   }
   
 
 
 
 
 
 
 
   private boolean tryConvertAssignmentExpressionIntoVariableDeclaration(BlockStatement block, Statement declarationPoint, AstType type, String variableName)
   {
     return ((declarationPoint instanceof ExpressionStatement)) && (tryConvertAssignmentExpressionIntoVariableDeclaration(block, ((ExpressionStatement)declarationPoint).getExpression(), type, variableName));
   }
   
 
 
 
 
 
 
 
 
 
 
   private boolean tryConvertAssignmentExpressionIntoVariableDeclaration(BlockStatement block, Expression expression, AstType type, String variableName)
   {
     if ((expression instanceof AssignmentExpression)) {
       AssignmentExpression assignment = (AssignmentExpression)expression;
       
       if ((assignment.getOperator() == AssignmentOperatorType.ASSIGN) && 
         ((assignment.getLeft() instanceof IdentifierExpression))) {
         IdentifierExpression identifier = (IdentifierExpression)assignment.getLeft();
         
         if (StringUtilities.equals(identifier.getIdentifier(), variableName)) {
           this.variablesToDeclare.add(new VariableToDeclare(type, variableName, (Variable)identifier.getUserData(Keys.VARIABLE), assignment, block));
           
 
 
 
 
 
 
 
 
           return true;
         }
       }
     }
     
 
     return false;
   }
   
 
   protected static final class VariableToDeclare
   {
     private final AstType _type;
     
     private final String _name;
     
     private final Variable _variable;
     
     private final Statement _insertionPoint;
     
     private final AssignmentExpression _replacedAssignment;
     
     private final BlockStatement _block;
     
     public VariableToDeclare(AstType type, String name, Variable variable, Statement insertionPoint, BlockStatement block)
     {
       this._type = type;
       this._name = name;
       this._variable = variable;
       this._insertionPoint = insertionPoint;
       this._replacedAssignment = null;
       this._block = block;
     }
     
 
 
 
 
 
     public VariableToDeclare(AstType type, String name, Variable variable, AssignmentExpression replacedAssignment, BlockStatement block)
     {
       this._type = type;
       this._name = name;
       this._variable = variable;
       this._insertionPoint = null;
       this._replacedAssignment = replacedAssignment;
       this._block = block;
     }
     
     public BlockStatement getBlock() {
       return this._block;
     }
     
     public AstType getType() {
       return this._type;
     }
     
     public String getName() {
       return this._name;
     }
     
     public Variable getVariable() {
       return this._variable;
     }
     
     public AssignmentExpression getReplacedAssignment() {
       return this._replacedAssignment;
     }
     
     public Statement getInsertionPoint() {
       return this._insertionPoint;
     }
     
     public String toString()
     {
       return "VariableToDeclare{Type=" + this._type + ", Name='" + this._name + '\'' + ", Variable=" + this._variable + ", InsertionPoint=" + this._insertionPoint + ", ReplacedAssignment=" + this._replacedAssignment + '}';
     }
   }
   
 
 
   private final class IsSingleAssignmentVisitor
     extends DepthFirstAstVisitor<Void, Boolean>
   {
     private final String _variableName;
     
     private final AssignmentExpression _replacedAssignment;
     
     private boolean _abort;
     
     private int _loopOrTryDepth;
     
     private int _assignmentCount;
     
 
     IsSingleAssignmentVisitor(String variableName, AssignmentExpression replacedAssignment)
     {
       this._variableName = ((String)com.strobel.core.VerifyArgument.notNull(variableName, "variableName"));
       this._replacedAssignment = replacedAssignment;
     }
     
     final boolean isAssigned() {
       return (this._assignmentCount > 0) && (!this._abort);
     }
     
     final boolean isSingleAssignment() {
       return (this._assignmentCount < 2) && (!this._abort);
     }
     
     protected Boolean visitChildren(AstNode node, Void data)
     {
       if (this._abort) {
         return Boolean.FALSE;
       }
       return (Boolean)super.visitChildren(node, data);
     }
     
     public Boolean visitForStatement(ForStatement node, Void _)
     {
       this._loopOrTryDepth += 1;
       try {
         return (Boolean)super.visitForStatement(node, _);
       }
       finally {
         this._loopOrTryDepth -= 1;
       }
     }
     
     public Boolean visitForEachStatement(ForEachStatement node, Void _)
     {
       this._loopOrTryDepth += 1;
       try {
         if (StringUtilities.equals(node.getVariableName(), this._variableName)) {
           this._assignmentCount += 1;
         }
         return (Boolean)super.visitForEachStatement(node, _);
       }
       finally {
         this._loopOrTryDepth -= 1;
       }
     }
     
     public Boolean visitDoWhileStatement(com.strobel.decompiler.languages.java.ast.DoWhileStatement node, Void _)
     {
       this._loopOrTryDepth += 1;
       try {
         return (Boolean)super.visitDoWhileStatement(node, _);
       }
       finally {
         this._loopOrTryDepth -= 1;
       }
     }
     
     public Boolean visitWhileStatement(com.strobel.decompiler.languages.java.ast.WhileStatement node, Void _)
     {
       this._loopOrTryDepth += 1;
       try {
         return (Boolean)super.visitWhileStatement(node, _);
       }
       finally {
         this._loopOrTryDepth -= 1;
       }
     }
     
     public Boolean visitTryCatchStatement(TryCatchStatement node, Void data)
     {
       this._loopOrTryDepth += 1;
       try {
         return (Boolean)super.visitTryCatchStatement(node, data);
       }
       finally {
         this._loopOrTryDepth -= 1;
       }
     }
     
     public Boolean visitAssignmentExpression(AssignmentExpression node, Void _)
     {
       Expression left = node.getLeft();
       
       if (((left instanceof IdentifierExpression)) && (StringUtilities.equals(((IdentifierExpression)left).getIdentifier(), this._variableName)))
       {
 
         if ((this._loopOrTryDepth != 0) && (this._replacedAssignment != node)) {
           this._abort = true;
           return Boolean.FALSE;
         }
         
         this._assignmentCount += 1;
       }
       
       return (Boolean)super.visitAssignmentExpression(node, _);
     }
     
     public Boolean visitTypeDeclaration(TypeDeclaration node, Void data)
     {
       return null;
     }
     
     public Boolean visitUnaryOperatorExpression(UnaryOperatorExpression node, Void _)
     {
       Expression operand = node.getExpression();
       
       switch (DeclareVariablesTransform.1.$SwitchMap$com$strobel$decompiler$languages$java$ast$UnaryOperatorType[node.getOperator().ordinal()]) {
       case 1: 
       case 2: 
       case 3: 
       case 4: 
         if (((operand instanceof IdentifierExpression)) && (StringUtilities.equals(((IdentifierExpression)operand).getIdentifier(), this._variableName)))
         {
 
           if (this._loopOrTryDepth != 0) {
             this._abort = true;
             return Boolean.FALSE;
           }
           
           if (this._assignmentCount == 0) {
             this._assignmentCount += 1;
           }
           
           this._assignmentCount += 1;
         }
         
         break;
       }
       
       return (Boolean)super.visitUnaryOperatorExpression(node, _);
     }
   }
   
 
 
   private final class ParameterAssignmentVisitor
     extends DepthFirstAstVisitor<Void, Boolean>
   {
     private final Set<ParameterDefinition> _unassignedParameters;
     
     private final Map<String, ParameterDefinition> _parametersByName;
     
 
     ParameterAssignmentVisitor(Map<String, ParameterDefinition> unassignedParameters)
     {
       this._unassignedParameters = unassignedParameters;
       this._parametersByName = parametersByName;
       
       for (ParameterDefinition p : unassignedParameters) {
         this._parametersByName.put(p.getName(), p);
       }
     }
     
     protected Boolean visitChildren(AstNode node, Void data)
     {
       return (Boolean)super.visitChildren(node, data);
     }
     
     public Boolean visitAssignmentExpression(AssignmentExpression node, Void _)
     {
       Expression left = node.getLeft();
       Variable variable = (Variable)left.getUserData(Keys.VARIABLE);
       
       if ((variable != null) && (variable.isParameter())) {
         this._unassignedParameters.remove(variable.getOriginalParameter());
         return (Boolean)super.visitAssignmentExpression(node, _);
       }
       
       ParameterDefinition parameter = (ParameterDefinition)left.getUserData(Keys.PARAMETER_DEFINITION);
       
       if ((parameter == null) && ((left instanceof IdentifierExpression))) {
         parameter = (ParameterDefinition)this._parametersByName.get(((IdentifierExpression)left).getIdentifier());
       }
       
       if (parameter != null) {
         this._unassignedParameters.remove(parameter);
       }
       
       return (Boolean)super.visitAssignmentExpression(node, _);
     }
     
     public Boolean visitTypeDeclaration(TypeDeclaration node, Void data)
     {
       return null;
     }
     
     public Boolean visitUnaryOperatorExpression(UnaryOperatorExpression node, Void _)
     {
       Expression operand = node.getExpression();
       
       switch (DeclareVariablesTransform.1.$SwitchMap$com$strobel$decompiler$languages$java$ast$UnaryOperatorType[node.getOperator().ordinal()]) {
       case 1: 
       case 2: 
       case 3: 
       case 4: 
         ParameterDefinition parameter = (ParameterDefinition)operand.getUserData(Keys.PARAMETER_DEFINITION);
         
         if ((parameter == null) && ((operand instanceof IdentifierExpression))) {
           parameter = (ParameterDefinition)this._parametersByName.get(((IdentifierExpression)operand).getIdentifier());
         }
         
         if (parameter != null) {
           this._unassignedParameters.remove(parameter);
         }
         
         break;
       }
       
       
       return (Boolean)super.visitUnaryOperatorExpression(node, _);
     }
   }
 }


