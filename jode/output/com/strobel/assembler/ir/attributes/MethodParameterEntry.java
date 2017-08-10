/* MethodParameterEntry - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir.attributes;
import com.strobel.assembler.metadata.Flags;

public final class MethodParameterEntry
{
    private final String _name;
    private final int _flags;
    
    public MethodParameterEntry(String name, int flags) {
	_name = name;
	_flags = flags;
    }
    
    public String getName() {
	return _name;
    }
    
    public int getFlags() {
	return _flags;
    }
    
    public String toString() {
	return ("MethodParameterEntry{name='" + _name + "'" + ", flags="
		+ Flags.toString((long) _flags) + '}');
    }
}
