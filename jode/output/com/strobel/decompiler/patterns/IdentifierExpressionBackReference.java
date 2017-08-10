/* IdentifierExpressionBackReference - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.patterns;
import com.strobel.core.CollectionUtilities;
import com.strobel.core.StringUtilities;
import com.strobel.core.VerifyArgument;
import com.strobel.decompiler.languages.java.ast.AstNode;
import com.strobel.decompiler.languages.java.ast.Identifier;
import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
import com.strobel.decompiler.languages.java.ast.Roles;

public final class IdentifierExpressionBackReference extends Pattern
{
    private final String _referencedGroupName;
    
    public IdentifierExpressionBackReference(String referencedGroupName) {
	_referencedGroupName
	    = (String) VerifyArgument.notNull(referencedGroupName,
					      "referencedGroupName");
    }
    
    public final String getReferencedGroupName() {
	return _referencedGroupName;
    }
    
    public final boolean matches(INode other, Match match) {
	if (!(other instanceof IdentifierExpression)
	    || CollectionUtilities
		   .any(((IdentifierExpression) other).getTypeArguments()))
	    return false;
    label_1859:
	{
	    INode referenced
		= (INode) CollectionUtilities
			      .lastOrDefault(match.get(_referencedGroupName));
	    if (!(referenced instanceof AstNode)
		|| !(StringUtilities.equals
		     (((IdentifierExpression) other).getIdentifier(),
		      ((Identifier)
		       ((AstNode) referenced).getChildByRole(Roles.IDENTIFIER))
			  .getName())))
		PUSH false;
	    else
		PUSH true;
	    break label_1859;
	}
	return POP;
    }
}
