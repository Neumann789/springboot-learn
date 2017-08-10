/* ExceptionTableEntry - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir.attributes;
import com.strobel.assembler.metadata.TypeReference;

public final class ExceptionTableEntry
{
    private final int _startOffset;
    private final int _endOffset;
    private final int _handlerOffset;
    private final TypeReference _catchType;
    
    public ExceptionTableEntry(int startOffset, int endOffset,
			       int handlerOffset, TypeReference catchType) {
	_startOffset = startOffset;
	_endOffset = endOffset;
	_handlerOffset = handlerOffset;
	_catchType = catchType;
    }
    
    public int getStartOffset() {
	return _startOffset;
    }
    
    public int getEndOffset() {
	return _endOffset;
    }
    
    public int getHandlerOffset() {
	return _handlerOffset;
    }
    
    public TypeReference getCatchType() {
	return _catchType;
    }
    
    public String toString() {
	return ("Handler{From=" + _startOffset + ", To=" + _endOffset
		+ ", Target=" + _handlerOffset + ", Type=" + _catchType + '}');
    }
}
