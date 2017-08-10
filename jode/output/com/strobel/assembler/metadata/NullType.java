/* NullType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;

final class NullType extends TypeDefinition
{
    static final NullType INSTANCE = new NullType();
    
    private NullType() {
	setName("__Null");
    }
    
    public String getSimpleName() {
	return "__Null";
    }
    
    public String getFullName() {
	return getSimpleName();
    }
    
    public String getInternalName() {
	return getSimpleName();
    }
    
    public final Object accept(TypeMetadataVisitor visitor, Object parameter) {
	return visitor.visitNullType(this, parameter);
    }
}
