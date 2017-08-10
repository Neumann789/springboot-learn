/* MethodParametersAttribute - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir.attributes;
import java.util.List;

import com.strobel.core.VerifyArgument;

public final class MethodParametersAttribute extends SourceAttribute
{
    private final List _entries;
    
    public MethodParametersAttribute(List entries) {
	super("MethodParameters", 1 + entries.size() * 4);
	_entries = (List) VerifyArgument.notNull(entries, "entries");
    }
    
    public List getEntries() {
	return _entries;
    }
}
