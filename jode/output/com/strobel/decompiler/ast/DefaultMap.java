/* DefaultMap - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.ast;
import java.util.IdentityHashMap;

import com.strobel.core.VerifyArgument;
import com.strobel.functions.Supplier;

public final class DefaultMap extends IdentityHashMap
{
    private final Supplier _defaultValueFactory;
    
    public DefaultMap(Supplier defaultValueFactory) {
	_defaultValueFactory
	    = (Supplier) VerifyArgument.notNull(defaultValueFactory,
						"defaultValueFactory");
    }
    
    public Object get(Object key) {
	Object value;
    label_1498:
	{
	    value = super.get(key);
	    if (value == null)
		put(key, value = _defaultValueFactory.get());
	    break label_1498;
	}
	return value;
    }
}
