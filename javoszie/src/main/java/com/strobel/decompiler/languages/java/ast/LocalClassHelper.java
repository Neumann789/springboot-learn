 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.assembler.metadata.FieldDefinition;
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.IMethodSignature;
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.assembler.metadata.MetadataHelper;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.DecompilerSettings;
 import com.strobel.decompiler.ast.Variable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class LocalClassHelper
 {
   private static final ConvertTypeOptions OUTER_TYPE_CONVERT_OPTIONS = new ConvertTypeOptions(false, false);
   static { OUTER_TYPE_CONVERT_OPTIONS.setIncludeTypeArguments(false); }
   
   public static void replaceClosureMembers(DecompilerContext context, AnonymousObjectCreationExpression node)
   {
     replaceClosureMembers(context, node.getTypeDeclaration(), Collections.singletonList(node));
   }
   
 
 
 
   public static void replaceClosureMembers(DecompilerContext context, TypeDeclaration declaration, List<? extends ObjectCreationExpression> instantiations)
   {
     VerifyArgument.notNull(context, "context");
     VerifyArgument.notNull(declaration, "declaration");
     VerifyArgument.notNull(instantiations, "instantiations");
     
     Map<String, Expression> initializers = new HashMap();
     Map<String, Expression> replacements = new HashMap();
     List<AstNode> nodesToRemove = new ArrayList();
     List<ParameterDefinition> parametersToRemove = new ArrayList();
     List<Expression> originalArguments;
     List<Expression> originalArguments;
     if (instantiations.isEmpty()) {
       originalArguments = Collections.emptyList();
     }
     else {
       originalArguments = new ArrayList(((ObjectCreationExpression)instantiations.get(0)).getArguments());
     }
     
     new ClosureRewriterPhaseOneVisitor(context, originalArguments, replacements, initializers, parametersToRemove, nodesToRemove).run(declaration);
     
     rewriteThisReferences(context, declaration, initializers);
     
     new ClosureRewriterPhaseTwoVisitor(context, replacements, initializers).run(declaration);
     
     for (Iterator i$ = instantiations.iterator(); i$.hasNext();) { instantiation = (ObjectCreationExpression)i$.next();
       for (ParameterDefinition p : parametersToRemove) {
         Expression argumentToRemove = (Expression)CollectionUtilities.getOrDefault(instantiation.getArguments(), p.getPosition());
         
         if (argumentToRemove != null) {
           instantiation.getArguments().remove(argumentToRemove);
         }
       }
     }
     ObjectCreationExpression instantiation;
     for (AstNode n : nodesToRemove) { int argumentIndex;
       if ((n instanceof Expression)) {
         argumentIndex = originalArguments.indexOf(n);
         
         if (argumentIndex >= 0) {
           for (ObjectCreationExpression instantiation : instantiations) {
             Expression argumentToRemove = (Expression)CollectionUtilities.getOrDefault(instantiation.getArguments(), argumentIndex);
             
             if (argumentToRemove != null) {
               argumentToRemove.remove();
             }
           }
         }
       }
       
       n.remove();
     }
   }
   
   public static void introduceInitializerBlocks(DecompilerContext context, AstNode node) {
     VerifyArgument.notNull(context, "context");
     VerifyArgument.notNull(node, "node");
     
     new IntroduceInitializersVisitor(context).run(node);
   }
   
 
 
 
   private static void rewriteThisReferences(DecompilerContext context, TypeDeclaration declaration, Map<String, Expression> initializers)
   {
     TypeDefinition innerClass = (TypeDefinition)declaration.getUserData(Keys.TYPE_DEFINITION);
     ContextTrackingVisitor<Void> thisRewriter;
     if (innerClass != null) {
       thisRewriter = new ThisReferenceReplacingVisitor(context, innerClass);
       
       for (Expression e : initializers.values()) {
         thisRewriter.run(e);
       }
     }
   }
   
 
   private static final class ClosureRewriterPhaseOneVisitor
     extends ContextTrackingVisitor<Void>
   {
     private final Map<String, Expression> _replacements;
     
     private final List<Expression> _originalArguments;
     
     private final List<ParameterDefinition> _parametersToRemove;
     
     private final Map<String, Expression> _initializers;
     
     private final List<AstNode> _nodesToRemove;
     private boolean _baseConstructorCalled;
     
     public ClosureRewriterPhaseOneVisitor(DecompilerContext context, List<Expression> originalArguments, Map<String, Expression> replacements, Map<String, Expression> initializers, List<ParameterDefinition> parametersToRemove, List<AstNode> nodesToRemove)
     {
       super();
       
       this._originalArguments = ((List)VerifyArgument.notNull(originalArguments, "originalArguments"));
       this._replacements = ((Map)VerifyArgument.notNull(replacements, "replacements"));
       this._initializers = ((Map)VerifyArgument.notNull(initializers, "initializers"));
       this._parametersToRemove = ((List)VerifyArgument.notNull(parametersToRemove, "parametersToRemove"));
       this._nodesToRemove = ((List)VerifyArgument.notNull(nodesToRemove, "nodesToRemove"));
     }
     
     public Void visitConstructorDeclaration(ConstructorDeclaration node, Void _)
     {
       boolean wasDone = this._baseConstructorCalled;
       
       this._baseConstructorCalled = false;
       try
       {
         return (Void)super.visitConstructorDeclaration(node, _);
       }
       finally {
         this._baseConstructorCalled = wasDone;
       }
     }
     
     protected Void visitChildren(AstNode node, Void _)
     {
       MethodDefinition currentMethod = this.context.getCurrentMethod();
       
       if ((currentMethod != null) && (!currentMethod.isConstructor())) {
         return null;
       }
       
       return (Void)super.visitChildren(node, _);
     }
     
     public Void visitSuperReferenceExpression(SuperReferenceExpression node, Void _)
     {
       super.visitSuperReferenceExpression(node, _);
       
       if ((this.context.getCurrentMethod() != null) && (this.context.getCurrentMethod().isConstructor()) && ((node.getParent() instanceof InvocationExpression)))
       {
 
 
 
 
 
         this._baseConstructorCalled = true;
       }
       
       return null;
     }
     
     public Void visitAssignmentExpression(AssignmentExpression node, Void _)
     {
       super.visitAssignmentExpression(node, _);
       
       if ((this.context.getCurrentMethod() == null) || (!this.context.getCurrentMethod().isConstructor())) {
         return null;
       }
       
       Expression left = node.getLeft();
       Expression right = node.getRight();
       
       if ((left instanceof MemberReferenceExpression)) {
         if ((right instanceof IdentifierExpression)) {
           Variable variable = (Variable)right.getUserData(Keys.VARIABLE);
           
           if ((variable == null) || (!variable.isParameter())) {
             return null;
           }
           
           MemberReferenceExpression memberReference = (MemberReferenceExpression)left;
           MemberReference member = (MemberReference)memberReference.getUserData(Keys.MEMBER_REFERENCE);
           
           if (((member instanceof FieldReference)) && ((memberReference.getTarget() instanceof ThisReferenceExpression)))
           {
 
             FieldDefinition resolvedField = ((FieldReference)member).resolve();
             
             if ((resolvedField != null) && (resolvedField.isSynthetic())) {
               ParameterDefinition parameter = variable.getOriginalParameter();
               
               int parameterIndex = parameter.getPosition();
               
               if (parameter.getMethod().getParameters().size() > this._originalArguments.size()) {
                 parameterIndex -= parameter.getMethod().getParameters().size() - this._originalArguments.size();
               }
               
               if ((parameterIndex >= 0) && (parameterIndex < this._originalArguments.size())) {
                 Expression argument = (Expression)this._originalArguments.get(parameterIndex);
                 
                 if (argument == null) {
                   return null;
                 }
                 
                 this._nodesToRemove.add(argument);
                 
                 if ((argument instanceof ThisReferenceExpression))
                 {
 
 
                   markConstructorParameterForRemoval(node, parameter);
                   return null;
                 }
                 
                 this._parametersToRemove.add(parameter);
                 
                 String fullName = member.getFullName();
                 
                 if (!LocalClassHelper.hasSideEffects(argument)) {
                   this._replacements.put(fullName, argument);
                 }
                 else {
                   this.context.getForcedVisibleMembers().add(resolvedField);
                   this._initializers.put(fullName, argument);
                 }
                 
                 if ((node.getParent() instanceof ExpressionStatement)) {
                   this._nodesToRemove.add(node.getParent());
                 }
                 
                 markConstructorParameterForRemoval(node, parameter);
               }
             }
             else if ((this._baseConstructorCalled) && (resolvedField != null) && (this.context.getCurrentMethod().isConstructor()) && ((!this.context.getCurrentMethod().isSynthetic()) || (this.context.getSettings().getShowSyntheticMembers())))
             {
 
 
 
 
               MemberReferenceExpression leftMemberReference = (MemberReferenceExpression)left;
               MemberReference leftMember = (MemberReference)leftMemberReference.getUserData(Keys.MEMBER_REFERENCE);
               Variable rightVariable = (Variable)right.getUserData(Keys.VARIABLE);
               
               if (rightVariable.isParameter()) {
                 ParameterDefinition parameter = variable.getOriginalParameter();
                 int parameterIndex = parameter.getPosition();
                 
                 if ((parameterIndex >= 0) && (parameterIndex < this._originalArguments.size())) {
                   Expression argument = (Expression)this._originalArguments.get(parameterIndex);
                   
                   if ((parameterIndex == 0) && ((argument instanceof ThisReferenceExpression)) && (LocalClassHelper.isLocalOrAnonymous(this.context.getCurrentType())))
                   {
 
 
 
 
 
                     return null;
                   }
                   
                   FieldDefinition resolvedTargetField = ((FieldReference)leftMember).resolve();
                   
                   if ((resolvedTargetField != null) && (!resolvedTargetField.isSynthetic())) {
                     this._parametersToRemove.add(parameter);
                     this._initializers.put(resolvedTargetField.getFullName(), argument);
                     
                     if ((node.getParent() instanceof ExpressionStatement)) {
                       this._nodesToRemove.add(node.getParent());
                     }
                   }
                 }
               }
             }
           }
         }
         else if ((this._baseConstructorCalled) && ((right instanceof MemberReferenceExpression))) {
           MemberReferenceExpression leftMemberReference = (MemberReferenceExpression)left;
           MemberReference leftMember = (MemberReference)leftMemberReference.getUserData(Keys.MEMBER_REFERENCE);
           MemberReferenceExpression rightMemberReference = (MemberReferenceExpression)right;
           MemberReference rightMember = (MemberReference)right.getUserData(Keys.MEMBER_REFERENCE);
           
           if (((rightMember instanceof FieldReference)) && ((rightMemberReference.getTarget() instanceof ThisReferenceExpression)))
           {
 
             FieldDefinition resolvedTargetField = ((FieldReference)leftMember).resolve();
             FieldDefinition resolvedSourceField = ((FieldReference)rightMember).resolve();
             
             if ((resolvedSourceField != null) && (resolvedTargetField != null) && (resolvedSourceField.isSynthetic()) && (!resolvedTargetField.isSynthetic()))
             {
 
 
 
               Expression initializer = (Expression)this._replacements.get(rightMember.getFullName());
               
               if (initializer != null) {
                 this._initializers.put(resolvedTargetField.getFullName(), initializer);
                 
                 if ((node.getParent() instanceof ExpressionStatement)) {
                   this._nodesToRemove.add(node.getParent());
                 }
               }
             }
           }
         }
       }
       
       return null;
     }
     
     private void markConstructorParameterForRemoval(AssignmentExpression node, ParameterDefinition parameter) {
       ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration)node.getParent(ConstructorDeclaration.class);
       
       if (constructorDeclaration != null) {
         AstNodeCollection<ParameterDeclaration> parameters = constructorDeclaration.getParameters();
         
         for (ParameterDeclaration p : parameters) {
           if (p.getUserData(Keys.PARAMETER_DEFINITION) == parameter) {
             this._nodesToRemove.add(p);
             break;
           }
         }
       }
     }
   }
   
   private static boolean isLocalOrAnonymous(TypeDefinition type) {
     return (type != null) && ((type.isLocalClass()) || (type.isAnonymous()));
   }
   
   private static boolean hasSideEffects(Expression e) {
     return (!(e instanceof IdentifierExpression)) && (!(e instanceof PrimitiveExpression)) && (!(e instanceof ThisReferenceExpression)) && (!(e instanceof SuperReferenceExpression)) && (!(e instanceof NullReferenceExpression)) && (!(e instanceof ClassOfExpression));
   }
   
 
 
   private static final class ClosureRewriterPhaseTwoVisitor
     extends ContextTrackingVisitor<Void>
   {
     private final Map<String, Expression> _replacements;
     
 
     private final Map<String, Expression> _initializers;
     
 
 
     protected ClosureRewriterPhaseTwoVisitor(DecompilerContext context, Map<String, Expression> replacements, Map<String, Expression> initializers)
     {
       super();
       
       this._replacements = ((Map)VerifyArgument.notNull(replacements, "replacements"));
       this._initializers = ((Map)VerifyArgument.notNull(initializers, "initializers"));
     }
     
     public Void visitFieldDeclaration(FieldDeclaration node, Void data)
     {
       super.visitFieldDeclaration(node, data);
       
       FieldDefinition field = (FieldDefinition)node.getUserData(Keys.FIELD_DEFINITION);
       
       if ((field != null) && (!this._initializers.isEmpty()) && (node.getVariables().size() == 1) && (((VariableInitializer)node.getVariables().firstOrNullObject()).getInitializer().isNull()))
       {
 
 
 
         Expression initializer = (Expression)this._initializers.get(field.getFullName());
         
         if (initializer != null) {
           ((VariableInitializer)node.getVariables().firstOrNullObject()).setInitializer(initializer.clone());
         }
       }
       
       return null;
     }
     
     public Void visitMemberReferenceExpression(MemberReferenceExpression node, Void _)
     {
       super.visitMemberReferenceExpression(node, _);
       
       if (((node.getParent() instanceof AssignmentExpression)) && (node.getRole() == AssignmentExpression.LEFT_ROLE))
       {
 
         return null;
       }
       
       MemberReference member = (MemberReference)node.getUserData(Keys.MEMBER_REFERENCE);
       
       if ((member instanceof FieldReference)) {
         Expression replacement = (Expression)this._replacements.get(member.getFullName());
         
         if (replacement != null) {
           node.replaceWith(replacement.clone());
         }
       }
       
       return null;
     }
   }
   
   private static class ThisReferenceReplacingVisitor extends ContextTrackingVisitor<Void> {
     private final TypeDefinition _innerClass;
     
     public ThisReferenceReplacingVisitor(DecompilerContext context, TypeDefinition innerClass) {
       super();
       this._innerClass = innerClass;
     }
     
     public Void visitMemberReferenceExpression(MemberReferenceExpression node, Void data)
     {
       super.visitMemberReferenceExpression(node, data);
       
       if ((node.getTarget() instanceof ThisReferenceExpression)) {
         ThisReferenceExpression thisReference = (ThisReferenceExpression)node.getTarget();
         Expression target = thisReference.getTarget();
         
         if ((target == null) || (target.isNull())) {
           MemberReference member = (MemberReference)node.getUserData(Keys.MEMBER_REFERENCE);
           
           if ((member == null) && ((node.getParent() instanceof InvocationExpression))) {
             member = (MemberReference)node.getParent().getUserData(Keys.MEMBER_REFERENCE);
           }
           
           if ((member != null) && (MetadataHelper.isEnclosedBy(this._innerClass, member.getDeclaringType())))
           {
 
             AstBuilder astBuilder = (AstBuilder)this.context.getUserData(Keys.AST_BUILDER);
             
             if (astBuilder != null) {
               thisReference.setTarget(new TypeReferenceExpression(thisReference.getOffset(), astBuilder.convertType(member.getDeclaringType(), LocalClassHelper.OUTER_TYPE_CONVERT_OPTIONS)));
             }
           }
         }
       }
       
 
 
 
 
 
 
 
 
       return null;
     }
   }
   
   private static final class IntroduceInitializersVisitor extends ContextTrackingVisitor<Void> {
     public IntroduceInitializersVisitor(DecompilerContext context) {
       super();
     }
     
     public Void visitSuperReferenceExpression(SuperReferenceExpression node, Void _)
     {
       super.visitSuperReferenceExpression(node, _);
       
       if ((this.context.getCurrentMethod() != null) && (this.context.getCurrentMethod().isConstructor()) && (this.context.getCurrentMethod().getDeclaringType().isAnonymous()) && ((node.getParent() instanceof InvocationExpression)) && (node.getRole() == Roles.TARGET_EXPRESSION))
       {
 
 
 
 
 
 
 
 
 
         Statement parentStatement = (Statement)CollectionUtilities.firstOrDefault(node.getAncestors(Statement.class));
         ConstructorDeclaration constructor = (ConstructorDeclaration)CollectionUtilities.firstOrDefault(node.getAncestors(ConstructorDeclaration.class));
         
         if ((parentStatement == null) || (constructor == null) || (constructor.getParent() == null) || (parentStatement.getNextStatement() == null))
         {
 
 
 
           return null;
         }
         
         InstanceInitializer initializer = new InstanceInitializer();
         BlockStatement initializerBody = new BlockStatement();
         
         for (Statement current = parentStatement.getNextStatement(); current != null;) {
           Statement next = current.getNextStatement();
           
           current.remove();
           initializerBody.addChild(current, current.getRole());
           current = next;
         }
         
         initializer.setBody(initializerBody);
         constructor.getParent().insertChildAfter(constructor, initializer, Roles.TYPE_MEMBER);
       }
       
       return null;
     }
   }
 }


