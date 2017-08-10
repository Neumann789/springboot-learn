/* InnerClassesAttribute - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir.attributes;
import java.util.List;

import com.strobel.core.VerifyArgument;

public final class InnerClassesAttribute extends SourceAttribute
{
    private final List _entries;
    
    public InnerClassesAttribute(int length, List entries) {
	super("InnerClasses", length);
	_entries = (List) VerifyArgument.notNull(entries, "entries");
    }
    
    public List getEntries() {
	return _entries;
    }
}
