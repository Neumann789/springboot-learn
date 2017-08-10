/* EnumValueDeclaration - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.languages.EntityType;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class EnumValueDeclaration extends EntityDeclaration
{
    public final AstNodeCollection getArguments() {
	return getChildrenByRole(Roles.ARGUMENT);
    }
    
    public EntityType getEntityType() {
	return EntityType.ENUM_VALUE;
    }
    
    public final JavaTokenNode getLeftBraceToken() {
	return (JavaTokenNode) getChildByRole(Roles.LEFT_BRACE);
    }
    
    public final AstNodeCollection getMembers() {
	return getChildrenByRole(Roles.TYPE_MEMBER);
    }
    
    public final JavaTokenNode getRightBraceToken() {
	return (JavaTokenNode) getChildByRole(Roles.RIGHT_BRACE);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitEnumValueDeclaration(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof FieldDeclaration))
	    return false;
    label_1604:
	{
	    FieldDeclaration otherDeclaration = (FieldDeclaration) other;
	    if (otherDeclaration.isNull()
		|| !matchString(getName(), otherDeclaration.getName())
		|| !matchAnnotationsAndModifiers(otherDeclaration, match)
		|| !getReturnType().matches(otherDeclaration.getReturnType(),
					    match)
		|| !getMembers().matches(getMembers(), match))
		PUSH false;
	    else
		PUSH true;
	    break label_1604;
	}
	return POP;
    }
}
