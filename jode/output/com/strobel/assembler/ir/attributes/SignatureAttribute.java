/* SignatureAttribute - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir.attributes;

public final class SignatureAttribute extends SourceAttribute
{
    private final String _signature;
    
    public SignatureAttribute(String signature) {
	super("Signature", 4);
	_signature = signature;
    }
    
    public String getSignature() {
	return _signature;
    }
}
