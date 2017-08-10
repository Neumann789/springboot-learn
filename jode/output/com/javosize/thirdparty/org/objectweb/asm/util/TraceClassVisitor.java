/* TraceClassVisitor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.util;
import java.io.PrintWriter;

import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.Attribute;
import com.javosize.thirdparty.org.objectweb.asm.ClassVisitor;
import com.javosize.thirdparty.org.objectweb.asm.FieldVisitor;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.TypePath;

public final class TraceClassVisitor extends ClassVisitor
{
    private final PrintWriter pw;
    public final Printer p;
    
    public TraceClassVisitor(PrintWriter printwriter) {
	this(null, printwriter);
    }
    
    public TraceClassVisitor(ClassVisitor classvisitor,
			     PrintWriter printwriter) {
	this(classvisitor, new Textifier(), printwriter);
    }
    
    public TraceClassVisitor(ClassVisitor classvisitor, Printer printer,
			     PrintWriter printwriter) {
	super(327680, classvisitor);
	pw = printwriter;
	p = printer;
    }
    
    public void visit(int i, int i_0_, String string, String string_1_,
		      String string_2_, String[] strings) {
	p.visit(i, i_0_, string, string_1_, string_2_, strings);
	super.visit(i, i_0_, string, string_1_, string_2_, strings);
    }
    
    public void visitSource(String string, String string_3_) {
	p.visitSource(string, string_3_);
	super.visitSource(string, string_3_);
    }
    
    public void visitOuterClass(String string, String string_4_,
				String string_5_) {
	p.visitOuterClass(string, string_4_, string_5_);
	super.visitOuterClass(string, string_4_, string_5_);
    }
    
    public AnnotationVisitor visitAnnotation(String string, boolean bool) {
	Printer printer;
    label_699:
	{
	    printer = p.visitClassAnnotation(string, bool);
	    if (cv != null)
		PUSH cv.visitAnnotation(string, bool);
	    else
		PUSH null;
	    break label_699;
	}
	AnnotationVisitor annotationvisitor = POP;
	return new TraceAnnotationVisitor(annotationvisitor, printer);
    }
    
    public AnnotationVisitor visitTypeAnnotation(int i, TypePath typepath,
						 String string, boolean bool) {
	Printer printer;
    label_700:
	{
	    printer = p.visitClassTypeAnnotation(i, typepath, string, bool);
	    if (cv != null)
		PUSH cv.visitTypeAnnotation(i, typepath, string, bool);
	    else
		PUSH null;
	    break label_700;
	}
	AnnotationVisitor annotationvisitor = POP;
	return new TraceAnnotationVisitor(annotationvisitor, printer);
    }
    
    public void visitAttribute(Attribute attribute) {
	p.visitClassAttribute(attribute);
	super.visitAttribute(attribute);
    }
    
    public void visitInnerClass(String string, String string_6_,
				String string_7_, int i) {
	p.visitInnerClass(string, string_6_, string_7_, i);
	super.visitInnerClass(string, string_6_, string_7_, i);
    }
    
    public FieldVisitor visitField(int i, String string, String string_8_,
				   String string_9_, Object object) {
	Printer printer;
    label_701:
	{
	    printer = p.visitField(i, string, string_8_, string_9_, object);
	    if (cv != null)
		PUSH cv.visitField(i, string, string_8_, string_9_, object);
	    else
		PUSH null;
	    break label_701;
	}
	FieldVisitor fieldvisitor = POP;
	return new TraceFieldVisitor(fieldvisitor, printer);
    }
    
    public MethodVisitor visitMethod(int i, String string, String string_10_,
				     String string_11_, String[] strings) {
	Printer printer;
    label_702:
	{
	    printer
		= p.visitMethod(i, string, string_10_, string_11_, strings);
	    if (cv != null)
		PUSH cv.visitMethod(i, string, string_10_, string_11_,
				    strings);
	    else
		PUSH null;
	    break label_702;
	}
	MethodVisitor methodvisitor = POP;
	return new TraceMethodVisitor(methodvisitor, printer);
    }
    
    public void visitEnd() {
    label_703:
	{
	    p.visitClassEnd();
	    if (pw != null) {
		p.print(pw);
		pw.flush();
	    }
	    break label_703;
	}
	super.visitEnd();
    }
}
