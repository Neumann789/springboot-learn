/* BootstrapMethodsTableEntry - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir.attributes;
import java.util.Collections;
import java.util.List;

import com.strobel.assembler.metadata.MethodReference;
import com.strobel.core.ArrayUtilities;
import com.strobel.core.VerifyArgument;

public final class BootstrapMethodsTableEntry
{
    private final MethodReference _method;
    private final List _arguments;
    
    public BootstrapMethodsTableEntry(MethodReference method, List arguments) {
	this(method, ((List) VerifyArgument.notNull(arguments, "arguments"))
			 .toArray());
    }
    
    public transient BootstrapMethodsTableEntry(MethodReference method,
						Object[] arguments) {
	_method = (MethodReference) VerifyArgument.notNull(method, "method");
    label_1170:
	{
	    PUSH this;
	    if (!ArrayUtilities.isNullOrEmpty(arguments))
		PUSH ArrayUtilities.asUnmodifiableList(arguments);
	    else
		PUSH Collections.emptyList();
	    break label_1170;
	}
	((BootstrapMethodsTableEntry) POP)._arguments = POP;
    }
    
    public final List getArguments() {
	return _arguments;
    }
    
    public final MethodReference getMethod() {
	return _method;
    }
}
