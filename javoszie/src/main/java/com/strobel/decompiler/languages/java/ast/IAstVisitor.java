package com.strobel.decompiler.languages.java.ast;

import com.strobel.decompiler.patterns.Pattern;

public abstract interface IAstVisitor<T, R>
{
  public abstract R visitComment(Comment paramComment, T paramT);
  
  public abstract R visitPatternPlaceholder(AstNode paramAstNode, Pattern paramPattern, T paramT);
  
  public abstract R visitInvocationExpression(InvocationExpression paramInvocationExpression, T paramT);
  
  public abstract R visitTypeReference(TypeReferenceExpression paramTypeReferenceExpression, T paramT);
  
  public abstract R visitJavaTokenNode(JavaTokenNode paramJavaTokenNode, T paramT);
  
  public abstract R visitMemberReferenceExpression(MemberReferenceExpression paramMemberReferenceExpression, T paramT);
  
  public abstract R visitIdentifier(Identifier paramIdentifier, T paramT);
  
  public abstract R visitNullReferenceExpression(NullReferenceExpression paramNullReferenceExpression, T paramT);
  
  public abstract R visitThisReferenceExpression(ThisReferenceExpression paramThisReferenceExpression, T paramT);
  
  public abstract R visitSuperReferenceExpression(SuperReferenceExpression paramSuperReferenceExpression, T paramT);
  
  public abstract R visitClassOfExpression(ClassOfExpression paramClassOfExpression, T paramT);
  
  public abstract R visitBlockStatement(BlockStatement paramBlockStatement, T paramT);
  
  public abstract R visitExpressionStatement(ExpressionStatement paramExpressionStatement, T paramT);
  
  public abstract R visitBreakStatement(BreakStatement paramBreakStatement, T paramT);
  
  public abstract R visitContinueStatement(ContinueStatement paramContinueStatement, T paramT);
  
  public abstract R visitDoWhileStatement(DoWhileStatement paramDoWhileStatement, T paramT);
  
  public abstract R visitEmptyStatement(EmptyStatement paramEmptyStatement, T paramT);
  
  public abstract R visitIfElseStatement(IfElseStatement paramIfElseStatement, T paramT);
  
  public abstract R visitLabelStatement(LabelStatement paramLabelStatement, T paramT);
  
  public abstract R visitLabeledStatement(LabeledStatement paramLabeledStatement, T paramT);
  
  public abstract R visitReturnStatement(ReturnStatement paramReturnStatement, T paramT);
  
  public abstract R visitSwitchStatement(SwitchStatement paramSwitchStatement, T paramT);
  
  public abstract R visitSwitchSection(SwitchSection paramSwitchSection, T paramT);
  
  public abstract R visitCaseLabel(CaseLabel paramCaseLabel, T paramT);
  
  public abstract R visitThrowStatement(ThrowStatement paramThrowStatement, T paramT);
  
  public abstract R visitCatchClause(CatchClause paramCatchClause, T paramT);
  
  public abstract R visitAnnotation(Annotation paramAnnotation, T paramT);
  
  public abstract R visitNewLine(NewLineNode paramNewLineNode, T paramT);
  
  public abstract R visitVariableDeclaration(VariableDeclarationStatement paramVariableDeclarationStatement, T paramT);
  
  public abstract R visitVariableInitializer(VariableInitializer paramVariableInitializer, T paramT);
  
  public abstract R visitText(TextNode paramTextNode, T paramT);
  
  public abstract R visitImportDeclaration(ImportDeclaration paramImportDeclaration, T paramT);
  
  public abstract R visitSimpleType(SimpleType paramSimpleType, T paramT);
  
  public abstract R visitMethodDeclaration(MethodDeclaration paramMethodDeclaration, T paramT);
  
  public abstract R visitInitializerBlock(InstanceInitializer paramInstanceInitializer, T paramT);
  
