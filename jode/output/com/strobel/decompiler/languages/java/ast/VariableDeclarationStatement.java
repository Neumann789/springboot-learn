/* VariableDeclarationStatement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import java.util.List;

import javax.lang.model.element.Modifier;

import com.strobel.core.Predicate;
import com.strobel.core.StringUtilities;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;
import com.strobel.decompiler.patterns.Role;

public class VariableDeclarationStatement extends Statement
{
    public static final Role MODIFIER_ROLE = EntityDeclaration.MODIFIER_ROLE;
    private boolean _anyModifiers;
    
    public VariableDeclarationStatement() {
	super(-34);
    }
    
    public VariableDeclarationStatement(AstType type, String name,
					int offset) {
	this(type, name, offset, null);
    }
    
    public VariableDeclarationStatement(AstType type, String name,
					Expression initializer) {
	this(type, name, -34, initializer);
    }
    
    public VariableDeclarationStatement(AstType type, String name, int offset,
					Expression initializer) {
    label_1683:
	{
	    PUSH this;
	    if (initializer != null)
		PUSH initializer.getOffset();
	    else
		PUSH offset;
	    break label_1683;
	}
	((UNCONSTRUCTED)POP).Statement(POP);
	setType(type);
	getVariables().add(new VariableInitializer(name, initializer));
    }
    
    public final boolean isAnyModifiers() {
	return _anyModifiers;
    }
    
    public final void setAnyModifiers(boolean value) {
	verifyNotFrozen();
	_anyModifiers = value;
    }
    
    public final List getModifiers() {
	return EntityDeclaration.getModifiers(this);
    }
    
    public final void addModifier(Modifier modifier) {
	EntityDeclaration.addModifier(this, modifier);
    }
    
    public final void removeModifier(Modifier modifier) {
	EntityDeclaration.removeModifier(this, modifier);
    }
    
    public final void setModifiers(List modifiers) {
	EntityDeclaration.setModifiers(this, modifiers);
    }
    
    public final AstType getType() {
	return (AstType) getChildByRole(Roles.TYPE);
    }
    
    public final void setType(AstType value) {
	setChildByRole(Roles.TYPE, value);
    }
    
    public final JavaTokenNode getSemicolonToken() {
	return (JavaTokenNode) getChildByRole(Roles.SEMICOLON);
    }
    
    public final AstNodeCollection getVariables() {
	return getChildrenByRole(Roles.VARIABLE);
    }
    
    public final VariableInitializer getVariable(final String name) {
	return (VariableInitializer) getVariables()
					 .firstOrNullObject(new Predicate() {
	    {
		super();
	    }
	    
	    public boolean test(VariableInitializer variable) {
		return StringUtilities.equals(variable.getName(), name);
	    }
	});
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitVariableDeclaration(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof VariableDeclarationStatement))
	    return false;
    label_1684:
	{
	    VariableDeclarationStatement otherDeclaration
		= (VariableDeclarationStatement) other;
	    if (other.isNull()
		|| !getType().matches(otherDeclaration.getType(), match)
		|| (!isAnyModifiers() && !otherDeclaration.isAnyModifiers()
		    && !(getChildrenByRole(MODIFIER_ROLE).matches
			 (otherDeclaration.getChildrenByRole(MODIFIER_ROLE),
			  match)))
		|| !getVariables().matches(otherDeclaration.getVariables(),
					   match))
		PUSH false;
	    else
		PUSH true;
	    break label_1684;
	}
	return POP;
    }
}
