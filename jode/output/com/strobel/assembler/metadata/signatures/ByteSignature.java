/* ByteSignature - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.signatures;

public final class ByteSignature implements BaseType
{
    private static final ByteSignature _singleton = new ByteSignature();
    
    private ByteSignature() {
	/* empty */
    }
    
    public static ByteSignature make() {
	return _singleton;
    }
    
    public void accept(TypeTreeVisitor v) {
	v.visitByteSignature(this);
    }
}
