/* LineNumberTableEntry - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir.attributes;

public final class LineNumberTableEntry
{
    private final int _offset;
    private final int _lineNumber;
    
    public LineNumberTableEntry(int offset, int lineNumber) {
	_offset = offset;
	_lineNumber = lineNumber;
    }
    
    public int getOffset() {
	return _offset;
    }
    
    public int getLineNumber() {
	return _lineNumber;
    }
}
