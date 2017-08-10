/* EmptyKeyMap - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.componentmodel;
import com.strobel.core.VerifyArgument;

final class EmptyKeyMap implements FrugalKeyMap
{
    public FrugalKeyMap plus(Key key, Object value) {
	VerifyArgument.notNull(key, "key");
	VerifyArgument.notNull(value, "value");
	return new SingleKeyMap(key.hashCode(), value);
    }
    
    public final FrugalKeyMap minus(Key key) {
	VerifyArgument.notNull(key, "key");
	return this;
    }
    
    public final Object get(Key key) {
	return null;
    }
    
    public final boolean isEmpty() {
	return true;
    }
}
