/* ErrorOperand - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir;

public final class ErrorOperand
{
    private final String _message;
    
    public ErrorOperand(String message) {
	_message = message;
    }
    
    public String toString() {
	if (_message == null)
	    return "!!! BAD OPERAND !!!";
	return _message;
    }
}
