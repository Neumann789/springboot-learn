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
	if (i != 262144 && i != 327680)
	    throw new IllegalArgumentException();
	api = i;
	av = annotationvisitor_0_;
    }
    
    public void visit(String string, Object object) {
	if (av != null)
	    av.visit(string, object);
    }
    
    public void visitEnum(String string, String string_1_, String string_2_) {
	if (av != null)
	    av.visitEnum(string, string_1_, string_2_);
    }
    
    public AnnotationVisitor visitAnnotation(String string, String string_3_) {
	if (av != null)
	    return av.visitAnnotation(string, string_3_);
	return null;
    }
    
    public AnnotationVisitor visitArray(String string) {
	if (av != null)
	    return av.visitArray(string);
	return null;
    }
    
    public void visitEnd() {
	if (av != null)
	    av.visitEnd();
    }
}
