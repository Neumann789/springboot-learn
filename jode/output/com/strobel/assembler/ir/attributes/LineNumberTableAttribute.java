/* LineNumberTableAttribute - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir.attributes;
import java.util.List;

import com.strobel.core.ArrayUtilities;
import com.strobel.core.VerifyArgument;

public final class LineNumberTableAttribute extends SourceAttribute
{
    private final List _entries;
    private final int _maxOffset;
    
    public LineNumberTableAttribute(LineNumberTableEntry[] entries) {
	super("LineNumberTable",
	      2 + ((LineNumberTableEntry[])
		   VerifyArgument.notNull(entries, "entries")).length * 4);
	_entries
	    = ArrayUtilities.asUnmodifiableList((Object[]) entries.clone());
	int max = -2147483648;
	LineNumberTableEntry[] arr$ = entries;
	int len$ = arr$.length;
	int i$ = 0;
	for (;;) {
	    if (i$ >= len$)
		_maxOffset = max;
	    LineNumberTableEntry entry = arr$[i$];
	label_1171:
	    {
		int offset = entry.getOffset();
		if (offset > max)
		    max = offset;
		break label_1171;
	    }
	    i$++;
	}
    }
    
    public List getEntries() {
	return _entries;
    }
    
    public int getMaxOffset() {
	return _maxOffset;
    }
}
