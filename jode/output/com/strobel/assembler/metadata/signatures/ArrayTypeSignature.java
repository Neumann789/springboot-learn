/* ArrayTypeSignature - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.signatures;

public final class ArrayTypeSignature implements FieldTypeSignature
{
    private final TypeSignature _componentType;
    
    private ArrayTypeSignature(TypeSignature componentType) {
	_componentType = componentType;
    }
    
    public static ArrayTypeSignature make(TypeSignature ct) {
	return new ArrayTypeSignature(ct);
    }
    
    public TypeSignature getComponentType() {
	return _componentType;
    }
    
    public void accept(TypeTreeVisitor v) {
	v.visitArrayTypeSignature(this);
    }
}
