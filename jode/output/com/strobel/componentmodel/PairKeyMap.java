/* PairKeyMap - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.componentmodel;
import com.strobel.core.VerifyArgument;

final class PairKeyMap implements FrugalKeyMap
{
    private final int _keyIndex1;
    private final int _keyIndex2;
    private final Object _value1;
    private final Object _value2;
    
    PairKeyMap(int keyIndex1, Object value1, int keyIndex2, Object value2) {
	_keyIndex1 = keyIndex1;
	_keyIndex2 = keyIndex2;
	_value1 = VerifyArgument.notNull(value1, "value1");
	_value2 = VerifyArgument.notNull(value2, "value2");
    }
    
    public final FrugalKeyMap plus(Key key, Object value) {
	VerifyArgument.notNull(key, "key");
	VerifyArgument.notNull(value, "value");
	int keyIndex = key.hashCode();
	if (keyIndex != _keyIndex1) {
	    if (keyIndex != _keyIndex2)
		return new ArrayKeyMap(new int[] { _keyIndex1, _keyIndex2,
						   keyIndex },
				       new Object[] { _value1, _value2,
						      value });
	    return new PairKeyMap(keyIndex, value, _keyIndex1, _value1);
	}
	return new PairKeyMap(keyIndex, value, _keyIndex2, _value2);
    }
    
    public final FrugalKeyMap minus(Key key) {
	VerifyArgument.notNull(key, "key");
	int keyIndex = key.hashCode();
	if (keyIndex != _keyIndex1) {
	    if (keyIndex != _keyIndex2)
		return this;
	    return new SingleKeyMap(_keyIndex1, _value1);
	}
	return new SingleKeyMap(_keyIndex2, _value2);
    }
    
    public final Object get(Key key) {
	VerifyArgument.notNull(key, "key");
	if (key.hashCode() != _keyIndex1) {
	    if (key.hashCode() != _keyIndex2)
		return null;
	    return _value2;
	}
	return _value1;
    }
    
    public final boolean isEmpty() {
	return false;
    }
}
