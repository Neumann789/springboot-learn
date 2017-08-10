/* TypeParameterDeclaration - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;
import com.strobel.decompiler.patterns.Role;

public class TypeParameterDeclaration extends AstNode
{
    public static final Role ANNOTATION_ROLE
	= EntityDeclaration.ANNOTATION_ROLE;
    
    public TypeParameterDeclaration() {
	/* empty */
    }
    
    public TypeParameterDeclaration(String name) {
	setName(name);
    }
    
    public final AstNodeCollection getAnnotations() {
	return getChildrenByRole(ANNOTATION_ROLE);
    }
    
    public final String getName() {
	return ((Identifier) getChildByRole(Roles.IDENTIFIER)).getName();
    }
    
    public final void setName(String value) {
	setChildByRole(Roles.IDENTIFIER, Identifier.create(value));
    }
    
    public final AstType getExtendsBound() {
	return (AstType) getChildByRole(Roles.EXTENDS_BOUND);
    }
    
    public final void setExtendsBound(AstType value) {
	setChildByRole(Roles.EXTENDS_BOUND, value);
    }
    
    public final Identifier getNameToken() {
	return (Identifier) getChildByRole(Roles.IDENTIFIER);
    }
    
    public final void setNameToken(Identifier value) {
	setChildByRole(Roles.IDENTIFIER, value);
    }
    
    public NodeType getNodeType() {
	return NodeType.UNKNOWN;
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitTypeParameterDeclaration(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof TypeParameterDeclaration))
	    return false;
    label_1681:
	{
	    TypeParameterDeclaration otherDeclaration
		= (TypeParameterDeclaration) other;
	    if (otherDeclaration.isNull()
		|| !matchString(getName(), otherDeclaration.getName())
		|| !getExtendsBound()
			.matches(otherDeclaration.getExtendsBound(), match)
		|| !getAnnotations().matches(otherDeclaration.getAnnotations(),
					     match))
		PUSH false;
	    else
		PUSH true;
	    break label_1681;
	}
	return POP;
    }
}
