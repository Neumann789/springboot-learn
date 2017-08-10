/* IdentifierExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class IdentifierExpression extends Expression
{
    public IdentifierExpression(int offset, String identifier) {
	super(offset);
	setIdentifier(identifier);
    }
    
    public IdentifierExpression(int offset, Identifier identifier) {
	super(offset);
	setIdentifierToken(identifier);
    }
    
    public final String getIdentifier() {
	return ((Identifier) getChildByRole(Roles.IDENTIFIER)).getName();
    }
    
    public final void setIdentifier(String value) {
	setChildByRole(Roles.IDENTIFIER, Identifier.create(value));
    }
    
    public final Identifier getIdentifierToken() {
	return (Identifier) getChildByRole(Roles.IDENTIFIER);
    }
    
    public final void setIdentifierToken(Identifier value) {
	setChildByRole(Roles.IDENTIFIER, value);
    }
    
    public final AstNodeCollection getTypeArguments() {
	return getChildrenByRole(Roles.TYPE_ARGUMENT);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitIdentifierExpression(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof IdentifierExpression))
	    return false;
    label_1609:
	{
	    IdentifierExpression otherIdentifier
		= (IdentifierExpression) other;
	    if (otherIdentifier.isNull()
		|| !matchString(getIdentifier(),
				otherIdentifier.getIdentifier())
		|| !getTypeArguments()
			.matches(otherIdentifier.getTypeArguments(), match))
		PUSH false;
	    else
		PUSH true;
	    break label_1609;
	}
	return POP;
    }
}
