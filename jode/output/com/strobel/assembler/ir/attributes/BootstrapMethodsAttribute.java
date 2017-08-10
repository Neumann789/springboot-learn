/* BootstrapMethodsAttribute - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir.attributes;
import java.util.Collections;
import java.util.List;

import com.strobel.core.ArrayUtilities;
import com.strobel.core.VerifyArgument;

public final class BootstrapMethodsAttribute extends SourceAttribute
{
    private final List _bootstrapMethods;
    
    public BootstrapMethodsAttribute(List bootstrapMethods) {
	this((BootstrapMethodsTableEntry[])
	     (((List)
	       VerifyArgument.notNull(bootstrapMethods, "bootstrapMethods"))
		  .toArray
	      (new BootstrapMethodsTableEntry[bootstrapMethods.size()])));
    }
    
    public transient BootstrapMethodsAttribute
	(BootstrapMethodsTableEntry[] bootstrapMethods) {
	super("BootstrapMethods", computeSize(bootstrapMethods));
    label_1169:
	{
	    PUSH this;
	    if (!ArrayUtilities.isNullOrEmpty(bootstrapMethods))
		PUSH ArrayUtilities.asUnmodifiableList(bootstrapMethods);
	    else
		PUSH Collections.emptyList();
	    break label_1169;
	}
	((BootstrapMethodsAttribute) POP)._bootstrapMethods = POP;
    }
    
    public final List getBootstrapMethods() {
	return _bootstrapMethods;
    }
    
    private static int computeSize
	(BootstrapMethodsTableEntry[] bootstrapMethods) {
	int size = 2;
	if (bootstrapMethods != null) {
	    BootstrapMethodsTableEntry[] arr$ = bootstrapMethods;
	    int len$ = arr$.length;
	    int i$ = 0;
	    for (;;) {
		if (i$ >= len$)
		    return size;
		BootstrapMethodsTableEntry bootstrapMethod = arr$[i$];
		size += 2 + 2 * bootstrapMethod.getArguments().size();
		i$++;
	    }
	}
	return size;
    }
}
