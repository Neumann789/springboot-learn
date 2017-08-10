/* InvocationExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import java.util.Iterator;

import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class InvocationExpression extends Expression
{
    public InvocationExpression(int offset, Expression target,
				Iterable arguments) {
	super(offset);
	addChild(target, Roles.TARGET_EXPRESSION);
	if (arguments != null) {
	    Iterator i$ = arguments.iterator();
	    while (i$.hasNext()) {
		Expression argument = (Expression) i$.next();
		addChild(argument, Roles.ARGUMENT);
	    }
	}
	return;
    }
    
    public transient InvocationExpression(int offset, Expression target,
					  Expression[] arguments) {
	super(offset);
	addChild(target, Roles.TARGET_EXPRESSION);
	if (arguments != null) {
	    Expression[] arr$ = arguments;
	    int len$ = arr$.length;
	    for (int i$ = 0; i$ < len$; i$++) {
		Expression argument = arr$[i$];
		addChild(argument, Roles.ARGUMENT);
	    }
	}
	return;
    }
    
    public final Expression getTarget() {
	return (Expression) getChildByRole(Roles.TARGET_EXPRESSION);
    }
    
    public final void setTarget(Expression value) {
	setChildByRole(Roles.TARGET_EXPRESSION, value);
    }
    
    public final AstNodeCollection getArguments() {
	return getChildrenByRole(Roles.ARGUMENT);
    }
    
    public final JavaTokenNode getLeftParenthesisToken() {
	return (JavaTokenNode) getChildByRole(Roles.LEFT_PARENTHESIS);
    }
    
    public final JavaTokenNode getRightParenthesisToken() {
	return (JavaTokenNode) getChildByRole(Roles.LEFT_PARENTHESIS);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitInvocationExpression(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof InvocationExpression))
	    return false;
    label_1617:
	{
	    InvocationExpression otherExpression
		= (InvocationExpression) other;
	    if (!getTarget().matches(otherExpression.getTarget(), match)
		|| !getArguments().matches(otherExpression.getArguments(),
					   match))
		PUSH false;
	    else
		PUSH true;
	    break label_1617;
	}
	return POP;
    }
}
