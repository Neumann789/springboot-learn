/* TypeVariableSignature - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.signatures;

public final class TypeVariableSignature implements FieldTypeSignature
{
    private final String _name;
    
    private TypeVariableSignature(String name) {
	_name = name;
    }
    
    public static TypeVariableSignature make(String name) {
	return new TypeVariableSignature(name);
    }
    
    public String getName() {
	return _name;
    }
    
    public void accept(TypeTreeVisitor v) {
	v.visitTypeVariableSignature(this);
    }
    
    public String toString() {
	return "T" + _name + ";";
    }
}
