/* CompositeTypeLoader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import com.strobel.core.VerifyArgument;

public final class CompositeTypeLoader implements ITypeLoader
{
    private final ITypeLoader[] _typeLoaders;
    
    public transient CompositeTypeLoader(ITypeLoader[] typeLoaders) {
	_typeLoaders
	    = ((ITypeLoader[])
	       ((ITypeLoader[])
		VerifyArgument.noNullElementsAndNotEmpty(typeLoaders,
							 "typeLoaders"))
		   .clone());
    }
    
    public boolean tryLoadType(String internalName, Buffer buffer) {
	ITypeLoader[] arr$ = _typeLoaders;
	int len$ = arr$.length;
	int i$ = 0;
	for (;;) {
	    if (i$ >= len$)
		return false;
	    ITypeLoader typeLoader = arr$[i$];
	    if (!typeLoader.tryLoadType(internalName, buffer)) {
		buffer.reset();
		i$++;
	    }
	    return true;
	}
    }
}
