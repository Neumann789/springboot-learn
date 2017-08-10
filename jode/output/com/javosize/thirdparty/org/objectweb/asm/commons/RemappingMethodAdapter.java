/* RemappingMethodAdapter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.Handle;
import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.TypePath;

public class RemappingMethodAdapter extends LocalVariablesSorter
{
    protected final Remapper remapper;
    
    public RemappingMethodAdapter(int i, String string,
				  MethodVisitor methodvisitor,
				  Remapper remapper) {
	this(327680, i, string, methodvisitor, remapper);
    }
    
    protected RemappingMethodAdapter(int i, int i_0_, String string,
				     MethodVisitor methodvisitor,
				     Remapper remapper) {
	super(i, i_0_, string, methodvisitor);
	this.remapper = remapper;
    }
    
    public AnnotationVisitor visitAnnotationDefault() {
    label_426:
	{
	    AnnotationVisitor annotationvisitor
		= super.visitAnnotationDefault();
	    if (annotationvisitor != null)
		PUSH new RemappingAnnotationAdapter(annotationvisitor,
						    remapper);
	    else
		PUSH annotationvisitor;
	    break label_426;
	}
	return POP;
    }
    
    public AnnotationVisitor visitAnnotation(String string, boolean bool) {
    label_427:
	{
	    AnnotationVisitor annotationvisitor
		= super.visitAnnotation(remapper.mapDesc(string), bool);
	    if (annotationvisitor != null)
		PUSH new RemappingAnnotationAdapter(annotationvisitor,
						    remapper);
	    else
		PUSH annotationvisitor;
	    break label_427;
	}
	return POP;
    }
    
    public AnnotationVisitor visitTypeAnnotation(int i, TypePath typepath,
						 String string, boolean bool) {
    label_428:
	{
	    AnnotationVisitor annotationvisitor
		= super.visitTypeAnnotation(i, typepath,
					    remapper.mapDesc(string), bool);
	    if (annotationvisitor != null)
		PUSH new RemappingAnnotationAdapter(annotationvisitor,
						    remapper);
	    else
		PUSH annotationvisitor;
	    break label_428;
	}
	return POP;
    }
    
    public AnnotationVisitor visitParameterAnnotation(int i, String string,
						      boolean bool) {
    label_429:
	{
	    AnnotationVisitor annotationvisitor
		= super.visitParameterAnnotation(i, remapper.mapDesc(string),
						 bool);
	    if (annotationvisitor != null)
		PUSH new RemappingAnnotationAdapter(annotationvisitor,
						    remapper);
	    else
		PUSH annotationvisitor;
	    break label_429;
	}
	return POP;
    }
    
    public void visitFrame(int i, int i_1_, Object[] objects, int i_2_,
			   Object[] objects_3_) {
	super.visitFrame(i, i_1_, remapEntries(i_1_, objects), i_2_,
			 remapEntries(i_2_, objects_3_));
    }
    
    private Object[] remapEntries(int i, Object[] objects) {
	int i_4_ = 0;
	for (;;) {
	    if (i_4_ >= i)
		return objects;
	    if (!(objects[i_4_] instanceof String))
		i_4_++;
	    Object[] objects_5_;
	label_431:
	    {
		objects_5_ = new Object[i];
		if (i_4_ > 0)
		    System.arraycopy(objects, 0, objects_5_, 0, i_4_);
		break label_431;
	    }
	    for (;;) {
		Object object = objects[i_4_];
		PUSH objects_5_;
	    label_430:
		{
		    PUSH i_4_++;
		    if (!(object instanceof String))
			PUSH object;
		    else
			PUSH remapper.mapType((String) object);
		    break label_430;
		}
		POP[POP] = POP;
		if (i_4_ >= i)
		    return objects_5_;
	    }
	}
    }
    
    public void visitFieldInsn(int i, String string, String string_6_,
			       String string_7_) {
	super.visitFieldInsn(i, remapper.mapType(string),
			     remapper.mapFieldName(string, string_6_,
						   string_7_),
			     remapper.mapDesc(string_7_));
    }
    
    /**
     * @deprecated
     */
    public void visitMethodInsn(int i, String string, String string_8_,
				String string_9_) {
    label_432:
	{
	    if (api < 327680) {
		PUSH this;
		PUSH i;
		PUSH string;
		PUSH string_8_;
		PUSH string_9_;
		if (i != 185)
		    PUSH false;
		else
		    PUSH true;
	    } else {
		super.visitMethodInsn(i, string, string_8_, string_9_);
		return;
	    }
	}
	((RemappingMethodAdapter) POP).doVisitMethodInsn(POP, POP, POP, POP,
							 POP);
	break label_432;
    }
    
    public void visitMethodInsn(int i, String string, String string_10_,
				String string_11_, boolean bool) {
	if (api >= 327680)
	    doVisitMethodInsn(i, string, string_10_, string_11_, bool);
	else
	    super.visitMethodInsn(i, string, string_10_, string_11_, bool);
	return;
    }
    
    private void doVisitMethodInsn(int i, String string, String string_12_,
				   String string_13_, boolean bool) {
	if (mv != null)
	    mv.visitMethodInsn(i, remapper.mapType(string),
			       remapper.mapMethodName(string, string_12_,
						      string_13_),
			       remapper.mapMethodDesc(string_13_), bool);
	return;
    }
    
    public transient void visitInvokeDynamicInsn(String string,
						 String string_14_,
						 Handle handle,
						 Object[] objects) {
	int i = 0;
	for (;;) {
	    if (i >= objects.length)
		super.visitInvokeDynamicInsn
		    (remapper.mapInvokeDynamicMethodName(string, string_14_),
		     remapper.mapMethodDesc(string_14_),
		     (Handle) remapper.mapValue(handle), objects);
	    objects[i] = remapper.mapValue(objects[i]);
	    i++;
	}
    }
    
    public void visitTypeInsn(int i, String string) {
	super.visitTypeInsn(i, remapper.mapType(string));
    }
    
    public void visitLdcInsn(Object object) {
	super.visitLdcInsn(remapper.mapValue(object));
    }
    
    public void visitMultiANewArrayInsn(String string, int i) {
	super.visitMultiANewArrayInsn(remapper.mapDesc(string), i);
    }
    
    public AnnotationVisitor visitInsnAnnotation(int i, TypePath typepath,
						 String string, boolean bool) {
    label_433:
	{
	    AnnotationVisitor annotationvisitor
		= super.visitInsnAnnotation(i, typepath,
					    remapper.mapDesc(string), bool);
	    if (annotationvisitor != null)
		PUSH new RemappingAnnotationAdapter(annotationvisitor,
						    remapper);
	    else
		PUSH annotationvisitor;
	    break label_433;
	}
	return POP;
    }
    
    public void visitTryCatchBlock(Label label, Label label_15_,
				   Label label_16_, String string) {
	PUSH this;
	PUSH label;
	PUSH label_15_;
    label_434:
	{
	    PUSH label_16_;
	    if (string != null)
		PUSH remapper.mapType(string);
	    else
		PUSH null;
	    break label_434;
	}
	((NON VIRTUAL LocalVariablesSorter) POP).visitTryCatchBlock(POP, POP,
								    POP, POP);
    }
    
    public AnnotationVisitor visitTryCatchAnnotation(int i, TypePath typepath,
						     String string,
						     boolean bool) {
    label_435:
	{
	    AnnotationVisitor annotationvisitor
		= super.visitTryCatchAnnotation(i, typepath,
						remapper.mapDesc(string),
						bool);
	    if (annotationvisitor != null)
		PUSH new RemappingAnnotationAdapter(annotationvisitor,
						    remapper);
	    else
		PUSH annotationvisitor;
	    break label_435;
	}
	return POP;
    }
    
    public void visitLocalVariable(String string, String string_17_,
				   String string_18_, Label label,
				   Label label_19_, int i) {
	super.visitLocalVariable(string, remapper.mapDesc(string_17_),
				 remapper.mapSignature(string_18_, true),
				 label, label_19_, i);
    }
    
    public AnnotationVisitor visitLocalVariableAnnotation(int i,
							  TypePath typepath,
							  Label[] labels,
							  Label[] labels_20_,
							  int[] is,
							  String string,
							  boolean bool) {
    label_436:
	{
	    AnnotationVisitor annotationvisitor
		= super.visitLocalVariableAnnotation(i, typepath, labels,
						     labels_20_, is,
						     remapper.mapDesc(string),
						     bool);
	    if (annotationvisitor != null)
		PUSH new RemappingAnnotationAdapter(annotationvisitor,
						    remapper);
	    else
		PUSH annotationvisitor;
	    break label_436;
	}
	return POP;
    }
}
