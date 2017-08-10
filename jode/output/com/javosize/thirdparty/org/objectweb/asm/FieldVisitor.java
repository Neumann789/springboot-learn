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
	if (i == 262144 || i == 327680) {
	    api = i;
	    fv = fieldvisitor_0_;
	}
	throw new IllegalArgumentException();
    }
    
    public AnnotationVisitor visitAnnotation(String string, boolean bool) {
	if (fv == null)
	    return null;
	return fv.visitAnnotation(string, bool);
    }
    
    public AnnotationVisitor visitTypeAnnotation(int i, TypePath typepath,
						 String string, boolean bool) {
	if (api >= 327680) {
	    if (fv == null)
		return null;
	    return fv.visitTypeAnnotation(i, typepath, string, bool);
	}
	throw new RuntimeException();
    }
    
    public void visitAttribute(Attribute attribute) {
	if (fv != null)
	    fv.visitAttribute(attribute);
	return;
    }
    
    public void visitEnd() {
	if (fv != null)
	    fv.visitEnd();
	return;
    }
}
