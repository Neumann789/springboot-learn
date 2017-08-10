/* LongSignature - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.signatures;

public final class LongSignature implements BaseType
{
    private static final LongSignature _singleton = new LongSignature();
    
    private LongSignature() {
	/* empty */
    }
    
    public static LongSignature make() {
	return _singleton;
    }
    
    public void accept(TypeTreeVisitor v) {
	v.visitLongSignature(this);
    }
}
