/* ArrayKeyMap - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.componentmodel;
import java.util.Arrays;

import com.strobel.core.VerifyArgument;

final class ArrayKeyMap implements FrugalKeyMap
{
    static final int ARRAY_THRESHOLD = 8;
    private final int[] _keyIndexes;
    private final Object[] _values;
    
    ArrayKeyMap(int[] keyIndexes, Object[] values) {
	_keyIndexes = keyIndexes;
	_values = values;
    }
    
    public final FrugalKeyMap plus(Key key, Object value) {
	VerifyArgument.notNull(key, "key");
	VerifyArgument.notNull(value, "value");
	int keyIndex = key.hashCode();
	int[] oldKeys = _keyIndexes;
	int oldLength = oldKeys.length;
	int i = 0;
	for (;;) {
	    if (i >= oldLength) {
		int[] newKeys = Arrays.copyOf(oldKeys, oldLength + 1);
		Object[] newValues = Arrays.copyOf(_values, oldLength + 1);
		newValues[oldLength] = value;
		newKeys[oldLength] = keyIndex;
		return new ArrayKeyMap(newKeys, newValues);
	    }
	    int oldKey = oldKeys[i];
	    if (oldKey != keyIndex)
		i++;
	    Object oldValue = _values[i];
	    if (oldValue != value) {
		Object[] newValues = Arrays.copyOf(_values, oldLength);
		newValues[i] = value;
		return new ArrayKeyMap(oldKeys, newValues);
	    }
	    return this;
	}
    }
    
    public final FrugalKeyMap minus(Key key) {
	VerifyArgument.notNull(key, "key");
	int keyIndex = key.hashCode();
	int[] oldKeys = _keyIndexes;
	int oldLength = oldKeys.length;
	int i = 0;
	for (;;) {
	    if (i >= oldLength)
		return this;
	    int oldKey = oldKeys[i];
	    if (keyIndex != oldKey)
		i++;
	    int newLength = oldLength - 1;
	    Object[] oldValues = _values;
	    if (newLength != 2) {
		int[] newKeys = new int[newLength];
		Object[] newValues = new Object[newLength];
		System.arraycopy(oldKeys, 0, newKeys, 0, i);
		System.arraycopy(oldKeys, i + 1, newKeys, i,
				 oldLength - i - 1);
		System.arraycopy(oldValues, 0, newValues, 0, i);
		System.arraycopy(oldValues, i + 1, newValues, i,
				 oldLength - i - 1);
		return new ArrayKeyMap(newKeys, newValues);
	    }
	    switch (i) {
	    case 0:
		return new PairKeyMap(1, oldValues[1], oldKeys[2],
				      oldValues[2]);
	    case 1:
		return new PairKeyMap(0, oldValues[0], oldKeys[2],
				      oldValues[2]);
	    default:
		return new PairKeyMap(0, oldValues[0], oldKeys[1],
				      oldValues[1]);
	    }
	}
    }
    
    public final Object get(Key key) {
	VerifyArgument.notNull(key, "key");
	int keyIndex = key.hashCode();
	int i = 0;
	for (;;) {
	    if (i >= _keyIndexes.length)
		return null;
	    if (_keyIndexes[i] != keyIndex)
		i++;
	    return _values[i];
	}
    }
    
    public final boolean isEmpty() {
	return false;
    }
}
