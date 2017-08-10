/* TypeReferenceDescriptorComparisonNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.patterns;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.core.StringUtilities;
import com.strobel.core.VerifyArgument;
import com.strobel.decompiler.languages.java.ast.Keys;
import com.strobel.decompiler.languages.java.ast.TypeReferenceExpression;

public final class TypeReferenceDescriptorComparisonNode extends Pattern
{
    private final String _descriptor;
    
    public TypeReferenceDescriptorComparisonNode(String descriptor) {
	_descriptor
	    = (String) VerifyArgument.notNull(descriptor, "descriptor");
    }
    
    public boolean matches(INode other, Match match) {
	if (!(other instanceof TypeReferenceExpression))
	    return false;
	TypeReferenceExpression typeReferenceExpression
	    = (TypeReferenceExpression) other;
    label_1863:
	{
	    TypeReference typeReference
		= (TypeReference) typeReferenceExpression.getType()
				      .getUserData(Keys.TYPE_REFERENCE);
	    if (typeReference == null
		|| !StringUtilities.equals(_descriptor,
					   typeReference.getInternalName()))
		PUSH false;
	    else
		PUSH true;
	    break label_1863;
	}
	return POP;
    }
}
