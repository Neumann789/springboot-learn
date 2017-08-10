/* AnonymousObjectCreationExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import com.strobel.decompiler.patterns.INode;
import com.strobel.decompiler.patterns.Match;

public class AnonymousObjectCreationExpression extends ObjectCreationExpression
{
    public AnonymousObjectCreationExpression(int offset,
					     TypeDeclaration typeDeclaration,
					     AstType type) {
	super(offset, type);
	setTypeDeclaration(typeDeclaration);
    }
    
    public transient AnonymousObjectCreationExpression
	(int offset, TypeDeclaration typeDeclaration, AstType type,
	 Expression[] arguments) {
	super(offset, type, arguments);
	setTypeDeclaration(typeDeclaration);
    }
    
    public AnonymousObjectCreationExpression(int offset,
					     TypeDeclaration typeDeclaration,
					     AstType type,
					     Iterable arguments) {
	super(offset, type, arguments);
	setTypeDeclaration(typeDeclaration);
    }
    
    public final TypeDeclaration getTypeDeclaration() {
	return (TypeDeclaration) getChildByRole(Roles.LOCAL_TYPE_DECLARATION);
    }
    
    public final void setTypeDeclaration(TypeDeclaration value) {
	setChildByRole(Roles.LOCAL_TYPE_DECLARATION, value);
    }
    
    public Object acceptVisitor(IAstVisitor visitor, Object data) {
	return visitor.visitAnonymousObjectCreationExpression(this, data);
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof AnonymousObjectCreationExpression))
	    return false;
    label_1584:
	{
	    AnonymousObjectCreationExpression otherExpression
		= (AnonymousObjectCreationExpression) other;
	    if (!super.matches(other, match)
		|| !getTypeDeclaration()
			.matches(otherExpression.getTypeDeclaration(), match))
		PUSH false;
	    else
		PUSH true;
	    break label_1584;
	}
	return POP;
    }
}
