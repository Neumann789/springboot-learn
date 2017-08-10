 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.FieldDefinition;
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.Predicate;
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.DecompilerSettings;
 import com.strobel.decompiler.languages.java.ast.AstBuilder;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.BlockStatement;
 import com.strobel.decompiler.languages.java.ast.CompilationUnit;
 import com.strobel.decompiler.languages.java.ast.ConstructorDeclaration;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.ExpressionStatement;
 import com.strobel.decompiler.languages.java.ast.FieldDeclaration;
 import com.strobel.decompiler.languages.java.ast.InvocationExpression;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
 import com.strobel.decompiler.languages.java.ast.Statement;
 import com.strobel.decompiler.languages.java.ast.SuperReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.TypeDeclaration;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.NamedNode;
 
 public class RemoveHiddenMembersTransform extends ContextTrackingVisitor<Void>
 {
   public RemoveHiddenMembersTransform(DecompilerContext context)
   {
     super(context);
   }
   
   public Void visitTypeDeclaration(TypeDeclaration node, Void _)
   {
     if (!(node.getParent() instanceof CompilationUnit)) {
       TypeDefinition type = (TypeDefinition)node.getUserData(Keys.TYPE_DEFINITION);
       
       if ((type != null) && (AstBuilder.isMemberHidden(type, this.context))) {
         node.remove();
         return null;
       }
     }
     
     return (Void)super.visitTypeDeclaration(node, _);
   }
   
   public Void visitFieldDeclaration(FieldDeclaration node, Void data)
   {
     FieldDefinition field = (FieldDefinition)node.getUserData(Keys.FIELD_DEFINITION);
     
     if ((field != null) && (AstBuilder.isMemberHidden(field, this.context))) {
       node.remove();
       return null;
     }
     
     return (Void)super.visitFieldDeclaration(node, data);
   }
   
   public Void visitMethodDeclaration(MethodDeclaration node, Void _)
   {
     MethodDefinition method = (MethodDefinition)node.getUserData(Keys.METHOD_DEFINITION);
     
     if (method != null) {
       if (AstBuilder.isMemberHidden(method, this.context)) {
         node.remove();
         return null;
       }
       
       if ((method.isTypeInitializer()) && 
         (node.getBody().getStatements().isEmpty())) {
         node.remove();
         return null;
       }
     }
     
 
     return (Void)super.visitMethodDeclaration(node, _);
   }
   
 
 
 
 
   private static final INode DEFAULT_CONSTRUCTOR_BODY = new BlockStatement(new Statement[] { new ExpressionStatement(new InvocationExpression(-34, new SuperReferenceExpression(-34), new Expression[0])) });
   
 
 
 
 
 
 
 
   private static final AstNode EMPTY_SUPER = new ExpressionStatement(new NamedNode("target", new SuperReferenceExpression(-34).invoke(new Expression[0])).toExpression());
   
 
 
 
 
 
 
   public Void visitConstructorDeclaration(ConstructorDeclaration node, Void _)
   {
     final MethodDefinition method = (MethodDefinition)node.getUserData(Keys.METHOD_DEFINITION);
     
     if (method != null) {
       if (AstBuilder.isMemberHidden(method, this.context)) {
         if ((method.getDeclaringType().isEnum()) && (method.getDeclaringType().isAnonymous()) && (!node.getBody().getStatements().isEmpty()))
         {
 
 
 
 
 
           return (Void)super.visitConstructorDeclaration(node, _);
         }
         
         node.remove();
         return null;
       }
       
       if ((!this.context.getSettings().getShowSyntheticMembers()) && (node.getParameters().isEmpty()) && (DEFAULT_CONSTRUCTOR_BODY.matches(node.getBody())))
       {
 
 
 
 
 
 
         TypeDefinition declaringType = method.getDeclaringType();
         
         if (declaringType != null) {
           boolean hasOtherConstructors = CollectionUtilities.any(declaringType.getDeclaredMethods(), new Predicate()
           {
 
             public boolean test(MethodDefinition m)
             {
               return (m.isConstructor()) && (!m.isSynthetic()) && (!StringUtilities.equals(m.getErasedSignature(), method.getErasedSignature()));
             }
           });
           
 
 
 
           if (!hasOtherConstructors) {
             node.remove();
             return null;
           }
         }
       }
     }
     
     return (Void)super.visitConstructorDeclaration(node, _);
   }
   
   public Void visitExpressionStatement(ExpressionStatement node, Void data)
   {
     super.visitExpressionStatement(node, data);
     
 
 
 
 
 
     if ((inConstructor()) && (!this.context.getSettings().getShowSyntheticMembers())) {
       Match match = EMPTY_SUPER.match(node);
       
       if (match.success()) {
         AstNode target = (AstNode)CollectionUtilities.firstOrDefault(match.get("target"));
         MemberReference member = (MemberReference)target.getUserData(Keys.MEMBER_REFERENCE);
         
         if (((member instanceof MethodReference)) && (((MethodReference)member).isConstructor())) {
           node.remove();
         }
       }
     }
     
     return null;
   }
 }


