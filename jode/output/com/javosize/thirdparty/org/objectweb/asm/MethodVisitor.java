/* MethodVisitor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm;

public abstract class MethodVisitor
{
    protected final int api;
    protected MethodVisitor mv;
    
    public MethodVisitor(int i) {
	this(i, null);
    }
    
    public MethodVisitor(int i, MethodVisitor methodvisitor_0_) {
	if (i == 262144 || i == 327680) {
	    api = i;
	    mv = methodvisitor_0_;
	}
	throw new IllegalArgumentException();
    }
    
    public void visitParameter(String string, int i) {
	if (api >= 327680) {
	    if (mv != null)
		mv.visitParameter(string, i);
	} else
	    throw new RuntimeException();
	return;
    }
    
    public AnnotationVisitor visitAnnotationDefault() {
	if (mv == null)
	    return null;
	return mv.visitAnnotationDefault();
    }
    
    public AnnotationVisitor visitAnnotation(String string, boolean bool) {
	if (mv == null)
	    return null;
	return mv.visitAnnotation(string, bool);
    }
    
    public AnnotationVisitor visitTypeAnnotation(int i, TypePath typepath,
						 String string, boolean bool) {
	if (api >= 327680) {
	    if (mv == null)
		return null;
	    return mv.visitTypeAnnotation(i, typepath, string, bool);
	}
	throw new RuntimeException();
    }
    
    public AnnotationVisitor visitParameterAnnotation(int i, String string,
						      boolean bool) {
	if (mv == null)
	    return null;
	return mv.visitParameterAnnotation(i, string, bool);
    }
    
    public void visitAttribute(Attribute attribute) {
	if (mv != null)
	    mv.visitAttribute(attribute);
	return;
    }
    
    public void visitCode() {
	if (mv != null)
	    mv.visitCode();
	return;
    }
    
    public void visitFrame(int i, int i_1_, Object[] objects, int i_2_,
			   Object[] objects_3_) {
	if (mv != null)
	    mv.visitFrame(i, i_1_, objects, i_2_, objects_3_);
	return;
    }
    
    public void visitInsn(int i) {
	if (mv != null)
	    mv.visitInsn(i);
	return;
    }
    
    public void visitIntInsn(int i, int i_4_) {
	if (mv != null)
	    mv.visitIntInsn(i, i_4_);
	return;
    }
    
    public void visitVarInsn(int i, int i_5_) {
	if (mv != null)
	    mv.visitVarInsn(i, i_5_);
	return;
    }
    
    public void visitTypeInsn(int i, String string) {
	if (mv != null)
	    mv.visitTypeInsn(i, string);
	return;
    }
    
    public void visitFieldInsn(int i, String string, String string_6_,
			       String string_7_) {
	if (mv != null)
	    mv.visitFieldInsn(i, string, string_6_, string_7_);
	return;
    }
    
    /**
     * @deprecated
     */
    public void visitMethodInsn(int i, String string, String string_8_,
				String string_9_) {
    label_366:
	{
	    if (api < 327680) {
		if (mv != null)
		    mv.visitMethodInsn(i, string, string_8_, string_9_);
	    } else {
		if (i != 185)
		    PUSH false;
		else
		    PUSH true;
		break label_366;
	    }
	    return;
	}
	boolean bool = POP;
	visitMethodInsn(i, string, string_8_, string_9_, bool);
    }
    
    public void visitMethodInsn(int i, String string, String string_10_,
				String string_11_, boolean bool) {
	if (api >= 327680) {
	    if (mv != null)
		mv.visitMethodInsn(i, string, string_10_, string_11_, bool);
	} else {
	label_367:
	    {
		PUSH bool;
		if (i != 185)
		    PUSH false;
		else
		    PUSH true;
		break label_367;
	    }
	    if (POP == POP)
		visitMethodInsn(i, string, string_10_, string_11_);
	    else
		throw new IllegalArgumentException
			  ("INVOKESPECIAL/STATIC on interfaces require ASM 5");
	}
	return;
    }
    
    public transient void visitInvokeDynamicInsn(String string,
						 String string_12_,
						 Handle handle,
						 Object[] objects) {
	if (mv != null)
	    mv.visitInvokeDynamicInsn(string, string_12_, handle, objects);
	return;
    }
    
    public void visitJumpInsn(int i, Label label) {
	if (mv != null)
	    mv.visitJumpInsn(i, label);
	return;
    }
    
    public void visitLabel(Label label) {
	if (mv != null)
	    mv.visitLabel(label);
	return;
    }
    
    public void visitLdcInsn(Object object) {
	if (mv != null)
	    mv.visitLdcInsn(object);
	return;
    }
    
    public void visitIincInsn(int i, int i_13_) {
	if (mv != null)
	    mv.visitIincInsn(i, i_13_);
	return;
    }
    
    public transient void visitTableSwitchInsn(int i, int i_14_, Label label,
					       Label[] labels) {
	if (mv != null)
	    mv.visitTableSwitchInsn(i, i_14_, label, labels);
	return;
    }
    
    public void visitLookupSwitchInsn(Label label, int[] is, Label[] labels) {
	if (mv != null)
	    mv.visitLookupSwitchInsn(label, is, labels);
	return;
    }
    
    public void visitMultiANewArrayInsn(String string, int i) {
	if (mv != null)
	    mv.visitMultiANewArrayInsn(string, i);
	return;
    }
    
    public AnnotationVisitor visitInsnAnnotation(int i, TypePath typepath,
						 String string, boolean bool) {
	if (api >= 327680) {
	    if (mv == null)
		return null;
	    return mv.visitInsnAnnotation(i, typepath, string, bool);
	}
	throw new RuntimeException();
    }
    
    public void visitTryCatchBlock(Label label, Label label_15_,
				   Label label_16_, String string) {
	if (mv != null)
	    mv.visitTryCatchBlock(label, label_15_, label_16_, string);
	return;
    }
    
    public AnnotationVisitor visitTryCatchAnnotation(int i, TypePath typepath,
						     String string,
						     boolean bool) {
	if (api >= 327680) {
	    if (mv == null)
		return null;
	    return mv.visitTryCatchAnnotation(i, typepath, string, bool);
	}
	throw new RuntimeException();
    }
    
    public void visitLocalVariable(String string, String string_17_,
				   String string_18_, Label label,
				   Label label_19_, int i) {
	if (mv != null)
	    mv.visitLocalVariable(string, string_17_, string_18_, label,
				  label_19_, i);
	return;
    }
    
    public AnnotationVisitor visitLocalVariableAnnotation(int i,
							  TypePath typepath,
							  Label[] labels,
							  Label[] labels_20_,
							  int[] is,
							  String string,
							  boolean bool) {
	if (api >= 327680) {
	    if (mv == null)
		return null;
	    return mv.visitLocalVariableAnnotation(i, typepath, labels,
						   labels_20_, is, string,
						   bool);
	}
	throw new RuntimeException();
    }
    
    public void visitLineNumber(int i, Label label) {
	if (mv != null)
	    mv.visitLineNumber(i, label);
	return;
    }
    
    public void visitMaxs(int i, int i_21_) {
	if (mv != null)
	    mv.visitMaxs(i, i_21_);
	return;
    }
    
    public void visitEnd() {
	if (mv != null)
	    mv.visitEnd();
	return;
    }
}
