/* ClassAnnotationElement - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.annotations;
import com.strobel.assembler.metadata.TypeReference;

public final class ClassAnnotationElement extends AnnotationElement
{
    private final TypeReference _classType;
    
    public ClassAnnotationElement(TypeReference classType) {
	super(AnnotationElementType.Class);
	_classType = classType;
    }
    
    public TypeReference getClassType() {
	return _classType;
    }
}
