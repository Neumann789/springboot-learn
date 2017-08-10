/* MemberReferenceExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import java.util.Iterator;

import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class MemberReferenceExpression extends Expression
{
    public MemberReferenceExpression(int offset, Expression target,
				     String memberName,
				     Iterable typeArguments) {
	super(offset);
	addChild(target, Roles.TARGET_EXPRESSION);
	setMemberName(memberName);
	if (typeArguments != null) {
	    Iterator i$ = typeArguments.iterator();
	    while (i$.hasNext()) {
		AstType argument = (AstType) i$.next();
		addChild(argument, Roles.TYPE_ARGUMENT);
	    }
	}
	return;
    }
    
    public transient MemberReferenceExpression(int offset, Expression target,
					       String memberName,
					       AstType[] typeArguments) {
	super(offset);
	addChild(target, Roles.TARGET_EXPRESSION);
	setMemberName(memberName);
	if (typeArguments != null) {
	    AstType[] arr$ = typeArguments;
	    int len$ = arr$.length;
	    for (int i$ = 0; i$ < len$; i$++) {
		AstType argument = arr$[i$];
		addChild(argument, Roles.TYPE_ARGUMENT);
	    }
	}
	return;
    }
    
    public final String getMemberName() {
	return ((Identifier) getChildByRole(Roles.IDENTIFIER)).getName();
    }
    
    public final void setMemberName(String name) {
	setChildByRole(Roles.IDENTIFIER, Identifier.create(name));
    }
    
    public final Identifier getMemberNameToken() {
	return (Identifier) getChildByRole(Roles.IDENTIFIER);
    }
    
    public final void setMemberNameToken(Identifier token) {
	setChildByRole(Roles.IDENTIFIER, token);
    }
    
    public final Expression getTarget() {
	return (Expression) getChildByRole(Roles.TARGET_EXPRESSION);
    }
    
    public final void setTarget(Expression value) {
	setChildByRole(Roles.TARGET_EXPRESSION, value);
    }
    
    public final AstNodeCollection getTypeArguments() {
	return getChildrenByRole(Roles.TYPE_ARGUMENT);
    }
    
    public final JavaTokenNode getDotToken() {
	return (JavaTokenNode) getChildByRole(Roles.DOT);
    }
    
    public final JavaTokenNode getLeftChevronToken() {
	return (JavaTokenNode) getChildByRole(Roles.LEFT_CHEVRON);
    }
    
    public final JavaTokenNode getRightChevronToken() {
	return (JavaTokenNode) getChildByRole(Roles.RIGHT_CHEVRON);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitMemberReferenceExpression(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof MemberReferenceExpression))
	    return false;
    label_1654:
	{
	    MemberReferenceExpression otherExpression
		= (MemberReferenceExpression) other;
	    if (otherExpression.isNull()
		|| !getTarget().matches(otherExpression.getTarget(), match)
		|| !matchString(getMemberName(),
				otherExpression.getMemberName())
		|| !getTypeArguments()
			.matches(otherExpression.getTypeArguments(), match))
		PUSH false;
	    else
		PUSH true;
	    break label_1654;
	}
	return POP;
    }
}
