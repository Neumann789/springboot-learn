/* NoRetryMetadataSystem - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler;
import java.util.HashSet;
import java.util.Set;

import com.strobel.assembler.metadata.ITypeLoader;
import com.strobel.assembler.metadata.MetadataSystem;
import com.strobel.assembler.metadata.TypeDefinition;

final class NoRetryMetadataSystem extends MetadataSystem
{
    private final Set _failedTypes;
    
    NoRetryMetadataSystem() {
	_failedTypes = new HashSet();
    }
    
    NoRetryMetadataSystem(String classPath) {
	super(classPath);
	_failedTypes = new HashSet();
    }
    
    NoRetryMetadataSystem(ITypeLoader typeLoader) {
	super(typeLoader);
	_failedTypes = new HashSet();
    }
    
    protected TypeDefinition resolveType(String descriptor,
					 boolean mightBePrimitive) {
	TypeDefinition result;
    label_1483:
	{
	    if (!_failedTypes.contains(descriptor)) {
		result = super.resolveType(descriptor, mightBePrimitive);
		if (result == null)
		    _failedTypes.add(descriptor);
	    } else
		return null;
	}
	return result;
	break label_1483;
    }
}
