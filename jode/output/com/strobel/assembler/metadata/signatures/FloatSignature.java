/* FloatSignature - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.signatures;

public final class FloatSignature implements BaseType
{
    private static final FloatSignature _singleton = new FloatSignature();
    
    private FloatSignature() {
	/* empty */
    }
    
    public static FloatSignature make() {
	return _singleton;
    }
    
    public void accept(TypeTreeVisitor v) {
	v.visitFloatSignature(this);
    }
}
