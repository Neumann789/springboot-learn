 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.assembler.metadata.FieldDefinition;
 import com.strobel.assembler.metadata.GenericParameter;
 import com.strobel.assembler.metadata.MetadataHelper;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.Predicates;
 import com.strobel.core.ReadOnlyList;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.ast.Variable;
 import com.strobel.decompiler.patterns.Pattern;
 import java.util.Collections;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Set;
 
 
 
 
 
 
 
 
 
 
 public final class JavaNameResolver
 {
   public static NameResolveResult resolve(String name, AstNode node)
   {
     return new Result(NameResolveMode.EXPRESSION, resolveCore(node, name, NameResolveMode.EXPRESSION));
   }
   
 
 
   public static NameResolveResult resolveAsType(String name, AstNode node)
   {
     return new Result(NameResolveMode.TYPE, resolveCore(node, name, NameResolveMode.TYPE));
   }
   
 
 
 
 
 
 
   private static List<Object> resolveCore(AstNode location, String name, NameResolveMode mode)
   {
     Set<Object> results = FindDeclarationVisitor.resolveName(location, name, mode);
     
     if (results.isEmpty()) {
       return ReadOnlyList.emptyList();
     }
     
     return new ReadOnlyList(Object.class, results);
   }
   
   private static final class FindDeclarationVisitor implements IAstVisitor<String, Set<Object>> {
     private final NameResolveMode _mode;
     private boolean _isStaticContext = false;
     
     FindDeclarationVisitor(NameResolveMode mode, boolean isStaticContext) {
       this._mode = ((NameResolveMode)VerifyArgument.notNull(mode, "mode"));
       this._isStaticContext = isStaticContext;
     }
     
     static Set<Object> resolveName(AstNode node, String name, NameResolveMode mode) {
       VerifyArgument.notNull(node, "node");
       VerifyArgument.notNull(name, "name");
       VerifyArgument.notNull(mode, "mode");
       
       AstNode n = node;
       Set<Object> results = null;
       
       while ((n instanceof Expression)) {
         n = n.getParent();
       }
       
       if ((n == null) || (n.isNull())) {
         return Collections.emptySet();
       }
       
       TypeDeclaration lastTypeDeclaration = null;
       
       FindDeclarationVisitor visitor = new FindDeclarationVisitor(mode, isStaticContext(node));
       
       while ((n != null) && (!n.isNull())) {
         if ((n instanceof CompilationUnit)) {
           Set<Object> unitResults = (Set)n.acceptVisitor(visitor, name);
           
           if (!unitResults.isEmpty()) {
             if (results == null) {
               return unitResults;
             }
             
             results.addAll(unitResults);
           }
         }
         
         AstNode parent = n.getParent();
         
         if ((n instanceof MethodDeclaration)) {
           Set<Object> methodResults = (Set)n.acceptVisitor(visitor, name);
           
           if (!methodResults.isEmpty()) {
             if (results == null) {
               results = new LinkedHashSet();
             }
             results.addAll(methodResults);
           }
           
           MethodDefinition method = (MethodDefinition)n.getUserData(Keys.METHOD_DEFINITION);
           
           if (method != null) {
             visitor._isStaticContext = method.isStatic();
           }
         }
         else if ((n instanceof TypeDeclaration)) {
           Set<Object> typeResults = (Set)n.acceptVisitor(visitor, name);
           
           if (!typeResults.isEmpty()) {
             if (results == null) {
               results = new LinkedHashSet();
             }
             
             results.addAll(typeResults);
             return results;
           }
           
           if ((parent instanceof TypeDeclaration)) {
             TypeDefinition type = (TypeDefinition)n.getUserData(Keys.TYPE_DEFINITION);
             
             if (type != null) {
               visitor._isStaticContext = type.isStatic();
             }
           }
           else if ((parent instanceof LocalTypeDeclarationStatement)) {
             n = ((LocalTypeDeclarationStatement)parent).getTypeDeclaration();
             parent = n.getParent();
           }
           
           lastTypeDeclaration = (TypeDeclaration)n;
         }
         else if ((n instanceof Statement)) {
           Statement s = (Statement)n;
           Set<Object> statementResults = (Set)s.acceptVisitor(visitor, name);
           
           if (!statementResults.isEmpty()) {
             if (results == null) {
               results = new LinkedHashSet();
             }
             
             results.addAll(statementResults);
             
             if ((mode == NameResolveMode.EXPRESSION) || ((mode == NameResolveMode.TYPE) && (CollectionUtilities.any(results, Predicates.instanceOf(TypeReference.class)))))
             {
 
               return results;
             }
           }
           
           Statement previousStatement = ((Statement)n).getPreviousStatement();
           
           if (previousStatement != null) {
             n = previousStatement;
             continue;
           }
         }
         
         n = parent;
       }
       
       if (results != null) {
         return results;
       }
       
       if (lastTypeDeclaration != null) {
         return visitor.searchUpScope(name, (TypeDefinition)lastTypeDeclaration.getUserData(Keys.TYPE_DEFINITION), new LinkedHashSet(), true);
       }
       
 
 
 
 
 
       return Collections.emptySet();
     }
     
 
 
 
 
     private Set<Object> searchUpScope(String name, TypeDefinition type, Set<String> visitedTypes, boolean searchGenericParameters)
     {
       if ((type == null) || (visitedTypes.contains(type.getInternalName()))) {
         return Collections.emptySet();
       }
       
       Set<Object> results = null;
       
       if (this._mode == NameResolveMode.EXPRESSION) {
         for (FieldDefinition f : type.getDeclaredFields()) {
           if ((StringUtilities.equals(f.getName(), name)) && ((!this._isStaticContext) || (f.isStatic()))) {
             return Collections.singleton(f);
           }
         }
       }
       
       if (StringUtilities.equals(type.getSimpleName(), name)) {
         results = new LinkedHashSet();
         results.add(type);
       }
       
       if (searchGenericParameters) {
         for (GenericParameter gp : type.getGenericParameters()) {
           if (StringUtilities.equals(gp.getName(), name)) {
             if (results == null) {
               results = new LinkedHashSet();
             }
             results.add(gp);
           }
         }
       }
       
       for (TypeDefinition declaredType : type.getDeclaredTypes()) {
         if (StringUtilities.equals(declaredType.getSimpleName(), name)) {
           if (results == null) {
             results = new LinkedHashSet();
           }
           
           results.add(declaredType);
         }
       }
       
       if ((results != null) && (!results.isEmpty())) {
         return results;
       }
       
       TypeReference baseType = type.getBaseType();
       
       if (baseType != null) {
         TypeDefinition resolvedBaseType = baseType.resolve();
         
         if (resolvedBaseType != null) {
           Set<Object> baseTypeResults = searchUpScope(name, resolvedBaseType, visitedTypes, false);
           
           if ((baseTypeResults != null) && (!baseTypeResults.isEmpty())) {
             if (results == null) {
               results = (baseTypeResults instanceof LinkedHashSet) ? baseTypeResults : new LinkedHashSet(baseTypeResults);
             }
             else
             {
               results.addAll(baseTypeResults);
             }
           }
         }
       }
       
       for (TypeReference ifType : MetadataHelper.getInterfaces(type)) {
         TypeDefinition resultIfType = ifType.resolve();
         
         if (resultIfType != null) {
           Set<Object> ifTypeResults = searchUpScope(name, resultIfType, visitedTypes, false);
           
           if ((ifTypeResults != null) && (!ifTypeResults.isEmpty())) {
             if (results == null) {
               results = (ifTypeResults instanceof LinkedHashSet) ? ifTypeResults : new LinkedHashSet(ifTypeResults);
             }
             else
             {
               results.addAll(ifTypeResults);
             }
           }
         }
       }
       
       MethodReference declaringMethod = type.getDeclaringMethod();
       
       if (declaringMethod != null) {
         TypeReference declaringType = declaringMethod.getDeclaringType();
         
         if (declaringType != null) {
           TypeDefinition resolvedType = declaringType.resolve();
           
           if (resolvedType != null) {
             Set<Object> declaringTypeResults = searchUpScope(name, resolvedType, visitedTypes, true);
             
             if ((declaringTypeResults != null) && (!declaringTypeResults.isEmpty())) {
               if (results == null) {
                 results = (declaringTypeResults instanceof LinkedHashSet) ? declaringTypeResults : new LinkedHashSet(declaringTypeResults);
               }
               else
               {
                 results.addAll(declaringTypeResults);
               }
             }
           }
           else if (StringUtilities.equals(declaringType.getSimpleName(), name)) {
             if (results == null) {
               results = new LinkedHashSet();
             }
             results.add(declaringType);
           }
         }
       }
       
       TypeReference declaringType = type.getDeclaringType();
       
       if (declaringType != null) {
         TypeDefinition resolvedType = declaringType.resolve();
         
         if (resolvedType != null) {
           Set<Object> declaringTypeResults = searchUpScope(name, resolvedType, visitedTypes, true);
           
           if ((declaringTypeResults != null) && (!declaringTypeResults.isEmpty())) {
             if (results == null) {
               results = (declaringTypeResults instanceof LinkedHashSet) ? declaringTypeResults : new LinkedHashSet(declaringTypeResults);
             }
             else
             {
               results.addAll(declaringTypeResults);
             }
           }
         }
         else if (StringUtilities.equals(declaringType.getSimpleName(), name)) {
           if (results == null) {
             results = new LinkedHashSet();
           }
           results.add(declaringType);
         }
       }
       
       if (results != null) {
         return results;
       }
       
       return Collections.emptySet();
     }
     
     private static boolean isStaticContext(AstNode node) {
       for (AstNode n = node; 
           (n != null) && (!n.isNull()); 
           n = n.getParent())
       {
         if ((n instanceof MethodDeclaration)) {
           MethodDefinition method = (MethodDefinition)n.getUserData(Keys.METHOD_DEFINITION);
           
           if (method != null) {
             return method.isStatic();
           }
         }
         
         if ((n instanceof TypeDeclaration)) {
           TypeDefinition type = (TypeDefinition)n.getUserData(Keys.TYPE_DEFINITION);
           
           if (type != null) {
             return type.isStatic();
           }
         }
       }
       
       return false;
     }
     
     public Set<Object> visitComment(Comment node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitPatternPlaceholder(AstNode node, Pattern pattern, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitInvocationExpression(InvocationExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitTypeReference(TypeReferenceExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitJavaTokenNode(JavaTokenNode node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitMemberReferenceExpression(MemberReferenceExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitIdentifier(Identifier node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitNullReferenceExpression(NullReferenceExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitThisReferenceExpression(ThisReferenceExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitSuperReferenceExpression(SuperReferenceExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitClassOfExpression(ClassOfExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitBlockStatement(BlockStatement node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitExpressionStatement(ExpressionStatement node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitBreakStatement(BreakStatement node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitContinueStatement(ContinueStatement node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitDoWhileStatement(DoWhileStatement node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitEmptyStatement(EmptyStatement node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitIfElseStatement(IfElseStatement node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitLabelStatement(LabelStatement node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitLabeledStatement(LabeledStatement node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitReturnStatement(ReturnStatement node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitSwitchStatement(SwitchStatement node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitSwitchSection(SwitchSection node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitCaseLabel(CaseLabel node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitThrowStatement(ThrowStatement node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitCatchClause(CatchClause node, String name)
     {
       if ((this._mode == NameResolveMode.EXPRESSION) && (StringUtilities.equals(node.getVariableName(), name))) {
         Variable exceptionVariable = (Variable)node.getUserData(Keys.VARIABLE);
         
         if (exceptionVariable != null) {
           return Collections.singleton(exceptionVariable);
         }
       }
       return Collections.emptySet();
     }
     
     public Set<Object> visitAnnotation(Annotation node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitNewLine(NewLineNode node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitVariableDeclaration(VariableDeclarationStatement node, String name)
     {
       if (this._mode == NameResolveMode.EXPRESSION) {
         VariableInitializer v = node.getVariable(name);
         
         if (v != null) {
           Variable variable = (Variable)v.getUserData(Keys.VARIABLE);
           
           if (variable != null) {
             return Collections.singleton(variable);
           }
         }
       }
       return Collections.emptySet();
     }
     
     public Set<Object> visitVariableInitializer(VariableInitializer node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitText(TextNode node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitImportDeclaration(ImportDeclaration node, String name)
     {
       TypeReference importedType = (TypeReference)node.getUserData(Keys.TYPE_REFERENCE);
       
       if ((importedType != null) && (StringUtilities.equals(importedType.getSimpleName(), name))) {
         return Collections.singleton(importedType);
       }
       
       return Collections.emptySet();
     }
     
     public Set<Object> visitSimpleType(SimpleType node, String name)
     {
       if (StringUtilities.equals(node.getIdentifier(), name)) {
         return Collections.singleton(node.toTypeReference());
       }
       return Collections.emptySet();
     }
     
     public Set<Object> visitMethodDeclaration(MethodDeclaration node, String name)
     {
       Set<Object> results = null;
       
       if (this._mode == NameResolveMode.EXPRESSION) {
         for (ParameterDeclaration p : node.getParameters()) {
           if (StringUtilities.equals(p.getName(), name)) {
             ParameterDefinition pd = (ParameterDefinition)p.getUserData(Keys.PARAMETER_DEFINITION);
             
             if (pd != null)
             {
 
 
               if (results == null) {
                 results = new LinkedHashSet();
               }
               
               results.add(pd);
             }
           }
         }
       }
       for (TypeParameterDeclaration tp : node.getTypeParameters()) {
         TypeDefinition gp = (TypeDefinition)tp.getUserData(Keys.TYPE_DEFINITION);
         
         if ((gp != null) && (StringUtilities.equals(gp.getName(), name))) {
           if (results == null) {
             results = new LinkedHashSet();
           }
           results.add(gp);
         }
       }
       
       if (results != null) {
         return results;
       }
       
       return Collections.emptySet();
     }
     
     public Set<Object> visitInitializerBlock(InstanceInitializer node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitConstructorDeclaration(ConstructorDeclaration node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitTypeParameterDeclaration(TypeParameterDeclaration node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitParameterDeclaration(ParameterDeclaration node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitFieldDeclaration(FieldDeclaration node, String name)
     {
       if ((this._mode == NameResolveMode.EXPRESSION) && 
         (StringUtilities.equals(node.getName(), name))) {
         FieldDefinition f = (FieldDefinition)node.getUserData(Keys.FIELD_DEFINITION);
         
         if ((f != null) && ((!this._isStaticContext) || (f.isStatic()))) {
           return Collections.singleton(f);
         }
       }
       
       return Collections.emptySet();
     }
     
     public Set<Object> visitTypeDeclaration(TypeDeclaration node, String name)
     {
       Set<Object> results = null;
       
       if (this._mode == NameResolveMode.EXPRESSION) {
         for (EntityDeclaration member : node.getMembers()) {
           if ((member instanceof FieldDeclaration)) {
             Set<Object> fieldResults = (Set)member.acceptVisitor(this, name);
             
             if (!fieldResults.isEmpty())
             {
 
 
               return fieldResults;
             }
           }
         }
       }
       if (StringUtilities.equals(node.getName(), name)) {
         TypeDefinition typeDefinition = (TypeDefinition)node.getUserData(Keys.TYPE_DEFINITION);
         
         if (typeDefinition != null) {
           results = new LinkedHashSet();
           results.add(typeDefinition);
         }
       }
       
       for (EntityDeclaration member : node.getMembers()) {
         if ((member instanceof TypeDeclaration)) {
           TypeDeclaration td = (TypeDeclaration)member;
           
           if (StringUtilities.equals(td.getName(), name)) {
             TypeDefinition t = (TypeDefinition)td.getUserData(Keys.TYPE_DEFINITION);
             
             if (t != null)
             {
 
 
               if (results == null) {
                 results = new LinkedHashSet();
               }
               
               results.add(t);
             }
           }
         }
       }
       if ((this._mode == NameResolveMode.TYPE) && (results != null) && (!results.isEmpty())) {
         return results;
       }
       
       for (TypeParameterDeclaration tp : node.getTypeParameters()) {
         TypeDefinition gp = (TypeDefinition)tp.getUserData(Keys.TYPE_DEFINITION);
         
         if ((gp != null) && (StringUtilities.equals(gp.getName(), name))) {
           if (results == null) {
             results = new LinkedHashSet();
           }
           results.add(gp);
         }
       }
       
       if ((results != null) && (!results.isEmpty())) {
         return results;
       }
       
       return searchUpScope(name, (TypeDefinition)node.getUserData(Keys.TYPE_DEFINITION), new LinkedHashSet(), true);
     }
     
 
 
 
 
 
     public Set<Object> visitLocalTypeDeclarationStatement(LocalTypeDeclarationStatement node, String name)
     {
       TypeDeclaration typeDeclaration = node.getTypeDeclaration();
       
       if (typeDeclaration.isNull()) {
         return Collections.emptySet();
       }
       
       if (StringUtilities.equals(typeDeclaration.getName(), name)) {
         TypeDefinition type = (TypeDefinition)typeDeclaration.getUserData(Keys.TYPE_DEFINITION);
         
         if (type != null) {
           return Collections.singleton(type);
         }
       }
       
       return searchUpScope(name, (TypeDefinition)typeDeclaration.getUserData(Keys.TYPE_DEFINITION), new LinkedHashSet(), true);
     }
     
 
 
 
 
 
     public Set<Object> visitCompilationUnit(CompilationUnit node, String name)
     {
       Set<Object> results = null;
       
       for (TypeDeclaration typeDeclaration : node.getTypes()) {
         Set<Object> typeResults = (Set)typeDeclaration.acceptVisitor(this, name);
         
         if (!typeResults.isEmpty())
         {
 
 
           if (results == null) {
             results = new LinkedHashSet();
           }
           
           results.addAll(typeResults);
         }
       }
       for (ImportDeclaration typeImport : node.getImports()) {
         Set<Object> importResults = (Set)typeImport.acceptVisitor(this, name);
         
         if (!importResults.isEmpty())
         {
 
 
           if (results == null) {
             results = new LinkedHashSet();
           }
           
           results.addAll(importResults);
         }
       }
       if (results != null) {
         return results;
       }
       
       return Collections.emptySet();
     }
     
     public Set<Object> visitPackageDeclaration(PackageDeclaration node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitArraySpecifier(ArraySpecifier node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitComposedType(ComposedType node, String name)
     {
       return (Set)node.getBaseType().acceptVisitor(this, name);
     }
     
     public Set<Object> visitWhileStatement(WhileStatement node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitPrimitiveExpression(PrimitiveExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitCastExpression(CastExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitBinaryOperatorExpression(BinaryOperatorExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitInstanceOfExpression(InstanceOfExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitIndexerExpression(IndexerExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitIdentifierExpression(IdentifierExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitUnaryOperatorExpression(UnaryOperatorExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitConditionalExpression(ConditionalExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitArrayInitializerExpression(ArrayInitializerExpression arrayInitializerExpression, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitObjectCreationExpression(ObjectCreationExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitArrayCreationExpression(ArrayCreationExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitAssignmentExpression(AssignmentExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitForStatement(ForStatement node, String name) {
       Set<Object> results;
       if (this._mode == NameResolveMode.EXPRESSION) {
         results = null;
         
         for (Statement initializer : node.getInitializers()) {
           Set<Object> initializerResults = (Set)initializer.acceptVisitor(this, name);
           
           if (!node.getInitializers().isEmpty())
           {
 
 
             if (results == null) {
               results = new LinkedHashSet();
             }
             
             results.addAll(initializerResults);
           }
         }
       }
       return Collections.emptySet();
     }
     
     public Set<Object> visitForEachStatement(ForEachStatement node, String name)
     {
       if ((this._mode == NameResolveMode.EXPRESSION) && 
         (StringUtilities.equals(node.getVariableName(), name))) {
         Variable v = (Variable)node.getUserData(Keys.VARIABLE);
         
         if (v != null) {
           return Collections.singleton(v);
         }
       }
       
 
       return Collections.emptySet();
     }
     
     public Set<Object> visitTryCatchStatement(TryCatchStatement node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitGotoStatement(GotoStatement node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitParenthesizedExpression(ParenthesizedExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitSynchronizedStatement(SynchronizedStatement node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitAnonymousObjectCreationExpression(AnonymousObjectCreationExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitWildcardType(WildcardType node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitMethodGroupExpression(MethodGroupExpression node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitEnumValueDeclaration(EnumValueDeclaration node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitAssertStatement(AssertStatement node, String name)
     {
       return Collections.emptySet();
     }
     
     public Set<Object> visitLambdaExpression(LambdaExpression node, String name)
     {
       if (this._mode == NameResolveMode.EXPRESSION) {
         Set<Object> results = null;
         
         for (ParameterDeclaration pd : node.getParameters()) {
           if (StringUtilities.equals(pd.getName(), name)) {
             ParameterDefinition p = (ParameterDefinition)pd.getUserData(Keys.PARAMETER_DEFINITION);
             
             if (p != null) {
               if (results == null) {
                 results = new LinkedHashSet();
               }
               results.add(p);
             }
           }
         }
         
         if (results != null) {
           return results;
         }
       }
       
       return Collections.emptySet();
     }
   }
   
   private static final class Result extends NameResolveResult {
     private final NameResolveMode _mode;
     private final List<Object> _candidates;
     
     Result(NameResolveMode mode, List<Object> candidates) {
       this._mode = mode;
       this._candidates = candidates;
     }
     
     public final List<Object> getCandidates()
     {
       return this._candidates;
     }
     
     public final NameResolveMode getMode()
     {
       return this._mode;
     }
   }
 }