  public abstract R visitConstructorDeclaration(ConstructorDeclaration paramConstructorDeclaration, T paramT);
  
  public abstract R visitTypeParameterDeclaration(TypeParameterDeclaration paramTypeParameterDeclaration, T paramT);
  
  public abstract R visitParameterDeclaration(ParameterDeclaration paramParameterDeclaration, T paramT);
  
  public abstract R visitFieldDeclaration(FieldDeclaration paramFieldDeclaration, T paramT);
  
  public abstract R visitTypeDeclaration(TypeDeclaration paramTypeDeclaration, T paramT);
  
  public abstract R visitCompilationUnit(CompilationUnit paramCompilationUnit, T paramT);
  
  public abstract R visitPackageDeclaration(PackageDeclaration paramPackageDeclaration, T paramT);
  
  public abstract R visitArraySpecifier(ArraySpecifier paramArraySpecifier, T paramT);
  
  public abstract R visitComposedType(ComposedType paramComposedType, T paramT);
  
  public abstract R visitWhileStatement(WhileStatement paramWhileStatement, T paramT);
  
  public abstract R visitPrimitiveExpression(PrimitiveExpression paramPrimitiveExpression, T paramT);
  
  public abstract R visitCastExpression(CastExpression paramCastExpression, T paramT);
  
  public abstract R visitBinaryOperatorExpression(BinaryOperatorExpression paramBinaryOperatorExpression, T paramT);
  
  public abstract R visitInstanceOfExpression(InstanceOfExpression paramInstanceOfExpression, T paramT);
  
  public abstract R visitIndexerExpression(IndexerExpression paramIndexerExpression, T paramT);
  
  public abstract R visitIdentifierExpression(IdentifierExpression paramIdentifierExpression, T paramT);
  
  public abstract R visitUnaryOperatorExpression(UnaryOperatorExpression paramUnaryOperatorExpression, T paramT);
  
  public abstract R visitConditionalExpression(ConditionalExpression paramConditionalExpression, T paramT);
  
  public abstract R visitArrayInitializerExpression(ArrayInitializerExpression paramArrayInitializerExpression, T paramT);
  
  public abstract R visitObjectCreationExpression(ObjectCreationExpression paramObjectCreationExpression, T paramT);
  
  public abstract R visitArrayCreationExpression(ArrayCreationExpression paramArrayCreationExpression, T paramT);
  
  public abstract R visitAssignmentExpression(AssignmentExpression paramAssignmentExpression, T paramT);
  
  public abstract R visitForStatement(ForStatement paramForStatement, T paramT);
  
  public abstract R visitForEachStatement(ForEachStatement paramForEachStatement, T paramT);
  
  public abstract R visitTryCatchStatement(TryCatchStatement paramTryCatchStatement, T paramT);
  
  public abstract R visitGotoStatement(GotoStatement paramGotoStatement, T paramT);
  
  public abstract R visitParenthesizedExpression(ParenthesizedExpression paramParenthesizedExpression, T paramT);
  
  public abstract R visitSynchronizedStatement(SynchronizedStatement paramSynchronizedStatement, T paramT);
  
  public abstract R visitAnonymousObjectCreationExpression(AnonymousObjectCreationExpression paramAnonymousObjectCreationExpression, T paramT);
  
  public abstract R visitWildcardType(WildcardType paramWildcardType, T paramT);
  
  public abstract R visitMethodGroupExpression(MethodGroupExpression paramMethodGroupExpression, T paramT);
  
  public abstract R visitEnumValueDeclaration(EnumValueDeclaration paramEnumValueDeclaration, T paramT);
  
  public abstract R visitAssertStatement(AssertStatement paramAssertStatement, T paramT);
  
  public abstract R visitLambdaExpression(LambdaExpression paramLambdaExpression, T paramT);
  
  public abstract R visitLocalTypeDeclarationStatement(LocalTypeDeclarationStatement paramLocalTypeDeclarationStatement, T paramT);
}


