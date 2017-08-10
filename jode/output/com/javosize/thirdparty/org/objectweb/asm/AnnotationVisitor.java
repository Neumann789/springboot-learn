/* AnnotationVisitor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm;

public abstract class AnnotationVisitor
{
    protected final int api;
    protected AnnotationVisitor av;
    
    public AnnotationVisitor(int i) {
	this(i, null);
    }
    
    public AnnotationVisitor(int i, AnnotationVisitor annotationvisitor_0_) {
	if (i == 262144 || i == 327680) {
	    api = i;
	    av = annotationvisitor_0_;
	}
	throw new IllegalArgumentException();
    }
    
    public void visit(String string, Object object) {
	if (av != null)
	    av.visit(string, object);
	return;
    }
    
    public void visitEnum(String string, String string_1_, String string_2_) {
	if (av != null)
	    av.visitEnum(string, string_1_, string_2_);
	return;
    }
    
    public AnnotationVisitor visitAnnotation(String string, String string_3_) {
	if (av == null)
	    return null;
	return av.visitAnnotation(string, string_3_);
    }
    
    public AnnotationVisitor visitArray(String string) {
	if (av == null)
	    return null;
	return av.visitArray(string);
    }
    
    public void visitEnd() {
	if (av != null)
	    av.visitEnd();
	return;
    }
}
