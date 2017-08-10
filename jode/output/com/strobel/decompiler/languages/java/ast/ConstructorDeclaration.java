/* ConstructorDeclaration - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.languages.EntityType;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class ConstructorDeclaration extends EntityDeclaration
{
    public static final TokenRole THROWS_KEYWORD
	= MethodDeclaration.THROWS_KEYWORD;
    
    public final AstNodeCollection getParameters() {
	return getChildrenByRole(Roles.PARAMETER);
    }
    
    public final AstNodeCollection getThrownTypes() {
	return getChildrenByRole(Roles.THROWN_TYPE);
    }
    
    public final BlockStatement getBody() {
	return (BlockStatement) getChildByRole(Roles.BODY);
    }
    
    public final void setBody(BlockStatement value) {
	setChildByRole(Roles.BODY, value);
    }
    
    public final JavaTokenNode getLeftParenthesisToken() {
	return (JavaTokenNode) getChildByRole(Roles.LEFT_PARENTHESIS);
    }
    
    public final JavaTokenNode getRightParenthesisToken() {
	return (JavaTokenNode) getChildByRole(Roles.RIGHT_PARENTHESIS);
    }
    
    public EntityType getEntityType() {
	return EntityType.CONSTRUCTOR;
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitConstructorDeclaration(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof MethodDeclaration))
	    return false;
    label_1596:
	{
	    MethodDeclaration otherDeclaration = (MethodDeclaration) other;
	    if (otherDeclaration.isNull()
		|| !matchString(getName(), otherDeclaration.getName())
		|| !matchAnnotationsAndModifiers(otherDeclaration, match)
		|| !getParameters().matches(otherDeclaration.getParameters(),
					    match)
		|| !getBody().matches(otherDeclaration.getBody(), match))
		PUSH false;
	    else
		PUSH true;
	    break label_1596;
	}
	return POP;
    }
}
