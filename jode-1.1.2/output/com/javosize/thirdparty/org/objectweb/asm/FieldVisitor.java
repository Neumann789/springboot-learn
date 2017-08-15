/* FieldVisitor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm;

public abstract class FieldVisitor
{
    protected final int api;
    protected FieldVisitor fv;
    
    public FieldVisitor(int i) {
	this(i, null);
    }
    
    public FieldVisitor(int i, FieldVisitor fieldvisitor_0_) {
	if (i != 262144 && i != 327680)
	    throw new IllegalArgumentException();
	api = i;
	fv = fieldvisitor_0_;
    }
    
    public AnnotationVisitor visitAnnotation(String string, boolean bool) {
	if (fv != null)
	    return fv.visitAnnotation(string, bool);
	return null;
    }
    
    public AnnotationVisitor visitTypeAnnotation(int i, TypePath typepath,
						 String string, boolean bool) {
	if (api < 327680)
	    throw new RuntimeException();
	if (fv != null)
	    return fv.visitTypeAnnotation(i, typepath, string, bool);
	return null;
    }
    
    public void visitAttribute(Attribute attribute) {
	if (fv != null)
	    fv.visitAttribute(attribute);
    }
    
    public void visitEnd() {
	if (fv != null)
	    fv.visitEnd();
    }
}
