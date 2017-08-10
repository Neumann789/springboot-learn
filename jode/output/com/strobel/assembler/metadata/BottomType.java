/* BottomType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;

final class BottomType extends TypeDefinition
{
    static final BottomType INSTANCE = new BottomType();
    
    private BottomType() {
	setName("__Bottom");
    }
    
    public String getSimpleName() {
	return "__Bottom";
    }
    
    public String getFullName() {
	return getSimpleName();
    }
    
    public String getInternalName() {
	return getSimpleName();
    }
    
    public final Object accept(TypeMetadataVisitor visitor, Object parameter) {
	return visitor.visitBottomType(this, parameter);
    }
}
