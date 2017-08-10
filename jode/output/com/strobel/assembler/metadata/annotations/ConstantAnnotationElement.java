/* ConstantAnnotationElement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.annotations;
import com.strobel.core.VerifyArgument;

public final class ConstantAnnotationElement extends AnnotationElement
{
    private final Object _constantValue;
    
    public ConstantAnnotationElement(Object constantValue) {
	super(AnnotationElementType.Constant);
	_constantValue
	    = VerifyArgument.notNull(constantValue, "constantValue");
    }
    
    public Object getConstantValue() {
	return _constantValue;
    }
}
