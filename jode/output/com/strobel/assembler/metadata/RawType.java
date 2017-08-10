/* RawType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import com.strobel.core.VerifyArgument;

public final class RawType extends TypeReference
{
    private final TypeReference _genericTypeDefinition;
    
    public RawType(TypeReference genericTypeDefinition) {
	_genericTypeDefinition
	    = (TypeReference) VerifyArgument.notNull(genericTypeDefinition,
						     "genericTypeDefinition");
    }
    
    public String getFullName() {
	return _genericTypeDefinition.getFullName();
    }
    
    public String getInternalName() {
	return _genericTypeDefinition.getInternalName();
    }
    
    public TypeReference getDeclaringType() {
	return _genericTypeDefinition.getDeclaringType();
    }
    
    public String getSimpleName() {
	return _genericTypeDefinition.getSimpleName();
    }
    
    public String getPackageName() {
	return _genericTypeDefinition.getPackageName();
    }
    
    public String getName() {
	return _genericTypeDefinition.getName();
    }
    
    public TypeReference getUnderlyingType() {
	return _genericTypeDefinition;
    }
    
    public final Object accept(TypeMetadataVisitor visitor, Object parameter) {
	return visitor.visitRawType(this, parameter);
    }
    
    public TypeDefinition resolve() {
	return getUnderlyingType().resolve();
    }
}
