/* RemappingAnnotationAdapter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;

public class RemappingAnnotationAdapter extends AnnotationVisitor
{
    protected final Remapper remapper;
    
    public RemappingAnnotationAdapter(AnnotationVisitor annotationvisitor,
				      Remapper remapper) {
	this(327680, annotationvisitor, remapper);
    }
    
    protected RemappingAnnotationAdapter(int i,
					 AnnotationVisitor annotationvisitor,
					 Remapper remapper) {
	super(i, annotationvisitor);
	this.remapper = remapper;
    }
    
    public void visit(String string, Object object) {
	av.visit(string, remapper.mapValue(object));
    }
    
    public void visitEnum(String string, String string_0_, String string_1_) {
	av.visitEnum(string, remapper.mapDesc(string_0_), string_1_);
    }
    
    public AnnotationVisitor visitAnnotation(String string, String string_2_) {
    label_413:
	{
	    AnnotationVisitor annotationvisitor
		= av.visitAnnotation(string, remapper.mapDesc(string_2_));
	    if (annotationvisitor != null) {
		if (annotationvisitor != av)
		    PUSH new RemappingAnnotationAdapter(annotationvisitor,
							remapper);
		else
		    PUSH this;
	    } else
		PUSH null;
	    break label_413;
	}
	return POP;
    }
    
    public AnnotationVisitor visitArray(String string) {
    label_414:
	{
	    AnnotationVisitor annotationvisitor = av.visitArray(string);
	    if (annotationvisitor != null) {
		if (annotationvisitor != av)
		    PUSH new RemappingAnnotationAdapter(annotationvisitor,
							remapper);
		else
		    PUSH this;
	    } else
		PUSH null;
	    break label_414;
	}
	return POP;
    }
}
