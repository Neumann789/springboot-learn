/* IntSignature - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.signatures;

public final class IntSignature implements BaseType
{
    private static final IntSignature _singleton = new IntSignature();
    
    private IntSignature() {
	/* empty */
    }
    
    public static IntSignature make() {
	return _singleton;
    }
    
    public void accept(TypeTreeVisitor v) {
	v.visitIntSignature(this);
    }
}
