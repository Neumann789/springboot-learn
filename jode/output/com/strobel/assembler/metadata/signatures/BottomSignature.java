/* BottomSignature - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.signatures;

public final class BottomSignature implements FieldTypeSignature
{
    private static final BottomSignature _singleton = new BottomSignature();
    
    private BottomSignature() {
	/* empty */
    }
    
    public static BottomSignature make() {
	return _singleton;
    }
    
    public void accept(TypeTreeVisitor v) {
	v.visitBottomSignature(this);
    }
}
