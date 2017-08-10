/* AnnotationAnnotationElement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.annotations;
import com.strobel.core.VerifyArgument;

public final class AnnotationAnnotationElement extends AnnotationElement
{
    private final CustomAnnotation _annotation;
    
    public AnnotationAnnotationElement(CustomAnnotation annotation) {
	super(AnnotationElementType.Annotation);
	_annotation = (CustomAnnotation) VerifyArgument.notNull(annotation,
								"annotation");
    }
    
    public CustomAnnotation getAnnotation() {
	return _annotation;
    }
}
