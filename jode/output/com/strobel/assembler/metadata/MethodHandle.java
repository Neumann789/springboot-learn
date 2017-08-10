/* MethodHandle - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import com.strobel.core.VerifyArgument;

public final class MethodHandle
{
    private final MethodReference _method;
    private final MethodHandleType _handleType;
    
    public MethodHandle(MethodReference method, MethodHandleType handleType) {
	_method = (MethodReference) VerifyArgument.notNull(method, "method");
	_handleType = (MethodHandleType) VerifyArgument.notNull(handleType,
								"handleType");
    }
    
    public final MethodHandleType getHandleType() {
	return _handleType;
    }
    
    public final MethodReference getMethod() {
	return _method;
    }
    
    public final String toString() {
	return (_handleType + " " + _method.getFullName() + ":"
		+ _method.getSignature());
    }
}
