 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.FieldDefinition;
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AnonymousObjectCreationExpression;
 import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
 import com.strobel.decompiler.languages.java.ast.AssignmentOperatorType;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.ConstructorDeclaration;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.ExpressionStatement;
 import com.strobel.decompiler.languages.java.ast.FieldDeclaration;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
 import com.strobel.decompiler.languages.java.ast.Roles;
 import com.strobel.decompiler.languages.java.ast.Statement;
 import com.strobel.decompiler.languages.java.ast.SuperReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.TypeReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.VariableInitializer;
 import com.strobel.decompiler.patterns.AnyNode;
 import com.strobel.decompiler.patterns.Choice;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.MemberReferenceTypeNode;
 import com.strobel.decompiler.patterns.TypedNode;
 import java.util.HashMap;
 import java.util.Map;
 
 public class IntroduceInitializersTransform extends ContextTrackingVisitor<Void>
 {
   private final Map<String, FieldDeclaration> _fieldDeclarations;
   private final Map<String, AssignmentExpression> _initializers;
   private MethodDefinition _currentInitializerMethod;
   private MethodDefinition _currentConstructor;
   
   public IntroduceInitializersTransform(DecompilerContext context)
   {
     super(context);
     
     this._fieldDeclarations = new HashMap();
     this._initializers = new HashMap();
   }
   
   public void run(AstNode compilationUnit)
   {
     new ContextTrackingVisitor(this.context)
     {
       public Void visitFieldDeclaration(FieldDeclaration node, Void _) {
         FieldDefinition field = (FieldDefinition)node.getUserData(Keys.FIELD_DEFINITION);
         
         if (field != null) {
           IntroduceInitializersTransform.this._fieldDeclarations.put(field.getFullName(), node);
         }
         
         return (Void)super.visitFieldDeclaration(node, _); } }.run(compilationUnit);
     
 
 
     super.run(compilationUnit);
     
     inlineInitializers();
     
     com.strobel.decompiler.languages.java.ast.LocalClassHelper.introduceInitializerBlocks(this.context, compilationUnit);
   }
   
   private void inlineInitializers() {
     for (String fieldName : this._initializers.keySet()) {
       FieldDeclaration declaration = (FieldDeclaration)this._fieldDeclarations.get(fieldName);
       
       if ((declaration != null) && (((VariableInitializer)declaration.getVariables().firstOrNullObject()).getInitializer().isNull()))
       {
 
         AssignmentExpression assignment = (AssignmentExpression)this._initializers.get(fieldName);
         Expression value = assignment.getRight();
         
         value.remove();
         ((VariableInitializer)declaration.getVariables().firstOrNullObject()).setInitializer(value);
         
         AstNode parent = assignment.getParent();
         
         if ((parent instanceof ExpressionStatement)) {
           parent.remove();
         }
         else if (parent.getRole() == Roles.VARIABLE) {
           Expression left = assignment.getLeft();
           
           left.remove();
           assignment.replaceWith(left);
         }
         else {
           Expression left = assignment.getLeft();
           
           left.remove();
           parent.replaceWith(left);
         }
       }
     }
   }
   
   public Void visitAnonymousObjectCreationExpression(AnonymousObjectCreationExpression node, Void data)
   {
     MethodDefinition oldInitializer = this._currentInitializerMethod;
     MethodDefinition oldConstructor = this._currentConstructor;
     
     this._currentInitializerMethod = null;
     this._currentConstructor = null;
     try
     {
       return (Void)super.visitAnonymousObjectCreationExpression(node, data);
     }
     finally {
       this._currentInitializerMethod = oldInitializer;
       this._currentConstructor = oldConstructor;
     }
   }
   
   public Void visitMethodDeclaration(MethodDeclaration node, Void _)
   {
     MethodDefinition oldInitializer = this._currentInitializerMethod;
     MethodDefinition oldConstructor = this._currentConstructor;
     
     MethodDefinition method = (MethodDefinition)node.getUserData(Keys.METHOD_DEFINITION);
     
     if ((method != null) && (method.isTypeInitializer())) {
       this._currentConstructor = null;
       this._currentInitializerMethod = method;
     }
     else {
       this._currentConstructor = ((method != null) && (method.isConstructor()) ? method : null);
       this._currentInitializerMethod = null;
     }
     try
     {
       return (Void)super.visitMethodDeclaration(node, _);
     }
     finally {
       this._currentConstructor = oldConstructor;
       this._currentInitializerMethod = oldInitializer;
     }
   }
   
 
 
 
   private static final INode FIELD_ASSIGNMENT = new AssignmentExpression(new MemberReferenceTypeNode("target", new Choice(new INode[] { new com.strobel.decompiler.languages.java.ast.MemberReferenceExpression(-34, new Choice(new INode[] { new TypedNode(TypeReferenceExpression.class), new TypedNode(com.strobel.decompiler.languages.java.ast.ThisReferenceExpression.class) }).toExpression(), "$any$", new com.strobel.decompiler.languages.java.ast.AstType[0]), new com.strobel.decompiler.languages.java.ast.IdentifierExpression(-34, "$any$") }).toExpression(), FieldReference.class).toExpression(), AssignmentOperatorType.ASSIGN, new AnyNode("value").toExpression());
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public Void visitAssignmentExpression(AssignmentExpression node, Void data)
   {
     super.visitAssignmentExpression(node, data);
     
     if (!(node.getParent() instanceof Statement))
     {
 
 
       return null;
     }
     
     if (((this._currentInitializerMethod == null) && (this._currentConstructor == null)) || (this.context.getCurrentType() == null)) {
       return null;
     }
     
     Match match = FIELD_ASSIGNMENT.match(node);
     
     if (match.success()) {
       Expression target = (Expression)CollectionUtilities.firstOrDefault(match.get("target"));
       FieldReference reference = (FieldReference)target.getUserData(Keys.MEMBER_REFERENCE);
       FieldDefinition definition = reference.resolve();
       
 
 
 
 
       if ((definition != null) && (definition.isFinal()) && (definition.getConstantValue() != null)) {
         node.getParent().remove();
         return null;
       }
       
 
 
 
 
       if ((this._currentInitializerMethod != null) && (this._currentInitializerMethod.getDeclaringType().isInterface()) && (StringUtilities.equals(this.context.getCurrentType().getInternalName(), reference.getDeclaringType().getInternalName())))
       {
 
 
         this._initializers.put(reference.getFullName(), node);
       }
     }
     
     return null;
   }
   
   public Void visitSuperReferenceExpression(SuperReferenceExpression node, Void _)
   {
     super.visitSuperReferenceExpression(node, _);
     
     MethodDefinition method = this.context.getCurrentMethod();
     
     if ((method != null) && (method.isConstructor()) && ((method.isSynthetic()) || (method.getDeclaringType().isAnonymous())) && ((node.getParent() instanceof com.strobel.decompiler.languages.java.ast.InvocationExpression)) && (node.getRole() == Roles.TARGET_EXPRESSION))
     {
 
 
 
 
 
 
 
 
 
       Statement parentStatement = (Statement)CollectionUtilities.firstOrDefault(node.getAncestors(Statement.class));
       ConstructorDeclaration constructor = (ConstructorDeclaration)CollectionUtilities.firstOrDefault(node.getAncestors(ConstructorDeclaration.class));
       
       if ((parentStatement == null) || (constructor == null) || (constructor.getParent() == null) || (parentStatement.getNextStatement() == null))
       {
 
 
 
         return null;
       }
       
       Statement current = parentStatement.getNextStatement();
       while ((current instanceof ExpressionStatement))
       {
         Statement next = current.getNextStatement();
         Expression expression = ((ExpressionStatement)current).getExpression();
         Match match = FIELD_ASSIGNMENT.match(expression);
         
         if (!match.success()) break;
         Expression target = (Expression)CollectionUtilities.firstOrDefault(match.get("target"));
         MemberReference reference = (MemberReference)target.getUserData(Keys.MEMBER_REFERENCE);
         
         if (StringUtilities.equals(this.context.getCurrentType().getInternalName(), reference.getDeclaringType().getInternalName())) {
           this._initializers.put(reference.getFullName(), (AssignmentExpression)expression);
         }
         
 
 
 
 
 
 
 
         current = next;
       }
     }
     
     return null;
   }
 }


