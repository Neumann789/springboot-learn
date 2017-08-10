/* IntroduceInitializersTransform - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.strobel.assembler.metadata.FieldDefinition;
import com.strobel.assembler.metadata.FieldReference;
import com.strobel.assembler.metadata.MemberReference;
import com.strobel.assembler.metadata.MethodDefinition;
import com.strobel.core.CollectionUtilities;
import com.strobel.core.StringUtilities;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.languages.java.ast.AnonymousObjectCreationExpression;
import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
import com.strobel.decompiler.languages.java.ast.AssignmentOperatorType;
import com.strobel.decompiler.languages.java.ast.AstNode;
import com.strobel.decompiler.languages.java.ast.AstType;
import com.strobel.decompiler.languages.java.ast.ConstructorDeclaration;
import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
import com.strobel.decompiler.languages.java.ast.Expression;
import com.strobel.decompiler.languages.java.ast.ExpressionStatement;
import com.strobel.decompiler.languages.java.ast.FieldDeclaration;
import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
import com.strobel.decompiler.languages.java.ast.InvocationExpression;
import com.strobel.decompiler.languages.java.ast.Keys;
import com.strobel.decompiler.languages.java.ast.LocalClassHelper;
import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
import com.strobel.decompiler.languages.java.ast.Roles;
import com.strobel.decompiler.languages.java.ast.Statement;
import com.strobel.decompiler.languages.java.ast.SuperReferenceExpression;
import com.strobel.decompiler.languages.java.ast.VariableInitializer;
import com.strobel.decompiler.patterns.AnyNode;
import com.strobel.decompiler.patterns.Choice;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;
import com.strobel.decompiler.patterns.MemberReferenceTypeNode;
import com.strobel.decompiler.patterns.TypedNode;

public class IntroduceInitializersTransform extends ContextTrackingVisitor
{
    private final Map _fieldDeclarations = new HashMap();
    private final Map _initializers = new HashMap();
    private MethodDefinition _currentInitializerMethod;
    private MethodDefinition _currentConstructor;
    private static final INode FIELD_ASSIGNMENT
	= (new AssignmentExpression
	   (new MemberReferenceTypeNode
		("target",
		 new Choice
		     (new INode[]
		      { (new MemberReferenceExpression
			 (-34,
			  new Choice
			      (new INode[]
			       { (new TypedNode
				  (com.strobel.decompiler.languages.java.ast.TypeReferenceExpression.class)),
				 (new TypedNode
				  (com.strobel.decompiler.languages.java.ast.ThisReferenceExpression.class)) })
			      .toExpression(),
			  "$any$", new AstType[0])),
			new IdentifierExpression(-34, "$any$") })
		     .toExpression(),
		 FieldReference.class)
		.toExpression(),
	    AssignmentOperatorType.ASSIGN,
	    new AnyNode("value").toExpression()));
    
    public IntroduceInitializersTransform(DecompilerContext context) {
	super(context);
    }
    
    public void run(AstNode compilationUnit) {
	(/*TYPE_ERROR*/ new ContextTrackingVisitor(context) {
	    null(DecompilerContext x0) {
		super(x0);
	    }
	    
	    public Void visitFieldDeclaration(FieldDeclaration node, Void _) {
	    label_1742:
		{
		    FieldDefinition field
			= ((FieldDefinition)
			   node.getUserData(Keys.FIELD_DEFINITION));
		    if (field != null)
			_fieldDeclarations.put(field.getFullName(), node);
		    break label_1742;
		}
		return (Void) super.visitFieldDeclaration(node, _);
	    }
	}).run(compilationUnit);
	super.run(compilationUnit);
	inlineInitializers();
	LocalClassHelper.introduceInitializerBlocks(context, compilationUnit);
    }
    
    private void inlineInitializers() {
	Iterator i$ = _initializers.keySet().iterator();
	for (;;) {
	    IF (!i$.hasNext())
		/* empty */
	    String fieldName = (String) i$.next();
	    FieldDeclaration declaration
		= (FieldDeclaration) _fieldDeclarations.get(fieldName);
	    if (declaration != null
		&& ((VariableInitializer)
		    declaration.getVariables().firstOrNullObject())
		       .getInitializer
		       ().isNull()) {
		AssignmentExpression assignment
		    = (AssignmentExpression) _initializers.get(fieldName);
		Expression value = assignment.getRight();
		value.remove();
		((VariableInitializer)
		 declaration.getVariables().firstOrNullObject())
		    .setInitializer(value);
		AstNode parent = assignment.getParent();
		if (!(parent instanceof ExpressionStatement)) {
		    if (parent.getRole() != Roles.VARIABLE) {
			Expression left = assignment.getLeft();
			left.remove();
			parent.replaceWith(left);
		    } else {
			Expression left = assignment.getLeft();
			left.remove();
			assignment.replaceWith(left);
		    }
		} else
		    parent.remove();
	    }
	    continue;
	}
    }
    
    public Void visitAnonymousObjectCreationExpression
	(AnonymousObjectCreationExpression node, Void data) {
	MethodDefinition oldInitializer = _currentInitializerMethod;
	MethodDefinition oldConstructor = _currentConstructor;
	_currentInitializerMethod = null;
	_currentConstructor = null;
	try {
	    Void var_void
		= ((Void)
		   super.visitAnonymousObjectCreationExpression(node, data));
	    _currentInitializerMethod = oldInitializer;
	    _currentConstructor = oldConstructor;
	    return var_void;
	} finally {
	    Object object = POP;
	    _currentInitializerMethod = oldInitializer;
	    _currentConstructor = oldConstructor;
	    throw object;
	}
    }
    
    public Void visitMethodDeclaration(MethodDeclaration node, Void _) {
	MethodDefinition oldInitializer = _currentInitializerMethod;
	MethodDefinition oldConstructor = _currentConstructor;
	MethodDefinition method
	    = (MethodDefinition) node.getUserData(Keys.METHOD_DEFINITION);
    label_1738:
	{
	label_1737:
	    {
		if (method == null || !method.isTypeInitializer()) {
		    PUSH this;
		    if (method == null || !method.isConstructor())
			PUSH null;
		    else
			PUSH method;
		} else {
		    _currentConstructor = null;
		    _currentInitializerMethod = method;
		    break label_1738;
		}
	    }
	    ((IntroduceInitializersTransform) POP)._currentConstructor = POP;
	    _currentInitializerMethod = null;
	}
	try {
	    Void var_void = (Void) super.visitMethodDeclaration(node, _);
	    _currentConstructor = oldConstructor;
	    _currentInitializerMethod = oldInitializer;
	    return var_void;
	} finally {
	    Object object = POP;
	    _currentConstructor = oldConstructor;
	    _currentInitializerMethod = oldInitializer;
	    throw object;
	}
	break label_1737;
    }
    
    public Void visitAssignmentExpression(AssignmentExpression node,
					  Void data) {
	super.visitAssignmentExpression(node, data);
    label_1739:
	{
	    if (node.getParent() instanceof Statement) {
		if ((_currentInitializerMethod != null
		     || _currentConstructor != null)
		    && context.getCurrentType() != null) {
		    Match match = FIELD_ASSIGNMENT.match(node);
		    if (match.success()) {
			Expression target
			    = ((Expression)
			       CollectionUtilities
				   .firstOrDefault(match.get("target")));
			FieldReference reference
			    = ((FieldReference)
			       target.getUserData(Keys.MEMBER_REFERENCE));
			FieldDefinition definition = reference.resolve();
			if (definition == null || !definition.isFinal()
			    || definition.getConstantValue() == null) {
			    if (_currentInitializerMethod != null
				&& _currentInitializerMethod.getDeclaringType
				       ().isInterface()
				&& (StringUtilities.equals
				    (context.getCurrentType()
					 .getInternalName(),
				     reference.getDeclaringType()
					 .getInternalName())))
				_initializers.put(reference.getFullName(),
						  node);
			} else {
			    node.getParent().remove();
			    return null;
			}
		    }
		} else
		    return null;
	    } else
		return null;
	}
	return null;
	break label_1739;
    }
    
    public Void visitSuperReferenceExpression(SuperReferenceExpression node,
					      Void _) {
	super.visitSuperReferenceExpression(node, _);
    label_1741:
	{
	    MethodDefinition method = context.getCurrentMethod();
	    if (method != null && method.isConstructor()
		&& (method.isSynthetic()
		    || method.getDeclaringType().isAnonymous())
		&& node.getParent() instanceof InvocationExpression
		&& node.getRole() == Roles.TARGET_EXPRESSION) {
		Statement parentStatement
		    = (Statement) (CollectionUtilities.firstOrDefault
				   (node.getAncestors(Statement.class)));
		ConstructorDeclaration constructor
		    = ((ConstructorDeclaration)
		       (CollectionUtilities.firstOrDefault
			(node.getAncestors(ConstructorDeclaration.class))));
		if (parentStatement != null && constructor != null
		    && constructor.getParent() != null
		    && parentStatement.getNextStatement() != null) {
		    Statement next;
		    for (Statement current
			     = parentStatement.getNextStatement();
			 current instanceof ExpressionStatement;
			 current = next) {
			next = current.getNextStatement();
			Expression expression
			    = ((ExpressionStatement) current).getExpression();
			Match match = FIELD_ASSIGNMENT.match(expression);
			if (!match.success())
			    break;
			Expression target
			    = ((Expression)
			       CollectionUtilities
				   .firstOrDefault(match.get("target")));
		    label_1740:
			{
			    MemberReference reference
				= ((MemberReference)
				   target.getUserData(Keys.MEMBER_REFERENCE));
			    if (StringUtilities.equals(context.getCurrentType
							   ()
							   .getInternalName(),
						       reference
							   .getDeclaringType
							   ()
							   .getInternalName()))
				_initializers.put(reference.getFullName(),
						  ((AssignmentExpression)
						   expression));
			    break label_1740;
			}
		    }
		} else
		    return null;
	    }
	    break label_1741;
	}
	return null;
    }
}
