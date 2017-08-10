/* TraceFieldVisitor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.util;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.Attribute;
import com.javosize.thirdparty.org.objectweb.asm.FieldVisitor;
import com.javosize.thirdparty.org.objectweb.asm.TypePath;

public final class TraceFieldVisitor extends FieldVisitor
{
    public final Printer p;
    
    public TraceFieldVisitor(Printer printer) {
	this(null, printer);
    }
    
    public TraceFieldVisitor(FieldVisitor fieldvisitor, Printer printer) {
	super(327680, fieldvisitor);
	p = printer;
    }
    
    public AnnotationVisitor visitAnnotation(String string, boolean bool) {
	Printer printer;
    label_704:
	{
	    printer = p.visitFieldAnnotation(string, bool);
	    if (fv != null)
		PUSH fv.visitAnnotation(string, bool);
	    else
		PUSH null;
	    break label_704;
	}
	AnnotationVisitor annotationvisitor = POP;
	return new TraceAnnotationVisitor(annotationvisitor, printer);
    }
    
    public AnnotationVisitor visitTypeAnnotation(int i, TypePath typepath,
						 String string, boolean bool) {
	Printer printer;
    label_705:
	{
	    printer = p.visitFieldTypeAnnotation(i, typepath, string, bool);
	    if (fv != null)
		PUSH fv.visitTypeAnnotation(i, typepath, string, bool);
	    else
		PUSH null;
	    break label_705;
	}
	AnnotationVisitor annotationvisitor = POP;
	return new TraceAnnotationVisitor(annotationvisitor, printer);
    }
    
    public void visitAttribute(Attribute attribute) {
	p.visitFieldAttribute(attribute);
	super.visitAttribute(attribute);
    }
    
    public void visitEnd() {
	p.visitFieldEnd();
	super.visitEnd();
    }
}
