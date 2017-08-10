/* TryWithResourcesTransform - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import java.util.ArrayList;
import java.util.Iterator;

import javax.lang.model.element.Modifier;

import com.strobel.core.CollectionUtilities;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
import com.strobel.decompiler.languages.java.ast.AssignmentOperatorType;
import com.strobel.decompiler.languages.java.ast.AstBuilder;
import com.strobel.decompiler.languages.java.ast.AstNode;
import com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression;
import com.strobel.decompiler.languages.java.ast.BinaryOperatorType;
import com.strobel.decompiler.languages.java.ast.BlockStatement;
import com.strobel.decompiler.languages.java.ast.CatchClause;
import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
import com.strobel.decompiler.languages.java.ast.DefiniteAssignmentAnalysis;
import com.strobel.decompiler.languages.java.ast.DefiniteAssignmentStatus;
import com.strobel.decompiler.languages.java.ast.Expression;
import com.strobel.decompiler.languages.java.ast.ExpressionStatement;
import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
import com.strobel.decompiler.languages.java.ast.IfElseStatement;
import com.strobel.decompiler.languages.java.ast.JavaResolver;
import com.strobel.decompiler.languages.java.ast.Keys;
import com.strobel.decompiler.languages.java.ast.NullReferenceExpression;
import com.strobel.decompiler.languages.java.ast.SimpleType;
import com.strobel.decompiler.languages.java.ast.Statement;
import com.strobel.decompiler.languages.java.ast.ThrowStatement;
import com.strobel.decompiler.languages.java.ast.TryCatchStatement;
import com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement;
import com.strobel.decompiler.patterns.AnyNode;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.IdentifierExpressionBackReference;
import com.strobel.decompiler.patterns.Match;
import com.strobel.decompiler.patterns.NamedNode;
import com.strobel.decompiler.patterns.Pattern;
import com.strobel.decompiler.semantics.ResolveResult;

public class TryWithResourcesTransform extends ContextTrackingVisitor
{
    private static final INode RESOURCE_INIT_PATTERN;
    private static final INode CLEAR_SAVED_EXCEPTION_PATTERN;
    private final TryCatchStatement _tryPattern;
    private final AstBuilder _astBuilder;
    private final JavaResolver _resolver;
    
    private static final class MergeResourceTryStatementsVisitor
	extends ContextTrackingVisitor
    {
	MergeResourceTryStatementsVisitor(DecompilerContext context) {
	    super(context);
	}
	
	public Void visitTryCatchStatement(TryCatchStatement node, Void data) {
	    super.visitTryCatchStatement(node, data);
	label_1805:
	    {
		if (!node.getResources().isEmpty()) {
		    java.util.List resources = new ArrayList();
		    TryCatchStatement parentTry;
		    TryCatchStatement current;
		    for (current = node;
			 (current.getCatchClauses().isEmpty()
			  && current.getFinallyBlock().isNull());
			 current = parentTry) {
			AstNode parent;
			parent = current.getParent();
			if (parent instanceof BlockStatement
			    && (parent.getParent()
				instanceof TryCatchStatement)) {
			    parentTry = (TryCatchStatement) parent.getParent();
			label_1804:
			    {
				if (parentTry.getTryBlock().getStatements()
					.hasSingleElement()) {
				    if (!current.getResources().isEmpty())
					resources
					    .addAll(0, current.getResources());
				    break label_1804;
				}
			    }
			}
			break;
		    }
		    BlockStatement tryContent = node.getTryBlock();
		    if (current != node) {
			Iterator i$ = resources.iterator();
			for (;;) {
			    if (!i$.hasNext()) {
				tryContent.remove();
				current.setTryBlock(tryContent);
				break;
			    }
			    VariableDeclarationStatement resource
				= (VariableDeclarationStatement) i$.next();
			    resource.remove();
			    current.getResources().add(resource);
			}
		    }
		} else
		    return null;
	    }
	    return null;
	    break label_1805;
	}
    }
    
    public TryWithResourcesTransform(DecompilerContext context) {
	super(context);
	_astBuilder = (AstBuilder) context.getUserData(Keys.AST_BUILDER);
	if (_astBuilder != null) {
	    _resolver = new JavaResolver(context);
	    TryCatchStatement tryPattern = new TryCatchStatement(-34);
	    tryPattern
		.setTryBlock(new AnyNode("tryContent").toBlockStatement());
	    CatchClause catchClause
		= (new CatchClause
		   (new BlockStatement
		    (new Statement[]
		     { (new ExpressionStatement
			(new AssignmentExpression
			 (new IdentifierExpressionBackReference
			      ("savedException").toExpression(),
			  new NamedNode
			      ("caughtException",
			       new IdentifierExpression(-34, "$any$"))
			      .toExpression()))),
		       new ThrowStatement(new IdentifierExpressionBackReference
					      ("caughtException")
					      .toExpression()) })));
	    catchClause.setVariableName("$any$");
	    catchClause.getExceptionTypes().add(new SimpleType("Throwable"));
	    tryPattern.getCatchClauses().add(catchClause);
	    TryCatchStatement disposeTry = new TryCatchStatement(-34);
	    disposeTry.setTryBlock
		(new BlockStatement(new Statement[]
				    { new ExpressionStatement
				      (new IdentifierExpressionBackReference
					   ("resource").toExpression
					   ().invoke
				       ("close", new Expression[0])) }));
	    CatchClause disposeCatch
		= (new CatchClause
		   (new BlockStatement
		    (new Statement[]
		     { new ExpressionStatement
		       (new IdentifierExpressionBackReference
			    ("savedException").toExpression
			    ().invoke
			("addSuppressed",
			 (new Expression[]
			  { new NamedNode
				("caughtOnClose",
				 new IdentifierExpression(-34, "$any$"))
				.toExpression() }))) })));
	    disposeCatch.setVariableName("$any$");
	    disposeCatch.getExceptionTypes().add(new SimpleType("Throwable"));
	    disposeTry.getCatchClauses().add(disposeCatch);
	    tryPattern.setFinallyBlock
		(new BlockStatement
		 (new Statement[]
		  { new IfElseStatement
		    (-34,
		     (new BinaryOperatorExpression
		      (new IdentifierExpressionBackReference("resource")
			   .toExpression(),
		       BinaryOperatorType.INEQUALITY,
		       new NullReferenceExpression(-34))),
		     (new BlockStatement
		      (new Statement[]
		       { new IfElseStatement
			 (-34,
			  (new BinaryOperatorExpression
			   (new IdentifierExpressionBackReference
				("savedException").toExpression(),
			    BinaryOperatorType.INEQUALITY,
			    new NullReferenceExpression(-34))),
			  new BlockStatement(new Statement[] { disposeTry }),
			  (new BlockStatement
			   (new Statement[]
			    { new ExpressionStatement
			      (new IdentifierExpressionBackReference
				   ("resource").toExpression
				   ().invoke
			       ("close", new Expression[0])) }))) }))) }));
	    _tryPattern = tryPattern;
	} else {
	    _tryPattern = null;
	    _resolver = null;
	}
	return;
    }
    
    public void run(AstNode compilationUnit) {
	if (_tryPattern != null) {
	    super.run(compilationUnit);
	    new MergeResourceTryStatementsVisitor(context)
		.run(compilationUnit);
	}
	return;
    }
    
    public Void visitTryCatchStatement(TryCatchStatement node, Void data) {
	super.visitTryCatchStatement(node, data);
	BlockStatement parent;
	Statement p;
    label_1801:
	{
	    if (node.getParent() instanceof BlockStatement) {
		parent = (BlockStatement) node.getParent();
		p = ((Statement)
		     node.getPreviousSibling(BlockStatement.STATEMENT_ROLE));
		if (p == null)
		    PUSH null;
		else
		    PUSH ((Statement)
			  p.getPreviousSibling(BlockStatement.STATEMENT_ROLE));
	    } else
		return null;
	}
	Statement pp = POP;
    label_1803:
	{
	    BlockStatement tryContent;
	    VariableDeclarationStatement newResourceDeclaration;
	label_1802:
	    {
		if (pp != null) {
		    Statement initializeResource = pp;
		    Statement clearCaughtException = p;
		    Match m = Match.createNew();
		    if (!RESOURCE_INIT_PATTERN.matches(initializeResource, m)
			|| !CLEAR_SAVED_EXCEPTION_PATTERN
				.matches(clearCaughtException, m)
			|| !_tryPattern.matches(node, m))
			break label_1803;
		    IdentifierExpression resource
			= ((IdentifierExpression)
			   CollectionUtilities.first(m.get("resource")));
		    ResolveResult resourceResult = _resolver.apply(resource);
		    if (resourceResult != null
			&& resourceResult.getType() != null) {
			tryContent
			    = ((BlockStatement)
			       CollectionUtilities.first(m.get("tryContent")));
			Expression resourceInitializer
			    = ((Expression)
			       CollectionUtilities
				   .first(m.get("resourceInitializer")));
			IdentifierExpression caughtException
			    = ((IdentifierExpression)
			       CollectionUtilities
				   .first(m.get("caughtException")));
			IdentifierExpression caughtOnClose
			    = ((IdentifierExpression)
			       CollectionUtilities
				   .first(m.get("caughtOnClose")));
			CatchClause caughtParent
			    = ((CatchClause)
			       CollectionUtilities.first(caughtException
							     .getAncestors
							 (CatchClause.class)));
			CatchClause caughtOnCloseParent
			    = ((CatchClause)
			       CollectionUtilities.first(caughtOnClose
							     .getAncestors
							 (CatchClause.class)));
			if (caughtParent != null && caughtOnCloseParent != null
			    && Pattern.matchString(caughtException
						       .getIdentifier(),
						   caughtParent
						       .getVariableName())
			    && Pattern.matchString(caughtOnClose
						       .getIdentifier(),
						   caughtOnCloseParent
						       .getVariableName())) {
			    VariableDeclarationStatement resourceDeclaration
				= (ConvertLoopsTransform
				       .findVariableDeclaration
				   (node, resource.getIdentifier()));
			    if (resourceDeclaration != null
				&& (resourceDeclaration.getParent()
				    instanceof BlockStatement)) {
				BlockStatement outerTemp
				    = new BlockStatement();
				BlockStatement temp = new BlockStatement();
				initializeResource.remove();
				clearCaughtException.remove();
				node.replaceWith(outerTemp);
				temp.add(initializeResource);
				temp.add(clearCaughtException);
				temp.add(node);
				outerTemp.add(temp);
				Statement declarationPoint
				    = (ConvertLoopsTransform
					   .canMoveVariableDeclarationIntoStatement
				       (context, resourceDeclaration, node));
				node.remove();
				outerTemp.replaceWith(node);
				if (declarationPoint == outerTemp) {
				    tryContent.remove();
				    resource.remove();
				    resourceInitializer.remove();
				    newResourceDeclaration
					= (new VariableDeclarationStatement
					   ((_astBuilder.convertType
					     (resourceResult.getType())),
					    resource.getIdentifier(),
					    resourceInitializer));
				    Statement firstStatement
					= ((Statement)
					   (CollectionUtilities.firstOrDefault
					    (tryContent.getStatements())));
				    Statement lastStatement
					= ((Statement)
					   (CollectionUtilities.lastOrDefault
					    (tryContent.getStatements())));
				    if (firstStatement == null)
					newResourceDeclaration
					    .addModifier(Modifier.FINAL);
				    else {
					DefiniteAssignmentAnalysis analysis
					    = (new DefiniteAssignmentAnalysis
					       (context, tryContent));
					analysis.setAnalyzedRange
					    (firstStatement, lastStatement);
					analysis.analyze
					    (resource.getIdentifier(),
					     (DefiniteAssignmentStatus
					      .DEFINITELY_NOT_ASSIGNED));
					if (!analysis.isPotentiallyAssigned())
					    newResourceDeclaration
						.addModifier(Modifier.FINAL);
				    }
				} else {
				    initializeResource.remove();
				    clearCaughtException.remove();
				    parent.insertChildBefore
					(node, initializeResource,
					 BlockStatement.STATEMENT_ROLE);
				    parent.insertChildBefore
					(node, clearCaughtException,
					 BlockStatement.STATEMENT_ROLE);
				    return null;
				}
			    } else
				return null;
			} else
			    return null;
		    } else
			return null;
		} else
		    return null;
	    }
	    node.setTryBlock(tryContent);
	    node.getResources().add(newResourceDeclaration);
	    node.getCatchClauses().clear();
	    node.setFinallyBlock(null);
	}
	return null;
	break label_1802;
	break label_1801;
    }
    
    static {
	Expression resource
	    = new NamedNode
		  ("resource", new IdentifierExpression(-34, "$any$"))
		  .toExpression();
	Expression savedException
	    = new NamedNode
		  ("savedException", new IdentifierExpression(-34, "$any$"))
		  .toExpression();
	RESOURCE_INIT_PATTERN
	    = new ExpressionStatement(new AssignmentExpression
				      (resource, AssignmentOperatorType.ASSIGN,
				       new AnyNode("resourceInitializer")
					   .toExpression()));
	CLEAR_SAVED_EXCEPTION_PATTERN
	    = (new ExpressionStatement
	       (new AssignmentExpression(savedException,
					 AssignmentOperatorType.ASSIGN,
					 new NullReferenceExpression(-34))));
    }
}
