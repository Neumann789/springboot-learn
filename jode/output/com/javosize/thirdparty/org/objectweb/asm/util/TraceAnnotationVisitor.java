/* TraceAnnotationVisitor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.util;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;

public final class TraceAnnotationVisitor extends AnnotationVisitor
{
    private final Printer p;
    
    public TraceAnnotationVisitor(Printer printer) {
	this(null, printer);
    }
    
    public TraceAnnotationVisitor(AnnotationVisitor annotationvisitor,
				  Printer printer) {
	super(327680, annotationvisitor);
	p = printer;
    }
    
    public void visit(String string, Object object) {
	p.visit(string, object);
	super.visit(string, object);
    }
    
    public void visitEnum(String string, String string_0_, String string_1_) {
	p.visitEnum(string, string_0_, string_1_);
	super.visitEnum(string, string_0_, string_1_);
    }
    
    public AnnotationVisitor visitAnnotation(String string, String string_2_) {
	Printer printer;
    label_697:
	{
	    printer = p.visitAnnotation(string, string_2_);
	    if (av != null)
		PUSH av.visitAnnotation(string, string_2_);
	    else
		PUSH null;
	    break label_697;
	}
	AnnotationVisitor annotationvisitor = POP;
	return new TraceAnnotationVisitor(annotationvisitor, printer);
    }
    
    public AnnotationVisitor visitArray(String string) {
	Printer printer;
    label_698:
	{
	    printer = p.visitArray(string);
	    if (av != null)
		PUSH av.visitArray(string);
	    else
		PUSH null;
	    break label_698;
	}
	AnnotationVisitor annotationvisitor = POP;
	return new TraceAnnotationVisitor(annotationvisitor, printer);
    }
    
    public void visitEnd() {
	p.visitAnnotationEnd();
	super.visitEnd();
    }
}
