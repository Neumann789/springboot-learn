 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.Pattern;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class DepthFirstAstVisitor<T, S>
   implements IAstVisitor<T, S>
 {
   protected S visitChildren(AstNode node, T data)
   {
     AstNode next;
     for (AstNode child = node.getFirstChild(); child != null; child = next)
     {
 
 
       next = child.getNextSibling();
       child.acceptVisitor(this, data);
     }
     
     return null;
   }
   
   public S visitComment(Comment node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitPatternPlaceholder(AstNode node, Pattern pattern, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitInvocationExpression(InvocationExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitTypeReference(TypeReferenceExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitJavaTokenNode(JavaTokenNode node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitMemberReferenceExpression(MemberReferenceExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitIdentifier(Identifier node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitNullReferenceExpression(NullReferenceExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitThisReferenceExpression(ThisReferenceExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitSuperReferenceExpression(SuperReferenceExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitClassOfExpression(ClassOfExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitBlockStatement(BlockStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitExpressionStatement(ExpressionStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitBreakStatement(BreakStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitContinueStatement(ContinueStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitDoWhileStatement(DoWhileStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitEmptyStatement(EmptyStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitIfElseStatement(IfElseStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitLabelStatement(LabelStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitLabeledStatement(LabeledStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitReturnStatement(ReturnStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitSwitchStatement(SwitchStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitSwitchSection(SwitchSection node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitCaseLabel(CaseLabel node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitThrowStatement(ThrowStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitCatchClause(CatchClause node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitAnnotation(Annotation node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitNewLine(NewLineNode node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitVariableDeclaration(VariableDeclarationStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitVariableInitializer(VariableInitializer node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitText(TextNode node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitImportDeclaration(ImportDeclaration node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitSimpleType(SimpleType node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitMethodDeclaration(MethodDeclaration node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitInitializerBlock(InstanceInitializer node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitConstructorDeclaration(ConstructorDeclaration node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitTypeParameterDeclaration(TypeParameterDeclaration node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitParameterDeclaration(ParameterDeclaration node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitFieldDeclaration(FieldDeclaration node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitTypeDeclaration(TypeDeclaration node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitCompilationUnit(CompilationUnit node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitPackageDeclaration(PackageDeclaration node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitArraySpecifier(ArraySpecifier node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitComposedType(ComposedType node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitWhileStatement(WhileStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitPrimitiveExpression(PrimitiveExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitCastExpression(CastExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitBinaryOperatorExpression(BinaryOperatorExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitInstanceOfExpression(InstanceOfExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitIndexerExpression(IndexerExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitIdentifierExpression(IdentifierExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitUnaryOperatorExpression(UnaryOperatorExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitConditionalExpression(ConditionalExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitArrayInitializerExpression(ArrayInitializerExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitObjectCreationExpression(ObjectCreationExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitArrayCreationExpression(ArrayCreationExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitAssignmentExpression(AssignmentExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitForStatement(ForStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitForEachStatement(ForEachStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitGotoStatement(GotoStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitParenthesizedExpression(ParenthesizedExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitSynchronizedStatement(SynchronizedStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitAnonymousObjectCreationExpression(AnonymousObjectCreationExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitWildcardType(WildcardType node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitMethodGroupExpression(MethodGroupExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitEnumValueDeclaration(EnumValueDeclaration node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitAssertStatement(AssertStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitLambdaExpression(LambdaExpression node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitLocalTypeDeclarationStatement(LocalTypeDeclarationStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
   
   public S visitTryCatchStatement(TryCatchStatement node, T data)
   {
     return (S)visitChildren(node, data);
   }
 }


