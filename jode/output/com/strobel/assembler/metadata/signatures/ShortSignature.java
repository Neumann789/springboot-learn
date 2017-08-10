/* ShortSignature - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.signatures;

public final class ShortSignature implements BaseType
{
    private static final ShortSignature _singleton = new ShortSignature();
    
    private ShortSignature() {
	/* empty */
    }
    
    public static ShortSignature make() {
	return _singleton;
    }
    
    public void accept(TypeTreeVisitor v) {
	v.visitShortSignature(this);
    }
}
