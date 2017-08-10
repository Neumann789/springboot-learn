/* InliningHelper - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import java.util.Map;

import com.strobel.assembler.metadata.MethodDefinition;
import com.strobel.assembler.metadata.MethodReference;
import com.strobel.assembler.metadata.ParameterDefinition;
import com.strobel.core.StringUtilities;
import com.strobel.core.VerifyArgument;
import com.strobel.decompiler.DecompilerContext;
import com.strobel.decompiler.ast.Variable;

public final class InliningHelper
{
    private static class InliningVisitor extends ContextTrackingVisitor
    {
	private final Map _argumentMappings;
	private AstNode _result;
	
	public InliningVisitor(DecompilerContext context,
			       Map argumentMappings) {
	    super(context);
	    _argumentMappings
		= (Map) VerifyArgument.notNull(argumentMappings,
					       "argumentMappings");
	}
	
	public final AstNode getInlinedBody() {
	    return _result;
	}
	
	public void run(AstNode root) {
	    BlockStatement body;
	label_1613:
	    {
		if (root instanceof MethodDeclaration) {
		    MethodDeclaration clone = (MethodDeclaration) root.clone();
		    super.run(clone);
		    body = clone.getBody();
		    AstNodeCollection statements = body.getStatements();
		    if (statements.size() == 1) {
			Statement firstStatement
			    = (Statement) statements.firstOrNullObject();
			if (firstStatement instanceof ExpressionStatement
			    || firstStatement instanceof ReturnStatement) {
			    _result = firstStatement
					  .getChildByRole(Roles.EXPRESSION);
			    _result.remove();
			    return;
			}
		    }
		} else
		    throw new IllegalArgumentException
			      ("InliningVisitor must be run against a MethodDeclaration.");
	    }
	    _result = body;
	    _result.remove();
	    break label_1613;
	}
	
	public Void visitIdentifierExpression(IdentifierExpression node,
					      Void _) {
	label_1614:
	    {
		Variable variable = (Variable) node.getUserData(Keys.VARIABLE);
		if (variable != null && variable.isParameter()) {
		    ParameterDefinition parameter
			= variable.getOriginalParameter();
		    if (areMethodsEquivalent(((MethodReference)
					      parameter.getMethod()),
					     context.getCurrentMethod())) {
			AstNode replacement
			    = (AstNode) _argumentMappings.get(parameter);
			if (replacement != null) {
			    node.replaceWith(replacement.clone());
			    return null;
			}
		    }
		}
		break label_1614;
	    }
	    return (Void) super.visitIdentifierExpression(node, _);
	}
	
	private boolean areMethodsEquivalent(MethodReference m1,
					     MethodDefinition m2) {
	label_1615:
	    {
		if (m1 != m2) {
		    if (m1 != null && m2 != null) {
			if (!StringUtilities.equals(m1.getFullName(),
						    m2.getFullName())
			    || !(StringUtilities.equals
				 (m1.getErasedSignature(),
				  m2.getErasedSignature())))
			    PUSH false;
			else
			    PUSH true;
		    } else
			return false;
		} else
		    return true;
	    }
	    return POP;
	    break label_1615;
	}
    }
    
    public static AstNode inlineMethod(MethodDeclaration method,
				       Map argumentMappings) {
	VerifyArgument.notNull(method, "method");
	VerifyArgument.notNull(argumentMappings, "argumentMappings");
	DecompilerContext context = new DecompilerContext();
    label_1612:
	{
	    MethodDefinition definition
		= ((MethodDefinition)
		   method.getUserData(Keys.METHOD_DEFINITION));
	    if (definition != null)
		context.setCurrentType(definition.getDeclaringType());
	    break label_1612;
	}
	InliningVisitor visitor
	    = new InliningVisitor(context, argumentMappings);
	visitor.run(method);
	return visitor.getInlinedBody();
    }
}
