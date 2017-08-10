/* ArrayAnnotationElement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.annotations;
import com.strobel.core.VerifyArgument;

public final class ArrayAnnotationElement extends AnnotationElement
{
    private final AnnotationElement[] _elements;
    
    public ArrayAnnotationElement(AnnotationElement[] elements) {
	super(AnnotationElementType.Array);
	_elements = (AnnotationElement[]) VerifyArgument.notNull(elements,
								 "elements");
    }
    
    public AnnotationElement[] getElements() {
	return _elements;
    }
}
