/* ConstantValueAttribute - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir.attributes;

public final class ConstantValueAttribute extends SourceAttribute
{
    private final Object _value;
    
    public ConstantValueAttribute(Object value) {
	super("ConstantValue", 2);
	_value = value;
    }
    
    public Object getValue() {
	return _value;
    }
}
