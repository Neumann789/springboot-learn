/* LambdaExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;
import com.strobel.decompiler.patterns.Role;

public class LambdaExpression extends Expression
{
    public static final TokenRole ARROW_ROLE = new TokenRole("->", 2);
    public static final Role BODY_ROLE
	= new Role("Body", AstNode.class, AstNode.NULL);
    
    public LambdaExpression(int offset) {
	super(offset);
    }
    
    public final AstNodeCollection getParameters() {
	return getChildrenByRole(Roles.PARAMETER);
    }
    
    public final JavaTokenNode getArrowToken() {
	return (JavaTokenNode) getChildByRole(ARROW_ROLE);
    }
    
    public final AstNode getBody() {
	return getChildByRole(BODY_ROLE);
    }
    
    public final void setBody(AstNode value) {
	setChildByRole(BODY_ROLE, value);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitLambdaExpression(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof LambdaExpression))
	    return false;
    label_1651:
	{
	    LambdaExpression otherLambda = (LambdaExpression) other;
	    if (!getParameters().matches(otherLambda.getParameters(), match)
		|| !getBody().matches(otherLambda.getBody(), match))
		PUSH false;
	    else
		PUSH true;
	    break label_1651;
	}
	return POP;
    }
}
