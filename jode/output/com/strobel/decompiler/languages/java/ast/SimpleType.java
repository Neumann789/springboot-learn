/* SimpleType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import java.util.Iterator;

import com.strobel.decompiler.languages.TextLocation;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class SimpleType extends AstType
{
    public SimpleType(String identifier) {
	this(identifier, EMPTY_TYPES);
    }
    
    public SimpleType(Identifier identifier) {
	setIdentifierToken(identifier);
    }
    
    public SimpleType(String identifier, TextLocation location) {
	setChildByRole(Roles.IDENTIFIER,
		       Identifier.create(identifier, location));
    }
    
    public SimpleType(String identifier, Iterable typeArguments) {
	setIdentifier(identifier);
	if (typeArguments != null) {
	    Iterator i$ = typeArguments.iterator();
	    while (i$.hasNext()) {
		AstType typeArgument = (AstType) i$.next();
		addChild(typeArgument, Roles.TYPE_ARGUMENT);
	    }
	}
	return;
    }
    
    public transient SimpleType(String identifier, AstType[] typeArguments) {
	setIdentifier(identifier);
	if (typeArguments != null) {
	    AstType[] arr$ = typeArguments;
	    int len$ = arr$.length;
	    for (int i$ = 0; i$ < len$; i$++) {
		AstType typeArgument = arr$[i$];
		addChild(typeArgument, Roles.TYPE_ARGUMENT);
	    }
	}
	return;
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
	return visitor.visitSimpleType(this, data);
    }
    
    public String toString() {
	AstNodeCollection typeArguments = getTypeArguments();
	if (!typeArguments.isEmpty()) {
	    StringBuilder sb = new StringBuilder(getIdentifier()).append('<');
	    boolean first = true;
	    Iterator i$ = typeArguments.iterator();
	    for (;;) {
		if (!i$.hasNext())
		    return sb.append('>').toString();
		AstType typeArgument;
	    label_1674:
		{
		    typeArgument = (AstType) i$.next();
		    if (!first)
			sb.append(", ");
		    break label_1674;
		}
		first = false;
		sb.append(typeArgument);
	    }
	}
	return getIdentifier();
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof SimpleType))
	    return false;
    label_1675:
	{
	    SimpleType otherType = (SimpleType) other;
	    if (other.isNull()
		|| !matchString(getIdentifier(), otherType.getIdentifier())
		|| !getTypeArguments().matches(otherType.getTypeArguments(),
					       match))
		PUSH false;
	    else
		PUSH true;
	    break label_1675;
	}
	return POP;
    }
}
