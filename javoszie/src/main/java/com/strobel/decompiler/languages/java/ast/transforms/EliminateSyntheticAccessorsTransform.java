 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.AstType;
 import com.strobel.decompiler.languages.java.ast.BlockStatement;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.ExpressionStatement;
 import com.strobel.decompiler.languages.java.ast.InvocationExpression;
 import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
 import com.strobel.decompiler.languages.java.ast.ParameterDeclaration;
 import com.strobel.decompiler.languages.java.ast.ReturnStatement;
 import com.strobel.decompiler.languages.java.ast.Statement;
 import com.strobel.decompiler.patterns.AnyNode;
 import com.strobel.decompiler.patterns.MemberReferenceTypeNode;
 import com.strobel.decompiler.patterns.NamedNode;
 import com.strobel.decompiler.patterns.ParameterReferenceNode;
 import com.strobel.decompiler.patterns.SubtreeMatch;
 import com.strobel.decompiler.patterns.TypedNode;
 import java.util.List;
 
 public class EliminateSyntheticAccessorsTransform extends ContextTrackingVisitor<Void>
 {
   private final List<com.strobel.decompiler.languages.java.ast.AstNode> _nodesToRemove;
   private final java.util.Map<String, MethodDeclaration> _accessMethodDeclarations;
   private final java.util.Set<String> _visitedTypes;
   private static final MethodDeclaration SYNTHETIC_GET_ACCESSOR;
   private static final MethodDeclaration SYNTHETIC_SET_ACCESSOR;
   private static final MethodDeclaration SYNTHETIC_SET_ACCESSOR_ALT;
   private static final MethodDeclaration SYNTHETIC_STATIC_GET_ACCESSOR;
   private static final MethodDeclaration SYNTHETIC_STATIC_SET_ACCESSOR;
   private static final MethodDeclaration SYNTHETIC_STATIC_SET_ACCESSOR_ALT;
   
   public EliminateSyntheticAccessorsTransform(com.strobel.decompiler.DecompilerContext context)
   {
     super(context);
     
     this._nodesToRemove = new java.util.ArrayList();
     this._accessMethodDeclarations = new java.util.HashMap();
     this._visitedTypes = new java.util.HashSet();
   }
   
 
 
 
   public void run(com.strobel.decompiler.languages.java.ast.AstNode compilationUnit)
   {
     new PhaseOneVisitor(null).run(compilationUnit);
     
     super.run(compilationUnit);
     
     for (com.strobel.decompiler.languages.java.ast.AstNode node : this._nodesToRemove) {
       node.remove();
     }
   }
   
   private static String makeMethodKey(MethodReference method) {
     return method.getFullName() + ":" + method.getErasedSignature();
   }
   
   public Void visitInvocationExpression(InvocationExpression node, Void data)
   {
     super.visitInvocationExpression(node, data);
     
     Expression target = node.getTarget();
     AstNodeCollection<Expression> arguments = node.getArguments();
     
     if ((target instanceof MemberReferenceExpression)) {
       MemberReferenceExpression memberReference = (MemberReferenceExpression)target;
       
       com.strobel.assembler.metadata.MemberReference reference = (com.strobel.assembler.metadata.MemberReference)memberReference.getUserData(com.strobel.decompiler.languages.java.ast.Keys.MEMBER_REFERENCE);
       
       if (reference == null) {
         reference = (com.strobel.assembler.metadata.MemberReference)node.getUserData(com.strobel.decompiler.languages.java.ast.Keys.MEMBER_REFERENCE);
       }
       
       if ((reference instanceof MethodReference)) {
         MethodReference method = (MethodReference)reference;
         com.strobel.assembler.metadata.TypeReference declaringType = method.getDeclaringType();
         
         if ((!com.strobel.assembler.metadata.MetadataResolver.areEquivalent(this.context.getCurrentType(), declaringType)) && (!com.strobel.assembler.metadata.MetadataHelper.isEnclosedBy(this.context.getCurrentType(), declaringType)) && (!this._visitedTypes.contains(declaringType.getInternalName())))
         {
 
 
           MethodDefinition resolvedMethod = method.resolve();
           
           if ((resolvedMethod != null) && (resolvedMethod.isSynthetic()))
           {
             com.strobel.decompiler.languages.java.ast.AstBuilder astBuilder = (com.strobel.decompiler.languages.java.ast.AstBuilder)this.context.getUserData(com.strobel.decompiler.languages.java.ast.Keys.AST_BUILDER);
             
             if (astBuilder != null) {
               com.strobel.decompiler.languages.java.ast.TypeDeclaration ownerTypeDeclaration = astBuilder.createType(resolvedMethod.getDeclaringType());
               
               ownerTypeDeclaration.acceptVisitor(new PhaseOneVisitor(null), data);
             }
           }
         }
         
         String key = makeMethodKey(method);
         MethodDeclaration declaration = (MethodDeclaration)this._accessMethodDeclarations.get(key);
         
         if (declaration != null) {
           MethodDefinition definition = (MethodDefinition)declaration.getUserData(com.strobel.decompiler.languages.java.ast.Keys.METHOD_DEFINITION);
           List<com.strobel.assembler.metadata.ParameterDefinition> parameters = definition != null ? definition.getParameters() : null;
           
           if ((definition != null) && (parameters.size() == arguments.size())) {
             java.util.Map<com.strobel.assembler.metadata.ParameterDefinition, com.strobel.decompiler.languages.java.ast.AstNode> parameterMap = new java.util.IdentityHashMap();
             
             int i = 0;
             
             for (Expression argument : arguments) {
               parameterMap.put(parameters.get(i++), argument);
             }
             
             com.strobel.decompiler.languages.java.ast.AstNode inlinedBody = com.strobel.decompiler.languages.java.ast.InliningHelper.inlineMethod(declaration, parameterMap);
             
             if ((inlinedBody instanceof Expression)) {
               node.replaceWith(inlinedBody);
             }
             else if ((inlinedBody instanceof BlockStatement)) {
               BlockStatement block = (BlockStatement)inlinedBody;
               
               if (block.getStatements().size() == 2) {
                 Statement setStatement = (Statement)block.getStatements().firstOrNullObject();
                 
                 if ((setStatement instanceof ExpressionStatement)) {
                   Expression expression = ((ExpressionStatement)setStatement).getExpression();
                   
                   if ((expression instanceof AssignmentExpression)) {
                     expression.remove();
                     node.replaceWith(expression);
                   }
                 }
               }
               else if (block.getStatements().size() == 3) {
                 Statement tempAssignment = (Statement)block.getStatements().firstOrNullObject();
                 Statement setStatement = (Statement)com.strobel.core.CollectionUtilities.getOrDefault(block.getStatements(), 1);
                 
                 if (((tempAssignment instanceof com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement)) && ((setStatement instanceof ExpressionStatement)))
                 {
 
                   Expression expression = ((ExpressionStatement)setStatement).getExpression();
                   
                   if ((expression instanceof AssignmentExpression)) {
                     com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement tempVariable = (com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement)tempAssignment;
                     Expression initializer = ((com.strobel.decompiler.languages.java.ast.VariableInitializer)tempVariable.getVariables().firstOrNullObject()).getInitializer();
                     AssignmentExpression assignment = (AssignmentExpression)expression;
                     
                     initializer.remove();
                     assignment.setRight(initializer);
                     expression.remove();
                     node.replaceWith(expression);
                   }
                 }
               }
             }
           }
         }
       }
     }
     
     return null;
   }
   
 
 
 
 
 
 
 
 
   static
   {
     MethodDeclaration getAccessor = new MethodDeclaration();
     MethodDeclaration setAccessor = new MethodDeclaration();
     
     getAccessor.setName("$any$");
     getAccessor.getModifiers().add(new com.strobel.decompiler.languages.java.ast.JavaModifierToken(javax.lang.model.element.Modifier.STATIC));
     getAccessor.setReturnType(new AnyNode("returnType").toType());
     
     setAccessor.setName("$any$");
     setAccessor.getModifiers().add(new com.strobel.decompiler.languages.java.ast.JavaModifierToken(javax.lang.model.element.Modifier.STATIC));
     setAccessor.setReturnType(new AnyNode("returnType").toType());
     
     ParameterDeclaration getParameter = new ParameterDeclaration("$any$", new AnyNode("targetType").toType());
     
 
 
 
     getParameter.setAnyModifiers(true);
     getAccessor.getParameters().add(getParameter);
     
     ParameterDeclaration setParameter1 = new ParameterDeclaration("$any$", new AnyNode("targetType").toType());
     
 
 
 
     ParameterDeclaration setParameter2 = new ParameterDeclaration("$any$", new com.strobel.decompiler.patterns.BackReference("returnType").toType());
     
 
 
 
     setParameter1.setAnyModifiers(true);
     setParameter2.setAnyModifiers(true);
     
     setAccessor.getParameters().add(setParameter1);
     setAccessor.getParameters().add(new com.strobel.decompiler.patterns.OptionalNode(setParameter2).toParameterDeclaration());
     
     getAccessor.setBody(new BlockStatement(new Statement[] { new ReturnStatement(-34, new SubtreeMatch(new MemberReferenceTypeNode(new MemberReferenceExpression(-34, new ParameterReferenceNode(0).toExpression(), "$any$", new AstType[0]), FieldReference.class)).toExpression()) }));
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     MethodDeclaration altSetAccessor = (MethodDeclaration)setAccessor.clone();
     
     setAccessor.setBody(new com.strobel.decompiler.patterns.Choice(new com.strobel.decompiler.patterns.INode[] { new BlockStatement(new Statement[] { new ExpressionStatement(new AssignmentExpression(new MemberReferenceTypeNode(new MemberReferenceExpression(-34, new ParameterReferenceNode(0).toExpression(), "$any$", new AstType[0]), FieldReference.class).toExpression(), com.strobel.decompiler.languages.java.ast.AssignmentOperatorType.ANY, new ParameterReferenceNode(1, "value").toExpression())), new ReturnStatement(-34, new com.strobel.decompiler.patterns.BackReference("value").toExpression()) }), new BlockStatement(new Statement[] { new ReturnStatement(-34, new AssignmentExpression(new MemberReferenceTypeNode(new MemberReferenceExpression(-34, new ParameterReferenceNode(0).toExpression(), "$any$", new AstType[0]), FieldReference.class).toExpression(), com.strobel.decompiler.languages.java.ast.AssignmentOperatorType.ANY, new ParameterReferenceNode(1, "value").toExpression())) }) }).toBlockStatement());
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement tempVariable = new com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement(new AnyNode().toType(), "$any$", new AnyNode("value").toExpression());
     
 
 
 
 
     tempVariable.addModifier(javax.lang.model.element.Modifier.FINAL);
     
     altSetAccessor.setBody(new BlockStatement(new Statement[] { new NamedNode("tempVariable", tempVariable).toStatement(), new ExpressionStatement(new AssignmentExpression(new MemberReferenceTypeNode(new MemberReferenceExpression(-34, new ParameterReferenceNode(0).toExpression(), "$any$", new AstType[0]), FieldReference.class).toExpression(), com.strobel.decompiler.languages.java.ast.AssignmentOperatorType.ANY, new SubtreeMatch(new com.strobel.decompiler.patterns.DeclaredVariableBackReference("tempVariable")).toExpression())), new ReturnStatement(-34, new com.strobel.decompiler.patterns.DeclaredVariableBackReference("tempVariable").toExpression()) }));
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     SYNTHETIC_GET_ACCESSOR = getAccessor;
     SYNTHETIC_SET_ACCESSOR = setAccessor;
     SYNTHETIC_SET_ACCESSOR_ALT = altSetAccessor;
     
     MethodDeclaration staticGetAccessor = (MethodDeclaration)getAccessor.clone();
     MethodDeclaration staticSetAccessor = (MethodDeclaration)setAccessor.clone();
     MethodDeclaration altStaticSetAccessor = (MethodDeclaration)altSetAccessor.clone();
     
     staticGetAccessor.getParameters().clear();
     
     staticGetAccessor.setBody(new BlockStatement(new Statement[] { new ReturnStatement(-34, new SubtreeMatch(new MemberReferenceTypeNode(new MemberReferenceExpression(-34, new TypedNode(com.strobel.decompiler.languages.java.ast.TypeReferenceExpression.class).toExpression(), "$any$", new AstType[0]), FieldReference.class)).toExpression()) }));
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     ((ParameterDeclaration)staticSetAccessor.getParameters().firstOrNullObject()).remove();
     
     staticSetAccessor.setBody(new com.strobel.decompiler.patterns.Choice(new com.strobel.decompiler.patterns.INode[] { new BlockStatement(new Statement[] { new ExpressionStatement(new AssignmentExpression(new MemberReferenceTypeNode(new MemberReferenceExpression(-34, new TypedNode(com.strobel.decompiler.languages.java.ast.TypeReferenceExpression.class).toExpression(), "$any$", new AstType[0]), FieldReference.class).toExpression(), com.strobel.decompiler.languages.java.ast.AssignmentOperatorType.ANY, new NamedNode("value", new SubtreeMatch(new ParameterReferenceNode(0))).toExpression())), new ReturnStatement(-34, new com.strobel.decompiler.patterns.BackReference("value").toExpression()) }), new BlockStatement(new Statement[] { new ReturnStatement(-34, new AssignmentExpression(new MemberReferenceTypeNode(new MemberReferenceExpression(-34, new TypedNode(com.strobel.decompiler.languages.java.ast.TypeReferenceExpression.class).toExpression(), "$any$", new AstType[0]), FieldReference.class).toExpression(), com.strobel.decompiler.languages.java.ast.AssignmentOperatorType.ANY, new NamedNode("value", new SubtreeMatch(new ParameterReferenceNode(0))).toExpression())) }) }).toBlockStatement());
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     ((ParameterDeclaration)altStaticSetAccessor.getParameters().firstOrNullObject()).remove();
     
     altStaticSetAccessor.setBody(new BlockStatement(new Statement[] { new NamedNode("tempVariable", tempVariable).toStatement(), new ExpressionStatement(new AssignmentExpression(new MemberReferenceTypeNode(new MemberReferenceExpression(-34, new TypedNode(com.strobel.decompiler.languages.java.ast.TypeReferenceExpression.class).toExpression(), "$any$", new AstType[0]), FieldReference.class).toExpression(), com.strobel.decompiler.languages.java.ast.AssignmentOperatorType.ANY, new SubtreeMatch(new com.strobel.decompiler.patterns.DeclaredVariableBackReference("tempVariable")).toExpression())), new ReturnStatement(-34, new com.strobel.decompiler.patterns.DeclaredVariableBackReference("tempVariable").toExpression()) }));
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     SYNTHETIC_STATIC_GET_ACCESSOR = staticGetAccessor;
     SYNTHETIC_STATIC_SET_ACCESSOR = staticSetAccessor;
     SYNTHETIC_STATIC_SET_ACCESSOR_ALT = altStaticSetAccessor;
   }
   
   private class PhaseOneVisitor extends ContextTrackingVisitor<Void> {
     private PhaseOneVisitor() {
       super();
     }
     
     public Void visitTypeDeclaration(com.strobel.decompiler.languages.java.ast.TypeDeclaration node, Void _)
     {
       com.strobel.assembler.metadata.TypeDefinition type = (com.strobel.assembler.metadata.TypeDefinition)node.getUserData(com.strobel.decompiler.languages.java.ast.Keys.TYPE_DEFINITION);
       
       if ((type != null) && 
         (!EliminateSyntheticAccessorsTransform.this._visitedTypes.add(type.getInternalName()))) {
         return null;
       }
       
 
       return (Void)super.visitTypeDeclaration(node, _);
     }
     
     public Void visitMethodDeclaration(MethodDeclaration node, Void _)
     {
       MethodDefinition method = (MethodDefinition)node.getUserData(com.strobel.decompiler.languages.java.ast.Keys.METHOD_DEFINITION);
       
       if ((method != null) && 
         (method.isSynthetic()) && (method.isStatic()) && (
         (tryMatchAccessor(node)) || (tryMatchCallWrapper(node)))) {
         EliminateSyntheticAccessorsTransform.this._accessMethodDeclarations.put(EliminateSyntheticAccessorsTransform.makeMethodKey(method), node);
       }
       
 
 
       return (Void)super.visitMethodDeclaration(node, _);
     }
     
     private boolean tryMatchAccessor(MethodDeclaration node) {
       if ((EliminateSyntheticAccessorsTransform.SYNTHETIC_GET_ACCESSOR.matches(node)) || (EliminateSyntheticAccessorsTransform.SYNTHETIC_SET_ACCESSOR.matches(node)) || (EliminateSyntheticAccessorsTransform.SYNTHETIC_SET_ACCESSOR_ALT.matches(node)) || (EliminateSyntheticAccessorsTransform.SYNTHETIC_STATIC_GET_ACCESSOR.matches(node)) || (EliminateSyntheticAccessorsTransform.SYNTHETIC_STATIC_SET_ACCESSOR.matches(node)) || (EliminateSyntheticAccessorsTransform.SYNTHETIC_STATIC_SET_ACCESSOR_ALT.matches(node)))
       {
 
 
 
 
 
         return true;
       }
       
       return false;
     }
     
     private boolean tryMatchCallWrapper(MethodDeclaration node) {
       AstNodeCollection<Statement> statements = node.getBody().getStatements();
       
       if (!statements.hasSingleElement()) {
         return false;
       }
       
       Statement s = (Statement)statements.firstOrNullObject();
       InvocationExpression invocation;
       InvocationExpression invocation;
       if ((s instanceof ExpressionStatement)) {
         ExpressionStatement e = (ExpressionStatement)s;
         
         invocation = (e.getExpression() instanceof InvocationExpression) ? (InvocationExpression)e.getExpression() : null;
       } else {
         InvocationExpression invocation;
         if ((s instanceof ReturnStatement)) {
           ReturnStatement r = (ReturnStatement)s;
           
           invocation = (r.getExpression() instanceof InvocationExpression) ? (InvocationExpression)r.getExpression() : null;
         }
         else
         {
           invocation = null;
         }
       }
       if (invocation == null) {
         return false;
       }
       
       MethodReference targetMethod = (MethodReference)invocation.getUserData(com.strobel.decompiler.languages.java.ast.Keys.MEMBER_REFERENCE);
       MethodDefinition resolvedTarget = targetMethod != null ? targetMethod.resolve() : null;
       
       if (resolvedTarget == null) {
         return false;
       }
       
       int parametersStart = resolvedTarget.isStatic() ? 0 : 1;
       List<ParameterDeclaration> parameterList = com.strobel.core.CollectionUtilities.toList(node.getParameters());
       List<Expression> argumentList = com.strobel.core.CollectionUtilities.toList(invocation.getArguments());
       
       if (argumentList.size() != parameterList.size() - parametersStart) {
         return false;
       }
       
       if (!resolvedTarget.isStatic()) {
         if (!(invocation.getTarget() instanceof MemberReferenceExpression)) {
           return false;
         }
         
         MemberReferenceExpression m = (MemberReferenceExpression)invocation.getTarget();
         Expression target = m.getTarget();
         
         if (!target.matches(new com.strobel.decompiler.languages.java.ast.IdentifierExpression(-34, ((ParameterDeclaration)parameterList.get(0)).getName()))) {
           return false;
         }
       }
       
 
 
       int i = parametersStart; for (int j = 0; 
           (i < parameterList.size()) && (j < argumentList.size()); 
           j++)
       {
 
         Expression pattern = new com.strobel.decompiler.patterns.Choice(new com.strobel.decompiler.patterns.INode[] { new com.strobel.decompiler.languages.java.ast.CastExpression(new AnyNode().toType(), new com.strobel.decompiler.languages.java.ast.IdentifierExpression(-34, ((ParameterDeclaration)parameterList.get(i)).getName())), new com.strobel.decompiler.languages.java.ast.IdentifierExpression(-34, ((ParameterDeclaration)parameterList.get(i)).getName()) }).toExpression();
         
 
 
 
 
 
 
         if (!pattern.matches((com.strobel.decompiler.patterns.INode)argumentList.get(j))) {
           return false;
         }
         i++;
       }
       
 
 
 
 
 
 
 
 
 
 
 
 
 
       return i == j + parametersStart;
     }
   }
 }


