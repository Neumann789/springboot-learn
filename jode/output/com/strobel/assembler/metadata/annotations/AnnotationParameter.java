/* AnnotationParameter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.annotations;
import com.strobel.core.VerifyArgument;

public final class AnnotationParameter
{
    private final AnnotationElement _value;
    private final String _member;
    
    public AnnotationParameter(String member, AnnotationElement value) {
	_member = (String) VerifyArgument.notNull(member, "member");
	_value = (AnnotationElement) VerifyArgument.notNull(value, "value");
    }
    
    public final String getMember() {
	return _member;
    }
    
    public final AnnotationElement getValue() {
	return _value;
    }
}
