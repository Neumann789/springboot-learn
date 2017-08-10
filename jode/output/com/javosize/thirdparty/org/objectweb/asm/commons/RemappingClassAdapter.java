/* RemappingClassAdapter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.ClassVisitor;
import com.javosize.thirdparty.org.objectweb.asm.FieldVisitor;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.TypePath;

public class RemappingClassAdapter extends ClassVisitor
{
    protected final Remapper remapper;
    protected String className;
    
    public RemappingClassAdapter(ClassVisitor classvisitor,
				 Remapper remapper) {
	this(327680, classvisitor, remapper);
    }
    
    protected RemappingClassAdapter(int i, ClassVisitor classvisitor,
				    Remapper remapper) {
	super(i, classvisitor);
	this.remapper = remapper;
    }
    
    public void visit(int i, int i_0_, String string, String string_1_,
		      String string_2_, String[] strings) {
	className = string;
	PUSH this;
	PUSH i;
	PUSH i_0_;
	PUSH remapper.mapType(string);
	PUSH remapper.mapSignature(string_1_, false);
    label_415:
	{
	    PUSH remapper.mapType(string_2_);
	    if (strings != null)
		PUSH remapper.mapTypes(strings);
	    else
		PUSH null;
	    break label_415;
	}
	((NON VIRTUAL ClassVisitor) POP).visit(POP, POP, POP, POP, POP, POP);
    }
    
    public AnnotationVisitor visitAnnotation(String string, boolean bool) {
    label_416:
	{
	    AnnotationVisitor annotationvisitor
		= super.visitAnnotation(remapper.mapDesc(string), bool);
	    if (annotationvisitor != null)
		PUSH createRemappingAnnotationAdapter(annotationvisitor);
	    else
		PUSH null;
	    break label_416;
	}
	return POP;
    }
    
    public AnnotationVisitor visitTypeAnnotation(int i, TypePath typepath,
						 String string, boolean bool) {
    label_417:
	{
	    AnnotationVisitor annotationvisitor
		= super.visitTypeAnnotation(i, typepath,
					    remapper.mapDesc(string), bool);
	    if (annotationvisitor != null)
		PUSH createRemappingAnnotationAdapter(annotationvisitor);
	    else
		PUSH null;
	    break label_417;
	}
	return POP;
    }
    
    public FieldVisitor visitField(int i, String string, String string_3_,
				   String string_4_, Object object) {
    label_418:
	{
	    FieldVisitor fieldvisitor
		= super.visitField(i,
				   remapper.mapFieldName(className, string,
							 string_3_),
				   remapper.mapDesc(string_3_),
				   remapper.mapSignature(string_4_, true),
				   remapper.mapValue(object));
	    if (fieldvisitor != null)
		PUSH createRemappingFieldAdapter(fieldvisitor);
	    else
		PUSH null;
	    break label_418;
	}
	return POP;
    }
    
    public MethodVisitor visitMethod(int i, String string, String string_5_,
				     String string_6_, String[] strings) {
	String string_7_ = remapper.mapMethodDesc(string_5_);
	PUSH this;
	PUSH i;
	PUSH remapper.mapMethodName(className, string, string_5_);
	PUSH string_7_;
    label_419:
	{
	    PUSH remapper.mapSignature(string_6_, false);
	    if (strings != null)
		PUSH remapper.mapTypes(strings);
	    else
		PUSH null;
	    break label_419;
	}
    label_420:
	{
	    MethodVisitor methodvisitor
		= ((NON VIRTUAL ClassVisitor) POP).visitMethod(POP, POP, POP,
							       POP, POP);
	    if (methodvisitor != null)
		PUSH createRemappingMethodAdapter(i, string_7_, methodvisitor);
	    else
		PUSH null;
	    break label_420;
	}
	return POP;
    }
    
    public void visitInnerClass(String string, String string_8_,
				String string_9_, int i) {
	PUSH this;
    label_421:
	{
	    PUSH remapper.mapType(string);
	    if (string_8_ != null)
		PUSH remapper.mapType(string_8_);
	    else
		PUSH null;
	    break label_421;
	}
	((NON VIRTUAL ClassVisitor) POP).visitInnerClass(POP, POP, string_9_,
							 i);
    }
    
    public void visitOuterClass(String string, String string_10_,
				String string_11_) {
	PUSH this;
    label_423:
	{
	label_422:
	    {
		PUSH remapper.mapType(string);
		if (string_10_ != null)
		    PUSH remapper.mapMethodName(string, string_10_,
						string_11_);
		else
		    PUSH null;
		break label_422;
	    }
	    if (string_11_ != null)
		PUSH remapper.mapMethodDesc(string_11_);
	    else
		PUSH null;
	    break label_423;
	}
	((NON VIRTUAL ClassVisitor) POP).visitOuterClass(POP, POP, POP);
    }
    
    protected FieldVisitor createRemappingFieldAdapter
	(FieldVisitor fieldvisitor) {
	return new RemappingFieldAdapter(fieldvisitor, remapper);
    }
    
    protected MethodVisitor createRemappingMethodAdapter
	(int i, String string, MethodVisitor methodvisitor) {
	return new RemappingMethodAdapter(i, string, methodvisitor, remapper);
    }
    
    protected AnnotationVisitor createRemappingAnnotationAdapter
	(AnnotationVisitor annotationvisitor) {
	return new RemappingAnnotationAdapter(annotationvisitor, remapper);
    }
}
