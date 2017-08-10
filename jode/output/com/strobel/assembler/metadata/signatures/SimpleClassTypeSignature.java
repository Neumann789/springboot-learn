/* SimpleClassTypeSignature - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.signatures;

public final class SimpleClassTypeSignature implements FieldTypeSignature
{
    private final boolean _dollar;
    private final String _name;
    private final TypeArgument[] _typeArguments;
    
    private SimpleClassTypeSignature(String n, boolean dollar,
				     TypeArgument[] tas) {
	_name = n;
	_dollar = dollar;
	_typeArguments = tas;
    }
    
    public static SimpleClassTypeSignature make(String n, boolean dollar,
						TypeArgument[] tas) {
	return new SimpleClassTypeSignature(n, dollar, tas);
    }
    
    public boolean useDollar() {
	return _dollar;
    }
    
    public String getName() {
	return _name;
    }
    
    public TypeArgument[] getTypeArguments() {
	return _typeArguments;
    }
    
    public void accept(TypeTreeVisitor v) {
	v.visitSimpleClassTypeSignature(this);
    }
}
