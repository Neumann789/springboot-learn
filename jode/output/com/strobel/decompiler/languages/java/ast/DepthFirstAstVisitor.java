/* DepthFirstAstVisitor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.Pattern;

public abstract class DepthFirstAstVisitor implements IAstVisitor
{
    protected Object visitChildren(AstNode node, Object data) {
	AstNode child = node.getFirstChild();
	for (;;) {
	    if (child == null)
		return null;
	    AstNode next = child.getNextSibling();
	    child.acceptVisitor(this, data);
	    child = next;
	}
    }
    
    public Object visitComment(Comment node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitPatternPlaceholder(AstNode node, Pattern pattern,
					  Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitInvocationExpression(InvocationExpression node,
					    Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitTypeReference(TypeReferenceExpression node,
				     Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitJavaTokenNode(JavaTokenNode node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitMemberReferenceExpression
	(MemberReferenceExpression node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitIdentifier(Identifier node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitNullReferenceExpression(NullReferenceExpression node,
					       Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitThisReferenceExpression(ThisReferenceExpression node,
					       Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitSuperReferenceExpression(SuperReferenceExpression node,
						Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitClassOfExpression(ClassOfExpression node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitBlockStatement(BlockStatement node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitExpressionStatement(ExpressionStatement node,
					   Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitBreakStatement(BreakStatement node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitContinueStatement(ContinueStatement node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitDoWhileStatement(DoWhileStatement node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitEmptyStatement(EmptyStatement node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitIfElseStatement(IfElseStatement node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitLabelStatement(LabelStatement node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitLabeledStatement(LabeledStatement node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitReturnStatement(ReturnStatement node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitSwitchStatement(SwitchStatement node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitSwitchSection(SwitchSection node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitCaseLabel(CaseLabel node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitThrowStatement(ThrowStatement node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitCatchClause(CatchClause node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitAnnotation(Annotation node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitNewLine(NewLineNode node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitVariableDeclaration(VariableDeclarationStatement node,
					   Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitVariableInitializer(VariableInitializer node,
					   Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitText(TextNode node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitImportDeclaration(ImportDeclaration node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitSimpleType(SimpleType node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitMethodDeclaration(MethodDeclaration node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitInitializerBlock(InstanceInitializer node,
					Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitConstructorDeclaration(ConstructorDeclaration node,
					      Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitTypeParameterDeclaration(TypeParameterDeclaration node,
						Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitParameterDeclaration(ParameterDeclaration node,
					    Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitFieldDeclaration(FieldDeclaration node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitTypeDeclaration(TypeDeclaration node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitCompilationUnit(CompilationUnit node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitPackageDeclaration(PackageDeclaration node,
					  Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitArraySpecifier(ArraySpecifier node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitComposedType(ComposedType node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitWhileStatement(WhileStatement node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitPrimitiveExpression(PrimitiveExpression node,
					   Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitCastExpression(CastExpression node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitBinaryOperatorExpression(BinaryOperatorExpression node,
						Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitInstanceOfExpression(InstanceOfExpression node,
					    Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitIndexerExpression(IndexerExpression node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitIdentifierExpression(IdentifierExpression node,
					    Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitUnaryOperatorExpression(UnaryOperatorExpression node,
					       Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitConditionalExpression(ConditionalExpression node,
					     Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitArrayInitializerExpression
	(ArrayInitializerExpression node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitObjectCreationExpression(ObjectCreationExpression node,
						Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitArrayCreationExpression(ArrayCreationExpression node,
					       Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitAssignmentExpression(AssignmentExpression node,
					    Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitForStatement(ForStatement node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitForEachStatement(ForEachStatement node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitGotoStatement(GotoStatement node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitParenthesizedExpression(ParenthesizedExpression node,
					       Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitSynchronizedStatement(SynchronizedStatement node,
					     Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitAnonymousObjectCreationExpression
	(AnonymousObjectCreationExpression node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitWildcardType(WildcardType node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitMethodGroupExpression(MethodGroupExpression node,
					     Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitEnumValueDeclaration(EnumValueDeclaration node,
					    Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitAssertStatement(AssertStatement node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitLambdaExpression(LambdaExpression node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitLocalTypeDeclarationStatement
	(LocalTypeDeclarationStatement node, Object data) {
	return visitChildren(node, data);
    }
    
    public Object visitTryCatchStatement(TryCatchStatement node, Object data) {
	return visitChildren(node, data);
    }
}
