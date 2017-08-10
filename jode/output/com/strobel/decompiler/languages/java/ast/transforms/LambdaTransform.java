/* LambdaTransform - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast.transforms;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.strobel.assembler.metadata.DynamicCallSite;
import com.strobel.assembler.metadata.MemberReference;
import com.strobel.assembler.metadata.MetadataFilters;
import com.strobel.assembler.metadata.MetadataHelper;
import com.strobel.assembler.metadata.MethodDefinition;
import com.strobel.assembler.metadata.MethodReference;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.core.Predicates;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.languages.java.ast.AstNode;
import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
import com.strobel.decompiler.languages.java.ast.AstType;
import com.strobel.decompiler.languages.java.ast.BlockStatement;
import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
import com.strobel.decompiler.languages.java.ast.Expression;
import com.strobel.decompiler.languages.java.ast.ExpressionStatement;
import com.strobel.decompiler.languages.java.ast.Identifier;
import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
import com.strobel.decompiler.languages.java.ast.JavaResolver;
import com.strobel.decompiler.languages.java.ast.Keys;
import com.strobel.decompiler.languages.java.ast.LambdaExpression;
import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
import com.strobel.decompiler.languages.java.ast.MethodGroupExpression;
import com.strobel.decompiler.languages.java.ast.ParameterDeclaration;
import com.strobel.decompiler.languages.java.ast.ReturnStatement;
import com.strobel.decompiler.languages.java.ast.Roles;
import com.strobel.decompiler.languages.java.ast.Statement;

public class LambdaTransform extends ContextTrackingVisitor
{
    private final JavaResolver _resolver;
    private final Map _methodDeclarations = new HashMap();
    
    public LambdaTransform(DecompilerContext context) {
	super(context);
	_resolver = new JavaResolver(context);
    }
    
    public void run(AstNode compilationUnit) {
	compilationUnit.acceptVisitor(new ContextTrackingVisitor(context) {
	    null(DecompilerContext x0) {
		super(x0);
	    }
	    
	    public Void visitMethodDeclaration(MethodDeclaration node,
					       Void _) {
	    label_1769:
		{
		    MemberReference methodReference
			= ((MemberReference)
			   node.getUserData(Keys.MEMBER_REFERENCE));
		    if (methodReference instanceof MethodReference)
			_methodDeclarations.put(makeMethodKey((MethodReference)
							      methodReference),
						node);
		    break label_1769;
		}
		return (Void) super.visitMethodDeclaration(node, _);
	    }
	}, null);
	super.run(compilationUnit);
    }
    
    public Void visitMethodGroupExpression(MethodGroupExpression node,
					   Void data) {
    label_1760:
	{
	    MemberReference reference
		= (MemberReference) node.getUserData(Keys.MEMBER_REFERENCE);
	    if (reference instanceof MethodReference) {
		MethodReference method = (MethodReference) reference;
		MethodDefinition resolvedMethod = method.resolve();
		DynamicCallSite callSite
		    = ((DynamicCallSite)
		       node.getUserData(Keys.DYNAMIC_CALL_SITE));
		if (resolvedMethod != null && resolvedMethod.isSynthetic()
		    && callSite != null) {
		    inlineLambda(node, resolvedMethod);
		    return null;
		}
	    }
	    break label_1760;
	}
	return (Void) super.visitMethodGroupExpression(node, data);
    }
    
    private void inlineLambda(MethodGroupExpression methodGroup,
			      MethodDefinition method) {
	MethodDeclaration declaration
	    = ((MethodDeclaration)
	       _methodDeclarations.get(makeMethodKey(method)));
	if (declaration != null) {
	    BlockStatement body
		= (BlockStatement) declaration.getBody().clone();
	    AstNodeCollection parameters = declaration.getParameters();
	    Map renamedVariables = new HashMap();
	    AstNodeCollection closureArguments
		= methodGroup.getClosureArguments();
	    Statement firstStatement;
	    int offset;
	label_1761:
	    {
		firstStatement
		    = (Statement) body.getStatements().firstOrNullObject();
		if (firstStatement == null || firstStatement.isNull())
		    offset = -34;
		else
		    offset = firstStatement.getOffset();
		break label_1761;
	    }
	    Expression a = (Expression) closureArguments.firstOrNullObject();
	    ParameterDeclaration p
		= (ParameterDeclaration) parameters.firstOrNullObject();
	    LambdaExpression lambda;
	    DynamicCallSite callSite;
	    TypeReference lambdaType;
	label_1764:
	    {
	    label_1763:
		{
		    for (;;) {
		    label_1762:
			{
			    if (p == null || p.isNull() || a == null
				|| a.isNull()) {
				body.acceptVisitor(new ContextTrackingVisitor(context,
									      renamedVariables) {
				    /*synthetic*/ final Map val$renamedVariables;
				    
				    null(DecompilerContext x0, Map map) {
					val$renamedVariables = map;
					super(x0);
				    }
				    
				    public Void visitIdentifier
					(Identifier node, Void _) {
				    label_1770:
					{
					    String oldName = node.getName();
					    if (oldName != null) {
						IdentifierExpression newName
						    = ((IdentifierExpression)
						       val$renamedVariables
							   .get(oldName));
						if (newName != null
						    && (newName.getIdentifier()
							!= null))
						    node.setName
							(newName
							     .getIdentifier());
					    }
					    break label_1770;
					}
					return ((Void)
						super.visitIdentifier(node,
								      _));
				    }
				    
				    public Void visitIdentifierExpression
					(IdentifierExpression node, Void _) {
				    label_1771:
					{
					    String oldName
						= node.getIdentifier();
					    if (oldName != null) {
						IdentifierExpression newName
						    = ((IdentifierExpression)
						       val$renamedVariables
							   .get(oldName));
						if (newName != null) {
						    node.replaceWith
							(newName.clone());
						    return null;
						}
					    }
					    break label_1771;
					}
					return ((Void)
						(super
						     .visitIdentifierExpression
						 (node, _)));
				    }
				}, null);
				lambda = new LambdaExpression(offset);
				callSite = ((DynamicCallSite)
					    (methodGroup.getUserData
					     (Keys.DYNAMIC_CALL_SITE)));
				lambdaType
				    = ((TypeReference)
				       methodGroup
					   .getUserData(Keys.TYPE_REFERENCE));
				if (callSite != null)
				    lambda.putUserData(Keys.DYNAMIC_CALL_SITE,
						       callSite);
			    } else {
				if (a instanceof IdentifierExpression)
				    renamedVariables.put
					(p.getName(),
					 (IdentifierExpression) a);
				break label_1762;
			    }
			    break label_1763;
			}
			p = ((ParameterDeclaration)
			     p.getNextSibling(p.getRole()));
			a = (Expression) a.getNextSibling(a.getRole());
		    }
		}
		if (lambdaType == null) {
		    if (callSite == null)
			return;
		    lambdaType = callSite.getMethodType().getReturnType();
		} else
		    lambda.putUserData(Keys.TYPE_REFERENCE, lambdaType);
		break label_1764;
	    }
	label_1765:
	    {
		body.remove();
		if (body.getStatements().size() != 1
		    || (!(firstStatement instanceof ExpressionStatement)
			&& !(firstStatement instanceof ReturnStatement)))
		    lambda.setBody(body);
		else {
		    Expression simpleBody
			= ((Expression)
			   firstStatement.getChildByRole(Roles.EXPRESSION));
		    simpleBody.remove();
		    lambda.setBody(simpleBody);
		}
		break label_1765;
	    }
	    int parameterCount = 0;
	    int parametersToSkip = closureArguments.size();
	    Iterator i$ = declaration.getParameters().iterator();
	label_1768:
	    {
		MethodReference functionMethod;
	    label_1766:
		{
		    for (;;) {
			if (!i$.hasNext()) {
			    if (MetadataHelper.isRawType(lambdaType))
				break;
			    com.strobel.assembler.metadata.TypeDefinition resolvedType
				= lambdaType.resolve();
			    if (resolvedType == null)
				break label_1768;
			    functionMethod = null;
			    PUSH resolvedType;
			    if (callSite == null)
				PUSH Predicates.alwaysTrue();
			    else
				PUSH MetadataFilters
					 .matchName(callSite.getMethodName());
			} else {
			    ParameterDeclaration p_9_
				= (ParameterDeclaration) i$.next();
			    if (parametersToSkip-- <= 0) {
				ParameterDeclaration lambdaParameter
				    = (ParameterDeclaration) p_9_.clone();
				lambdaParameter.setType(AstType.NULL);
				lambda.addChild(lambdaParameter,
						Roles.PARAMETER);
				parameterCount++;
			    }
			    continue;
			}
			break label_1766;
		    }
		    break label_1768;
		}
		List methods = MetadataHelper.findMethods(POP, POP);
		Iterator i$_10_ = methods.iterator();
		while (i$_10_.hasNext()) {
		    MethodReference m;
		    m = (MethodReference) i$_10_.next();
		    MethodDefinition r = m.resolve();
		    IF (r == null || !r.isAbstract() || r.isStatic()
			|| r.isDefault())
			/* empty */
		    functionMethod = r;
		    break;
		}
		if (functionMethod != null
		    && functionMethod.containsGenericParameters()
		    && (functionMethod.getParameters().size()
			== parameterCount)) {
		    TypeReference asMemberOf
			= MetadataHelper.asSuper(functionMethod
						     .getDeclaringType(),
						 lambdaType);
		    if (asMemberOf != null
			&& !MetadataHelper.isRawType(asMemberOf)) {
		    label_1767:
			{
			    PUSH functionMethod;
			    if (!MetadataHelper.isRawType(asMemberOf))
				PUSH asMemberOf;
			    else
				PUSH MetadataHelper.erase(asMemberOf);
			    break label_1767;
			}
			functionMethod = MetadataHelper.asMemberOf(POP, POP);
			lambda.putUserData(Keys.MEMBER_REFERENCE,
					   functionMethod);
			if (functionMethod != null) {
			    List fp = functionMethod.getParameters();
			    int i = 0;
			    ParameterDeclaration p_11_
				= ((ParameterDeclaration)
				   lambda.getParameters().firstOrNullObject());
			    while (i < parameterCount) {
				p_11_.putUserData(Keys.PARAMETER_DEFINITION,
						  fp.get(i));
				i++;
				p_11_
				    = ((ParameterDeclaration)
				       p_11_.getNextSibling(Roles.PARAMETER));
			    }
			}
		    }
		}
	    }
	    methodGroup.replaceWith(lambda);
	    lambda.acceptVisitor(this, null);
	    break label_1766;
	    break label_1763;
	} else {
	    /* empty */
	}
	return;
    }
    
    private static String makeMethodKey(MethodReference method) {
	return method.getFullName() + ":" + method.getErasedSignature();
    }
}
