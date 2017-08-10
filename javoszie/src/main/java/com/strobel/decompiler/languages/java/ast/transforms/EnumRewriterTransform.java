 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.FieldDefinition;
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AstBuilder;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.BlockStatement;
 import com.strobel.decompiler.languages.java.ast.ConstructorDeclaration;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.EnumValueDeclaration;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.FieldDeclaration;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
 import com.strobel.decompiler.languages.java.ast.ObjectCreationExpression;
 import com.strobel.decompiler.languages.java.ast.Statement;
 import com.strobel.decompiler.languages.java.ast.TypeDeclaration;
 import com.strobel.decompiler.languages.java.ast.TypeReferenceExpression;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.NamedNode;
 import com.strobel.decompiler.patterns.Repeat;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 
 public class EnumRewriterTransform implements IAstTransform
 {
   private final DecompilerContext _context;
   
   public EnumRewriterTransform(DecompilerContext context)
   {
     this._context = ((DecompilerContext)com.strobel.core.VerifyArgument.notNull(context, "context"));
   }
   
   public void run(AstNode compilationUnit)
   {
     compilationUnit.acceptVisitor(new Visitor(this._context), null);
   }
   
   private static final class Visitor extends ContextTrackingVisitor<Void> {
     private Map<String, FieldDeclaration> _valueFields = new LinkedHashMap();
     private Map<String, ObjectCreationExpression> _valueInitializers = new LinkedHashMap();
     private MemberReference _valuesField;
     
     protected Visitor(DecompilerContext context) {
       super();
     }
     
     public Void visitTypeDeclaration(TypeDeclaration typeDeclaration, Void _)
     {
       MemberReference oldValuesField = this._valuesField;
       Map<String, FieldDeclaration> oldValueFields = this._valueFields;
       Map<String, ObjectCreationExpression> oldValueInitializers = this._valueInitializers;
       
       LinkedHashMap<String, FieldDeclaration> valueFields = new LinkedHashMap();
       LinkedHashMap<String, ObjectCreationExpression> valueInitializers = new LinkedHashMap();
       
       this._valuesField = findValuesField(typeDeclaration);
       this._valueFields = valueFields;
       this._valueInitializers = valueInitializers;
       try
       {
         super.visitTypeDeclaration(typeDeclaration, _);
       }
       finally {
         this._valuesField = oldValuesField;
         this._valueFields = oldValueFields;
         this._valueInitializers = oldValueInitializers;
       }
       
       rewrite(valueFields, valueInitializers);
       
       return null;
     }
     
     private MemberReference findValuesField(TypeDeclaration declaration) {
       TypeDefinition definition = (TypeDefinition)declaration.getUserData(Keys.TYPE_DEFINITION);
       
       if ((definition == null) || (!definition.isEnum())) {
         return null;
       }
       
       AstBuilder astBuilder = (AstBuilder)this.context.getUserData(Keys.AST_BUILDER);
       
       if (astBuilder == null) {
         return null;
       }
       
       MethodDeclaration pattern = new MethodDeclaration();
       
       pattern.setName("values");
       pattern.setReturnType(astBuilder.convertType(definition.makeArrayType()));
       pattern.getModifiers().add(new com.strobel.decompiler.languages.java.ast.JavaModifierToken(javax.lang.model.element.Modifier.PUBLIC));
       pattern.getModifiers().add(new com.strobel.decompiler.languages.java.ast.JavaModifierToken(javax.lang.model.element.Modifier.STATIC));
       pattern.setBody(new BlockStatement(new Statement[] { new com.strobel.decompiler.languages.java.ast.ReturnStatement(-34, new com.strobel.decompiler.patterns.Choice(new com.strobel.decompiler.patterns.INode[] { new MemberReferenceExpression(-34, new NamedNode("valuesField", new TypeReferenceExpression(-34, astBuilder.convertType(definition)).member("$any$")).toExpression(), "clone", new com.strobel.decompiler.languages.java.ast.AstType[0]).invoke(new Expression[0]), new com.strobel.decompiler.languages.java.ast.CastExpression(astBuilder.convertType(definition.makeArrayType()), new MemberReferenceExpression(-34, new NamedNode("valuesField", new TypeReferenceExpression(-34, astBuilder.convertType(definition)).member("$any$")).toExpression(), "clone", new com.strobel.decompiler.languages.java.ast.AstType[0]).invoke(new Expression[0])) }).toExpression()) }));
       
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
       for (com.strobel.decompiler.languages.java.ast.EntityDeclaration d : declaration.getMembers()) {
         if ((d instanceof MethodDeclaration)) {
           Match match = pattern.match(d);
           
           if (match.success()) {
             MemberReferenceExpression reference = (MemberReferenceExpression)com.strobel.core.CollectionUtilities.firstOrDefault(match.get("valuesField"));
             return (MemberReference)reference.getUserData(Keys.MEMBER_REFERENCE);
           }
         }
       }
       
       return null;
     }
     
     public Void visitFieldDeclaration(FieldDeclaration node, Void data)
     {
       TypeDefinition currentType = this.context.getCurrentType();
       
       if ((currentType != null) && (currentType.isEnum())) {
         FieldDefinition field = (FieldDefinition)node.getUserData(Keys.FIELD_DEFINITION);
         
         if ((field != null) && 
           (field.isEnumConstant())) {
           this._valueFields.put(field.getName(), node);
         }
       }
       
 
       return (Void)super.visitFieldDeclaration(node, data);
     }
     
     public Void visitAssignmentExpression(com.strobel.decompiler.languages.java.ast.AssignmentExpression node, Void data)
     {
       TypeDefinition currentType = this.context.getCurrentType();
       MethodDefinition currentMethod = this.context.getCurrentMethod();
       
       if ((currentType != null) && (currentMethod != null) && (currentType.isEnum()) && (currentMethod.isTypeInitializer()))
       {
 
 
 
         Expression left = node.getLeft();
         Expression right = node.getRight();
         
         MemberReference member = (MemberReference)left.getUserData(Keys.MEMBER_REFERENCE);
         
         if ((member instanceof com.strobel.assembler.metadata.FieldReference)) {
           FieldDefinition resolvedField = ((com.strobel.assembler.metadata.FieldReference)member).resolve();
           
           if ((resolvedField != null) && (((right instanceof ObjectCreationExpression)) || ((right instanceof com.strobel.decompiler.languages.java.ast.ArrayCreationExpression))))
           {
 
 
             String fieldName = resolvedField.getName();
             
             if ((resolvedField.isEnumConstant()) && ((right instanceof ObjectCreationExpression)) && (com.strobel.assembler.metadata.MetadataResolver.areEquivalent(currentType, resolvedField.getFieldType())))
             {
 
 
               this._valueInitializers.put(fieldName, (ObjectCreationExpression)right);
             }
             else if ((resolvedField.isSynthetic()) && (!this.context.getSettings().getShowSyntheticMembers()) && (matchesValuesField(resolvedField)) && (com.strobel.assembler.metadata.MetadataResolver.areEquivalent(currentType.makeArrayType(), resolvedField.getFieldType())))
             {
 
 
 
               Statement parentStatement = findStatement(node);
               
               if (parentStatement != null) {
                 parentStatement.remove();
               }
             }
           }
         }
       }
       
       return (Void)super.visitAssignmentExpression(node, data);
     }
     
     private static final com.strobel.decompiler.patterns.INode SUPER_PATTERN = new com.strobel.decompiler.patterns.SubtreeMatch(new BlockStatement(new Statement[] { new Repeat(new com.strobel.decompiler.patterns.TypedNode(com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement.class)).toStatement(), new NamedNode("superCall", new com.strobel.decompiler.languages.java.ast.ExpressionStatement(new com.strobel.decompiler.languages.java.ast.InvocationExpression(-34, new com.strobel.decompiler.languages.java.ast.SuperReferenceExpression(-34), new Expression[] { new Repeat(new com.strobel.decompiler.patterns.AnyNode()).toExpression() }))).toStatement(), new Repeat(new com.strobel.decompiler.patterns.AnyNode()).toStatement() }));
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     public Void visitConstructorDeclaration(ConstructorDeclaration node, Void _)
     {
       TypeDefinition currentType = this.context.getCurrentType();
       MethodDefinition constructor = (MethodDefinition)node.getUserData(Keys.METHOD_DEFINITION);
       
       if ((currentType != null) && (currentType.isEnum())) {
         List<ParameterDefinition> pDefinitions = constructor.getParameters();
         AstNodeCollection<com.strobel.decompiler.languages.java.ast.ParameterDeclaration> pDeclarations = node.getParameters();
         
         for (int i = 0; (i < pDefinitions.size()) && (!pDeclarations.isEmpty()) && (((ParameterDefinition)pDefinitions.get(i)).isSynthetic()); i++) {
           ((com.strobel.decompiler.languages.java.ast.ParameterDeclaration)pDeclarations.firstOrNullObject()).remove();
         }
         
         BlockStatement body = node.getBody();
         Match superCallMatch = SUPER_PATTERN.match(body);
         AstNodeCollection<Statement> statements = body.getStatements();
         
         if (superCallMatch.success()) {
           Statement superCall = (Statement)com.strobel.core.CollectionUtilities.first(superCallMatch.get("superCall"));
           superCall.remove();
         }
         
         if (statements.isEmpty()) {
           if (pDeclarations.isEmpty()) {
             node.remove();
           }
         }
         else if (currentType.isAnonymous()) {
           com.strobel.decompiler.languages.java.ast.InstanceInitializer initializer = new com.strobel.decompiler.languages.java.ast.InstanceInitializer();
           BlockStatement initializerBody = new BlockStatement();
           
           for (Statement statement : statements) {
             statement.remove();
             initializerBody.add(statement);
           }
           
           initializer.setBody(initializerBody);
           node.replaceWith(initializer);
         }
       }
       
       return (Void)super.visitConstructorDeclaration(node, _);
     }
     
     public Void visitMethodDeclaration(MethodDeclaration node, Void _)
     {
       TypeDefinition currentType = this.context.getCurrentType();
       
       if ((currentType != null) && (currentType.isEnum()) && (!this.context.getSettings().getShowSyntheticMembers())) {
         MethodDefinition method = (MethodDefinition)node.getUserData(Keys.METHOD_DEFINITION);
         
         if ((method != null) && (method.isPublic()) && (method.isStatic()))
         {
 
 
           switch (method.getName()) {
           case "values": 
             if ((method.getParameters().isEmpty()) && (com.strobel.assembler.metadata.MetadataResolver.areEquivalent(currentType.makeArrayType(), method.getReturnType())))
             {
 
               node.remove();
             }
             
 
             break;
           case "valueOf": 
             if ((currentType.equals(method.getReturnType().resolve())) && (method.getParameters().size() == 1))
             {
 
               ParameterDefinition p = (ParameterDefinition)method.getParameters().get(0);
               
               if ("java/lang/String".equals(p.getParameterType().getInternalName())) {
                 node.remove();
               }
             }
             
             break;
           }
           
         }
       }
       return (Void)super.visitMethodDeclaration(node, _);
     }
     
 
 
 
 
     private void rewrite(LinkedHashMap<String, FieldDeclaration> valueFields, LinkedHashMap<String, ObjectCreationExpression> valueInitializers)
     {
       if ((valueFields.isEmpty()) || (valueFields.size() != valueInitializers.size())) {
         return;
       }
       
       MethodDeclaration typeInitializer = findMethodDeclaration((AstNode)com.strobel.core.CollectionUtilities.first(valueInitializers.values()));
       
       for (String name : valueFields.keySet()) {
         FieldDeclaration field = (FieldDeclaration)valueFields.get(name);
         ObjectCreationExpression initializer = (ObjectCreationExpression)valueInitializers.get(name);
         
         assert ((field != null) && (initializer != null));
         
         com.strobel.assembler.metadata.MethodReference constructor = (com.strobel.assembler.metadata.MethodReference)initializer.getUserData(Keys.MEMBER_REFERENCE);
         MethodDefinition resolvedConstructor = constructor.resolve();
         
         EnumValueDeclaration enumDeclaration = new EnumValueDeclaration();
         Statement initializerStatement = findStatement(initializer);
         
         assert (initializerStatement != null);
         
         initializerStatement.remove();
         
         enumDeclaration.setName(name);
         enumDeclaration.putUserData(Keys.FIELD_DEFINITION, field.getUserData(Keys.FIELD_DEFINITION));
         enumDeclaration.putUserData(Keys.MEMBER_REFERENCE, field.getUserData(Keys.MEMBER_REFERENCE));
         
         if (resolvedConstructor != null) {
           enumDeclaration.putUserData(Keys.TYPE_DEFINITION, resolvedConstructor.getDeclaringType());
         }
         
         int i = 0;
         
         AstNodeCollection<Expression> arguments = initializer.getArguments();
         boolean trimArguments = arguments.size() == constructor.getParameters().size();
         
         for (Expression argument : arguments) {
           if ((!trimArguments) || (resolvedConstructor == null) || (!resolvedConstructor.isSynthetic()) || (i++ >= 2))
           {
 
 
             argument.remove();
             enumDeclaration.getArguments().add(argument);
           }
         }
         if ((initializer instanceof com.strobel.decompiler.languages.java.ast.AnonymousObjectCreationExpression)) {
           com.strobel.decompiler.languages.java.ast.AnonymousObjectCreationExpression creation = (com.strobel.decompiler.languages.java.ast.AnonymousObjectCreationExpression)initializer;
           
           for (com.strobel.decompiler.languages.java.ast.EntityDeclaration member : creation.getTypeDeclaration().getMembers()) {
             member.remove();
             enumDeclaration.getMembers().add(member);
           }
         }
         
         field.replaceWith(enumDeclaration);
       }
       
       if ((typeInitializer != null) && (typeInitializer.getBody().getStatements().isEmpty())) {
         typeInitializer.remove();
       }
     }
     
     private Statement findStatement(AstNode node) {
       for (AstNode current = node; current != null; current = current.getParent()) {
         if ((current instanceof Statement)) {
           return (Statement)current;
         }
       }
       return null;
     }
     
     private MethodDeclaration findMethodDeclaration(AstNode node) {
       for (AstNode current = node; current != null; current = current.getParent()) {
         if ((current instanceof MethodDeclaration)) {
           return (MethodDeclaration)current;
         }
       }
       return null;
     }
     
     private boolean matchesValuesField(FieldDefinition field) {
       if (field == null) {
         return false;
       }
       
       if (field.isEquivalentTo(this._valuesField)) {
         return true;
       }
       
       String fieldName = field.getName();
       
       return (com.strobel.core.StringUtilities.equals(fieldName, "$VALUES")) || (com.strobel.core.StringUtilities.equals(fieldName, "ENUM$VALUES"));
     }
   }
 }


