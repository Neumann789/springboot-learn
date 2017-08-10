/* RuntimeHelpers - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.compilerservices;
import java.lang.reflect.Field;

import com.strobel.core.VerifyArgument;
import com.strobel.util.ContractUtils;

import sun.misc.Unsafe;

public final class RuntimeHelpers
{
    private static Unsafe _unsafe;
    
    private RuntimeHelpers() {
	throw ContractUtils.unreachable();
    }
    
    public static void ensureClassInitialized(Class clazz) {
	getUnsafeInstance().ensureClassInitialized
	    ((Class) VerifyArgument.notNull(clazz, "clazz"));
    }
    
    private static Unsafe getUnsafeInstance() {
	if (_unsafe == null) {
	    try {
		_unsafe = Unsafe.getUnsafe();
	    } catch (Throwable PUSH) {
		Object object = POP;
	    }
	    try {
		Field instanceField
		    = Unsafe.class.getDeclaredField("theUnsafe");
		instanceField.setAccessible(true);
		_unsafe = (Unsafe) instanceField.get(Unsafe.class);
	    } catch (Throwable PUSH) {
		Throwable t = POP;
		throw new IllegalStateException
			  (String.format
			   ("Could not load an instance of the %s class.",
			    new Object[] { Unsafe.class.getName() }));
	    }
	    return _unsafe;
	}
	return _unsafe;
    }
}
