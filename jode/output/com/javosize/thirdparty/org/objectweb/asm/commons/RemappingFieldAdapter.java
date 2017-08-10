/* RemappingFieldAdapter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.FieldVisitor;
import com.javosize.thirdparty.org.objectweb.asm.TypePath;

public class RemappingFieldAdapter extends FieldVisitor
{
    private final Remapper remapper;
    
    public RemappingFieldAdapter(FieldVisitor fieldvisitor,
				 Remapper remapper) {
	this(327680, fieldvisitor, remapper);
    }
    
    protected RemappingFieldAdapter(int i, FieldVisitor fieldvisitor,
				    Remapper remapper) {
	super(i, fieldvisitor);
	this.remapper = remapper;
    }
    
    public AnnotationVisitor visitAnnotation(String string, boolean bool) {
    label_424:
	{
	    AnnotationVisitor annotationvisitor
		= fv.visitAnnotation(remapper.mapDesc(string), bool);
	    if (annotationvisitor != null)
		PUSH new RemappingAnnotationAdapter(annotationvisitor,
						    remapper);
	    else
		PUSH null;
	    break label_424;
	}
	return POP;
    }
    
    public AnnotationVisitor visitTypeAnnotation(int i, TypePath typepath,
						 String string, boolean bool) {
    label_425:
	{
	    AnnotationVisitor annotationvisitor
		= super.visitTypeAnnotation(i, typepath,
					    remapper.mapDesc(string), bool);
	    if (annotationvisitor != null)
		PUSH new RemappingAnnotationAdapter(annotationvisitor,
						    remapper);
	    else
		PUSH null;
	    break label_425;
	}
	return POP;
    }
}
