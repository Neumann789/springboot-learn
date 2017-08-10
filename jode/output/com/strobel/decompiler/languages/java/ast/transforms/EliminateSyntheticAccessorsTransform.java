/* EliminateSyntheticAccessorsTransform - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Modifier;

import com.strobel.assembler.metadata.MemberReference;
import com.strobel.assembler.metadata.MetadataHelper;
import com.strobel.assembler.metadata.MetadataResolver;
import com.strobel.assembler.metadata.MethodDefinition;
import com.strobel.assembler.metadata.MethodReference;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.core.CollectionUtilities;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
import com.strobel.decompiler.languages.java.ast.AssignmentOperatorType;
import com.strobel.decompiler.languages.java.ast.AstBuilder;
import com.strobel.decompiler.languages.java.ast.AstNode;
import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
import com.strobel.decompiler.languages.java.ast.AstType;
import com.strobel.decompiler.languages.java.ast.BlockStatement;
import com.strobel.decompiler.languages.java.ast.CastExpression;
import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
import com.strobel.decompiler.languages.java.ast.Expression;
import com.strobel.decompiler.languages.java.ast.ExpressionStatement;
import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
import com.strobel.decompiler.languages.java.ast.InliningHelper;
import com.strobel.decompiler.languages.java.ast.InvocationExpression;
import com.strobel.decompiler.languages.java.ast.JavaModifierToken;
import com.strobel.decompiler.languages.java.ast.Keys;
import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
import com.strobel.decompiler.languages.java.ast.ParameterDeclaration;
import com.strobel.decompiler.languages.java.ast.ReturnStatement;
import com.strobel.decompiler.languages.java.ast.Statement;
import com.strobel.decompiler.languages.java.ast.TypeDeclaration;
import com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement;
import com.strobel.decompiler.languages.java.ast.VariableInitializer;
import com.strobel.decompiler.patterns.AnyNode;
import com.strobel.decompiler.patterns.BackReference;
import com.strobel.decompiler.patterns.Choice;
import com.strobel.decompiler.patterns.DeclaredVariableBackReference;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.MemberReferenceTypeNode;
import com.strobel.decompiler.patterns.NamedNode;
import com.strobel.decompiler.patterns.OptionalNode;
import com.strobel.decompiler.patterns.ParameterReferenceNode;
import com.strobel.decompiler.patterns.SubtreeMatch;
import com.strobel.decompiler.patterns.TypedNode;

public class EliminateSyntheticAccessorsTransform
    extends ContextTrackingVisitor
{
    private final List _nodesToRemove = new ArrayList();
    private final Map _accessMethodDeclarations = new HashMap();
    private final Set _visitedTypes = new HashSet();
    private static final MethodDeclaration SYNTHETIC_GET_ACCESSOR;
    private static final MethodDeclaration SYNTHETIC_SET_ACCESSOR;
    private static final MethodDeclaration SYNTHETIC_SET_ACCESSOR_ALT;
    private static final MethodDeclaration SYNTHETIC_STATIC_GET_ACCESSOR;
    private static final MethodDeclaration SYNTHETIC_STATIC_SET_ACCESSOR;
    private static final MethodDeclaration SYNTHETIC_STATIC_SET_ACCESSOR_ALT;
    
    private class PhaseOneVisitor extends ContextTrackingVisitor
    {
	private PhaseOneVisitor() {
	    super(access$100(EliminateSyntheticAccessorsTransform.this));
	}
	
	public Void visitTypeDeclaration(TypeDeclaration node, Void _) {
	    TypeDefinition type
		= (TypeDefinition) node.getUserData(Keys.TYPE_DEFINITION);
	    if (type == null || _visitedTypes.add(type.getInternalName()))
		return (Void) super.visitTypeDeclaration(node, _);
	    return null;
	}
	
	public Void visitMethodDeclaration(MethodDeclaration node, Void _) {
	label_1713:
	    {
		MethodDefinition method
		    = ((MethodDefinition)
		       node.getUserData(Keys.METHOD_DEFINITION));
		if (method != null && method.isSynthetic() && method.isStatic()
		    && (tryMatchAccessor(node) || tryMatchCallWrapper(node)))
		    _accessMethodDeclarations.put(makeMethodKey(method), node);
		break label_1713;
	    }
	    return (Void) super.visitMethodDeclaration(node, _);
	}
	
	private boolean tryMatchAccessor(MethodDeclaration node) {
	    if (!EliminateSyntheticAccessorsTransform
		     .SYNTHETIC_GET_ACCESSOR.matches(node)
		&& !EliminateSyntheticAccessorsTransform
			.SYNTHETIC_SET_ACCESSOR.matches(node)
		&& !EliminateSyntheticAccessorsTransform
			.SYNTHETIC_SET_ACCESSOR_ALT.matches(node)
		&& !EliminateSyntheticAccessorsTransform
			.SYNTHETIC_STATIC_GET_ACCESSOR.matches(node)
		&& !EliminateSyntheticAccessorsTransform
			.SYNTHETIC_STATIC_SET_ACCESSOR.matches(node)
		&& !EliminateSyntheticAccessorsTransform
			.SYNTHETIC_STATIC_SET_ACCESSOR_ALT.matches(node))
		return false;
	    return true;
	}
	
	private boolean tryMatchCallWrapper(MethodDeclaration node) {
	    AstNodeCollection statements = node.getBody().getStatements();
	    InvocationExpression invocation;
	label_1716:
	    {
	    label_1714:
		{
		    if (statements.hasSingleElement()) {
			Statement s
			    = (Statement) statements.firstOrNullObject();
			if (!(s instanceof ExpressionStatement)) {
			    if (!(s instanceof ReturnStatement))
				invocation = null;
			    else {
			    label_1715:
				{
				    ReturnStatement r = (ReturnStatement) s;
				    if (!(r.getExpression()
					  instanceof InvocationExpression))
					PUSH null;
				    else
					PUSH ((InvocationExpression)
					      r.getExpression());
				    break label_1715;
				}
				invocation = POP;
			    }
			    break label_1716;
			}
			ExpressionStatement e = (ExpressionStatement) s;
			if (!(e.getExpression()
			      instanceof InvocationExpression))
			    PUSH null;
			else
			    PUSH (InvocationExpression) e.getExpression();
		    } else
			return false;
		}
		invocation = POP;
	    }
	label_1717:
	    {
		if (invocation != null) {
		    MethodReference targetMethod
			= ((MethodReference)
			   invocation.getUserData(Keys.MEMBER_REFERENCE));
		    if (targetMethod == null)
			PUSH null;
		    else
			PUSH targetMethod.resolve();
		} else
		    return false;
	    }
	    MethodDefinition resolvedTarget = POP;
	label_1718:
	    {
		if (resolvedTarget != null) {
		    if (!resolvedTarget.isStatic())
			PUSH true;
		    else
			PUSH false;
		} else
		    return false;
	    }
	    int parametersStart = POP;
	    List parameterList
		= CollectionUtilities.toList(node.getParameters());
	    List argumentList
		= CollectionUtilities.toList(invocation.getArguments());
	label_1719:
	    {
		if (argumentList.size()
		    == parameterList.size() - parametersStart) {
		    if (!resolvedTarget.isStatic()) {
			if (invocation.getTarget()
			    instanceof MemberReferenceExpression) {
			    MemberReferenceExpression m
				= ((MemberReferenceExpression)
				   invocation.getTarget());
			    Expression target = m.getTarget();
			    if (!target.matches(new IdentifierExpression
						(-34, ((ParameterDeclaration)
						       parameterList.get(0))
							  .getName())))
				return false;
			} else
			    return false;
		    }
		} else
		    return false;
	    }
	    int i = parametersStart;
	    int j = 0;
	label_1720:
	    {
		for (;;) {
		    if (i >= parameterList.size()
			|| j >= argumentList.size()) {
			if (i != j + parametersStart)
			    PUSH false;
			else
			    PUSH true;
		    } else {
			Expression pattern
			    = new Choice
				  (new INode[] { (new CastExpression
						  (new AnyNode().toType(),
						   (new IdentifierExpression
						    (-34,
						     ((ParameterDeclaration)
						      parameterList.get(i))
							 .getName())))),
						 (new IdentifierExpression
						  (-34, ((ParameterDeclaration)
							 parameterList.get(i))
							    .getName())) })
				  .toExpression();
			if (pattern.matches((INode) argumentList.get(j))) {
			    i++;
			    j++;
			}
			return false;
		    }
		    break label_1720;
		}
	    }
	    return POP;
	    break label_1720;
	    break label_1719;
	    break label_1718;
	    break label_1717;
	    break label_1714;
	}
    }
    
    public EliminateSyntheticAccessorsTransform(DecompilerContext context) {
	super(context);
    }
    
    public void run(AstNode compilationUnit) {
	new PhaseOneVisitor().run(compilationUnit);
	super.run(compilationUnit);
	Iterator i$ = _nodesToRemove.iterator();
	for (;;) {
	    IF (!i$.hasNext())
		/* empty */
	    AstNode node = (AstNode) i$.next();
	    node.remove();
	}
    }
    
    private static String makeMethodKey(MethodReference method) {
	return method.getFullName() + ":" + method.getErasedSignature();
    }
    
    public Void visitInvocationExpression(InvocationExpression node,
					  Void data) {
	super.visitInvocationExpression(node, data);
	Expression target = node.getTarget();
    label_1712:
	{
	    AstNodeCollection arguments = node.getArguments();
	    if (target instanceof MemberReferenceExpression) {
		MemberReferenceExpression memberReference
		    = (MemberReferenceExpression) target;
		MemberReference reference;
	    label_1709:
		{
		    reference
			= ((MemberReference)
			   memberReference.getUserData(Keys.MEMBER_REFERENCE));
		    if (reference == null)
			reference = ((MemberReference)
				     node.getUserData(Keys.MEMBER_REFERENCE));
		    break label_1709;
		}
		if (reference instanceof MethodReference) {
		    MethodReference method = (MethodReference) reference;
		label_1710:
		    {
			TypeReference declaringType
			    = method.getDeclaringType();
			if (!(MetadataResolver.areEquivalent
			      (context.getCurrentType(), declaringType))
			    && !(MetadataHelper.isEnclosedBy
				 (context.getCurrentType(), declaringType))
			    && !(_visitedTypes.contains
				 (declaringType.getInternalName()))) {
			    MethodDefinition resolvedMethod = method.resolve();
			    if (resolvedMethod != null
				&& resolvedMethod.isSynthetic()) {
				AstBuilder astBuilder
				    = ((AstBuilder)
				       context.getUserData(Keys.AST_BUILDER));
				if (astBuilder != null) {
				    TypeDeclaration ownerTypeDeclaration
					= (astBuilder.createType
					   (resolvedMethod
						.getDeclaringType()));
				    ownerTypeDeclaration.acceptVisitor
					(new PhaseOneVisitor(), data);
				}
			    }
			}
			break label_1710;
		    }
		    String key = makeMethodKey(method);
		    MethodDeclaration declaration
			= ((MethodDeclaration)
			   _accessMethodDeclarations.get(key));
		    if (declaration != null) {
			MethodDefinition definition;
		    label_1711:
			{
			    definition
				= ((MethodDefinition)
				   declaration
				       .getUserData(Keys.METHOD_DEFINITION));
			    if (definition == null)
				PUSH null;
			    else
				PUSH definition.getParameters();
			    break label_1711;
			}
			List parameters = POP;
			if (definition != null
			    && parameters.size() == arguments.size()) {
			    Map parameterMap = new IdentityHashMap();
			    int i = 0;
			    Iterator i$ = arguments.iterator();
			    for (;;) {
				if (!i$.hasNext()) {
				    AstNode inlinedBody
					= (InliningHelper.inlineMethod
					   (declaration, parameterMap));
				    if (!(inlinedBody instanceof Expression)) {
					if (inlinedBody
					    instanceof BlockStatement) {
					    BlockStatement block
						= (BlockStatement) inlinedBody;
					    if (block.getStatements().size()
						!= 2) {
						if (block.getStatements()
							.size()
						    == 3) {
						    Statement tempAssignment
							= ((Statement)
							   (block.getStatements
								()
								.firstOrNullObject
							    ()));
						    Statement setStatement
							= ((Statement)
							   (CollectionUtilities
								.getOrDefault
							    ((block
								  .getStatements
							      ()),
							     1)));
						    if ((tempAssignment
							 instanceof VariableDeclarationStatement)
							&& (setStatement
							    instanceof ExpressionStatement)) {
							Expression expression
							    = (((ExpressionStatement)
								setStatement)
								   .getExpression
							       ());
							if (expression
							    instanceof AssignmentExpression) {
							    VariableDeclarationStatement tempVariable
								= ((VariableDeclarationStatement)
								   tempAssignment);
							    Expression initializer
								= ((VariableInitializer)
								   tempVariable
								       .getVariables
								       ()
								       .firstOrNullObject())
								      .getInitializer();
							    AssignmentExpression assignment
								= ((AssignmentExpression)
								   expression);
							    initializer
								.remove();
							    assignment.setRight
								(initializer);
							    expression
								.remove();
							    node.replaceWith
								(expression);
							}
						    }
						}
					    } else {
						Statement setStatement
						    = ((Statement)
						       (block.getStatements
							    ()
							    .firstOrNullObject
							()));
						if (setStatement
						    instanceof ExpressionStatement) {
						    Expression expression
							= ((ExpressionStatement)
							   setStatement)
							      .getExpression();
						    if (expression
							instanceof AssignmentExpression) {
							expression.remove();
							node.replaceWith
							    (expression);
						    }
						}
					    }
					}
				    } else
					node.replaceWith(inlinedBody);
				    break;
				}
				Expression argument = (Expression) i$.next();
				parameterMap.put(parameters.get(i++),
						 argument);
			    }
			}
		    }
		}
	    }
	    break label_1712;
	}
	return null;
    }
    
    /*synthetic*/ static DecompilerContext access$100
	(EliminateSyntheticAccessorsTransform x0) {
	return x0.context;
    }
    
    static {
	MethodDeclaration getAccessor = new MethodDeclaration();
	MethodDeclaration setAccessor = new MethodDeclaration();
	getAccessor.setName("$any$");
	getAccessor.getModifiers().add(new JavaModifierToken(Modifier.STATIC));
	getAccessor.setReturnType(new AnyNode("returnType").toType());
	setAccessor.setName("$any$");
	setAccessor.getModifiers().add(new JavaModifierToken(Modifier.STATIC));
	setAccessor.setReturnType(new AnyNode("returnType").toType());
	ParameterDeclaration getParameter
	    = new ParameterDeclaration("$any$",
				       new AnyNode("targetType").toType());
	getParameter.setAnyModifiers(true);
	getAccessor.getParameters().add(getParameter);
	ParameterDeclaration setParameter1
	    = new ParameterDeclaration("$any$",
				       new AnyNode("targetType").toType());
	ParameterDeclaration setParameter2
	    = new ParameterDeclaration("$any$", new BackReference
						    ("returnType").toType());
	setParameter1.setAnyModifiers(true);
	setParameter2.setAnyModifiers(true);
	setAccessor.getParameters().add(setParameter1);
	setAccessor.getParameters()
	    .add(new OptionalNode(setParameter2).toParameterDeclaration());
	getAccessor.setBody
	    (new BlockStatement
	     (new Statement[]
	      { new ReturnStatement
		(-34,
		 new SubtreeMatch
		     (new MemberReferenceTypeNode
		      (new MemberReferenceExpression(-34,
						     new ParameterReferenceNode
							 (0).toExpression(),
						     "$any$", new AstType[0]),
		       com.strobel.assembler.metadata.FieldReference.class))
		     .toExpression()) }));
	MethodDeclaration altSetAccessor
	    = (MethodDeclaration) setAccessor.clone();
	setAccessor.setBody
	    (new Choice
		 (new INode[]
		  { (new BlockStatement
		     (new Statement[]
		      { (new ExpressionStatement
			 (new AssignmentExpression
			  (new MemberReferenceTypeNode
			       ((new MemberReferenceExpression
				 (-34,
				  new ParameterReferenceNode(0).toExpression(),
				  "$any$", new AstType[0])),
				com.strobel.assembler.metadata.FieldReference.class)
			       .toExpression(),
			   AssignmentOperatorType.ANY,
			   new ParameterReferenceNode(1, "value")
			       .toExpression()))),
			new ReturnStatement(-34, new BackReference("value")
						     .toExpression()) })),
		    (new BlockStatement
		     (new Statement[]
		      { new ReturnStatement
			(-34,
			 (new AssignmentExpression
			  (new MemberReferenceTypeNode
			       ((new MemberReferenceExpression
				 (-34,
				  new ParameterReferenceNode(0).toExpression(),
				  "$any$", new AstType[0])),
				com.strobel.assembler.metadata.FieldReference.class)
			       .toExpression(),
			   AssignmentOperatorType.ANY,
			   new ParameterReferenceNode(1, "value")
			       .toExpression()))) })) })
		 .toBlockStatement());
	VariableDeclarationStatement tempVariable
	    = new VariableDeclarationStatement(new AnyNode().toType(), "$any$",
					       new AnyNode("value")
						   .toExpression());
	tempVariable.addModifier(Modifier.FINAL);
	altSetAccessor.setBody
	    (new BlockStatement
	     (new Statement[]
	      { new NamedNode("tempVariable", tempVariable).toStatement(),
		(new ExpressionStatement
		 (new AssignmentExpression
		  (new MemberReferenceTypeNode
		       ((new MemberReferenceExpression
			 (-34, new ParameterReferenceNode(0).toExpression(),
			  "$any$", new AstType[0])),
			com.strobel.assembler.metadata.FieldReference.class)
		       .toExpression(),
		   AssignmentOperatorType.ANY,
		   new SubtreeMatch
		       (new DeclaredVariableBackReference("tempVariable"))
		       .toExpression()))),
		new ReturnStatement(-34,
				    new DeclaredVariableBackReference
					("tempVariable").toExpression()) }));
	SYNTHETIC_GET_ACCESSOR = getAccessor;
	SYNTHETIC_SET_ACCESSOR = setAccessor;
	SYNTHETIC_SET_ACCESSOR_ALT = altSetAccessor;
	MethodDeclaration staticGetAccessor
	    = (MethodDeclaration) getAccessor.clone();
	MethodDeclaration staticSetAccessor
	    = (MethodDeclaration) setAccessor.clone();
	MethodDeclaration altStaticSetAccessor
	    = (MethodDeclaration) altSetAccessor.clone();
	staticGetAccessor.getParameters().clear();
	staticGetAccessor.setBody
	    (new BlockStatement
	     (new Statement[]
	      { new ReturnStatement
		(-34,
		 new SubtreeMatch
		     (new MemberReferenceTypeNode
		      ((new MemberReferenceExpression
			(-34,
			 new TypedNode
			     (com.strobel.decompiler.languages.java.ast.TypeReferenceExpression.class)
			     .toExpression(),
			 "$any$", new AstType[0])),
		       com.strobel.assembler.metadata.FieldReference.class))
		     .toExpression()) }));
	((ParameterDeclaration)
	 staticSetAccessor.getParameters().firstOrNullObject())
	    .remove();
	staticSetAccessor.setBody
	    (new Choice
		 (new INode[]
		  { (new BlockStatement
		     (new Statement[]
		      { (new ExpressionStatement
			 (new AssignmentExpression
			  (new MemberReferenceTypeNode
			       ((new MemberReferenceExpression
				 (-34,
				  new TypedNode
				      (com.strobel.decompiler.languages.java.ast.TypeReferenceExpression.class)
				      .toExpression(),
				  "$any$", new AstType[0])),
				com.strobel.assembler.metadata.FieldReference.class)
			       .toExpression(),
			   AssignmentOperatorType.ANY,
			   new NamedNode
			       ("value", (new SubtreeMatch
					  (new ParameterReferenceNode(0))))
			       .toExpression()))),
			new ReturnStatement(-34, new BackReference("value")
						     .toExpression()) })),
		    (new BlockStatement
		     (new Statement[]
		      { new ReturnStatement
			(-34,
			 (new AssignmentExpression
			  (new MemberReferenceTypeNode
			       ((new MemberReferenceExpression
				 (-34,
				  new TypedNode
				      (com.strobel.decompiler.languages.java.ast.TypeReferenceExpression.class)
				      .toExpression(),
				  "$any$", new AstType[0])),
				com.strobel.assembler.metadata.FieldReference.class)
			       .toExpression(),
			   AssignmentOperatorType.ANY,
			   new NamedNode
			       ("value", (new SubtreeMatch
					  (new ParameterReferenceNode(0))))
			       .toExpression()))) })) })
		 .toBlockStatement());
	((ParameterDeclaration)
	 altStaticSetAccessor.getParameters().firstOrNullObject())
	    .remove();
	altStaticSetAccessor.setBody
	    (new BlockStatement
	     (new Statement[]
	      { new NamedNode("tempVariable", tempVariable).toStatement(),
		(new ExpressionStatement
		 (new AssignmentExpression
		  (new MemberReferenceTypeNode
		       ((new MemberReferenceExpression
			 (-34,
			  new TypedNode
			      (com.strobel.decompiler.languages.java.ast.TypeReferenceExpression.class)
			      .toExpression(),
			  "$any$", new AstType[0])),
			com.strobel.assembler.metadata.FieldReference.class)
		       .toExpression(),
		   AssignmentOperatorType.ANY,
		   new SubtreeMatch
		       (new DeclaredVariableBackReference("tempVariable"))
		       .toExpression()))),
		new ReturnStatement(-34,
				    new DeclaredVariableBackReference
					("tempVariable").toExpression()) }));
	SYNTHETIC_STATIC_GET_ACCESSOR = staticGetAccessor;
	SYNTHETIC_STATIC_SET_ACCESSOR = staticSetAccessor;
	SYNTHETIC_STATIC_SET_ACCESSOR_ALT = altStaticSetAccessor;
    }
}
