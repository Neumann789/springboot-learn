/* ParameterAnnotationsAttribute - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.ir.attributes;
import com.strobel.assembler.metadata.annotations.CustomAnnotation;

public final class ParameterAnnotationsAttribute extends SourceAttribute
{
    private final CustomAnnotation[][] _annotations;
    
    public ParameterAnnotationsAttribute(String name, int length,
					 CustomAnnotation[][] annotations) {
	super(name, length);
	_annotations = annotations;
    }
    
    public CustomAnnotation[][] getAnnotations() {
	return _annotations;
    }
}
