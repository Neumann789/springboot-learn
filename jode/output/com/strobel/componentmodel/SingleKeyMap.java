/* SingleKeyMap - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.componentmodel;
import com.strobel.core.VerifyArgument;

final class SingleKeyMap implements FrugalKeyMap
{
    private final int _keyIndex;
    private final Object _value;
    
    SingleKeyMap(int keyIndex, Object value) {
	_keyIndex = keyIndex;
	_value = value;
    }
    
    public final FrugalKeyMap plus(Key key, Object value) {
	VerifyArgument.notNull(key, "key");
	VerifyArgument.notNull(value, "value");
	if (key.hashCode() != _keyIndex)
	    return new PairKeyMap(_keyIndex, _value, key.hashCode(), value);
	return new SingleKeyMap(key.hashCode(), value);
    }
    
    public final FrugalKeyMap minus(Key key) {
	VerifyArgument.notNull(key, "key");
	if (key.hashCode() != _keyIndex)
	    return this;
	return EMPTY;
    }
    
    public final Object get(Key key) {
	VerifyArgument.notNull(key, "key");
	if (key.hashCode() != _keyIndex)
	    return null;
	return _value;
    }
    
    public final boolean isEmpty() {
	return false;
    }
}
