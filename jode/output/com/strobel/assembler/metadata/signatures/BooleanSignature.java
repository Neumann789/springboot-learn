/* BooleanSignature - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.signatures;

public final class BooleanSignature implements BaseType
{
    private static final BooleanSignature _singleton = new BooleanSignature();
    
    private BooleanSignature() {
	/* empty */
    }
    
    public static BooleanSignature make() {
	return _singleton;
    }
    
    public void accept(TypeTreeVisitor v) {
	v.visitBooleanSignature(this);
    }
}
