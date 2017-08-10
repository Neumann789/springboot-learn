/* IAstVisitor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.Pattern;

public interface IAstVisitor
{
    public Object visitComment(Comment comment, Object object);
    
    public Object visitPatternPlaceholder(AstNode astnode, Pattern pattern,
					  Object object);
    
    public Object visitInvocationExpression
	(InvocationExpression invocationexpression, Object object);
    
    public Object visitTypeReference
	(TypeReferenceExpression typereferenceexpression, Object object);
    
    public Object visitJavaTokenNode(JavaTokenNode javatokennode,
				     Object object);
    
    public Object visitMemberReferenceExpression
	(MemberReferenceExpression memberreferenceexpression, Object object);
    
    public Object visitIdentifier(Identifier identifier, Object object);
    
    public Object visitNullReferenceExpression
	(NullReferenceExpression nullreferenceexpression, Object object);
    
    public Object visitThisReferenceExpression
	(ThisReferenceExpression thisreferenceexpression, Object object);
    
    public Object visitSuperReferenceExpression
	(SuperReferenceExpression superreferenceexpression, Object object);
    
    public Object visitClassOfExpression(ClassOfExpression classofexpression,
					 Object object);
    
    public Object visitBlockStatement(BlockStatement blockstatement,
				      Object object);
    
    public Object visitExpressionStatement
	(ExpressionStatement expressionstatement, Object object);
    
    public Object visitBreakStatement(BreakStatement breakstatement,
				      Object object);
    
    public Object visitContinueStatement(ContinueStatement continuestatement,
					 Object object);
    
    public Object visitDoWhileStatement(DoWhileStatement dowhilestatement,
					Object object);
    
    public Object visitEmptyStatement(EmptyStatement emptystatement,
				      Object object);
    
    public Object visitIfElseStatement(IfElseStatement ifelsestatement,
				       Object object);
    
    public Object visitLabelStatement(LabelStatement labelstatement,
				      Object object);
    
    public Object visitLabeledStatement(LabeledStatement labeledstatement,
					Object object);
    
    public Object visitReturnStatement(ReturnStatement returnstatement,
				       Object object);
    
    public Object visitSwitchStatement(SwitchStatement switchstatement,
				       Object object);
    
    public Object visitSwitchSection(SwitchSection switchsection,
				     Object object);
    
    public Object visitCaseLabel(CaseLabel caselabel, Object object);
    
    public Object visitThrowStatement(ThrowStatement throwstatement,
				      Object object);
    
    public Object visitCatchClause(CatchClause catchclause, Object object);
    
    public Object visitAnnotation(Annotation annotation, Object object);
    
    public Object visitNewLine(NewLineNode newlinenode, Object object);
    
    public Object visitVariableDeclaration
	(VariableDeclarationStatement variabledeclarationstatement,
	 Object object);
    
    public Object visitVariableInitializer
	(VariableInitializer variableinitializer, Object object);
    
    public Object visitText(TextNode textnode, Object object);
    
    public Object visitImportDeclaration(ImportDeclaration importdeclaration,
					 Object object);
    
    public Object visitSimpleType(SimpleType simpletype, Object object);
    
    public Object visitMethodDeclaration(MethodDeclaration methoddeclaration,
					 Object object);
    
    public Object visitInitializerBlock
	(InstanceInitializer instanceinitializer, Object object);
    
    public Object visitConstructorDeclaration
	(ConstructorDeclaration constructordeclaration, Object object);
    
    public Object visitTypeParameterDeclaration
	(TypeParameterDeclaration typeparameterdeclaration, Object object);
    
    public Object visitParameterDeclaration
	(ParameterDeclaration parameterdeclaration, Object object);
    
    public Object visitFieldDeclaration(FieldDeclaration fielddeclaration,
					Object object);
    
    public Object visitTypeDeclaration(TypeDeclaration typedeclaration,
				       Object object);
    
    public Object visitCompilationUnit(CompilationUnit compilationunit,
				       Object object);
    
    public Object visitPackageDeclaration
	(PackageDeclaration packagedeclaration, Object object);
    
    public Object visitArraySpecifier(ArraySpecifier arrayspecifier,
				      Object object);
    
    public Object visitComposedType(ComposedType composedtype, Object object);
    
    public Object visitWhileStatement(WhileStatement whilestatement,
				      Object object);
    
    public Object visitPrimitiveExpression
	(PrimitiveExpression primitiveexpression, Object object);
    
    public Object visitCastExpression(CastExpression castexpression,
				      Object object);
    
    public Object visitBinaryOperatorExpression
	(BinaryOperatorExpression binaryoperatorexpression, Object object);
    
    public Object visitInstanceOfExpression
	(InstanceOfExpression instanceofexpression, Object object);
    
    public Object visitIndexerExpression(IndexerExpression indexerexpression,
					 Object object);
    
    public Object visitIdentifierExpression
	(IdentifierExpression identifierexpression, Object object);
    
    public Object visitUnaryOperatorExpression
	(UnaryOperatorExpression unaryoperatorexpression, Object object);
    
    public Object visitConditionalExpression
	(ConditionalExpression conditionalexpression, Object object);
    
    public Object visitArrayInitializerExpression
	(ArrayInitializerExpression arrayinitializerexpression, Object object);
    
    public Object visitObjectCreationExpression
	(ObjectCreationExpression objectcreationexpression, Object object);
    
    public Object visitArrayCreationExpression
	(ArrayCreationExpression arraycreationexpression, Object object);
    
    public Object visitAssignmentExpression
	(AssignmentExpression assignmentexpression, Object object);
    
    public Object visitForStatement(ForStatement forstatement, Object object);
    
    public Object visitForEachStatement(ForEachStatement foreachstatement,
					Object object);
    
    public Object visitTryCatchStatement(TryCatchStatement trycatchstatement,
					 Object object);
    
    public Object visitGotoStatement(GotoStatement gotostatement,
				     Object object);
    
    public Object visitParenthesizedExpression
	(ParenthesizedExpression parenthesizedexpression, Object object);
    
    public Object visitSynchronizedStatement
	(SynchronizedStatement synchronizedstatement, Object object);
    
    public Object visitAnonymousObjectCreationExpression
	(AnonymousObjectCreationExpression anonymousobjectcreationexpression,
	 Object object);
    
    public Object visitWildcardType(WildcardType wildcardtype, Object object);
    
    public Object visitMethodGroupExpression
	(MethodGroupExpression methodgroupexpression, Object object);
    
    public Object visitEnumValueDeclaration
	(EnumValueDeclaration enumvaluedeclaration, Object object);
    
    public Object visitAssertStatement(AssertStatement assertstatement,
				       Object object);
    
    public Object visitLambdaExpression(LambdaExpression lambdaexpression,
					Object object);
    
    public Object visitLocalTypeDeclarationStatement
	(LocalTypeDeclarationStatement localtypedeclarationstatement,
	 Object object);
}
