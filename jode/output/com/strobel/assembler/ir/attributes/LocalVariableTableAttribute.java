/* LocalVariableTableAttribute - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir.attributes;
import java.util.List;

import com.strobel.core.ArrayUtilities;

public final class LocalVariableTableAttribute extends SourceAttribute
{
    private final List _entries;
    
    public LocalVariableTableAttribute(String name,
				       LocalVariableTableEntry[] entries) {
	super(name, 2 + entries.length * 10);
	_entries
	    = ArrayUtilities.asUnmodifiableList((Object[]) entries.clone());
    }
    
    public List getEntries() {
	return _entries;
    }
}
