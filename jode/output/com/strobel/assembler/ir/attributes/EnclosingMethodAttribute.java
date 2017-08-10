/* EnclosingMethodAttribute - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir.attributes;
import com.strobel.assembler.metadata.MethodReference;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.core.VerifyArgument;

public final class EnclosingMethodAttribute extends SourceAttribute
{
    private final TypeReference _enclosingType;
    private final MethodReference _enclosingMethod;
    
    public EnclosingMethodAttribute(TypeReference enclosingType,
				    MethodReference enclosingMethod) {
	super("EnclosingMethod", 4);
	_enclosingType
	    = (TypeReference) VerifyArgument.notNull(enclosingType,
						     "enclosingType");
	_enclosingMethod = enclosingMethod;
    }
    
    public TypeReference getEnclosingType() {
	return _enclosingType;
    }
    
    public MethodReference getEnclosingMethod() {
	return _enclosingMethod;
    }
}
