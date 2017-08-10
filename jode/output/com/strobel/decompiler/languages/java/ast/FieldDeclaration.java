/* FieldDeclaration - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.languages.EntityType;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class FieldDeclaration extends EntityDeclaration
{
    public final AstNodeCollection getVariables() {
	return getChildrenByRole(Roles.VARIABLE);
    }
    
    public EntityType getEntityType() {
	return EntityType.FIELD;
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitFieldDeclaration(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof FieldDeclaration))
	    return false;
    label_1605:
	{
	    FieldDeclaration otherDeclaration = (FieldDeclaration) other;
	    if (otherDeclaration.isNull()
		|| !matchString(getName(), otherDeclaration.getName())
		|| !matchAnnotationsAndModifiers(otherDeclaration, match)
		|| !getReturnType().matches(otherDeclaration.getReturnType(),
					    match))
		PUSH false;
	    else
		PUSH true;
	    break label_1605;
	}
	return POP;
    }
}
