/* LocalVariableTableEntry - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir.attributes;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.core.VerifyArgument;

public final class LocalVariableTableEntry
{
    private final int _index;
    private final String _name;
    private final TypeReference _type;
    private final int _scopeOffset;
    private final int _scopeLength;
    
    public LocalVariableTableEntry(int index, String name, TypeReference type,
				   int scopeOffset, int scopeLength) {
	_index = VerifyArgument.isNonNegative(index, "index");
	_name = (String) VerifyArgument.notNull(name, "name");
	_type = (TypeReference) VerifyArgument.notNull(type, "type");
	_scopeOffset
	    = VerifyArgument.isNonNegative(scopeOffset, "scopeOffset");
	_scopeLength
	    = VerifyArgument.isNonNegative(scopeLength, "scopeLength");
    }
    
    public int getIndex() {
	return _index;
    }
    
    public String getName() {
	return _name;
    }
    
    public TypeReference getType() {
	return _type;
    }
    
    public int getScopeOffset() {
	return _scopeOffset;
    }
    
    public int getScopeLength() {
	return _scopeLength;
    }
    
    public String toString() {
	return ("LocalVariableTableEntry{Index=" + _index + ", Name='" + _name
		+ '\'' + ", Type=" + _type + ", ScopeOffset=" + _scopeOffset
		+ ", ScopeLength=" + _scopeLength + '}');
    }
}
