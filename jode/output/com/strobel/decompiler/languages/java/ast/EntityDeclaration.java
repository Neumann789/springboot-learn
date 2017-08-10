/* EntityDeclaration - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.lang.model.element.Modifier;

import com.strobel.core.VerifyArgument;
import com.strobel.decompiler.languages.EntityType;
import com.strobel.decompiler.languages.TextLocation;
import com.strobel.decompiler.patterns.Match;
import com.strobel.decompiler.patterns.Role;

public abstract class EntityDeclaration extends AstNode
{
    public static final Role ANNOTATION_ROLE = Roles.ANNOTATION;
    public static final Role UNATTACHED_ANNOTATION_ROLE
	= new Role("UnattachedAnnotation", Annotation.class);
    public static final Role MODIFIER_ROLE
	= new Role("Modifier", JavaModifierToken.class);
    public static final Role PRIVATE_IMPLEMENTATION_TYPE_ROLE
	= new Role("PrivateImplementationType", AstType.class, AstType.NULL);
    private boolean _anyModifiers;
    
    public final boolean isAnyModifiers() {
	return _anyModifiers;
    }
    
    public final void setAnyModifiers(boolean value) {
	verifyNotFrozen();
	_anyModifiers = value;
    }
    
    public NodeType getNodeType() {
	return NodeType.MEMBER;
    }
    
    public abstract EntityType getEntityType();
    
    public final AstNodeCollection getAnnotations() {
	return getChildrenByRole(ANNOTATION_ROLE);
    }
    
    public final boolean hasModifier(Modifier modifier) {
	Iterator i$ = getModifiers().iterator();
	for (;;) {
	    if (!i$.hasNext())
		return false;
	    JavaModifierToken modifierToken = (JavaModifierToken) i$.next();
	    IF (modifierToken.getModifier() != modifier)
		/* empty */
	    return true;
	}
    }
    
    public final AstNodeCollection getModifiers() {
	return getChildrenByRole(MODIFIER_ROLE);
    }
    
    public final String getName() {
	return ((Identifier) getChildByRole(Roles.IDENTIFIER)).getName();
    }
    
    public final void setName(String value) {
	setChildByRole(Roles.IDENTIFIER, Identifier.create(value));
    }
    
    public final Identifier getNameToken() {
	return (Identifier) getChildByRole(Roles.IDENTIFIER);
    }
    
    public final void setNameToken(Identifier value) {
	setChildByRole(Roles.IDENTIFIER, value);
    }
    
    public final AstType getReturnType() {
	return (AstType) getChildByRole(Roles.TYPE);
    }
    
    public final void setReturnType(AstType type) {
	setChildByRole(Roles.TYPE, type);
    }
    
    public EntityDeclaration clone() {
	EntityDeclaration copy = (EntityDeclaration) super.clone();
	copy._anyModifiers = _anyModifiers;
	return copy;
    }
    
    protected final boolean matchAnnotationsAndModifiers
	(EntityDeclaration other, Match match) {
    label_1601:
	{
	    VerifyArgument.notNull(other, "other");
	    if (other == null || other.isNull()
		|| (!isAnyModifiers()
		    && !getModifiers().matches(other.getModifiers(), match))
		|| !getAnnotations().matches(other.getAnnotations(), match))
		PUSH false;
	    else
		PUSH true;
	    break label_1601;
	}
	return POP;
    }
    
    static List getModifiers(AstNode node) {
	List modifiers = null;
	Iterator i$ = node.getChildrenByRole(MODIFIER_ROLE).iterator();
    label_1603:
	{
	    for (;;) {
		if (!i$.hasNext()) {
		    if (modifiers == null)
			PUSH Collections.emptyList();
		    else
			PUSH Collections.unmodifiableList(modifiers);
		    break label_1603;
		}
		JavaModifierToken modifierToken;
	    label_1602:
		{
		    modifierToken = (JavaModifierToken) i$.next();
		    if (modifiers == null)
			modifiers = new ArrayList();
		    break label_1602;
		}
		modifiers.add(modifierToken.getModifier());
	    }
	}
	return POP;
	break label_1603;
    }
    
    static void setModifiers(AstNode node, Collection modifiers) {
	AstNodeCollection modifierTokens
	    = node.getChildrenByRole(MODIFIER_ROLE);
	modifierTokens.clear();
	Iterator i$ = modifiers.iterator();
	for (;;) {
	    IF (!i$.hasNext())
		/* empty */
	    Modifier modifier = (Modifier) i$.next();
	    modifierTokens.add(new JavaModifierToken(TextLocation.EMPTY,
						     modifier));
	}
    }
    
    static void addModifier(AstNode node, Modifier modifier) {
	List modifiers = getModifiers(node);
	if (!modifiers.contains(modifier))
	    node.addChild(new JavaModifierToken(TextLocation.EMPTY, modifier),
			  MODIFIER_ROLE);
	return;
    }
    
    static boolean removeModifier(AstNode node, Modifier modifier) {
	AstNodeCollection modifierTokens
	    = node.getChildrenByRole(MODIFIER_ROLE);
	Iterator i$ = modifierTokens.iterator();
	for (;;) {
	    if (!i$.hasNext())
		return false;
	    JavaModifierToken modifierToken = (JavaModifierToken) i$.next();
	    IF (modifierToken.getModifier() != modifier)
		/* empty */
	    modifierToken.remove();
	    return true;
	}
    }
}
