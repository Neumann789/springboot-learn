/* AnnotationElement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.annotations;

public abstract class AnnotationElement
{
    private final AnnotationElementType _elementType;
    
    protected AnnotationElement(AnnotationElementType elementType) {
	_elementType = elementType;
    }
    
    public AnnotationElementType getElementType() {
	return _elementType;
    }
}
