/* AssertStatementTransform - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import com.strobel.assembler.metadata.FieldDefinition;
import com.strobel.assembler.metadata.FieldReference;
import com.strobel.assembler.metadata.MemberReference;
import com.strobel.assembler.metadata.MetadataHelper;
import com.strobel.assembler.metadata.MetadataResolver;
import com.strobel.assembler.metadata.MethodDefinition;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.core.CollectionUtilities;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.languages.java.ast.AssertStatement;
import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
import com.strobel.decompiler.languages.java.ast.AstNode;
import com.strobel.decompiler.languages.java.ast.AstType;
import com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression;
import com.strobel.decompiler.languages.java.ast.BinaryOperatorType;
import com.strobel.decompiler.languages.java.ast.BlockStatement;
import com.strobel.decompiler.languages.java.ast.CastExpression;
import com.strobel.decompiler.languages.java.ast.ClassOfExpression;
import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
import com.strobel.decompiler.languages.java.ast.Expression;
import com.strobel.decompiler.languages.java.ast.ExpressionStatement;
import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
import com.strobel.decompiler.languages.java.ast.IfElseStatement;
import com.strobel.decompiler.languages.java.ast.InvocationExpression;
import com.strobel.decompiler.languages.java.ast.Keys;
import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
import com.strobel.decompiler.languages.java.ast.ObjectCreationExpression;
import com.strobel.decompiler.languages.java.ast.PrimitiveExpression;
import com.strobel.decompiler.languages.java.ast.SimpleType;
import com.strobel.decompiler.languages.java.ast.Statement;
import com.strobel.decompiler.languages.java.ast.ThrowStatement;
import com.strobel.decompiler.languages.java.ast.TypeReferenceExpression;
import com.strobel.decompiler.languages.java.ast.UnaryOperatorExpression;
import com.strobel.decompiler.languages.java.ast.UnaryOperatorType;
import com.strobel.decompiler.patterns.AnyNode;
import com.strobel.decompiler.patterns.Choice;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.LeftmostBinaryOperandNode;
import com.strobel.decompiler.patterns.Match;
import com.strobel.decompiler.patterns.NamedNode;
import com.strobel.decompiler.patterns.OptionalNode;
import com.strobel.decompiler.patterns.TypedNode;
import com.strobel.functions.Function;

public class AssertStatementTransform extends ContextTrackingVisitor
{
    private static final IfElseStatement ASSERT_PATTERN
	= (new IfElseStatement
	   (-34,
	    new Choice
		(new INode[]
		 { (new UnaryOperatorExpression
		    (UnaryOperatorType.NOT,
		     new Choice
			 (new INode[]
			  { (new BinaryOperatorExpression
			     (new LeftmostBinaryOperandNode
				  ((new NamedNode
				    ("assertionsDisabledCheck",
				     new TypeReferenceExpression
					 (-34, new SimpleType("$any$"))
					 .member("$assertionsDisabled"))),
				   BinaryOperatorType.LOGICAL_OR, true)
				  .toExpression(),
			      BinaryOperatorType.LOGICAL_OR,
			      new AnyNode("condition").toExpression())),
			    new TypeReferenceExpression
				(-34, new SimpleType("$any$"))
				.member("$assertionsDisabled") })
			 .toExpression())),
		   (new BinaryOperatorExpression
		    (new LeftmostBinaryOperandNode
			 ((new UnaryOperatorExpression
			   (UnaryOperatorType.NOT,
			    new NamedNode
				("assertionsDisabledCheck",
				 new TypeReferenceExpression
				     (-34, new SimpleType("$any$"))
				     .member("$assertionsDisabled"))
				.toExpression())),
			  BinaryOperatorType.LOGICAL_AND, true)
			 .toExpression(),
		     BinaryOperatorType.LOGICAL_AND,
		     new AnyNode("invertedCondition").toExpression())) })
		.toExpression(),
	    (new BlockStatement
	     (new Statement[]
	      { new ThrowStatement(new ObjectCreationExpression
				   (-34, new SimpleType("AssertionError"),
				    (new Expression[]
				     { new OptionalNode
					   (new AnyNode("message"))
					   .toExpression() }))) }))));
    private static final AssignmentExpression ASSERTIONS_DISABLED_PATTERN
	= (new AssignmentExpression
	   (new NamedNode
		("$assertionsDisabled",
		 new Choice(new INode[]
			    { new IdentifierExpression(-34,
						       "$assertionsDisabled"),
			      new TypedNode(TypeReferenceExpression.class)
				  .toExpression
				  ().member("$assertionsDisabled") }))
		.toExpression(),
	    (new UnaryOperatorExpression
	     (UnaryOperatorType.NOT,
	      (new InvocationExpression
	       (-34,
		(new MemberReferenceExpression
		 (-34,
		  new NamedNode
		      ("type",
		       new ClassOfExpression(-34, new SimpleType("$any$")))
		      .toExpression(),
		  "desiredAssertionStatus", new AstType[0])),
		new Expression[0]))))));
    
    public AssertStatementTransform(DecompilerContext context) {
	super(context);
    }
    
    public Void visitIfElseStatement(IfElseStatement node, Void data) {
	super.visitIfElseStatement(node, data);
	transformAssert(node);
	return null;
    }
    
    public Void visitAssignmentExpression(AssignmentExpression node,
					  Void data) {
	super.visitAssignmentExpression(node, data);
	removeAssertionsDisabledAssignment(node);
	return null;
    }
    
    private void removeAssertionsDisabledAssignment
	(AssignmentExpression node) {
	if (!context.getSettings().getShowSyntheticMembers()) {
	    Match m = ASSERTIONS_DISABLED_PATTERN.match(node);
	    if (m.success()) {
		AstNode parent = node.getParent();
		if (parent instanceof ExpressionStatement
		    && parent.getParent() instanceof BlockStatement
		    && (parent.getParent().getParent()
			instanceof MethodDeclaration)) {
		    MethodDeclaration staticInitializer
			= (MethodDeclaration) parent.getParent().getParent();
		    MethodDefinition methodDefinition
			= ((MethodDefinition)
			   staticInitializer
			       .getUserData(Keys.METHOD_DEFINITION));
		    if (methodDefinition != null
			&& methodDefinition.isTypeInitializer()) {
			Expression field
			    = ((Expression)
			       CollectionUtilities
				   .first(m.get("$assertionsDisabled")));
			ClassOfExpression type
			    = ((ClassOfExpression)
			       m.get("type").iterator().next());
			MemberReference reference
			    = ((MemberReference)
			       field.getUserData(Keys.MEMBER_REFERENCE));
			if (reference instanceof FieldReference) {
			    FieldDefinition resolvedField
				= ((FieldReference) reference).resolve();
			    if (resolvedField != null
				&& resolvedField.isSynthetic()) {
				TypeReference typeReference
				    = ((TypeReference)
				       type.getType()
					   .getUserData(Keys.TYPE_REFERENCE));
				if (typeReference != null
				    && ((MetadataResolver.areEquivalent
					 (context.getCurrentType(),
					  typeReference))
					|| (MetadataHelper.isEnclosedBy
					    (context.getCurrentType(),
					     typeReference)))) {
				    parent.remove();
				    if (staticInitializer.getBody()
					    .getStatements
					    ().isEmpty())
					staticInitializer.remove();
				}
			    }
			}
		    }
		}
	    }
	}
	return;
    }
    
    private AssertStatement transformAssert(IfElseStatement ifElse) {
	Match m = ASSERT_PATTERN.match(ifElse);
	Expression condition;
    label_1693:
	{
	    Expression assertionsDisabledCheck;
	label_1692:
	    {
		if (m.success()) {
		    assertionsDisabledCheck
			= (Expression) (CollectionUtilities.firstOrDefault
					(m.get("assertionsDisabledCheck")));
		    condition
			= (Expression) CollectionUtilities
					   .firstOrDefault(m.get("condition"));
		    if (condition == null) {
			condition
			    = (Expression) (CollectionUtilities.firstOrDefault
					    (m.get("invertedCondition")));
			if (condition != null)
			    condition = (Expression) condition
							 .replaceWith(new Function() {
				{
				    super();
				}
				
				public Expression apply(AstNode input) {
				    return (new UnaryOperatorExpression
					    (UnaryOperatorType.NOT,
					     (Expression) input));
				}
			    });
		    }
		} else
		    return null;
	    }
	    if (condition != null && assertionsDisabledCheck != null
		&& (assertionsDisabledCheck.getParent()
		    instanceof BinaryOperatorExpression)
		&& (assertionsDisabledCheck.getParent().getParent()
		    instanceof BinaryOperatorExpression)) {
		BinaryOperatorExpression logicalOr
		    = ((BinaryOperatorExpression)
		       assertionsDisabledCheck.getParent());
		Expression right = logicalOr.getRight();
		right.remove();
		assertionsDisabledCheck.replaceWith(right);
		condition.remove();
		logicalOr.setRight(condition);
		condition = logicalOr;
	    }
	    break label_1693;
	}
	PUSH new AssertStatement;
    label_1694:
	{
	    DUP
	    if (condition != null)
		PUSH condition.getOffset();
	    else
		PUSH ifElse.getOffset();
	    break label_1694;
	}
	((UNCONSTRUCTED)POP).AssertStatement(POP);
	AssertStatement assertStatement;
    label_1696:
	{
	label_1695:
	    {
		assertStatement = POP;
		if (condition == null)
		    assertStatement.setCondition
			(new PrimitiveExpression(-34, Boolean.valueOf(false)));
		else {
		    condition.remove();
		    assertStatement.setCondition(condition);
		}
		break label_1695;
	    }
	    if (m.has("message")) {
		Expression message
		    = ((Expression)
		       CollectionUtilities.firstOrDefault(m.get("message")));
		for (;;) {
		    if (!(message instanceof CastExpression)) {
			message.remove();
			assertStatement.setMessage(message);
			break;
		    }
		    message = ((CastExpression) message).getExpression();
		}
	    }
	    break label_1696;
	}
	ifElse.replaceWith(assertStatement);
	return assertStatement;
	break label_1692;
    }
}
