/* CharSignature - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.signatures;

public final class CharSignature implements BaseType
{
    private static final CharSignature _singleton = new CharSignature();
    
    private CharSignature() {
	/* empty */
    }
    
    public static CharSignature make() {
	return _singleton;
    }
    
    public void accept(TypeTreeVisitor v) {
	v.visitCharSignature(this);
    }
}
