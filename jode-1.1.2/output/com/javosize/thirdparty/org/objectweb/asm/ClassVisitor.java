/* ClassVisitor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm;

public abstract class ClassVisitor
{
    protected final int api;
    protected ClassVisitor cv;
    
    public ClassVisitor(int i) {
	this(i, null);
    }
    
    public ClassVisitor(int i, ClassVisitor classvisitor_0_) {
	if (i != 262144 && i != 327680)
	    throw new IllegalArgumentException();
	api = i;
	cv = classvisitor_0_;
    }
    
    public void visit(int i, int i_1_, String string, String string_2_,
		      String string_3_, String[] strings) {
	if (cv != null)
	    cv.visit(i, i_1_, string, string_2_, string_3_, strings);
    }
    
    public void visitSource(String string, String string_4_) {
	if (cv != null)
	    cv.visitSource(string, string_4_);
    }
    
    public void visitOuterClass(String string, String string_5_,
				String string_6_) {
	if (cv != null)
	    cv.visitOuterClass(string, string_5_, string_6_);
    }
    
    public AnnotationVisitor visitAnnotation(String string, boolean bool) {
	if (cv != null)
	    return cv.visitAnnotation(string, bool);
	return null;
    }
    
    public AnnotationVisitor visitTypeAnnotation(int i, TypePath typepath,
						 String string, boolean bool) {
	if (api < 327680)
	    throw new RuntimeException();
	if (cv != null)
	    return cv.visitTypeAnnotation(i, typepath, string, bool);
	return null;
    }
    
    public void visitAttribute(Attribute attribute) {
	if (cv != null)
	    cv.visitAttribute(attribute);
    }
    
    public void visitInnerClass(String string, String string_7_,
				String string_8_, int i) {
	if (cv != null)
	    cv.visitInnerClass(string, string_7_, string_8_, i);
    }
    
    public FieldVisitor visitField(int i, String string, String string_9_,
				   String string_10_, Object object) {
	if (cv != null)
	    return cv.visitField(i, string, string_9_, string_10_, object);
	return null;
    }
    
    public MethodVisitor visitMethod(int i, String string, String string_11_,
				     String string_12_, String[] strings) {
	if (cv != null)
	    return cv.visitMethod(i, string, string_11_, string_12_, strings);
	return null;
    }
    
    public void visitEnd() {
	if (cv != null)
	    cv.visitEnd();
    }
}
