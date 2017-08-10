/* TraceMethodVisitor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.util;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.Attribute;
import com.javosize.thirdparty.org.objectweb.asm.Handle;
import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.TypePath;

public final class TraceMethodVisitor extends MethodVisitor
{
    public final Printer p;
    
    public TraceMethodVisitor(Printer printer) {
	this(null, printer);
    }
    
    public TraceMethodVisitor(MethodVisitor methodvisitor, Printer printer) {
	super(327680, methodvisitor);
	p = printer;
    }
    
    public void visitParameter(String string, int i) {
	p.visitParameter(string, i);
	super.visitParameter(string, i);
    }
    
    public AnnotationVisitor visitAnnotation(String string, boolean bool) {
	Printer printer;
    label_706:
	{
	    printer = p.visitMethodAnnotation(string, bool);
	    if (mv != null)
		PUSH mv.visitAnnotation(string, bool);
	    else
		PUSH null;
	    break label_706;
	}
	AnnotationVisitor annotationvisitor = POP;
	return new TraceAnnotationVisitor(annotationvisitor, printer);
    }
    
    public AnnotationVisitor visitTypeAnnotation(int i, TypePath typepath,
						 String string, boolean bool) {
	Printer printer;
    label_707:
	{
	    printer = p.visitMethodTypeAnnotation(i, typepath, string, bool);
	    if (mv != null)
		PUSH mv.visitTypeAnnotation(i, typepath, string, bool);
	    else
		PUSH null;
	    break label_707;
	}
	AnnotationVisitor annotationvisitor = POP;
	return new TraceAnnotationVisitor(annotationvisitor, printer);
    }
    
    public void visitAttribute(Attribute attribute) {
	p.visitMethodAttribute(attribute);
	super.visitAttribute(attribute);
    }
    
    public AnnotationVisitor visitAnnotationDefault() {
	Printer printer;
    label_708:
	{
	    printer = p.visitAnnotationDefault();
	    if (mv != null)
		PUSH mv.visitAnnotationDefault();
	    else
		PUSH null;
	    break label_708;
	}
	AnnotationVisitor annotationvisitor = POP;
	return new TraceAnnotationVisitor(annotationvisitor, printer);
    }
    
    public AnnotationVisitor visitParameterAnnotation(int i, String string,
						      boolean bool) {
	Printer printer;
    label_709:
	{
	    printer = p.visitParameterAnnotation(i, string, bool);
	    if (mv != null)
		PUSH mv.visitParameterAnnotation(i, string, bool);
	    else
		PUSH null;
	    break label_709;
	}
	AnnotationVisitor annotationvisitor = POP;
	return new TraceAnnotationVisitor(annotationvisitor, printer);
    }
    
    public void visitCode() {
	p.visitCode();
	super.visitCode();
    }
    
    public void visitFrame(int i, int i_0_, Object[] objects, int i_1_,
			   Object[] objects_2_) {
	p.visitFrame(i, i_0_, objects, i_1_, objects_2_);
	super.visitFrame(i, i_0_, objects, i_1_, objects_2_);
    }
    
    public void visitInsn(int i) {
	p.visitInsn(i);
	super.visitInsn(i);
    }
    
    public void visitIntInsn(int i, int i_3_) {
	p.visitIntInsn(i, i_3_);
	super.visitIntInsn(i, i_3_);
    }
    
    public void visitVarInsn(int i, int i_4_) {
	p.visitVarInsn(i, i_4_);
	super.visitVarInsn(i, i_4_);
    }
    
    public void visitTypeInsn(int i, String string) {
	p.visitTypeInsn(i, string);
	super.visitTypeInsn(i, string);
    }
    
    public void visitFieldInsn(int i, String string, String string_5_,
			       String string_6_) {
	p.visitFieldInsn(i, string, string_5_, string_6_);
	super.visitFieldInsn(i, string, string_5_, string_6_);
    }
    
    /**
     * @deprecated
     */
    public void visitMethodInsn(int i, String string, String string_7_,
				String string_8_) {
	if (api < 327680) {
	    p.visitMethodInsn(i, string, string_7_, string_8_);
	    if (mv != null)
		mv.visitMethodInsn(i, string, string_7_, string_8_);
	} else
	    super.visitMethodInsn(i, string, string_7_, string_8_);
	return;
    }
    
    public void visitMethodInsn(int i, String string, String string_9_,
				String string_10_, boolean bool) {
	if (api >= 327680) {
	    p.visitMethodInsn(i, string, string_9_, string_10_, bool);
	    if (mv != null)
		mv.visitMethodInsn(i, string, string_9_, string_10_, bool);
	} else
	    super.visitMethodInsn(i, string, string_9_, string_10_, bool);
	return;
    }
    
    public transient void visitInvokeDynamicInsn(String string,
						 String string_11_,
						 Handle handle,
						 Object[] objects) {
	p.visitInvokeDynamicInsn(string, string_11_, handle, objects);
	super.visitInvokeDynamicInsn(string, string_11_, handle, objects);
    }
    
    public void visitJumpInsn(int i, Label label) {
	p.visitJumpInsn(i, label);
	super.visitJumpInsn(i, label);
    }
    
    public void visitLabel(Label label) {
	p.visitLabel(label);
	super.visitLabel(label);
    }
    
    public void visitLdcInsn(Object object) {
	p.visitLdcInsn(object);
	super.visitLdcInsn(object);
    }
    
    public void visitIincInsn(int i, int i_12_) {
	p.visitIincInsn(i, i_12_);
	super.visitIincInsn(i, i_12_);
    }
    
    public transient void visitTableSwitchInsn(int i, int i_13_, Label label,
					       Label[] labels) {
	p.visitTableSwitchInsn(i, i_13_, label, labels);
	super.visitTableSwitchInsn(i, i_13_, label, labels);
    }
    
    public void visitLookupSwitchInsn(Label label, int[] is, Label[] labels) {
	p.visitLookupSwitchInsn(label, is, labels);
	super.visitLookupSwitchInsn(label, is, labels);
    }
    
    public void visitMultiANewArrayInsn(String string, int i) {
	p.visitMultiANewArrayInsn(string, i);
	super.visitMultiANewArrayInsn(string, i);
    }
    
    public AnnotationVisitor visitInsnAnnotation(int i, TypePath typepath,
						 String string, boolean bool) {
	Printer printer;
    label_710:
	{
	    printer = p.visitInsnAnnotation(i, typepath, string, bool);
	    if (mv != null)
		PUSH mv.visitInsnAnnotation(i, typepath, string, bool);
	    else
		PUSH null;
	    break label_710;
	}
	AnnotationVisitor annotationvisitor = POP;
	return new TraceAnnotationVisitor(annotationvisitor, printer);
    }
    
    public void visitTryCatchBlock(Label label, Label label_14_,
				   Label label_15_, String string) {
	p.visitTryCatchBlock(label, label_14_, label_15_, string);
	super.visitTryCatchBlock(label, label_14_, label_15_, string);
    }
    
    public AnnotationVisitor visitTryCatchAnnotation(int i, TypePath typepath,
						     String string,
						     boolean bool) {
	Printer printer;
    label_711:
	{
	    printer = p.visitTryCatchAnnotation(i, typepath, string, bool);
	    if (mv != null)
		PUSH mv.visitTryCatchAnnotation(i, typepath, string, bool);
	    else
		PUSH null;
	    break label_711;
	}
	AnnotationVisitor annotationvisitor = POP;
	return new TraceAnnotationVisitor(annotationvisitor, printer);
    }
    
    public void visitLocalVariable(String string, String string_16_,
				   String string_17_, Label label,
				   Label label_18_, int i) {
	p.visitLocalVariable(string, string_16_, string_17_, label, label_18_,
			     i);
	super.visitLocalVariable(string, string_16_, string_17_, label,
				 label_18_, i);
    }
    
    public AnnotationVisitor visitLocalVariableAnnotation(int i,
							  TypePath typepath,
							  Label[] labels,
							  Label[] labels_19_,
							  int[] is,
							  String string,
							  boolean bool) {
	Printer printer;
    label_712:
	{
	    printer
		= p.visitLocalVariableAnnotation(i, typepath, labels,
						 labels_19_, is, string, bool);
	    if (mv != null)
		PUSH mv.visitLocalVariableAnnotation(i, typepath, labels,
						     labels_19_, is, string,
						     bool);
	    else
		PUSH null;
	    break label_712;
	}
	AnnotationVisitor annotationvisitor = POP;
	return new TraceAnnotationVisitor(annotationvisitor, printer);
    }
    
    public void visitLineNumber(int i, Label label) {
	p.visitLineNumber(i, label);
	super.visitLineNumber(i, label);
    }
    
    public void visitMaxs(int i, int i_20_) {
	p.visitMaxs(i, i_20_);
	super.visitMaxs(i, i_20_);
    }
    
    public void visitEnd() {
	p.visitMethodEnd();
	super.visitEnd();
    }
}
