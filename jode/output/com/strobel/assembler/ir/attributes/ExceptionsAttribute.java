/* ExceptionsAttribute - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir.attributes;
import java.util.List;

import com.strobel.assembler.metadata.TypeReference;
import com.strobel.core.ArrayUtilities;
import com.strobel.core.VerifyArgument;

public final class ExceptionsAttribute extends SourceAttribute
{
    private final List _exceptionTypes;
    
    public transient ExceptionsAttribute(TypeReference[] exceptionTypes) {
	super("Exceptions",
	      2 * (1 + ((TypeReference[])
			(VerifyArgument.noNullElements
			 (exceptionTypes, "exceptionTypes"))).length));
	_exceptionTypes = ArrayUtilities.asUnmodifiableList(exceptionTypes);
    }
    
    public List getExceptionTypes() {
	return _exceptionTypes;
    }
}
