/* VoidSignature - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.signatures;

public final class VoidSignature implements BaseType
{
    private static final VoidSignature _singleton = new VoidSignature();
    
    private VoidSignature() {
	/* empty */
    }
    
    public static VoidSignature make() {
	return _singleton;
    }
    
    public void accept(TypeTreeVisitor v) {
	v.visitVoidSignature(this);
    }
}
