/* WildcardType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class WildcardType extends AstType
{
    public static final TokenRole WILDCARD_TOKEN_ROLE = new TokenRole("?");
    public static final TokenRole EXTENDS_KEYWORD_ROLE = Roles.EXTENDS_KEYWORD;
    public static final TokenRole SUPER_KEYWORD_ROLE
	= new TokenRole("super", 1);
    
    public final JavaTokenNode getWildcardToken() {
	return (JavaTokenNode) getChildByRole(WILDCARD_TOKEN_ROLE);
    }
    
    public final AstNodeCollection getExtendsBounds() {
	return getChildrenByRole(Roles.EXTENDS_BOUND);
    }
    
    public final AstNodeCollection getSuperBounds() {
	return getChildrenByRole(Roles.SUPER_BOUND);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitWildcardType(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof WildcardType))
	    return false;
    label_1687:
	{
	    WildcardType otherWildcard = (WildcardType) other;
	    if (!getExtendsBounds().matches(otherWildcard.getExtendsBounds(),
					    match)
		|| !getSuperBounds().matches(otherWildcard.getSuperBounds(),
					     match))
		PUSH false;
	    else
		PUSH true;
	    break label_1687;
	}
	return POP;
    }
}
