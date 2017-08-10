/* BlobAttribute - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir.attributes;
import com.strobel.core.VerifyArgument;

public final class BlobAttribute extends SourceAttribute
{
    private final int _dataOffset;
    private final byte[] _data;
    
    public BlobAttribute(String name, byte[] data) {
	this(name, data, -1);
    }
    
    public BlobAttribute(String name, byte[] data, int dataOffset) {
	super(name, data.length);
	_dataOffset = dataOffset;
	_data = (byte[]) VerifyArgument.notNull(data, "data");
    }
    
    public int getDataOffset() {
	return _dataOffset;
    }
    
    public byte[] getData() {
	return _data;
    }
}
