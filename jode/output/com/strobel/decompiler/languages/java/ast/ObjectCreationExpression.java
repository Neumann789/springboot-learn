/* ObjectCreationExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import java.util.Iterator;

import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class ObjectCreationExpression extends Expression
{
    public static final TokenRole NEW_KEYWORD_ROLE = new TokenRole("new", 1);
    
    public ObjectCreationExpression(int offset, AstType type) {
	super(offset);
	setType(type);
    }
    
    public ObjectCreationExpression(int offset, AstType type,
				    Iterable arguments) {
	super(offset);
	setType(type);
	if (arguments != null) {
	    Iterator i$ = arguments.iterator();
	    while (i$.hasNext()) {
		Expression argument = (Expression) i$.next();
		addChild(argument, Roles.ARGUMENT);
	    }
	}
	return;
    }
    
    public transient ObjectCreationExpression(int offset, AstType type,
					      Expression[] arguments) {
	super(offset);
	setType(type);
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
    
    public final JavaTokenNode getNewToken() {
	return (JavaTokenNode) getChildByRole(NEW_KEYWORD_ROLE);
    }
    
    public final AstType getType() {
	return (AstType) getChildByRole(Roles.TYPE);
    }
    
    public final void setType(AstType type) {
	setChildByRole(Roles.TYPE, type);
    }
    
    public final JavaTokenNode getLeftParenthesisToken() {
	return (JavaTokenNode) getChildByRole(Roles.LEFT_PARENTHESIS);
    }
    
    public final JavaTokenNode getRightParenthesisToken() {
	return (JavaTokenNode) getChildByRole(Roles.RIGHT_PARENTHESIS);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitObjectCreationExpression(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof ObjectCreationExpression))
	    return false;
    label_1668:
	{
	    ObjectCreationExpression otherExpression
		= (ObjectCreationExpression) other;
	    if (otherExpression.isNull()
		|| !getTarget().matches(otherExpression.getTarget(), match)
		|| !getType().matches(otherExpression.getType(), match)
		|| !getArguments().matches(otherExpression.getArguments(),
					   match))
		PUSH false;
	    else
		PUSH true;
	    break label_1668;
	}
	return POP;
    }
}
